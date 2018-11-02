package xx.erhuo.bmob;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RobotUtils {

    //json串，<a href="https://www.kancloud.cn/turing/web_api/522992">

    //key
    private static final String REQTYPE = "reqType";//0-文本(默认)、1-图片、2-音频
    private static final String PERCEPTION = "perception";//大key
    private static final     String INPUT_TEXT = "inputText";//文本信息，文本，图片，音频必须选一个
    private static final        String TEXT = "text";

    private static final String USER_INFO = "userInfo";//用户参数,大key
    private static final     String API_KEY = "8ed2a14298554877b7d7e6b2ff5caf96";
    //private static final     String USER_ID = "295450";
    private static final     String API_KEY_NAME = "apiKey";
    private static final     String USER_ID_NAME = "userId";

    //返回Json key
    private static final String INTENT = "intent";
    private static final     String CODE = "code";
    private static final String RESULTS = "results";//数组groupType resultType values
    private static final String GROUP_TYPE = "groupType";
    private static final String RESULT_TYPE = "resultType";
    private static final String VALUES = "values";
    private String valuesKey = "";//values中的key == 获取到的resultType；

    private static final  String url = "http://openapi.tuling123.com/openapi/api/v2";

    public  static final String COLLECTION_ERROR = "COLLECTION_ERROR";
    public static final String NONE_RESPONSE = "NONE_RESPONSE";
    /**
     * 将json串发送出去并获得返回值
     * @param jsonContent
     * @return MessageBean
     */
    public static  MessageBean doPost(String jsonContent){
        InputStream is = null;
        DataOutputStream dos = null;
        OutputStream os = null;
        try {
                URL  urlNet = new URL(url) ;
                HttpURLConnection connection = (HttpURLConnection) urlNet.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestMethod("POST");
                //设置请求属性,发送数据
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                connection.setRequestProperty("Charset", "UTF-8");
                //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
                connection.connect();
                //建立输入流，向指向的URL传入参数
//                dos = new DataOutputStream(connection.getOutputStream());
//                dos.writeBytes(new String(jsonContent.getBytes("GBK"), "UTF-8"));//添加发送数据
//                //Log.i("发送前",URLEncoder.encode(jsonContent,"UTF-8"));
//                dos.flush();

                os = connection.getOutputStream();
                os.write(jsonContent.getBytes());
                os.flush();

                int reqCode = connection.getResponseCode();
                if(reqCode != HttpURLConnection.HTTP_OK){
                //非两百表示网络错误
                    return null;
            }else {
                is = connection.getInputStream();
                String resultJson = getTextFromInputStream(is);
                if(resultJson == null){
                    return null;
                }else{
                    Log.i("resultJson",resultJson);
                   //return getContentFromJson(resultJson);
                    return getMessageBeanFromJson(resultJson);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("doPost",e.toString());
            //return COLLECTION_ERROR;
            return null;
        }finally {
            if(is != null){
                try {
                    is.close();
                    //dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 将要发送的内容拼接成json串
     * @param content
     * @return 返回为空时表示拼接失败
     */
    public static JSONObject transToJson(String content,String userId){
        JSONObject result = new JSONObject();
        try {
            result.put(REQTYPE,0);

            JSONObject perceptionJson = new JSONObject();
            JSONObject InputTextJSon = new JSONObject();
            InputTextJSon.put(TEXT,content);//放入内容
            perceptionJson.put(INPUT_TEXT,InputTextJSon);
            result.put(PERCEPTION,perceptionJson);

            JSONObject userInfoJson = new JSONObject();
            userInfoJson.put(API_KEY_NAME,API_KEY);
            userInfoJson.put(USER_ID_NAME,userId);
            result.put(USER_INFO,userInfoJson);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("transToJson",e.toString());
            return null;
        }
        return result;
    }

    /**
     * 从输入流中读取文本，默认utf-8
     * @param is
     * @return String
     */
    public static String getTextFromInputStream(InputStream is){
        ByteArrayOutputStream bos = null;
        byte[] bytes = new byte[128];
        int len = 0;
        bos = new ByteArrayOutputStream();
        try{
            while((len = is.read(bytes))!= -1){
                bos.write(bytes,0,len);
            }
            return new String(bos.toByteArray(),"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
            Log.d("getTextFromInputStream",e.toString());
            return null;
        }finally {
            if (bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 从json串中读出想要的内容,返回的可能多种类型数据，此处将其组合到一起作为返回值
     * @param str
     * @return String
     */
    public static String getContentFromJson(String str){
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONObject intentJson = jsonObject.getJSONObject(INTENT);
            int code = intentJson.getInt(CODE);
            //输出功能code可用于查询当前出现的错误，但是返回正确的时候code不知
            Log.i("getContentFromJson  " ,"code==" + code);

            JSONArray resultsJson = jsonObject.getJSONArray(RESULTS);
            StringBuilder resultBuilder = new StringBuilder();
            for(int i = 0 ; i<resultsJson.length() ; i++){
                //返回的可能多种类型数据，此处将其组合到一起作为返回值
                JSONObject resultJson = resultsJson.getJSONObject(i);
                String resultType = resultJson.getString(RESULT_TYPE);
                JSONObject valuesJson = resultJson.getJSONObject(VALUES);
                String value = valuesJson.getString(resultType);
                resultBuilder.append(value).append("/n");
            }
            //resultBuilder.deleteCharAt(resultBuilder.length() - 1);
            resultBuilder.delete(resultBuilder.length() - 2,resultBuilder.length());//删除最后一个换行
            return resultBuilder.toString();


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("getContentFromJson",e.toString());
            return null;
        }
    }
    /**
     * 从json串中读出内容,返回的MessageBean
     * @param str
     * @return MessageBean
     */
    public static MessageBean getMessageBeanFromJson(String str){

        MessageBean message = new MessageBean();
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONObject intentJson = jsonObject.getJSONObject(INTENT);
            int code = intentJson.getInt(CODE);
            //输出功能code可用于查询当前出现的错误，但是返回正确的时候code不知
            Log.i("getContentFromJson  " ,"code==" + code);

            JSONArray resultsJson = jsonObject.getJSONArray(RESULTS);
            StringBuilder resultBuilder = new StringBuilder();
            for(int i = 0 ; i<resultsJson.length() ; i++){
                //返回的可能多种类型数据，此处将其组合到一起作为返回值
                JSONObject resultJson = resultsJson.getJSONObject(i);
                String resultType = resultJson.getString(RESULT_TYPE);
                message.setMessageType(resultType);
                JSONObject valuesJson = resultJson.getJSONObject(VALUES);
                String value = valuesJson.getString(resultType);
                resultBuilder.append(value).append("/n");
            }
            resultBuilder.delete(resultBuilder.length() - 2,resultBuilder.length());//删除最后一个换行
            message.setContent(resultBuilder.toString());
            return message;


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("getContentFromJson",e.toString());
            return null;
        }
    }

}
