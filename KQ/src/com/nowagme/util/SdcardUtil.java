package com.nowagme.util;

import android.os.Environment;

public class SdcardUtil {
	
	/**
	 * 获取SDCard的目录.
	 * @param imageUrl
	 * @return
	 * @throws Exception
	 */
	public static String getDir(){
		return Environment.getExternalStorageDirectory()+"/";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
