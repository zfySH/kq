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
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.Constant;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.db.InviteMessgeDao;
import cn.kangeqiu.kq.db.UserDao;
import cn.kangeqiu.kq.domain.InviteMessage;
import cn.kangeqiu.kq.domain.User;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMNotifier;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.jingyi.MiChat.application.BaseApplication;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.medicine.app.BaseActivity;
import com.medicine.app.fragments.MenuFragment;
import com.medicine.app.sm.SlidingMenu;
import com.nowagame.kq.activity.MineActivity;
import com.nowagme.football.AppConfig;
import com.nowagme.football.FragmentData;
import com.nowagme.football.FragmentMatch;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.MyActivityManager;
import com.nowagme.util.WebRequestUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

//import com.umeng.analytics.MobclickAgent;

public class MainActivity extends BaseActivity {

	protected static final String TAG = "MainActivity";
	// 未读消息textview
	private TextView unreadLabel;
	// 未读通讯录textview
	// private TextView unreadAddressLable;

	private Button[] mTabs;
	private ContactlistFragment contactListFragment;
	// private ChatHistoryFragment chatHistoryFragment;
	private ChatAllHistoryFragment chatHistoryFragment;
	private SettingsFragment settingFragment;
	private MineActivity personFragment;
	private MenuFragment menuFragment;
	private FragmentMatch teamFragment;
	private FragmentData dataFragment;

	// private FragmentNearbyNew nearbyFragment;
	// private FragmentFinder finderFragment;
	private Fragment[] fragments;
	private int index;
	private Context context;
	private boolean progressShow;
	// 当前fragment的index
	private int currentTabIndex;
	private NewMessageBroadcastReceiver msgReceiver;
	// 账号在别处登录
	public boolean isConflict = false;
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;
	// public static DrawerLayout mDrawerLayout = null;
	// private RelativeLayout lv = null;
	private long keyDownTime;
	private int count = 0;
	// 定位相关
	LocationClient mLocClient;
	boolean isFirstLoc = true;// 是否首次定位
	private double lastLatitude = 0.0, lastLongitude = 0.0;// 末次的纬度，经度

	// private RelativeLayout drawer_1, drawer_2, drawer_3, drawer_4;

	/**
	 * 检查当前用户是否被删除
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

	private Fragment mContent;
	FrameLayout frame;
	RelativeLayout containter;

	public MainActivity() {
		super(R.string.app_name);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
	}

	public void openMenu() {
		getSlidingMenu().showMenu();
	}

	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 200:// 编辑成功
			if (personFragment.intr == 1) {
				menuFragment.inidate();
				personFragment.initData();
			} else {
				menuFragment.inidate();
			}
			// if (requestCode==0) {
			// personFragment.initData();
			//
			// }else if(requestCode==1){
			//
			// }
			// initData(true);

			break;
		case 300:// 推出成功
			// initData(true);
			finish();
			startActivity(new Intent(context, LoginActivity.class));
			break;
		case RESULT_OK:
			if (requestCode == MineActivity.REQUEST_CODE_FANS) {
				personFragment.initData();
			} else if (requestCode == MineActivity.REQUEST_CODE_GUANZHU) {
				personFragment.initData();
			} else if (requestCode == ChatAllHistoryFragment.REQUEST_CODE_CREATE_HOURSE) {
				chatHistoryFragment.RefreshData();
			}
			break;
		}

	}

	/**
	 * 百度定位.
	 */
	private void initBaiduLocation() {
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				postBaiduLocation(location);
			}

