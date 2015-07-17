package com.jingyi.MiChat.utils;

import java.io.File;

public class RootUtil {

	public static int isDeviceRooted() {
		if (checkRootMethod1()) {
			return 1;
		}
		if (checkRootMethod2()) {
			return 1;
		}
		if (checkRootMethod3()) {
			return 1;
		}
		return 0;
	}

	private static boolean checkRootMethod1() {
		String buildTags = android.os.Build.TAGS;

		if (buildTags != null && buildTags.contains("test-keys")) {
			return true;
		}
		return false;
	}

	private static boolean checkRootMethod2() {
		try {
			File file = new File("/system/app/Superuser.apk");
			if (file.exists()) {
				return true;
			}
		} catch (Exception e) {
		}

		return false;
	}

	private static boolean checkRootMethod3() {
		String[] places = {"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
		for (int i = 0; i < places.length; i++) {
			String path = places[i] + "su";
			File file = new File(path);
			if (file.exists()) {
				return true;
			}
		}
		return false;
	}

}
