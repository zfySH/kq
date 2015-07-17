package com.nowagme.util;

import java.sql.Timestamp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.TypedValue;

import com.jingyi.MiChat.application.BaseApplication;

/**
 * Common Tool
 * 
 * @author zhu
 */
public final class ToolUtil {
	private final static Context context;
	static {
		context = BaseApplication.applicationContext;
	}

	private final static String SHARE_PREFERENCES_KEY = "shared";

	private static SharedPreferences getSharedPreferences() {
		return context.getSharedPreferences(SHARE_PREFERENCES_KEY,
				Context.MODE_PRIVATE);
	}

	private static Editor getEditor() {
		return getSharedPreferences().edit();
	}

	public static void remove(String key) {
		Editor editor = getEditor();
		editor.remove(key);
		editor.commit();
	}

	public static Timestamp getCurrentDateTime() {
		return new java.sql.Timestamp(new java.util.Date().getTime());
	}

	@SuppressWarnings("unchecked")
	public static <TObject extends Object> TObject getValue(Class<TObject> cls,
			String key) {
		if (cls == String.class)
			return (TObject) getSharedPreferences().getString(key, "");
		else if (cls == Boolean.class)
			return (TObject) Boolean.valueOf(getSharedPreferences().getBoolean(
					key, false));
		else if (cls == Float.class)
			return (TObject) Float.valueOf(getSharedPreferences().getFloat(key,
					0f));
		else if (cls == Integer.class)
			return (TObject) Integer.valueOf(getSharedPreferences().getInt(key,
					0));
		else if (cls == Long.class)
			return (TObject) Long.valueOf(getSharedPreferences()
					.getLong(key, 0));

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <TObject extends Object> TObject getValue(Class<TObject> cls,
			String key, TObject defVal) {
		if (cls == String.class)
			return (TObject) getSharedPreferences().getString(key,
					(String) defVal);
		else if (cls == Boolean.class)
			return (TObject) Boolean.valueOf(getSharedPreferences().getBoolean(
					key, (Boolean) defVal));
		else if (cls == Float.class)
			return (TObject) Float.valueOf(getSharedPreferences().getFloat(key,
					(Float) defVal));
		else if (cls == Integer.class)
			return (TObject) Integer.valueOf(getSharedPreferences().getInt(key,
					(Integer) defVal));
		else if (cls == Long.class)
			return (TObject) Long.valueOf(getSharedPreferences().getLong(key,
					(Long) defVal));

		return null;
	}

	public static <TObject extends Object> void putValue(Class<TObject> cls,
			String key, TObject value) {
		Editor editor = getEditor();
		if (cls == String.class) {
			editor.putString(key, (String) value);
		} else if (cls == Boolean.class) {
			editor.putBoolean(key, (Boolean) value);
		} else if (cls == Float.class) {
			editor.putFloat(key, (Float) value);
		} else if (cls == Integer.class) {
			editor.putInt(key, (Integer) value);
		} else if (cls == Long.class) {
			editor.putLong(key, (Long) value);
		}
		editor.commit();
	}

	public static int getPixels(int dipValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dipValue, context.getResources().getDisplayMetrics());
	}

	public static double getPixels(double dipValue) {
		return (double) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				(float) dipValue, context.getResources().getDisplayMetrics());
	}
}