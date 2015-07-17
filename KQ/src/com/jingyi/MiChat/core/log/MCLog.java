package com.jingyi.MiChat.core.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android.util.Log;

import com.jingyi.MiChat.appinfo.AppInfo;
import com.jingyi.MiChat.config.MCFilePathConfig;
import com.jingyi.MiChat.utils.StringUtil;


public class MCLog {

	public static void d(String msg) {
		if (AppInfo.getInstance().isDebug()) {
			Log.d("MiChat", msg);
		}
	}

	public static void message(String msg) {
		if (AppInfo.getInstance().isDebug()) {
			Log.d("message", msg);
			writeLogtoFile("d", "message", msg);
		}
	}

	public static void dSaveLog(String tag, String msg) {
		if (AppInfo.getInstance().isDebug()) {
			Log.d(tag, msg);
			writeLogtoFile("d", tag, msg);
		}
	}

	public static void cmfuTracker(String msg) {
		if (AppInfo.getInstance().isDebug()) {
			Log.d("CmfuTracker", msg);
		}
	}

	public static void e(String msg) {
		if (msg == null)
			return;
		Log.v("MiChat", msg);
	}

	public static void url(String msg) {
		if (AppInfo.getInstance().isDebug()) {
			Log.d("Url", msg);
		}
	}

	public static void data(String msg) {
		if (AppInfo.getInstance().isDebug()) {
			Log.d("Data", msg);
		}
	}

	public static void w(String msg) {
		if (AppInfo.getInstance().isDebug()) {
			Log.d("MiChatW", msg);
		}
	}

	private static void writeLogtoFile(String mylogtype, String tag, String text) {// 新建或打开日志文件
		Date nowtime = new Date();
		String needWriteFiel = StringUtil.formatDate(nowtime);
		String needWriteMessage = StringUtil.formatData02(nowtime) + "    " + mylogtype + "    " + tag + "    " + text + "\r\n";
		String path = MCFilePathConfig.getRootPath();
		File file = new File(path + "log.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
			BufferedWriter bufWriter = new BufferedWriter(filerWriter);
			bufWriter.write(needWriteMessage);
			bufWriter.newLine();
			bufWriter.close();
			filerWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
