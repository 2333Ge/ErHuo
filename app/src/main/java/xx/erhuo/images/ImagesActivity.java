package xx.erhuo.images;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.erhuo.view.Topbar;
import com.erhuo.view.Topbar.TopbarCilckListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xx.erhuo.com.R;
import xx.erhuo.com.SellActivity;

/**改写，显示images*/
public class ImagesActivity extends Activity {
	
	private FileTraversal fileTraversal;
	private GridView imgGridView;
	private GridView gv_display;
	private ImgsAdapter imgsAdapter;
	private RelativeLayout rl_full;
	//Button choise_button;
	private Button b_OK;
	private Button b_fileName;
	private Topbar topBar_chosePicture;
	//自加
	private ListView lv_listFiles;
	private HorizontalScrollView hsv_containGridView;
	private GridViewAdapter gvAdapter;
	
	private Animation anim_down_in, anim_down_out,anim_left_in,anim_right_out;
	
	private Util util;
	private int pageNum = 0;//设置各个页面CheckBox相关
	private Map<String,Integer> forderMap;
	private Map<String,Integer> positionMap;
	private boolean isClickChangedCheckBox = true;//标志位，防止被动响应chheckBox改变监听事件
	private int positionTemp;//点击photo记录photo的位置，因为如果原来photo没有被选中，返回时使用list.get会null出错
	
	//判断重复数据不能通过set,set是通过引用的地址进行区分，判断重复可以通过list里的contain方法
	private ArrayList<String> filelist;
	
	//FilelistActivity部分
	private ImgFileListAdapter listAdapter;
	private List<FileTraversal> locallist;
	
	
	private static final int requestCode_picture = 1;
	private static final int requestCode_picture2 = 2;
	public static final String Extra_imagePath = "Extra_imagePath";
	public static final String Extra_imageIsSelected = "Extra_imageIsSelected";//跳转页面时，设置对应CheckBox状态
	
	public static final String Extra_imagePathAllList = "Extra_imagePathAllList";
	public static final String Extra_imagePathList = "Extra_imagePathList";//跳转页面时，设置对应CheckBox状态
	public static final String Extra_imagePosition = "Extra_imagePosition";
	
