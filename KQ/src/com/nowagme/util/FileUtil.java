package com.nowagme.util;

import java.io.File;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


public class FileUtil {
	

	
	/**
	 * 创建目录.
	 * @param dir
	 * @throws Exception
	 */
	public static void mkdir(String dir) throws Exception{
		File f=null;
		try{
			f = new File(dir);
			if(f.exists()) return;
			f.mkdirs();
		}finally{
			f=null;
		}
	}

	
	/**
	 * uri转文件路径.
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	public static String uri2FilePath(Uri uri,ContentResolver cr) throws Exception{
		String data = MediaStore.Images.Media.DATA;
		Cursor cursor = cr.query(uri, new String[]{data}, null, null, null);
		String filePath = null;
		if (cursor != null) {
			cursor.moveToFirst();
			filePath = cursor.getString(cursor.getColumnIndex(data));
			cursor.close();
		}
		return filePath;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
