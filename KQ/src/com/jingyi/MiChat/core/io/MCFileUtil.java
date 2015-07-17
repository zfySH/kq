package com.jingyi.MiChat.core.io;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.GZIPOutputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import cn.xddai.chardet.CharsetDetector;

public class MCFileUtil {


	public static String charsetDetect(File file) {
		String _charset = "utf-8";
		try {
			InputStream fs = new FileInputStream(file);
			CharsetDetector charDect = new CharsetDetector();
			String[] probableSet = charDect.detectChineseCharset(fs);
			if (probableSet.length > 0) {
				for (int i = 0; i < probableSet.length; i++) {
					if (probableSet[i].startsWith("GB"))
						return "GBK";
				}
				if (probableSet[0].equalsIgnoreCase("windows-1252"))
					return "UTF-16LE";
				return probableSet[0];
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return _charset;
	}

	/**
	 * 
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String LoadFile(File file) {
		return LoadFile(file, "UTF-8");
	}

	public static String LoadFile(File file, String charset) {
		try {
			byte[] bytes = LoadFileBytes(file);
			if (bytes != null)
				return new String(bytes, charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] LoadFileBytes(File file) {
		if (file == null || !file.exists())
			return null;

		InputStream is = null;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] result = null;
		try {
			is = new FileInputStream(file);

			byte[] charBuffer = new byte[1024 * 8];
			int readsize = 0;
			while ((readsize = is.read(charBuffer)) > 0) {
				output.write(charBuffer, 0, readsize);
			}
			output.flush();
			result = output.toByteArray();

		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
				if (output != null)
					output.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;

	}

	public static boolean SaveFile(File file, String content) {
		return SaveFile(file, content, "UTF-8");
	}

	public static boolean SaveFile(File file, String content, String charset) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = content.getBytes(charset);
			fos.write(buffer);
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean SaveFile(File file, byte[] data) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * <p>
	 * Title: SaveFile
	 * </p>
	 * <p>
	 * Description: 图片存储于SD卡
	 * </p>
	 * 
	 * @param bm
	 * @param fileName
	 * @throws Exception
	 */
	public static void SaveFile(Bitmap bm, String fileName) throws Exception {
		File dirFile = new File(fileName);
		// 检测图片是否存在
		if (dirFile.exists()) {
			dirFile.delete(); // 删除原图片
		}
		File myCaptureFile = createFile(fileName, true);
		
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
		// 100表示不进行压缩，70表示压缩率为30%
		bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos.close();
	}

	public static boolean AppendFile(String name, String content) {
		try {
			FileOutputStream fos = new FileOutputStream(name, true);
			byte[] buffer = content.getBytes();
			fos.write(buffer);
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void copyFile(File sourcefile, File targetFile, boolean overwrite) {
		if (!sourcefile.exists())
			return;
		if (targetFile.exists()) {
			if (overwrite) {
				targetFile.delete();
			} else {
				return;
			}
		} else {
			createFile(targetFile, true);
		}
		try {
			InputStream is = new FileInputStream(sourcefile);
			FileOutputStream fos = new FileOutputStream(targetFile);
			byte[] buffer = new byte[1024];
			while (is.read(buffer) > -1) {
				fos.write(buffer);
			}
			is.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param oldPath
	 * @param newPath
	 * @return boolean
	 */
	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { 
				InputStream inStream = new FileInputStream(oldPath); // ����ԭ�ļ�
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // �ֽ��� �ļ���С
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	/*
	 * 
	 * @param oldPath String 
	 * 
	 * @param newPath String 
	 * 
	 * @return boolean
	 */
	public static void copyAllFile(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs();
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
					byte[] b = new byte[1444];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {
					copyAllFile(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void copyFile(InputStream is, File targetFile) {
		if (targetFile.exists()) {
			return;
		} else {
			createFile(targetFile, true);
		}
		try {
			FileOutputStream fos = new FileOutputStream(targetFile);
			/*
			 * byte[] buffer = new byte[1024]; while (is.read(buffer) > -1) {
			 * fos.write(buffer); }
			 */
			int bytesum = 0;
			int byteread = 0;
			byte[] buffer = new byte[1444];
			while ((byteread = is.read(buffer)) != -1) {
				bytesum += byteread; // �ֽ��� �ļ���С
				System.out.println(bytesum);
				fos.write(buffer, 0, byteread);
			}
			is.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyAssetsFile(Context ctx, String assetsPath, File targetFile) {
		InputStream is = null;
		try {
			is = ctx.getResources().getAssets().open(assetsPath);
			copyFile(is, targetFile);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static void createFile(File file, boolean isFile) {
		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				createFile(file.getParentFile(), false);
			} else {
				if (isFile) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					file.mkdirs();
				}
			}
		}
	}

	public static File createFile(String fileName, boolean isFile) {
		File file = new File(fileName);
		if(file.exists() && isFile){
			file.delete();
		}
		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				createFile(file.getParentFile(), false);
			} else {
				if (isFile) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					file.mkdirs();
				}
			}
		}
		return file;
	}

	public static byte[] LoadAsset(Context ctx, String path) {
		InputStream is;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] result = null;
		try {
			if (ctx == null)
				return null;
			if (ctx.getResources() == null)
				return null;
			if (ctx.getResources().getAssets() == null)
				return null;
			is = ctx.getResources().getAssets().open(path);

			byte[] charBuffer = new byte[1024 * 8];
			int readsize = 0;
			while ((readsize = is.read(charBuffer)) > 0) {
				output.write(charBuffer, 0, readsize);
			}
			output.flush();
			result = output.toByteArray();
			is.close();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getFileName(String path) {
		int index = path.lastIndexOf("/") + 1;
		String tmp = path.substring(index);
		if (tmp.indexOf("?") > -1) {
			tmp = tmp.substring(0, tmp.indexOf("?"));
		}
		return tmp;
	}

	public static void deleteAllFiles(File root) {
		if (root.exists()) {
			String deleteCmd = "rm -r " + root.getAbsolutePath();
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void moveAllFiles(String sourceFile, String destFile) {
		if (new File(sourceFile).exists()) {
			String deleteCmd = "rename " + sourceFile + " " + destFile;
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String formatUrl(String url) {
		if (url.contains("?")) {
			return url + "&random" + System.currentTimeMillis();
		} else {
			return url + "?random=" + System.currentTimeMillis();
		}
	}

	/** �ļ��Ƿ���� */
	public static boolean checkFileExist(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	/** �ļ��Ƿ���� */
	public static boolean checkFileExist(File file) {
		return file.exists();
	}

	/** ɾ���ļ� */
	public static boolean deleteFile(String fileName) {
		boolean res = false;
		if (!TextUtils.isEmpty(fileName)) {
			File file = new File(fileName);
			if (file.exists()) {
				res = file.delete();
			}
		}
		return res;
	}

	public static boolean deleteFolderFile(String filePath, boolean deleteThisPath) {
		boolean res = false;
		if (!TextUtils.isEmpty(filePath)) {
			File file = new File(filePath);
			if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFolderFile(files[i].getAbsolutePath(), true);
				}
				res = true;
			}
			if (deleteThisPath) {
				if (!file.isDirectory()) {
					file.delete();
					res = true;
				} else {
					if (file.listFiles().length == 0) {
						file.delete();
						res = true;
					}
				}
			}
		}
		return res;
	}

	public static void makeDir(String dirName) {
		File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	/**
	 * 
	 * @param dir
	 * @param extension
	 * @return
	 */
	public static File findFirstFileByExtension(File dir, final String extension) {
		if (!dir.exists())
			return null;
		if (!dir.isDirectory())
			return null;
		File[] files = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String fname) {
				return fname != null && fname.toLowerCase().endsWith(extension);
			}
		});

		if (files == null || files.length == 0) {
			return null;
		}

		return files[0];
	}

	public static File[] getFilesFromDir(String dirName) {
		File file = new File(dirName);
		if (file.exists()) {
			File[] files = file.listFiles();
			return files;
		}
		return null;
	}

	public static int ReadlittleEndianInt(InputStream dis) throws IOException {
		byte[] bytes = new byte[4];
		dis.read(bytes);
		ByteBuffer bytebuffer = ByteBuffer.wrap(bytes);
		bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
		int result = bytebuffer.getInt();
		return result;
	}

	public static long ReadlittleEndianLong(InputStream dis) throws IOException {
		byte[] bytes = new byte[8];
		int re = dis.read(bytes);
		if (re == -1) {
			return -1;
		}
		ByteBuffer bytebuffer = ByteBuffer.wrap(bytes);
		bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
		long result = bytebuffer.getLong();
		return result;
	}

	public static void createNoMeida(String dir) {
		File file = new File(dir + ".nomedia");
		if (file.exists()) {
			return;
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void scanDirAsync(Context ctx, String dir) {
		Intent scanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_DIR");
		scanIntent.setData(Uri.fromFile(new File(dir)));
		ctx.sendBroadcast(scanIntent);
	}

	/**
	 * 获取文件大小 飞文件夹
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static long getFileSizes(File f) {
		long s = 0;
		try {
			if (f.exists()) {
				FileInputStream fis = null;
				fis = new FileInputStream(f);
				s = fis.available();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return s;
	}

	/**
	 * gzip压缩文件
	 * 
	 * @param fromFile
	 * @param toFile
	 */
	public static void gzipFile(File fromFile, File toFile) {
		try {
			if (fromFile.exists()) {
				GZIPOutputStream out = null;
				if (!toFile.exists()) {
					toFile.createNewFile();
				}
				out = new GZIPOutputStream(new FileOutputStream(toFile));
				FileInputStream in = null;
				in = new FileInputStream(fromFile);
				byte[] buf = new byte[1024];
				int len;
				if (in != null) {
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
				}
				if (out != null) {
					out.finish();
					out.close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


}
