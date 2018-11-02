package com.erhuo.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xx.erhuo.com.R;

/**自定义控件，顶部布局，便于复用*/
public class Topbar extends RelativeLayout {
	
	
	private Button leftButton,rightButton;
	private TextView tvTitle;
	
	private int leftTextColor;
	private Drawable leftBackground;
	private String leftText;
	private float leftTextSize;
	private float leftWidth;
	private float leftHeight;
	private boolean leftIsVisable;
	private boolean leftIsClickable;
	
	private int rightTextColor;
	private Drawable rightBackground;
	private String rightText;
	private float rightTextSize;
	private boolean rightIsVisable;
	private boolean rightIsClickable;
	private float rightWidth;
	private float rightHeight;
	
	private int titleTextColor;
	private String titleText;
	private float titleTextSize;
	
	//布局
	private LayoutParams leftParams,rightParams,titleParams;
	
	//点击事件映射，接口回调机制
	private TopbarCilckListener listener;
	/**顶部点击监听事件*/
	public interface TopbarCilckListener{
		public void leftClick(View v);
		public void rightClick(View v);
	}
	//适配器机制
	public void setOnTopbarCilckListener(TopbarCilckListener TopbarCilckListener){
		this.listener = TopbarCilckListener;
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	public Topbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		//取出自定义属性的值的映射
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.Topbar);
		//第二个参数是默认值
		leftTextColor = ta.getColor(R.styleable.Topbar_leftTextColor, 0x7f040000);//默认灰色
		leftBackground = ta.getDrawable(R.styleable.Topbar_leftBackground);
		leftText = ta.getString(R.styleable.Topbar_leftText);
		leftTextSize = ta.getDimension(R.styleable.Topbar_leftTextSize, 15);
		leftIsVisable = ta.getBoolean(R.styleable.Topbar_leftIsVisable, true);
		leftIsClickable = ta.getBoolean(R.styleable.Topbar_leftIsClickable, false);
		leftWidth = ta.getDimension(R.styleable.Topbar_leftWidth, 0);
		leftHeight = ta.getDimension(R.styleable.Topbar_leftHeight, 0);
		
		rightTextColor = ta.getColor(R.styleable.Topbar_rightTextColor, 0x7f040000);//默认灰色
		rightBackground = ta.getDrawable(R.styleable.Topbar_rightBackground);
		rightText = ta.getString(R.styleable.Topbar_rightText);
		rightTextSize = ta.getDimension(R.styleable.Topbar_rightTextSize, 15);
		rightIsVisable = ta.getBoolean(R.styleable.Topbar_rightIsVisable, true);
		rightIsClickable = ta.getBoolean(R.styleable.Topbar_rightIsClickable, false);
		rightWidth = ta.getDimension(R.styleable.Topbar_rightWidth, 0);
		rightHeight = ta.getDimension(R.styleable.Topbar_rightHeight, 0);
		
		
		titleTextColor = ta.getColor(R.styleable.Topbar_titleTextColor, 0x7f040000);//默认灰色
		titleText = ta.getString(R.styleable.Topbar_titleText);
		titleTextSize = ta.getDimension(R.styleable.Topbar_titleTextSize, 22);
		
		//资源回收，避免浪费与缓存出错
		ta.recycle();
		
		leftButton = new Button(context);
		rightButton = new Button(context);
		tvTitle =  new TextView(context);;
		
		leftButton.setBackground(leftBackground);
		leftButton.setText(leftText);
		leftButton.setTextSize(leftTextSize);
		leftButton.setTextColor(leftTextColor);
		leftButton.setAllCaps(false);
		leftButton.setClickable(leftIsClickable);
		//leftButton.setWidth(dp2px(context,leftWidth));
		//leftButton.setHeight(dp2px(context,leftHeight));
		setLeftIsVisable(leftIsVisable);
		//setLeftIsClickable(leftIsClickable);
		leftButton.setPadding(0, 0, 0, 0);
		
		rightButton.setBackground(rightBackground);
		rightButton.setText(rightText);
		rightButton.setAllCaps(false);
		rightButton.setTextSize(rightTextSize);
		rightButton.setTextColor(rightTextColor);
		rightButton.setClickable(rightIsClickable);//没用？？??????????
		//rightButton.setWidth(dp2px(context,rightWidth));
		//rightButton.setHeight(dp2px(context,rightHeight));
		setRightIsVisable(rightIsVisable);
		rightButton.setPadding(0, 0, 0, 0);
		

		tvTitle.setText(titleText);
		tvTitle.setTextSize(titleTextSize);
		tvTitle.setTextColor(titleTextColor);
		
		//setBackgroundColor(0x123456);
		
		//布局
		//宽高
		if(leftWidth == 0 || leftHeight == 0){
			leftParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}else{
			leftParams = new LayoutParams(dp2px(context,leftWidth),dp2px(context,leftHeight));
		}
		
		//此处的TRUE是RelativeLayout中定义的一个常量
		leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,TRUE);
		leftParams.addRule(RelativeLayout.CENTER_VERTICAL,TRUE);
		//添加
		addView(leftButton,leftParams);
		
		
		if(rightWidth == 0 || rightHeight == 0){
			rightParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}else{
			rightParams = new LayoutParams(dp2px(context,rightWidth),dp2px(context,rightHeight));
		}
		
		rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,TRUE);
		rightParams.addRule(RelativeLayout.CENTER_VERTICAL,TRUE);
		
		addView(rightButton,rightParams);
		
		titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		titleParams.addRule(RelativeLayout.CENTER_IN_PARENT,TRUE);
		
		addView(tvTitle,titleParams);	
		
		if(leftIsClickable){
			leftButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.leftClick(v);
				}
			});
		}
		if(rightIsClickable){
			rightButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.rightClick(v);
				}
			});
		}
	
	}
	/**左边按钮可见性*/
	public void setLeftIsVisable(boolean IsVisable){
		if(IsVisable){
			leftButton.setVisibility(View.VISIBLE);
		}else{
			leftButton.setVisibility(View.GONE);
		}
	}
	/**右边按钮可见性*/
	public void setRightIsVisable(boolean IsVisable){
		if(IsVisable){
			rightButton.setVisibility(View.VISIBLE);
		}else{
			rightButton.setVisibility(View.GONE);
		}
	}
	/**设置右边文字*/
	public void setRightText(String text){
		rightButton.setText(text);
	}
	/**设置左边文字*/
	public void setLeftText(String text){
		leftButton.setText(text);
	}
	/**设置title文字*/
	public void setTitleText(String text){
		tvTitle.setText(text);
	}
	/**得到右边文字*/
	public void getRightText(String text){
		rightButton.getText();
	}
	/**得到左边文字*/
	public void getLeftText(String text){
		leftButton.getText();
	}
	/**得到title文字*/
	public void getTitleText(String text){
		tvTitle.getText();
	}
	/**设置左边按钮是否可以点击*/
	public void setLeftIsClickable(boolean IsClickable){
		leftButton.setClickable(IsClickable);
	}
	/**设置右边按钮是否可以点击*/
	public void setRightIsClickable(boolean IsClickable){
		rightButton.setClickable(IsClickable);
	}
	
	/**
     * dp转换成px
     */
    public int dp2px(Context context, float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

}
