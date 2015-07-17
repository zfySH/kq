/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.kangeqiu.kq.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.Constant;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.db.UserDao;
import cn.kangeqiu.kq.domain.Constants;
import cn.kangeqiu.kq.domain.User;
import cn.kangeqiu.kq.utils.CommonUtils;

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
import com.nowagme.football.RegisterFirstActivity;
import com.nowagme.util.MyActivityManager;
import com.nowagme.util.ToolUtil;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

//import com.umeng.analytics.MobclickAgent;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends AgentActivity {
	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["
			+ LoginActivity.class.getName() + "]";

	public static final int REQUEST_CODE_SETNICK = 1;
	public static final int REQUEST_CODE_FROM_REGISTER = 2;
	public static final int REQUEST_CODE_FROM_FORGET = 3;

	private EditText usernameEditText;
	private EditText passwordEditText;

	private boolean progressShow;
	private boolean autoLogin = false;

	private String currentUsername;
	private String currentPassword;
	// 整个平台的Controller, 负责管理整个SDK的配置、操作等处理
	private UMSocialService mController = UMServiceFactory
			.getUMSocialService(Constants.LOGINDESCRIPTOR);

	private String uId = "";
	private String state = "";
	MyActivityManager mam = MyActivityManager.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// // 如果用户名密码都有，直接进入主页面
		// if (DemoHXSDKHelper.getInstance().isLogined()) {
		// autoLogin = true;
		// startActivity(new Intent(LoginActivity.this, MainActivity.class));
		//
		// return;
		// }
		setContentView(R.layout.activity_login);
		mam.pushOneActivity(LoginActivity.this);
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);

		String name = ToolUtil.getValue(String.class, "name", "");
		String pwd = ToolUtil.getValue(String.class, "pwd", "");
		usernameEditText.setText(name);
		passwordEditText.setText(pwd);

		// 获取来源参数
		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			String username = extras.getString("username");
			if (!TextUtils.isEmpty(username)) {
				usernameEditText.setText(username);
			}
			String password = extras.getString("pwd");
			if (!TextUtils.isEmpty(password)) {
				passwordEditText.setText(password);
				login();
			}
		}

		// 如果用户名改变，清空密码
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				passwordEditText.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		// 设置新浪SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());

		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this, Constants.WXAPP_ID,
				Constants.WXAPP_SECRET);
		wxHandler.addToSocialSDK();
		// 需要修改的:这里需要拿到环信id
		// if (DemoApplication.getInstance().getUserName() != null) {
		// usernameEditText.setText(DemoApplication.getInstance().getUserName());
		// }
		// if (SeetingActivity.num == 1) {
		//
		// } else {
		// if (name != null && pwd != null) {
		// init();
		// doLogin();
		// } else {
		//
		// }
		// }

	}

	public void delete(View v) {
		usernameEditText.setText("");

	}

	public void WeiboLogin(View v) {
		progressShow = true;
		pd = new ProgressDialog(LoginActivity.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				progressShow = false;
			}
		});
		pd.setMessage("正在验证身份信息");
		pd.show();
		login(SHARE_MEDIA.SINA);
	}

	public void WXLogin(View v) {

		mController.doOauthVerify(this, SHARE_MEDIA.WEIXIN,
				new UMAuthListener() {
					@Override
					public void onStart(SHARE_MEDIA platform) {
					}

					@Override
					public void onError(SocializeException e,
							SHARE_MEDIA platform) {
						Toast.makeText(
								LoginActivity.this,
								"授权错误" + "code:" + e.getErrorCode() + ";msg:"
										+ e.toString() + ";platform:"
										+ platform, Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {

						Toast.makeText(LoginActivity.this, "授权完成",
								Toast.LENGTH_SHORT).show();
						// 获取相关授权信息
						mController.getPlatformInfo(LoginActivity.this,
								SHARE_MEDIA.WEIXIN, new UMDataListener() {
									@Override
									public void onStart() {
									}

									@Override
									public void onComplete(int status,
											Map<String, Object> info) {
										if (status == 200 && info != null) {
											StringBuilder sb = new StringBuilder();
											Set<String> keys = info.keySet();

											uId = info.get("openid").toString();

											progressShow = true;
											pd = new ProgressDialog(
													LoginActivity.this);
											pd.setCanceledOnTouchOutside(false);
											pd.setOnCancelListener(new OnCancelListener() {

												@Override
												public void onCancel(
														DialogInterface dialog) {
													progressShow = false;
												}
											});
											pd.setMessage("正在验证身份信息");
											pd.show();
											doPullDate("1", "2035",
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
																	pd.dismiss();
																	logi("doLoginSuccess():"
																			+ resp.getJson()
																					.toString());
																	int playerId = 0;
																	// state =
																	// resp
																	// .getJson()
																	// .getString(
																	// "is_first");
																	@SuppressWarnings("unchecked")
																	JSONArray list = resp
																			.getJson()
																			.getJSONArray(
																					"user");
																	// List<Map<String,
																	// Object>>
																	// list =
																	// (List<Map<String,
																	// Object>>)
																	// data
																	// .get("user");
																	playerId = Integer
																			.parseInt(list
																					.getJSONObject(
																							0)
																					.getString(
																							"id"));
																	String nickname = (String) (list
																			.getJSONObject(0)
																			.getString("nickname"));
																	if (nickname
																			.equals(""))
																		state = "1";
																	else
																		state = "0";

																	AppConfig
																			.getInstance()
																			.setPlayerId(
																					playerId);
																	// doShowTabMenu();
																	// 这里开始自动登录环信.
																	// 环信用户名和密码均为:playerId
																	// 设置环信用户昵称
																	BaseApplication.currentUserNick = nickname;
																	currentUsername = String
																			.valueOf(playerId);
																	currentPassword = String
																			.valueOf(playerId);

																	ToolUtil.putValue(
																			String.class,
																			"uid",
																			currentUsername);
																	if (state
																			.equals("1")) {// 首次登录
																		startActivity(new Intent(
																				LoginActivity.this,
																				EditfileActivity1.class));
																	} else
																		startActivity(new Intent(
																				LoginActivity.this,
																				MainActivity.class));
																	finish();
																	// 开始登录环信
																	doLoginHuanXin();
																} else {
																	pd.dismiss();
																	Toast.makeText(
																			getApplicationContext(),
																			resp.getJson()
																					.getString(
																							"message"),
																			Toast.LENGTH_SHORT)
																			.show();
																}
															} catch (Exception e) {
																// TODO
																// Auto-generated
																// catch block
																pd.dismiss();
																e.printStackTrace();
															}
														}

														@Override
														public void onError(
																MCHttpResp resp) {
															pd.dismiss();
															super.onError(resp);
														}

													});
											for (String key : keys) {
												sb.append(key
														+ "="
														+ info.get(key)
																.toString()
														+ "\r\n");
											}
										} else {
											pd.dismiss();
											Log.d("TestData", "发生错误：" + status);
										}
									}
								});
					}

					@Override
					public void onCancel(SHARE_MEDIA platform) {
						Toast.makeText(LoginActivity.this, "授权取消",
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	/**
	 * 授权。如果授权成功，则获取用户信息</br>
	 */
	private void login(final SHARE_MEDIA platform) {
		mController.doOauthVerify(this, platform, new UMAuthListener() {

			@Override
			public void onStart(SHARE_MEDIA platform) {
			}

			@Override
			public void onError(SocializeException e, SHARE_MEDIA platform) {
				pd.dismiss();
			}

			@Override
			public void onComplete(Bundle value, SHARE_MEDIA platform) {
				// Toast.makeText(LoginActivity.this, "onComplete", 0).show();
				String uid = value.getString("uid");
				if (!TextUtils.isEmpty(uid)) {
					getUserInfo(platform);
				} else {
					Toast.makeText(LoginActivity.this, "授权失败...",
							Toast.LENGTH_SHORT).show();
					pd.dismiss();
				}
			}

			@Override
			public void onCancel(SHARE_MEDIA platform) {
				pd.dismiss();
			}
		});
	}

	/**
	 * 获取授权平台的用户信息</br>
	 */
	private void getUserInfo(SHARE_MEDIA platform) {
		mController.getPlatformInfo(this, platform, new UMDataListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(int status, Map<String, Object> info) {
				// String showText = "";
				// if (status == StatusCode.ST_CODE_SUCCESSED) {
				// showText = "用户名：" +
				// info.get("screen_name").toString();
				// Log.d("#########", "##########" + info.toString());
				// } else {
				// showText = "获取用户信息失败";
				// }
				if (info != null) {
					// Toast.makeText(LoginActivity.this, info.toString(),
					// Toast.LENGTH_SHORT).show();

					uId = info.get("uid").toString();
					doPullDate("1", "2035", new MCHttpCallBack() {
						@Override
						public void onSuccess(MCHttpResp resp) {
							super.onSuccess(resp);
							try {
								String resultCode = resp.getJson().getString(
										"result_code");
								if (resultCode.equals("0")) {
									logi("doLoginSuccess()");

									// state = resp.getJson()
									// .getString("is_first");
									int playerId = 0;
									@SuppressWarnings("unchecked")
									JSONArray list = resp.getJson()
											.getJSONArray("user");
									playerId = Integer.parseInt(list
											.getJSONObject(0).getString("id"));
									String nickname = (String) (list
											.getJSONObject(0)
											.getString("nickname"));
									if (nickname.equals(""))
										state = "1";
									else
										state = "0";
									AppConfig.getInstance().setPlayerId(
											playerId);
									// doShowTabMenu();
									// 这里开始自动登录环信.
									// 环信用户名和密码均为:playerId
									// 设置环信用户昵称
									BaseApplication.currentUserNick = nickname;
									currentUsername = String.valueOf(playerId);
									currentPassword = String.valueOf(playerId);
									// 开始登录环信
									doLoginHuanXin();
								} else {
									pd.dismiss();
									Toast.makeText(
											getApplicationContext(),
											resp.getJson().getString("message"),
											Toast.LENGTH_SHORT).show();
								}
							} catch (Exception e) {
								// TODO
								// Auto-generated
								// catch block
								pd.dismiss();
								e.printStackTrace();
							}
						}

						@Override
						public void onError(MCHttpResp resp) {
							super.onError(resp);
							pd.dismiss();
						}

					});
				}
			}
		});
	}

	private void init() {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available,
					Toast.LENGTH_SHORT).show();
			return;
		}
		currentUsername = usernameEditText.getText().toString().trim();
		currentPassword = passwordEditText.getText().toString().trim();

		if (TextUtils.isEmpty(currentUsername)) {
			Toast.makeText(this, R.string.User_name_cannot_be_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(this, R.string.Password_cannot_be_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}

	}

	/**
	 * 登陆
	 * 
	 * @param view
	 */
	public void login(View view) {
		// 登录踢球吧
		login();

	}

	public void login() {
		init();
		// Intent intent = new Intent(LoginActivity.this,
		// cn.kangeqiu.kq.activity.AlertDialog.class);
		// intent.putExtra("editTextShow", true);
		// intent.putExtra("titleIsCancel", true);
		// intent.putExtra("msg",
		// getResources().getString(R.string.please_set_the_current));
		// intent.putExtra("edit_text", currentUsername);
		// startActivityForResult(intent, REQUEST_CODE_SETNICK);
		// 登录踢球吧
		doLogin();

	}

	private ProgressDialog pd;

	/**
	 * 登录
	 */
	private void doLogin() {
		progressShow = true;
		pd = new ProgressDialog(LoginActivity.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				progressShow = false;
			}
		});
		pd.setMessage("正在验证身份信息");
		pd.show();
		doPullDate(null, "1001", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				try {
					String resultCode = resp.getJson().getString("result_code");
					pd.dismiss();
					if (resultCode.equals("0")) {
						logi("doLoginSuccess()");
						int playerId = 0;
						@SuppressWarnings("unchecked")
						JSONArray list = resp.getJson().getJSONArray("user");
						// List<Map<String, Object>> list = (List<Map<String,
						// Object>>) data
						// .get("user");
						playerId = Integer.parseInt(list.getJSONObject(0)
								.getString("id"));
						String nickname = (String) (list.getJSONObject(0)
								.getString("nickname"));
						if (nickname.equals(""))
							state = "1";
						else
							state = "0";
						AppConfig.getInstance().setPlayerId(playerId);
						// doShowTabMenu();
						// 这里开始自动登录环信.
						// 环信用户名和密码均为:playerId
						// 设置环信用户昵称
						BaseApplication.currentUserNick = nickname;
						currentUsername = String.valueOf(playerId);
						currentPassword = String.valueOf(playerId);
						String editName = usernameEditText.getText().toString()
								.trim();
						String editPwd = passwordEditText.getText().toString()
								.trim();
						ToolUtil.putValue(String.class, "name", editName);
						ToolUtil.putValue(String.class, "pwd", editPwd);
						ToolUtil.putValue(String.class, "uid", currentUsername);
						if (state.equals("1")) {// 首次登录
							startActivity(new Intent(LoginActivity.this,
									EditfileActivity1.class));
						} else
							startActivity(new Intent(LoginActivity.this,
									MainActivity.class));
						finish();
						// 开始登录环信
						doLoginHuanXin();
					} else {
						Toast.makeText(getApplicationContext(),
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				try {
					Toast.makeText(LoginActivity.this,
							resp.getJson().getString("message"),
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pd.dismiss();
			}

		});
		// Map<String, String> parameters = new HashMap<String, String>();
		// parameters.put("u_mobile", currentUsername);
		// parameters.put("u_pwd", currentPassword);
		// parameters.put("app_action", "1001");
		// parameters.put("app_platform", "0");
		// parameters.put("u_type", "0");
		// try {
		// AppConfig.getInstance().addSign(parameters);
		// new postTask().execute(AppConfig.getInstance().makeHttpPostUtil(
		// parameters));
		// } catch (Exception e) {
		// e.printStackTrace();
		// doLoginErr();
		// }
	}

	private void doPullDate(String loginType, String action,
			MCHttpCallBack listener) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		if (action.equals("1001")) {
			pair.add(new BasicNameValuePair("u_mobile", currentUsername));
			pair.add(new BasicNameValuePair("u_pwd", currentPassword));
			pair.add(new BasicNameValuePair("u_type", "0"));
		}
		if (action.equals("2035")) {
			pair.add(new BasicNameValuePair("app_version", UpdataUtil
					.getAppVersion(this)));
			pair.add(new BasicNameValuePair("u_login_type", loginType));
			pair.add(new BasicNameValuePair("u_login_uid", uId));

		}
		new WebRequestUtil().execute(false,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listener);
	}

	// class postTask extends AsyncTask<HttpPostUtil, Integer,
	// WebCallResultUtil> {
	// @Override
	// protected void onProgressUpdate(Integer... values) {
	// logi("void onProgressUpdate(Integer... values)");
	// }
	//
	// @Override
	// protected void onPostExecute(WebCallResultUtil result) {
	// logi("void onPostExecute(String result)");
	// String responseText = result.getResponseText();
	// if (result.isCallRight()) {
	// try {
	// Map<String, Object> data = JsonUtil.parse(responseText);
	// String result_code = (String) data.get("result_code");
	// if ("0".equals(result_code)) {
	//
	// doLoginSuccess(data);
	// } else {
	// doLoginFail(data);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// doLoginErr();
	// }
	// } else {
	// doLoginErr();
	// }
	// }
	//
	// @Override
	// protected WebCallResultUtil doInBackground(HttpPostUtil... args) {
	// logi("String doInBackground(HttpPostUtil... args)");
	// HttpPostUtil httpPostUtil = args[0];
	// String message = null;
	// WebCallResultUtil mWebCallResultUtil = new WebCallResultUtil();
	// try {
	// boolean isOK = httpPostUtil.submit();
	// if (isOK) {
	// message = httpPostUtil.getResponseText();
	// mWebCallResultUtil.setCallRight(true);
	// } else {
	// message = "提交失败.";
	// mWebCallResultUtil.setCallRight(false);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// mWebCallResultUtil.setCallRight(false);
	// message = "提交错误:" + e.getMessage();
	// }
	// logi("message=" + message);
	// mWebCallResultUtil.setResponseText(message);
	// return mWebCallResultUtil;
	// }
	//
	// }

	private void doLoginHuanXin() {
		logi("doLoginHuanXin()");

		// progressShow = true;
		// final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
		// pd.setCanceledOnTouchOutside(false);
		// pd.setOnCancelListener(new OnCancelListener() {
		//
		// @Override
		// public void onCancel(DialogInterface dialog) {
		// progressShow = false;
		// }
		// });
		// pd.setMessage(getString(R.string.Is_landing));
		// pd.show();

		final long start = System.currentTimeMillis();
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(currentUsername, currentPassword,
				new EMCallBack() {

					@Override
					public void onSuccess() {
						// umeng自定义事件，开发者可以把这个删掉
						loginSuccess2Umeng(start);

						if (!progressShow) {
							return;
						}
						// 登陆成功，保存用户名密码
						BaseApplication.getInstance().setUserName(
								currentUsername);
						BaseApplication.getInstance().setPassword(
								currentPassword);

						// String editName =
						// usernameEditText.getText().toString()
						// .trim();
						// String editPwd =
						// passwordEditText.getText().toString()
						// .trim();
						// ToolUtil.putValue(String.class, "name", editName);
						// ToolUtil.putValue(String.class, "pwd", editPwd);
						// ToolUtil.putValue(String.class, "uid",
						// currentUsername);

						// runOnUiThread(new Runnable() {
						// public void run() {
						// pd.setMessage(getString(R.string.list_is_for));
						// }
						// });
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
									pd.dismiss();
									BaseApplication.getInstance().logout(null);
									Toast.makeText(getApplicationContext(),
											R.string.login_failure_failed, 1)
											.show();
								}
							});
							return;
						}
						// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
						boolean updatenick = EMChatManager.getInstance()
								.updateCurrentUserNick(
										BaseApplication.currentUserNick.trim());
						if (!updatenick) {
							Log.v("LoginActivity",
									"update current user nick fail");
						}
						if (!LoginActivity.this.isFinishing())
							pd.dismiss();
						// 进入主页面

						// if (state.equals("1")) {// 首次登录
						// startActivity(new Intent(LoginActivity.this,
						// EditfileActivity1.class));
						// } else
						// startActivity(new Intent(LoginActivity.this,
						// MainActivity.class));
						// finish();
					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(final int code, final String message) {
						loginFailure2Umeng(start, code, message);
						if (!progressShow) {
							return;
						}
						runOnUiThread(new Runnable() {
							public void run() {
								pd.dismiss();
								Toast.makeText(
										getApplicationContext(),
										getString(R.string.Login_failed)
												+ message, Toast.LENGTH_SHORT)
										.show();
							}
						});
					}
				});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		logi("onActivityResult:requestCode=" + requestCode + ",resultCode="
				+ resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_SETNICK:// 设置昵称

				break;

			case REQUEST_CODE_FROM_REGISTER:// 注册界面返回
				Bundle bundle = data.getExtras();
				currentUsername = bundle.getString("username");
				currentPassword = bundle.getString("pwd");
				usernameEditText.setText(currentUsername);
				passwordEditText.setText(currentPassword);
				break;
			}
		}
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
		UserDao dao = new UserDao(LoginActivity.this);
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
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		MobclickAgent.onEvent(this, "login_zhuce");
		TCAgent.onEvent(this, "login_zhuce");
		Intent intent = new Intent(this, RegisterFirstActivity.class);
		intent.putExtra("type", 1);
		startActivityForResult(intent, REQUEST_CODE_FROM_REGISTER);

		// startActivity(new I ntent(this, CreatMyHouseActivity.class));

	}

	public void forget(View view) {
		Intent intent = new Intent(this, RegisterFirstActivity.class);
		intent.putExtra("type", 2);
		startActivityForResult(intent, REQUEST_CODE_FROM_FORGET);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (autoLogin) {
			return;
		}
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

	private void loginSuccess2Umeng(final long start) {
		// runOnUiThread(new Runnable() {
		// public void run() {
		// long costTime = System.currentTimeMillis() - start;
		// Map<String, String> params = new HashMap<String, String>();
		// params.put("status", "success");
		// MobclickAgent.onEventValue(LoginActivity.this, "login1",
		// params, (int) costTime);
		// MobclickAgent.onEventDuration(LoginActivity.this, "login1",
		// (int) costTime);
		// }
		// });
	}

	private void loginFailure2Umeng(final long start, final int code,
			final String message) {
		// runOnUiThread(new Runnable() {
		// public void run() {
		// long costTime = System.currentTimeMillis() - start;
		// Map<String, String> params = new HashMap<String, String>();
		// params.put("status", "failure");
		// params.put("error_code", code + "");
		// params.put("error_description", message);
		// MobclickAgent.onEventValue(LoginActivity.this, "login1",
		// params, (int) costTime);
		// MobclickAgent.onEventDuration(LoginActivity.this, "login1",
		// (int) costTime);
		//
		// }
		// });
	}

	/**
	 * log information
	 * 
	 * @param msg
	 */
	public static void logi(String msg) {
		Log.i(TAG, CLASS_NAME + " " + msg);
	}
}
