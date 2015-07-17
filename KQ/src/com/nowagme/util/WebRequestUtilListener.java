package com.nowagme.util;

import java.util.Map;

/**
 * WEB request请求的回调.
 * @author zjq
 *
 */
public abstract class WebRequestUtilListener{
	/**
	 * 调用完成并得到正确结果.
	 */
	public abstract void onSucces(Map<String, Object> data);
	
	/**
	 * 调用完成并得到失败结果.
	 */
	public abstract void onFail(Map<String, Object> data);
	
	/**
	 * 调用失败(比如网络不通).
	 */
	public abstract void onError();
}
