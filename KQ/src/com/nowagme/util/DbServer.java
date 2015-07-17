package com.nowagme.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbServer {

	private static DBHelper dbhelper;

	private static DbServer dbserver;

	public static DbServer getInstance(Context context) {
		if (dbserver == null) {
			dbserver = new DbServer();
		}
		if (dbhelper == null) {
			dbhelper = new DBHelper(context);
		}
		return dbserver;
	}

	public static void clearAll() {
		clear();
		clearOffline();
	}

	public static void clear() {
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL("delete from cacheTable ");
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public static void add(String uri, JSONObject json) {
		SQLiteDatabase db = dbhelper.getWritableDatabase();

		// uri = json.getJSONObject("Meta").get("Uri").toString()
		// .replaceFirst("/", "");
		db.beginTransaction();
		try {
			db.execSQL("delete from cacheTable where uri= ?",
					new Object[] { uri });
			db.execSQL("insert into cacheTable (uri , jsonData) values(?,?)",
					new Object[] { uri, json.toString() });

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public static JSONObject select(String uri) {
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from cacheTable where uri = ?",
				new String[] { uri });
		JSONObject object = null;
		if (cursor.moveToFirst()) {
			try {
				object = new JSONObject(cursor.getString(cursor
						.getColumnIndex("jsonData")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		cursor.close();
		return object;
	}

	public static void delete(String uri) {
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		db.execSQL("delete from cacheTable where uri= ?", new Object[] { uri });

	}

	// 离线操作
	public static void clearOffline() {
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL("delete from offlinecacheTable ");
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public static void addOffline(String uri, JSONObject json) {
		addOffline(uri, json.toString());
	}

	public static void addOffline(String uri, String json) {
		addOffline(uri, json, 0, "", 0,"");
	}

	public static void addOffline(String uri, String json, int isSyns,
			String title, int Priority,String updateTime) {
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL("delete from offlinecacheTable where uri= ?",
					new Object[] { uri });

			db.execSQL(
					"insert into offlinecacheTable (uri , jsonData, is_sync, title, priority, updateTime) values(?,?,?,?,?,?)",
					new Object[] { uri, json, isSyns, title, Priority, updateTime });

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public static JSONObject selectOffline(String uri) {
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from offlinecacheTable where uri = ?",
				new String[] { uri });
		JSONObject object = null;
		if (cursor.moveToFirst()) {
			try {
				object = new JSONObject(cursor.getString(cursor
						.getColumnIndex("jsonData")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		cursor.close();
		return object;
	}

	public static int selectOfflineIsSync(String uri) {
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from offlinecacheTable where uri = ?",
				new String[] { uri });
		int isSync = 0;
		if (cursor.moveToFirst()) {
			isSync = cursor.getInt(cursor.getColumnIndex("is_sync"));
		}
		cursor.close();
		return isSync;
	}

	public static List<String> selectAllOfflineUri() {

		List<String> list = new ArrayList<String>();
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from offlinecacheTable where is_sync = 0 order by priority desc,updateTime desc", null);
		// cursor.getString(1);
		// JSONObject object = null;
		String uri = null;
		while (cursor.moveToNext()) {
			// object = new JSONObject(cursor.getString(cursor
			// .getColumnIndex("jsonData")));
			uri = cursor.getString(cursor.getColumnIndex("uri"));
			list.add(uri);
		}
		cursor.close();
		return list;
	}

	public static String selectAllOfflineTitle(String uri) {

		// List<String> list = new ArrayList<String>();
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from offlinecacheTable where uri = ?",
				new String[] { uri });
		// cursor.getString(1);
		// JSONObject object = null;
		String title = null;
		while (cursor.moveToNext()) {
			// object = new JSONObject(cursor.getString(cursor
			// .getColumnIndex("jsonData")));
			title = cursor.getString(cursor.getColumnIndex("title"));
			// list.add(uri);
		}
		cursor.close();
		return title;
	}

	public static List<JSONObject> selectAllOffline() {
		List<JSONObject> list = new ArrayList<JSONObject>();

		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from offlinecacheTable where is_sync = 0", null);
		// cursor.getString(1);
		JSONObject object = null;
		while (cursor.moveToNext()) {
			try {
				object = new JSONObject(cursor.getString(cursor
						.getColumnIndex("jsonData")));
				list.add(object);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		cursor.close();
		return list;
	}

	public static void deleteOffline(String uri) {
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		db.execSQL("delete from offlinecacheTable where uri= ?",
				new Object[] { uri });

	}

	// public List<Object> page(int start, int end) {
	// SQLiteDatabase db = dbhelper.getReadableDatabase();
	// List<JsonDBCusor> page = new ArrayList<JsonDBCusor>();
	// Cursor cur = db.rawQuery(
	// "select id,name,time from jsontable order by id limit ?,?",
	// new String[] { String.valueOf(start), String.valueOf(end) });
	//
	// while (cur.moveToNext()) {
	// int id = cur.getInt(cur.getColumnIndex("id"));
	// String name = cur.getString(cur.getColumnIndex("name"));
	// int time = cur.getInt(cur.getColumnIndex("time"));
	// page.add(new JsonDBCusor(id, name, time));
	// }
	//
	// cur.close();
	//
	// return page;
	// }

	public Cursor curpage(int start, int end) {
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cur = db
				.rawQuery(
						"select id as _id,name,time from jsontable order by id limit ?,?",
						new String[] { String.valueOf(start),
								String.valueOf(end) });

		cur.moveToFirst();

		return cur;
	}

	public long getCount() {
		SQLiteDatabase db = dbhelper.getReadableDatabase();

		Cursor cur = db.rawQuery("select count(*) from jsontable", null);
		cur.moveToFirst();

		long count = cur.getLong(0);

		cur.close();

		return count;
	}

	public void transaction() {
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL("update student set time = 21 where id =5");
			db.execSQL("update student set time= 22 where id=6");
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}
}
