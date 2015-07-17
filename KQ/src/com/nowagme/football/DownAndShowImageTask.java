package com.nowagme.football;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import cn.kangeqiu.kq.R;

import com.nowagme.util.DownloadUtil;

public class DownAndShowImageTask extends AsyncTask<Void, Integer, Boolean> {

	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["
			+ DownAndShowImageTask.class.getName() + "]";

	private ImageView iv = null;
	private String url = null;
	private Button btn = null;

	private boolean isGrid = false;

	public DownAndShowImageTask(String url, ImageView iv) {
		this.iv = iv;
		this.url = url;
	}

	public DownAndShowImageTask(String url, ImageView iv, boolean isGrid) {
		this.iv = iv;
		this.url = url;
		this.isGrid = isGrid;
	}

	public DownAndShowImageTask(String url, Button btn) {
		this.btn = btn;
		this.url = url;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		logi("downImageTask:void onProgressUpdate(Integer... values)");
	}

	@Override
	protected void onPostExecute(Boolean result) {
		logi("void onPostExecute(Boolean result)");
		if (!result)
			return;
		String cacheImageFullFileName = AppConfig.getInstance()
				.makeCacheImageFullFileName(url);
		if (iv != null) {
			if (!isGrid) {
				Bitmap bm = BitmapFactory.decodeFile(cacheImageFullFileName);
				iv.setImageBitmap(bm);
			} else {
				if (url.equals(iv.getTag(R.id.iconUrl))) {
					String cacheTrueName = AppConfig.getInstance()
							.makeCacheImageFullFileName(url);
					Bitmap bm = BitmapFactory.decodeFile(cacheTrueName);
					iv.setImageBitmap(bm);
				}
			}
		}
		if (btn != null) {
			Drawable newDrawable = Drawable
					.createFromPath(cacheImageFullFileName);
			Drawable[] drawables = btn.getCompoundDrawables();
			int index = 0;
			for (Drawable drawable : drawables) {
				if (drawable != null) {
					newDrawable.setBounds(drawable.getBounds());
					drawables[index] = newDrawable;
				}
				index++;
			}
			btn.setCompoundDrawables(drawables[0], drawables[1], drawables[2],
					drawables[3]);
		}

	}

	@Override
	protected Boolean doInBackground(Void... args) {
		logi("Boolean doInBackground(Void... args)");
		if (url == null || url.length() == 0)
			return false;
		// 判断是否已经有缓存了
		String cacheImageFullFileName = AppConfig.getInstance()
				.makeCacheImageFullFileName(url);
		boolean hasCache = AppConfig.getInstance().checkCacheImageExists(
				cacheImageFullFileName);
		if (hasCache) {
			logi("Cached:" + cacheImageFullFileName);
			return true;
		} else {
			logi("NO Cache:" + cacheImageFullFileName);
			DownloadUtil newDownloadUtil = new DownloadUtil(url);
			int ret = newDownloadUtil.down2sd(cacheImageFullFileName);
			if (ret == 1)
				return true;
			return false;
		}
	}

	/**
	 * log information
	 * 
	 * @param msg
	 */
	public static void logi(String msg) {
		Log.i(TAG, CLASS_NAME + " " + msg);
	}

}
