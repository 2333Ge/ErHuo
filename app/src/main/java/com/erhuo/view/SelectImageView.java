package com.erhuo.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import xx.erhuo.com.R;

public class SelectImageView extends RelativeLayout {
	
	private ImageView imageDelete,imageChose;
	
	//private int deleteBackgroundtColor;
	private Drawable deleteBackground;
	private Drawable deleteBackgroundSrc;

	
	//private int choseBackgroundtColor;
	private Drawable choseBackground;
	private Drawable choseBackgroundSrc;

	
	private LayoutParams deleteParams,choseParams;
	
	private SelectImageClickListener clickListener;
	
	public interface SelectImageClickListener{
		public void DeleteClick(View v);
		public void SelectClick(View v);
		
	}
	//适配器模式
	public void setOnSivClickListener(SelectImageClickListener SelectImageClickListener){
		this.clickListener = SelectImageClickListener;
	}

	
	@SuppressLint("NewApi")
	public SelectImageView(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.SelectImageView);
		
		//deleteBackgroundtColor = ta.getColor(R.styleable.SelectImageView_deleteBackground, 0);
		deleteBackground = ta.getDrawable(R.styleable.SelectImageView_deleteBackground);
		deleteBackgroundSrc = ta.getDrawable(R.styleable.SelectImageView_deleteSrc);

		//choseBackgroundtColor = ta.getColor(R.styleable.SelectImageView_imageBackground, 0);
		choseBackground = ta.getDrawable(R.styleable.SelectImageView_imageBackground);
		choseBackgroundSrc = ta.getDrawable(R.styleable.SelectImageView_imageSrc);

		
		ta.recycle();
		
		imageDelete = new ImageView(context);
		imageChose = new ImageView(context);
		//绑定属性
		imageDelete.setBackground(deleteBackground);
		imageDelete.setImageDrawable(deleteBackgroundSrc);

		imageChose.setBackground(choseBackground);
		imageChose.setImageDrawable(choseBackgroundSrc);
		
		choseParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//此处的TRUE是RelativeLayout中定义的一个常量
		choseParams.addRule(RelativeLayout.CENTER_IN_PARENT,TRUE);
		//添加
		addView(imageChose,choseParams);
		
		deleteParams = new LayoutParams(75, 75);
		//此处的TRUE是RelativeLayout中定义的一个常量
		deleteParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,TRUE);
		deleteParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,TRUE);
		//添加
		addView(imageDelete,deleteParams);
		
		imageDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clickListener.DeleteClick(v);
				
			}
		});
		imageChose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clickListener.SelectClick(v);
				
			}
		});
	}
	public void setCenterBitmap(Bitmap bitmap){
		imageChose.setImageBitmap(bitmap);
	}
	public void setCancleBitmap(Bitmap bitmap){
		imageDelete.setImageBitmap(bitmap);
	}
	
}
