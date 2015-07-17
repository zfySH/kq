package com.jingyi.MiChat.core.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import android.text.TextUtils;

import com.jingyi.MiChat.core.http.MCHttpCache.MCHttpCacheItem;


public class MCHttpRequestSync {

	private final AbstractHttpClient client;
	private final HttpContext httpContext;

	private int executionCount = 0;
	private String charset;

	private String mUrl;
	private String targetFilePath = null;
	private boolean isResume = false;

	public MCHttpRequestSync(AbstractHttpClient client, HttpContext httpContext, String charset) {
		this.client = client;
		this.httpContext = httpContext;
		this.charset = charset;
	}

	/***
	 * ���߳��������������л���Ļ�����ô�ӻ����ж�ȡ
	 * 
	 * @param request
	 * @throws IOException
	 */
	public MCHttpResp sendRequest(HttpUriRequest request, String targetPath) {

		if (request instanceof HttpGet) {
			mUrl = ((HttpGet) request).getURI().toString();
		}

		targetFilePath = targetPath;

		MCHttpCacheItem loadCacheFile = MCHttpCache.getInstance().loadFromCache(mUrl, false);
		if (loadCacheFile != null && loadCacheFile.getContent().length() > 0) {
			MCHttpResp resp = new MCHttpResp(true, 200, MCHttpResp.LOAD_TYPE_FROM_CACHE, loadCacheFile.getContent());
			return resp;
		}

		if (isResume && targetFilePath != null) {
			File downloadFile = new File(targetFilePath);
			long fileLen = 0;
			if (downloadFile.isFile() && downloadFile.exists()) {
				fileLen = downloadFile.length();
			}
			if (fileLen > 0)
				request.setHeader("RANGE", "bytes=" + fileLen + "-");
		}

		boolean retry = true;
		IOException cause = null;
		HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
		while (retry) {
			try {
				HttpResponse response = client.execute(request, httpContext);
				return handleResponse(response);
			} catch (UnknownHostException e) {
				return new MCHttpResp(false, MCHttpResp.ERROR_UNKNOWN_HOST);
			} catch (SocketTimeoutException e) {
				cause = e;
				retry = retryHandler.retryRequest(cause, ++executionCount, httpContext);
				if (!retry) {
					return new MCHttpResp(false, MCHttpResp.ERROR_SOCKET_TIMEOUT);
				}
			} catch (IOException e) {
				cause = e;
				retry = retryHandler.retryRequest(cause, ++executionCount, httpContext);
			} catch (NullPointerException e) {
				// HttpClient 4.0.x 之前的一个bug
				// http://code.google.com/p/android/issues/detail?id=5255
				cause = new IOException("NPE in HttpClient" + e.getMessage());
				retry = retryHandler.retryRequest(cause, ++executionCount, httpContext);
			} catch (Exception e) {
				cause = new IOException("Exception" + e.getMessage());
				retry = retryHandler.retryRequest(cause, ++executionCount, httpContext);
			}
		}
		return new MCHttpResp(false, MCHttpResp.ERROR_UNKNOWN);
	}

	private MCHttpResp handleResponse(HttpResponse response) {
		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() >= 300) {
			// String errorMsg =
			// "response status error code:"+status.getStatusCode();
			if (status.getStatusCode() == 416) {
				// errorMsg += " \n maybe you have download complete.";
			}
			MCHttpResp resp = new MCHttpResp(false, status.getStatusCode());
			return resp;
		} else {
			try {
				// if (response.containsHeader("Content-Type") &&
				// response.getHeaders("Content-Type") != null &&
				// response.getHeaders("Content-Type").length > 0) {
				// String contentType =
				// response.getHeaders("Content-Type")[0].getValue();
				// if (contentType.contains("htm"))
				// return new MCHttpResp(false, MCHttpResp.ERROR_CONTENT_TYPE);
				// }
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					if (targetFilePath != null && targetFilePath.length() > 0) {
						MCHttpResp resp = getFileEntity(entity, targetFilePath);
						return resp;
					} else {
						MCHttpResp resp = getStringEntity(entity, charset);
						MCHttpCache.getInstance().saveCache(response, mUrl, resp.getData(), false);
						return resp;
					}
				} else {
					return new MCHttpResp(false, MCHttpResp.ERROR_ENTITY_IS_NULL);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new MCHttpResp(false, MCHttpResp.ERROR_UNKNOWN);
			}
		}
	}

	private MCHttpResp getFileEntity(HttpEntity entity, String target) throws IOException {
		if (TextUtils.isEmpty(target) || target.trim().length() == 0)
			return new MCHttpResp(false);

		File targetFile = new File(target);

		if (!targetFile.exists()) {
			targetFile.createNewFile();
		}
		long current = 0;
		FileOutputStream os = null;
		InputStream input = null;
		try {
			if (isResume) {
				current = targetFile.length();
				os = new FileOutputStream(target, true);
			} else {
				os = new FileOutputStream(target);
			}

			input = entity.getContent();
			long count = entity.getContentLength() + current;

			if (current >= count) {
				return new MCHttpResp(false);
			}

			int readLen = 0;
			byte[] buffer = new byte[1024];
			while (!(current >= count) && ((readLen = input.read(buffer, 0, 1024)) > 0)) {// δȫ����ȡ
				os.write(buffer, 0, readLen);
				current += readLen;
			}
			if (current < count) {
				throw new IOException("user stop download thread");
			}
			return new MCHttpResp(true, 200, MCHttpResp.LOAD_TYPE_FROM_NETWORK, "ok");
		} catch (Exception e) {
			e.printStackTrace();
			return new MCHttpResp(false, MCHttpResp.ERROR_UNKNOWN);
		} finally {
			if (input != null)
				input.close();
			if (os != null)
				os.close();
		}
	}

	private MCHttpResp getStringEntity(HttpEntity entity, String charset) throws IOException {
		if (entity == null)
			return null;
		ByteArrayOutputStream outStream = null;
		InputStream is = null;
		try {
			outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];

			long count = entity.getContentLength();
			long curCount = 0;
			int len = -1;
			is = entity.getContent();
			while ((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
				curCount += len;
			}
			byte[] data = outStream.toByteArray();
			return new MCHttpResp(true, 200, MCHttpResp.LOAD_TYPE_FROM_NETWORK, new String(data, charset));

		} catch (Exception e) {
			e.printStackTrace();
			return new MCHttpResp(false, MCHttpResp.ERROR_UNKNOWN);
		} finally {
			if (outStream != null)
				outStream.close();
			if (is != null)
				is.close();
		}

	}


}