	public static final String answer_imgPathList = "answer_imgPathList";
	public static final String answer_imgForderMap = "answer_imgForderMap";
	public static final String answer_imgPositionMp = "answer_imgPositionMp";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photogrally);
		
		init();
		}
	
	private void init(){
		imgGridView = (GridView) findViewById(R.id.gridView1);
		b_OK = (Button) findViewById(R.id.b_OK);
		b_fileName = (Button) findViewById(R.id.b_fileName);
		topBar_chosePicture = (Topbar) findViewById(R.id.topBar_chosePicture);
		gv_display = (GridView) findViewById(R.id.gv_display);
		hsv_containGridView = (HorizontalScrollView) findViewById(R.id.hsv_containGridView);
		rl_full = (RelativeLayout) findViewById(R.id.rl_full);
		
		
		anim_down_in = AnimationUtils.loadAnimation(ImagesActivity.this, R.anim.down_in);
		anim_down_out = AnimationUtils.loadAnimation(ImagesActivity.this, R.anim.down_out);
		anim_left_in = AnimationUtils.loadAnimation(ImagesActivity.this, R.anim.left_in);
		anim_right_out = AnimationUtils.loadAnimation(ImagesActivity.this, R.anim.right_out);
		
		initListView();
		initGirdView1();
		
		//初始显示第一个相册
		fileTraversal = locallist.get(0);
		imgsAdapter=new ImgsAdapter(this, fileTraversal.getFilecontent(),onItemClickListener);
		imgGridView.setAdapter(imgsAdapter);
		//左下角按钮显示
		b_fileName.setText("当前目录：" + fileTraversal.getFilename());
		b_fileName.setOnClickListener(new fileButtonClickListener());
		
		//当前目录文件列表
		filelist=new ArrayList<String>();
		forderMap = new HashMap<String, Integer>();//记录已经勾选的对应页面号
		positionMap = new HashMap<String, Integer>();//记录在那个页面的相应位置
		//设置topBar_chosePicture监听器
		topBar_chosePicture.setOnTopbarCilckListener(new myOnTopbarCilckListener());
		
		initView();
		
		rl_full.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(lv_listFiles.getVisibility() == View.VISIBLE){
					lv_listFiles.startAnimation(anim_down_out);
					lv_listFiles.setVisibility(View.GONE);
				}
				if(hsv_containGridView.getVisibility() == View.VISIBLE){
					hsv_containGridView.startAnimation(anim_right_out);
					hsv_containGridView.setVisibility(View.GONE);
				}
			}
			
		});
		
		b_OK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent();
				intent.putExtra(answer_imgPathList,filelist);
				//map不能直接putExtra，转换为ArrayList
				intent.putIntegerArrayListExtra(answer_imgForderMap,getMapValue(filelist, forderMap));
				intent.putIntegerArrayListExtra(answer_imgPositionMp,getMapValue(filelist, positionMap));
				setResult(RESULT_OK,intent);
				ImagesActivity.this.finish();
				
			}
		});
		
		
	}
	
	
	/**初始化上方二次预览界面*/
	private void initGirdView1(){
		
		int length = 100;
		 DisplayMetrics dm = new DisplayMetrics();
	     getWindowManager().getDefaultDisplay().getMetrics(dm);
	     float density = dm.density;
	     //8改为filelist长度
	     int gridviewWidth = (int) (8 * (length + 5) * density);
	     
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
	     gv_display.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
	     gv_display.setStretchMode(GridView.NO_STRETCH);//设置如何填满空余的位置，不拉伸
	     
	     
	     
	    
	}
	/**初始化目录选择listView*/
	private void initListView(){
		lv_listFiles = (ListView) findViewById(R.id.lv_listFile);
		util = new Util(this);
		locallist=util.LocalImgFileList();
		Collections.reverse(locallist);//反序，中文目录在前
		List<HashMap<String, String>> listdata=new ArrayList<HashMap<String,String>>();
		if (locallist!=null) {
			for (int i = 0; i < locallist.size(); i++) {
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("filecount", locallist.get(i).getFilecontent().size()+"张");
				map.put("imgpath", locallist.get(i).getFilecontent().get(0)==null?null:(locallist.get(i).getFilecontent().get(0)));
				map.put("filename", locallist.get(i).getFilename());
				listdata.add(map);
			}
		}
		listAdapter=new ImgFileListAdapter(this, listdata);
		lv_listFiles.setAdapter(listAdapter);
		lv_listFiles.setOnItemClickListener(new listItemClickListener());
	}
	/**初始化二次次进入时各个界面的显示状态*/
	private void initView(){
		boolean isInitView = true;
		///~注意小心界面初始化还没完成就设置状态会崩溃
		ArrayList<String> filelistTemp = getIntent().getStringArrayListExtra(SellActivity.Extra_pathList);
		if(filelistTemp != null){
			//Toast.makeText(ImagesActivity.this,"filelistTemp != null", Toast.LENGTH_SHORT).show();
			filelist.addAll(filelistTemp);
			ArrayList<Integer> listForderTemp = getIntent().getIntegerArrayListExtra(SellActivity.Extra_forderListTemp);
			forderMap = setMapValue(filelist, listForderTemp);
			ArrayList<Integer> listpositionTemp = getIntent().getIntegerArrayListExtra(SellActivity.Extra_positionListTemp);
			positionMap = setMapValue(filelist, listpositionTemp);
			imgGridView.getViewTreeObserver().addOnGlobalLayoutListener(new myOnGlobalLayoutListener(isInitView));
			
			gvAdapter = new GridViewAdapter(ImagesActivity.this, filelist,mySelectImageItemClickListener);
	 		 gv_display.setAdapter(gvAdapter);
		}
	}
	/**将map的value按序取出来，便于存取
	 * @param listKey key值的list
	 * @param listMap 需要转换的对象
	 * */
	private ArrayList<Integer> getMapValue(List<String> listKey, Map<String,Integer> listMap){
		ArrayList<Integer> listValue = new ArrayList<Integer>();
		for(String key:listKey){
			listValue.add(listMap.get(key));
		}
		return listValue;
	}
	/**将list的key,value按序取出来转换为map,便于使用
	 * @param listKey key值的list
	 * @param listValue value值的list
	 * */
	private Map<String,Integer> setMapValue(ArrayList<String> listKey, ArrayList<Integer> listValue){
		HashMap<String,Integer> mapValue = new HashMap<String, Integer>();
		for(int i=0 ; i<listKey.size(); i++){
			mapValue.put(listKey.get(i), listValue.get(i));
		}
		return mapValue;
	}

	/**自写，后面图片点击监听器*/
	ImgsAdapter.OnitemClickListener onItemClickListener = new ImgsAdapter.OnitemClickListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
			
			if(isClickChangedCheckBox){
				///Toast.makeText(ImagesActivity.this, "onCheckedChanged", Toast.LENGTH_SHORT).show();
				Log.d("onCheckedChanged", "position" + position);
				String filapath=fileTraversal.getFilecontent().get(position);
				
				if (isChecked) {
					if(filelist.size() >= 8){
						buttonView.setChecked(false);
					}else{
						if(!filelist.contains(filapath)){
	                		filelist.add(filapath);
	                		forderMap.put(filapath, pageNum);
	                		positionMap.put(filapath, position);
	                		
	                		gvAdapter = new GridViewAdapter(ImagesActivity.this, filelist,mySelectImageItemClickListener);
	                		//每次都要重新设置监听器？
	                		gv_display.setAdapter(gvAdapter);
	                		
	                	}
					}
				}else{
					gvAdapter = new GridViewAdapter(ImagesActivity.this, filelist,mySelectImageItemClickListener);

					gv_display.setAdapter(gvAdapter);
					filelist.remove(filapath);
					forderMap.remove(filapath);
	        		positionMap.remove(filapath);
				}
				topBar_chosePicture.setRightText(filelist.size() + "/8");
			}
			isClickChangedCheckBox = true;
				
		}

		@Override
		public void onPohtoClicked(View v, int position) {
			
			startActivityDisplay2(position);
			
		}
	};
	/**无滑动浏览*/
	public void startActivityDisplay1(int position){
		String filapath=fileTraversal.getFilecontent().get(position);
		Intent intent = new Intent(ImagesActivity.this,ImgDisplayActivity.class);
		intent.putExtra(Extra_imagePath, filapath);
		intent.putExtra(Extra_imageIsSelected, imgsAdapter.isChecked(position));
		positionTemp = position;
		startActivityForResult(intent, requestCode_picture);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	/**有滑动浏览*/
	public void startActivityDisplay2(int position){
		fileTraversal.getFilecontent().get(position);
		Intent intent = new Intent(ImagesActivity.this,ImgDisplayActivity2.class);
		intent.putExtra(Extra_imagePathList, filelist);
		intent.putStringArrayListExtra(Extra_imagePathAllList,(ArrayList)fileTraversal.getFilecontent());
		intent.putExtra(Extra_imagePosition, position);
		
		
		
		startActivityForResult(intent, requestCode_picture2);
	}
	
	/**此方法是接收来自其他activity中setResult()传回来的resultCode和intent*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == requestCode_picture && resultCode==RESULT_OK){
        	
            if (data!=null){
            	//Toast.makeText(ImagesActivity.this, "requestCode_picture", Toast.LENGTH_SHORT).show();
                boolean isSelected = data.getBooleanExtra(ImgDisplayActivity.answer_isSelected, false);
                String imgPath = data.getStringExtra(ImgDisplayActivity.answer_imgPath);
               if(isSelected ){
            	   if(!filelist.contains(imgPath)){
            		 //选中且原本不包含该数据则添加,这表明这是本页面数据
       				forderMap.put(imgPath,pageNum);
           			positionMap.put(imgPath,positionTemp);
           			filelist.add(imgPath);
           			isClickChangedCheckBox = false;
           			setCheckBoxstate(pageNum,positionTemp, true);
            	   }
    				
    			}else{
    				//没选中，可不用加判断
    				isClickChangedCheckBox = false;
    				setCheckBoxstate(forderMap.get(imgPath),positionMap.get(imgPath), false);
    				forderMap.remove(imgPath);
        			positionMap.remove(imgPath);
        			filelist.remove(imgPath);
        			
    			}
    			   			
    			//传递的是一个引用，无需在adapter中再remove一次
    			//gvAdapter.notifyDataSetChanged();//这个函数默认删除最后一个？？？？？？？？？？？？？？？弹出地址正确，显示不正确,所以采用下面的方法刷新
    			gvAdapter = new GridViewAdapter(ImagesActivity.this, filelist,mySelectImageItemClickListener);
        		
        		gv_display.setAdapter(gvAdapter);
    			
    			topBar_chosePicture.setRightText(filelist.size() + "/8");
              }
         }else if ( requestCode == requestCode_picture2 && resultCode==RESULT_OK){
        	 
             if (data!=null){
            	 ArrayList<String> listTemp = data.getStringArrayListExtra(ImgDisplayActivity2.answer_imgPathList);
				 int listSize = listTemp.size();
            	 while(listTemp.size() > 8){
            	 	//清楚后下一个需要清除的还是序号为8的
					 listTemp.remove(8);
					 Toast.makeText(ImagesActivity.this,"所选择的的图片超过上限", Toast.LENGTH_SHORT).show();
				 }
            	 //先清空当前页面状态
            	 ArrayList<String> listTemp2 = new ArrayList<String>();
            	 for(String f:filelist){
            		 //迭代中，此时还不能remove()
            		 int pageNumTemp = forderMap.get(f);
            		 int positionTemp0 = positionMap.get(f);
            		 isClickChangedCheckBox = false;
            		 setCheckBoxstate(pageNumTemp,positionTemp0, false);
            		 if( pageNumTemp == pageNum){
            			listTemp2.add(f);
            		 }
            	 }
            	 for(String f:listTemp2){
            		 //remove当前页面cb状态
            		 forderMap.remove(f);
          			positionMap.remove(f);
          			filelist.remove(f);
            	 }
            	 
            	 for(String path:listTemp){
            		 if(!filelist.contains(path)){
                		 //选中且原本不包含该数据则添加,这表明这是本页面数据
           				forderMap.put(path,pageNum);
           				int positionTemp0 = (int)fileTraversal.getFilecontent().indexOf(path);
               			positionMap.put(path,positionTemp0);
               			
               			filelist.add(path);
               			isClickChangedCheckBox = false;
               			setCheckBoxstate(pageNum,positionTemp0, true);
               			//Toast.makeText(ImagesActivity.this, pageNum + "\n"+ positionTemp0 , Toast.LENGTH_SHORT).show();
            		    
                	   }
            	}
            	 
                gvAdapter = new GridViewAdapter(ImagesActivity.this, filelist,mySelectImageItemClickListener);
         		
         		gv_display.setAdapter(gvAdapter);
     			
     			topBar_chosePicture.setRightText(filelist.size() + "/8");
               }
         }
    }
    /**Topbar点击事件*/
    class myOnTopbarCilckListener implements TopbarCilckListener {
    	/**结束并回传数据*/
		@Override
		public void leftClick(View v) {
			ImagesActivity.this.finish();
			
		}

		@Override
		public void rightClick(View v) {
			
			if(hsv_containGridView.getVisibility() == View.GONE){
				hsv_containGridView.startAnimation(anim_left_in);
				
				hsv_containGridView.bringToFront();
				hsv_containGridView.setVisibility(View.VISIBLE);
				if(lv_listFiles.getVisibility() == View.VISIBLE){
					lv_listFiles.startAnimation(anim_down_out);
					lv_listFiles.setVisibility(View.GONE);
				}
			}else{
				hsv_containGridView.startAnimation(anim_right_out);
				hsv_containGridView.setVisibility(View.GONE);
			}
			
		}
    	
    }
    /**因为切换图片选择界面时CheckBox状态会重置，此函数便用于设置对应页面的对应CheckBox状态*/
	private void setCheckBoxstate(int pageNum,int imgPosition,boolean state){
		//是相同页面时才设置状态
		if(pageNum == this.pageNum ){
			//Toast.makeText(ImagesActivity.this,"this.pageNum" + this.pageNum +"pageNum:" +pageNum + "imgPosition: " +imgPosition+ " ", Toast.LENGTH_LONG).show();
			Log.d("setCheckBoxstate","this.pageNum" + this.pageNum +"pageNum:" +pageNum + "imgPosition: " +imgPosition+ " ");
			
			imgsAdapter.setCheckeBoxState(imgPosition, state);
		}
	}
	
	
	/**ListView点击监听*/
	class listItemClickListener implements OnItemClickListener {

		//点击以后，后台View会发生变化
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			fileTraversal = locallist.get(position);
			pageNum = position;
			boolean islvCllicked = true;
			//Toast.makeText(ImagesActivity.this, pageNum + "pageNum", 0).show();
			//imgsAdapter=new ImgsAdapter(ImagesActivity.this, fileTraversal.getFilecontent(),onItemClickClass);
			imgsAdapter=new ImgsAdapter(ImagesActivity.this, fileTraversal.getFilecontent(),onItemClickListener);
			imgGridView.setAdapter(imgsAdapter);
			b_fileName.setText("当前目录:" + fileTraversal.getFilename());
			
			//正常的imgGridView加载完成时回调，会一直存在
			imgGridView.getViewTreeObserver().addOnGlobalLayoutListener(new myOnGlobalLayoutListener(islvCllicked));
			
		}
		
		}

	/**重写布局加载完成反馈函数，设置为true时才会响应*/
	class myOnGlobalLayoutListener implements OnGlobalLayoutListener {
		boolean islvCllicked = false;
		public myOnGlobalLayoutListener(boolean islvCllicked){
			
			this.islvCllicked = islvCllicked;
			
		}
		@Override
		public void onGlobalLayout() {
			if(!islvCllicked){
				return;
			}else{
				for(int i=0; i < filelist.size(); i++){
					
					String imgPath = filelist.get(i);
					setCheckBoxstate(forderMap.get(imgPath), positionMap.get(imgPath), true);
				}
				islvCllicked = false;
			}
			
		}
	}
	/**左下角按钮监听器*/
	class fileButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			
			if(lv_listFiles.getVisibility() == View.VISIBLE){
				
				lv_listFiles.startAnimation(anim_down_out);
				lv_listFiles.setVisibility(View.GONE);
				
			
			}else{
				
				lv_listFiles.startAnimation(anim_down_in);
				lv_listFiles.setVisibility(View.VISIBLE);
				
				if(hsv_containGridView.getVisibility() == View.VISIBLE){
					hsv_containGridView.startAnimation(anim_right_out);
					hsv_containGridView.setVisibility(View.GONE);
				}
			}
	
		}
	}
	/**二次确认GridView item点击监听器*/
	GridViewAdapter.SelectImageItemClickListener mySelectImageItemClickListener = new GridViewAdapter.SelectImageItemClickListener(){

		@Override
		public void DeleteClick(int position,List<String> ListImagePath) {
			String imgPath = filelist.get(position);
			//Toast.makeText(ImagesActivity.this, "imgPath:" + imgPath, Toast.LENGTH_SHORT).show();
			//先设置状态再移除数据,状态变化会触发对应函数
			isClickChangedCheckBox = false;
			setCheckBoxstate(forderMap.get(imgPath), positionMap.get(imgPath), false);
			
			forderMap.remove(imgPath);
			positionMap.remove(imgPath);
			filelist.remove(imgPath);
			//不在同一页面就单独处理
			
			//传递的是一个引用，无需在adapter中再remove一次
			//gvAdapter.notifyDataSetChanged();//这个函数默认删除最后一个？？？？？？？？？？？？？？？弹出地址正确，显示不正确,所以采用下面的方法刷新
			gvAdapter = new GridViewAdapter(ImagesActivity.this, filelist,mySelectImageItemClickListener);
    		
    		gv_display.setAdapter(gvAdapter);
			
			topBar_chosePicture.setRightText(filelist.size() + "/8");
			
		}
		@Override
		public void SelectClick(int position) {
			//只能预览，不能勾选，因为后台的后面GridView可能已经发生变化
			String filepath=filelist.get(position);
			isClickChangedCheckBox = false;//返回时，可能会触发CheckBox监听事件
			
			Intent intent = new Intent(ImagesActivity.this,ImgDisplayActivity.class);
			intent.putExtra(Extra_imagePath, filepath);
			intent.putExtra(Extra_imageIsSelected, true);
			startActivityForResult(intent, requestCode_picture);
			
		}
		
	};
	
	
}
