package xx.erhuo.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.erhuo.view.Topbar;
import com.erhuo.view.Topbar.TopbarCilckListener;

import java.util.Arrays;
import java.util.List;

import xx.erhuo.bean.Type;

public class ChoseTypeActivity extends Activity {
	private Topbar topBar_choseType;
	private ListView lv_typeAll,lv_typeNow;
	private List<String> listTypeAll,listTypeNow;
	private ArrayAdapter<String> typeAllAdapter, typeNowAdapter;
	private View viewTemp;
	private View viewTemp2;
	private int num = -1;
	
	private String type1,type2;
	
	public static final String answer_bigType = "request_bigType";
	public static final String answer_smallType = "request_smallType";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chosetype);
		init();
		
	}
	
	private void init(){
		lv_typeAll = (ListView) findViewById(R.id.lv_typeAll);
		lv_typeNow = (ListView) findViewById(R.id.lv_typeNow);
		topBar_choseType = (Topbar) findViewById(R.id.topBar_choseType);
		listTypeAll = Arrays.asList(Type.typeAll);
        //type1 = listTypeAll.get(0);
		listTypeNow = Arrays.asList(Type.type[0]);
		//ArrayAdapter<String> typeAllAdapter = new ArrayAdapter<String>(ChoseTypeActivity.this, android.R.layout.simple_list_item_1,listTypeAll);
		typeAllAdapter = new ArrayAdapter<String>(ChoseTypeActivity.this, R.layout.item_chosetype,R.id.text1,listTypeAll);
		typeNowAdapter = new ArrayAdapter<String>(ChoseTypeActivity.this, R.layout.item_chosetype,R.id.text1,listTypeNow);
		lv_typeAll.setAdapter(typeAllAdapter);

		lv_typeNow.setAdapter(typeNowAdapter);
		
		
		lv_typeAll.setOnItemClickListener(new MyTypeAllListener());
		lv_typeNow.setOnItemClickListener(new MyTypeNowListener());
		topBar_choseType.setOnTopbarCilckListener(new TopbarCilckListener(){

			@Override
			public void leftClick(View v) {
				ChoseTypeActivity.this.finish();
				
			}

			@Override
			public void rightClick(View v) {
				if(type2 == null){
					 Toast.makeText(ChoseTypeActivity.this, "请选择类型", Toast.LENGTH_SHORT).show();
					 return;
				}
				Intent intent=new Intent();
				intent.putExtra(answer_bigType,type1);
				intent.putExtra(answer_smallType,type2);
				setResult(RESULT_OK,intent);
				ChoseTypeActivity.this.finish();
				
			}
			
		});
	}
	class MyTypeAllListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//lv_typeAll.setBackgroundColor(getResources().getColor(R.color.whitesmoke));
			type1 = Type.typeAll[position];
			
			if(position == num){
				return;
			}else if(position != num){
				viewTemp2 = null;
				type2 = null;
			}
			if(viewTemp != null){
				viewTemp.setBackgroundColor(getResources().getColor(R.color.whitesmoke));
			}
			view.setBackgroundColor(getResources().getColor(R.color.lightgray));
			viewTemp = view;
			
			listTypeNow = Arrays.asList(Type.type[position]);
			typeNowAdapter = new ArrayAdapter<String>(ChoseTypeActivity.this, R.layout.item_chosetype,R.id.text1,listTypeNow);
			lv_typeNow.setAdapter(typeNowAdapter);
			lv_typeNow.setOnItemClickListener(new MyTypeNowListener());
			ChoseTypeActivity.this.num = position;
		}
	}
	
	class MyTypeNowListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    //前一个为空白时点击后面的选项
		    if(type1 == null){
                viewTemp =lv_typeAll.getChildAt(0-lv_typeAll.getFirstVisiblePosition());
                viewTemp.setBackgroundColor(getResources().getColor(R.color.lightgray));
                type1 = listTypeAll.get(0);
                num = 0;
            }

			type2 = Type.type[num][position];
			if(viewTemp2 != null){
				viewTemp2.setBackgroundColor(getResources().getColor(R.color.whitesmoke));
			}
			view.setBackgroundColor(getResources().getColor(R.color.lightgray));
			viewTemp2 = view;
		}
		
	}
}
