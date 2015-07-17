package com.jingyi.MiChat.application;

import android.app.Application;

public class ApplicationContext {
	private static Application mApplicationContext;
	public static Application getInstance(){
		return mApplicationContext;
	}
	
	public static void setApplicationContext(Application applicationContext){
		mApplicationContext = applicationContext;
	}
}