			@Override
			public void onReceivePoi(BDLocation location) {
				postBaiduLocation(location);
			}
		});
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType(AppConfig.getInstance().getCoordType()); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	/**
	 * 上报球员位置.
	 * 
	 * @param location
	 */
	private void postBaiduLocation(BDLocation location) {
		if (location == null)
			return;
		if (location.getLatitude() == lastLatitude
				&& location.getLongitude() == lastLongitude)
			return;

		// 上报位置
		lastLatitude = location.getLatitude();
		lastLongitude = location.getLongitude();

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", "1002");
		parameters.put("app_platform", "0");
		parameters.put("u_uid",
				String.valueOf(AppConfig.getInstance().getPlayerId()));
		parameters.put("u_w", String.valueOf(location.getLatitude()));
		parameters.put("u_j", String.valueOf(location.getLongitude()));

		HttpPostUtil mHttpPostUtil = null;
		try {
			AppConfig.getInstance().addSign(parameters);
			mHttpPostUtil = AppConfig.getInstance()
					.makeHttpPostUtil(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// WEB request and deal the listener
		// new WebRequestUtil(mHttpPostUtil).execute(new
		// WebRequestUtilListener() {
		//
		// @Override
		// public void onSucces(Map<String, Object> data) {
		// Log.d(TAG, "success");
		// }
		//
		// @Override
		// public void onFail(Map<String, Object> data) {
		// Log.v(TAG, "faild:" + data.get("message"));
		// }
		//
		// @Override
		// public void onError() {
		// Log.v(TAG, "post error");
		// }
		// });

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new FragmentMatch(R.color.red);
		// set the Above View
		setContentView(R.layout.content_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();
		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		menuFragment = new MenuFragment();
		personFragment = new MineActivity();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, menuFragment).commit();
		// customize the SlidingMenu
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		context = this;
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED,
						false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			BaseApplication.getInstance().logout(null);
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		} else if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}
		setContentView(R.layout.activity_main);
		initView();

		// MobclickAgent.setDebugMode( true );
		// --?--
		// MobclickAgent.updateOnlineConfig(this);

		if (getIntent().getBooleanExtra("conflict", false)
				&& !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false)
				&& !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}

		inviteMessgeDao = new InviteMessgeDao(this);
		userDao = new UserDao(this);
		// 这个fragment只显示好友和群组的聊天记录
		// chatHistoryFragment = new ChatHistoryFragment();
		// 显示所有人消息记录的fragment
		chatHistoryFragment = new ChatAllHistoryFragment();
		// contactListFragment = new ContactlistFragment();
		// settingFragment = new SettingsFragment();
		teamFragment = new FragmentMatch();
		dataFragment = new FragmentData();
		// nearbyFragment = new FragmentNearbyNew();
		// finderFragment = new FragmentFinder();
		// fragments = new Fragment[] { chatHistoryFragment,
		// nearbyFragment,finderFragment, teamFragment, personFragment };
		fragments = new Fragment[] { teamFragment, chatHistoryFragment,
				dataFragment, personFragment };
		// 添加显示第一个fragment
		// getSupportFragmentManager().beginTransaction()
		// .add(R.id.fragment_container, teamFragment).show(teamFragment)
		// .commit();

		// getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
		// chatHistoryFragment)
		// .add(R.id.fragment_container,
		// contactListFragment).hide(contactListFragment).show(chatHistoryFragment).commit();

		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager
				.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

		// 注册一个透传消息的BroadcastReceiver
		IntentFilter cmdMessageIntentFilter = new IntentFilter(EMChatManager
				.getInstance().getCmdMessageBroadcastAction());
		cmdMessageIntentFilter.setPriority(3);
		registerReceiver(cmdMessageReceiver, cmdMessageIntentFilter);

		// 注册一个离线消息的BroadcastReceiver
		// IntentFilter offlineMessageIntentFilter = new
		// IntentFilter(EMChatManager.getInstance()
		// .getOfflineMessageBroadcastAction());
		// registerReceiver(offlineMessageReceiver, offlineMessageIntentFilter);

		// setContactListener监听联系人的变化等
		// EMContactManager.getInstance().setContactListener(
		// new MyContactListener());
		// 注册一个监听连接状态的listener
		// EMChatManager.getInstance().addConnectionListener(
		// new MyConnectionListener());
		// 注册群聊相关的listener
		// EMGroupManager.getInstance().addGroupChangeListener(
		// new MyGroupChangeListener());
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();

		// 百度定位上报位置
		// initBaiduLocation();
	}

	public void refreshMatch() {
		teamFragment.refreshMatch();
	}

	public void refreshMessage(int hourseNum) {
		chatHistoryFragment.refreshMessage(hourseNum, count - hourseNum);
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		frame = (FrameLayout) findViewById(R.id.content_frame);
		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
		containter = (RelativeLayout) findViewById(R.id.fragment_container);
		mTabs = new Button[4];
		mTabs[0] = (Button) findViewById(R.id.btn_team);
		mTabs[1] = (Button) findViewById(R.id.btn_conversation);
		mTabs[2] = (Button) findViewById(R.id.btn_data);
		mTabs[3] = (Button) findViewById(R.id.btn_person);
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);
	}

	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_team:
			index = 0;
			MobclickAgent.onEvent(this, "match_live");
			TCAgent.onEvent(this, "match_live");

			break;
		case R.id.btn_conversation:
			index = 1;
			break;
		case R.id.btn_data:
			index = 2;
			MobclickAgent.onEvent(this, "data");
			TCAgent.onEvent(this, "data");

			break;
		case R.id.btn_person:
			index = 3;

			personFragment.initData();
			break;
		}

		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager()
					.beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				frame.setVisibility(View.GONE);
				containter.setVisibility(View.VISIBLE);
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 停止百度定位
		if (mLocClient != null)
			mLocClient.stop();

		// 注销广播接收者
		try {
			unregisterReceiver(msgReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(ackMessageReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(cmdMessageReceiver);
		} catch (Exception e) {
		}

		// try {
		// unregisterReceiver(offlineMessageReceiver);
		// } catch (Exception e) {
		// }

		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}

	}

	/**
	 * 获取末条透传消息内容和全部未读的消息记录数.
	 */
	public void updateUnreadLabel() {
		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", "2025"));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		new WebRequestUtil().execute(true,
				AppConfig.getInstance().makeUrl(Integer.parseInt("2025")),
				pair, new MCHttpCallBack() {
					@Override
					public void onSuccess(MCHttpResp resp) {
						super.onSuccess(resp);
						try {

							String resultCode = resp.getJson().getString(
									"result_code");
							if (resultCode.equals("0")) {
								JSONArray records = (JSONArray) resp.getJson()
										.getJSONArray("records");
								int counts = 0;
								for (int i = 0; i < records.length(); i++) {
									// 未读消息记录数
									String unread_count = records
											.getJSONObject(i).getString(
													"unread_count");
									if (unread_count == null
											|| unread_count.length() == 0)
										unread_count = "0";
									int count = Integer.parseInt(unread_count);
									counts += count;
								}
								updateAllUnreadLabel(counts);
							} else {
								Toast.makeText(context,
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
						Toast.makeText(MainActivity.this,
								resp.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				});

	}

	/**
	 * 刷新未读消息数
	 */
	public void updateAllUnreadLabel(int sysUnreadCount) {
		// 未读的普通聊天消息数量
		int countNormal = getUnreadMsgCountTotal();

		// 总的未读消息数
		count = countNormal + sysUnreadCount;

		// update
		if (count > 0) {
			unreadLabel.setText(String.valueOf(count));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 刷新申请与通知消息数
	 */
	public void updateUnreadAddressLable() {
		// 周建强注释以下代码 20150309
		// runOnUiThread(new Runnable() {
		// public void run() {
		// int count = getUnreadAddressCountTotal();
		// if (count > 0) {
		// unreadAddressLable.setText(String.valueOf(count));
		// unreadAddressLable.setVisibility(View.VISIBLE);
		// } else {
		// unreadAddressLable.setVisibility(View.INVISIBLE);
		// }
		// }
		// });

	}

	/**
	 * 获取未读申请与通知消息
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		if (BaseApplication.getInstance().getContactList()
				.get(Constant.NEW_FRIENDS_USERNAME) != null)
			unreadAddressCountTotal = BaseApplication.getInstance()
					.getContactList().get(Constant.NEW_FRIENDS_USERNAME)
					.getUnreadMsgCount();
		return unreadAddressCountTotal;
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

	/**
	 * 新消息广播接收者
	 * 
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

			String from = intent.getStringExtra("from");
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			// 2014-10-22 修复在某些机器上，在聊天页面对方发消息过来时不立即显示内容的bug
			if (ChatActivity.activityInstance != null) {
				if (message.getChatType() == ChatType.GroupChat) {
					if (message.getTo().equals(
							ChatActivity.activityInstance.getToChatUsername()))
						return;
				} else {
					if (from.equals(ChatActivity.activityInstance
							.getToChatUsername()))
						return;
				}
			}

			// 注销广播接收者，否则在ChatActivity中会收到这个广播
			abortBroadcast();

			notifyNewMessage(message);

			// 刷新bottom bar消息未读数
			updateUnreadLabel();
			if (currentTabIndex == 1) {
				// 当前页面如果为聊天历史页面，刷新此页面
				if (chatHistoryFragment != null) {
					chatHistoryFragment.refresh();
				}
			}

		}
	}

	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();

			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");

			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);

				if (msg != null) {

					// 2014-11-5 修复在某些机器上，在聊天页面对方发送已读回执时不立即显示已读的bug
					if (ChatActivity.activityInstance != null) {
						if (msg.getChatType() == ChatType.Chat) {
							if (from.equals(ChatActivity.activityInstance
									.getToChatUsername()))
								return;
						}
					}

					msg.isAcked = true;
				}
			}

		}
	};

	/**
	 * 透传消息BroadcastReceiver
	 */
	private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();
			EMLog.d(TAG, "收到透传消息");
			// 获取cmd message对象
			@SuppressWarnings("unused")
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = intent.getParcelableExtra("message");
			// 获取消息body
			CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
			String action = cmdMsgBody.action;// 获取自定义action

			// 获取扩展属性 此处省略
			// message.getStringAttribute("");
			EMLog.d(TAG,
					String.format("透传消息：action:%s,message:%s", action,
							message.toString()));
			String st9 = getResources().getString(
					R.string.receive_the_passthrough);
			// Toast.makeText(MainActivity.this, st9 + action,
			// Toast.LENGTH_SHORT)
			// .show();

			// 更新未读标记
			updateUnreadLabel();

			if (currentTabIndex == 1) {
				// 当前页面如果为聊天历史页面，刷新此页面
				if (chatHistoryFragment != null) {
					chatHistoryFragment.refresh();
				}
			}
		}
	};

	/**
	 * 离线消息BroadcastReceiver sdk 登录后，服务器会推送离线消息到client，这个receiver，是通知UI
	 * 有哪些人发来了离线消息 UI 可以做相应的操作，比如下载用户信息
	 */
	// private BroadcastReceiver offlineMessageReceiver = new
	// BroadcastReceiver() {
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// String[] users = intent.getStringArrayExtra("fromuser");
	// String[] groups = intent.getStringArrayExtra("fromgroup");
	// if (users != null) {
	// for (String user : users) {
	// System.out.println("收到user离线消息：" + user);
	// }
	// }
	// if (groups != null) {
	// for (String group : groups) {
	// System.out.println("收到group离线消息：" + group);
	// }
	// }
	// }
	// };

	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;

	/***
	 * 好友变化listener
	 * 
	 */
	// private class MyContactListener implements EMContactListener {
	//
	// @Override
	// public void onContactAdded(List<String> usernameList) {
	// // 保存增加的联系人
	// Map<String, User> localUsers = BaseApplication.getInstance()
	// .getContactList();
	// Map<String, User> toAddUsers = new HashMap<String, User>();
	// for (String username : usernameList) {
	// User user = setUserHead(username);
	// // 添加好友时可能会回调added方法两次
	// if (!localUsers.containsKey(username)) {
	// userDao.saveContact(user);
	// }
	// toAddUsers.put(username, user);
	// }
	// localUsers.putAll(toAddUsers);
	// // 刷新ui
	// if (currentTabIndex == 1)
	// contactListFragment.refresh();
	//
	// }
	//
	// @Override
	// public void onContactDeleted(final List<String> usernameList) {
	// // 被删除
	// Map<String, User> localUsers = BaseApplication.getInstance()
	// .getContactList();
	// for (String username : usernameList) {
	// localUsers.remove(username);
	// userDao.deleteContact(username);
	// inviteMessgeDao.deleteMessage(username);
	// }
	// runOnUiThread(new Runnable() {
	// public void run() {
	// // 如果正在与此用户的聊天页面
	// String st10 = getResources().getString(
	// R.string.have_you_removed);
	// if (ChatActivity.activityInstance != null
	// && usernameList
	// .contains(ChatActivity.activityInstance
	// .getToChatUsername())) {
	// Toast.makeText(
	// MainActivity.this,
	// ChatActivity.activityInstance
	// .getToChatUsername() + st10,
	// Toast.LENGTH_LONG).show();
	// ChatActivity.activityInstance.finish();
	// }
	// updateUnreadLabel();
	// // 刷新ui
	// if (currentTabIndex == 1)
	// contactListFragment.refresh();
	// else if (currentTabIndex == 0)
	// chatHistoryFragment.refresh();
	// }
	// });
	//
	// }
	//
	// @Override
	// public void onContactInvited(String username, String reason) {
	// // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
	// List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
	//
	// for (InviteMessage inviteMessage : msgs) {
	// if (inviteMessage.getGroupId() == null
	// && inviteMessage.getFrom().equals(username)) {
	// inviteMessgeDao.deleteMessage(username);
	// }
	// }
	// // 自己封装的javabean
	// InviteMessage msg = new InviteMessage();
	// msg.setFrom(username);
	// msg.setTime(System.currentTimeMillis());
	// msg.setReason(reason);
	// Log.d(TAG, username + "请求加你为好友,reason: " + reason);
	// // 设置相应status
	// msg.setStatus(InviteMesageStatus.BEINVITEED);
	// notifyNewIviteMessage(msg);
	//
	// }
	//
	// @Override
	// public void onContactAgreed(String username) {
	// List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
	// for (InviteMessage inviteMessage : msgs) {
	// if (inviteMessage.getFrom().equals(username)) {
	// return;
	// }
	// }
	// // 自己封装的javabean
	// InviteMessage msg = new InviteMessage();
	// msg.setFrom(username);
	// msg.setTime(System.currentTimeMillis());
	// Log.d(TAG, username + "同意了你的好友请求");
	// msg.setStatus(InviteMesageStatus.BEAGREED);
	// notifyNewIviteMessage(msg);
	//
	// }
	//
	// @Override
	// public void onContactRefused(String username) {
	// // 参考同意，被邀请实现此功能,demo未实现
	// Log.d(username, username + "拒绝了你的好友请求");
	// }
	//
	// }

	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 */
	private void notifyNewIviteMessage(InviteMessage msg) {
		saveInviteMsg(msg);
		// 提示有新消息
		EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

		// 刷新bottom bar消息未读数
		updateUnreadAddressLable();
		// 刷新好友页面ui
		// if (currentTabIndex == 1)
		// contactListFragment.refresh();
	}

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 */
	private void saveInviteMsg(InviteMessage msg) {
		// 保存msg
		inviteMessgeDao.saveMessage(msg);
		// 未读数加1
		User user = BaseApplication.getInstance().getContactList()
				.get(Constant.NEW_FRIENDS_USERNAME);
		if (user.getUnreadMsgCount() == 0)
			user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
	}

	/**
	 * set head
	 * 
	 * @param username
	 * @return
	 */
	User setUserHead(String username) {
		User user = new User();
		user.setUsername(username);
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
		return user;
	}

	/**
	 * 连接监听listener
	 * 
	 */
	// private class MyConnectionListener implements EMConnectionListener {
	//
	// @Override
	// public void onConnected() {
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// chatHistoryFragment.errorItem.setVisibility(View.GONE);
	// }
	//
	// });
	// }
	//
	// @Override
	// public void onDisconnected(final int error) {
	// final String st1 = getResources().getString(
	// R.string.Less_than_chat_server_connection);
	// final String st2 = getResources().getString(
	// R.string.the_current_network);
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// if (error == EMError.USER_REMOVED) {
	// // 显示帐号已经被移除
	// showAccountRemovedDialog();
	// } else if (error == EMError.CONNECTION_CONFLICT) {
	// // 显示帐号在其他设备登陆dialog
	// showConflictDialog();
	// } else {
	// chatHistoryFragment.errorItem
	// .setVisibility(View.VISIBLE);
	// if (NetUtils.hasNetwork(MainActivity.this))
	// chatHistoryFragment.errorText.setText(st1);
	// else
	// chatHistoryFragment.errorText.setText(st2);
	//
	// }
	// }
	//
	// });
	// }
	// }

	/**
	 * MyGroupChangeListener
	 */
	// private class MyGroupChangeListener implements GroupChangeListener {
	//
	// @Override
	// public void onInvitationReceived(String groupId, String groupName,
	// String inviter, String reason) {
	// boolean hasGroup = false;
	// // for (EMGroup group : EMGroupManager.getInstance().getAllGroups())
	// // {
	// // if (group.getGroupId().equals(groupId)) {
	// // hasGroup = true;
	// // break;
	// // }
	// // }
	// // if (!hasGroup)
	// // return;
	//
	// // 被邀请
	// String st3 = getResources().getString(
	// R.string.Invite_you_to_join_a_group_chat);
	// EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
	// msg.setChatType(ChatType.GroupChat);
	// msg.setFrom(inviter);
	// msg.setTo(groupId);
	// msg.setMsgId(UUID.randomUUID().toString());
	// msg.addBody(new TextMessageBody(inviter + st3));
	// // 保存邀请消息
	// EMChatManager.getInstance().saveMessage(msg);
	// // 提醒新消息
	// EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
	//
	// runOnUiThread(new Runnable() {
	// public void run() {
	// updateUnreadLabel();
	// // 刷新ui
	// if (currentTabIndex == 0)
	// chatHistoryFragment.refresh();
	// if (CommonUtils.getTopActivity(MainActivity.this).equals(
	// GroupsActivity.class.getName())) {
	// GroupsActivity.instance.onResume();
	// }
	// }
	// });
	//
	// }
	//
	// @Override
	// public void onInvitationAccpted(String groupId, String inviter,
	// String reason) {
	//
	// }
	//
	// @Override
	// public void onInvitationDeclined(String groupId, String invitee,
	// String reason) {
	//
	// }
	//
	// @Override
	// public void onUserRemoved(String groupId, String groupName) {
	// // 提示用户被T了，demo省略此步骤
	// // 刷新ui
	// runOnUiThread(new Runnable() {
	// public void run() {
	// try {
	// updateUnreadLabel();
	// if (currentTabIndex == 0)
	// chatHistoryFragment.refresh();
	// if (CommonUtils.getTopActivity(MainActivity.this)
	// .equals(GroupsActivity.class.getName())) {
	// GroupsActivity.instance.onResume();
	// }
	// } catch (Exception e) {
	// EMLog.v(TAG, "refresh exception " + e.getMessage());
	// }
	// }
	// });
	// }
	//
	// @Override
	// public void onGroupDestroy(String groupId, String groupName) {
	// // 群被解散
	// // 提示用户群被解散,demo省略
	// // 刷新ui
	// runOnUiThread(new Runnable() {
	// public void run() {
	// updateUnreadLabel();
	// if (currentTabIndex == 0)
	// chatHistoryFragment.refresh();
	// if (CommonUtils.getTopActivity(MainActivity.this).equals(
	// GroupsActivity.class.getName())) {
	// GroupsActivity.instance.onResume();
	// }
	// }
	// });
	//
	// }
	//
	// @Override
	// public void onApplicationReceived(String groupId, String groupName,
	// String applyer, String reason) {
	// // 用户申请加入群聊
	// InviteMessage msg = new InviteMessage();
	// msg.setFrom(applyer);
	// msg.setTime(System.currentTimeMillis());
	// msg.setGroupId(groupId);
	// msg.setGroupName(groupName);
	// msg.setReason(reason);
	// Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
	// msg.setStatus(InviteMesageStatus.BEAPPLYED);
	// notifyNewIviteMessage(msg);
	// }
	//
	// @Override
	// public void onApplicationAccept(String groupId, String groupName,
	// String accepter) {
	// String st4 = getResources().getString(
	// R.string.Agreed_to_your_group_chat_application);
	// // 加群申请被同意
	// EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
	// msg.setChatType(ChatType.GroupChat);
	// msg.setFrom(accepter);
	// msg.setTo(groupId);
	// msg.setMsgId(UUID.randomUUID().toString());
	// msg.addBody(new TextMessageBody(accepter + st4));
	// // 保存同意消息
	// EMChatManager.getInstance().saveMessage(msg);
	// // 提醒新消息
	// EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
	//
	// runOnUiThread(new Runnable() {
	// public void run() {
	// updateUnreadLabel();
	// // 刷新ui
	// if (currentTabIndex == 0)
	// chatHistoryFragment.refresh();
	// if (CommonUtils.getTopActivity(MainActivity.this).equals(
	// GroupsActivity.class.getName())) {
	// GroupsActivity.instance.onResume();
	// }
	// }
	// });
	// }
	//
	// @Override
	// public void onApplicationDeclined(String groupId, String groupName,
	// String decliner, String reason) {
	// // 加群申请被拒绝，demo未实现
	// }
	//
	// }

	@Override
	protected void onResume() {
		super.onResume();
		if (!isConflict || !isCurrentAccountRemoved) {
			updateUnreadLabel();
			updateUnreadAddressLable();
			EMChatManager.getInstance().activityResumed();
		}

	}

	// @Override
	// protected void onSaveInstanceState(Bundle outState) {
	// outState.putBoolean("isConflict", isConflict);
	// outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
	// super.onSaveInstanceState(outState);
	// }

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// moveTaskToBack(false);
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		BaseApplication.getInstance().logout(null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(
							MainActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								conflictBuilder = null;
								finish();
								startActivity(new Intent(MainActivity.this,
										LoginActivity.class));
							}
						});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.v(TAG,
						"---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		BaseApplication.getInstance().logout(null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(
							MainActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								accountRemovedBuilder = null;
								finish();
								startActivity(new Intent(MainActivity.this,
										LoginActivity.class));
							}
						});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
				EMLog.v(TAG,
						"---------color userRemovedBuilder error"
								+ e.getMessage());
			}

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		if (getIntent().getBooleanExtra("conflict", false)
				&& !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false)
				&& !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	MyActivityManager mam = MyActivityManager.getInstance();

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.v("TAG", "on back1");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - keyDownTime < 4000) {

				Log.v("tag", "1=" + keyDownTime);
				moveTaskToBack(false);
				mam.finishAllActivity();
				finish();
				return true;
			}

			keyDownTime = System.currentTimeMillis();
			Log.v("tag", "2=" + keyDownTime);
			Toast.makeText(this, "在按一次退出系统", Toast.LENGTH_SHORT).show();
		}

		return true;
	}
}