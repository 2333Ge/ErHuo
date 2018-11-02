package com.erhuo.utils;

import android.util.Log;

public class LogUtils {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WOARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static int level = VERBOSE;
    public static void v(String tag,String msg){
        if(level <= VERBOSE){
            Log.v("二货/" + tag,msg);
        }
    }
    public static void d(String tag,String msg){
        if(level <= VERBOSE){
            Log.d("二货/" +tag,msg);
        }
    }public static void i(String tag,String msg){
        if(level <= VERBOSE){
            Log.i("二货/" +tag,msg);
        }
    }public static void w(String tag,String msg){
        if(level <= VERBOSE){
            Log.w("二货/" +tag,msg);
        }
    }public static void e(String tag,String msg){
        if(level <= VERBOSE){
            Log.e("二货/" +tag,msg);
        }
    }




}
