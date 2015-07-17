package com.jingyi.MiChat.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Time;

public class StringUtil {

	public static String FixString(String src, int len, boolean isAddDel) {
		if (src.length() <= len)
			return src;
		return src.substring(0, len) + (isAddDel ? "..." : "");
	}

	/**
	 * 判断字符串是否为空字符串
	 * 
	 * @param str
	 * @return 如果str为NULL或者空字符串，返回true，否则返回false
	 */
	public static boolean isBlank(String str) {
		if (str == null || str.trim().length() == 0)
			return true;
		return false;
	}

	public static String GetUrlDomain(String url) {
		int startpos = url.indexOf("://") + 3;
		int endpos = url.substring(startpos).indexOf("/");
		return url.substring(startpos).substring(0, endpos);
	}

	public static String FixTime(long time) {
		long n = System.currentTimeMillis() - time;
		if (n < (long) 1000l * 60) {
			return "1分钟内";
		} else if (n < (long) 1000l * 60 * 60) {
			long min = n / (long) (1000l * 60);
			return min + "分钟前";
		} else if (n < (long) 1000l * 60 * 60 * 24) {
			long hour = n / (long) (1000l * 60 * 60);
			return hour + "小时前";
		} else if (n < (long) 1000l * 60 * 60 * 24 * 30) {
			long day = n / (long) (1000l * 60 * 60 * 24);
			return day + "天前";
		}
		// else if (n < (long) 1000l * 60 * 60 * 24 * 30 * 12)
		// {
		// long month = n / (long) (1000l * 60 * 60 * 24 * 30);
		// return month + "月前";
		// }
		else {
			return "";
		}
	}

	public static String FixTime2(long time) {
		long n = time - System.currentTimeMillis();
		if (n > (long) (1000l * 60 * 60 * 24)) {
			long day = n / (long) (1000l * 60 * 60 * 24);
			return day + "天后";
		} else if (n > (long) (1000l * 60 * 60)) {
			long hour = n / (long) (1000l * 60 * 60);
			return hour + "小时后";
		} else if (n > (long) (1000l * 60)) {
			long min = n / (long) (1000l * 60);
			return min + "分钟后";
		}
		return "已过期";
	}

	public static String FixTime4(long time) {
		long n = time - System.currentTimeMillis();
		if (n <= (long) (1000l * 60 * 60 * 24 * 7)) {
			long day = n / (long) (1000l * 60 * 60 * 24);
			return day + "";
		}
		return null;
	}

	public static String FixTime3(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		java.util.Date dt = new Date(time);
		String sDateTime = sdf.format(dt); // 得到精确到秒的表示：08/31/2006 21:08:00
		return sDateTime;
	}

	public static String FixTime5(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		java.util.Date dt = new Date(time);
		String sDateTime = sdf.format(dt); // 得到精确到秒的表示：08/31/2006 21:08:00
		return sDateTime;
	}

	public static String formatDuring(long mss) {

		long days = 0;
		long hours = 0;
		long minutes = 0;
		long seconds = 0;
		if (mss > 0) {
			days = mss / (1000 * 60 * 60 * 24);
			hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
			minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
			seconds = (mss % (1000 * 60)) / 1000;
		}

		return days + "天" + hours + "小时" + minutes + "分" + seconds + "秒";
	}

	public static String formatLimitFreeDuring(long mss) {
		long hours = 0;
		long minutes = 0;

		if (mss > 0) {
			hours = mss / (1000 * 60 * 60);
			minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		}
		return hours + "小时" + minutes + "分";
	}

	public static String formatDuring(long begin, long end) {
		return formatDuring(end - begin);
	}

	public static String FixWords(int words) {
		if (words > 10000) {
			return (words / 10000) + "万字";
		}
		return words + "字";
	}

	public static String FixClickCount(long count) {
		if (count > 100000000) {
			return "<font color='#cb0b3e'>" + (count / 100000000) + "亿</font>";
		}
		if (count > 10000) {
			return "<font color='#cb0b3e'>" + (count / 10000) + "万</font>";
		}
		return "<font color='#cb0b3e'>" + String.valueOf(count) + "</font>";
	}

