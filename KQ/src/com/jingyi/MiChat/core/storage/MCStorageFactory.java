package com.jingyi.MiChat.core.storage;

import com.jingyi.MiChat.config.MCFilePathConfig;


public class MCStorageFactory {
	public static IMCStorage getStorage() {
		// 暂时先全部用snappyDB方式
		try {
			return MCStorageSnappyDB.getInstance(MCFilePathConfig.getCachePath());
		} catch (Exception e) {
			e.printStackTrace();
			return MCStorageFile.getInstance(MCFilePathConfig.getCachePath());
		}
	}

	public static MCStorageFile getImageStorage() {
		//暂时先全用文件方式
		return MCStorageFile.getInstance(MCFilePathConfig.getImagePath());
	}
}
