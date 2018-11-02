package xx.erhuo.com;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.LogUtils;
import com.erhuo.utils.MyApplication;
import com.erhuo.utils.NotLeakHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.b.I;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xx.erhuo.bean.Constant;
import xx.erhuo.bean.SoldHistoryBean;
import xx.erhuo.bean.User;
import xx.erhuo.bmob.MessageDBUtils;
import xx.erhuo.com.adapter.BoughtHistoryAdapter;
import xx.erhuo.com.adapter.OnSellAdapter;

public class OnSellActivity extends AppCompatActivity {

    public static void actionStart(Context context,String title,String uid){
        Intent intent = new Intent(context,OnSellActivity.class);
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
    private List<SoldHistoryBean> onSellList;//此处是查看架上，和买到的东西实体类类似，公用，后期改
    private OnSellAdapter onSellAdapter;

    private final int SEARCH_FAIL = 0;
    private final int SEARCH_SUCCESS = 1;
    private final int TRANS_SUCCESS = 2;
    private final int TRANS_FAIL = 3;
    private final int DELETE_SECCESS = 4;
    private final int DELETE_FAIL = 5;
    private final int INTERNET_ERROR = 6;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold);
        user = MyApplication.getUser();
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
        onSellList = new ArrayList<>();
        onSellAdapter = new OnSellAdapter(onSellList);
        onSellAdapter.setOnItemClickListener(rvItemClickListener);
        rv_sold.setAdapter(onSellAdapter);

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
                int status = setListSoldHistoryFromJson(onSellList,result);
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
    private NotLeakHandler handler = new NotLeakHandler<OnSellActivity>(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEARCH_SUCCESS:
                    onSellAdapter.notifyDataSetChanged();
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
                case DELETE_SECCESS:
                    toast("下架成功");
                    onSellList.remove((int)msg.obj);
                    onSellAdapter.notifyDataSetChanged();
                    break;
                case DELETE_FAIL:
                    toast("下架失败");
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
                //{"image":"http://39.105.0.212/shop/products/a1e3211120a043f78fc9310e31c814bb.jpg",
                // "pname":"¥550 低价转让联想，英特尔电脑四核4G","nowPrice":"550.0",
                // "desc":"本人开公司，因公司运营不善，倒闭，现低价转让因特尔双核四核内存4G台式电脑，成色很新，办公桌椅面议，一些基本办公PS  CAD软件足够用，保你到手插上电源，网线直接使用，同城欢迎上门看货，或者快递都可以！需要与我联系",
                // "pid":"117","date":"2018-6-26 3:13:20"}
                soldHistoryBean.setTitle(jsonObject.getString("pname"));
                soldHistoryBean.setImgUrl(jsonObject.getString("image"));
                soldHistoryBean.setPid(jsonObject.getString("pid"));
                soldHistoryBean.setDate(jsonObject.getString("date"));
                soldHistoryBean.setPrice(jsonObject.getString("nowPrice"));
                list.add(soldHistoryBean);
            }
            return TRANS_SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.e("setListSoldHistoryFromJson","转换失败,请检查json格式");
            return TRANS_FAIL;
        }

    }
    OnSellAdapter.OnItemClickListener rvItemClickListener = new OnSellAdapter.OnItemClickListener() {

        @Override
        public void otherClick(int position) {
            ACommodityActivity.actionStart(OnSellActivity.this,onSellList.get(position).getPid());
        }

        @Override
        public void otherLongClick(int position) {
            if(user.getUserId().equals(uid)){
                deleteDialog(position);
            }

        }
    };

    /**
     * 删除某项的对话框
     */
    private void deleteDialog(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否下架该物品?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteRequest(position);

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
    private void deleteRequest(final int position){
        HttpUtils.sendOkHttpRequest(Constant.disOnSellUrl, "idss", onSellList.get(position).getPid(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message m = handler.obtainMessage();
                m.what = INTERNET_ERROR;
                handler.sendMessage(m);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                Message m = handler.obtainMessage();
                if(responseStr.equals("success")){
                    m.what = DELETE_SECCESS;
                    m.obj = position;
                }else{
                    m.what = DELETE_FAIL;
                }
                handler.sendMessage(m);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(SEARCH_FAIL);
        handler.removeMessages(TRANS_FAIL);
        handler.removeMessages(SEARCH_SUCCESS);
        handler.removeMessages(DELETE_FAIL);
        handler.removeMessages(DELETE_SECCESS);
        handler.removeMessages(INTERNET_ERROR);
    }
    private void toast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();

    }
}