	public static String FixClickCount1(long count) {
		if (count > 100000000) {
			return "<font color='#ffffff'>" + (count / 100000000) + "亿</font>";
		}
		if (count > 10000) {
			return "<font color='#ffffff'>" + (count / 10000) + "万</font>";
		}
		return "<font color='#ffffff'>" + String.valueOf(count) + "</font>";
	}

	public static String FixClickCount2(long count) {
		if (count > 100000000) {
			return (count / 100000000) + "亿";
		}
		if (count > 10000) {
			return (count / 10000) + "万";
		}
		return String.valueOf(count);
	}

	public static String FixInteractionCount(long count) {
		if (count > 100000000) {
			return (count / 100000000) + "亿";
		}
		if (count > 10000) {
			return (count / 10000) + "万";
		}
		return String.valueOf(count);
	}

	public static String fixStr(String str) {
		String[] removeStr = {"\r", "<br>", "　", "</br>", "\r<br>"};
		return trans(str, removeStr);

	}

	public static String fixTimeForMsg(long time) {
		SimpleDateFormat sdf;
		java.util.Date dt = new Date(time);
		String sDateTime;
		long today = System.currentTimeMillis();
		long yes = today - 24 * 60 * 60 * 1000;
		long yesBeyes = today - 24 * 60 * 60 * 1000 * 2;
		if (isToday(time, today)) {
			sdf = new SimpleDateFormat("HH:mm");
			sDateTime = sdf.format(dt);
			return sDateTime;
		} else if (isToday(time, yes)) {
			sdf = new SimpleDateFormat("HH:mm");
			sDateTime = sdf.format(dt);
			return "昨天 " + sDateTime;
		} else if (isToday(time, yesBeyes)) {
			sdf = new SimpleDateFormat("HH:mm");
			sDateTime = sdf.format(dt);
			return "前天 " + sDateTime;
		} else {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			sDateTime = sdf.format(dt);
			return sDateTime;
		}
	}

	private static boolean isToday(long when, long today) {
		Time time = new Time();
		time.set(when);

		int thenYear = time.year;
		int thenMonth = time.month;
		int thenMonthDay = time.monthDay;

		time.set(today);
		return (thenYear == time.year) && (thenMonth == time.month) && (thenMonthDay == time.monthDay);
	}

	private static String trans(String str, String[] removeStr) {
		for (String rs : removeStr) {
			str = str.replaceAll(rs, "");
		}
		// System.out.println("str==========="+str);
		return str;

	}

	public static String fixNewStr(String str) {
		String[] reStr = {"&nbsp;", " "};
		for (String rs : reStr) {
			str = str.replaceAll(rs, "");
		}
		return str;
	}

	public static String nbspToSpace(String str) {
		String[] reStr = {"&nbsp;"};
		for (String rs : reStr) {
			str = str.replaceAll(rs, " ");
		}
		return str;
	}

	public static String splitStr(String str) {
		if (str.length() > 80)
			return str.substring(0, 80);
		else
			return str;
	}

