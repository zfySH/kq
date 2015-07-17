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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.TextUtils;

import com.jingyi.MiChat.core.bitmap.MCBitmapFactory;
import com.jingyi.MiChat.core.bitmap.MCBitmapManager;
import com.jingyi.MiChat.core.bitmap.MCBitmapUtil;
import com.jingyi.MiChat.core.http.MCHttpCache.MCHttpCacheItem;
import com.jingyi.MiChat.core.log.MCLog;
import com.jingyi.MiChat.core.storage.MCStorageFactory;

public class MCHttpRequestAsync extends MCAsyncTask<Object, Object, Object> {

	private AbstractHttpClient client;
	private HttpContext httpContext;
	private HttpUriRequest mHttpUriRequest;

	private final MCHttpCallBack callback;

	private int executionCount = 0;
	private String targetFilePath = null; // ���ص�·��
	private boolean isResume = false; // �Ƿ�ϵ�����
	private String charset;

	private boolean mIsLazyLoad;
	private String mUrl;
	private boolean mIsLoadFromCache = false;

	private boolean mIsBitmap = false;
	// private String mBitmapPath;
	public boolean mIsNeedDownBitmap = true;// 是否需要启动线程下载

	private long mStratTime; // 对象被new出世间，可以理解为开始请求时间
	private HttpUtil mQDHttp;
	private String mKey;

	public MCHttpRequestAsync(HttpUtil qdhttp, AbstractHttpClient client,
			HttpContext httpContext, MCHttpCallBack callback, String charset,
			boolean isLazyLoad, String targetPath) {
		this.client = client;
		this.httpContext = httpContext;
		this.callback = callback;
		this.charset = charset;
		mQDHttp = qdhttp;
		mIsLazyLoad = isLazyLoad;
		mStratTime = System.currentTimeMillis();
		this.targetFilePath = targetPath;
	}

	public MCHttpRequestAsync(String url, MCHttpCallBack callback) {
		mIsBitmap = true;
		mUrl = url;
		this.callback = callback;
		Bitmap qdbitmap = MCBitmapManager.getBitmap(mUrl);
		if (qdbitmap != null && !qdbitmap.isRecycled()) {
			mIsNeedDownBitmap = false;
			if (callback != null) {
				callback.onSuccess(new MCHttpResp(true, qdbitmap, mUrl));
			}
			return;
		}
	}

	public boolean isNeedDownBitmap() {
		return mIsNeedDownBitmap;
	}

