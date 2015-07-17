package com.jingyi.MiChat.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

	public static boolean isNetworkAvailable(Context ctx) {
		try {
			ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			return (info != null && info.isAvailable());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isWifiAvailable(Context ctx) {
		try {
			if (isNetworkAvailable(ctx)) {
				ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = cm.getActiveNetworkInfo();
				if (info != null && info.isAvailable() && info.getType() == ConnectivityManager.TYPE_WIFI) {
					return true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	public static String getNetworkAccessPoint(Context ctx) {
		if (isNetworkAvailable(ctx)) {
			ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				return info.getTypeName() + "-" + info.getExtraInfo();
			}
		}
		return "";
	}

}
