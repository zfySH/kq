package com.jingyi.MiChat.core.thread;

import java.util.ArrayList;


public abstract class CachedLowThreadHandler<E> {

	private static final int MAX_COUNT = 10; // 数据量满10时
	private static final int MAX_TIME = 30 * 1000; // 每30秒
	
	private ArrayList<E> mList;
	private long lastPostTime;
	
	public CachedLowThreadHandler(){
		mList = new ArrayList<E>();
		lastPostTime = System.currentTimeMillis();
	}
	
	public void add(E item){
		mList.add(item);
		if (mList.size() >= MAX_COUNT || System.currentTimeMillis() >= (lastPostTime + MAX_TIME)){
			//如果超过10条，或者超过30秒，那么提交一把
			doStaff();
		}
	}
	
	boolean isThreadRunning = false;
	
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			isThreadRunning = true;
			flushData(mList);
			mList.clear();
			postData();
			isThreadRunning = false;
			lastPostTime = System.currentTimeMillis();
		}
	};
	
	public void doStaff(){
		if (!isThreadRunning){
			MCThreadPool.getInstance(MCThreadPool.PRIORITY_LOW).submit(runnable);
		}
	}
	
	protected abstract void flushData(ArrayList<E> list);
	protected abstract void postData();


}
