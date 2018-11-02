package xx.erhuo.com;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xx.erhuo.bean.Commodity;
import xx.erhuo.com.adapter.RvSearchAdapter;

public class SearchActivity extends AppCompatActivity {

    /*
    * 网络问题奔溃
    * */

    private final String SEARCH_KEY = "SEARCH_KEY";

    public static void actionStart(Context context,String searchKey){
        Intent intent = new Intent(context,SearchActivity.class);
        intent.putExtra("SEARCH_KEY",searchKey);
        context.startActivity(intent);
    }

    private TextView tv_searchFail;
    private EditText et_search;
    private RecyclerView rv_commoditys;
    private List<Commodity> commodityList;
    private RvSearchAdapter searchAdapter;
    private ImageView iv_back;
    private LinearLayout ll_loadOK;
    private ProgressBar pb_loadIng;
    private TextView tv_loadFail;

    private static  final int SEARCH_SUCCESS = 1,SEARCH_FAIL = 0,SEARCH_NONE_RESULT = 2;
    private static final String searchUrl = "http://39.105.0.212/android/product_findByKeyword";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_commodity);
        initView();
    }

    private void initView() {
        tv_searchFail = findViewById(R.id.tv_searchFail);
        et_search = findViewById(R.id.et_input_search);
        rv_commoditys = findViewById(R.id.rv_commodity_search);
        iv_back = findViewById(R.id.iv_back_commodity);
        ll_loadOK = findViewById(R.id.ll_searchResult_loadOK);
        pb_loadIng = findViewById(R.id.pb_searchResult_loadIng);
        tv_loadFail = findViewById(R.id.tv_searchResult_loadFail);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv_commoditys.setLayoutManager(llm);
        et_search.setOnClickListener(searchEtClick);
        String searchKey = getIntent().getStringExtra(SEARCH_KEY);
        et_search.setText(searchKey);
        iv_back.setOnClickListener(finishClick);


        HttpUtils.sendOkHttpRequest(searchUrl, new String[]{"keyword"}, new String[]{searchKey}, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message m = handler.obtainMessage();
                m.what = SEARCH_FAIL;
                handler.sendMessage(m);
                LogUtils.e("onFailure","查询失败" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                LogUtils.i("查询成功",responseData);
                commodityList = getCommodityListFromJsonStr(responseData);

                Message m = handler.obtainMessage();
                if (commodityList != null){
                    m.what = SEARCH_SUCCESS;
                }else{
                    m.what = SEARCH_NONE_RESULT;
                }
                handler.sendMessage(m);

            }
        });
    }

    View.OnClickListener searchEtClick = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            SearchInputActivity.actionStart(SearchActivity.this);
        }
    };
    private void toastShort(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEARCH_SUCCESS:
                    searchAdapter = new RvSearchAdapter(commodityList);
                    rv_commoditys.setAdapter(searchAdapter);
                    searchAdapter.setCommodityClickListener(mCommodityClickListener);
                    LogUtils.d("commodityList.size",commodityList.size()+"");
                    ll_loadOK.setVisibility(View.VISIBLE);
                    pb_loadIng.setVisibility(View.GONE);
                    break;
                case SEARCH_NONE_RESULT:
                    tv_searchFail.setVisibility(View.VISIBLE);
                    break;
                case SEARCH_FAIL:
                    pb_loadIng.setVisibility(View.GONE);
                    tv_loadFail.setVisibility(View.VISIBLE);
                    //toastShort("查找失败");
                    break;
                default:break;
            }
        }
    };
    /**
     * 从json string 中获得 List &lt;Commodity&gt;
     * @param str
     * @return
     * @throws JSONException
     */
    private List<Commodity> getCommodityListFromJsonStr(String str) {
        //{"image":"39.105.0.212/shop/products/b0dd03ce731d470982a0f23ac2a931d5.jpg",
        // "pname":"联想手机一台 品相好，二手机，正常使用，不喜欢就退，退货来回买家承担",
        // "oldPrice":"1000.0",
        // "nowPrice":"380.0"}
        JSONObject jsonObject;
        Commodity commodity;
        List imgUrlList;
        List<Commodity> listCommodity = new ArrayList<Commodity>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(str);

        for(int i =0; i < jsonArray.length();i++){
            jsonObject = jsonArray.getJSONObject(i);
            commodity = new Commodity();
            commodity.setTitle(jsonObject.getString("pname"));
            imgUrlList = new ArrayList<String>();
            imgUrlList.add(jsonObject.getString("image"));//此处应该还有个循环获取所有图片地址
            commodity.setImgUrlList(imgUrlList);
            commodity.setDate(jsonObject.getString("date"));
            commodity.setPrice((float) jsonObject.getDouble("nowPrice"));
            commodity.setId(jsonObject.getString("pid"));
            commodity.setPriceOriginal((float) jsonObject.getDouble("oldPrice"));
            listCommodity.add(commodity);
        }
        return listCommodity;
        } catch (JSONException e) {
            return null;
        }
    }

    View.OnClickListener finishClick = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            finish();
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(SEARCH_SUCCESS);
        handler.removeMessages(SEARCH_FAIL);
        handler.removeMessages(SEARCH_NONE_RESULT);

    }
    RvSearchAdapter.CommodityClickListener mCommodityClickListener = new RvSearchAdapter.CommodityClickListener() {
        @Override
        public void onClick(View view, int position) {
            ACommodityActivity.actionStart(SearchActivity.this,commodityList.get(position).getId());
        }
    };
}
