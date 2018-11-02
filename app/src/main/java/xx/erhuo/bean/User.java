package xx.erhuo.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.erhuo.utils.LogUtils;

public class  User {
	//用户信息发生改变时要更新上一个页面的user
	//

	private boolean isLogin;//登录与否

	private String name;
	private String realName;
	private String qqNum;
	private String email;
	private String address;
	private String userId;
	private int sellNum;
	private int buyNum;
	private boolean sex;

	private String backgroundUrl;
	private String phone;
	private String headUrl;
	private int concernNum;
	private String birthday;

	private static SharedPreferences sp;
	private Editor editor;
	private Context context;
	//{"status":"1","msg":"登录成功","nicheng":"yadid","realname":"杨纪昆",
	// "sex":"male","qq":"2065994853","email":"2065994853@qq.com",
	// "address":"郑州大学新校区柳园","uid":"72","sellnum":"45","buynum":"33",
	// {"image":"http://39.105.0.212/shop/products/bbe9af811f7f3c2f94817e7eba86bf66.jpg",
	// "guanzhu":"23","telephone":"18860351251","birthday":"1995-11-27",
	// "background":"http://39.105.0.212/shop/products/background.jpg"}
	private static final String userSpName = "user";
	private static final String statusKey = "statusKey";
	private static final String nameKey = "nameKey";

	private static final String realNameKey = "realNameKey";
	private static final String qqNumKey = "qqNumKey";
	private static final String emailKey = "emailKey";
	private static final String addressKey = "addresKey";
	private static final String 	userIdKey = "userIdKey";
	private static final String sellNumKey = "sellNumKey";
	private static final String buyNumKey = "buyNumKey";
	private static final String sexKey = "sexKey";

	private static final String backgroundKey = "backgroundKey";
	private static final String phoneKey = "phoneKey";
	private static final String headUrlKey = "imageUrlKey";
	private static final String 	concernNumKey = "concernNumKey";
	private static final String birthdayKey = "birthdayKey";

	public User(Context context){
		this.context = context;
		sp = context.getSharedPreferences(userSpName, Context.MODE_PRIVATE);
		editor = sp.edit();
		getSpStatus();
		initUser(); //和getSpStatus()一起，获得当前状态，并赋值给name和status

	}
	/**初始化用户，获取所有数据*/
	private void initUser() {
		if(isLogin){
			this.name =  sp.getString(nameKey,"二货用户");
			this.realName =  sp.getString(realNameKey,"");
			this.qqNum =  sp.getString(qqNumKey,"");
			this.email =  sp.getString(emailKey,email);
			this.address =  sp.getString(addressKey,"");
			this.userId = sp.getString(userIdKey,null);
			this.sellNum = sp.getInt(sellNumKey,0);
			this.buyNum = sp.getInt(buyNumKey,0);
			this.sex = sp.getBoolean(sexKey,true);

			backgroundUrl = sp.getString(backgroundKey,null);
			phone = sp.getString(phoneKey,"");
			headUrl = sp.getString(headUrlKey,null);
			concernNum = sp.getInt(concernNumKey,0);
			birthday = sp.getString(birthdayKey,"");
		}else{
			LogUtils.e("initUser","尚未登录");
		}
	}

	/**
	 * 首先获取登录状态
	 * */
	public boolean getSpStatus(){
		isLogin = sp.getBoolean(statusKey, false);
		return isLogin;
	}
	public void setSpStatus(boolean isLogin){
		this.isLogin = isLogin;
		putValueInToSp(statusKey,isLogin);
	}

	public void setSpName(String name){
		this.name = name;
		putValueInToSp(nameKey,name == null ? "":name);
	}
	public void setSpRealName(String realName){
		this.realName = realName;
		putValueInToSp(realNameKey,realName == null ? "":realName);
	}
	public void setSpQqNum(String qqNum){
		this.qqNum = qqNum;
		putValueInToSp(qqNumKey,qqNum == null ? "":qqNum);
	}
	public void setSpEmail(String email){
		this.email = email;
		putValueInToSp(emailKey,email == null ? "":email);
	}
	public void setSpAddress(String address){
		this.address = address;
		putValueInToSp(addressKey,address == null ? "":address);
	}

	public void setSpUserId(String userId){
		this.userId = userId;
		putValueInToSp(userIdKey,userId == null ? "":userId);
	}
	public void setSpSellNum(int sellNum){
		this.sellNum = sellNum;
		putValueInToSp(sellNumKey,sellNum);
	}
	public void setSpBuyNum(int buyNum){
		this.buyNum = buyNum;
		putValueInToSp(buyNumKey,buyNum);
	}
	public void setSpSex(boolean sex){
		this.sex = sex;
		putValueInToSp(sexKey,sex);
	}
	public void setSpBirthday(String birthday){
		this.birthday = birthday;
		putValueInToSp(birthdayKey,birthday);
	}
	public void setSpPhone(String phone){
		this.phone = phone;
		putValueInToSp(phoneKey,phone);
	}
	public void setSpHeadUrl(String headUrl){
		this.headUrl = headUrl;
		putValueInToSp(headUrlKey,headUrl);
	}
	public void setSpBackgroundUrl(String url){
		this.backgroundUrl = url;
		putValueInToSp(backgroundKey,backgroundUrl);
	}
	public void setSpConcernNum(int concernNum){
		this.concernNum = concernNum;
		putValueInToSp(concernNumKey,concernNum);
	}






	public String getName() {
		return name;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public String getRealName() {
		return realName;
	}

	public String getQqNum() {
		return qqNum;
	}

	public String getEmail() {
		return email;
	}

	public String getAddress() {
		return address;
	}

	public String getUserId() {
		return userId;
	}

	public String getBackgroundUrl() {
		return backgroundUrl;
	}

	public String getPhone() {
		return phone;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public int getConcernNum() {
		return concernNum;
	}

	public String getBirthday() {
		return birthday;
	}

	public int getSellNum() {
		return sellNum;

	}

	public int getBuyNum() {
		return buyNum;
	}

	public boolean isSex() {
		return sex;
	}

	/**存放数据于SharedPreferences*/
	private void putValueInToSp(String key, String value){
		editor.putString(key, value);
		editor.commit();
	}
	private void putValueInToSp(String key,boolean value){
		editor.putBoolean(key, value);
		editor.commit();
	}
	private void putValueInToSp(String key,int value){
		editor.putInt(key, value);
		editor.commit();
	}


}
