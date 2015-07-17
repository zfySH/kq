package com.nowagme.football;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.SdcardUtil;
import com.nowagme.util.StringUtil;

public class AppConfig {

	/********************* 参数修改区 start **************************/

	/**
	 * application secret key.
	 */
	private String appSecret = "de3jkfg74slftG$*&a_@";
	/**
	 * host.
	 */
	public final static String host = "http://i.kangeqiu.cn";
	// public final static String host = "http://123.59.68.184:8080";

	/**
	 * normal form action
	 */
	public final static String urlSDK = "/sdk.do";
	/**
	 * special from action which contains file upload.
	 */
	public final static String urlMSDK = "/msdk.do";

	/**
	 * cache direction.
	 */
	private String cacheDir = SdcardUtil.getDir() + "2015kanqiu";
	/**
	 * image cache direction.
	 */
	private String cacheImageDir = cacheDir + File.separator + "images";

	/**
	 * encoding.
	 */
	private String encoding = HTTP.UTF_8;

	/**
	 * 经、纬度地图坐标类型.
	 */
	private String coordType = "bd09ll";

	/********************* 参数修改区 end **************************/

	public static final int FROM_NONE_MESSAGE = -1;// 非消息的跳转.
	public static final String FROM_PARAM_NAME = "from";// 跳转来源参数名
	public static final String FROM_PARAM_KEY = "key";// 跳转来源对应的key

	private int playerId = 0;

	/**
	 * 唯一的实例属性.
	 */
	private static AppConfig instance = null;

	/**
	 * 获取对象.
	 * 
	 * @return
	 */
	public static AppConfig getInstance() {
		if (instance != null)
			return instance;
		synchronized (AppConfig.class) {
			if (instance == null) {
				instance = new AppConfig();
			}
		}
		return instance;
	}

	/**
	 * 不允许使用构造器方法.
	 */
	private AppConfig() {

	}

	/**
	 * 判断图片缓存是否存在.
	 * 
	 * @param url
	 * @return
	 */
	public boolean checkCacheImageExists(String imgFullFileName) {
		File f = new File(imgFullFileName);
		return f.exists();
	}

	/**
	 * 获取图片缓存的文件名（含完整路径）
	 * 
	 * @param url
	 * @return
	 */
	public String makeCacheImageFullFileName(String url) {
		return getCacheImageDir() + File.separator
				+ StringUtil.stringToMD5(url) + StringUtil.getFileExt(url);
	}

	/**
	 * 获取图片缓存目录.
	 * 
	 * @return
	 */
	public String getCacheImageDir() {
		return this.cacheImageDir;
	}

	/**
	 * 获取球员编号.
	 * 
	 * @return
	 */
	public int getPlayerId() {
		return this.playerId;
	}

	/**
	 * 设置球员编号.
	 * 
	 * @return
	 */
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	/**
	 * 加入签名.
	 * 
	 * @param parameters
	 * @throws Exception
	 */
	public void addSign(Map<String, String> parameters) throws Exception {
		String sign = makeSign(parameters);
		parameters.put("app_sign", sign);
	}

	/**
	 * 加入签名.
	 * 
	 * @param parameters
	 * @throws Exception
	 */
	public void addSign(ArrayList<NameValuePair> parameters) throws Exception {
		String sign = makeSign(parameters);
		parameters.add(new BasicNameValuePair("app_sign", sign));
	}

	/**
	 * 生成签名.
	 * 
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	private String makeSign(Map<String, String> parameters) throws Exception {
		String sign = StringUtil.makeSign(appSecret, parameters, encoding);
		return sign;
	}

	/**
	 * new 生成签名.
	 * 
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	private String makeSign(ArrayList<NameValuePair> parameters)
			throws Exception {
		String sign = StringUtil.makeSign(appSecret, parameters, encoding);
		return sign;
	}

	/**
	 * 生成post提交对象.
	 * 
	 * @param parameters
	 * @return
	 */
	public HttpPostUtil makeHttpPostUtil(Map<String, String> parameters)
			throws Exception {
		String url = makeUrl(Integer.parseInt(parameters.get("app_action")));

		Log.i("http", "url:" + url);
		Log.i("http", "parameters:" + parameters.toString());
		HttpPostUtil mHttpPostUtil = new HttpPostUtil(url, encoding, parameters);
		return mHttpPostUtil;
	}

	/**
	 * 根据请求编号获取请求地址.
	 * 
	 * @param actionId
	 * @return
	 */
	public String makeUrl(int actionId) {
		if (actionId == 1003 || actionId == 1006 || actionId == 1023)
			return host + urlMSDK;
		return host + urlSDK;
	}

	/**
	 * 获取经纬度坐标类型.
	 * 
	 * @return
	 */
	public String getCoordType() {
		return coordType;
	}

}
