package com.erhuo.utils;

import android.app.Application;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import xx.erhuo.bean.User;
import xx.erhuo.bmob.MessageDBUtils;

public class MyApplication extends Application {
    private static Context context;
    private static User user;
    private static MessageDBUtils messageDBUtils;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        user = new User(context);
        messageDBUtils = new MessageDBUtils(context);
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            BmobIM.init(this);
            BmobIM.registerDefaultMessageHandler(new BombMessageHandler());
        }
    }
    public static Context getContext(){
        return context;
    }

    public static User getUser() {
        return user;
    }

    public static MessageDBUtils getMessageDBUtils(){return messageDBUtils;};
    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
