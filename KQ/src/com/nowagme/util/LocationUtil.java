package com.nowagme.util;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public final class LocationUtil {
	private static LocationUtil mThis;
	private BDLocationListener defListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation arg0) {
			location = arg0;
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	public static LocationUtil getInstance(Context context) {
		LocationUtil.context = context;
		mThis = new LocationUtil();
		return mThis;
	}

	private LocationClient mLocationClient;
	private BDLocation location = null;
	private static Context context;

	private LocationUtil() {
		initClient();
	}

	private void initClient() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("detail");
		option.setCoorType("bd09ll");
		option.setScanSpan(5000);
		option.disableCache(true);
		option.setPriority(LocationClientOption.GpsFirst);

		mLocationClient = new LocationClient(context);
		mLocationClient.setLocOption(option);
	}

	/**
	 * 开始获取位置
	 */
	public void startScan() {
		startScan(defListener);
	}

	public void appendListener(BDLocationListener listener) {
		mLocationClient.registerLocationListener(listener);
	}

	public void removeListener(BDLocationListener listener) {
		mLocationClient.unRegisterLocationListener(listener);
	}

	public void startScan(BDLocationListener listener) {
		if (mLocationClient.isStarted()) {
			mLocationClient.stop();
			initClient();
		}

		if (listener != null)
			mLocationClient.registerLocationListener(listener);
		else
			mLocationClient.registerLocationListener(defListener);
		
		mLocationClient.start();
	}

	/**
	 * 停止获取位置
	 */
	public void stopScan() {
		if (mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
	}

	/**
	 * 获取位置
	 * 
	 * @return
	 */
	public BDLocation getLocation() {
		return location;
	}

	public void onStop() {
		try {
			location = null;
			mLocationClient = null;
			this.finalize();
		} catch (Throwable e) {
		}
	}
}