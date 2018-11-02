package xx.erhuo.com;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.LogUtils;
import com.erhuo.utils.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.exception.BmobException;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xx.erhuo.bean.Commodity;
import xx.erhuo.bean.CommodityCommand;
import xx.erhuo.bean.Constant;
import xx.erhuo.bean.LeaveMessage;
import xx.erhuo.bean.User;
import xx.erhuo.bmob.BombChatActivity;
import xx.erhuo.bmob.FindOthersListActivity;
import xx.erhuo.bmob.OthersBean;
import xx.erhuo.com.adapter.CommandAdapter;
import xx.erhuo.com.adapter.CommodityImgAdapter;
import xx.erhuo.com.adapter.LeaveMessageAdapter;
import xx.erhuo.com.util.CommadUtils;

import static xx.erhuo.com.util.CommadUtils.setListCommandFromJson;

public class ACommodityActivity extends AppCompatActivity {

    public static void actionStart(Context context,String pid){
        Intent intent = new Intent(context,ACommodityActivity.class);
        intent.putExtra(PID,pid);
        context.startActivity(intent);
    }

    private ImageView iv_big;
    private TextView tv_priceNow, tv_priceOld;
    private TextView tv_imgMenu,tv_imgLast,tv_imgNext;
    private TextView tv_title,tv_desc,tv_date;
    private TextView tv_comment;
    private TextView tv_concernNum;
    private ImageView iv_concernIcon;
    private RecyclerView rv_imgs,rv_leaveMesage,rv_commodity_command;
    private LinearLayout ll_concernProduct;
    private CommodityImgAdapter imgsAdapter;
    private Toolbar t_name_commodity;
    private CollapsingToolbarLayout ctl_coordinatorLayout_commodity;
    private CoordinatorLayout cl_loadOK;
    private ProgressBar pb_loadIng;
    private TextView tv_loadFail;
    private  TextView tv_ownerName,tv_ownerOnSellNum,tv_SelledNum,tv_guanZhu;
    private Button b_concern_other;
    private TextView tv_leaveMessageNum;
    private CircleImageView civ_ownerHead;
    private ImageView iv_sex;
    private User user;
    private String pid;
    private Commodity commodity;
    private boolean isConcendProduct; //这个属性是进入这个界面就应该加载的
    private LeaveMessageAdapter leaveMessageAdapter;
    private List<LeaveMessage> leaveMessageList;
    private CommandAdapter commandAdapter;
    private List<CommodityCommand> commandList;

