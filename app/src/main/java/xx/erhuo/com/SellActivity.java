package xx.erhuo.com;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.erhuo.view.Topbar;
import com.erhuo.view.Topbar.TopbarCilckListener;


import java.util.ArrayList;
import java.util.List;

import xx.erhuo.com.adapter.GvSellAdapter;
import xx.erhuo.images.ImagesActivity;
import xx.erhuo.images.ImgDisplayActivity;

public class SellActivity extends Activity {

	private GridView gv_chose;
	private EditText et_title, et_detail,et_price,et_priceOriginal;
	private Button b_typeName;
	private TextView b_type;
	private Button b_Up;
	private ImageView iv_type_in;
	private Topbar topbarSell;
	private ArrayList<String> listfile = new ArrayList<String>();
	private myitemClickListener listener1;
	private GvSellAdapter gvAdapter;
	private AddListener addListener;
	//--2-- 暂存下一个页面传回的布局情况，方便二次进入时的显示
	private ArrayList<Integer> listForder = new ArrayList<Integer>();
	private ArrayList<Integer> listPosition = new ArrayList<Integer>();
	private String typeBig;
	private String typeSmall;
	private Handler handler;
	
	private static final String upUrl = "http://39.105.0.212/shop/user_login.action";
	private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 10;
	private static final int requestCode_chosePictures = 1;
	private static final int requestCode_picture = 2;
	private static final int requestCode_choseType = 3;
	public static final String Extra_pathList = "Extra_pathList";
	public static final String Extra_forderListTemp = "Extra_forderListTemp";
	public static final String Extra_positionListTemp = "Extra_positionListTemp";
	public static final String Extra_imagePath = "Extra_imagePath";
	public static final String Extra_imageIsSelected = "Extra_imageIsSelected";//跳转页面时，设置对应CheckBox状态
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sell);
		checkPermission();
		init();
	}
	/**初始化*/
	@SuppressLint("HandlerLeak")
	private  void init(){
		
		gv_chose = (GridView) findViewById(R.id.gv_chose);
		et_title = (EditText) findViewById(R.id.et_sellName);
		et_detail = (EditText) findViewById(R.id.et_sellDetail);
		et_price = (EditText) findViewById(R.id.et_price);
		et_priceOriginal =  (EditText) findViewById(R.id.et_priceOriginal);;
		b_type = (TextView) findViewById(R.id.b_type);
		b_Up = (Button) findViewById(R.id.b_up);
		topbarSell = (Topbar) findViewById(R.id.topbar_sell);
		 b_typeName = (Button) findViewById(R.id. b_typeName);
		 iv_type_in = (ImageView) findViewById(R.id.iv_type_in);
		listener1 = new myitemClickListener();
		addListener = new AddListener();
		gvAdapter = new GvSellAdapter(SellActivity.this, listfile, listener1);
		gvAdapter.setAddImgClickListener(addListener);
		gv_chose.setAdapter(gvAdapter);
		
		topbarSell.setOnTopbarCilckListener(new MyTopbarCilckListener());
		iv_type_in.setOnClickListener(new ChoseTypeClick());
		b_type.setOnClickListener(new ChoseTypeClick());
		
		handler = new  Handler(){
			//自定义0时失败,1时成功
			public void handleMessage(android.os.Message msg){
				switch (msg.what){
					case 0:
						Toast.makeText(SellActivity.this,"连接失败" + (String)msg.obj , Toast.LENGTH_LONG).show();
						break;
					case 1:
						Toast.makeText(SellActivity.this,"连接成功:" + (String)msg.obj , Toast.LENGTH_LONG).show();
						break;
					
					}
			}};
	}
	public void setGvAdapter(){
		gvAdapter = new GvSellAdapter(SellActivity.this, listfile, listener1);
    	gvAdapter.setAddImgClickListener(addListener);
    	gv_chose.setAdapter(gvAdapter);
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == requestCode_chosePictures && resultCode==RESULT_OK){
            if (data!=null){
            	listfile = data.getStringArrayListExtra(ImagesActivity.answer_imgPathList);
            	listForder = data.getIntegerArrayListExtra(ImagesActivity.answer_imgForderMap);
            	listPosition = data.getIntegerArrayListExtra(ImagesActivity.answer_imgPositionMp);
            	
            	gvAdapter = new GvSellAdapter(SellActivity.this, listfile, listener1);
            	gvAdapter.setAddImgClickListener(addListener);
            	gv_chose.setAdapter(gvAdapter);
         }
        }else if ( requestCode == requestCode_picture && resultCode==RESULT_OK){
             if (data!=null){
                 boolean isSelected = data.getBooleanExtra(ImgDisplayActivity.answer_isSelected, false);
                 String imgPath = data.getStringExtra(ImgDisplayActivity.answer_imgPath);
                if( !isSelected ){
                	int index = listfile.indexOf(imgPath);
                	listfile.remove(index);
                	listForder.remove(index);
                	listPosition.remove(index);
                	gvAdapter = new GvSellAdapter(SellActivity.this, listfile, listener1);
                	gvAdapter.setAddImgClickListener(addListener);
                	gv_chose.setAdapter(gvAdapter);
                }
             }
        }else if( requestCode == requestCode_choseType && resultCode==RESULT_OK){
        	 if (data!=null){
        		 typeBig = data.getStringExtra(ChoseTypeActivity.answer_bigType);
        		 typeSmall = data.getStringExtra(ChoseTypeActivity.answer_smallType);
        		b_typeName.setVisibility(View.VISIBLE);
        		b_typeName.setText(typeBig + "/" + typeSmall);
        	 }
        }
     }
	class ChoseTypeClick implements View.OnClickListener{
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(SellActivity.this,ChoseTypeActivity.class);
			startActivityForResult(intent, requestCode_choseType);
		}
	}

	/***/
	class MyTopbarCilckListener implements TopbarCilckListener {

		@Override
		public void leftClick(View v) {
			SellActivity.this.finish();
		}

		@Override
		public void rightClick(View v) {
			
		}
		
	}


	class AddListener implements GvSellAdapter.AddImgClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(SellActivity.this,ImagesActivity.class);
			if(listfile != null){
				intent.putExtra(Extra_pathList,listfile);
				//map不能直接putExtra，转换为ArrayList
				intent.putIntegerArrayListExtra(Extra_forderListTemp,listForder);
				intent.putIntegerArrayListExtra(Extra_positionListTemp,listPosition);
			}
			startActivityForResult(intent, requestCode_chosePictures);
			}
		}
		
	
	class myitemClickListener implements GvSellAdapter.SelectImageItemClickListener{

		@Override
		public void DeleteClick(int position, List<String> listfile) {
			String imgPath = listfile.get(position);
			
			listfile.remove(imgPath);
			gvAdapter = new GvSellAdapter(SellActivity.this, listfile, listener1);
			gvAdapter.setAddImgClickListener(addListener);
        	gv_chose.setAdapter(gvAdapter);
			
			
		}
		@Override
		public void SelectClick(int position) {
			//只能预览，不能勾选，因为后台的后面GridView可能已经发生变化
			String filepath=listfile.get(position);
			Intent intent = new Intent(SellActivity.this,ImgDisplayActivity.class);
			intent.putExtra(Extra_imagePath, filepath);
			intent.putExtra(Extra_imageIsSelected, true);
			startActivityForResult(intent, requestCode_picture);
		}
		
	}
	/**android6.0以上权限申请,需要support-v4包*/
	private void checkPermission(){
		//检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
	    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
	            != PackageManager.PERMISSION_GRANTED) {
	        //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释,用户选择不再询问时，此方法返回 false。
	        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
	                .WRITE_EXTERNAL_STORAGE)) {
	            Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
	        }
	        //申请权限,第三个参数是请求码便于在onRequestPermissionsResult 方法中根据requestCode进行判断.
	        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},REQUEST_WRITE_EXTERNAL_STORAGE);

	    } else {
	       // Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
	        Log.d("checkPermission", "checkPermission: 已经授权！");
	    }
	}
}
