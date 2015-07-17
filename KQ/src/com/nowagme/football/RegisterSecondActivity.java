package com.nowagme.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.Constant;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.MainActivity;
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
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.MyActivityManager;
import com.nowagme.util.ToolUtil;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;

/**
 * 注册页
 * 
 */
public class RegisterSecondActivity extends BaseSimpleActivity {
	private TextView tv_username;
	private EditText et_captcha, et_password, et_password2;

	private String username, uid, captcha, password, Uid;
	private int type;
	private Context context;
	@SuppressWarnings("unused")
	private boolean progressShow;

	private int recLen = 61;
	private Button time, msg_btn;
	private String currentUsername;
	private String currentPassword;
	MyActivityManager mam = MyActivityManager.getInstance();
	TextView tv_num;// 用来显示剩余字数<br>
	int num = 20;// 限制的最大字数
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_register_second);
		mam.pushOneActivity(RegisterSecondActivity.this);
		context = this;
		// 参数
		Bundle extras = this.getIntent().getExtras();
		username = extras.getString("username");
		type = extras.getInt("type");
		uid = extras.getString("uid");

		tv_username = (TextView) findViewById(R.id.abc_activity_register_second__tv_username);
		et_captcha = (EditText) findViewById(R.id.abc_activity_register_second__et_captcha);
		et_password = (EditText) findViewById(R.id.abc_activity_register_second__et_password);
		time = (Button) findViewById(R.id.time_show);
		msg_btn = (Button) findViewById(R.id.get_message_btn);
		// et_password2 = (EditText)
		// findViewById(R.id.abc_activity_register_second__et_password2);

		tv_username.setText(username);
		et_password.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,

			int after) {

			}

			@Override
			public void afterTextChanged(Editable edt) {

				try {

					String temp = edt.toString();

					String tem = temp.substring(temp.length() - 1,
							temp.length());

					char[] temC = tem.toCharArray();

					int mid = temC[0];

					if (mid >= 48 && mid <= 57) {// 数字

						return;

					}

					if (mid >= 65 && mid <= 90) {// 大写字母

						return;

					}

					if (mid > 97 && mid <= 122) {// 小写字母

						return;

					}

					edt.delete(temp.length() - 1, temp.length());

				} catch (Exception e) {

					// TODO: handle exception

				}

			}

		});
		et_password.addTextChangedListener(new TextWatcher() 

		 {

		     public void afterTextChanged(Editable edt) 

		     {

		         String temp = edt.toString();

		         int posDot = temp.indexOf(".");

		         if (posDot <= 0) return;

		         if (temp.length() - posDot - 1 > 2)

		         {

		             edt.delete(posDot + 3, posDot + 4);

		         }

		     }

		     public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

		     public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

		 });
		
	}

	final Handler handler = new Handler() {

		public void handleMessage(Message msg) { // handle message
			switch (msg.what) {
			case 1:
				recLen--;
				time.setText("重新发送" + recLen);

				if (recLen > 0) {
					Message message = handler.obtainMessage(1);
					handler.sendMessageDelayed(message, 1000); // send message
				} else {
					time.setVisibility(View.GONE);
					msg_btn.setVisibility(View.VISIBLE);
				}
			}

			super.handleMessage(msg);
		}
	};

	/**
	 * 确定
	 * 
	 * @param view
	 */
	public void save(View view) {
		et_password.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start,
					int before, int count) {
				temp = s;
				System.out.println("s=" + s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int number = num - s.length();
//				tv_num.setText("" + number);
				selectionStart = et_password.getSelectionStart();
				selectionEnd = et_password.getSelectionEnd();
				// System.out.println("start="+selectionStart+",end="+selectionEnd);
				if (temp.length() > num) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionStart;
					et_password.setText(s);
					et_password.setSelection(tempSelection);// 设置光标在最后
					Toast.makeText(RegisterSecondActivity.this,"字数过长，最多20位", Toast.LENGTH_SHORT).show();
				}else if (temp.length()<6) {
					Toast.makeText(RegisterSecondActivity.this, "字数过段，最少6位", Toast.LENGTH_SHORT).show();
				}
			}
		});
		captcha = et_captcha.getText().toString().trim();
		password = et_password.getText().toString().trim();
		// password2=et_password2.getText().toString().trim();

		if (TextUtils.isEmpty(captcha)) {
			Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
			et_captcha.requestFocus();
			return;
		}

		if (TextUtils.isEmpty(password)) {
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			et_password.requestFocus();
			return;
		}

		// if(TextUtils.isEmpty(password2)){
		// Toast.makeText(this, "请输入密码确认", Toast.LENGTH_SHORT).show();
		// et_password2.requestFocus();
		// return;
		// }
		//
		// if(!password.equals(password2)){
		// Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
		// et_password.requestFocus();
		// return;
		// }

		// boolean debug = true;
		// if(debug){
		// doAfterSave();
		// return;
		// }

		// progressShow = true;
		// final ProgressDialog pd = new ProgressDialog(context);
		// pd.setCanceledOnTouchOutside(false);
		// pd.setOnCancelListener(new OnCancelListener() {
		// @Override
		// public void onCancel(DialogInterface dialog) {
		// progressShow = false;
		// }
		// });
		// pd.setMessage(getString(R.string.abc_data_loding));
		// pd.show();

		Map<String, String> parameters = new HashMap<String, String>();
		String action = "";
		if (type == 1) {
			action = "1100";
			parameters.put("u_mobile", username);
		} else if (type == 2) {
			action = "1110";
			parameters.put("u_uid", uid);
		}
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");

		parameters.put("u_pwd", password);
		parameters.put("u_captcha", captcha);
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
						// 成功

						if (type == 1) {
							Uid = data.get("uid").toString();
							Log.v("tag", "Uid___________________" + Uid);
							Toast.makeText(context, "注册成功！", Toast.LENGTH_SHORT)
									.show();
						} else if (type == 2) {
							Toast.makeText(context, "密码修改成功！",
									Toast.LENGTH_SHORT).show();
						}

						// 自动跳转到登录页面
						doAfterSave();
						Log.v("tag",
								"Uid__________**************************_________"
										+ Uid);
					}

					@Override
					public void onFail(Map<String, Object> data) {
						Toast.makeText(context, "操作失败:" + data.get("message"),
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError() {
					}
				});
	}

	/**
	 * 获取验证码
	 * 
	 * @param view
	 */

	public void getCaptcha(View view) {

		AlertDialog.Builder builder1 = new AlertDialog.Builder(
				RegisterSecondActivity.this);
		builder1.setCancelable(false);
		builder1.setMessage("我们将发送验证码到这个手机号码：" + username);
		builder1.setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				recLen = 61;
				progressShow = true;
				// final ProgressDialog pd = new ProgressDialog(context);
				// pd.setCanceledOnTouchOutside(false);
				// pd.setOnCancelListener(new OnCancelListener() {
				// @Override
				// public void onCancel(DialogInterface dialog) {
				// progressShow = false;
				// }
				// });
				// pd.setMessage(getString(R.string.abc_data_loding));
				// pd.show();

				Map<String, String> parameters = new HashMap<String, String>();
				String action = "";
				if (type == 1)
					action = "1099";
				else if (type == 2)
					action = "1109";
				parameters.put("app_action", action);
				parameters.put("app_platform", "0");
				parameters.put("u_mobile", username);
				HttpPostUtil mHttpPostUtil = null;
				try {
					AppConfig.getInstance().addSign(parameters);
					mHttpPostUtil = AppConfig.getInstance().makeHttpPostUtil(
							parameters);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// WEB request and deal the listener
				new WebRequestUtil(mHttpPostUtil)
						.executeWithOutCache(new WebRequestUtilListener() {

							@Override
							public void onSucces(Map<String, Object> data) {
								// 成功
								Toast.makeText(context,
										"验证码马上会发送到手机" + username + ",请注意查收！",
										Toast.LENGTH_SHORT).show();
								msg_btn.setVisibility(View.GONE);
								time.setVisibility(View.VISIBLE);
								Message message = handler.obtainMessage(1); // Message
								handler.sendMessageDelayed(message, 1000);
							}

							@Override
							public void onFail(Map<String, Object> data) {
								Toast.makeText(context,
										"操作失败:" + data.get("message"),
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onError() {
							}
						});

			}
		});

		builder1.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
		builder1.create().show();

	}

	/**
	 * 提交后执行的操作.
	 */
	public void doAfterSave() {
		if (type == 1) {
			// 返回登录页面
			Intent intent = new Intent(this, EditfileActivity1.class);
			Bundle bundle = new Bundle();
			bundle.putString("username", username);
			bundle.putString("pwd", password);
			bundle.putString("uid", Uid);

			intent.putExtras(bundle);
			startActivity(intent);
			// doLogin();
		} else {
			// 返回登录页面
			// Intent intent = new Intent(this, LoginActivity.class);
			// Bundle bundle = new Bundle();
			// bundle.putString("username", username);
			// bundle.putString("pwd", password);
			// intent.putExtras(bundle);
			// startActivity(intent);
			doLogin();
		}

	}

	private ProgressDialog pd;

	/**
	 * 登录
	 */
	private void doLogin() {
		progressShow = true;
		pd = new ProgressDialog(RegisterSecondActivity.this);
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
					if (resultCode.equals("0")) {
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
						AppConfig.getInstance().setPlayerId(playerId);
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
					Toast.makeText(RegisterSecondActivity.this,
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
	}

	private void doLoginHuanXin() {

		final long start = System.currentTimeMillis();
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(currentUsername, currentPassword,
				new EMCallBack() {

					@Override
					public void onSuccess() {
						// umeng自定义事件，开发者可以把这个删掉

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
						ToolUtil.putValue(String.class, "name", username);
						ToolUtil.putValue(String.class, "pwd", password);
						ToolUtil.putValue(String.class, "uid", currentUsername);

						runOnUiThread(new Runnable() {
							public void run() {
								pd.setMessage(getString(R.string.list_is_for));
							}
						});
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
						if (!RegisterSecondActivity.this.isFinishing())
							pd.dismiss();
						// 进入主页面

						// if (state.equals("1")) {// 首次登录
						// startActivity(new Intent(LoginActivity.this,
						// EditfileActivity1.class));
						// } else
						startActivity(new Intent(RegisterSecondActivity.this,
								MainActivity.class));
						finish();
					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(final int code, final String message) {
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

	private void doPullDate(String loginType, String action,
			MCHttpCallBack listener) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		if (action.equals("1001")) {
			pair.add(new BasicNameValuePair("u_mobile", username));
			pair.add(new BasicNameValuePair("u_pwd", password));
			pair.add(new BasicNameValuePair("u_type", "0"));
		}
		new WebRequestUtil().execute(false,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listener);
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
		UserDao dao = new UserDao(RegisterSecondActivity.this);
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

	public void back(View view) {
		finish();
	}

}
