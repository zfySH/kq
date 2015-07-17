package com.jingyi.MiChat.core.http;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Looper;

import com.jingyi.MiChat.appinfo.AppInfo;
import com.jingyi.MiChat.core.bitmap.MCBitmapFactory;
import com.jingyi.MiChat.core.cookie.MCHttpCookie;
import com.jingyi.MiChat.core.report.MCHttpLog;
import com.jingyi.MiChat.core.thread.MCThreadPool;

public class HttpUtil {

	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8 * 1024; // 8KB
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";
	private String mCharset = "utf-8";

	private boolean mIsLazyLoad = false;
	private boolean mIsSaveLog = true;
	private int mPriority;

	private final DefaultHttpClient mHttpClient;
	private final HttpContext mHttpContext;
	private static int maxConnections = 10;
	private static int socketTimeout = 10 * 1000;
	private static int maxRetries = 5;
	private final Map<String, String> mClientHeaderMap;
	private MCHttpRequestAsync request;

	// 为了统计数据获取
	private HttpRequest mHttpRequest;
	private HttpResponse mHttpResponse;
	private HttpGet mHttpGet;
	private HttpPost mHttpPost;

	public HttpUtil() {
		BasicHttpParams httpParams = new BasicHttpParams();

		ConnManagerParams.setTimeout(httpParams, socketTimeout);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
				new ConnPerRouteBean(maxConnections));
		ConnManagerParams.setMaxTotalConnections(httpParams, 10);

		HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, socketTimeout);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams,
				DEFAULT_SOCKET_BUFFER_SIZE);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
				httpParams, schemeRegistry);
		mHttpContext = new SyncBasicHttpContext(new BasicHttpContext());
		mHttpClient = new DefaultHttpClient(cm, httpParams);
		mHttpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			public void process(HttpRequest request, HttpContext context) {
				mHttpRequest = request;
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
				for (String header : mClientHeaderMap.keySet()) {
					request.addHeader(header, mClientHeaderMap.get(header));
				}
			}
		});

		mHttpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			public void process(HttpResponse response, HttpContext context) {
				mHttpResponse = response;
				final HttpEntity entity = response.getEntity();
				if (entity == null) {
					return;
				}
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							response.setEntity(new InflatingEntity(response
									.getEntity()));
							break;
						}
					}
				}

				Header[] headers = response.getHeaders("Set-Cookie");
				if (headers != null && headers.length > 0) {
					for (int i = 0; i < headers.length; i++) {
						Header header = headers[i];
						String value = header.getValue();
						if (value.indexOf(";") > 0) {
							value = value.substring(0, value.indexOf(";"))
									.trim();
						}
						String[] keyValue = value.split("=");
						if ("cmfuToken".equalsIgnoreCase(keyValue[0])) {
							// 有cmfuToken了
							String cmfuToken = keyValue[1];
							MCHttpCookie.getInstance().setCmfuToken(cmfuToken);
						}
					}
				}

			}
		});

		mHttpClient.setHttpRequestRetryHandler(new MCRetryHandler(maxRetries));

		mClientHeaderMap = new HashMap<String, String>();
	}

	private static class InflatingEntity extends HttpEntityWrapper {
		public InflatingEntity(HttpEntity wrapped) {
			super(wrapped);
		}

		@Override
		public InputStream getContent() throws IOException {
			return new GZIPInputStream(wrappedEntity.getContent());
		}

		@Override
		public long getContentLength() {
			return -1;
		}
	}

	public void setIsLazyLoad(boolean isLazyLoad) {
		mIsLazyLoad = isLazyLoad;
	}

	public void setPriority(int priority) {
		mPriority = priority;
	}

	public void setSaveLog(boolean isSaveLog) {
		mIsSaveLog = isSaveLog;
	}

	public HttpClient getHttpClient() {
		return this.mHttpClient;
	}

	public HttpContext getHttpContext() {
		return this.mHttpContext;
	}

	public void configCharset(String charSet) {
		if (charSet != null && charSet.trim().length() != 0)
			this.mCharset = charSet;
	}

	public void addHeader(String key, String value) {
		mClientHeaderMap.put(key, value);
	}

	private void addCookieAndQDInfo() {
		String cookie = MCHttpCookie.getInstance().getCookies();
		if (cookie != null && cookie.length() > 0) {
			addHeader("Cookie", cookie);
		}

		String userAgent = AppInfo.getInstance().getUserAgent();
		if (userAgent != null && userAgent.length() > 0) {
			addHeader("UserAgent", userAgent);
		}
	}

	/***
	 * 异步发送Http get请求，单独起线程
	 * 
	 * @param url
	 * @param callback
	 */
	public void get(String url, MCHttpCallBack callback) {
		addCookieAndQDInfo();
		mHttpGet = new HttpGet(url);
		sendRequest(mHttpGet, callback, null);
	}

	/***
	 * 同步发送http get请求，使用当前线程
	 * 
	 * @param url
	 */
	public MCHttpResp get(String url) {
		if (AppInfo.getInstance().isDebug()
				&& Looper.myLooper() == Looper.getMainLooper()) {
			throw new RuntimeException("can not call this in main thread");
		}
		long startTime = System.currentTimeMillis();
		addCookieAndQDInfo();
		MCHttpRequestSync request = new MCHttpRequestSync(mHttpClient,
				mHttpContext, mCharset);
		mHttpGet = new HttpGet(url);
		MCHttpResp resp = request.sendRequest(mHttpGet, null);
		doSaveLog(url, "", startTime, resp);
		return resp;
	}

	public void doSaveLog(String url, String param, Long startTime,
			MCHttpResp resp) {
		if (!mIsSaveLog)
			return;
		StringBuffer request = new StringBuffer();
		httpRequestHeader(request);
		StringBuffer response = new StringBuffer();
		httpResponsHeader(response);
		MCHttpLog.Submit(url, param, System.currentTimeMillis() - startTime,
				resp.getCode(), resp.getData(), resp.getLoadType(),
				request.toString(), response.toString());
	}

	/***
	 * 异步发送Http post请求，单独起线程
	 * 
	 * @param url
	 * @param data
	 * @param callback
	 */
	public void post(String url, ArrayList<NameValuePair> data,
			MCHttpCallBack callback) {
		addCookieAndQDInfo();
		UrlEncodedFormEntity entity;
		String param = "";
		if (data != null) {
			for (NameValuePair nv : data) {
				param = param + nv.getName() + "_" + nv.getValue() + "&";
			}
		}
		try {
			if (data == null)
				data = new ArrayList<NameValuePair>();
			entity = new UrlEncodedFormEntity(data, HTTP.UTF_8);
			mHttpPost = new HttpPost(url);
			mHttpPost.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false); // 相当于.net里的
																	// Expect100Continue
																	// =
																	// false
			mHttpPost.setEntity(entity);
			sendRequest(mHttpPost, callback, null);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

			if (callback != null) {
				callback.onError(new MCHttpResp(false,
						MCHttpResp.ERROR_UNSUPPORT_ENCODING));
			}
		}

	}

	/***
	 * 同步发送Http post请求，单独起线程
	 * 
	 * @param url
	 * @param data
	 * @return
	 */
	public MCHttpResp post(String url, ArrayList<NameValuePair> data) {
		if (AppInfo.getInstance().isDebug()
				&& Looper.myLooper() == Looper.getMainLooper()) {
			throw new RuntimeException("can not call this in main thread");
		}
		long startTime = System.currentTimeMillis();
		addCookieAndQDInfo();
		UrlEncodedFormEntity entity;
		MCHttpResp resp = null;
		String param = "";
		if (data != null) {
			for (NameValuePair nv : data) {
				param = param + nv.getName() + "_" + nv.getValue() + "&";
			}
		}
		try {
			if (data == null)
				data = new ArrayList<NameValuePair>();
			entity = new UrlEncodedFormEntity(data, HTTP.UTF_8);
			mHttpPost = new HttpPost(url);
			mHttpPost.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false); // 相当于.net里的
																	// Expect100Continue
																	// =
																	// false
			mHttpPost.setEntity(entity);
			MCHttpRequestSync request = new MCHttpRequestSync(mHttpClient,
					mHttpContext, mCharset);
			resp = request.sendRequest(mHttpPost, null);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			resp = new MCHttpResp(false, MCHttpResp.ERROR_UNSUPPORT_ENCODING);
		}
		doSaveLog(url, param, startTime, resp);
		return resp;
	}

	/***
	 * 同步发送http get请求，并下载成文件，使用当前线程
	 * 
	 * @param url
	 */
	public MCHttpResp download(String url, String targetPath) {
		if (AppInfo.getInstance().isDebug()
				&& Looper.myLooper() == Looper.getMainLooper()) {
			throw new RuntimeException("can not call this in main thread");
		}
		addCookieAndQDInfo();
		MCHttpRequestSync request = new MCHttpRequestSync(mHttpClient,
				mHttpContext, mCharset);
		HttpUriRequest httpUriRequest = new HttpPost(url);
		httpUriRequest.setHeader("Accept-Encoding", "identity");
		return request.sendRequest(httpUriRequest, targetPath);
	}

	/***
	 * 异步发送http get请求，并下载成文件，单独起线程
	 * 
	 * @param url
	 */
	public void download(String url, String targetPath, MCHttpCallBack callback) {
		addCookieAndQDInfo();
		HttpUriRequest httpUriRequest = new HttpPost(url);
		httpUriRequest.setHeader("Accept-Encoding", "identity");
		sendRequest(httpUriRequest, callback, targetPath);
	}

	public void stopDownload() {
		if (request != null) {
			request.setStop(true);
		}
	}

	/**
	 * 异步下载图片
	 * 
	 * @param url
	 * @param path
	 * @param callback
	 */
	public void getBitmap(String url, MCHttpCallBack callback) {
		MCHttpRequestAsync re = new MCHttpRequestAsync(url, callback);
		if (re.isNeedDownBitmap()) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				executeOnExecutor(re, url);
			} else {
				re.execute(url);
			}
		}
	}

	/**
	 * 同步下载图片
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmap(String url) {
		return MCBitmapFactory.decodeURL(url);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void executeOnExecutor(MCHttpRequestAsync re, String url) {
		re.executeOnExecutor(
				MCThreadPool.getInstance(MCThreadPool.PRIORITY_MEDIUM), url);
	}

	/***
	 * 同步发送Http post请求，使用当前线程
	 * 
	 * @param url
	 * @param data
	 * @return
	 */
	public MCHttpResp post(String url, byte[] data) {
		if (AppInfo.getInstance().isDebug()
				&& Looper.myLooper() == Looper.getMainLooper()) {
			throw new RuntimeException("can not call this in main thread");
		}
		long startTime = System.currentTimeMillis();
		addCookieAndQDInfo();
		ByteArrayEntity entity;
		MCHttpResp resp = null;
		entity = new ByteArrayEntity(data);
		HttpPost post = new HttpPost(url);
		post.getParams().setBooleanParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, false); // 相当于.net里的
																// Expect100Continue
																// =
																// false
		post.setEntity(entity);
		MCHttpRequestSync request = new MCHttpRequestSync(mHttpClient,
				mHttpContext, mCharset);
		resp = request.sendRequest(post, null);
		doSaveLog(url, "post byte", startTime, resp);
		return resp;
	}

	/***
	 * 同步发送Http post请求，使用当前线程
	 * 
	 * @param url
	 * @param data
	 * @return
	 */
	public MCHttpResp postImage(String url, ArrayList<NameValuePair> parms,
			String imagePath) {
		if (AppInfo.getInstance().isDebug()
				&& Looper.myLooper() == Looper.getMainLooper()) {
			throw new RuntimeException("can not call this in main thread");
		}
		long startTime = System.currentTimeMillis();
		addCookieAndQDInfo();
		MCHttpResp resp = null;
		HttpPost post = new HttpPost(url);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			paramToUpload(bos, parms);
			post.setHeader("Content-Type", MULTIPART_FORM_DATA + "; boundary="
					+ BOUNDARY);
			imageContentToUpload(bos, imagePath);
			post.setEntity(new ByteArrayEntity(bos.toByteArray()));
			post.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false); // 相当于.net里的
																	// Expect100Continue
																	// =
																	// false
			MCHttpRequestSync request = new MCHttpRequestSync(mHttpClient,
					mHttpContext, mCharset);
			resp = request.sendRequest(post, null);
			doSaveLog(url, "post byte", startTime, resp);
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
			return new MCHttpResp(false, MCHttpResp.ERROR_UPLOAD_ERROR);
		}
	}

	private static final String BOUNDARY = getBoundry();
	private static final String MP_BOUNDARY = "--" + BOUNDARY;
	private static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
	private static final String MULTIPART_FORM_DATA = "multipart/form-data";

	private void paramToUpload(OutputStream baos,
			ArrayList<NameValuePair> params) throws IOException {
		String key = "";
		NameValuePair item = null;
		for (int loc = 0; loc < params.size(); loc++) {
			item = params.get(loc);
			key = item.getName();
			StringBuilder temp = new StringBuilder(10);
			temp.setLength(0);
			temp.append(MP_BOUNDARY).append("\r\n");
			temp.append("content-disposition: form-data; name=\"").append(key)
					.append("\"\r\n\r\n");
			temp.append(item.getValue()).append("\r\n");
			byte[] res = temp.toString().getBytes();
			try {
				baos.write(res);
			} catch (IOException e) {
				throw e;
			}
		}
	}

	/**
	 * 产生11位的boundary
	 */
	private static String getBoundry() {
		StringBuffer _sb = new StringBuffer();
		for (int t = 1; t < 12; t++) {
			long time = System.currentTimeMillis() + t;
			if (time % 3 == 0) {
				_sb.append((char) time % 9);
			} else if (time % 3 == 1) {
				_sb.append((char) (65 + time % 26));
			} else {
				_sb.append((char) (97 + time % 26));
			}
		}
		return _sb.toString();
	}

	private void imageContentToUpload(OutputStream out, String imgpath)
			throws IOException {
		if (imgpath == null) {
			return;
		}
		StringBuilder temp = new StringBuilder();

		temp.append(MP_BOUNDARY).append("\r\n");
		temp.append("Content-Disposition: form-data; name=\"pic\"; filename=\"")
				.append(imgpath).append("\"\r\n");
		String filetype = "image/jpg";
		temp.append("Content-Type: ").append(filetype).append("\r\n\r\n");
		// temp.append("content-disposition: form-data; name=\"file\"; filename=\"")
		// .append(imgpath).append("\"\r\n");
		// temp.append("Content-Type: application/octet-stream; charset=utf-8\r\n\r\n");
		byte[] res = temp.toString().getBytes();
		FileInputStream input = null;
		try {
			out.write(res);
			input = new FileInputStream(imgpath);
			byte[] buffer = new byte[1024 * 50];
			while (true) {
				int count = input.read(buffer);
				if (count == -1) {
					break;
				}
				out.write(buffer, 0, count);
			}
			out.write("\r\n".getBytes());
			out.write(("\r\n" + END_MP_BOUNDARY).getBytes());
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != input) {
				try {
					input.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}

	@SuppressLint("NewApi")
	private void sendRequest(HttpUriRequest uriRequest,
			MCHttpCallBack callback, String targetPath) {
		request = new MCHttpRequestAsync(this, mHttpClient, mHttpContext,
				callback, mCharset, mIsLazyLoad, targetPath);
		request.executeOnExecutor(MCThreadPool.getInstance(mPriority),
				uriRequest);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (mHttpPost != null) {
			httpPostString(sb);
		} else if (mHttpGet != null) {
			httpGetString(sb);
		}
		sb.append("Request:Headers \n");
		httpRequestHeader(sb);
		sb.append("Response:Headers \n");
		httpResponsHeader(sb);
		return sb.toString();
	}

	private void httpPostString(StringBuffer sb) {
		if (mHttpPost == null) {
			return;
		}
		sb.append("Request:Urls \n");
		sb.append("    ");
		sb.append(mHttpPost.getURI().toString());
		sb.append("\n");
		try {
			sb.append("Params \n");
			sb.append("    ");
			sb.append(EntityUtils.toString(mHttpPost.getEntity()));
			sb.append("\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void httpGetString(StringBuffer sb) {
		if (mHttpGet == null) {
			return;
		}
		sb.append("Request:Urls \n");
		try {
			sb.append("    ");
			sb.append(mHttpGet.getURI().toString());
			sb.append("\n");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void httpRequestHeader(StringBuffer sb) {
		if (mHttpRequest == null) {
			return;
		}
		Header[] headers = mHttpRequest.getAllHeaders();
		for (Header h : headers) {
			sb.append("    ");
			sb.append(h.toString());
			sb.append("\n");
		}
	}

	private void httpResponsHeader(StringBuffer sb) {
		if (mHttpResponse == null) {
			return;
		}
		Header[] headers = mHttpResponse.getAllHeaders();
		for (Header h : headers) {
			sb.append("    ");
			sb.append(h.toString());
			sb.append("\n");
		}
	}

}
