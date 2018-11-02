package xx.erhuo.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import xx.erhuo.com.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**目录选择页面适配器*/
public class ImgFileListAdapter extends BaseAdapter {

	Context context;
	String filecount="filecount";
	String filename="filename";
	String imgpath="imgpath";
	List<HashMap<String, String>> listdata;
	Util util;
	Bitmap[] bitmaps;
	private int index=-1;
	List<View> holderlist;
	
	public ImgFileListAdapter(Context context, List<HashMap<String, String>> listdata) {
		this.context=context;
		this.listdata=listdata;
		bitmaps=new Bitmap[listdata.size()];
		util=new Util(context);
		holderlist=new ArrayList<View>();
	}
	
	@Override
	public int getCount() {
		return listdata == null ? 0:listdata.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listdata.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		Holder holder;
		if (arg0 != index && arg0 > index) {
			holder=new Holder();
			arg1= LayoutInflater.from(context).inflate(R.layout.imgfileadapter, null);
			holder.photo_imgview=(ImageView) arg1.findViewById(R.id.filephoto_imgview);
			holder.filecount_textview=(TextView) arg1.findViewById(R.id.filecount_textview);
			holder.filename_textView=(TextView) arg1.findViewById(R.id.filename_textview);
			arg1.setTag(holder);
			holderlist.add(arg1);
		}else{
			holder= (Holder)holderlist.get(arg0).getTag();
			arg1=holderlist.get(arg0);
		}
		
		holder.filename_textView.setText(listdata.get(arg0).get(filename));
		holder.filecount_textview.setText(listdata.get(arg0).get(filecount));
		if (bitmaps[arg0] == null) {
			//设置imageView
			util.imgExcute(holder.photo_imgview,new ImgCallBack() {
				@Override
				public void resultImgCall(ImageView imageView, Bitmap bitmap) {
					bitmaps[arg0]=bitmap;
					//imageView.setImageBitmap(bitmap);
					
					//缩略图
					imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 200, 200));
					//设置成控件的高宽，方便自适应
					//imageView.setImageBitmap(Util.zoomBitmap2(bitmap,
					//		imageView.getWidth(), imageView.getHeight()));
				
				}
			}, listdata.get(arg0).get(imgpath));
		}
		else {
			//holder.photo_imgview.setImageBitmap(bitmaps[arg0]);
			//缩略图
			holder.photo_imgview.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmaps[arg0], 200, 200));
			//设置成控件的高宽，方便自适应
			//holder.photo_imgview.setImageBitmap(Util.zoomBitmap2(bitmaps[arg0],
			//		holder.photo_imgview.getWidth(), holder.photo_imgview.getHeight()));
		}
		
		return arg1;
	}
	
	class Holder{
		public ImageView photo_imgview;
		public TextView filecount_textview;
		public TextView filename_textView;
	}
	

	
	
}
