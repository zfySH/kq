package com.jingyi.MiChat.core.storage;

import java.io.File;
import java.util.HashMap;

import com.jingyi.MiChat.core.io.MCFileUtil;
import com.jingyi.MiChat.utils.CRC64;



public class MCStorageFile implements IMCStorage{

	
	private static HashMap<String, MCStorageFile> fileMap;
	public static synchronized MCStorageFile getInstance(String path){
		if (fileMap == null){
			fileMap = new HashMap<String, MCStorageFile>();
		}
		if (!fileMap.containsKey(path)){
			try {
				MCStorageFile fileDB = new MCStorageFile(path);
				fileMap.put(path, fileDB);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fileMap.get(path);
	}
	
	String mPath;
	
	private MCStorageFile(String path){
		mPath = path;
	}

	@Override
	public boolean put(String key, String value) {
		String path = getCacheFilePath(key);
		return MCFileUtil.SaveFile(new File(path), value);
	}

	@Override
	public boolean put(String key, byte[] value) {
		String path = getCacheFilePath(key);
		return MCFileUtil.SaveFile(new File(path), value);
	}

	@Override
	public String get(String key) {
		String path = getCacheFilePath(key);
		return MCFileUtil.LoadFile(new File(path));
	}

	@Override
	public byte[] getBytes(String key) {
		String path = getCacheFilePath(key);
		return MCFileUtil.LoadFileBytes(new File(path));
	}

	@Override
	public boolean exists(String key) {
		String path = getCacheFilePath(key);
		File file = new File(path);
		return file.exists();
	}
	
	
	public String getCacheFilePath(String url) {
		long crcLong = CRC64.crc64Long(url);
		long subFolder = crcLong % 10;
		
		MCFileUtil.makeDir(mPath);
		String cachePath = mPath + "/" + subFolder + "/";
		MCFileUtil.makeDir(cachePath);
		
		String name = String.valueOf(crcLong);
		return cachePath + name;
	}
	

}