    private static final String PID = "pid";
    private final int SEARCH_SUCCESS = 1;
    private final int NONE_RESULT = 3;
    private final int SEARCH_FAIL = 2;
    private final int DISPLAY_MESSAGE_SUCCESS = 4;
    private final int DISPLAY_MESSAGE_FAIL = 5;
    private final int LEAVE_MESSAGE_SUCCESS = 6;
    private final int LEAVE_MESSAGE_FAIL = 7;
    private final int DISPLAY_COMMAND_SUCCESS = 8;
    private final int DISPLAY_COMMAND_FAIL = 9;
    private final int CONCERN_INTERT_FAIL = 10;
    private final int CONCERN_SUCCESS = 11;
    private final int CONCERN_FAIL = 12;
    private final int CONCERN_PRODUCT_SUCCESS = 13;
    private final int CONCERN_PRODUCT_FAIL = 14;
    private final String commodityUrl = "http://39.105.0.212/android/product_findProductByPid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__acommodity);
        initData();
    }

    private void initData() {
        user  = MyApplication.getUser();
        pid = getIntent().getStringExtra(PID);
        leaveMessageList = new ArrayList<>();
        commandList = new ArrayList<>();
        if(pid == null){
            LogUtils.e("ACommodityActivity","pid == -1");
        }else{
            HttpUtils.sendOkHttpRequest(commodityUrl, "pid", pid, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message m = handler.obtainMessage();
                    m.what = SEARCH_FAIL;

                    handler.sendMessage(m);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonData = response.body().string();

                    commodity = getCommodityFromJson(jsonData);
                    Message m = handler.obtainMessage();
                    if (commodity != null){
                        m.what = SEARCH_SUCCESS;
                        LogUtils.i("commodity",jsonData);
                    }else {
                        m.what = NONE_RESULT;
                        LogUtils.e("NONE_RESULT",jsonData);
                    }
                    handler.sendMessage(m);
                }
            });
        }
    }
    private void initView(){
        ll_concernProduct = findViewById(R.id.ll_concernProduct);
        cl_loadOK = findViewById(R.id.cl_aCommodity_loadOK);
        pb_loadIng = findViewById(R.id.pb_aCommodity_loadIng);
        tv_loadFail = findViewById(R.id.tv_aCommodity_loadFail);
        t_name_commodity = findViewById(R.id.t_title_commodity);
        ctl_coordinatorLayout_commodity = findViewById(R.id.ctl_CollapsingToolbarLayout_commodity);
        iv_big = findViewById(R.id.iv_aCommodity_bigImg);
        b_concern_other = findViewById(R.id.b_concern_other);
        tv_priceNow = findViewById(R.id.tv_aCommodity_price);
        tv_imgMenu = findViewById(R.id.tv_aCommodity_imgMenu);
        tv_imgLast = findViewById(R.id.tv_aCommodity_lastImg);
        tv_imgNext = findViewById(R.id.tv_aCommodity_nextImg);
        tv_date = findViewById(R.id.tv_aCommodity_date);
        tv_title = findViewById(R.id.tv_aCommodity_title);
        tv_desc = findViewById(R.id.tv_aCommodity_desc);
        tv_comment = findViewById(R.id.tv_comment);
        tv_concernNum = findViewById(R.id.tv_aCommodity_concernNum);
        iv_concernIcon = findViewById(R.id.iv_aCommodity_concernIcon);
        rv_imgs = findViewById(R.id.rv_aCommodity_imgs);
        rv_leaveMesage = findViewById(R.id.rv_commodity_evaluate);
        rv_commodity_command = findViewById(R.id.rv_commodity_command);
        tv_ownerName = findViewById(R.id.tv_nameSeller_commodity);
        tv_ownerOnSellNum = findViewById(R.id.tv_seller_onSellNum);
        tv_SelledNum = findViewById(R.id.tv_seller_SellNum);
        tv_ownerOnSellNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnSellActivity.actionStart(ACommodityActivity.this,
                        commodity.getOwnerName()+"架上的",commodity.getOwnerId());
            }
        });
        tv_SelledNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoldHistoryActivity.actionStart(ACommodityActivity.this,
                        commodity.getOwnerName()+"卖出的",commodity.getOwnerId());
            }
        });
        tv_guanZhu = findViewById((R.id.tv_seller_econcernNum));
        tv_guanZhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConcernActivity.actionStart(ACommodityActivity.this,
                        commodity.getOwnerName()+"的关注",commodity.getOwnerId()      );
            }
        });

        civ_ownerHead = findViewById((R.id.civ_headSeller_commodity));
        iv_sex = findViewById((R.id.iv_sexSeller_commodity));
        tv_leaveMessageNum = findViewById(R.id.tv_leaveMessageNum);


        leaveMessageAdapter = new LeaveMessageAdapter(leaveMessageList);
        rv_leaveMesage.setAdapter(leaveMessageAdapter);
        GridLayoutManager glm = new GridLayoutManager(this,2);
        rv_commodity_command.setLayoutManager(glm);
        commandAdapter = new CommandAdapter(commandList);
        commandAdapter.setOnCommandClickListener(new CommandAdapter.OnCommandClickListener() {
            @Override
            public void onClick(View v, String id, int position) {
                ACommodityActivity.actionStart(ACommodityActivity.this,id);
            }
        });
        rv_commodity_command.setAdapter(commandAdapter);

        List<String> imgList = commodity.getImgUrlList();

        HttpUtils.setImageView(iv_big,imgList.get(0));
        new SetCircleImageViewTask().execute(commodity.getOwnerHeadImgUrl());
        tv_ownerOnSellNum.setText(commodity.getOnSellNum()+"");
        tv_SelledNum.setText(commodity.getSelledNum()+"");
        tv_guanZhu.setText(commodity.getGuanZhu()+"");
        tv_ownerName.setText(commodity.getOwnerName());
        int sexDrawableId = commodity.isOwnerSex()?R.drawable.boy32:R.drawable.girl32;
        iv_sex.setImageResource(sexDrawableId);

        imgsAdapter = new CommodityImgAdapter(imgList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_imgs.setLayoutManager(llm);
        rv_imgs.setAdapter(imgsAdapter);
        tv_priceNow.setText(commodity.getPrice()+ "");
        tv_concernNum.setText(commodity.getConcerned()+"");
        tv_desc.setText(commodity.getContent());
        tv_title.setText(commodity.getTitle());
        tv_date.setText(commodity.getDate());

        if(imgList.size()<=1){//设置颜色
            tv_imgMenu.setText(imgList.size() + "/" +imgList.size());
            tv_imgLast.setTextColor(getResources().getColor(R.color.img_isNotHaveOther));
            tv_imgNext.setTextColor(getResources().getColor(R.color.img_isNotHaveOther));
        }else{
            tv_imgMenu.setText("1/" +imgList.size());
            tv_imgLast.setTextColor(getResources().getColor(R.color.img_isNotHaveOther));
            tv_imgNext.setTextColor(getResources().getColor(R.color.img_havaOther));
        }
        iv_big.setOnClickListener(new ImgBigOnClick(imgList.get(0)));

        setSupportActionBar(t_name_commodity);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ctl_coordinatorLayout_commodity.setTitle(" ");

        tv_comment.setOnClickListener(commentClick);
        civ_ownerHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity_OthersMessage.actioStart(ACommodityActivity.this,commodity.getOwnerId());
            }
        });
        b_concern_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConcernSomeone();
            }
        });
        ll_concernProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !isConcendProduct){
                    concernProduct();
                }
            }
        });
        checkMessage();
        initCommand();
    }

    private void concernProduct() {
        HttpUtils.sendOkHttpRequest(Constant.concernSomethingUrl, new String[]{"uid", "pid"}, new String[]{user.getUserId(), commodity.getId()}, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message m = handler.obtainMessage();
                m.what = CONCERN_INTERT_FAIL;
                handler.sendMessage(m);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message m = handler.obtainMessage();
                String status = response.body().string();
                LogUtils.e("关注物品回复",status);
                if(status.equals("success"))
                    m.what = CONCERN_PRODUCT_SUCCESS;
                else
                    m.what = CONCERN_PRODUCT_FAIL;
                handler.sendMessage(m);
            }
        });
    }

    private void ConcernSomeone() {
        HttpUtils.sendOkHttpRequest(Constant.concernSomeOneUrl, new String[]{"uid", "uid2"},
                new String[]{user.getUserId(),commodity.getOwnerId()}, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message m = handler.obtainMessage();
                        m.what = CONCERN_INTERT_FAIL;
                        handler.sendMessage(m);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message m = handler.obtainMessage();
                        String status = response.body().string();
                        LogUtils.e("关注人回复",status);
                        if(status.equals("success"))
                            m.what = CONCERN_SUCCESS;
                        else
                            m.what = CONCERN_FAIL;
                        handler.sendMessage(m);
                    }
                });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEARCH_FAIL:
                    tv_loadFail.setVisibility(View.VISIBLE);
                    pb_loadIng.setVisibility(View.GONE);
                    break;
                case SEARCH_SUCCESS:
                    initView();
                    cl_loadOK.setVisibility(View.VISIBLE);
                    pb_loadIng.setVisibility(View.GONE);
                    break;
                case  NONE_RESULT:
                    break;
                case DISPLAY_MESSAGE_SUCCESS:
                       tv_leaveMessageNum.setText(""+leaveMessageList.size());
                       leaveMessageAdapter.notifyDataSetChanged();
                    break;
                case DISPLAY_MESSAGE_FAIL:
                    toast("读取留言失败");
                    break;
                case LEAVE_MESSAGE_SUCCESS:
                    checkMessage();//刷新全部，略显粗暴
                    break;
                case LEAVE_MESSAGE_FAIL:
                    toast("留言失败");
                    break;
                case DISPLAY_COMMAND_SUCCESS:
                    commandAdapter.notifyDataSetChanged();
                    break;
                case DISPLAY_COMMAND_FAIL:
                    toast("查看推荐失败");
                    break;
                case CONCERN_INTERT_FAIL:
                    toast("请检查网络后重试");
                    break;
                case CONCERN_SUCCESS:
                    b_concern_other.setText("已关注");
                    user.setSpConcernNum(user.getConcernNum()+1);
                    break;
                case CONCERN_FAIL:
                    toast("关注卖家失败");
                    break;
                case CONCERN_PRODUCT_SUCCESS:
                    tv_concernNum.setText(commodity.getConcerned() + 1 +"");
                    iv_concernIcon.setImageDrawable(getResources().getDrawable(R.drawable.like_full32));
                    break;
                case CONCERN_PRODUCT_FAIL:
                    toast("关注宝贝失败");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 查看留言
     */
    private void checkMessage(){
        //商品加载完成后再加载留言，以免findViewById还没执行
        HttpUtils.sendOkHttpRequest(Constant.checkCommentUrl, "pid", pid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.e("查看评论失败",e.toString());
                Message m = handler.obtainMessage();
                m.what = DISPLAY_MESSAGE_FAIL;
                handler.sendMessage(m);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String temp = response.body().string();
                LogUtils.e("评论",temp);
                List<LeaveMessage> tempList = getListMessageFromJson(temp);
                Message m = handler.obtainMessage();

                if(tempList == null){
                    m.what = DISPLAY_MESSAGE_FAIL;
                }else{
                    m.what = DISPLAY_MESSAGE_SUCCESS;
                    leaveMessageList.clear();//？？太粗暴了
                    leaveMessageList.addAll(tempList);
//                    LogUtils.e("leaveMessageList",leaveMessageList.size()+"");
//                    LogUtils.e("tempList",tempList.size()+"");
                }
                handler.sendMessage(m);
            }
        });
    }

    /**
     *
     */
    private void initCommand(){
        HttpUtils.sendOkHttpRequest(Constant.findCommandProductsUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.e("查看推荐失败",e.toString());
                Message m = handler.obtainMessage();
                m.what = DISPLAY_COMMAND_FAIL;
                handler.sendMessage(m);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String temp = response.body().string();
                LogUtils.e("推荐",temp);
                int status = setListCommandFromJson(commandList,temp);
                Message m = handler.obtainMessage();

                if(status == CommadUtils.TRANS_FAIL){
                    m.what = DISPLAY_COMMAND_FAIL;
                }else{
                    m.what = DISPLAY_COMMAND_SUCCESS;
//                    LogUtils.e("leaveMessageList",leaveMessageList.size()+"");
                }
                handler.sendMessage(m);
            }
        });
    }
    /**
     * 从json串中获得留言信息
     * @param str
     * @return
     */
    private List<LeaveMessage> getListMessageFromJson(String str){
        List<LeaveMessage> leaveMessageList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(str);
            JSONObject jsonObject;
            LeaveMessage leaveMessage;
            for(int i = 0; i< jsonArray.length();i++){
                leaveMessage = new LeaveMessage();
                jsonObject = jsonArray.getJSONObject(i);
                leaveMessage.setContent(jsonObject.getString("liuyan"));
                leaveMessage.setHeadUrl(jsonObject.getString("image"));
                leaveMessage.setId(jsonObject.getString("uid"));
                leaveMessage.setName(jsonObject.getString("username"));
                leaveMessage.setTime(jsonObject.getString("liuyan_time"));
                leaveMessageList.add(leaveMessage);
            }
            return leaveMessageList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     *
     * @param jsonData
     * @return
     */
    private Commodity getCommodityFromJson(String jsonData){
        Commodity commodity = new Commodity();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            commodity.setId(jsonObject.getString("pid"));
            commodity.setPriceOriginal((float)jsonObject.getDouble("oldPrice"));
            commodity.setPrice((float)jsonObject.getDouble("nowPrice"));
            commodity.setTitle(jsonObject.getString("pname"));
            commodity.setContent(jsonObject.getString("desc"));
            //"uid":"76","user_image":"http://39.105.0.212/shop/products/3ddd9a1a1d5df3bed7ef8ae6f9cab18a.png"
            // ,"sex":"male","user_guanzhu":"22","user_onsell_num":"20","user_jiaoyi":"5"
            commodity.setGuanZhu(jsonObject.getInt("user_guanzhu"));
            commodity.setOwnerId(jsonObject.getString("uid"));
            commodity.setOwnerSex(jsonObject.getString("sex").equals("male"));
            commodity.setOnSellNum(jsonObject.getInt("user_onsell_num"));
            commodity.setSelledNum(jsonObject.getInt("user_jiaoyi"));
            commodity.setOwnerName(jsonObject.getString("username"));
            commodity.setOwnerHeadImgUrl(jsonObject.getString("user_image"));
//            JSONArray imgJsonArray = jsonObject.getJSONArray("image");
//            for(int i = 0; i < imgJsonArray.length(); i++ ){
//                imgJsonArray.get
//            }
            List<String> imgUrlList = new ArrayList<>();
            imgUrlList.add(jsonObject.getString("image"));//此处因为数组
            commodity.setImgUrlList(imgUrlList);
            commodity.setConcerned(jsonObject.getInt("guanzhu"));
            commodity.setDate(jsonObject.getString("date"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return commodity;
    }

    class ImgBigOnClick implements View.OnClickListener {
        String url;
        ImgBigOnClick(String url){
            this.url = url;
        }
        @Override
        public void onClick(View view) {
            DisplayImgActivity.actionStart(ACommodityActivity.this,url);
        }
    }
    private View.OnClickListener commentClick = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            if( !user.isLogin()){
                toast("请先登陆");
            }else{
                showCommentDialog();
            }


        }
    };
    /**
     *性别选择
     */
    private void showCommentDialog() {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// 自定义对话框
        View commentView = View.inflate(this,R.layout.input_comment,null);
        builder.setView(commentView);
        dialog = builder.create();// 让弹出框显示
        //宽度铺满
        Window window = dialog.getWindow();
        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//?????????????设置失败
        //将设置好的属性set回去
        window.setAttributes(lp);

        dialog.show();
        EditText et_comment = commentView.findViewById(R.id.et_comment);
        Button b_commentOK = commentView.findViewById(R.id.b_comment_ok);
        b_commentOK.setOnClickListener(new DialogBtnClick(dialog,et_comment));

    }

    private class DialogBtnClick implements View.OnClickListener {
        AlertDialog dialog;
        EditText et;
        DialogBtnClick(AlertDialog dialog,EditText et){
            this.dialog = dialog;
            this.et = et;
        }
        @Override
        public void onClick(View view) {
            String comment = et.getText().toString();

            HttpUtils.sendOkHttpRequest(Constant.sendCommentUrl, new String[]{"pid", "uid", "comment"},
                    new String[]{pid, user.getUserId(), comment}, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            LogUtils.e("留言失败","留言失败" + e.toString());
                            Message m = handler.obtainMessage();
                            m.what = LEAVE_MESSAGE_FAIL;
                            handler.sendMessage(m);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String status = response.body().string();
                            Message m = handler.obtainMessage();
                            if(status.equals("success")){
                                m.what = LEAVE_MESSAGE_SUCCESS;
                                LogUtils.e("留言成功","留言成功");
                            }else{
                                m.what = LEAVE_MESSAGE_FAIL;
                                LogUtils.e("留言失败","留言失败");
                            }
                            handler.sendMessage(m);
                        }
                    });
            dialog.dismiss();
        }
    }
    /**
     * toolbar返回键事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_item_message:
                startChat();
                break;
        }
        return true;
    }

    private void startChat() {
        if(!user.isLogin()){
            toast("请先登录");
            return;
        }
        BmobIMUserInfo user = new BmobIMUserInfo();
        user.setUserId(commodity.getId());//信息可能未填完全
        user.setAvatar("null");
        user.setName("null");
        //向服务器发送更改信息+更改本地信息+回传给上一个界面
        BmobIM.getInstance().startPrivateConversation(user, new ConversationListener() {
            @Override
            public void done(BmobIMConversation c, BmobException e) {
                if(e==null){
                    //在此跳转到聊天页面
                    //OthersBean other = listOthers.get(position);
                    BombChatActivity.actionStart(ACommodityActivity.this,commodity.getOwnerName(),commodity.getOwnerId(),commodity.getOwnerHeadImgUrl(),c);

                }else{
                    LogUtils.e("实时聊天错误","实时聊天错误");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setIconsVisible(menu,true);
        getMenuInflater().inflate(R.menu.menu_acommodity,menu);
        return true;
    }
    /**
     * 解决menu不显示图标问题
     * @param menu
     * @param flag
     */
    private void setIconsVisible(Menu menu, boolean flag) {
        //判断menu是否为空
        if(menu != null) {
            try {
                //如果不为空,就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class SetCircleImageViewTask extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            LogUtils.e("SetCircleImageViewTask=================",url);
            if( url == null)
                return null;
            return HttpUtils.getBitMapFromUrl(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null){
                civ_ownerHead.setImageBitmap(bitmap);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(SEARCH_SUCCESS);
        handler.removeMessages(SEARCH_FAIL);
        handler.removeMessages(NONE_RESULT);
    }
    private void toast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }
}
