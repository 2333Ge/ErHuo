package xx.erhuo.images;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import xx.erhuo.com.R;

import java.util.ArrayList;
import java.util.List;

/**主页面适配器*/
public class ImgsAdapter extends BaseAdapter {

	Context context;
	List<String> data;
	public Bitmap bitmaps[];
	Util util;
	OnItemClickClass onItemClickClass;
	private int index=-1;
	
	List<View> holderlist;
	
	//自加
	private OnitemClickListener myOnitemClickListener;
	
	
	public ImgsAdapter(Context context, List<String> data, OnItemClickClass onItemClickClass) {
		this.context=context;
		this.data=data;
		this.onItemClickClass=onItemClickClass;
		bitmaps=new Bitmap[data.size()];
		util=new Util(context);
		holderlist=new ArrayList<View>();
	}
	public ImgsAdapter(Context context, List<String> data, OnitemClickListener myOnitemClickListener) {
		this.context=context;
		this.data=data;
		this.myOnitemClickListener=myOnitemClickListener;
		bitmaps=new Bitmap[data.size()];
		util=new Util(context);
		holderlist=new ArrayList<View>();
	}
	
	@Override
	public int getCount() {
		return data == null? 0 :data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		Holder holder;
		if (position != index && position > index) {
			index=position;
			view= LayoutInflater.from(context).inflate(R.layout.imgsitem, null);//提示尽量避免用null
			holder=new Holder();
			holder.imageView = (ImageView) view.findViewById(R.id.imageView1);
			holder.checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
			view.setTag(holder);
			holderlist.add(view);
		}else {
			holder= (Holder)holderlist.get(position).getTag();
			view=holderlist.get(position);
		}
		if (bitmaps[position] == null) {
			//加载速度快的秘密在这
			util.imgExcute(holder.imageView,new ImgClallBackLisner(position), data.get(position));
			
			
		}
		else {
			
			holder.imageView.setImageBitmap(Util.zoomBitmap2(bitmaps[position],
					holder.imageView.getWidth(), holder.imageView.getHeight()));
		}
		holder.imageView.setOnClickListener(new myOnClickListener(position));
		holder.checkBox.setOnCheckedChangeListener(new myOnCheckedChangeListener(position));
		return view;
	}
	
	class Holder{
		ImageView imageView;
		CheckBox checkBox;
	}

	//设置imageView
	public class ImgClallBackLisner implements ImgCallBack{
		int num;
		public ImgClallBackLisner(int num) {
			this.num=num;
		}
		
		@Override
		public void resultImgCall(ImageView imageView, Bitmap bitmap) {
			bitmaps[num]=bitmap;
			//设置成控件的高宽，方便自适应
			imageView.setImageBitmap(Util.zoomBitmap2(bitmap,
					imageView.getWidth(), imageView.getHeight()));
		
		}
	}

	public interface OnItemClickClass{
		public void OnItemClick(View v, int Position, CheckBox checkBox);
	}

	/**图片点击事件*/
	class OnPhotoClick implements OnClickListener {
		int position;
		CheckBox checkBox;

		public OnPhotoClick(int position,CheckBox checkBox) {
			this.position=position;
			this.checkBox=checkBox;
		}
		@Override
		public void onClick(View v) {
			if (data!=null && onItemClickClass!=null ) {
				onItemClickClass.OnItemClick(v, position, checkBox);
			}
		}
	}
	/**图片点击事件*/
	class OnCheckBoxChanged implements OnCheckedChangeListener {
		int position;
		CheckBox checkBox;

		public OnCheckBoxChanged(int position,CheckBox checkBox) {
			this.position=position;
			this.checkBox=checkBox;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (data!=null && onItemClickClass!=null ) {
				onItemClickClass.OnItemClick(buttonView, position, checkBox);
			}
		}

	}


	//以下是后期添加
	/**外部接口，实现图片点击，和checkedBox 变化的监听*/
	public interface OnitemClickListener{
		/**重写CheckBox的CheckedChange事件,传入参数position*/
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position);
		/**重写ImageView的click事件，传入参数position*/
		public void onPohtoClicked(View v, int position);
		
		
	}
	/**设置监听器*/
	public void setOnitemClickListener(OnitemClickListener myOnitemClickListener){
		this.myOnitemClickListener = myOnitemClickListener;
	}
	/**重写ImageView的click事件，传入参数position*/
	class myOnClickListener implements OnClickListener {
		int position;
		public myOnClickListener(int position){
			this.position = position;
		}
		@Override
		public void onClick(View v) {
			if(data ==null){
				Log.d("myOnClickListener", "data ==null");
			}if(myOnitemClickListener == null){
				Log.d("myOnClickListener", "myOnitemClickListener == null");
			}
			if (data!=null && myOnitemClickListener!=null ) {
				
				myOnitemClickListener.onPohtoClicked(v,position);
			}
			
		}
	}
	/**重写CheckBox的CheckedChange事件,传入参数position*/
	class myOnCheckedChangeListener implements OnCheckedChangeListener {

		int position = -1;
		public myOnCheckedChangeListener(int position){
			this.position = position;;
		}
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (data!=null && myOnitemClickListener!=null ) {
				myOnitemClickListener.onCheckedChanged(buttonView,isChecked,position);
			}
		}
	}
	/**!失败，点击变换状态监听事件，不通过系统变换响应此事件 */
	class myOnClickAndChangeListener implements OnClickListener {

		int position;
		public myOnClickAndChangeListener(int position){
			this.position = position;;
		}
		@Override
		public void onClick(View v) {
			if (data!=null){
				//myOnitemClickListener.OnClickAndChangeListener(v,position);
			}
			
		}
		
	}
	/**设置CheckeBox状态*/
	public void setCheckeBoxState(int position,boolean state){
		View v = holderlist.get(position);
		CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);
		cb.setChecked(state);
	}
	/**返回相应CheckeBox状态*/
	public boolean isChecked(int position){
		View v = holderlist.get(position);
		CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);
		return cb.isChecked();
	}
	
	
}
