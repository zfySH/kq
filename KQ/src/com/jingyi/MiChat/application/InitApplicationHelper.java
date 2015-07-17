package com.jingyi.MiChat.application;

import android.app.Application;

/**
 *
 * @author zhangyongfeng02
 *
 */
public class InitApplicationHelper {
	private Application mApplication;
	
	public InitApplicationHelper(Application app){
		this.mApplication = app;
	}
	
	public void init() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				initApplication();
			}
		}).start();
	}

	protected void initApplication() {
		//TODO
		
		
		
	}

}
