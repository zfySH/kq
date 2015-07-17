package com.jingyi.MiChat.core.report;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.database.Cursor;

import com.jingyi.MiChat.core.http.HttpUtil;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.jingyi.MiChat.core.log.MCLog;
import com.jingyi.MiChat.core.thread.CachedLowThreadHandler;
import com.jingyi.MiChat.setting.MCSetting;
import com.jingyi.MiChat.sqlite.MCHttpLogDatabase;



public class MCHttpLog extends CachedLowThreadHandler<MCHttpLogItem>{

	private final static String URL = "http://3gtest.if.qidian.com:8099/Atom.axd/Api/Client/R10";

	private static MCHttpLog mQDHttpLog;
	public static MCHttpLog getInstance() {
		if (mQDHttpLog == null) {
			mQDHttpLog = new MCHttpLog();
		}
		return mQDHttpLog;
	}

	public static void Submit(String url, String param, long reqTime, int status, String content, int loadType, String requestHeader, String responseHeader) {
		// 如果是在缓存里加载到的，那么不记log
		if (loadType == 1)
			return;
		String logType = MCSetting.getInstance().GetSetting("SettingHttpLogType", "0");
		if ("0".equals(logType)) {
			// 0是不记录
			return;
		} else if ("1".equals(logType)) {
			// 1是只记录错误的
			if (status == 200) {
				return;
			}
		} else if ("2".equals(logType)) {
			// 全部记录
		}
		MCHttpLogItem item = new MCHttpLogItem(url, param, reqTime, status, content, loadType, 0, requestHeader, responseHeader);
		getInstance().add(item);

		MCLog.d("QDHttpLog submit:" + item.toString());
	}

	@Override
	protected void flushData(ArrayList<MCHttpLogItem> list) {
		MCLog.d("QDHttpLog flushData:" + list.size());

		try {
			// 删除超过100条
			MCHttpLogDatabase.getInstance().execSQL(
					"delete from log where (select count(LogId) from log)> 100 and LogId in (select LogId from log order by LogId desc limit (select count(LogId) from log) offset 100)");
			MCHttpLogDatabase.getInstance().beginTransaction();
			for (int i = 0; i < list.size(); i++) {
				MCHttpLogItem item = list.get(i);
				if (item.getUrl() == null || item.getUrl().length() == 0) {
					continue;
				}
				MCHttpLogDatabase.getInstance().insert("log", null, item.getContentValues());
			}
			MCHttpLogDatabase.getInstance().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				MCHttpLogDatabase.getInstance().endTransaction();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void postData() {
		// 捞数据
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		Cursor cursor = null;
		try {
			cursor = MCHttpLogDatabase.getInstance().query("log", null, "UploadStatus = 0", null, null, null, null);
			while (cursor.moveToNext()) {
				JSONObject item = new JSONObject();
				item.put("LogTime", cursor.getLong(cursor.getColumnIndex("LogTime")));
				item.put("Url", cursor.getString(cursor.getColumnIndex("Url")));
				item.put("ReqTime", cursor.getLong(cursor.getColumnIndex("ReqTime")));
				item.put("Status", cursor.getInt(cursor.getColumnIndex("Status")));
				String content = cursor.getString(cursor.getColumnIndex("Content"));
				item.put("Content", content == null ? "" : content);
				item.put("LoadType", cursor.getInt(cursor.getColumnIndex("LoadType")));
				String param = cursor.getString(cursor.getColumnIndex("Param"));
				item.put("Param", param == null ? "" : param);
				item.put("LogId", cursor.getLong(cursor.getColumnIndex("LogId")));
				array.put(item);
			}
			json.put("Data", array);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			cursor.close();
		}

		// 发数据

		try {
			ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
			String str = android.util.Base64.encodeToString(json.toString().getBytes("UTF-8"), android.util.Base64.DEFAULT);
			data.add(new BasicNameValuePair("data", str));
			HttpUtil http = new HttpUtil();
			http.setSaveLog(false);
			MCHttpResp resp = http.post(URL, data);
			if (resp.isSuccess()) {
				JSONObject resultJson = resp.getJson();
				int result = resultJson.optInt("Result", -1);
				if (result == 0) {
					try {
						MCHttpLogDatabase.getInstance().beginTransaction();

						for (int i = 0; i < array.length(); i++) {
							JSONObject obj = array.getJSONObject(i);
							MCHttpLogDatabase.getInstance().execSQL("update log set UploadStatus = 1 where LogId=" + obj.optLong("LogId"));
						}

						MCHttpLogDatabase.getInstance().setTransactionSuccessful();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						MCHttpLogDatabase.getInstance().endTransaction();
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		MCLog.d("QDHttpLog postData");
	}

}
