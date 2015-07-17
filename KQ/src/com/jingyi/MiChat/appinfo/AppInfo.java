package com.jingyi.MiChat.appinfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.UUID;

import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.jingyi.MiChat.application.ApplicationContext;
import com.jingyi.MiChat.core.io.MCFileUtil;
import com.jingyi.MiChat.setting.MCSetting;
import com.jingyi.MiChat.utils.RootUtil;


public class AppInfo {

	private static AppInfo mInstance;
	private static boolean isLoadingInstance = false;
	public static AppInfo getInstance() {
		if (mInstance == null){
			if (isLoadingInstance){
				throw new IllegalStateException("不能在AppInfo加载中的时候，再调用AppInfo加载他本身");
			}
			isLoadingInstance = true;
			mInstance = new AppInfo();
			mInstance.initConfig();
			isLoadingInstance = false;
		}
		return mInstance;
	}
	private static final String OTHER = "0821CAAD409B8402";
	private String mVersionName;
	private int mIsRoot;
	private String mSource;
	private int mClientType;
	private int mVersionCode;
	private String mApkSource;
	private int mAPILevel = 4;
	private String mUserAgent;
	private String mIMEI;
	private String mIMSI;
	private String mICCID;
	private String mWifiMac;
	private String mSimSerial;
	private String mAndroidId;
	private String mCpuSerial;
	private String mSystemInfo;
	private int mScreenWidth;
	private int mScreenHeight;
	private String mSDK;
	private String mPhoneModel;
	private String mPhoneOS;
	private boolean mIsDebug = false;
	private int mBuild;

	private static final int China_LianTong = 0;
	private static final int China_DianXin = 1;
	private static final int China_YiDong = 2;

	private AppInfo() {
		init();
	}

