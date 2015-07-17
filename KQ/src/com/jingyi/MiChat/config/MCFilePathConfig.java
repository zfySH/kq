package com.jingyi.MiChat.config;

import java.io.File;

import android.os.Environment;

import com.jingyi.MiChat.application.ApplicationContext;


public class MCFilePathConfig {
	

	public String QIDIAN_PRIVATE_PATH = "";

	public static String getRootPath() {
		String uniqueName = "/MiChat/";
		String[] name = ApplicationContext.getInstance().getPackageName().split(".");
		
		String result = ApplicationContext.getInstance().getFilesDir().getAbsolutePath() + uniqueName;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File externalPath = Environment.getExternalStorageDirectory();
			if (externalPath.exists() && externalPath.canWrite()) {
				result = externalPath.getAbsolutePath() + uniqueName;
			}
		}
		File dir = new File(result);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return result;
	}

	public static String getCachePath() {
		return getSubPath("cache");
	}

	public static String getImagePath() {
		return getSubPath("image");
	}

	public static String getBookPath() {
		return getSubPath("book");
	}

	public static String getLogPath() {
		return getSubPath("log");
	}

	public static String getLocalCoverPath() {
		return getSubPath("cover");
	}

	public static String getSDCardDBFilePath() {
		return getRootPath() + "MiChat";
	}
	
	public static String getSDCardConfigFilePath() {
		return getRootPath() + "MiChatConfig";
	}

	public static String getSDCardQDHttpLogDBFilePath() {
		return getRootPath() + "MiChatHttpLog";
	}

	public static String getBookUserPath(int qdbookId, long userId) {
		return getBookPath() + userId + "/" + qdbookId + "/";
	}

	/** 获取书插图路径 */
	public static String getBookImageDirectoryPath() {
		return getSubPath("bookimage");
	}

	/** 得到书插图路径 */
	public static String getBookImageDownloadDirectoryPath() {
		return getSubPath("downloadimage");
	}

	/**
	 * 获取apk升级路径
	 * 
	 * @param context
	 * @return
	 */
	public static String getUpgrageAPKFilePath() {
		return getRootPath() + "MiChat.apk";
	}

	public static String getCloudConfigPath() {
		return getRootPath() + "Conf.json";
	}

	/**
	 * apk升级配置信息路径
	 * 
	 * @param context
	 * @return
	 */
	public static String getUpgrageAPKConfigPath() {
		return getRootPath() + "MiChatAndroidUpdateNew.xml";
	}

	private static String getSubPath(String subName) {
		String result = getRootPath() + "/" + subName;
		File dir = new File(result);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir.getAbsolutePath() + "/";
	}

	public static String getFontsPath() {
		return getRootPath() + "fonts/";
	}

	public static String getUrlPath() {
		return ApplicationContext.getInstance().getFilesDir().getAbsolutePath() + "/url.json";
	}
	
	public static String getHeadImageTempPath(){
		return getImagePath() + "headimage_temp.jpg";
	}



}
