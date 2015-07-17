package com.nowagme.util;

import java.util.ArrayList;
import java.util.Map;

import org.apache.http.NameValuePair;

import android.os.AsyncTask;

import com.jingyi.MiChat.core.http.HttpUtil;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.thread.MCThreadPool;
import com.nowagme.football.AppConfig;

public class WebRequestUtil {

	/**
	 * WEB请求.
	 */
	private HttpPostUtil request;

	public WebRequestUtil() {

	}

	public void setLazyLoad(boolean isLazy) {

	}

	public WebRequestUtil(HttpPostUtil request) {
		this.request = request;
	}

	/**
	 * 执行.
	 * 
	 * @param webRequestUtilListener
	 */
	public void executeWithOutCache(
			WebRequestUtilListener webRequestUtilListener) {
		new WebRequestTask(webRequestUtilListener).execute(request);
	}

	public void execute(boolean isLazyLoad, String makeUrl,
			ArrayList<NameValuePair> pair, MCHttpCallBack listener) {
		HttpUtil httpUtil = new HttpUtil();
		httpUtil.setIsLazyLoad(isLazyLoad);
		httpUtil.setPriority(MCThreadPool.PRIORITY_MEDIUM);
		httpUtil.setSaveLog(true);
		try {
			AppConfig.getInstance().addSign(pair);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpUtil.post(makeUrl, pair, listener);
	}

	/**
	 * execute by AsyncTask.
	 * 
	 * @author zjq
	 * 
	 */
	class WebRequestTask extends
			AsyncTask<HttpPostUtil, Integer, WebCallResultUtil> {

		private WebRequestUtilListener webRequestUtilListener;

		public WebRequestTask(WebRequestUtilListener webRequestUtilListener) {
			this.webRequestUtilListener = webRequestUtilListener;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		@Override
		protected void onPostExecute(WebCallResultUtil result) {
			String responseText = result.getResponseText();
			if (result.isCallRight()) {
				try {
					Map<String, Object> data = JsonUtil.parse(responseText);
					String result_code = (String) data.get("result_code");
					if ("0".equals(result_code)) {
						webRequestUtilListener.onSucces(data);
					} else {
						webRequestUtilListener.onFail(data);
					}
				} catch (Exception e) {
					e.printStackTrace();
					webRequestUtilListener.onError();
				}
			} else {
				webRequestUtilListener.onError();
			}
		}

		@Override
		protected WebCallResultUtil doInBackground(HttpPostUtil... args) {
			HttpPostUtil httpPostUtil = args[0];
			String message = null;
			WebCallResultUtil mWebCallResultUtil = new WebCallResultUtil();
			try {
				boolean isOK = httpPostUtil.submit();
				if (isOK) {
					message = httpPostUtil.getResponseText();
					mWebCallResultUtil.setCallRight(true);
				} else {
					message = "提交失败.";
					mWebCallResultUtil.setCallRight(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
				mWebCallResultUtil.setCallRight(false);
				message = "提交错误:" + e.getMessage();
			}
			mWebCallResultUtil.setResponseText(message);
			return mWebCallResultUtil;
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
