package xx.erhuo.com.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import xx.erhuo.bean.CommodityCommand;

public class CommadUtils {
    public static  final  int TRANS_FAIL  = 0;
    public static  final  int TRANS_SUCCESS  = 1;
    public static int setListCommandFromJson(List<CommodityCommand> commandList,String jsonStr){
        //{"image":"http://39.105.0.212/shop/products/28509bd9d122410b8984435f1a7641f9.jpg",
        // "pname":"大学化学类，厚的20，薄的15，仅限新区","nowPrice":"4.0","desc":"大学化学类，厚的20，薄的15，仅限新区","pid":"1","date":"2018-6-25 8:01:29"}
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            CommodityCommand command;
            JSONObject jsonObject;
            for(int i = 0 ; i<jsonArray.length() ; i++){
                jsonObject = jsonArray.getJSONObject(i);
                command = new CommodityCommand();
                command.setId(jsonObject.getString("pid"));
                command.setPrice(jsonObject.getString("nowPrice"));
                command.setImgUrl(jsonObject.getString("image"));
                command.setTitle(jsonObject.getString("pname"));
                command.setTime(jsonObject.getString("date"));
                commandList.add(command);
            }
            return TRANS_SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            return TRANS_FAIL;
        }

    }
}
