package com.jingyi.MiChat.core.storage;

public interface IMCStorage {
	boolean put(String key,String value);
	boolean put(String key,byte[] value);
	String get(String key);
	byte[] getBytes(String key);
	boolean exists(String key);
}
