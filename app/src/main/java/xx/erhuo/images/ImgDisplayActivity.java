package xx.erhuo.images;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;

import com.erhuo.view.Topbar;
import com.erhuo.view.Topbar.TopbarCilckListener;

import uk.co.senab.photoview.PhotoView;
import xx.erhuo.com.R;

import java.io.File;

/**图片预览界面*/
public class ImgDisplayActivity extends Activity {
	private CheckBox cb_chose;
	private Topbar topBar_pictureDisplay;
	//private ImageView iv_display;

	private PhotoView pv_display;
	
	private String imagePath;
	private Util util;
	private Bitmap bitmap;
	
	public static final String answer_imgPath = "answer_imgPath";
	public static final String answer_isSelected = "answer_isSelected";
	
	//获取屏幕长宽数据
	Resources resources ;
	DisplayMetrics dm ;
	float density ;
	private int phoneWidth;
	private int phoneHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_display);
		init();
}
	private void init(){
		cb_chose = (CheckBox) findViewById(R.id.cb_chose);
		topBar_pictureDisplay = (Topbar) findViewById(R.id.topBar_pictureDisplay);
		//iv_display = (ImageView) findViewById(R.id.iv_display);
		pv_display = findViewById(R.id.iv_display);
		
		imagePath = getIntent().getStringExtra(ImagesActivity.Extra_imagePath);
		boolean isSelected = getIntent().getBooleanExtra(ImagesActivity.Extra_imageIsSelected,false);
		cb_chose.setChecked(isSelected);
		
		util = new Util(ImgDisplayActivity.this);
		//获取屏幕长宽数据,以显示图片
		resources = this.getResources();
		dm = resources.getDisplayMetrics();
		density = dm.density;
		phoneWidth = dm.widthPixels;
		phoneHeight = dm.heightPixels;
		
//		try {
//			bitmap = util.getPathBitmap(Uri.fromFile(new File(imagePath)), phoneWidth, phoneHeight);
//
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			}
		bitmap = BitmapFactory.decodeFile(imagePath);
		pv_display.setImageBitmap(bitmap);
//		Glide.with(this).load(Uri.fromFile(new File(imagePath))).into(pv_display);
		/**topBar监听器*/
		topBar_pictureDisplay.setOnTopbarCilckListener(new TopbarCilckListener() {
			
			public void rightClick(View v) {
				Intent data=new Intent();
				//Toast.makeText(ImgDisplayActivity.this,"imagePath：" + imagePath + "cb_chose.isChecked():" + cb_chose.isChecked(), Toast.LENGTH_SHORT).show();
				data.putExtra(answer_imgPath,imagePath);
			    data.putExtra(answer_isSelected,cb_chose.isChecked());
			    ImgDisplayActivity.this.setResult(RESULT_OK,data);
				ImgDisplayActivity.this.finish();
				
				
			}
			public void leftClick(View v) {
				ImgDisplayActivity.this.finish();
				
			}
		});
	}
	

}
