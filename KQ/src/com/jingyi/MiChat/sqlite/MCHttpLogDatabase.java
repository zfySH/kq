package com.jingyi.MiChat.sqlite;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;

import com.jingyi.MiChat.config.MCFilePathConfig;


public class MCHttpLogDatabase extends MCDatabase{


	static MCHttpLogDatabase mInstance;

	public static synchronized MCHttpLogDatabase getInstance() {
		if (mInstance == null || mInstance.mDB == null || !mInstance.mDB.isOpen()) {
			mInstance = new MCHttpLogDatabase();
		}
		return mInstance;
	}

	private static final int DataBaseVersion = 1;
	private static final String CREATE_LOG = "create table if not exists log (LogId integer primary key autoincrement,LogTime integer,Url text,Param text,ReqTime integer,Status integer,Content text,LoadType integer,UploadStatus integer,RequestHeader text,ResponseHeader text);";

	private MCHttpLogDatabase() {
		if (this.mDB == null || !this.mDB.isOpen()) {
			try {
				File sdcardFile = new File(MCFilePathConfig.getSDCardQDHttpLogDBFilePath());
				try {
					this.mDB = SQLiteDatabase.openOrCreateDatabase(sdcardFile, null);
				} catch (Exception e) {
					e.printStackTrace();
				}

				createTables();
				upgradeDB();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}
	}

	@Override
	protected void upgradeDB() {
		if (mDB == null)
			return;

		int oldVersion = mDB.getVersion();
		try {
			if (oldVersion < 1) {
				mDB.execSQL("ALTER TABLE log ADD COLUMN RequestHeader text");
				mDB.execSQL("ALTER TABLE log ADD COLUMN ResponseHeader text");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (oldVersion != DataBaseVersion) {
			try {
				this.mDB.setVersion(DataBaseVersion);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void createTables() {
		if (mDB == null)
			return;
		try {
			mDB.beginTransaction();
			mDB.execSQL(CREATE_LOG);
			mDB.setVersion(DataBaseVersion);
			mDB.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDB.endTransaction();
		}
	}

}
