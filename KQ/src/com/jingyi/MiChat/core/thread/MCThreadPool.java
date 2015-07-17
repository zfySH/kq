package com.jingyi.MiChat.core.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MCThreadPool {

	private static Object lockobj = new Object();
	
	public static final int PRIORITY_HIGH = 0;//��������ȼ����߼�
	public static final int PRIORITY_MEDIUM = 1;//��������ȼ����м�
	public static final int PRIORITY_LOW = 2;//��������ȼ����ͼ�

	private static ExecutorService mHighPool;
	private static ExecutorService mMediumPool;
	private static ExecutorService mLowPool;
	
	private static ExecutorService getMediumInstance() {
		synchronized (lockobj) {
			if (mMediumPool == null || mMediumPool.isShutdown()) {
				mMediumPool = Executors.newFixedThreadPool(3);
			}
			return mMediumPool;
		}
	}

	private static ExecutorService getHighInstance() {
		synchronized (lockobj) {
			if (mHighPool == null || mHighPool.isShutdown()) {
				mHighPool = Executors.newFixedThreadPool(10);
			}
			return mHighPool;
		}
	}

	private static ExecutorService getLowInstance() {
		synchronized (lockobj) {
			if (mLowPool == null || mLowPool.isShutdown()) {
				mLowPool = Executors.newSingleThreadExecutor();
			}
			return mLowPool;
		}
	}
	
	public static ExecutorService getInstance(int priority){
		if (priority == PRIORITY_HIGH){
			return getHighInstance();
		} else if (priority == PRIORITY_MEDIUM){
			return getMediumInstance();
		} else {
			return getLowInstance();
		}
	}

	public static void shutdown() {
		if (mHighPool != null && !mHighPool.isShutdown()) {
			mHighPool.shutdown();
		}
		if (mMediumPool != null && !mMediumPool.isShutdown()) {
			mMediumPool.shutdown();
		}
		if (mLowPool != null && !mLowPool.isShutdown()) {
			mLowPool.shutdown();
		}
	}

}
