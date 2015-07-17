package com.nowagme.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.NameValuePair;

public class StringUtil {

	/**
	 * 生成api调用所需的签名.
	 * 
	 * @param appSecret
	 *            分配给每个应用的app_secret号码.
	 * @param hm
	 * @param charset
	 *            一般为UTF-8
	 * @return
	 * @throws Exception
	 */
	public static String makeSign(String appSecret, Map<String, String> hm,
			String charset) throws Exception {
		List<String> keys = new ArrayList<String>(hm.keySet());
		Collections.sort(keys);
		StringBuffer content = new StringBuffer();
		content.append(appSecret);
		for (int i = 0; i < keys.size(); i++) {
			content.append(keys.get(i));
			content.append(hm.get(keys.get(i)));
		}
		MessageDigest md = MessageDigest.getInstance("MD5");// md5加密
		return byte2hex(md.digest(content.toString().getBytes(charset)));
	}

	/**
	 * 生成api调用所需的签名.
	 * 
	 * @param appSecret
	 *            分配给每个应用的app_secret号码.
	 * @param hm
	 * @param charset
	 *            一般为UTF-8
	 * @return
	 * @throws Exception
	 */
	public static String makeSign(String appSecret,
			ArrayList<NameValuePair> hm, String charset) throws Exception {
		List<String> keys = new ArrayList<String>();
		// List<String> values = new ArrayList<String>();
		for (int j = 0; j < hm.size(); j++) {
			keys.add(hm.get(j).getName());
			// values.add(hm.get(j).getValue());
		}
		Collections.sort(keys);
		StringBuffer content = new StringBuffer();
		content.append(appSecret);
		for (int i = 0; i < keys.size(); i++) {
			content.append(keys.get(i));
			for (int k = 0; k < hm.size(); k++) {
				if (keys.get(i).equals(hm.get(k).getName())) {
					content.append(hm.get(k).getValue());
				}
			}

		}
		MessageDigest md = MessageDigest.getInstance("MD5");// md5加密
		return byte2hex(md.digest(content.toString().getBytes(charset)));
	}

	/**
	 * 2进制转16进制.
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		if (b == null)
			return "";
		String stmp = "";
		StringBuffer ret = new StringBuffer();
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1) {
				ret.append("0");
				ret.append(stmp);
			} else {
				ret.append(stmp);
			}
		}
		return ret.toString().toUpperCase(Locale.getDefault());
	}

	/**
	 * 将字符串转为md5字符串.
	 * 
	 * @param string
	 * @return
	 */
	public static String stringToMD5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	/**
	 * 从文件名字符串中获取文件后缀名.
	 * 文件名字符串可以是http://xxx.xxx.xxx/aaa.jpg，也可以是/aa/bbb/ccc.txt，还可以是xxxyyy.doc.
	 * 总之只要字符串的最后是文件名就可以.
	 * 
	 * @param fileStr
	 * @return
	 */
	public static String getFileExt(String fileStr) {
		if (fileStr == null)
			return null;
		fileStr = fileStr.trim();
		if (fileStr.length() == 0)
			return null;
		int lastIndex = fileStr.lastIndexOf('.');
		int last1 = fileStr.lastIndexOf('/');
		int last2 = fileStr.lastIndexOf('\\');
		if ((last1 > -1 && lastIndex < last1)
				|| (last2 > -1 && lastIndex < last2))
			return "";
		return fileStr.substring(lastIndex);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
