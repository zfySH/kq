package com.jingyi.MiChat.core.bitmap;

import android.content.Context;

import com.jingyi.MiChat.config.MCFilePathConfig;
import com.lidroid.xutils.BitmapUtils;

public class BitmapHelper {
	
	private static BitmapUtils mBitmapUtils;
	
	public static BitmapUtils getBitmapUtils(Context appContext){
		if(mBitmapUtils == null){
			mBitmapUtils = new BitmapUtils(appContext,MCFilePathConfig.getImagePath());
		}
		return mBitmapUtils;
	}

}
