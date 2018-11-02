package xx.erhuo.images;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import com.erhuo.view.Topbar;
import com.erhuo.view.Topbar.TopbarCilckListener;
import xx.erhuo.com.R;
import java.util.ArrayList;
import java.util.List;

/**图片预览界面2,增加ViewFlipper*/
public class ImgDisplayActivity2 extends Activity {
	//ViewFlipper初始时跳到指定页面先默认显示的第一个，先显示的CheckBox，
	//后显示的图片，所以会出现点进来若当前图片被选中，第一张图也会被选中的bug
	
	
	
	private Topbar topBar_pictureDisplay2;
	private ViewFlipper vf_display;
	private VfDisplayIniter myViewFlipperAdapter;
	private CheckBox cb_chose;
	private ProgressBar pb_imgLoading;
	private List<String> imagePathList;//已经选中的
	//private List<Integer> positionList;
	private List<String> imagePathAllList;
	private int position;
	private Handler mHandler;
	
	//手势相关
	private float startX;
	private Animation anim_left_out, anim_right_in,anim_left_in,anim_right_out,aIn,aOut;
	
	public static final String answer_imgPathList = "answer_imgPathList";
	
	
	public static final int msgFlipperAdapterAllOK = 0;
	public static final int msgFlipperAdapterNowOK = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_display2);
		init();
	}
	
	@SuppressLint("HandlerLeak")
	private void init(){
		pb_imgLoading = (ProgressBar) findViewById(R.id.pb_imgloading);
		topBar_pictureDisplay2 = (Topbar) findViewById(R.id.topBar_pictureDisplay2);
		vf_display = (ViewFlipper) findViewById(R.id.vf_display);
		cb_chose = (CheckBox) findViewById(R.id.cb_chose2);
		anim_right_in = AnimationUtils.loadAnimation(ImgDisplayActivity2.this, R.anim.right_in_fast);
		anim_left_out = AnimationUtils.loadAnimation(ImgDisplayActivity2.this, R.anim.left_out_fast);
		anim_left_in = AnimationUtils.loadAnimation(ImgDisplayActivity2.this, R.anim.left_in_fast);
		anim_right_out = AnimationUtils.loadAnimation(ImgDisplayActivity2.this, R.anim.right_out_fast);
		
		imagePathAllList = getIntent().getStringArrayListExtra(ImagesActivity.Extra_imagePathAllList);
		imagePathList = getIntent().getStringArrayListExtra(ImagesActivity.Extra_imagePathList);
		position = getIntent().getIntExtra(ImagesActivity.Extra_imagePosition, 0);
		cb_chose.setOnCheckedChangeListener(new myCBlistener());
		
		//设置初始化CheckBox状态,应该在Viewflipper加载完成时

		vf_display.getViewTreeObserver().addOnGlobalLayoutListener(myOnGlobalLayoutListener);


		anim_left_in.setAnimationListener(new MyAnmiListener());
		anim_right_in.setAnimationListener(new MyAnmiListener());
		topBar_pictureDisplay2.setOnTopbarCilckListener(new myTopbarListener());
		mHandler = new  Handler(){//~可能出现的危害内存泄漏https://blog.csdn.net/jdsjlzx/article/details/51386440
			
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				switch(msg.what){
				case msgFlipperAdapterAllOK:

					for(int i = 0; i<imagePathAllList.size() ; i++){
						View v = myViewFlipperAdapter.getAView(i);
						if (v.getParent() != null) {
							((ViewGroup)v.getParent()).removeView(v);
							}
						vf_display.addView(v);
					}
					vf_display.setDisplayedChild(position);
					//设置初始化CheckBox状态
					String imgPath = imagePathAllList.get(position);
					if( imagePathList.contains(imgPath)){
						cb_chose.setChecked(true);
					}
					vf_display.setOnTouchListener(new myTouchListener());
					break;
					default:
						break;
					
				}
			}
		};
		
		myViewFlipperAdapter = new VfDisplayIniter(ImgDisplayActivity2.this, imagePathAllList,position,ImgDisplayActivity2.this);
				
	}
	
	/**topBar监听器*/
	class myTopbarListener implements TopbarCilckListener {
		public void rightClick(View v) {
			Intent data=new Intent();
			
		    data.putStringArrayListExtra(answer_imgPathList, (ArrayList<String>) imagePathList);
		    ImgDisplayActivity2.this.setResult(RESULT_OK,data);
			ImgDisplayActivity2.this.finish();
			
			
		}
		public void leftClick(View v) {
			ImgDisplayActivity2.this.finish();
			
		}
	}
	
	/**CheckBox状态变化监听器*/
	class myCBlistener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			int position = vf_display.getDisplayedChild();
			String imgPath = imagePathAllList.get(position);
			
			if(isChecked){
				if( !imagePathList.contains(imgPath)){
					imagePathList.add(imgPath);
					
				}
			}else{
				imagePathList.remove(imgPath);
			}
			
			
		}
		
	}
	/***/
	ViewTreeObserver.OnGlobalLayoutListener myOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener(){

		@Override
		public void onGlobalLayout() {

			pb_imgLoading.setVisibility(View.GONE);
		}
	};


	/**ViewFlipper手势滑动监听*/
	class myTouchListener implements View.OnTouchListener{
		boolean isMove = false;
		boolean isLeftIn = true;
		
		@SuppressLint("ClickableViewAccessibility")
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					//vf_advertise.stopFlipping();
					startX = event.getX();
					break;
				case MotionEvent.ACTION_MOVE:
					if( (event.getX() - startX) > 300){
						aIn = anim_left_in;
						aOut = anim_right_out;
						isMove = true;
						isLeftIn = true;
					}else if((startX - event.getX()) > 300){
						aIn = anim_right_in;
						aOut = anim_left_out;
						isMove = true;
						isLeftIn = false;
					}
					
					break;
				case MotionEvent.ACTION_UP:
					if(isMove){
						vf_display.setInAnimation(aIn);
						vf_display.setOutAnimation(aOut);
						if(isLeftIn){
							vf_display.showPrevious();
						}else{
							vf_display.showNext();
						}
						isMove = false;
						//vf_display.startFlipping();
					}
						break;
			}
			Log.d("myTouchListener", "touch");
			return true;
		}
	}
	
	/**因为ViewFlipper没有滑动事件监听器，故通过其动画效果来判定*/
	class MyAnmiListener implements AnimationListener {

		@Override
		public void onAnimationStart(Animation animation) {
			
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			int position = vf_display.getDisplayedChild();
			String imgPath = imagePathAllList.get(position);
			if( imagePathList.contains(imgPath)){
				cb_chose.setChecked(true);
			}else{
				cb_chose.setChecked(false);
			}
			
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
			
		}
		
	}
	/**返回此Activity的Handler*/
	public Handler getHandler(){
		return this.mHandler;
	}
	@Override
	public void onDestroy() {
	    //  If null, all callbacks and messages will be removed.
		super.onDestroy();
	    mHandler.removeCallbacksAndMessages(null);
	}

}

