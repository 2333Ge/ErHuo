package com.erhuo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import xx.erhuo.images.Util;

public class HttpUtils {
    //自定义使用
    public interface HttpCallBackListener{
        void onFinish(String response);
        void onError(Exception e);

    }
    public static final String imgCacheDir = "imgCacheDir";
    /**
     * 使用okHTTP发送post请求，重写返回回调函数，注意key和content长度需一致
     * @param address
     * @param key
     * @param value
     * @param callback
     */
    public static void sendOkHttpRequest(String address, String[]key, String[] value, Callback callback){
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        FormBody.Builder bulider = new FormBody.Builder();
        if(key.length != value.length){
            LogUtils.e("sendOkHttpRequest","key.length != content.length");
        }
        for(int i = 0 ; i<key.length ; i++){
            bulider.add(key[i],value[i]);
        }
        RequestBody body = bulider.build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
    /**
     * 使用okHTTP发送post请求，重写返回回调函数，注意key和content长度需一致
     * @param address
     * @param callback
     */

    public static void sendOkHttpRequest(String address, Callback callback){
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 重载，只有一个key和value时候
     * @param address
     * @param key
     * @param value
     * @param callback
     */
    public static void sendOkHttpRequest(String address, String key,String value,Callback callback){
        sendOkHttpRequest(address,new String[]{key},new String[]{value},callback);
    }
    /**给定文件夹名，创建并返回文件缓存地址(内存)*/
    public static String getCache(String forderName){
        /*
        * ？Environment.getDownloadCacheDirectory();失败，权限不够
        * */
        File file = new File(MyApplication.getContext().getCacheDir(),forderName);
        if( !file.exists()){
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    public static boolean isImgUrl(String url){
        if(url == null){
            return false;
        }
        String pStr = ".*/.*";
        Pattern p = Pattern.compile(pStr);
        Matcher m = p.matcher(url);
        if (m.matches()){
            return true;
        }
        return false;
    }
    /**
     *
     * @param imgUrl
     * @return
     */
    public static Bitmap getBitMapFromUrl(String imgUrl){
        //正则匹配地是否合法
        if ( !isImgUrl(imgUrl)){
            return null;
        }
        LogUtils.e("imgUrl=====",imgUrl);
        String fileName = imgUrl.substring(imgUrl.lastIndexOf("/"));
//        LogUtils.e("fileName====",fileName);
        File file = new File(getCache(imgCacheDir) + fileName);
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            URL url;
            InputStream in = null;
            FileOutputStream fos = null;
            try {
                url = new URL(imgUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //设置请求方法，注意大写
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.connect();
                if(conn.getResponseCode() == 200) {//200成功
                    //获取服务器响应头中的流，流里的数据即是客户端请求的数据
                    in = conn.getInputStream();
                    //存入缓存
                    fos = new FileOutputStream(file);
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = in.read(b)) != -1) {
                        fos.write(b, 0, len);
                        fos.flush();
                        //这个方法的作用是把缓冲区的数据强行输出。如果你不flush就可能会没有真正输出
                        //没有flush不代表它就没有输出出，只是可能没有完全输出。调用flush是保证缓存清空输出
                    }
                    return BitmapFactory.decodeFile(file.getAbsolutePath());
                }
            } catch (Exception e) {
                LogUtils.e("",e.toString());
                e.printStackTrace();
            }finally {
                if(in != null){
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 设置ImageView线程，待优化，线程池+防止内存泄漏
     * @param imageView
     * @param imgUrl
     */
    public static void setImageView( ImageView imageView,String imgUrl){
        if (imgUrl != null && !imgUrl.equals("null")){
            new SetImageTask(imageView).execute(imgUrl);
        }
    }


    static class SetImageTask extends AsyncTask<String,Integer,Bitmap>{
        ImageView imageView;
        SetImageTask(ImageView imageView){
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            String imgUrl = strings[0];
            return getBitMapFromUrl(imgUrl);

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null){
                Bitmap b = Util.zoomBitmap(bitmap,400,400);
                imageView.setImageBitmap(b);
            }else{
                LogUtils.e("SetImageTask","bitmap == null");
            }
        }
    }
        /*
首先这是个lint警告，既然是警告，那在某种程度上忽略不管也是没什么大问题的。
但这里这个警告不建议忽略，因为TextView传进来之前，必定是初始化过的，初始化是需要Context的。
个人猜测你的TextView应该是某个Activity上的，那么你的Context就是这个Activity，而AsyncTask是异步子线程，
与Activity所在的UI线程没啥联系。
当你把Activity的Context给了TextView，TextView又给了AsyncTask，这样就导致子线程间接持有了Activity的引用。
当AsyncTask执行耗时操作时，你把这个Activity关了，GC发现Activity的还被AsyncTask捏着在，不敢回收内存，就容易出现内存泄漏......*/



}
