package com.nowagme.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class HttpGetUtil {
	
	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["+ HttpGetUtil.class.getName() + "]";
	

	private String url;
	private Map<String, String> parameters;
	private String encoding;
	private String responseText;
	

	public HttpGetUtil(String url, String encoding,
			Map<String, String> parameters) {
		super();
		this.url = url;
		this.parameters = parameters;
		this.encoding = encoding;
	}

	/**
	 * 返回值.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getResponseText() throws Exception {
		return this.responseText;
	}

	/**
	 * submit.
	 * 
	 * @return
	 */
	public boolean submit() throws Exception {
		String actionURL = url;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 设置普通参数
		if (parameters != null&&parameters.size()>0) {
			for (String name : parameters.keySet()) {
				params.add(new BasicNameValuePair(name,parameters.get(name)));
			}
			actionURL+="?"+URLEncodedUtils.format(params, encoding);
		}
		logi("009:actionURL="+actionURL);
		HttpGet get = new HttpGet(actionURL);// 创建http get对象并设置action url.
//		get.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "application/x-www-form-urlencoded");
//		get.addHeader("Content-Type", "application/x-www-form-urlencoded");
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);
		int status = response.getStatusLine().getStatusCode();
		boolean isOK = (status == HttpStatus.SC_OK);
		if (isOK) {
			StringBuffer data = new StringBuffer();
			BufferedReader rd = null;
			try {
				rd = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				String line = "";
				while ((line = rd.readLine()) != null) {
					data.append(line);
				}
			} finally {
				if (rd != null)
					rd.close();
				rd = null;
			}
			this.responseText = data.toString();
		}
		return isOK;
	}

	/**
	  * log information
	  * @param msg
	  */
	 public static void logi(String msg){
		 Log.i(TAG, CLASS_NAME+" "+msg);
	 }
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
