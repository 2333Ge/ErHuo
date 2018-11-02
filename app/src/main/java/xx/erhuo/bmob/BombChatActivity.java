package xx.erhuo.bmob;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.erhuo.utils.LogUtils;
import com.erhuo.utils.MyApplication;
import com.erhuo.utils.TimeHelper;

import xx.erhuo.bean.User;
import xx.erhuo.com.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

public class BombChatActivity extends AppCompatActivity implements MessageListHandler {
    private Button b_send,b_chatMore;
    private EditText et_content;
    private RecyclerView rv_chat;
    private RvChatAdapter adapter;
    private List<MessageBean> listMessage = new ArrayList<MessageBean>();
    private LinearLayout ll_chatMore;
    private Toolbar t_title;
    private TextView tv_name;

    private String name;
    private String id;
    private String otherHeadImgUrl;
    private User user;
    //数据库
    private MessageDBUtils messageDBUtils;
    private String tableName = "bombChat";
    private String finalTableName;
    private MessageBean lastMessage = new MessageBean();
    private BmobIMConversation c;

    private static final  String KEY_NAME = "KEY_NAME";
    private static final  String KEY_ID = "KEY_ID";
    private static final  String KEY_HEAD_IMG = "KEY_HEAD_IMG";
    public static void actionStart(Context context,String name ,String id,String imgUrl,BmobIMConversation c){
        Intent intent = new Intent(context,BombChatActivity.class);
        intent.putExtra(KEY_NAME,name);
        intent.putExtra(KEY_ID,id);
        intent.putExtra(KEY_HEAD_IMG,imgUrl);
        Bundle bundle = new Bundle();
        bundle.putSerializable("c", c);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
//    public static void actionStartForResult(Activity activity,int requestCode,String name ,String id,String imgUrl,BmobIMConversation c){
//        Intent intent = new Intent(activity,BombChatActivity.class);
//        intent.putExtra(KEY_NAME,name);
//        intent.putExtra(KEY_ID,id);
//        intent.putExtra(KEY_HEAD_IMG,imgUrl);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("c", c);
//        intent.putExtras(bundle);
//        activity.startActivityForResult(intent,requestCode);
//!失败，onActivityForResult没返回值
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //在聊天页面的onCreate方法中，通过如下方法创建新的会话实例,这个obtain方法才是真正创建一个管理消息发送的会话
        c = BmobIMConversation.obtain(BmobIMClient.getInstance(),(BmobIMConversation)getIntent().getSerializableExtra("c"));
        //LogUtils.e("c======",c.getConversationId());

        initData();
        initView();

    }



    /**
     * 初始化变量
     */
    private void initData() {
        user = MyApplication.getUser();
        name = getIntent().getStringExtra(KEY_NAME);
        id = getIntent().getStringExtra(KEY_ID);
        otherHeadImgUrl = getIntent().getStringExtra(KEY_HEAD_IMG);

        lastMessage.setUserId(id);
        lastMessage.setHeadImgUrl(otherHeadImgUrl);
        lastMessage.setName(name);
        initDatabase();
    }

