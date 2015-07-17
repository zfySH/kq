package com.jingyi.MiChat.core.cookie;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.jingyi.MiChat.setting.MCSetting;


public class MCHttpCookie {

	private static MCHttpCookie mInstance;
	public static synchronized MCHttpCookie getInstance(){
		if (mInstance == null)
			mInstance = new MCHttpCookie();
		return mInstance;
	}
	
	public String getCookies(){
		String token = MCSetting.getInstance().GetSetting(MCSetting.SettingCmfuToken, "");
		if (token !=null && token.length() > 0){
			//如果是老版本升级上来的，那么CookieSyncManager里可能没有token，那么需要写入CookieSyncManager
			return "" + token + "";
		}
		return null;
	}
	
	public void syncCookies(){
		CookieManager.getInstance().setCookie("", getCookies());
		CookieSyncManager.getInstance().sync();
	}
	
	public void setCmfuToken(String cmfuToken){
		MCSetting.getInstance().SetSetting(MCSetting.SettingCmfuToken, cmfuToken);
		syncCookies();
	}
	
	public String getCmfuToken(){
		return MCSetting.getInstance().GetSetting(MCSetting.SettingCmfuToken, "");
	}

}
