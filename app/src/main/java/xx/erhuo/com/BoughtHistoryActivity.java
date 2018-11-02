package xx.erhuo.com;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.LogUtils;
import com.erhuo.utils.NotLeakHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xx.erhuo.bean.Constant;
import xx.erhuo.bean.SoldHistoryBean;
import xx.erhuo.bean.User;
import xx.erhuo.com.adapter.BoughtHistoryAdapter;
import xx.erhuo.com.adapter.SoldHistoryAdapter;

public class BoughtHistoryActivity extends AppCompatActivity {

    public static void actionStart(Context context,String title,String uid){
        Intent intent = new Intent(context,BoughtHistoryActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("uid",uid);
        context.startActivity(intent);
    }
    private TextView tv_title;
    private RecyclerView rv_sold;
    private Toolbar t_title;

    private String title;
    private String uid;
    private User user;
    private List<SoldHistoryBean> historyBeanList;
    private BoughtHistoryAdapter boughtAdapter;

    private final int SEARCH_FAIL = 0;
    private final int SEARCH_SUCCESS = 1;
    private final int TRANS_SUCCESS = 2;
    private final int TRANS_FAIL = 3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold);
        initView();
        initData();
    }



    private void initView() {
        tv_title = findViewById(R.id.tv_sold_toolbar);
        rv_sold = findViewById(R.id.rv_sold);
        t_title = findViewById(R.id.toolbar_sold);
        setSupportActionBar(t_title);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        t_title.setNavigationIcon(R.drawable.back32);

    }

    private void initData() {
        Intent temp = getIntent();
        uid = temp.getStringExtra("uid");
        title = temp.getStringExtra("title");
        tv_title.setText(title);
        historyBeanList = new ArrayList<>();
        boughtAdapter = new BoughtHistoryAdapter(historyBeanList);
        boughtAdapter.setOnItemClickListener(rvItemClickListener);
        rv_sold.setAdapter(boughtAdapter);

        HttpUtils.sendOkHttpRequest(Constant.checkBoughtHistoryUrl, "uid", uid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message m = handler.obtainMessage();
                m.what = SEARCH_FAIL;
                LogUtils.e("查找失败","" + e.toString());
                handler.sendMessage(m);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                LogUtils.i("买纪录====",result);
                Message m = handler.obtainMessage();
                int status = setListSoldHistoryFromJson(historyBeanList,result);
                if(status == TRANS_SUCCESS){
                    m.what = SEARCH_SUCCESS;
                }else{
                    m.what = TRANS_FAIL;
                }
                handler.sendMessage(m);
            }
        });
    }
    @SuppressLint("HandlerLeak")
    private NotLeakHandler handler = new NotLeakHandler<BoughtHistoryActivity>(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEARCH_SUCCESS:
                    boughtAdapter.notifyDataSetChanged();
                    break;
                case SEARCH_FAIL:
                    toast("查询失败，请检查网络后重试");
                    break;
                case TRANS_FAIL:
                    toast("服务器返回数据解读失败");
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 将json串转换为实体list
     * @param list
     * @param jsonStr
     * @return
     */
    private int setListSoldHistoryFromJson(List<SoldHistoryBean> list, String jsonStr){
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject jsonObject;
            SoldHistoryBean soldHistoryBean;
            for(int i = 0; i< jsonArray.length() ; i++){
                jsonObject  = jsonArray.getJSONObject(i);
                soldHistoryBean = new SoldHistoryBean();
                //[{"product_name":"果6s16g全网通，自己用的手机屏幕没有划痕，后面有痕迹自己看图",
                // "pid":"143","image":"http://39.105.0.212/shop/products/cb36ff75e30c46f29ef6be7e001b6114.jpg"
                // ,"price":"800.0","liuyan":"null","uid_buyer":"77","name_buyer":"冰雪灵之心",
                // "payDate":"2018-6-26 3:59:55"}
                soldHistoryBean.setTitle(jsonObject.getString("product_name"));
                soldHistoryBean.setBuyerName(jsonObject.getString("name_seller"));
                soldHistoryBean.setImgUrl(jsonObject.getString("image"));
                soldHistoryBean.setPid(jsonObject.getString("pid"));
                soldHistoryBean.setBuyerId(jsonObject.getString("uid_seller"));
                soldHistoryBean.setLeaveMessage(jsonObject.getString("liuyan"));
                soldHistoryBean.setDate(jsonObject.getString("payDate"));
                soldHistoryBean.setPrice(jsonObject.getString("price"));
                list.add(soldHistoryBean);
            }
            return TRANS_SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.e("setListSoldHistoryFromJson","转换失败,请检查json格式");
            return TRANS_FAIL;
        }

    }
    BoughtHistoryAdapter.OnItemClickListener rvItemClickListener = new BoughtHistoryAdapter.OnItemClickListener() {


        @Override
        public void defaultClick(int position) {
            toast("添加评价或查看评价");
        }

        @Override
        public void otherClick(int position) {
            ACommodityActivity.actionStart(BoughtHistoryActivity.this,historyBeanList.get(position).getPid());
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(SEARCH_FAIL);
        handler.removeMessages(TRANS_FAIL);
        handler.removeMessages(SEARCH_SUCCESS);
    }
    private void toast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();

    }
}
