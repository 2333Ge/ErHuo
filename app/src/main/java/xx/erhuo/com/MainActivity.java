package xx.erhuo.com;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.erhuo.utils.BombMessageHandler;
import com.erhuo.utils.LogUtils;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.exception.BmobException;
import xx.erhuo.bean.User;
import xx.erhuo.com.util.DataGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private TabLayout tl_bottom;
    private ViewPager vp_content;
    private List<Fragment> listFragmeent;
    private List<String> listStringTemp = Arrays.asList("主页","发现","消息","我的");
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frament_messages);
        checkPermission();
        initView();
        initData();
        initBomb();
    }




    private void initView() {
        tl_bottom = findViewById(R.id.tl_bottom);
        tl_bottom.addTab(tl_bottom.newTab().setIcon(getResources().getDrawable(DataGenerator.mTabResSelected[0])).setText(DataGenerator.mTabTitle[0]));
        tl_bottom.addTab(tl_bottom.newTab().setIcon(getResources().getDrawable(DataGenerator.mTabRes[1])).setText(DataGenerator.mTabTitle[1]));
        tl_bottom.addTab(tl_bottom.newTab().setIcon(getResources().getDrawable(DataGenerator.mTabRes[2])).setText(DataGenerator.mTabTitle[2]));
        tl_bottom.addTab(tl_bottom.newTab().setIcon(getResources().getDrawable(DataGenerator.mTabRes[3])).setText(DataGenerator.mTabTitle[3]));
        tl_bottom.addOnTabSelectedListener(tabBottomListener);

        vp_content = findViewById(R.id.vp_content);

    }

    /**
     *
     */
    private void initData() {
        listFragmeent = new ArrayList<Fragment>();
        listFragmeent.add(FragmentHome.newInstance());
        listFragmeent.add(FragmentTest.newInstance(listStringTemp.get(1)));
        listFragmeent.add(FragmentMessage.newInstance());

        user = new User(this);
        if(user.isLogin()){
            listFragmeent.add(FragmentMine.newInstance());
        }else{
            listFragmeent.add(FragmentMine.newInstance());
        }



        vp_content.setAdapter(fragmentPagerAdapter);
        // ！vp_content.addOnPageChangeListener(onPageChangeListener);弃
        tl_bottom.setupWithViewPager(vp_content);//框架中viewPager中改变当前Fragment时触发tabBottomListener，
        // 改变了图标颜色，但remove了文字，需重新添加，和手动改变底部图片原理相同
    }

    /**
     * 改变选中的 Tab 状态,和其他tab状态
     * @param tab
     */
    private void ChangeTabImg(TabLayout.Tab tab){
        TabLayout.Tab tabOther;
        for(int i=0;i< tl_bottom.getTabCount();i++){
            if(i == tab.getPosition()){
                tl_bottom.getTabAt(i).setIcon(getResources().getDrawable(DataGenerator.mTabResSelected[i]));
                tab.setText(listStringTemp.get(i));
            }else{
                tabOther = tl_bottom.getTabAt(i).setIcon(getResources().getDrawable(DataGenerator.mTabRes[i]));
                tabOther.setText(listStringTemp.get(i));
            }
        }
    }

    /**
     *
     */
    TabLayout.OnTabSelectedListener tabBottomListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if(vp_content != null){//可能还未初始化
                vp_content.setCurrentItem(tab.getPosition());//改变Fragment
            }
            ChangeTabImg(tab);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };
    /**
     *
     */
    FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return listFragmeent.get(position);
        }

        @Override
        public int getCount() {
            return listFragmeent.size();
        }
    };

    /**
     * 初始化即时通讯服务器
     */
    private void initBomb() {
        if( !user.isLogin()){
            return;
        }
        String id = user.getUserId();
        BmobIM.connect(id, new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    LogUtils.e("initBomb", "connect success");
                    toast("connect success");
                } else {
                    LogUtils.e("initBomb", e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BmobIM.getInstance().disConnect();
    }

    /**
     * 检查授权
     */
    private void checkPermission(){
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释,用户选择不再询问时，此方法返回 false。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限,第三个参数是请求码便于在onRequestPermissionsResult 方法中根据requestCode进行判断.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
            //Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            LogUtils.d("checkPermission", "REQUEST_WRITE_EXTERNAL_STORAGE: 已经授权！");
        }
    }
    private void toast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

}
