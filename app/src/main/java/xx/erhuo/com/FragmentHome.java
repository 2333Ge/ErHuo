package xx.erhuo.com;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.LogUtils;
import com.erhuo.utils.MyApplication;
import com.erhuo.utils.NotLeakHandler;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xx.erhuo.bean.Commodity;
import xx.erhuo.bean.CommodityCommand;
import xx.erhuo.bean.Constant;
import xx.erhuo.bmob.OthersBean;
import xx.erhuo.com.adapter.AdapterHomeCommodity;
import xx.erhuo.com.util.CommadUtils;
import xx.erhuo.com.util.MImageLoader;

import static xx.erhuo.com.util.CommadUtils.setListCommandFromJson;
import static xx.erhuo.com.util.CommadUtils.setListCommandFromJson;
public class FragmentHome extends Fragment {

    private View v;
    private Banner banner;
    private RecyclerView rv_HomeCommodity;
    private SwipeRefreshLayout srl_refresh;
    private TextView tv_sell;
    private EditText et_search;
    private LinearLayout ll_search;
    private List<String>listStringTemp = Arrays.asList("","","");
    private MImageLoader mImageLoader;
    private List<OthersBean>imgUrlList;
    private AdapterHomeCommodity listCommodityAdapter;
    private List<CommodityCommand> listCommodity;
    public static FragmentHome newInstance(){//List<Commodity>

        Bundle bundle = new Bundle();
        FragmentHome f = new FragmentHome();
        return f;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgUrlList = new ArrayList<>();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home,container,false);
        banner = v.findViewById(R.id.banner_home);
        rv_HomeCommodity = v.findViewById(R.id.rv_homeCommodity);
        srl_refresh = v.findViewById(R.id.srl_refreshHomeCommodity);
        tv_sell = v.findViewById(R.id.tv_sellHome);
        ll_search = v.findViewById(R.id.ll_Search);
        et_search = v.findViewById(R.id.et_search_home);
        initView();
        return v;
    }
    private void initView() {
        //banner.setImages(listStringTemp).setImageLoader(mImageLoader).setDelayTime(3000).start();
        checkCommand();
        initCommand();
        LinearLayoutManager llm = new LinearLayoutManager(MyApplication.getContext());
        rv_HomeCommodity.setLayoutManager(llm);
        listCommodity = new ArrayList<>();
        listCommodityAdapter = new AdapterHomeCommodity(listCommodity);
        rv_HomeCommodity.setAdapter(listCommodityAdapter);

        tv_sell.setOnClickListener(sellTvClickListener);
        ll_search.setOnClickListener(BtnClick2StartSearchActivity);
        et_search.setOnClickListener(BtnClick2StartSearchActivity);

        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onRefresh() {
                new AsyncTask<Void,Void ,Boolean>(){
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        srl_refresh.setRefreshing(!aBoolean);
                    }
                }.execute();
            }
        });
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CHECK_FAIL:toastShort("首页推荐读取失败");
                    break;
                case TRANS_FAIL:toastShort("服务器返回推荐数据读取失败");
                    break;
                case TRANS_SUCCESS:
                    mImageLoader = new MImageLoader(imgUrlList);
                    banner.setImages(listStringTemp).setImageLoader(mImageLoader).setDelayTime(3000).start();
                    banner.setOnBannerListener(new OnBannerListener() {
                        @Override
                        public void OnBannerClick(int position) {
                            //偏方
                            ACommodityActivity.actionStart(getContext(),imgUrlList.get((position + 1)%imgUrlList.size()).getId());
                        }
                    });
                    break;
                case DISPLAY_COMMAND_FAIL:
                    toastShort("查看推荐失败");
                    break;
                case DISPLAY_COMMAND_SUCCESS:
                    listCommodityAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private final int CHECK_SUCCESS = 1;
    private final int CHECK_FAIL = 0;
    private final int TRANS_SUCCESS = 2;
    private final int  TRANS_FAIL = 3;
    private void checkCommand(){
        HttpUtils.sendOkHttpRequest(Constant.checkProductRecommendUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message m = handler.obtainMessage();
                m.what = CHECK_FAIL;
                handler.sendMessage(m);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                LogUtils.e("checkCommand",responseData);
                int status = transJsonToList(responseData);
                Message m = handler.obtainMessage();
                m.what = status;
                handler.sendMessage(m);
            }
        });
    }
    private static final int DISPLAY_COMMAND_FAIL = 4;
    private static final int DISPLAY_COMMAND_SUCCESS = 5;
    /**
     *
     */
    private void initCommand(){
        HttpUtils.sendOkHttpRequest(Constant.findCommandProductsUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.e("查看推荐失败",e.toString());
                Message m = handler.obtainMessage();
                m.what = CHECK_FAIL;
                handler.sendMessage(m);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String temp = response.body().string();
                LogUtils.e("推荐",temp);
                int status = setListCommandFromJson(listCommodity,temp);
                Message m = handler.obtainMessage();

                if(status == CommadUtils.TRANS_FAIL){
                    m.what = DISPLAY_COMMAND_FAIL;
                }else{
                    m.what = DISPLAY_COMMAND_SUCCESS;
                }
                handler.sendMessage(m);
            }
        });
    }
    /**
     *
     * @param jsonStr
     * @return
     */
    private int transJsonToList(String jsonStr){
        try {

            JSONArray jsonArray = new JSONArray(jsonStr);

            for(int i = 0; i<jsonArray.length(); i++){
                OthersBean othersBean = new OthersBean();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                othersBean.setImgUrl(jsonObject.getString("image"));
                othersBean.setId(jsonObject.getString("id"));
                imgUrlList.add(othersBean);
            }
            return TRANS_SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            return TRANS_FAIL;
        }

    }
    private void toastShort(String str){
        Toast.makeText(MyApplication.getContext(),str,Toast.LENGTH_SHORT).show();
    }

    View.OnClickListener sellTvClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MyApplication.getContext(),SellActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener BtnClick2StartSearchActivity = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            SearchInputActivity.actionStart(getContext());
        }
    };

}
