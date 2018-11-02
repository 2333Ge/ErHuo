package xx.erhuo.com;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import xx.erhuo.bean.CommodityCommand;
import xx.erhuo.bean.Constant;
import xx.erhuo.bean.SoldHistoryBean;
import xx.erhuo.com.adapter.CommandAdapter;
import xx.erhuo.com.adapter.OnSellAdapter;
import xx.erhuo.com.adapter.OthersEvaluateAdapter;
import xx.erhuo.com.adapter.SoldHistoryAdapter;

public class Activity_OthersMessage extends AppCompatActivity {
    private RecyclerView rv_evaluate,rv_commodityOther;
    private Toolbar t_name_other;
    private CollapsingToolbarLayout ctl_coordinatorLayout_other;
    private String uid;
    private List<CommodityCommand> onSellList;
    private CommandAdapter onSellAdapter;
    private SoldHistoryAdapter soldHistoryAdapter;
    private List<SoldHistoryBean> historyBeanList;
    private final int SEARCH_FAIL = 0;
    private final int SEARCH_SOLD_HISTORY_SUCCESS = 1;
    private final int SEARCH_ONSELL_SUCCESS =5;
    private final int TRANS_SUCCESS = 2;
    private final int TRANS_FAIL = 3;

    private final int INTERNET_ERROR = 6;
    public static void actioStart(Context context,String uid){
        Intent intent = new Intent(context,Activity_OthersMessage.class);
        intent.putExtra("uid",uid);
        context.startActivity(intent);
    }
    //他人信息: {"image":"http://39.105.0.212/shop/products/bbe9af811f7f3c2f94817e7eba86bf66.jpg",
    // "guanzhu":"23","telephone":"18860351251","birthday"
    // :"2018-8-18","background":"http://39.105.0.212/shop/products/background.jpg"}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_information);
        uid = getIntent().getStringExtra("uid");
        initView();
        initData();

    }

    private void initData() {

        HttpUtils.sendOkHttpRequest(Constant.findUserInformationUrl, "uid", uid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.e("Activity_OthersMessage","查看他人信息失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.e("他人信息",response.body().string());
            }
        });
        HttpUtils.sendOkHttpRequest(Constant.checkOnSellUrl, "uid", uid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message m = handler.obtainMessage();
                m.what = INTERNET_ERROR;
                LogUtils.e("查找失败","" + e.toString());
                handler.sendMessage(m);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                LogUtils.i("架上====",result);
                Message m = handler.obtainMessage();
                int status = setListOnSellFromJson(onSellList,result);
                if(status == TRANS_SUCCESS){
                    m.what = SEARCH_ONSELL_SUCCESS;
                }else{
                    m.what = TRANS_FAIL;
                }
                handler.sendMessage(m);
            }
        });
        HttpUtils.sendOkHttpRequest(Constant.checkSoldHistoryUrl, "uid", uid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message m = handler.obtainMessage();
                m.what = SEARCH_FAIL;
                LogUtils.e("查找交易记录失败","" + e.toString());
                handler.sendMessage(m);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                LogUtils.i("卖纪录====",result);
                Message m = handler.obtainMessage();
                int status = setListSoldHistoryFromJson(historyBeanList,result);
                if(status == TRANS_SUCCESS){
                    m.what = SEARCH_SOLD_HISTORY_SUCCESS;
                }else{
                    m.what = TRANS_FAIL;
                }
                handler.sendMessage(m);
            }
        });
    }
    @SuppressLint("HandlerLeak")
    private NotLeakHandler handler = new NotLeakHandler<Activity_OthersMessage>(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEARCH_ONSELL_SUCCESS:
                    onSellAdapter.notifyDataSetChanged();
                    break;
                case SEARCH_SOLD_HISTORY_SUCCESS:
                    soldHistoryAdapter.notifyDataSetChanged();
                    break;
                case SEARCH_FAIL:
                    toast("查询失败，请检查网络后重试");
                    break;
                case TRANS_FAIL:
                    toast("服务器返回数据解读失败");
                    break;
                case INTERNET_ERROR:
                    toast("检查网络后重试");
                    break;

            }
        }
    };

    /**
     * 初始化界面
     */
    private void initView() {
        onSellList = new ArrayList<>();
        historyBeanList = new ArrayList<>();
        t_name_other = findViewById(R.id.t_title_other);
        ctl_coordinatorLayout_other = findViewById(R.id.ctl_CollapsingToolbarLayout_other);
        rv_evaluate = findViewById(R.id.rv_evaluate);
        rv_commodityOther = findViewById(R.id.rv_commodity_other);
        //设置ToolBar
        setSupportActionBar(t_name_other);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ctl_coordinatorLayout_other.setTitle("李嘉图·M·LU");

        //初始化评价
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv_evaluate.setLayoutManager(llm);
        soldHistoryAdapter = new SoldHistoryAdapter(historyBeanList);
        rv_evaluate.setAdapter(soldHistoryAdapter);

        //初始化商品
        GridLayoutManager glm = new GridLayoutManager(this,2);
        rv_commodityOther.setLayoutManager(glm);
        onSellAdapter = new CommandAdapter(onSellList);
        rv_commodityOther.setAdapter(onSellAdapter);
    }
    /**
     * 将json串转换为历史卖list
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
                soldHistoryBean.setBuyerName(jsonObject.getString("name_buyer"));
                soldHistoryBean.setImgUrl(jsonObject.getString("image"));
                soldHistoryBean.setPid(jsonObject.getString("pid"));
                soldHistoryBean.setBuyerId(jsonObject.getString("uid_buyer"));
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
    private int setListOnSellFromJson(List<CommodityCommand> list, String jsonStr) {
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject jsonObject;
            CommodityCommand command;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                command = new CommodityCommand();
                //{"image":"http://39.105.0.212/shop/products/a1e3211120a043f78fc9310e31c814bb.jpg",
                // "pname":"¥550 低价转让联想，英特尔电脑四核4G","nowPrice":"550.0",
                // "desc":"本人开公司，因公司运营不善，倒闭，现低价转让因特尔双核四核内存4G台式电脑，成色很新，办公桌椅面议，一些基本办公PS  CAD软件足够用，保你到手插上电源，网线直接使用，同城欢迎上门看货，或者快递都可以！需要与我联系",
                // "pid":"117","date":"2018-6-26 3:13:20"}
                command.setTitle(jsonObject.getString("pname"));
                command.setImgUrl(jsonObject.getString("image"));
                command.setId(jsonObject.getString("pid"));
                command.setTime(jsonObject.getString("date"));
                command.setPrice(jsonObject.getString("nowPrice"));
                list.add(command);
            }
            return TRANS_SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.e("setListSoldHistoryFromJson", "转换失败,请检查json格式");
            return TRANS_FAIL;
        }
    }
    private void toast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
