package com.jingyi.MiChat.core.http;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.jingyi.MiChat.core.storage.IMCStorage;
import com.jingyi.MiChat.core.storage.MCStorageFactory;


public class MCHttpCache {

	public final static int CACHE_TYPE_JSON = 0;
	public final static int CACHE_TYPE_IMAGE = 1;

	private static MCHttpCache mInstance;
	public static synchronized MCHttpCache getInstance() {
		if (mInstance == null)
			mInstance = new MCHttpCache();
		return mInstance;
	}

	private final Object lockobj = new Object();

	private MCHttpCache() {
	}

	private void saveCacheToFile(String url, String value, String time) {
		String v = time + "," + value;
		IMCStorage storage = MCStorageFactory.getStorage();
		storage.put(url, v);
	}

	public void saveCache(HttpResponse response, String url, String value, boolean isLazyLoad) {
		synchronized (lockobj) {
			if (value == null || value.length() == 0)
				return;
			try {
				Header[] cache = response.getHeaders("Cache-Control");
				if (cache != null && cache.length > 0 && "public".equals(cache[0].getValue())) {
					Header[] expires = response.getHeaders("Expires");
					if (expires != null && expires.length > 0) {
						SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
						Date date = sdf.parse(expires[0].getValue());
						saveCacheToFile(url, value, date.getTime() + "");
						return;
					}
				}

				if (isLazyLoad) {
					saveCacheToFile(url, value, "0");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * 从缓存中加载
	 * 
	 * @return
	 */
	public MCHttpCacheItem loadFromCache(String url, boolean isLazyLoad) {
		synchronized (lockobj) {
			try {
				if (url != null && url.length() > 0) {
					IMCStorage storage = MCStorageFactory.getStorage();
					String res = storage.get(url);
					if (res == null || res.length() == 0) {
						return null;
					}
					long expiredTime = Long.parseLong(res.substring(0, res.indexOf(",")));
					String r = res.substring(res.indexOf(",") + 1, res.length());
					if (isLazyLoad) {
						return new MCHttpCacheItem(MCHttpCacheItem.LOAD_TYPE_FROM_LAZY_LOAD, r);
					} else if (System.currentTimeMillis() < expiredTime) {
						return new MCHttpCacheItem(MCHttpCacheItem.LOAD_TYPE_FROM_CACHE, r);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	public class MCHttpCacheItem {
		/***
		 * 缓存里存放的是cache数据
		 */
		public static final int LOAD_TYPE_FROM_CACHE = 1;
		/***
		 * 缓存里存放的是lazyload数据
		 */
		public static final int LOAD_TYPE_FROM_LAZY_LOAD = 0;

		private int mType;
		private String mContent;

		public MCHttpCacheItem(int type, String content) {
			mType = type;
			mContent = content;
		}

		public String getContent() {
			return mContent;
		}

		public int getType() {
			return mType;
		}
	}


}
