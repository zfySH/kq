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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpPostUtil {

	private String url;
	private Map<String, String> parameters;
	private String encoding;
	private String responseText;

	public HttpPostUtil(String url, String encoding,
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
		HttpPost post = new HttpPost(url);// 创建http post对象并设置action url.
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 设置普通参数
		if (parameters != null && parameters.size() > 0) {
			for (String name : parameters.keySet()) {
				params.add(new BasicNameValuePair(name, parameters.get(name)));
			}
		}
		HttpClient client = new DefaultHttpClient();
		post.setEntity(new UrlEncodedFormEntity(params, encoding));
		HttpParams parms = client.getParams();
		HttpConnectionParams.setConnectionTimeout(parms, 60000);// 设置网络超时
		HttpConnectionParams.setSoTimeout(parms, 40000);// 设置网络超时

		HttpResponse response = client.execute(post);
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
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
