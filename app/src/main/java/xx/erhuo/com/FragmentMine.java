package xx.erhuo.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.MyApplication;

import de.hdodenhof.circleimageview.CircleImageView;
import xx.erhuo.bean.User;
import xx.erhuo.bmob.ChatActivity;
import xx.erhuo.bmob.MessageDBUtils;

public class FragmentMine extends Fragment {
    public static final String BUNDLE_NAME = "name";
    public static final String BUNDLE_IMGURL = "imgurl";
    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_USER_SETTING = 2;

    private View view;
    private TextView tv_name,tv_buyNum,tv_sellNum,tv_concernNum;
    private NavigationView nav_setting;
    private CircleImageView civ_head;

    private User user;
    public static FragmentMine newInstance(){

        FragmentMine f = new FragmentMine();

        return f;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = MyApplication.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mine,container,false);
        tv_name = view.findViewById(R.id.tv_name_mine);
        tv_buyNum = view.findViewById(R.id.tv_buyNum);
        tv_sellNum = view.findViewById(R.id.tv_sellNum);
        nav_setting = view.findViewById(R.id.nav_setting);
        civ_head = view.findViewById(R.id.civ_head_mine);
        tv_concernNum = view.findViewById(R.id.tv_concernNum);
        initView();
        return view;
    }

    private void initView() {
        tv_name.setOnClickListener(nameClickListener);
        civ_head.setOnClickListener(nameClickListener);
        nav_setting.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_ask:
                        ChatActivity.actionStart(getContext(),"小二");
                        break;
                    case R.id.item_onSell:
                        OnSellActivity.actionStart(getContext(),"我架上的",user.getUserId());
                        break;
                }
                return true;
            }
        });
        tv_sellNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !user.isLogin())
                    return;
                SoldHistoryActivity.actionStart(getContext(),"我卖出的",user.getUserId());

            }
        });
        tv_buyNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !user.isLogin())
                    return;
                BoughtHistoryActivity.actionStart(getContext(),"我买到的",user.getUserId());

            }
        });
        tv_concernNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !user.isLogin())
                    return;
                ConcernActivity.actionStart(getContext(),"我的关注",user.getUserId());


            }
        });
        if( !user.isLogin()){
            defaultSetting();
            return;
        }


        HttpUtils.setImageView(civ_head,user.getHeadUrl());

        tv_buyNum.setText(user.getBuyNum() + "");
        tv_sellNum.setText(user.getSellNum() + "");
        tv_concernNum.setText(user.getConcernNum() + "");
        tv_name.setText(user.getName());

    }
    View.OnClickListener nameClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            boolean isLogin = user.getSpStatus();
            if(isLogin){
                startActivityForResult(new Intent(getContext(),UserSettingActivity.class),REQUEST_CODE_USER_SETTING);
            }else{
                startActivityForResult(new Intent(getContext(),LoginRegisterActivity.class),REQUEST_CODE_LOGIN);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){

            case(REQUEST_CODE_LOGIN):
                //还要设置头像
                if(data != null){
                    int status = data.getIntExtra(LoginRegisterActivity.ANSWER_STATUS,LoginRegisterActivity.LOGIN_FAIL);
                    if (status == LoginRegisterActivity.LOGIN_SUCCESS){
                        tv_name.setText(user.getName());
                        tv_sellNum.setText(user.getSellNum()+"");
                        tv_buyNum.setText(user.getBuyNum() + "");
                        tv_concernNum.setText(user.getConcernNum() + "");
                        HttpUtils.setImageView(civ_head,user.getHeadUrl());
                    }else{
                        defaultSetting();
                    }
                }
                break;
            case REQUEST_CODE_USER_SETTING:
                if(data != null){
                    String out = data.getStringExtra(UserSettingActivity.ANSWER_OUT);
                    defaultSetting();
                    MessageDBUtils messageDBUtils =  new MessageDBUtils(getContext());
                    messageDBUtils.clearRecentMsg();
                }
                break;
        }
    }

    private void defaultSetting(){
        tv_name.setText("请先登录");
        tv_buyNum.setText("0");
        tv_sellNum.setText("0");
        civ_head.setImageDrawable(getResources().getDrawable(R.drawable.head));
    }
}
