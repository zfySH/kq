package com.jingyi.MiChat.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

import com.jingyi.MiChat.sqlite.MCConfigDatabase;


public class MCSetting {

	public final static String SettingSource = "Source";
	public final static String SettingUUID = "UUID";
	public final static String SettingCmfuToken = "SettingCmfuToken";
	public final static String SettingIMEIList = "SettingIMEIList";

	private static MCSetting mInstance;

	public static synchronized MCSetting getInstance() {
		if (mInstance == null)
			mInstance = new MCSetting();
		return mInstance;
	}

	Map<String, String> settingSet;
	HashSet<String> imeiSet;

	private MCSetting() {

	}

	private void loadUserSettingFromDB() {
		settingSet = Collections.synchronizedMap(new HashMap<String, String>());
		Cursor cursor = null;
		try {
			cursor = MCConfigDatabase.getInstance().query("setting", null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				String key = cursor.getString(cursor.getColumnIndex("Key"));
				String value = cursor.getString(cursor.getColumnIndex("Value"));
				settingSet.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public String GetSetting(String key, String defaultValue) {
		if (settingSet == null) {
			loadUserSettingFromDB();
		}
		return settingSet.get(key) == null ? defaultValue : settingSet.get(key);
	}

	public boolean SetSetting(String key, String value) {
		if (settingSet == null) {
			loadUserSettingFromDB();
		}
		settingSet.put(key, value);
		return setUserSettingToDB(key, value);
	}

	public Map<String, String> GetSettingSet(){
		if (settingSet == null) {
			loadUserSettingFromDB();
		}
		return settingSet;
	}
	
	private boolean setUserSettingToDB(String key, String value) {
		ContentValues values = new ContentValues();
		values.put("Key", key);
		values.put("Value", value);
		long result = MCConfigDatabase.getInstance().replace("setting", null, values);
		if (result > 0)
			return true;
		return false;
	}

	public boolean updateImei(String imei) {
		ArrayList<String> list = getImeiList();
		boolean isFind = false;
		for (int i=0;i<list.size();i++){
			String item = list.get(i);
			if (item.equalsIgnoreCase(imei)){
				isFind = true;
			}
		}
		if (!isFind){
			list.add(imei);
			
			StringBuffer sb = new StringBuffer();
			for (int i=0;i<list.size();i++){
				sb.append(list.get(i) + "|");
			}
			String imeistr = sb.toString();
			imeistr = imeistr.substring(0, imeistr.length() - 1);
			SetSetting(SettingIMEIList, imeistr);
		}
		return true;
	}

	public ArrayList<String> getImeiList() {
		String str = GetSetting(SettingIMEIList, "");
		String[] array = str.split("\\|");
		ArrayList<String> result = new ArrayList<String>();
		for (int i=0;i<array.length;i++){
			result.add(array[i]);
		}
		return result;
	}
	
	public void resetSetting(){
		try {
			MCConfigDatabase.getInstance().beginTransaction();
			MCConfigDatabase.getInstance().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				MCConfigDatabase.getInstance().endTransaction();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		MCSetting.getInstance().clearSetting();
	}

	public void clearSetting() {
		loadUserSettingFromDB();
	}


}
