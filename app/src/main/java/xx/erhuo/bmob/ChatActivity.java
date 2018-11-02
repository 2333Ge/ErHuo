package xx.erhuo.bmob;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.erhuo.utils.MyApplication;
import com.erhuo.utils.TimeHelper;

import xx.erhuo.bean.User;
import xx.erhuo.com.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private Button b_send,b_chatMore;
    private EditText et_content;
    private RecyclerView rv_chat;
    private RvChatAdapter adapter;
    private List<MessageBean> listMessage = new ArrayList<MessageBean>();
    private LinearLayout ll_chatMore;
    private Toolbar t_title;
    private TextView tv_name;
    private User user;
    //数据库
    private MessageDBUtils messageDBUtils;
    private String tableName = "robotMessage";

    private static final  String KEY_NAME = "KEY_NAME";

    public static void actionStart(Context context,String name){
        Intent intent = new Intent(context,ChatActivity.class);
        intent.putExtra(KEY_NAME,name);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = MyApplication.getUser();
        setContentView(R.layout.activity_chat);
        initView();
        initDatabase();

    }

    /**
     * 初始化数据库
     */
    private void initDatabase() {
        messageDBUtils = new MessageDBUtils(this,tableName);
        //listMessage.addAll();//可能是引用变了导致界面不显示
        messageDBUtils.setListAllFromTable(listMessage,tableName);
        adapter.notifyDataSetChanged();
        rv_chat.scrollToPosition(listMessage.size() - 1);

    }

    /**
     * 初始化布局
     */
    private void initView(){
        String name = getIntent().getStringExtra(KEY_NAME);
        tv_name = findViewById(R.id.tv_title_toolbar);
        tv_name.setText(name);
        //初始化聊天显示
        rv_chat = findViewById(R.id.rv_chat);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv_chat.setLayoutManager(llm);
        adapter = new RvChatAdapter(listMessage,this);
        adapter.setOnSendAgainListener(new RvChatAdapter.SendAgainListener() {
            @Override
            public void sendAgain(MessageBean messageBean) {
                sendNewMessage(messageBean.getContent());
                //删除数据库原来的
                messageDBUtils.delete(messageBean.getTime(),tableName);
            }
        });
        rv_chat.setAdapter(adapter);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_item_who :
                toastShrot("举报等");
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * Params 启动任务时的参数类型
     * Progress 后台任务执行中返回进度值的类型
     * Result 后台执行任务完成后返回结果的类型
     */
    class robotAsyncTask extends AsyncTask<String,Void,MessageBean>{

        @Override
        protected MessageBean doInBackground(String... strings) {
            String jsonContent = RobotUtils.transToJson(strings[0],strings[1]).toString();
            //Log.i("jsonContent",jsonContent);
            MessageBean resultMsg = RobotUtils.doPost(jsonContent);
            if(resultMsg == null ){
                return null;
            }else{
                resultMsg.setFromOthers(true);
                resultMsg.setTime(TimeHelper.getTimeNow());//////此处应该先获得返回的类型，前面一个可以返回MessageBean类型
                return resultMsg;
            }

        }

        @Override
        protected void onPostExecute(MessageBean messageBean) {
            super.onPostExecute(messageBean);
            if(messageBean != null){
                listMessage.add(messageBean);
                adapter.notifyItemInserted(listMessage.size()-1);
                rv_chat.scrollToPosition(listMessage.size() - 1);

                //messageDBUtils.insertAMessage(listMessage.get(listMessage.size() - 2),tableName);//得到返回值，说明上一条发送成功，存储上一条
                messageDBUtils.insertAMessage(messageBean,tableName);

            }else{//发送失败,!应该记录发送的顺序，因为可能不止一条发送失败，网络差也可能失败，期间可能发送了多条
                listMessage.get(listMessage.size()-1).setSendSuccessful(false);
                adapter.notifyDataSetChanged();
                rv_chat.scrollToPosition(listMessage.size() - 1);

                messageDBUtils.updateData(listMessage.get(listMessage.size()-1).getTime(),tableName,false);
                //此处应该是插入对应数据
            }

        }
    }

    /**
     * 发送新数据给机器人<href src="http://www.tuling123.com/member/robot/1290911/center/frame.jhtml?page=0&child=0"/></href>
     * @param content
     */
    private void sendNewMessage(String content){
        if( !content.equals("") ){
            MessageBean message = new MessageBean();
            message.setContent(content);
            message.setFromOthers(false);
            message.setUserId(user.getUserId());
            message.setTime(TimeHelper.getTimeNow());
            message.setSendSuccessful(true);
            message.setMessageType(MessageBean.TEXT);
            listMessage.add(message);
            messageDBUtils.insertAMessage(message,tableName);
            adapter.notifyItemInserted(listMessage.size() - 1);//有新消息，刷新显示
            rv_chat.scrollToPosition(listMessage.size() - 1);//定位到最后
            et_content.setText("");
            new robotAsyncTask().execute(content,"userID");//最后此处将填充UserId或者用户的名字等能标识用户的字符
            //得到返回值才说明发送成功
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat,menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageDBUtils.dispose();
    }

    public void toastShrot(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
}
