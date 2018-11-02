package com.erhuo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHelper {
	public  static final String RECENTLY = "recently";
	
	/**时间，long转换为String*/
	public static String timeLongToString(long timeLong){
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(timeLong);
		String stime = sdf.format(date);
		//System.out.println(str);
		return stime;
	}
	/**
	 * 时间,String转换为long
	 * */
	public static long timeStringToLong(String stime) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		date = sdf.parse(stime);
		return date.getTime();
}

	/**
	 * 将时间转为日常使用的形式
	 * @param timeLast
	 * @return String
	 */
	public static String longToLocalTime(long timeLast){
		StringBuilder resultBuilder = new StringBuilder();
		Date dateLast = new Date(timeLast);
		Date dateNow = new Date();

		if(dateLast.getYear() != dateNow.getYear() ){
			return timeLongToString(timeLast);
		}else if(dateLast.getMonth() != dateNow.getMonth()){
			SimpleDateFormat sdf= new SimpleDateFormat("MM月dd HH:mm");
			Date date = new Date(timeLast);
			String stime = sdf.format(date);
			return stime;
		}else{
			int dayLast = dateLast.getDay();
			int dayNow = dateNow.getDay();
			int hour;
			int minute;
			String hourStr;
			String minuteStr;
			if(dayLast == dayNow){
				SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
				Date date = new Date(timeLast);
				String stime = sdf.format(date);
				return stime;
			}else if ((dayNow - dayLast) == 1){//代码不够简洁
				hour = dateLast.getHours();
				hourStr = (hour < 10) ? "0" + hour : hour+"";
				minute = dateLast.getMinutes();
				minuteStr = (minute < 10) ? "0" + minute : minute+"";
				resultBuilder.append("昨天 ").
						append(hourStr).
						append(":").
						append(minuteStr);
				return resultBuilder.toString();
			}else if ((dayNow - dayLast) == 2){
				hour = dateLast.getHours();
				hourStr = (hour < 10) ? "0" + hour : hour+"";
				minute = dateLast.getMinutes();
				minuteStr = (minute < 10) ? "0" + minute : minute+"";
				resultBuilder.append("前天 ").
						append(hourStr).
						append(":").
						append(minuteStr);
				return resultBuilder.toString();
			}else{
				SimpleDateFormat sdf= new SimpleDateFormat("MM月dd HH:mm");
				Date date = new Date(timeLast);
				String stime = sdf.format(date);
				return stime;
			}
		}

	}

	/**
	 * 失败品
	 * @param lastTime
	 * @param thisTime
	 * @return
	 */
	public static boolean isBelow3Minutes(long lastTime,long thisTime){
		Date dateLast = new Date(lastTime);
		Date dateThis = new Date(thisTime);
		if(dateLast.getYear() == dateThis.getYear() &&
				dateLast.getMonth() == dateThis.getMonth() &&
				dateLast.getDay() == dateThis.getDay() &&
				dateLast.getHours() == dateThis.getHours() &&
				(dateThis.getMinutes() - dateLast.getMinutes()) <= 3)
			return true;

		int spaceTime = dateThis.getMinutes()+60 - dateLast.getMinutes();
		if(dateLast.getYear() == dateThis.getYear() &&
				dateLast.getMonth() == dateThis.getMonth() &&
				dateLast.getDay() == dateThis.getDay() &&
				dateLast.getHours() == dateThis.getHours()-1 &&
				spaceTime <=3 )//刚好跨小时
			return true;
		//跨天
		//月
		//年
		return false;
	}

	/**
	 * 两个时间小于4分钟
	 * @param lastTime
	 * @param thisTime
	 * @return
	 */
	public static boolean isBelow4Minutes(long lastTime,long thisTime){
		Date dateLast = new Date(lastTime);
		Date dateThis = new Date(thisTime);
		long yy=dateThis.getTime()-dateLast.getTime();
		int time=new Long(yy/1000/60).intValue();
		if(time < 4){
			return true;
		}else{
			return false;
		}
	}
	public static Long getTimeNow(){
		Date date = new Date();
		return date.getTime();

	}
}
