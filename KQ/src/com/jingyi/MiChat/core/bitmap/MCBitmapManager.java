package com.jingyi.MiChat.core.bitmap;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class MCBitmapManager {

	private static LruCache<String, Bitmap> CACHE;

	private static void createCache() {
		if (CACHE != null) {
			return;
		}
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int size = maxMemory / 8;
		CACHE = new LruCache<String, Bitmap>(size) {

			@Override
			protected void entryRemoved(final boolean evicted, final String key, final Bitmap oldValue, final Bitmap newValue) {
				super.entryRemoved(evicted, key, oldValue, newValue);
				if (oldValue != null && !oldValue.isRecycled()) {
					oldValue.recycle();
				}
			}

			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				try {
					return bitmap.getRowBytes() * bitmap.getHeight();
				} catch (Exception ex) {
					bitmap = null;
					return 0;
				}
			}
		};
	}

	public static Bitmap getBitmap(String tag) {
		if (tag == null) {
			return null;
		}
		createCache();
		Bitmap bit = CACHE.get(tag);
		return bit;
	}

	public static void removeBitmap(String tag) {
		createCache();
		CACHE.remove(tag);
	}

	public static void clear() {
		createCache();
		CACHE.evictAll();
	}

	public static void addBitmap(String tag, Bitmap bitmap) {
		try {
			if (tag == null) {
				return;
			}
			if (bitmap == null || bitmap.isRecycled()) {
				return;
			}
			createCache();
			if (getBitmap(tag) == null) {
				CACHE.put(tag, bitmap);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getString() {
		try {
			if (CACHE == null) {
				return "CACHE is null";
			}
			long softsize = 0;
			int softcount = 0;

			return "lru size:" + CACHE.size() + "  putCount:" + CACHE.putCount() + "  createCount:" + CACHE.createCount() + "  evictionCount:" + CACHE.evictionCount() + "  " + CACHE.toString()
					+ "   null count:" + softcount + "  size:" + softsize;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

}
