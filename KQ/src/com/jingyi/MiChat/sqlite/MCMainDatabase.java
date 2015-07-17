package com.jingyi.MiChat.sqlite;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jingyi.MiChat.application.ApplicationContext;
import com.jingyi.MiChat.config.MCFilePathConfig;
import com.jingyi.MiChat.core.io.MCFileUtil;

public class MCMainDatabase extends MCDatabase {
	
	static MCMainDatabase mInstance;
	private static final int DataBaseVersion = 1;
	
	public static synchronized MCMainDatabase getInstance(){
		if(mInstance == null || mInstance.mDB == null || !mInstance.isOpen()){
			mInstance = new MCMainDatabase();
		}
		return mInstance;
	}
	
	private static final String CREATE_USER = "create table if not exists user (UserId integer primary key autoincrement,UserToken text,ID text,UserName text,UserAge integer,Address text,Company text)";
	
	private MCMainDatabase(){
		if (this.mDB == null || !this.mDB.isOpen()) {
			Context app = ApplicationContext.getInstance();
			try {
				File sdcardFile = new File(MCFilePathConfig.getSDCardDBFilePath());
				File internalFile = app.getDatabasePath("MiChat");

				String parent = internalFile.getParent();
				File parentDir = new File(parent);
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}

				if (internalFile.exists()) { // 如果内部存储文件存在
					this.mDB = SQLiteDatabase.openOrCreateDatabase(internalFile, null);
				} else if (sdcardFile.exists() && sdcardFile.length() > 1024 * 10) // 如果内部存储不在，SD卡在,并且这个文件大于10K,并且不是单本
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
					createTables();
				}
				
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
			mDB.execSQL(CREATE_USER);
			
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
		Log.v("MiChat" + "MCMainDatabase  oldVersion:", ""+oldVersion);
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
		File sdcardFile = new File(MCFilePathConfig.getRootPath() + "MiChat");
		File internalFile = ApplicationContext.getInstance().getDatabasePath("MiChat");
		MCFileUtil.copyFile(internalFile, sdcardFile, true);
	}

}
