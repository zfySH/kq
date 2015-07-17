package cn.kangeqiu.kq.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.Constant;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.db.UserDao;
import cn.kangeqiu.kq.domain.User;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.jingyi.MiChat.application.BaseApplication;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.football.EditfileActivity1;
import com.nowagme.util.CheckUpdataListener;
import com.nowagme.util.ToolUtil;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 开屏页
 * 
 */
public class SplashActivity extends BaseActivity {
	private RelativeLayout rootLayout;
	private TextView versionText;

	private static final int sleepTime = 2000;
	private String state = "";
	private String uid = "";

	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String device_id = tm.getDeviceId();

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);

			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}

			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}

			json.put("device_id", device_id);

			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.activity_splash);
		super.onCreate(arg0);

		// Log.d("tag", getDeviceInfo(this));
		MobclickAgent.updateOnlineConfig(this);
		rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		versionText = (TextView) findViewById(R.id.tv_version);

		versionText.setText(getVersion());
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
		new Thread(new Runnable() {
			public void run() {
				// if (DemoHXSDKHelper.getInstance().isLogined()) {
				boolean flag = false;
				if (flag) {
					// ** 免登陆情况 加载所有本地群和会话
					// 不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
					// 加上的话保证进了主页面会话和群组都已经load完毕
					long start = System.currentTimeMillis();
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					long costTime = System.currentTimeMillis() - start;
					// 等待sleeptime时长
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					// 进入主页面
					startActivity(new Intent(SplashActivity.this,
							MainActivity.class));
					finish();
				} else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							UpdataUtil.checkUpdate(SplashActivity.this,
									new CheckUpdataListener() {

										@Override
										public void onSucces() {
											uid = ToolUtil.getValue(
													String.class, "uid");
											if (uid.equals("")) {
												startActivity(new Intent(
														SplashActivity.this,
														LoginActivity.class));
												finish();
											} else {

												doLoginHuanXin(uid);

												doPullDate("2030",
														new MCHttpCallBack() {
															@Override
															public void onSuccess(
																	MCHttpResp resp) {
																super.onSuccess(resp);
																try {
																	String resultCode = resp
																			.getJson()
																			.getString(
																					"result_code");
																	if (resultCode
																			.equals("0")) {
																		String nickname = (resp
																				.getJson()
																				.getString("nickname"));
																		if (nickname
																				.equals(""))
																			state = "1";
																		else
																			state = "0";

																		if (state
																				.equals("1")) {// 首次登录
																			startActivity(new Intent(
																					SplashActivity.this,
																					EditfileActivity1.class));
																		} else
																			startActivity(new Intent(
																					SplashActivity.this,
																					MainActivity.class));

																		AppConfig
																				.getInstance()
																				.setPlayerId(
																						Integer.parseInt(uid));
																		finish();
																	} else {
																		Toast.makeText(
																				SplashActivity.this,
																				resp.getJson()
																						.getString(
																								"message"),
																				Toast.LENGTH_SHORT)
																				.show();
																	}
																} catch (Exception e) {
																	e.printStackTrace();
																}
															}

															@Override
															public void onError(
																	MCHttpResp resp) {
																super.onError(resp);
															}

														});

												// 进入主页面
												// startActivity(new Intent(
												// SplashActivity.this,
												// MainActivity.class));

											}

										}
									});
						}
					});

				}
			}
		}).start();
	}

	private void doPullDate(String action, MCHttpCallBack listener) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", uid));
		new WebRequestUtil().execute(false,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listener);
	}

	private void doLoginHuanXin(final String uid) {

		final long start = System.currentTimeMillis();
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(uid, uid, new EMCallBack() {

			@Override
			public void onSuccess() {
				// umeng自定义事件，开发者可以把这个删掉

				// 登陆成功，保存用户名密码
				BaseApplication.getInstance().setUserName(uid);
				BaseApplication.getInstance().setPassword(uid);

				try {
					// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
					// ** manually load all local groups and
					// conversations in case we are auto login
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					// 处理好友和群组
					processContactsAndGroups();
				} catch (Exception e) {
					e.printStackTrace();
					// 取好友或者群聊失败，不让进入主页面
					runOnUiThread(new Runnable() {
						public void run() {
							BaseApplication.getInstance().logout(null);
							Toast.makeText(getApplicationContext(),
									R.string.login_failure_failed, 1).show();
						}
					});
					return;
				}
				// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMChatManager.getInstance()
						.updateCurrentUserNick(
								BaseApplication.currentUserNick.trim());
				if (!updatenick) {
					Log.v("LoginActivity", "update current user nick fail");
				}

			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
			}
		});

	}

	private void processContactsAndGroups() throws EaseMobException {
		// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
		List<String> usernames = EMContactManager.getInstance()
				.getContactUserNames();
		System.out.println("----------------" + usernames.toString());
		EMLog.d("roster", "contacts size: " + usernames.size());
		Map<String, User> userlist = new HashMap<String, User>();
		for (String username : usernames) {
			User user = new User();
			user.setUsername(username);
			setUserHearder(username, user);
			userlist.put(username, user);
		}
		// 添加user"申请与通知"
		User newFriends = new User();
		newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
		String strChat = getResources().getString(
				R.string.Application_and_notify);
		newFriends.setNick(strChat);

		userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
		// 添加"群聊"
		User groupUser = new User();
		String strGroup = getResources().getString(R.string.group_chat);
		groupUser.setUsername(Constant.GROUP_USERNAME);
		groupUser.setNick(strGroup);
		groupUser.setHeader("");
		userlist.put(Constant.GROUP_USERNAME, groupUser);

		// 存入内存
		BaseApplication.getInstance().setContactList(userlist);
		// 存入db
		UserDao dao = new UserDao(SplashActivity.this);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);

		// 获取黑名单列表
		List<String> blackList = EMContactManager.getInstance()
				.getBlackListUsernamesFromServer();
		// 保存黑名单
		EMContactManager.getInstance().saveBlackList(blackList);

		// 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
		EMGroupManager.getInstance().getGroupsFromServer();
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	/**
	 * 获取当前应用程序的版本号
	 */
	private String getVersion() {
		String st = getResources().getString(R.string.Version_number_is_wrong);
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return st;
		}
	}
}
