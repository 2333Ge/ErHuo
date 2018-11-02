package xx.erhuo.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.erhuo.utils.LogUtils;
import com.erhuo.view.SelectImageView;
import com.erhuo.view.SelectImageView.SelectImageClickListener;
import xx.erhuo.com.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**预览选择的图片界面的适配器*/
public class GridViewAdapter extends BaseAdapter {
	private Context context;
	private List<String> ListImagePath;
	private SelectImageItemClickListener itemClickListener;
	Util util;
	/**监听器接口*/
	public interface SelectImageItemClickListener{
		public void DeleteClick(int position, List<String> listfile);
		public void SelectClick(int position);
	}
	/**设置监听器*/
	public void setSelectImageItemClickListener(SelectImageItemClickListener SelectImageItemClickListener){
		this.itemClickListener = SelectImageItemClickListener;
	}
	//已经new了，list<View>不会改变？？
	/**将监听器固化到构造器里，就不用单独写个函数每次进行设置，此处是没有设置监听器*/
	public GridViewAdapter(Context context, List<String> listImagePath){
		this.context = context;
		this.ListImagePath = listImagePath;
		//ViewHorder = new ArrayList<View>();
		util = new Util(context);
	}
	/**将监听器固化到构造器里，就不用单独写个函数每次进行设置*/
	public GridViewAdapter(Context context, List<String> listImagePath, SelectImageItemClickListener itemClickListener){
		this.context = context;
		this.ListImagePath = listImagePath;
		this.itemClickListener = itemClickListener;
		//ViewHorder = new ArrayList<View>();
		util = new Util(context);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v ;
		if(convertView != null){
			v = convertView;
		}else{
			v = View.inflate(context, R.layout.item_picture_check, null);
			SelectImageView siv = (SelectImageView) v.findViewById(R.id.siv_itemImgCheck);
			
			try {
				Bitmap bitmap = util.getPathBitmap(Uri.fromFile(new File(ListImagePath.get(position))), 200, 200);
				Bitmap Bitmap100 = Util.zoomBitmap2(bitmap, 100, 100);
				siv.setCenterBitmap(Bitmap100);
				siv.setOnSivClickListener(new mySelectImageClickListener(position));
			} catch (FileNotFoundException e) {
				
				LogUtils.e("getView_FileNotFound", e.toString());
			}
		}
		
		return v;
	}
	class Horder{
		public SelectImageView siv;
	}
	
	@Override
	public int getCount() {
		//return listImagePath.size();
		return ListImagePath == null ? 0:ListImagePath.size();
	}
	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
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
	

}
