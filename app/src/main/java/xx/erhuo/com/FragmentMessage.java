package xx.erhuo.com;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.LogUtils;
import com.erhuo.utils.MyApplication;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.v3.exception.BmobException;
import xx.erhuo.bean.User;
import xx.erhuo.bmob.BombChatActivity;
import xx.erhuo.bmob.FindOthersActivity;
import xx.erhuo.bmob.MessageBean;
import xx.erhuo.bmob.MessageDBUtils;
import xx.erhuo.com.adapter.RvAdapter;



public class FragmentMessage extends Fragment implements MessageListHandler {
    private View v;
    private RecyclerView rv_messages;
    private User user;
    private Toolbar t_title;
    private TextView tv_name;


    private List<MessageBean> listMessage = new ArrayList<>();
    private MessageDBUtils messageDBUtils;
    private RvAdapter adapter;
    public static FragmentMessage newInstance() {

        Bundle bundle = new Bundle();
        FragmentMessage f = new FragmentMessage();

        return f;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageDBUtils = new MessageDBUtils(getContext());
        user = MyApplication.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.frament_temp, container, false);

        initRv();
        initView();
        return v;
    }




    private void initView() {
        t_title = v.findViewById(R.id.toolbar_include);
        t_title.setTitle("");
        setHasOptionsMenu(true);
        ((MainActivity)getActivity()).setSupportActionBar(t_title);
        tv_name = v.findViewById(R.id.tv_title_toolbar);
        tv_name.setText("消息");

    }


    private void initRv() {
        rv_messages = v.findViewById(R.id.rv_messages);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv_messages.setLayoutManager(llm);
        adapter = new RvAdapter(listMessage);
        rv_messages.setAdapter(adapter);
        adapter.setItemOnClickListener(msgOnClickListener);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                break;
            case R.id.menu_item_who :
                if(user.isLogin()){
                    FindOthersActivity.actionStart(getContext(),"查找用户名","");
                }else{
                    toast("请先登陆");
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    private RvAdapter.ItemOnClickListener msgOnClickListener = new RvAdapter.ItemOnClickListener(){
        @Override
        public void nameOnClick(View view, int position) {
            if(user.isLogin()){
                startChat(position);
            }else{
                toast("账号错误，请先登录");
            }

        }

        @Override
        public void headImgOnClick(View view, int position) {
            Activity_OthersMessage.actioStart(getContext(),listMessage.get(position).getUserId());
        }

        @Override
        public void nameOnLongClick(View view, int position) {
            deleteDialog(position);
        }
    };

    /**
     * 删除某项的对话框
     */
    private void deleteDialog(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("是否删除该条记录?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                messageDBUtils.delete(listMessage.get(position).getUserId(),MessageDBUtils.RECENT_MSG_TABLE);
                listMessage.remove(position);
                adapter.notifyDataSetChanged();
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

    private void startChat(int position) {

        final MessageBean messageBean = listMessage.get(position);
        BmobIMUserInfo userb = new BmobIMUserInfo();
        userb.setUserId(messageBean.getUserId());//信息可能未填完全
        String imgb = messageBean.getHeadImgUrl();
        String nameb = messageBean.getName();
        userb.setAvatar(imgb == null ? "img未加载出来":imgb);//此处设置的信息是对应？？？？
        if(nameb.equals("")){
            nameb = " ";
        }
        userb.setName(nameb == null? "姓名暂未加载出来":nameb);//名字不能为“”
        toast(userb.getAvatar() + userb.getName());
        //向服务器发送更改信息+更改本地信息+回传给上一个界面
        BmobIM.getInstance().startPrivateConversation(userb, new ConversationListener() {
            @Override
            public void done(BmobIMConversation c, BmobException e) {
                if(e==null){
                    //在此跳转到聊天页面
                    //BombChatActivity.actionStartForResult(getActivity(),10,messageBean.getName(),messageBean.getUserId(),messageBean.getHeadImgUrl(),c);
                    BombChatActivity.actionStart(getContext(),messageBean.getName(),messageBean.getUserId(),messageBean.getHeadImgUrl(),c);

                }else{
                    LogUtils.e("实时聊天错误","实时聊天错误" + e.getErrorCode() + e.toString());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        checkRecentMessage();
        //toast("onStart");
    }

    /**
     * 查询数据库，检查最新消息
     */
    private void checkRecentMessage() {
        if(user.isLogin()){
            messageDBUtils.selectMsgOfRecentMsg(listMessage);
            adapter.notifyDataSetChanged();
        }else{
            listMessage.clear();
            adapter.notifyDataSetChanged();
            List<MessageBean> temp = new ArrayList<>();
            messageDBUtils.selectMsgOfRecentMsg(temp);
            toast("数据库长度" +temp.size());

        }


    }
    private void toast(String str) {
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        checkRecentMessage();
        //toast("onMessageReceive");
    }
    @Override
    public void onResume() {
        super.onResume();
        BmobIM.getInstance().addMessageListHandler(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BmobIM.getInstance().removeMessageListHandler(this);
    }
}
