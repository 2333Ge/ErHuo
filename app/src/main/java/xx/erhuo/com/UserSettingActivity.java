package xx.erhuo.com;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.LogUtils;
import com.erhuo.utils.MyApplication;
import com.erhuo.utils.NotLeakHandler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xx.erhuo.bean.Constant;
import xx.erhuo.bean.User;
import xx.erhuo.bmob.MessageDBUtils;
import xx.erhuo.com.adapter.UserSettingAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class UserSettingActivity extends AppCompatActivity {

    //!序列化，传入一个user进来，初始化各个状态或者直接actionStart,此处进行序列化;
    private Toolbar t_title;
    private RecyclerView rv_settings;
    private CollapsingToolbarLayout ctl_coordinatorLayout;
    private TextView tv_buyNum,tv_sellNum,tv_concernNum;
    private LinearLayout ll_buy,ll_sell,ll_concern;
    private FloatingActionButton fab_head;
    private ImageView iv_headBackground;
    private Button b_out;
    private User user;
    private UserSettingAdapter adapter;
    private int mYear;
    private int mMonth;
    private int mDay;
    public static final String ANSWER_OUT = "out";
    public static final int REQUEST_USERSETTING = 1;
    private List<String> titleList;
    private List<String> contentlist ;
    private List<String> keyList;
    private final static String ImgUrlTest = "http://39.105.0.212/shop/products/a85ceef1bc69e78f29709fcae9b838e2.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        user = MyApplication.getUser();
        initView();
    }

    private void initView() {
        t_title = findViewById(R.id.t_title);
        ctl_coordinatorLayout = findViewById(R.id.ctl_CollapsingToolbarLayout);
        rv_settings = findViewById(R.id.rv_settings);
        b_out = findViewById(R.id.b_out);
        fab_head = findViewById(R.id.fab_head);
        iv_headBackground = findViewById(R.id.iv_headBackground);
        //设置ToolBar
        setSupportActionBar(t_title);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ctl_coordinatorLayout.setTitle(user.getName());

        //各种设置
        titleList =
                Arrays.asList("昵称","姓名","性别","生日","电话","邮箱","QQ","地址");
        //qq为云端数据库问题
        keyList =
                Arrays.asList("username","name","sex","birth","telephone","email","birthday","addr");
        contentlist = new ArrayList();
        contentlist.add(user.getName());
        contentlist.add(user.getRealName());
        contentlist.add(user.isSex()?"男":"女");
        contentlist.add(user.getBirthday());
        contentlist.add(user.getPhone());
        contentlist.add(user.getEmail());
        contentlist.add(user.getQqNum());
        contentlist.add(user.getAddress());
        rv_settings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserSettingAdapter(this,titleList,contentlist);
        rv_settings.setAdapter(adapter);
        adapter.setOnItemClickListener(rvItemClick);
        //账单信息
        tv_buyNum = findViewById(R.id.tv_buyNum);
        ll_buy = findViewById(R.id.ll_buy);
        tv_concernNum = findViewById(R.id.tv_concernNum);
        ll_concern = findViewById(R.id.ll_concern);
        tv_sellNum = findViewById(R.id.tv_sellNum);
        ll_sell = findViewById(R.id.ll_sell);

        ll_sell.setOnClickListener(billClickListener);
        ll_concern.setOnClickListener(billClickListener);
        ll_buy.setOnClickListener(billClickListener);
        //退出账号
        b_out.setOnClickListener(OutClick);

        int sellNum = user.getSellNum();
        int buyNum = user.getBuyNum();
        int concernNum = user.getConcernNum();
        tv_buyNum.setText(buyNum + "");
        tv_sellNum.setText(sellNum + "");
        tv_concernNum.setText(concernNum+"");
        String backUrl = user.getBackgroundUrl();
        String headUrl = user.getHeadUrl();
        if( backUrl != null){
            new MGetImgAsyncTask(1).execute(backUrl);
        }
        if( headUrl != null){
            new MGetImgAsyncTask(2).execute(headUrl);
        }
    }

    private View.OnClickListener OutClick = new  View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent data=new Intent();
            data.putExtra(ANSWER_OUT, "请先登录");
            setResult(RESULT_OK,data);
            user.setSpStatus(false);
            MessageDBUtils m = new MessageDBUtils(UserSettingActivity.this);
            m.clearRecentMsg();
            finish();
        }
    };

    View.OnClickListener billClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            //在此开启账单页面
            toastShort("billClickListener");
        }
    };

    /**
     * toolbar返回键事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
    private void toastShort(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    class MGetImgAsyncTask extends AsyncTask<String,Integer,Bitmap> {
        int which;//1是背景 ，2 是头像
        MGetImgAsyncTask(int which){
            this.which = which;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            String imgUrl = strings[0];
            if( !HttpUtils.isImgUrl(imgUrl)){
                return null;
            }
            String fileName = imgUrl.substring(imgUrl.lastIndexOf("/"));
            File file = new File(getCacheDir() + fileName);
            if (file.exists()) {
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            } else {
                URL url = null;
                InputStream in = null;
                FileOutputStream fos = null;
                try {
                    url = new URL(imgUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //设置请求方法，注意大写
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    if(conn.getResponseCode() == 200) {//200成功
                        //获取服务器响应头中的流，流里的数据即是客户端请求的数据
                        in = conn.getInputStream();
                        //存入缓存
                        fos = new FileOutputStream(file);
                        byte[] b = new byte[1024];
                        int len = 0;
                        while ((len = in.read(b)) != -1) {
                            fos.write(b, 0, len);
                            fos.flush();
                            //这个方法的作用是把缓冲区的数据强行输出。如果你不flush就可能会没有真正输出
                            //没有flush不代表它就没有输出出，只是可能没有完全输出。调用flush是保证缓存清空输出
                        }
                        return BitmapFactory.decodeFile(file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    LogUtils.e("",e.toString());
                    e.printStackTrace();
                }finally {
                    if(in != null){
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fos != null){
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null){
                return ;
            }
            switch (which){
                case  1:
                    iv_headBackground.setImageBitmap(bitmap);
                    break;
                case 2:
                    fab_head.setImageBitmap(bitmap);
                    break;
            }
        }
    }

    UserSettingAdapter.OnItemClickListener rvItemClick = new UserSettingAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, View view) {
            if(position == 3){
                //3为生日设置
                displayDateDialog();
            }else if(position == 2){
                //2为性别设置
                showSexChooseDialog();
            }else{
                //Activity_SettingEdit.actionStart(UserSettingActivity.this,titleList.get(position),contentlist.get(position));
                Intent intent = new Intent(UserSettingActivity.this,Activity_SettingEdit.class);

                intent.putExtra(Activity_SettingEdit.KEY_TITLE,titleList.get(position));
                intent.putExtra(Activity_SettingEdit.KEY_VALUELAST,contentlist.get(position));
                intent.putExtra(Activity_SettingEdit.KEY_POSITION,position);
                startActivityForResult(intent,REQUEST_USERSETTING);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            int position = data.getIntExtra(Activity_SettingEdit.KEY_POSITION,0);
            String value = data.getStringExtra(Activity_SettingEdit.KEY_VALUELAST);
            updateInformation(position,keyList.get(position),value);
        }
    }

    /**
     * 显示日期选择框
     */
    private void displayDateDialog(){
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        //new DatePickerDialog(context, onDateSetListener, mYear, mMonth, mDay).show();
        new DatePickerDialog(this,DatePickerDialog.THEME_HOLO_LIGHT,onDateSetListener, mYear, mMonth, mDay).show();
    }
    /**
     * 日期选择器对话框监听
     */
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String temp = mYear + "-" + mMonth + "-" + mDay;
            toast(temp);
            updateInformation(titleList.indexOf("生日"),"birth",temp);
            //在这发送网络请求，修改日期
        }
    };

    /**
     *性别选择
     */
    private void showSexChooseDialog() {
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);// 自定义对话框
        builder3.setSingleChoiceItems(new String[]{"男","女"}, 0, new DialogInterface.OnClickListener() {// 2默认的选中

            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                if(which == 0){
                    updateInformation(titleList.indexOf("性别"),"sex","male");
                }else{
                    updateInformation(titleList.indexOf("性别"),"sex","female");
                }
                dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
            }
        });
        builder3.show();// 让弹出框显示
    }

    /**
     * 修改性别，生日,key为网络段的key
     * @param position
     * @param key
     * @param value
     */
    private void updateInformation(final int position, final String key, final String value){

        HttpUtils.sendOkHttpRequest(Constant.updateUserInformationUrl,new String[]{"uid",key} , new String[]{user.getUserId(),value}, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.e("修改用户信息失败",e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String status = response.body().string();
                LogUtils.e("status",status);
                if(status.equals("success")){
                    if(key.equals("sex")){
                        if(value.equals("male")){
                            contentlist.set(position,"男");
                            user.setSpSex(true);
                        }else{
                            contentlist.set(position,"女");
                            user.setSpSex(false);
                        }
                    }else{//生日

                        contentlist.set(position,value);
                        switch (position){
                            case 0:
                                user.setSpName(value);
                                break;
                            case 1:
                                user.setSpRealName(value);
                                break;
                            case 3:
                                user.setSpBirthday(value);
                                break;
                            case 4:
                                user.setSpPhone(value);
                                break;
                            case 5:
                                user.setSpEmail(value);
                                break;
                            case 6:
                                user.setSpQqNum(value);
                                break;
                            case 7:
                                user.setSpAddress(value);
                                break;
                        }
                    }
                    Message m = mHandler.obtainMessage();
                    m.what = UPDATE_SUCCESS;
                    m.arg1 = position;
                    mHandler.sendMessage(m);
                }else{
                    Message m = mHandler.obtainMessage();
                    m.what = UPDATE_FAIL;
                    mHandler.sendMessage(m);
                }
            }
        });
    }
    private final int UPDATE_SUCCESS = 1;
    private final int UPDATE_FAIL = 0;
    @SuppressLint("HandlerLeak")
    private NotLeakHandler mHandler = new NotLeakHandler<UserSettingActivity>(UserSettingActivity.this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_FAIL:
                    toast("修改信息失败");
                    break;
                case UPDATE_SUCCESS:
                    adapter.notifyItemChanged(msg.arg1);
                    break;
            }
        }
    };

    private void toast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(UPDATE_FAIL);
        mHandler.removeMessages(UPDATE_SUCCESS);
    }
}
