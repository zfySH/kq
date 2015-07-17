package com.jingyi.MiChat.sqlite;

import java.io.File;
import java.io.IOException;

import android.database.sqlite.SQLiteDatabase;

import com.jingyi.MiChat.application.ApplicationContext;
import com.jingyi.MiChat.config.MCFilePathConfig;
import com.jingyi.MiChat.core.io.MCFileUtil;


public class MCConfigDatabase extends MCDatabase {
	static MCConfigDatabase mInstance;
	
	public static synchronized MCConfigDatabase getInstance() {
		if (mInstance == null || mInstance.mDB == null || !mInstance.mDB.isOpen()) {
			mInstance = new MCConfigDatabase();
		}
		return mInstance;
	}
	
	private static final int DataBaseVersion = 0;
	private static final String CREATE_SETTING = "create table if not exists setting (Key text primary key ,Value text);";
	private static final String CREATE_IMEI_TABLE = "create table if not exists ImeiTable" + "(Imei text primary key)";

	private MCConfigDatabase() {
		long start = System.currentTimeMillis();
		if (this.mDB == null || !this.mDB.isOpen()) {
			try {
				File sdcardFile = new File(MCFilePathConfig.getSDCardConfigFilePath());
				File internalFile = ApplicationContext.getInstance().getDatabasePath("MiChatConfig");

				String parent = internalFile.getParent();
				File parentDir = new File(parent);
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}

				if (internalFile.exists()) { // 如果内部存储文件存在
					this.mDB = SQLiteDatabase.openOrCreateDatabase(internalFile, null);
				} else if (sdcardFile.exists()) // 如果内部存储不在，SD卡在,并且这个文件大于10K,并且不是单本
				{
					MCFileUtil.copyFile(sdcardFile, internalFile, true);
					this.mDB = SQLiteDatabase.openOrCreateDatabase(internalFile, null);
				} else { // 如果两个都不�?
					if (!internalFile.exists()) { // 判断文件是否存在
						try {
							internalFile.createNewFile(); // 创建文件
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					try {
						this.mDB = SQLiteDatabase.openOrCreateDatabase(internalFile, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
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
	protected void createTables() {
		if (mDB == null)
			return;
		try {
			mDB.beginTransaction();
			mDB.execSQL(CREATE_SETTING);
			mDB.execSQL(CREATE_IMEI_TABLE);
			mDB.setVersion(DataBaseVersion);
			mDB.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDB.endTransaction();
		}

	}

	@Override
	protected void upgradeDB() {
		if (mDB == null)
			return;

		int oldVersion = mDB.getVersion();
		if (oldVersion != DataBaseVersion) {
			try {
				this.mDB.setVersion(DataBaseVersion);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	@Override
	public void CloseDB() {
		super.CloseDB();
		File sdcardFile = new File(MCFilePathConfig.getRootPath() + "MiChatConfig");
		File internalFile = ApplicationContext.getInstance().getDatabasePath("MiChatConfig");
		MCFileUtil.copyFile(internalFile, sdcardFile, true);
	}

}
