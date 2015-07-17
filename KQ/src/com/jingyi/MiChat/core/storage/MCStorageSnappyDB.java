package com.jingyi.MiChat.core.storage;

import java.util.HashMap;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

public class MCStorageSnappyDB implements IMCStorage{

	DB mDb;
	String mPath;
	private static HashMap<String, MCStorageSnappyDB> SnappyDBMap;
	public static synchronized MCStorageSnappyDB getInstance(String path) throws Exception{
		if (SnappyDBMap == null){
			SnappyDBMap = new HashMap<String, MCStorageSnappyDB>();
		}
		if (!SnappyDBMap.containsKey(path)){
			try {
				MCStorageSnappyDB snappyDB = new MCStorageSnappyDB(path);
				SnappyDBMap.put(path, snappyDB);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		return SnappyDBMap.get(path);
	}
	
	
	
	private MCStorageSnappyDB(String path) throws SnappydbException{
		mPath = path;
		mDb = DBFactory.open(mPath);
	}
	
	public void close(){
		if (mDb != null){
			try {
				mDb.close();
			} catch (SnappydbException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean put(String key, String value) {
		if (mDb != null){
			try {
				mDb.put(key, value);
				return true;
			} catch (SnappydbException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean put(String key, byte[] value) {
		if (mDb != null){
			try {
				mDb.put(key, value);
				return true;
			} catch (SnappydbException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public String get(String key) {
		if (mDb != null){
			try {
				if (mDb.exists(key)){
					return mDb.get(key);
				}
			} catch (SnappydbException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public byte[] getBytes(String key) {
		if (mDb != null){
			try {
				if (mDb.exists(key)){
					return mDb.getBytes(key);
				}
			} catch (SnappydbException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public boolean exists(String key) {
		if (mDb != null){
			try {
				return mDb.exists(key);
			} catch (SnappydbException e) {
				e.printStackTrace();
			}
		}
		return false;
	}



}