    /**
     * 初始化数据库
     */
    private void initDatabase() {
        finalTableName ="bombChat"+ MyApplication.getUser().getUserId()+"and" + id;

        messageDBUtils = new MessageDBUtils(this,finalTableName);
        messageDBUtils.setListAllFromTable(listMessage,finalTableName);//可能是引用变了导致界面不显示
//        List<MessageBean> listTemp = new ArrayList<>();
//        listTemp.addAll(messageDBUtils.selectAllFromTable(MessageDBUtils.RECENT_MSG_TABLE));
//        if (listTemp.size() >=1){
//            LogUtils.e("test=================",""+listTemp.get(0).getContent());
//        }

        adapter = new RvChatAdapter(listMessage,this);
        //adapter.notifyDataSetChanged();
        //rv_chat.scrollToPosition(listMessage.size() - 1);
    }
    /**
     * 初始化布局
     */
    private void initView(){

        tv_name = findViewById(R.id.tv_title_toolbar);
        tv_name.setText(name);
        //初始化聊天显示
        rv_chat = findViewById(R.id.rv_chat);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv_chat.setLayoutManager(llm);
        adapter.setOnSendAgainListener(new RvChatAdapter.SendAgainListener() {
            @Override
            public void sendAgain(MessageBean messageBean) {
                sendNewMessage(messageBean.getContent());
                //删除数据库原来的
                messageDBUtils.delete(messageBean.getTime(),finalTableName);
            }
        });
        rv_chat.setAdapter(adapter);
        rv_chat.scrollToPosition(listMessage.size() - 1);
        b_send = findViewById(R.id.b_sendMessage);
        et_content = findViewById(R.id.et_chatInput);

        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                rv_chat.scrollToPosition(listMessage.size()-1);
            }
        });

        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = et_content.getText().toString();
                sendNewMessage(content);
            }
        });
        ll_chatMore = findViewById(R.id.ll_chatMore);
        b_chatMore = findViewById(R.id.b_chatMore);
        b_chatMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ll_chatMore.getVisibility() == View.GONE){
                    ll_chatMore.setVisibility(View.VISIBLE);
                }else{
                    ll_chatMore.setVisibility(View.GONE);
                }

            }
        });
        //初始化toolbar
        t_title = findViewById(R.id.toolbar_include);
        t_title.setTitle("");
        setSupportActionBar(t_title);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }
    private void sendNewMessage(String content) {
        BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setContent(content);
        //可随意设置额外信息
        Map<String, Object> map = new HashMap<>();
        map.put("level", "1");
        msg.setExtraMap(map);
        c.sendMessage(msg, new MessageSendListener() {
            @Override
            public void onStart(BmobIMMessage msg) {
                super.onStart(msg);
                MessageBean message = new MessageBean();
                message.setContent(msg.getContent());
                message.setName(name);
                message.setFromOthers(false);
                message.setUserId(user.getUserId());
                message.setHeadImgUrl(user.getHeadUrl());
                message.setTime(TimeHelper.getTimeNow());
                message.setSendSuccessful(true);
                message.setMessageType(MessageBean.TEXT);
                listMessage.add(message);
                adapter.notifyItemInserted(listMessage.size() - 1);
                messageDBUtils.insertAMessage(message,finalTableName);
//                message.setUserId(id);
//                messageDBUtils.insertARecentMsg(message);//此处id是用户自己的id，数据库用id判断和谁的聊天记录，所以能直接这样插入
            }

            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                adapter.notifyDataSetChanged();
                et_content.setText("");
                if (e != null) {
                    listMessage.get(listMessage.size()-1).setSendSuccessful(false);
                    adapter.notifyDataSetChanged();
                    rv_chat.scrollToPosition(listMessage.size() - 1);
                    messageDBUtils.updateData(listMessage.get(listMessage.size()-1).getTime(),finalTableName,false);
                    toast(e.getMessage());
                }
            }
        });

    }
    private void toast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    public void onMessageReceive(List<MessageEvent> list) {
        for(int i = 0;  i <list.size();i++){
            String content = list.get(i).getMessage().getContent();
            String thisMessageId = list.get(i).getMessage().getFromId();
            String thisMessageName = list.get(i).getFromUserInfo().getName();
            String thisMessageImgUrl = list.get(i).getFromUserInfo().getAvatar();
            MessageBean message = new MessageBean();
            message.setContent(content);
            message.setFromOthers(true);
            message.setUserId(thisMessageId);
            message.setTime(TimeHelper.getTimeNow());
            message.setMessageType(MessageBean.TEXT);
            if(id.equals(thisMessageId)){
                //LogUtils.e("=========","id.equals(thisMessageId)");
                message.setName(thisMessageName);
                message.setHeadImgUrl(thisMessageImgUrl);
                listMessage.add(message);
                adapter.notifyDataSetChanged();
                rv_chat.scrollToPosition(listMessage.size()-1);
            }



        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
//                Intent intent = new Intent();
//                intent.putExtra("id",id);
//                BombChatActivity.this.setResult(RESULT_OK,intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        BmobIM.getInstance().addMessageListHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        messageDBUtils.updateDataOfRecentMsg(id,0);
        BmobIM.getInstance().removeMessageListHandler(this);
    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //messageDBUtils.dispose();
    }
}
