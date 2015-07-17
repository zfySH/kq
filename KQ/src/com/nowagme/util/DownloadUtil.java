package com.nowagme.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadUtil {
	
	
	/**
	 * http连接.
	 */
	private HttpURLConnection conn;
	
	/**
	 * sd card 目录. 
	 */
	private String sdcard;

	/**
	 * 要下载文件的网络http地址.
	 * 格式如:http://xxx.xxx.xxx/xxx.zip 等.
	 */
	private String url;
	
	public DownloadUtil(String url) {
		this.url=url;
		this.sdcard = SdcardUtil.getDir();
		initConnection();
	}
	
	/**
	 * 初始化连接.
	 */
	private void initConnection(){
		try {
			conn = (HttpURLConnection)(new URL(url)).openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取下载文件的长度.
	 * @return
	 */
	public int getLength(){
		return conn.getContentLength();
	}
	
	
	
    
    /**
     * 写文件到sd卡 demo 
     * 前提需要设置模拟器sd卡容量，否则会引发EACCES异常 
     * 先创建文件夹，在创建文件 
     * @param dir
     * @param filename
     * @param handler
     * @return
     */
    public int down2sd(String dir, String filename, DownloadUtilListener downloadUtilListener) {
        StringBuilder sb = new StringBuilder(sdcard).append(dir);
        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        //获取文件全名
        sb.append(filename);
        file = new File(sb.toString());
          
        OutputStream fos = null;
        InputStream is = null;
        try {
            is = conn.getInputStream();
            int length = conn.getContentLength();
            downloadUtilListener.onGetFileSize(length);
            //创建文件
            file.createNewFile();
            fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int totalLen = 0,len;
            while ((len=is.read(buf))!=-1){
                fos.write(buf,0,len);
                //同步更新数据
                totalLen+=len;
                downloadUtilListener.onProgressUpdate(totalLen);
            }
            downloadUtilListener.onFinishSuccess();
            return 1;
        } catch (Exception e) {
        	e.printStackTrace();
            return 0;
        } finally {
        	try {
                if(is!=null) {
                	is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        	
            try {
                if(fos!=null) {
                	fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    /**
     * 下载文件.
     * 此方法没有回调监听，因此在下载过程中无法知道进度情况.
     * 可以用于小文件等的快速下载.
     * 用于AsyncTask进行异步下载.
     * 
     * @param fullfilename 要保存到本地设备的文件名（含完整路径）
     * @return
     */
    public int down2sd(String fullfilename) {
    	//创建目录
    	int index = fullfilename.lastIndexOf(File.separator);
        File file = new File(fullfilename.substring(0, index));
        if (!file.exists()) {
            file.mkdirs();
        }
        //获取文件全名
        file = new File(fullfilename);
          
        OutputStream fos = null;
        InputStream is = null;
        try {
            is = conn.getInputStream();
            //创建文件
            file.createNewFile();
            fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len=is.read(buf))!=-1){
                fos.write(buf,0,len);
            }
            return 1;
        } catch (Exception e) {
        	e.printStackTrace();
            return 0;
        } finally {
        	try {
                if(is!=null) {
                	is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        	
            try {
                if(fos!=null) {
                	fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 内部回调接口类
     */
    public abstract class DownloadUtilListener{
    	/**
    	 * 获取下载文件的进度.
    	 * @param len
    	 */
        public abstract void onProgressUpdate(int len);
        
        /**
    	 * 获取下载文件总字节数.
    	 * @param len
    	 */
        public abstract void onGetFileSize(int len);
        
        /**
    	 * 下载完成.
    	 */
        public abstract void onFinishSuccess();
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
