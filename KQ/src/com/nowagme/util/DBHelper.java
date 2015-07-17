package com.nowagme.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private final static int dbVersion = 1;

	public DBHelper(Context context) {
		super(context, "KQ_" + dbVersion + ".db", null, dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String create_sql = "CREATE TABLE cacheTable (uri nvarchar(100) not null , jsonData text)";
		db.execSQL(create_sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
	}

}