	private void init() {
		long startTime = System.currentTimeMillis();
		// 获取IMEI
		TelephonyManager tm = (TelephonyManager) ApplicationContext.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
		try {
			mIMEI = tm.getDeviceId();
			mIMSI = tm.getSubscriberId();
			mICCID = tm.getSimSerialNumber();
			mSimSerial = tm.getSimSerialNumber();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			WifiManager wm = (WifiManager) ApplicationContext.getInstance().getSystemService(Context.WIFI_SERVICE);
			mWifiMac = wm.getConnectionInfo().getMacAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			mAndroidId = Secure.getString(ApplicationContext.getInstance().getContentResolver(), Secure.ANDROID_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		mCpuSerial = getCPUSerial();

		try {
			mSystemInfo = getSystemVersion();
		} catch (Exception ex) {
			ex.printStackTrace();
			mSystemInfo = "";
		}

		mSDK = Build.VERSION.RELEASE;
		mPhoneModel = Build.MODEL != null ? Build.MODEL.replaceAll("\\|", "_") : "";
		mPhoneOS = Build.VERSION.SDK != null ? Build.VERSION.SDK.replaceAll("\\|", "_") : "";


		mIsRoot = RootUtil.isDeviceRooted();

		try {
			String buildConfig = new String(MCFileUtil.LoadAsset(ApplicationContext.getInstance(), "BuildConfig.txt"));
			JSONObject json = new JSONObject(buildConfig);
			mIsDebug = json.optBoolean("Debug", false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		byte[] configData = MCFileUtil.LoadAsset(ApplicationContext.getInstance(), "config.txt");
		if (configData != null) {
			String config = new String(configData);
			String[] configArray = config.split("\\|");
			if (configArray.length > 0) {
				mApkSource = configArray[0];
			}
		}
		
		try {
			byte[] buildData = MCFileUtil.LoadAsset(ApplicationContext.getInstance(), "build.txt");
			if (buildData != null) {
				String buildTxt = new String(buildData);
				mBuild = Integer.valueOf(buildTxt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			PackageInfo info = ApplicationContext.getInstance().getPackageManager().getPackageInfo(ApplicationContext.getInstance().getPackageName(), 0);
			mVersionCode = info.versionCode;
			mVersionName = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		Log.d("MCChat", "appinfo使用时间:" + (System.currentTimeMillis() - startTime));
	}
	
	private void initConfig(){
		
		//******************注意appInfo初始化的时候，不能够再调用appinfo的getInstance方法

		if (mIMEI == null || mIMEI.length() == 0) {
			String setting = MCSetting.getInstance().GetSetting(MCSetting.SettingUUID, "");
			if (setting.equals("")) {
				if (mWifiMac != null && mWifiMac.length() > 0) {
					MCSetting.getInstance().SetSetting(MCSetting.SettingUUID, mWifiMac);
					mIMEI = mWifiMac;
				} else {
					String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 15);
					MCSetting.getInstance().SetSetting(MCSetting.SettingUUID, uuid);
					mIMEI = uuid;
				}
			} else {
				mIMEI = setting;
			}
		}

		MCSetting.getInstance().updateImei(mIMEI);
		
		//******************注意appInfo初始化的时候，不能够再调用appinfo的getInstance方法
		
		//******************注意appInfo初始化的时候，不能够再调用appinfo的getInstance方法
		try {
			String settingSource = MCSetting.getInstance().GetSetting(MCSetting.SettingSource, "");
			if (settingSource.equals("")) {
				mSource = mApkSource;
				MCSetting.getInstance().SetSetting(MCSetting.SettingSource, mSource);
			} else {
				mSource = settingSource;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//******************注意appInfo初始化的时候，不能够再调用appinfo的getInstance方法
	}

	public String getmSimSerial() {
		return mSimSerial;
	}

	public boolean isDebug() {
		return mIsDebug;
	}


	private String getCPUSerial() {
		String str = "", strCPU = "", cpuAddress = "0000000000000000";
		try {
			// 读取CPU信息
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			// 查找CPU序列号
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					// 查找到序列号所在行
					if (str.indexOf("Serial") > -1) {
						// 提取序列号
						strCPU = str.substring(str.indexOf(":") + 1, str.length());
						// 去空格
						cpuAddress = strCPU.trim();
						break;
					}
				} else {
					// 文件结尾
					break;
				}
			}
		} catch (Exception ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return cpuAddress;
	}

	/** 获取CPU名字 */
	public static String getCpuName() {
		try {
			FileReader fr = new FileReader("/proc/cpuinfo");
			BufferedReader br = new BufferedReader(fr);
			String cpuName = br.readLine();
			br.close();
			return cpuName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "arm";
	}

	/** 判断CPU */
	public static boolean isARM() {
		String cpuName = getCpuName();
		if (!TextUtils.isEmpty(cpuName)) {
			if (cpuName.toLowerCase().contains("arm")) {
				return true;
			}
		}
		return false;
	}

	private String getSystemVersion() {
		String[] version = {"null", "null", "null", "null"};
		String str1 = "/proc/version";
		String str2;
		String[] arrayOfString;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			version[0] = arrayOfString[2];// KernelVersion
			localBufferedReader.close();
		} catch (IOException e) {
		}
		version[1] = Build.VERSION.RELEASE;// firmware version
		version[2] = Build.MODEL;// model
		version[3] = Build.DISPLAY;// system version
		String e = "";
		for (String t : version) {
			e = e + t;
		}
		return e.replaceAll("\\|", "_");
	}


	public String getMCInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append(mIMEI);
		sb.append("|");
		sb.append(mVersionName);
		sb.append("|");
		sb.append(mScreenWidth);
		sb.append("|");
		sb.append(mScreenHeight);
		sb.append("|");
		sb.append(mSource);
		sb.append("|");
		sb.append(mSDK);
		sb.append("|");
		sb.append(mClientType);
		sb.append("|");
		sb.append(mPhoneModel);
		sb.append("|");
		sb.append(mVersionCode);
		sb.append("|");
		sb.append(mApkSource);
		sb.append("|");
		sb.append(mAPILevel);
		return sb.toString();
	}

	public String getDeviceInfo(boolean isGxb, double latitude, double longitude) {
		StringBuffer sb = new StringBuffer();
		if (!isGxb) {
			sb.append(mIMEI);
			sb.append("|");
			sb.append(latitude);
			sb.append("|");
			sb.append(longitude);
			sb.append("|");
			sb.append(mVersionName);
			sb.append("|");
			sb.append(mScreenWidth);
			sb.append("|");
			sb.append(mScreenHeight);
			sb.append("|");
			sb.append(mSource);
			sb.append("|");
			sb.append(mSDK);
			sb.append("|");
			sb.append(mClientType);
			sb.append("|");
			sb.append(mPhoneModel);
			sb.append("|");
			sb.append(mVersionCode);
			sb.append("|");
			sb.append(mApkSource);
			sb.append("|");
			sb.append(mAPILevel);
			sb.append("||");
			sb.append(mWifiMac);
			sb.append("|");
			sb.append(mSimSerial);
			sb.append("|");
			sb.append(mCpuSerial);
			sb.append("|");
			sb.append(mSystemInfo);
			sb.append("|");
			sb.append(mAndroidId);
			sb.append("|");
			sb.append(mIsRoot);
		} else {
			sb.append(mIMEI);
			sb.append("|");
			sb.append("|");
			sb.append("|");
			sb.append(mVersionName);
			sb.append("|");
			sb.append(mScreenWidth);
			sb.append("|");
			sb.append(mScreenHeight);
			sb.append("|");
			sb.append(mSource);
			sb.append("|");
			sb.append(mSDK);
			sb.append("|");
			sb.append(mClientType);
			sb.append("|");
			sb.append(mPhoneModel);
			sb.append("|");
			sb.append(mVersionCode);
			sb.append("|");
			sb.append(mApkSource);
			sb.append("|");
			sb.append(mAPILevel);
			sb.append("||");
			sb.append(mWifiMac);
			sb.append("|");
			sb.append(mSimSerial);
			sb.append("|");
			sb.append(mCpuSerial);
			sb.append("|");
			sb.append(mSystemInfo);
			sb.append("|");
			sb.append(mAndroidId);
			sb.append("|");
			sb.append(mIsRoot);
		}
		return sb.toString();
	}

	public String getDeviceInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append("IMEI:");
		sb.append(mIMEI);
		sb.append("|");
		sb.append("|");
		sb.append("PhoneModel:");
		sb.append(mPhoneModel);
		sb.append("|");
		sb.append("Source:");
		sb.append(mSource);
		sb.append("|");
		sb.append("SDK:");
		sb.append(mSDK);
		sb.append("|");
		sb.append("VersionCode:");
		sb.append(mVersionCode);
		sb.append("|");
		sb.append("VersionName:");
		sb.append(mVersionName);
		return sb.toString();
	}

	public String getUserAgent() {
		return mUserAgent;
	}

	public String getSource() {
		return mSource;
	}

	public int getVersionCode() {
		return mVersionCode;
	}

	public String getVersionName() {
		return mVersionName;
	}

	public String getApkSource() {
		return mApkSource;
	}

	public String getIMEI() {
		return mIMEI;
	}


	public void setUserAgent(String userAgent) {
		try {
			mUserAgent = userAgent + " MiChatAndroid/" + mVersionName + "/" + mVersionCode + "/" + mSource;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getIMSI() {
		return mIMSI;
	}

	public int getmScreenWidth() {
		return mScreenWidth;
	}

	public void setmScreenWidth(int mScreenWidth) {
		this.mScreenWidth = mScreenWidth;
	}

	public int getmScreenHeight() {
		return mScreenHeight;
	}

	public void setmScreenHeight(int mScreenHeight) {
		this.mScreenHeight = mScreenHeight;
	}

	public String getmWifiMac() {
		return mWifiMac;
	}

	public String getmSDK() {
		return mSDK;
	}

	public String getmPhoneModel() {
		return mPhoneModel;
	}

	public String getmSystemInfo() {
		return mSystemInfo;
	}

	public String getmAndroidId() {
		return mAndroidId;
	}

	public String getmCpuSerial() {
		return mCpuSerial;
	}
	
	public int getBuild(){
		return mBuild;
	}

	/**
	 * 得到运营商
	 * 
	 * @return
	 */
	public int getOperatorName() {
		if (mIMEI != null && !mIMEI.equals("") && mIMEI.length() >= 5) {
			// 截取IMSI前五位
			String iMsiStr = mIMEI.substring(0, 5);
			if (iMsiStr.equals("46001")) {
				// 中国联通
				return China_LianTong;
			} else if (iMsiStr.equals("46000") || iMsiStr.equals("46002") || iMsiStr.equals("46007")) {
				// 中国移动
				return China_YiDong;
			} else if (iMsiStr.equals("46003")) {
				// 电信
				return China_DianXin;
			}
		}
		return -1;
	}


}
