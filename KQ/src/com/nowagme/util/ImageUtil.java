package com.nowagme.util;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Log;

public class ImageUtil {
	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["+ ImageUtil.class.getName() + "]";

	/**
	 * 计算BitmapFactory.Options参数的InSampleSize数值.
	 * 
	 * @param options
	 *            BitmapFactory.Options参数
	 * @param dstWidth
	 *            目标图片宽带
	 * @param dstHeight
	 *            目标图片高度
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int dstWidth, int dstHeight) {
		logi("int calculateInSampleSize(BitmapFactory.Options options,int dstWidth, int dstHeight)");
		logi("options.outWidth="+options.outWidth+",options.outHeight="+options.outHeight+",dstWidth="+dstWidth+",dstHeight="+dstHeight);
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > dstHeight || width > dstWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			while ((halfHeight / inSampleSize) >= dstHeight
					&& (halfWidth / inSampleSize) >= dstWidth) {
				inSampleSize *= 2;
			}
		}
		logi("inSampleSize="+inSampleSize);
		return inSampleSize;
	}

	/**
	 * 放到或缩小图片.
	 * 
	 * @param src
	 *            原图片
	 * @param dstWidth
	 *            目标图片宽度
	 * @param dstHeight
	 *            目标图片高度
	 * @param filter
	 *            如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
	 * @return
	 */
	private static Bitmap createScaleBitmap(Bitmap src, int dstWidth,
			int dstHeight, boolean filter) {
		logi("Bitmap createScaleBitmap(Bitmap src, int dstWidth,int dstHeight, boolean filter)");
		logi("src="+src+",dstWidth="+dstWidth+",dstHeight="+dstHeight+",filter="+filter);
		Bitmap dst = Bitmap
				.createScaledBitmap(src, dstWidth, dstHeight, filter);
		if (src != dst) { // 如果没有缩放，那么不回收
			src.recycle(); // 释放Bitmap的native像素数组
			logi("src.recycle()");
		}
		return dst;
	}

	/**
	 * 从sd卡上加载图片.
	 * @param pathName
	 * @param dstWidth
	 * @param dstHeight
	 * @param filter
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFd(String pathName,
			int dstWidth, int dstHeight,boolean filter) {
		logi("Bitmap decodeSampledBitmapFromFd(String pathName,int dstWidth, int dstHeight,boolean filter)");
		logi("pathName="+pathName+",dstWidth="+dstWidth+", dstHeight="+dstHeight+",filter="+filter+")");
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		options.inSampleSize = calculateInSampleSize(options, dstWidth,
				dstHeight);
		options.inJustDecodeBounds = false;
		Bitmap src = BitmapFactory.decodeFile(pathName, options);
		return createScaleBitmap(src, dstWidth, dstHeight,filter);
	}

	/**
	 * 从网络上获取图片.
	 * 
	 * @param imageUrl
	 * @return
	 * @throws Exception
	 */
	public static Drawable loadImageFromNetwork(String imageUrl)
			throws Exception {
		return Drawable.createFromStream(new URL(imageUrl).openStream(),null);
	}

	/**
	 * 将bitmap保存到sdcard的指定目录中.
	 * 
	 * @param bm
	 * @param filename
	 *            带完整路径的文件名
	 * @throws Exception
	 */
	public static void saveBitmapToScard(Bitmap bm, String filename)
			throws Exception {
		File f = null;
		FileOutputStream fos = null;
		try {
			f = new File(filename);
			f.createNewFile();
			fos = new FileOutputStream(f);
			bm.compress(CompressFormat.JPEG, 100, fos);
		} finally {
			if (fos != null)
				fos.close();
			fos = null;
			f = null;
		}
	}

	/**
	 * 获取图片的选择角度.
	 * 
	 * @param filename
	 *            带完整路径的图片文件名
	 * @return
	 * @throws Exception
	 */
	public static int readPictureDegree(String filename) throws Exception {
		int degree = 0;
		ExifInterface exifInterface = new ExifInterface(filename);
		int orientation = exifInterface
				.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);
		switch (orientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			degree = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			degree = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			degree = 270;
			break;
		}
		return degree;
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

