package com.jingyi.MiChat.manager;

import com.jingyi.MiChat.setting.MCSetting;
import com.jingyi.MiChat.setting.SettingConsts;


public class MCThemeManager {
	public static int getQDTheme() {
		return Integer.parseInt(MCSetting.getInstance().GetSetting(SettingConsts.SettingIsNight, "0"));
	}

	public static void setQDTheme(int theme) {
		MCSetting.getInstance().SetSetting(SettingConsts.SettingIsNight, theme + "");
	}
}
