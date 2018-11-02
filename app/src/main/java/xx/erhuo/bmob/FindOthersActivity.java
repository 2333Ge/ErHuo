package xx.erhuo.bmob;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.erhuo.view.Topbar;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.exception.BmobException;
import xx.erhuo.com.R;


public class FindOthersActivity extends AppCompatActivity {
    private static final String KEY_TITLE = "title",KEY_VALUELAST = "valueLast";
    public static void actionStart(Context context, String title, String valueLast){
        Intent intent = new Intent(context,FindOthersActivity.class);
        intent.putExtra(KEY_TITLE,title);
        intent.putExtra(KEY_VALUELAST,valueLast);
        context.startActivity(intent);
    }


    private Topbar topbar;
    private EditText et_input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_setting_edit);
        initView();
    }

    private void initView() {
        topbar = findViewById(R.id.topBar_settingEdit);
        et_input = findViewById(R.id.et_settingInput);
        Intent intent = getIntent();
        String title = "修改",value = "";
        title = intent.getStringExtra(KEY_TITLE);
        value = intent.getStringExtra(KEY_VALUELAST);
        topbar.setTitleText(title);
        et_input.setText(value);
        et_input.setSelection(value.length());
        topbar.setOnTopbarCilckListener(topbarClickListener);
    }
    Topbar.TopbarCilckListener topbarClickListener = new Topbar.TopbarCilckListener(){

        @Override
        public void leftClick(View v) {
            FindOthersActivity.this.finish();
        }

        @Override
        public void rightClick(View v) {
            FindOthersListActivity.actionStart(FindOthersActivity.this,et_input.getText().toString());

//            BmobIMUserInfo user = new BmobIMUserInfo();
//            user.setUserId(et_input.getText().toString());//信息可能未填完全
//            user.setAvatar("null");
//            user.setName("null");
//            toast(et_input.getText().toString());
//            //向服务器发送更改信息+更改本地信息+回传给上一个界面
//            BmobIM.getInstance().startPrivateConversation(user, new ConversationListener() {
//                @Override
//                public void done(BmobIMConversation c, BmobException e) {
//                    if(e==null){
//                        //在此跳转到聊天页面
//                        BombChatActivity.actionStart(FindOthersActivity.this,et_input.getText().toString(),c);
//
//                    }else{
//                        toast(e.getMessage()+"("+e.getErrorCode()+")");
//                    }
//                }
//            });
        }
    };
    private void toast(String str){
        Toast.makeText(FindOthersActivity.this,str,Toast.LENGTH_SHORT).show();
    }
}
