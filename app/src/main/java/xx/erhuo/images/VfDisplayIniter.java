package xx.erhuo.images;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.erhuo.view.ImageLoader;
import xx.erhuo.com.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**初始化滑动浏览界面需要的View*/
public class VfDisplayIniter {
	private Context context;
	//private List<String> imgList;
	private List<String> imgListAll;
	private Util util;
	private List<View> listView;
	//获取屏幕长宽数据
	private Resources resources ;
	private DisplayMetrics dm ;
	float density ;
	//private OncheckBoxChangedListener myListener;
	private ImgDisplayActivity2 mActivity;
	//
	private ImageLoader mImageLoader;

	
	private Handler mHandler;
	
	
	public VfDisplayIniter(Context context, List<String> imgListAll, int nowPosition, ImgDisplayActivity2 ida) {
		//super();
		this.context = context;
		//this.imgList = imgList;
		this.imgListAll = imgListAll;
		//this.myListener = myListener;
		util = new Util(context);
		listView = new ArrayList<View>();
		resources = context.getResources();
		dm = resources.getDisplayMetrics();
		density = dm.density;
		mActivity = ida;
		new InitViewThread().start();
		mImageLoader = new ImageLoader();
	}
	
	
	/**初始化各个view*/
	public void setView(int position) {
		
		View v ;
		ImageView iv_display2;
		v = View.inflate(context, R.layout.item_viewflipper_picture, null);
		iv_display2 = (ImageView) v.findViewById(R.id.iv_display2);
		Bitmap bitmap = null ;
		try {
			File file = new File(imgListAll.get(position));
			bitmap = util.getPathBitmap(Uri.fromFile(file), 600, 600);
		} catch (Exception e) {
			Log.e("getView", e.toString());
			e.printStackTrace();
		}
		iv_display2.setImageBitmap(bitmap);
		
		listView.add(v);
		if(position + 1 == imgListAll.size()){
			mHandler = mActivity.getHandler();
			mHandler.sendEmptyMessage(ImgDisplayActivity2.msgFlipperAdapterAllOK);
			
		}
	}
	/**初始化各个view,使用了LruCache*/
	public void setView2(int position) {
		
		View v ;
		ImageView iv_display2;
		v = View.inflate(context, R.layout.item_viewflipper_picture, null);
		iv_display2 = (ImageView) v.findViewById(R.id.iv_display2);
		String imgPath = imgListAll.get(position);
		iv_display2.setTag(imgPath);
		mImageLoader.setImageViewFromPath(context, iv_display2, imgPath, 400, 400);
		
		listView.add(v);
		if(position + 1 == imgListAll.size()){
			mHandler = mActivity.getHandler();
			mHandler.sendEmptyMessage(ImgDisplayActivity2.msgFlipperAdapterAllOK);
			
		}
	}
	/**开启新线程加载view*/
	class InitViewThread extends Thread {
		@Override
		public void run() {
			super.run();
			for(int i = 0;i< imgListAll.size(); i++){
				//setView(i);
				setView2(i);
			}
			
		}
	}
	
	
	/**获得指定View*/
	public View getAView(int position){
		return listView.get(position);
	}
	
}
