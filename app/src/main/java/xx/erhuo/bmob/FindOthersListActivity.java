package xx.erhuo.bmob;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.LogUtils;
import com.erhuo.utils.MyApplication;
import com.erhuo.utils.NotLeakHandler;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.exception.BmobException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xx.erhuo.bean.Constant;
import xx.erhuo.bean.User;
import xx.erhuo.com.Activity_OthersMessage;
import xx.erhuo.com.R;

public class FindOthersListActivity extends AppCompatActivity {

    private RecyclerView rv_listOthers;
    private ImageView iv_back;
    private EditText et_name;
    private ListOthersAdapter adapter;
    private List<OthersBean> listOthers;
    private List<String> keyList;
    private String nameKey;
    private User user;
    public static void actionStart(Context context,String nameKey){
        Intent intent = new Intent(context,FindOthersListActivity.class);
        intent.putExtra("nameKey",nameKey);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_others);
        user = MyApplication.getUser();
        initView();
        initData();
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back_search_others);
        et_name = findViewById(R.id.et_input_name);
        rv_listOthers = findViewById(R.id.rv_findOthers);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        et_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData() {
        nameKey = getIntent().getStringExtra("nameKey");
        keyList = Arrays.asList("uid","image","nicheng");
        et_name.setText(nameKey);
        listOthers = new ArrayList<>();
        adapter = new ListOthersAdapter(listOthers);
        adapter.setOnClickListener(othersOnClick);
        rv_listOthers.setAdapter(adapter);
        searchOthers(nameKey);
    }

    private static final int SEARCH_SUCCESS = 1;
    private static final int SEARCH_FAIL = 0;
    private static final int SEARCH_NONE_RESULT = 2;
    private static final int TRANS_FAIL = 3;//转换为json失败
    private static final int TRANS_SUCCESS = 4;
        @SuppressLint("HandlerLeak")
        private NotLeakHandler handler  = new NotLeakHandler<FindOthersListActivity>(this){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case SEARCH_SUCCESS:
                        adapter.notifyDataSetChanged();
                        break;
                    case SEARCH_FAIL:
                        toast("查找失败");
                        break;
                    case SEARCH_NONE_RESULT:
                        toast("没有检索结果");
                        break;
                }
            }
        };
    private void searchOthers(String nameKey){
        HttpUtils.sendOkHttpRequest(Constant.findUsersUrl, "keyword", nameKey, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                toast("查找失败");
                LogUtils.e("others","查找失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String temp = response.body().string();
                LogUtils.e("others",temp);

                listOthers.addAll(getListFromJson(temp));//注意引用问题
                Message m = handler.obtainMessage();
                if(listOthers == null){
                    m.what = SEARCH_FAIL;
                    handler.sendMessage(m);
                    return;
                }else if(listOthers.size() == 0){
                    m.what = SEARCH_NONE_RESULT;
                }else{
                    m.what = SEARCH_SUCCESS;
                }
                handler.sendMessage(m);
            }
        });
    }
    /**
     * 设置List&ltOthersBean&gt,注意引用并没有改变
     * @param jsonStr 能转换为jsonArray的字符串
     * @return
     */
    private List<OthersBean>  getListFromJson(String jsonStr){
        List<OthersBean> listOthers = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            if (jsonArray.length() == 0){
                return listOthers;
            }
            JSONObject object;
            OthersBean othersBean;
            for(int i = 0; i < jsonArray.length();i++ ){
                object = jsonArray.getJSONObject(i);
                othersBean = new OthersBean();
                othersBean.setId(object.getString(keyList.get(0)));
                othersBean.setImgUrl(object.getString(keyList.get(1)));
                othersBean.setName(object.getString(keyList.get(2)));
                listOthers.add(othersBean);
            }
            return listOthers;
        } catch (JSONException e) {
            LogUtils.e("m =================JSONException",e.toString());
            return null;
        }
    }


    private ListOthersAdapter.OnClickListener othersOnClick = new ListOthersAdapter.OnClickListener(){

        @Override
        public void nameOnClick(View v, final int position) {
            BmobIMUserInfo userb = new BmobIMUserInfo();
            userb.setUserId(listOthers.get(position).getId());//信息可能未填完全
            String imgb = listOthers.get(position).getImgUrl();
            String nameb = listOthers.get(position).getName();
            userb.setAvatar(imgb == null? "null":imgb);//此处设置的信息是对应？？？？
            userb.setName(nameb == null? "null":nameb);//
            //向服务器发送更改信息+更改本地信息+回传给上一个界面
            BmobIM.getInstance().startPrivateConversation(userb, new ConversationListener() {
                @Override
                public void done(BmobIMConversation c, BmobException e) {
                    if(e==null){
                        //在此跳转到聊天页面
                        OthersBean other = listOthers.get(position);
                        BombChatActivity.actionStart(FindOthersListActivity.this,other.getName(),other.getId(),other.getImgUrl(),c);

                    }else{
                        LogUtils.e("实时聊天错误",e.getErrorCode()+e.toString());
                    }
                }
            });
        }


        @Override
        public void imgOnClick(View v, int position) {
            Activity_OthersMessage.actioStart(FindOthersListActivity.this,listOthers.get(position).getId());
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(SEARCH_FAIL);
        handler.removeMessages(SEARCH_SUCCESS);
        handler.removeMessages(SEARCH_NONE_RESULT);
    }
    private void toast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }
}
