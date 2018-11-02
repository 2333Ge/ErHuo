package xx.erhuo.com;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import xx.erhuo.com.adapter.SearchHistoryAdapter;

public class SearchInputActivity extends AppCompatActivity {

    public static void actionStart(Context context){
        Intent intent = new Intent(context,SearchInputActivity.class);
        context.startActivity(intent);
    }
    private EditText et_input;
    private RecyclerView rv_history;
    private SharedPreferences spHistory;
    private ImageView iv_back;
    private final String HISTORY_KEY = "HISTORY_KEY";
    private TextView tv_search;
    private Set<String> historySet;
    private List<String> historyList;
    private SearchHistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_input);
        initView();
        initData();
    }



    private void initView() {
        et_input = findViewById(R.id.et_input_search);
        tv_search = findViewById(R.id.tv_clickToSearch);
        rv_history = findViewById(R.id.rv_searchHistory);
        iv_back = findViewById(R.id.iv_back_search);
    }
    private void initData() {
        tv_search.setOnClickListener(Click2StartSearch);
        rv_history.setLayoutManager(new LinearLayoutManager(this));
        spHistory = getPreferences(MODE_PRIVATE);

        historySet = new LinkedHashSet<>(spHistory.getStringSet(HISTORY_KEY,new LinkedHashSet<String>(){}));
        historyList = new ArrayList<>();

        historyList = new ArrayList<>(historySet);
        historyAdapter = new SearchHistoryAdapter(historyList);
        historyAdapter.setHistoryClickListener(historyItemClick);
        rv_history.setAdapter(historyAdapter);

        iv_back.setOnClickListener(finishClick);
    }

    private View.OnClickListener Click2StartSearch  = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            String inputStr = et_input.getText().toString();
            //先判断是否为空字符
            if( !historySet.contains(inputStr)){
                historyList.add(inputStr);
                historySet.add(inputStr);
                SharedPreferences.Editor editor = spHistory.edit();

                editor.putStringSet(HISTORY_KEY,historySet);
                editor.apply();
                if (historyList.size() > 10){
                    //超出上限移除最后一个,即是第一个加进去的
                    String text = historyList.get(0);
                    historySet.remove(text);
                    historyList.remove(0);
                    //布局变化
                }
            }else{
                historyList.remove(inputStr);
                historyList.add(inputStr);//改一下顺序
            }
            SearchActivity.actionStart(SearchInputActivity.this,inputStr);
        }
    };
    View.OnClickListener finishClick = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            finish();
        }
    };
    SearchHistoryAdapter.HistoryClickListener historyItemClick = new  SearchHistoryAdapter.HistoryClickListener() {
        @Override
        public void historyItemClick(View v, String content, int position) {
            et_input.setText(content);
        }

        @Override
        public void historyItemLongClick(View v, String content, int position) {
            deleteDialog(position);
        }

        @Override
        public void deleteAll(View v) {
            deleteAllDialog();
        }
    };
    /**
     * 删除某项的对话框
     */
    private void deleteDialog(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否删除该条记录?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItem(position);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 删除全部历史的对话框
     */
    private void deleteAllDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否删全部搜索记录?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItem(-1);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 删除某个项该做的一系列操作，参数为-1时表示删除全部
     * @param position 位置
     */
    public void deleteItem(int position){

        SharedPreferences.Editor editor = spHistory.edit();
        if(position == -1){
            historyList.clear();
            historySet.clear();
        }else{
            String text = historyList.get(position);
            historySet.remove(text);
            historyList.remove(text);
        }
        editor.putStringSet(HISTORY_KEY,historySet);
        editor.apply();
        historyAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //重新回到这个界面时再改变界面，较和谐
        historyAdapter.notifyDataSetChanged();
    }


}
