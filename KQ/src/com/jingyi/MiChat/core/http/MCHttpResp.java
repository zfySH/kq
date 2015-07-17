package com.jingyi.MiChat.core.http;

import org.json.JSONObject;

import android.graphics.Bitmap;

public class MCHttpResp {

	private boolean success;
	private String mData;
	private Bitmap mQDBitmap;
	/**
	 * ���Code����0����ô����HTTP��CODE�����С��0�������Զ���Ĵ���CODE
	 */
	public int Code;
	private int mLoadType;// 判断是网络返回的，还是cache返回的

	public final static int ERROR_SOCKET_TIMEOUT = -10000;
	public final static int ERROR_UNKNOWN = -10001;
	public final static int ERROR_POST_DATA_NULL = -10002;
	public final static int ERROR_UNSUPPORT_ENCODING = -10003;
	public final static int ERROR_NO_CONNECTION = -10004;
	public final static int ERROR_EMPTY_URL = -10005;
	public final static int ERROR_JSON = -10006;
	public final static int ERROR_ENTITY_IS_NULL = -10007;
	public final static int ERROR_UNKNOWN_HOST = -10008;
	public final static int ERROR_CONTENT_TYPE = -10009;
	public final static int ERROR_UPLOAD_ERROR = -10010;
	private String mBitmapUrl;

	/**
	 * 没加载到
	 */
	public final static int LOAD_TYPE_ERROR = 0;
	/***
	 * 从缓存里加载到的
	 */
	public final static int LOAD_TYPE_FROM_CACHE = 1;
	/***
	 * 从网络里加载到的
	 */
	public final static int LOAD_TYPE_FROM_NETWORK = 2;

	public MCHttpResp() {
	}

	public MCHttpResp(boolean isSuccess) {
		success = isSuccess;
	}

	public MCHttpResp(boolean isSuccess, int code) {
		success = isSuccess;
		Code = code;
	}

	public MCHttpResp(boolean isSuccess, int code, int loadType, String data) {
		success = isSuccess;
		Code = code;
		mData = data;
		mLoadType = loadType;
	}

	public MCHttpResp(boolean isSuccess, Bitmap bitmap, String url) {
		success = isSuccess;
		mQDBitmap = bitmap;
		mBitmapUrl = url;
	}
	
	public String getBitmapUrl(){
		return mBitmapUrl;
	}
	/***
	 * �Ƿ��̵�������
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return success && ((mData != null && mData.length() > 0) || (mQDBitmap != null && !mQDBitmap.isRecycled()));
	}
	
	public boolean getSuccessVariable(){
		return success;
	}

	public int getCode() {
		return Code;
	}

	public String getData() {
		return mData;
	}

	public JSONObject getJson() {
		try {
			return new JSONObject(mData);
		} catch (Exception e) {
			e.printStackTrace();
			Code = ERROR_JSON;
			return null;
		}
	}

	/***
	 * 获取加载类型，0为未初始化，1为从缓存里加载到，2为从网络里加载到
	 * 
	 * @return
	 */
	public int getLoadType() {
		return mLoadType;
	}

	/***
	 * 是否捞到了Json内容
	 * 
	 * @return
	 */
	public boolean isJson() {
		if (!isSuccess())
			return false;
		try {
			new JSONObject(mData);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Code = ERROR_JSON;
			return false;
		}
	}

	public Bitmap getBitmap() {
		return mQDBitmap;
	}

	/***
	 * 获取错误提示信息
	 * 
	 * @return
	 */
	public String getErrorMessage() {
		if (Code == ERROR_SOCKET_TIMEOUT) {
			return "网络连接超时，请检查网络(" + Code + ")";
		} else if (Code == ERROR_UNKNOWN) {
			return "网络异常，请检查网络(" + Code + ")";
		} else if (Code == ERROR_POST_DATA_NULL) {
			return "参数错误，请重试(" + Code + ")";
		} else if (Code == ERROR_UNSUPPORT_ENCODING) {
			return "不支持的编码格式(" + Code + ")";
		} else if (Code == ERROR_NO_CONNECTION) {
			return "请检查网络是否已经打开(" + Code + ")";
		} else if (Code == ERROR_EMPTY_URL) {
			return "您请求的地址不存在或为空，请检查后重试(" + Code + ")";
		} else if (Code == ERROR_JSON) {
			return "数据解析错误(" + Code + ")";
		}
		return "加载失败，请检查网络(" + Code + ")";
	}

}
