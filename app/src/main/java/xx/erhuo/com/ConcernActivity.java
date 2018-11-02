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
import android.view.View;
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
import xx.erhuo.bmob.BombChatActivity;
import xx.erhuo.bmob.ListOthersAdapter;
import xx.erhuo.bmob.OthersBean;
import xx.erhuo.com.adapter.SoldHistoryAdapter;

public class ConcernActivity extends AppCompatActivity {

    public static void actionStart(Context context,String title,String uid){
        Intent intent = new Intent(context,ConcernActivity.class);
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
    private List<OthersBean> othersBeanList;
    private ListOthersAdapter othersAdapter;

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
        othersBeanList = new ArrayList<>();
        othersAdapter = new ListOthersAdapter(othersBeanList);
        othersAdapter.setOnClickListener(new ListOthersAdapter.OnClickListener() {
            @Override
            public void nameOnClick(View v, int position) {
                toast("聊天");
            }

            @Override
            public void imgOnClick(View v, int position) {
                Activity_OthersMessage.actioStart(ConcernActivity.this,uid);
            }
        });
        rv_sold.setAdapter(othersAdapter);

        HttpUtils.sendOkHttpRequest(Constant.checkConcernUrl, "uid", uid, new Callback() {
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
                LogUtils.i("关注====",result);
                Message m = handler.obtainMessage();
                int status = setListOtherFromJson(othersBeanList,result);
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
    private NotLeakHandler handler = new NotLeakHandler<ConcernActivity>(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEARCH_SUCCESS:
                    othersAdapter.notifyDataSetChanged();
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
    private int setListOtherFromJson(List<OthersBean> list, String jsonStr){
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject jsonObject;
            OthersBean othersBean;
            for(int i = 0; i< jsonArray.length() ; i++){
                jsonObject  = jsonArray.getJSONObject(i);
                othersBean = new OthersBean();
                //{"uid":"75","username":"红红火火",
                // "sex":"female",
                // "image":"http://39.105.0.212/shop/products/a85ceef1bc69e78f29709fcae9b838e2.jpg"
                othersBean.setId(jsonObject.getString("uid"));
                othersBean.setName(jsonObject.getString("username"));
                othersBean.setImgUrl(jsonObject.getString("image"));
                list.add(othersBean);
            }
            return TRANS_SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.e("setListOtherFromJson","转换失败,请检查json格式");
            return TRANS_FAIL;
        }

    }

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
