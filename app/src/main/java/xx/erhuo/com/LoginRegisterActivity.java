package xx.erhuo.com;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.LogUtils;
import com.erhuo.utils.MyApplication;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.exception.BmobException;
import xx.erhuo.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginRegisterActivity extends Activity {
	public static final int
			LOGIN_SUCCESS = 1,LOGIN_FAIL = 0,CONNECTED_SUCCESS = 3,CONNECTED_FAIL = 4,
			TRANS_SUCCESS = 5,TRANS_FAIL = 6;
	
	private CircleImageView civ_headLogin;
	private EditText et_name;
	private String sNum;
	private EditText et_password;
	private Button b_login;
	private Button b_changeRegister;
	public static final String ANSWER_STATUS = "status";
	
	private SharedPreferences sp;
	private static final String urlPath = "http://39.105.0.212/android/user_login";

	private Handler handler;

	private User user;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_register);
		user = MyApplication.getUser();
		init();
		
	}
	@SuppressLint("HandlerLeak")
	private void init(){
		 civ_headLogin =  findViewById(R.id.civ_headLogin);
		 et_name =  findViewById(R.id.et_Num);
		 et_password =  findViewById(R.id.et_Password);
		 b_login =  findViewById(R.id.b_Login);
		b_changeRegister =  findViewById(R.id.b_changeRegister);
		
		sp = getSharedPreferences("login",MODE_PRIVATE);
		String name = sp.getString("name", "");
		String password = sp.getString("password", "");
		
		if(name !=null){
			et_name.setText(name);
			if(password != null){
				et_password.setText(password);
			}
		}
		
		b_changeRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent= new Intent();
				intent.setAction("android.intent.action.VIEW");    
			    intent.setData(Uri.parse("http://39.105.0.212/shop/user_inRegist.action"));
			    startActivity(intent);
			}
		});

		//已经使用 WeakReference 在 Handler 与 Activity 之间搭建一座桥梁。若一个对象仅被 WeakReference 引用，则不能幸免于 GC
		handler = new  Handler(){
			//自定义0时失败,1时成功
			public void handleMessage(android.os.Message msg){
				super.handleMessage(msg);
				switch (msg.what){
					case CONNECTED_FAIL:
						Toast.makeText(LoginRegisterActivity.this,"连接失败" + (String)msg.obj , Toast.LENGTH_LONG).show();
						break;
					case CONNECTED_SUCCESS:
						int status = (int)msg.obj;
						if(status == TRANS_SUCCESS){
							//登录成功，记录登录状态
							Toast.makeText(LoginRegisterActivity.this,"登陆成功" , Toast.LENGTH_SHORT).show();
							initBomb();
							Intent data=new Intent();
							data.putExtra(ANSWER_STATUS, LOGIN_SUCCESS);
							LoginRegisterActivity.this.setResult(RESULT_OK,data);
							LoginRegisterActivity.this.finish();
						}else if(status == TRANS_FAIL){
							Toast.makeText(LoginRegisterActivity.this,"登陆成功，部分数据解读失败" , Toast.LENGTH_SHORT).show();
							initBomb();
							Intent data=new Intent();
							data.putExtra(ANSWER_STATUS, LOGIN_SUCCESS);
							LoginRegisterActivity.this.setResult(RESULT_OK,data);
							LoginRegisterActivity.this.finish();
						}else{
							Toast.makeText(LoginRegisterActivity.this,"用户名或密码错误" , Toast.LENGTH_SHORT).show();
							}
						break;
					}
			}};
		b_login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				login();
			}
		});
		
	}

	/**
	 * SharedPreferences写,非常适合保存零散的数据，如用户名密码+get方式向服务器提交数据
	 * @param sNum
	 * @param sPassword
	 */
	public void writeSp(String sNum,String sPassword){
		SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
		//拿到sp的编辑器
		Editor ed = sp.edit();
		ed.putString("name", sNum);
		ed.putString("password", sPassword);
		ed.commit();

	}

	/**
	 * 登录，使用okHttp
	 */
	private void login(){
		sNum = et_name.getText().toString();
		String sPassword = et_password.getText().toString();
		writeSp(sNum,sPassword);

		//String url
		HttpUtils.sendOkHttpRequest(urlPath,new String[]{"username","password"},new String[]{sNum,sPassword},new Callback(){

			@Override
			public void onFailure(Call call, IOException e) {
				android.os.Message m = handler.obtainMessage();
				m.what = LOGIN_FAIL;
				m.obj = e.toString();
				handler.sendMessage(m);
				LogUtils.e("login2--onFailure", "连接失败"+e.toString());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String responseData = response.body().string();
				LogUtils.e("onResponse ","responseData==" + responseData);
				android.os.Message m = handler.obtainMessage();
				m.what = CONNECTED_SUCCESS;
                JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(responseData);
					int status = jsonObject.getInt("status");

					if(status == LOGIN_SUCCESS){
						int transStatus = setUserFromJsonContent(jsonObject);
						m.obj = transStatus;

					}
					handler.sendMessage(m);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeMessages(CONNECTED_FAIL,CONNECTED_SUCCESS);
	}

	private void toastShort(String str){
		Toast.makeText(LoginRegisterActivity.this,str,Toast.LENGTH_SHORT).show();
	}

	private int setUserFromJsonContent(JSONObject jsonContent){
		//{"status":"1","msg":"登录成功","nicheng":"yadid",
		// "realname":"杨纪昆","sex":"male","qq":"2065994853","email":
		// "2065994853@qq.com","address":"郑州大学新校区柳园",
		// "uid":"72","sellnum":"45","buynum":"33",
		// {"image":"http://39.105.0.212/shop/products/bbe9af811f7f3c2f94817e7eba86bf66.jpg",
		// "guanzhu":"23","telephone":"18860351251","birthday":"1995-11-27",
		// "background":"http://39.105.0.212/shop/products/background.jpg"}

		user.setSpStatus(true);
		try {
			user.setSpBackgroundUrl(jsonContent.getString("background"));
			user.setSpBirthday(jsonContent.getString("birthday"));
			user.setSpHeadUrl(jsonContent.getString("image"));

			user.setSpPhone(jsonContent.getString("telephone"));

			user.setSpAddress(jsonContent.getString("address"));

			user.setSpEmail(jsonContent.getString("email"));
			user.setSpName(jsonContent.getString("nicheng"));
			user.setSpQqNum(jsonContent.getString("qq"));
			user.setSpRealName(jsonContent.getString("realname"));
			user.setSpSex(jsonContent.getString("sex").equals("male"));
			user.setSpUserId(jsonContent.getString("uid"));
			user.setSpBuyNum(jsonContent.getInt("buynum"));
			user.setSpSellNum(jsonContent.getInt("sellnum"));
			user.setSpConcernNum(jsonContent.getInt("guanzhu"));
			return TRANS_SUCCESS;
		} catch (JSONException e) {
			e.printStackTrace();
			return TRANS_FAIL;
		}

	}
	/**
	 * 初始化即时通讯服务器
	 */
	private void initBomb() {
		String id = null;
		if( user.isLogin()){
			id = user.getUserId();
		}
		if(id == null){
			id = "-1";
		}
		BmobIM.connect(id, new ConnectListener() {
			@Override
			public void done(String uid, BmobException e) {
				if (e == null) {
					LogUtils.e("initBomb", "connect success");
					toast("connect success");

				} else {
					LogUtils.e("initBomb", e.getErrorCode() + "/" + e.getMessage());
				}
			}
		});
	}
	private void toast(String str){
		Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
	}
}
