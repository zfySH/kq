package com.jingyi.MiChat.core.http;


public class MCHttpCallBack {

	
	public void onError(MCHttpResp resp) {}
	
	/****
	 * 开始网络请求前，在线程中执行
	 */
	public void beforeStart() {}
	
	/****
	 * 在UI线程里执行
	 */
	public void onStart() {}
	
	/***
	 * 加载成功后，在线程里执行
	 * @param resp
	 */
	public void beforeSucess(MCHttpResp resp) {}
	
	/***
	 * 加载成功后，在UI线程里执行
	 * @param resp
	 */
	public void onSuccess(MCHttpResp resp) {}
	public void onLoading(long count, long current) {}
	public void onStop() {}
	
	public void onLogin(){}

}