	public static String getTime(long l) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("MM-dd");
		Date d = new Date(l);
		return sDateFormat.format(d);

	}

	public static String fixTime(long l) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date d = new Date(l);
		return sDateFormat.format(d);

	}

	public static String fixTime01(long l) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date d = new Date(l);
		return sDateFormat.format(d);

	}

	public static String formatDate(Date mDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(mDate);
	}

	public static String formatData02(Date mData) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(mData);
	}

	public static String formatData03(Date mData) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return df.format(mData);
	}

	public static String getDate(String timeStr) {
		Date date = new Date(Long.parseLong(timeStr.trim()));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		String dateString = formatter.format(date);
		return getIntTime(dateString);
	}

	/**
	 * 得到yyyy-MM-dd格式时间
	 * 
	 * @return
	 */
	public static String getDate02(String timeStr) {
		if (timeStr.equals("")) {
			return "";
		}
		Date date = new Date(Long.parseLong(timeStr.trim()));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		return dateString;
	}

	private static String getIntTime(String time) {
		String timeStr[] = time.split("-");
		String timeInt = timeStr[0] + timeStr[1] + timeStr[2];
		return timeInt;
	}

	private static String getIntTime02(String time) {
		String timeStr[] = time.split("-");
		String timeInt = timeStr[0] + "年" + timeStr[1] + "月";
		return timeInt;
	}

	/**
	 * 
	 * <p>
	 * Title: getYearAndMonth
	 * </p>
	 * <p>
	 * Description: 获取类似2015年04月这样的字符
	 * </p>
	 * 
	 * @param time
	 * @return
	 */
	public static String getYearAndMonth(long time) {
		return getIntTime02(fixTime(time));
	}

	/**
	 * 得到系统当前时间
	 * 
	 * @return
	 */
	public static String getNowTime() {
		Date dataTime = new Date();
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleFormat.format(dataTime);
	}

	/**
	 * 得到yyyy-MM-dd格式时间
	 * 
	 * @return
	 */
	public static String getDateTwo(String timeStr) {
		Date date = new Date(Long.parseLong(timeStr.trim()));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		return dateString;
	}

	/**
	 * 查找字符串中包含了另�?个字符串几次
	 * 
	 * @param s
	 * @param toFind
	 * @return
	 */
	public static int findStrContainsCount(String s, String toFind) {
		if (s == null || s.length() == 0)
			return 0;
		int index = 0;
		int count = 0;
		while (index != -1) {
			if (s == null || s.length() == 0)
				break;
			index = s.indexOf(toFind);
			if (index == -1)
				break;
			int length = toFind.length();
			s = s.substring(index + length);
			count++;
		}
		return count;
	}

	public static String getStr(Context ctx, int id) {
		return ctx.getString(id);
	}

	public static String filterString(String s) {
		return s.replaceAll("<br>", "\r\n").replaceAll("&nbsp;", "").replaceAll("�?", "").replaceAll("<b>", "").replaceAll("</b>", "");
	}

	public static String getPercent(int x, int total) {

		String result = "";// 接受百分比的�?
		double x_double = x * 1.0;
		double tempresult = x_double / total;
		// NumberFormat nf = NumberFormat.getPercentInstance();
		// nf.setMinimumFractionDigits( 2 ); 保留到小数点后几�?
		DecimalFormat df1 = new DecimalFormat("0.00%"); // ##.00%
														// 百分比格式，后面不足2位的�?0补齐
														// result=nf.format(tempresult);
		result = df1.format(tempresult);
		return result;
	}

	public static String getStringByLength(String s, int length) {
		if (TextUtils.isEmpty(s)) {
			return "";
		}
		if (s.length() <= length) {
			return s;
		}
		return s.substring(0, length);
	}

	public static String[] filterUrl(Object object) {
		String[] strs = ((String) object).split(",");
		// ["http:\/\/fast-cdn.dianjoy.com\/dev\/upload\/ad_url\/201405\/0_f5d776257701262c807d2e6ca3e4e7bc_h_400.jpg"
		// "http:\/\/fast-cdn.dianjoy.com\/dev\/upload\/ad_url\/201406\/0_e6bc59ce5d385feed850b38621b82f9f_h_400.jpeg"]
		String[] desStrs = new String[strs.length];
		for (int i = 0; i < strs.length; i++) {
			desStrs[i] = strs[i].replaceAll("[\\\\\\[\\]\"]", "");
		}
		return desStrs;
	}

	public static boolean equalsIgnoreCase(String str1, String str2) {
		if (str1 == null && str2 != null) {
			return false;
		} else if (str1 != null && str2 == null) {
			return false;
		} else if (str1 == null && str2 == null) {
			return true;
		}
		return str1.equalsIgnoreCase(str2);
	}

	public static String fixNullStringToEmpty(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}

	/**
	 * 
	 * <p>
	 * Title: decimalFormat
	 * </p>
	 * <p>
	 * Description: 2个double相处,保留2位小数点
	 * </p>
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static String decimalFormat(double a, double b) {
		DecimalFormat df = new DecimalFormat("0.00");
		if (b == 0)
			return "";
		final double c = a / b;
		return df.format(c);

	}

	/**
	 * 去除字符串中的所有空格
	 * 
	 * @param string
	 * @return
	 */
	public static String removeBlankSpace(String string) {
		return string.replaceAll("\\s*", "");
	}

	public static boolean EmailFormat(String email) {// 邮箱判断正则表达式
		Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher mc = pattern.matcher(email);
		return mc.matches();
	}


}
