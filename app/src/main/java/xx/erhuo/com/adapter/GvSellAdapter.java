package xx.erhuo.com.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.erhuo.view.SelectImageView;
import com.erhuo.view.SelectImageView.SelectImageClickListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import xx.erhuo.com.R;
import xx.erhuo.images.Util;

public class GvSellAdapter extends BaseAdapter {
	private Context context;
	private List<String> ListImagePath;
	private SelectImageItemClickListener itemClickListener;
	private AddImgClickListener addImgClickListener;
	Util util;
	/**将监听器固化到构造器里，就不用单独写个函数每次进行设置*/
	public GvSellAdapter(Context context, List<String> listImagePath, SelectImageItemClickListener itemClickListener){
		this.context = context;
		this.ListImagePath = listImagePath;
		this.itemClickListener = itemClickListener;
		util = new Util(context);
	}
	
	/**监听器接口*/
	public interface SelectImageItemClickListener{
		public void DeleteClick(int position, List<String> listfile);
		public void SelectClick(int position);
	}
	/**设置监听器*/
	public void setAddImgClickListener(AddImgClickListener addImgClickListener){
		this.addImgClickListener = addImgClickListener;
	}
	/**添加图片监听器接口*/
	public interface AddImgClickListener{
		public void onClick(View v);
		
	}
	/**设置添加图片监听器*/
	public void setSelectImageItemClickListener(SelectImageItemClickListener SelectImageItemClickListener){
		this.itemClickListener = SelectImageItemClickListener;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int size = ListImagePath.size();
		if( size == 0 || position == size && size != 8 ){
			View v = View.inflate(context, R.layout.item_add, null);
			ImageView imageView = (ImageView) v.findViewById(R.id.iv_add);
		     imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					addImgClickListener.onClick(v);
				}
			});
		    return imageView;
		}
		View v ;
		if(convertView != null){
			v = convertView;
		}else{
			v = View.inflate(context, R.layout.item_picture_check2, null);
			SelectImageView siv = (SelectImageView) v.findViewById(R.id.siv_itemImgCheck2);
			
			try {
				Bitmap bitmap = util.getPathBitmap(Uri.fromFile(new File(ListImagePath.get(position))), 400, 400);
				Bitmap Bitmap100 = Util.zoomBitmap2(bitmap, 100, 100);
				siv.setCenterBitmap(Bitmap100);
				siv.setOnSivClickListener(new mySelectImageClickListener(position));
			} catch (FileNotFoundException e) {
				
				Log.e("getView_FileNotFound", e.toString());
			}
		}
		
		return v;
	}
	/**图片点击监听器*/
	class mySelectImageClickListener implements SelectImageClickListener {
		int position;
		public mySelectImageClickListener(int position){
			this.position = position;
		}
		/**右上角点击监听，刷新交给适配器，不用重写*/
		@Override
		public void DeleteClick(View v) {
			//ListImagePath.remove(position);
			//notifyDataSetChanged();
			itemClickListener.DeleteClick(position,ListImagePath);
		}
		@Override
		public void SelectClick(View v) {
			itemClickListener.SelectClick(position);
		}
		
	}
	@Override
	public int getCount() {
		if(ListImagePath == null){
			return 0;
		}
		if(ListImagePath.size() == 8){
			return 8;
		}else{
			return ListImagePath.size()+1;
		}
	}

	@Override
	public Object getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}
}
