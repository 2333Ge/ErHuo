package xx.erhuo.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.erhuo.view.Topbar;


public class Activity_SettingEdit extends AppCompatActivity {

    public static final String KEY_TITLE = "title";
    public static final String KEY_VALUELAST = "valueLast";
    public static final String KEY_POSITION = "position";

    public static void actionStart(Context context,String title, String valueLast,int position){
        Intent intent = new Intent(context,Activity_SettingEdit.class);
        intent.putExtra(KEY_TITLE,title);
        intent.putExtra(KEY_VALUELAST,valueLast);
        intent.putExtra(KEY_POSITION,position);
        context.startActivity(intent);
    }


    private Topbar topbar;
    private EditText et_input;
    private int position;


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
        position = intent.getIntExtra(KEY_POSITION,0);
        topbar.setTitleText(title);
        et_input.setText(value);
        et_input.setSelection(value.length());
        topbar.setOnTopbarCilckListener(topbarClickListener);
    }
    Topbar.TopbarCilckListener topbarClickListener = new Topbar.TopbarCilckListener(){

        @Override
        public void leftClick(View v) {
            Activity_SettingEdit.this.finish();
        }

        @Override
        public void rightClick(View v) {
            //向服务器发送更改信息+更改本地信息+回传给上一个界面
            Intent intent = new Intent();
            intent.putExtra(KEY_POSITION,position);
            intent.putExtra(KEY_VALUELAST,et_input.getText().toString());
            setResult(UserSettingActivity.REQUEST_USERSETTING,intent);
            Activity_SettingEdit.this.finish();
        }
    };

}
