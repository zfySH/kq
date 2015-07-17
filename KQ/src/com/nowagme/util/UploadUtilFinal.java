package com.nowagme.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class UploadUtilFinal {

	private String url;
	private Map<String, String> parameters;
	private Map<String, String> files;
	private String responseText;
	private Charset charset;

	public UploadUtilFinal(String url, Charset charset,
			Map<String, String> parameters, Map<String, String> files) {
		super();
		this.url = url;
		this.parameters = parameters;
		this.files = files;
		this.charset = charset;
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
	 * upload.
	 * 
	 * @return
	 */
	public boolean upload() throws Exception {
		HttpPost post = new HttpPost(url);// 创建http post对象并设置action url.
		MultipartEntity multipart = new MultipartEntity();// 创建multipart/form-data上传参数对象.

		// 设置普通参数
		if (parameters != null) {
			for (String name : parameters.keySet()) {
				multipart.addPart(name, new StringBody(parameters.get(name),
						charset));
			}
		}
		// 设置上传文件参数
		if (files != null) {
			for (String name : files.keySet()) {
				multipart
						.addPart(name, new FileBody(new File(files.get(name))));
			}
		}
		HttpClient client = new DefaultHttpClient();
		post.setEntity(multipart);
		HttpResponse response = client.execute(post);
		int status = response.getStatusLine().getStatusCode();
		boolean isOK = (status == 200);
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
