package com.jingyi.MiChat.core.bitmap;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;

import com.jingyi.MiChat.core.io.MCFileUtil;


public class MCBitmapFactory {


	public static Bitmap createScaledBitmap(String tag, Bitmap src, int dstWidth, int dstHeight, boolean filter) {
		Bitmap qdbitmap = MCBitmapManager.getBitmap(tag);
		if (qdbitmap == null || qdbitmap.isRecycled()) {
			MCBitmapManager.removeBitmap(tag);
			qdbitmap = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, filter);
			if (qdbitmap == null) {
				return null;
			}
			MCBitmapManager.addBitmap(tag, qdbitmap);
		}
		return qdbitmap;
	}

	public static Bitmap decodeResource(Resources res, String tag, int resId) {
		Bitmap qdbitmap = MCBitmapManager.getBitmap(tag);
		if (qdbitmap == null || qdbitmap.isRecycled()) {
			MCBitmapManager.removeBitmap(tag);
			qdbitmap = BitmapFactory.decodeResource(res, resId);
			if (qdbitmap == null) {
				return null;
			}
			MCBitmapManager.addBitmap(tag, qdbitmap);
		}
		return qdbitmap;
	}

	public static Bitmap decodeStream(Resources res, String tag, InputStream is, Options options) {
		Bitmap qdbitmap = MCBitmapManager.getBitmap(tag);
		if (qdbitmap == null || qdbitmap.isRecycled()) {
			MCBitmapManager.removeBitmap(tag);
			qdbitmap = BitmapFactory.decodeStream(is);
			if (qdbitmap == null) {
				return null;
			}
			MCBitmapManager.addBitmap(tag, qdbitmap);
		}
		return qdbitmap;
	}

	public static Bitmap decodeBytes(String tag, byte[] bytes) {
		if (bytes == null)
			return null;
		Bitmap qdbitmap = MCBitmapManager.getBitmap(tag);
		if (qdbitmap == null || qdbitmap.isRecycled()) {
			MCBitmapManager.removeBitmap(tag);
			qdbitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			if (qdbitmap == null) {
				return null;
			}
			MCBitmapManager.addBitmap(tag, qdbitmap);
		}
		return qdbitmap;
	}

	public static Bitmap decodeFile(String path) {
		return decodeFile(path, null);
	}

	public static Bitmap decodeFile(String path, Options options) {
		Bitmap qdbitmap = MCBitmapManager.getBitmap(path);
		if (qdbitmap == null || qdbitmap.isRecycled()) {
			MCBitmapManager.removeBitmap(path);
			qdbitmap = BitmapFactory.decodeFile(path, options);
			if (qdbitmap == null) {
				return null;
			}
			MCBitmapManager.addBitmap(path, qdbitmap);
		}
		return qdbitmap;
	}

	public static Bitmap decodeURL(String url) {
		Bitmap qdbitmap = MCBitmapManager.getBitmap(url);
		if (qdbitmap == null || qdbitmap.isRecycled()) {
			MCBitmapManager.removeBitmap(url);
			try {
				qdbitmap = MCBitmapUtil.downLoadBitmap(url);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (qdbitmap == null) {
				return null;
			}
			MCBitmapManager.addBitmap(url, qdbitmap);
		}
		return qdbitmap;
	}

	public static Bitmap createBitmap(String tag, Bitmap source, int x, int y, int width, int height, Matrix m, boolean filter) {
		if (width <= 0 || height <= 0) {
			return null;
		}
		if (source == null || source.isRecycled()) {
			return null;
		}

		Bitmap qdbitmap = MCBitmapManager.getBitmap(tag);
		if (qdbitmap == null || qdbitmap.isRecycled()) {
			MCBitmapManager.removeBitmap(tag);
			qdbitmap = Bitmap.createBitmap(source, x, y, width, height, m, filter);
			if (qdbitmap == null) {
				return null;
			}
			MCBitmapManager.addBitmap(tag, qdbitmap);
		}
		return qdbitmap;
	}

	public static Bitmap createBitmap(String tag, Bitmap source, int x, int y, int width, int height) {
		if (width <= 0 || height <= 0) {
			return null;
		}
		if (source == null || source.isRecycled()) {
			return null;
		}

		Bitmap qdbitmap = MCBitmapManager.getBitmap(tag);
		if (qdbitmap == null || qdbitmap.isRecycled()) {
			MCBitmapManager.removeBitmap(tag);
			qdbitmap = Bitmap.createBitmap(source, x, y, width, height);
			if (qdbitmap == null) {
				return null;
			}
			MCBitmapManager.addBitmap(tag, qdbitmap);
		}
		return qdbitmap;
	}

	public static Bitmap createBitmap(String tag, int width, int height, Config config) {
		if (width <= 0 || height <= 0) {
			return null;
		}

		Bitmap qdbitmap = MCBitmapManager.getBitmap(tag);
		if (qdbitmap == null || qdbitmap.isRecycled()) {
			MCBitmapManager.removeBitmap(tag);
			qdbitmap = Bitmap.createBitmap(width, height, config);
			if (qdbitmap == null) {
				return null;
			}
			MCBitmapManager.addBitmap(tag, qdbitmap);
		}
		return qdbitmap;
	}

	public static Bitmap LoadAsset(Context context, String tag, String path) {
		Bitmap qdbitmap = MCBitmapManager.getBitmap(tag);
		if (qdbitmap == null || qdbitmap.isRecycled()) {
			MCBitmapManager.removeBitmap(tag);
			byte[] by = MCFileUtil.LoadAsset(context, path);
			qdbitmap = BitmapFactory.decodeByteArray(by, 0, by.length);
			if (qdbitmap == null) {
				return null;
			}
			MCBitmapManager.addBitmap(tag, qdbitmap);
		}
		return qdbitmap;
	}

}
