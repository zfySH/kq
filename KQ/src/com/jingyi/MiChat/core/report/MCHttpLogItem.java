package com.jingyi.MiChat.core.report;

import android.content.ContentValues;

public class MCHttpLogItem {

	private String url;
	private long reqTime;
	private int status;
	private String content;
	private int loadType;
	private String param;
	private int UploadStatus;
	private String RequestHeader;
	private String ResponseHeader;

	public MCHttpLogItem(String url, String param, long reqTime, int status, String content, int loadType, int uploadStatus, String requestHeader, String responseHeader) {
		this.url = url;
		this.reqTime = reqTime;
		this.status = status;
		if (content != null){
			if (content.length() > 100){
				this.content = content.substring(0, 100);
			}else {
				this.content = content;
			}
		}
		
		this.loadType = loadType;
		this.param = param;
		UploadStatus = uploadStatus;
		this.RequestHeader = requestHeader;
		this.ResponseHeader = responseHeader;
	}

	public String getUrl() {
		return url;
	}

	 private static final String LOG_FORMAT = "%1$s url:%2$s,param:%3$s,reqTime:%4$s,status:%5$s,loadType:%6$s\ncontent:%7$s";
	
	 @Override
	 public String toString(){
		 return String.format(LOG_FORMAT,System.currentTimeMillis(),url,param,reqTime,status,loadType,content);
	 }

	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put("LogTime", System.currentTimeMillis());
		values.put("Url", url);
		values.put("ReqTime", reqTime);
		values.put("Status", status);
		values.put("Content", content);
		values.put("LoadType", loadType);
		values.put("Param", param);
		values.put("UploadStatus", UploadStatus);
		values.put("RequestHeader", RequestHeader);
		values.put("ResponseHeader", ResponseHeader);
		return values;
	}


}
