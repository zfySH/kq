package com.nowagme.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtil {
	
	
	/**
	 * 获取当前日期和时间.
	 * @return
	 */
	public static String date2String(){
		return date2String("yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 获取当前日期和时间.
	 * @return
	 */
	public static String date2String(String format){
		Calendar c = Calendar.getInstance();
		String s = new SimpleDateFormat(format,Locale.getDefault()).format(c.getTime());
		return s;
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("test");
	}

}