	/***
	 * ���߳��������������л���Ļ�����ô�ӻ����ж�ȡ
	 * 
	 * @param request
	 * @throws IOException
	 */
	private void makeRequestWithRetries(HttpUriRequest request)
			throws IOException {
		mHttpUriRequest = request;

		if (request instanceof HttpGet) {
			mUrl = ((HttpGet) request).getURI().toString();
			mKey = mUrl;
		} else if (request instanceof HttpPost) {
			mUrl = ((HttpPost) request).getURI().toString();
			HttpEntity entity = ((HttpPost) mHttpUriRequest).getEntity();
			try {
				mKey = mUrl + "?" + EntityUtils.toString(entity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mIsLoadFromCache = false;

		MCHttpCacheItem loadCacheFile = MCHttpCache.getInstance()
				.loadFromCache(mKey, mIsLazyLoad);

		if (loadCacheFile != null && loadCacheFile.getContent().length() > 0) {
			// 如果从缓存里加载到了
			// try {
			// Thread.sleep(2000);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			MCHttpResp resp = new MCHttpResp(true, 200,
					MCHttpResp.LOAD_TYPE_FROM_CACHE, loadCacheFile.getContent());
			if (callback != null) {
				callback.beforeSucess(resp);
			}
			publishProgress(UPDATE_SUCCESS, resp);
			mIsLoadFromCache = true;
			if (loadCacheFile.getType() == MCHttpCacheItem.LOAD_TYPE_FROM_CACHE) {
				// 如果是lazyload，那么还要继续去加载，如果是cache，那么从缓存里加载到了，就直接return了
				return;
			}
		}

		// try {
		// Thread.sleep(2000);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

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
		HttpRequestRetryHandler retryHandler = client
				.getHttpRequestRetryHandler();
		while (retry) {
			try {
				if (!isCancelled()) {
					HttpResponse response = client
							.execute(request, httpContext);
					if (!isCancelled()) {
						handleResponse(response);
					}
				}
				return;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				publishProgress(UPDATE_FAILURE, new MCHttpResp(false,
						MCHttpResp.ERROR_UNKNOWN_HOST));
				return;
			} catch (SocketTimeoutException e) {
				cause = e;
				retry = retryHandler.retryRequest(cause, ++executionCount,
						httpContext);
				if (!retry) {
					publishProgress(UPDATE_FAILURE, new MCHttpResp(false,
							MCHttpResp.ERROR_SOCKET_TIMEOUT));
				}
			} catch (IOException e) {
				cause = e;
				retry = retryHandler.retryRequest(cause, ++executionCount,
						httpContext);
			} catch (NullPointerException e) {
				// HttpClient 4.0.x 之前的一个bug
				// http://code.google.com/p/android/issues/detail?id=5255
				cause = new IOException("NPE in HttpClient" + e.getMessage());
				retry = retryHandler.retryRequest(cause, ++executionCount,
						httpContext);
			} catch (Exception e) {
				cause = new IOException("Exception" + e.getMessage());
				retry = retryHandler.retryRequest(cause, ++executionCount,
						httpContext);
			}
		}
		if (cause != null)
			throw cause;
		else
			throw new IOException("δ֪�������");
	}

	private void doBitmap() {
		// QDBitmap qdbitmap = MCBitmapManager.getBitmap(mBitmapPath);
		// if (qdbitmap != null && !qdbitmap.isRecycled()) {
		// publicBitmap(qdbitmap);
		// return;
		// }
		// 从内存缓存中捞

		if (mUrl == null)
			return;

		Bitmap qdbitmap = MCBitmapManager.getBitmap(mUrl);
		if (qdbitmap != null && !qdbitmap.isRecycled()) {
			publicBitmap(qdbitmap);
			return;
		}
		// 从持久化缓存中捞
		byte[] bytes = MCStorageFactory.getImageStorage().getBytes(mUrl);
		if (bytes != null && bytes.length > 0) {
			qdbitmap = MCBitmapFactory.decodeBytes(mUrl, bytes);
			if (qdbitmap != null && !qdbitmap.isRecycled()) {
				publicBitmap(qdbitmap);
				return;
			}
		}
		qdbitmap = MCBitmapFactory.decodeURL(mUrl);
		if (qdbitmap != null && !qdbitmap.isRecycled()) {
			MCStorageFactory.getImageStorage().put(mUrl,
					MCBitmapUtil.bitmap2Bytes(qdbitmap));
			publicBitmap(qdbitmap);
			return;
		}
		publicBitmap(null);
	}

	public void publicBitmap(Bitmap bitmap) {
		MCHttpResp resp = null;
		if (bitmap == null) {
			resp = new MCHttpResp(false);
			publishProgress(UPDATE_FAILURE, resp);
		} else {
			resp = new MCHttpResp(true, bitmap, mUrl);
			if (callback != null) {
				callback.beforeSucess(resp);
			}
			publishProgress(UPDATE_SUCCESS, resp);
		}
	}

	@Override
	protected Object doInBackground(Object... params) {
		if (callback != null) {
			callback.beforeStart();
		}
		if (params != null && params.length == 3) {
			targetFilePath = String.valueOf(params[1]);
			isResume = (Boolean) params[2];
		}
		try {
			publishProgress(UPDATE_START); // ��ʼ
			if (mIsBitmap) {
				doBitmap();
			} else {
				makeRequestWithRetries((HttpUriRequest) params[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
			publishProgress(UPDATE_FAILURE, new MCHttpResp(false,
					MCHttpResp.ERROR_UNKNOWN)); // ����
		}

		return null;
	}

	private final static int UPDATE_START = 1;
	private final static int UPDATE_LOADING = 2;
	private final static int UPDATE_FAILURE = 3;
	private final static int UPDATE_SUCCESS = 4;

	@SuppressWarnings("unchecked")
	@Override
	protected void onProgressUpdate(Object... values) {
		int update = Integer.valueOf(String.valueOf(values[0]));
		switch (update) {
		case UPDATE_START:
			if (callback != null)
				callback.onStart();
			break;
		case UPDATE_LOADING:
			if (callback != null)
				callback.onLoading(Long.valueOf(String.valueOf(values[1])),
						Long.valueOf(String.valueOf(values[2])));
			break;
		case UPDATE_FAILURE:
			// 如果是lazyload，并且当前已经从缓存中加载到内容了，然后从网络上加载的时候，加载失败了以后不回调了
			if (((MCHttpResp) values[1]).Code == 401) {
				if (callback != null)
					callback.onLogin();
			} else if (callback != null && mIsLoadFromCache == false) {
				callback.onError((MCHttpResp) values[1]);
			} else if (callback != null) {
				callback.onError((MCHttpResp) values[1]);
			}
			doSaveLog((MCHttpResp) values[1]);
			break;
		case UPDATE_SUCCESS:
			if (callback != null) {
				MCHttpResp resp = (MCHttpResp) values[1];
				if (!mIsBitmap) {
					MCLog.d("QDHttpRequestAsync UPDATE_SUCCESS:"
							+ resp.getLoadType() + "," + mUrl);
				}
				callback.onSuccess(resp);
			}
			doSaveLog((MCHttpResp) values[1]);
			break;
		default:
			break;
		}
		super.onProgressUpdate(values);

	}

	private void doSaveLog(MCHttpResp resp) {
		if (mIsBitmap) {
			return;
		}
		String param = null;
		if (mHttpUriRequest instanceof HttpPost) {
			HttpEntity entity = ((HttpPost) mHttpUriRequest).getEntity();
			try {
				param = EntityUtils.toString(entity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mQDHttp.doSaveLog(mHttpUriRequest.getURI().toString(), param,
				System.currentTimeMillis() - mStratTime, resp);
	}

	private void handleResponse(HttpResponse response) {
		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() >= 300) {
			// String errorMsg =
			// "response status error code:"+status.getStatusCode();
			if (status.getStatusCode() == 416 && isResume) {
				// errorMsg += " \n maybe you have download complete.";
			}
			MCHttpResp resp = new MCHttpResp(false, status.getStatusCode());
			publishProgress(UPDATE_FAILURE, resp);
		} else {
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					time = SystemClock.uptimeMillis();
					if (targetFilePath != null) {
						// if (response.containsHeader("Content-Type") &&
						// response.getHeaders("Content-Type") != null &&
						// response.getHeaders("Content-Type").length > 0) {
						// String contentType =
						// response.getHeaders("Content-Type")[0].getValue();
						// if (contentType.contains("htm")) {
						// publishProgress(UPDATE_FAILURE, new MCHttpResp(false,
						// MCHttpResp.ERROR_CONTENT_TYPE));
						// return;
						// }
						// }

						MCHttpResp resp = getFileEntity(entity, targetFilePath,
								isResume);
						if (resp.getSuccessVariable()) {
							if (callback != null) {
								callback.beforeSucess(resp);
							}
							publishProgress(UPDATE_SUCCESS, resp);
						} else {
							publishProgress(UPDATE_FAILURE, resp);
						}

					} else {
						MCHttpResp resp = getStringEntity(entity, charset);
						MCHttpCache.getInstance().saveCache(response, mKey,
								resp.getData(), mIsLazyLoad);
						if (callback != null) {
							callback.beforeSucess(resp);
						}
						publishProgress(UPDATE_SUCCESS, resp);
					}
				} else {
					publishProgress(UPDATE_FAILURE, new MCHttpResp(false,
							MCHttpResp.ERROR_ENTITY_IS_NULL));
				}
			} catch (Exception e) {
				e.printStackTrace();
				publishProgress(UPDATE_FAILURE, new MCHttpResp(false,
						MCHttpResp.ERROR_UNKNOWN));
			}
		}
	}

	private long time;

	private void publishDownloadProgress(long count, long current,
			boolean mustNoticeUI) {
		if (mustNoticeUI) {
			publishProgress(UPDATE_LOADING, count, current);
		} else {
			long thisTime = SystemClock.uptimeMillis();
			if (thisTime - time >= 1000) {// ÿ��ˢһ��
				time = thisTime;
				publishProgress(UPDATE_LOADING, count, current);
			}
		}
	}

	private boolean mStop = false;

	public boolean isStop() {
		return mStop;
	}

	public void setStop(boolean stop) {
		this.mStop = stop;
	}

	private MCHttpResp getFileEntity(HttpEntity entity, String target,
			boolean isResume) throws IOException {
		if (TextUtils.isEmpty(target) || target.trim().length() == 0)
			return new MCHttpResp(false);

		File targetFile = new File(target);

		if (!targetFile.exists()) {
			targetFile.createNewFile();
		}

		if (mStop) {
			return new MCHttpResp(false);
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

			if (mStop) {
				return new MCHttpResp(false);
			}
			input = entity.getContent();
			long count = entity.getContentLength() + current;

			if (current >= count || mStop) {
				return new MCHttpResp(false);
			}

			int readLen = 0;
			byte[] buffer = new byte[1024];
			while (!mStop && !(current >= count)
					&& ((readLen = input.read(buffer, 0, 1024)) > 0)) {// δȫ����ȡ
				os.write(buffer, 0, readLen);
				current += readLen;
				publishDownloadProgress(count, current, false);
			}
			publishDownloadProgress(count, current, true);
			if (current < count) {
				return new MCHttpResp(false);
			}
			return new MCHttpResp(true);
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

	private MCHttpResp getStringEntity(HttpEntity entity, String charset)
			throws IOException {
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
				publishDownloadProgress(count, curCount, false);
			}
			publishDownloadProgress(count, curCount, true);
			byte[] data = outStream.toByteArray();
			return new MCHttpResp(true, 200, MCHttpResp.LOAD_TYPE_FROM_NETWORK,
					new String(data, charset));

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
