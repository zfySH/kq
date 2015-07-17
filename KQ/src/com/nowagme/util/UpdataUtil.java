package com.nowagme.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;
import cn.kangeqiu.kq.domain.Constants;

import com.nowagme.football.AppConfig;

public class UpdataUtil {

	public static void checkUpdate(final Context context,
			final CheckUpdataListener listener) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", "2029");
		parameters.put("app_platform", "0");

		HttpPostUtil mHttpPostUtil = null;
		try {
			AppConfig.getInstance().addSign(parameters);
			mHttpPostUtil = AppConfig.getInstance()
					.makeHttpPostUtil(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// WEB request and deal the listener
		new WebRequestUtil(mHttpPostUtil)
				.executeWithOutCache(new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						if (data.get("result_code").toString().equals("0")) {
							int serverVersionCode = Integer.parseInt(data.get(
									"version").toString());
							int userVersionCode = getVersionCode(context);
							if (userVersionCode >= serverVersionCode) {
								listener.onSucces();
							} else {
								UpdateManager updateManager = new UpdateManager(
										context);
								updateManager.checkUpdateInfo(data.get("url")
										.toString());
							}
						} else {
							Toast.makeText(context, "版本获取失败",
									Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFail(Map<String, Object> data) {
					}

					@Override
					public void onError() {
					}
				});
	}

	public static String getVersion(Context context)// 获取版本号
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "未知版本";
		}
	}

	public static int getVersionCode(Context context)// 获取版本号(内部识别号)
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public static String getAppVersion(Context context) {// 接口版本号
		// try {
		// ApplicationInfo appInfo = context.getPackageManager()
		// .getApplicationInfo(context.getPackageName(),
		// PackageManager.GET_META_DATA);
		// String msg = appInfo.metaData.getString("APP_VERSION");
		// return msg;
		// } catch (NameNotFoundException e) {
		// e.printStackTrace();
		// return "";
		// }
		return Constants.APP_VERSION;
	}
}
