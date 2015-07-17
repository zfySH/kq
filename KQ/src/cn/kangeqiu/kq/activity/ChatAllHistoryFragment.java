package cn.kangeqiu.kq.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.Constant;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.ChatnewsView;
import cn.kangeqiu.kq.activity.view.MessageHourseView;
import cn.kangeqiu.kq.adapter.ChatAllHistoryAdapter;
import cn.kangeqiu.kq.adapter.MyPagerAdapter;

import com.easemob.chat.EMConversation;
import com.nowagame.kq.activity.CreatMyHouseActivity;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

/**
 * 显示所有会话记录，比较简单的实现，更好的可能是把陌生人存入本地，这样取到的聊天记录是可控的
 * 
 */
public class ChatAllHistoryFragment extends Fragment {

	private InputMethodManager inputMethodManager;
	private ListView listView;
	private ChatAllHistoryAdapter adapter;
	private EditText query;
	private ImageButton clearSearch;
	public RelativeLayout errorItem, rl_comment, rl_help, rl_discussmy;
	public TextView errorText, sys_last_msg, sys_unread_msg_number,
			txt_discussmy, txt_assistmy, sys_last_msg1, sys_last_msg2;
	private boolean hidden;
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();
	private ImageButton imger_add;

	@SuppressWarnings("unused")
	private boolean progressShow;

	private JSONArray records = new JSONArray();

	private View view;

	private MessageHourseView houseView;
	private ChatnewsView messageView;
	public static ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片
	private TextView all, important, unread_hourse_number;
	// , unread_msg_number;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	Context context;
	public final static int REQUEST_CODE_CREATE_HOURSE = 21;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_conversation_history,
				container, false);
		initView();
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		listViews = new ArrayList<View>();
		houseView = new MessageHourseView(getActivity());
		messageView = new ChatnewsView(getActivity());
		listViews.add(houseView.getView());
		listViews.add(messageView.getView());
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	public void RefreshData() {
		houseView.doFirstShowNearby();
		// messageView
	}

	/*
	 * 初始化控件
	 */
	private void initView() {
		all = (TextView) view.findViewById(R.id.all);
		important = (TextView) view.findViewById(R.id.message_title);
		mPager = (ViewPager) view.findViewById(R.id.vPager);
		imger_add = (ImageButton) view.findViewById(R.id.imger_add);

		unread_hourse_number = (TextView) view
				.findViewById(R.id.unread_hourse_number);
//		unread_msg_number = (TextView) view
//				.findViewById(R.id.unread_msg_number);
		InitViewPager();

		all.setOnClickListener(new MyOnClickListener(0));
		important.setOnClickListener(new MyOnClickListener(1));

		imger_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MobclickAgent.onEvent(getActivity(), "room_creat");
				TCAgent.onEvent(getActivity(), "room_creat");

				startActivityForResult(new Intent(getActivity(),
						CreatMyHouseActivity.class), REQUEST_CODE_CREATE_HOURSE);
			}
		});
		// 按钮点击事件

	}

	public void refreshMessage(int hourseNum, int msgNum) {
		if (hourseNum > 0) {
			unread_hourse_number.setVisibility(View.VISIBLE);
			unread_hourse_number.setText(hourseNum + "");
		} else
			unread_hourse_number.setVisibility(View.GONE);

		// if (msgNum > 0) {
		// unread_msg_number.setVisibility(View.VISIBLE);
		// unread_msg_number.setText(msgNum + "");
		// } else
		// unread_msg_number.setVisibility(View.GONE);
	}

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		@SuppressLint("ResourceAsColor")
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					all.setBackgroundResource(R.drawable.abc_button_viewpager_white_left);
					all.setTextColor(getActivity().getResources().getColor(
							R.color.top_bar_normal_bg));
					important.setBackgroundResource(R.drawable.trans_bg);
					important.setTextColor(getActivity().getResources()
							.getColor(R.color.white));

				}
				imger_add.setVisibility(View.VISIBLE);
				break;
			case 1:
				if (currIndex == 0) {

					all.setBackgroundResource(R.color.transparent);
					all.setTextColor(getActivity().getResources().getColor(
							R.color.white));
					important
							.setBackgroundResource(R.drawable.abc_button_viewpager_white_right);
					important.setTextColor(getActivity().getResources()
							.getColor(R.color.top_bar_normal_bg));

				}
				imger_add.setVisibility(View.GONE);
				break;

			}
			currIndex = arg0;

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (((MainActivity) getActivity()).isConflict) {
			outState.putBoolean("isConflict", true);
		} else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
			outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
		}
	}

	// class ChatnewsView implements OnClickListener{
	// private Activity context;
	// private LayoutInflater inflater;
	// private boolean hidden;
	//
	// public ChatnewsView(Activity context) {
	// this.context = context;
	// inflater = context.getLayoutInflater();
	// }
	// public View getView() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	// /**
	// * 系统通知的点击事件.
	// */
	// public void doShowSystemMessage(View v, String type) {
	// Log.d("demoTAG", "doShowSystemMessage()");
	// Intent intent = new Intent(getActivity(), SysMsgActivity.class);
	// intent.putExtra("type", type);
	// startActivity(intent);
	// }
	//
	// public void onActivityCreated(Bundle savedInstanceState) {
	//
	// if (savedInstanceState != null
	// && savedInstanceState.getBoolean("isConflict", false))
	// return;
	// View v = inflater.inflate(R.layout.abc_fragment_news, null);
	// sys_last_msg = (TextView) v.findViewById(R.id.sys_last_msg);
	// sys_last_msg1 = (TextView) getView().findViewById(R.id.sys_last_msg1);
	// sys_last_msg2 = (TextView) getView().findViewById(R.id.sys_last_msg2);
	//
	// sys_unread_msg_number = (TextView)
	// getView().findViewById(R.id.sys_unread_msg_number);
	// txt_discussmy = (TextView) getView().findViewById(R.id.txt_discussmy);
	// txt_assistmy = (TextView) getView().findViewById(R.id.unread_assistmy);
	//
	// inputMethodManager = (InputMethodManager) getActivity()
	// .getSystemService(Context.INPUT_METHOD_SERVICE);
	// errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
	// rl_comment = (RelativeLayout) getView().findViewById(R.id.rel_comment);
	// rl_discussmy = (RelativeLayout) getView().findViewById(
	// R.id.rl_discussmy);
	// rl_help = (RelativeLayout) getView().findViewById(R.id.rl_help);
	// rl_comment.setOnClickListener(this);
	// rl_help.setOnClickListener(this);
	// rl_discussmy.setOnClickListener(this);
	// errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
	// conversationList.addAll(loadConversationsWithRecentChat());
	// listView = (ListView) getView().findViewById(R.id.list);
	// adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
	// // 设置adapter
	// listView.setAdapter(adapter);
	//
	// // 载入末条系统消息和未读消息数
	// doLoadCmdMessageOfLastBodyAndUnreadCount();
	//
	// final String st2 = getResources().getString(
	// R.string.Cant_chat_with_yourself);
	// listView.setOnItemClickListener(new OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view,
	// int position, long id) {
	// EMConversation conversation = adapter.getItem(position);
	// String username = conversation.getUserName();
	// if (username
	// .equals(BaseApplication.getInstance().getUserName()))
	// Toast.makeText(getActivity(), st2, Toast.LENGTH_SHORT)
	// .show();
	// else {
	// // 进入聊天页面
	// Intent intent = new Intent(getActivity(),
	// ChatActivity.class);
	// if (conversation.isGroup()) {
	// // it is group chat
	// intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
	// intent.putExtra("groupId", username);
	// } else {
	// // it is single chat
	// intent.putExtra("userId", username);
	// }
	// startActivity(intent);
	// }
	// }
	// });
	// // 注册上下文菜单
	// registerForContextMenu(listView);
	//
	// listView.setOnTouchListener(new OnTouchListener() {
	//
	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// // 隐藏软键盘
	// hideSoftKeyboard();
	// return false;
	// }
	//
	// });
	// // // 搜索框
	// // query = (EditText) getView().findViewById(R.id.query);
	// // String strSearch = getResources().getString(R.string.search);
	// // query.setHint(strSearch);
	// // // 搜索框中清除button
	// // clearSearch = (ImageButton)
	// // getView().findViewById(R.id.search_clear);
	// // query.addTextChangedListener(new TextWatcher() {
	// // public void onTextChanged(CharSequence s, int start, int before, int
	// // count) {
	// // adapter.getFilter().filter(s);
	// // if (s.length() > 0) {
	// // clearSearch.setVisibility(View.VISIBLE);
	// // } else {
	// // clearSearch.setVisibility(View.INVISIBLE);
	// // }
	// // }
	// //
	// // public void beforeTextChanged(CharSequence s, int start, int count,
	// // int after) {
	// // }
	// //
	// // public void afterTextChanged(Editable s) {
	// // }
	// // });
	// // clearSearch.setOnClickListener(new OnClickListener() {
	// // @Override
	// // public void onClick(View v) {
	// // query.getText().clear();
	// // hideSoftKeyboard();
	// // }
	// // });
	//
	// }
	//
	// void hideSoftKeyboard() {
	// if (getActivity().getWindow().getAttributes().softInputMode !=
	// WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
	// if (getActivity().getCurrentFocus() != null)
	// inputMethodManager.hideSoftInputFromWindow(getActivity()
	// .getCurrentFocus().getWindowToken(),
	// InputMethodManager.HIDE_NOT_ALWAYS);
	// }
	// }
	//
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// super.onCreateContextMenu(menu, v, menuInfo);
	// // if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
	// getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
	// // }
	// }
	//
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_message) {
			// EMConversation tobeDeleteCons = adapter
			// .getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			// // 删除此会话
			// EMChatManager.getInstance().deleteConversation(
			// tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup());
			// InviteMessgeDao inviteMessgeDao = new
			// InviteMessgeDao(getActivity());
			// inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
			// adapter.remove(tobeDeleteCons);
			// adapter.notifyDataSetChanged();
			//
			// // 更新消息未读数
			// ((MainActivity) getActivity()).updateUnreadLabel();
			return messageView.onContextItemSelected(item);
		}
		return getActivity().onContextItemSelected(item);
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		messageView.refresh();
		houseView.doFirstShowNearby();
		// conversationList.clear();
		// conversationList.addAll(loadConversationsWithRecentChat());
		// if (adapter != null)
		// adapter.notifyDataSetChanged();
		//
		// // 更新系统通知区域的显示.
		// // 载入末条系统消息和未读消息数
		// doLoadCmdMessageOfLastBodyAndUnreadCount();
	}

	//
	// /**
	// * 获取所有会话
	// *
	// * @param context
	// * @return +
	// */
	// private List<EMConversation> loadConversationsWithRecentChat() {
	// // 获取所有会话，包括陌生人
	// Hashtable<String, EMConversation> conversations = EMChatManager
	// .getInstance().getAllConversations();
	// List<EMConversation> list = new ArrayList<EMConversation>();
	// // 过滤掉messages seize为0的conversation
	// for (EMConversation conversation : conversations.values()) {
	// if (conversation.getAllMessages().size() != 0)
	// list.add(conversation);
	// }
	// // 排序
	// sortConversationByLastChatTime(list);
	// return list;
	// }
	//
	// /**
	// * 根据最后一条消息的时间排序
	// *
	// * @param usernames
	// */
	// private void sortConversationByLastChatTime(
	// List<EMConversation> conversationList) {
	// Collections.sort(conversationList, new Comparator<EMConversation>() {
	// @Override
	// public int compare(final EMConversation con1,
	// final EMConversation con2) {
	//
	// EMMessage con2LastMessage = con2.getLastMessage();
	// EMMessage con1LastMessage = con1.getLastMessage();
	// if (con2LastMessage.getMsgTime() == con1LastMessage
	// .getMsgTime()) {
	// return 0;
	// } else if (con2LastMessage.getMsgTime() > con1LastMessage
	// .getMsgTime()) {
	// return 1;
	// } else {
	// return -1;
	// }
	// }
	//
	// });
	// }
	//
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		messageView.onHiddenChanged(hidden);
		houseView.onHiddenChanged(hidden);
	}

	public void onResume() {
		super.onResume();
		messageView.onResume();
		houseView.onResume();
	}
	//
	//
	//
	// /**
	// * 更新系统消息的未读记录数.
	// *
	// * @param unreadCount
	// */
	// public void doUpdateSysUnredLabel(int unreadCount) {
	// Log.d("doUpdateSysUnredLabel", "doUpdateSysUnredLabel(" + unreadCount
	// + ")");
	// if (unreadCount > 0) {
	// sys_unread_msg_number.setVisibility(View.VISIBLE);
	// sys_unread_msg_number.setText(String.valueOf(unreadCount));
	// } else {
	// sys_unread_msg_number.setVisibility(View.INVISIBLE);
	// }
	// }
	//
	// /**
	// * 获取末条透传消息内容和全部未读的消息记录数.
	// */
	// public void doLoadCmdMessageOfLastBodyAndUnreadCount() {
	// // progressShow = true;
	// // final ProgressDialog pd = new ProgressDialog(getActivity());
	// // pd.setCanceledOnTouchOutside(false);
	// // pd.setOnCancelListener(new OnCancelListener() {
	// // @Override
	// // public void onCancel(DialogInterface dialog) {
	// // progressShow = false;
	// // }
	// // });
	// // pd.setMessage(getString(R.string.abc_data_loding));
	// // pd.show();
	// CPorgressDialog.showProgressDialog(getActivity());
	//
	// ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
	// pair.add(new BasicNameValuePair("app_action", "2025"));
	// pair.add(new BasicNameValuePair("app_platform", "0"));
	// pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
	// .getPlayerId() + ""));
	// new WebRequestUtil().execute(true,
	// AppConfig.getInstance().makeUrl(Integer.parseInt("2025")),
	// pair, new MCHttpCallBack() {
	// @Override
	// public void onSuccess(MCHttpResp resp) {
	// super.onSuccess(resp);
	// CPorgressDialog.hideProgressDialog();
	// try {
	// records = (JSONArray) resp.getJson().getJSONArray(
	// "records");
	// for (int i = 0; i < records.length(); i++) {
	// if (records.getJSONObject(i).getString("group")
	// .equals("0")) {// 系统
	// // 末条消息
	// String last_msg_body = records
	// .getJSONObject(i).getString(
	// "last_msg_body");
	// if (last_msg_body == null)
	// last_msg_body = "";
	// sys_last_msg.setText(last_msg_body);
	// // 未读消息记录数
	// String unread_count = records
	// .getJSONObject(i).getString(
	// "unread_count");
	// if (unread_count == null
	// || unread_count.length() == 0)
	// unread_count = "0";
	// int count = Integer.parseInt(unread_count);
	// if (count > 0) {
	// sys_unread_msg_number
	// .setVisibility(View.VISIBLE);
	// sys_unread_msg_number.setText(String
	// .valueOf(count));
	// } else {
	// sys_unread_msg_number
	// .setVisibility(View.INVISIBLE);
	// }
	// } else if (records.getJSONObject(i)
	// .getString("group").equals("1")) {// 点评
	// // 末条消息
	// String last_msg_body = records
	// .getJSONObject(i).getString(
	// "last_msg_body");
	// if (last_msg_body == null)
	// last_msg_body = "";
	// sys_last_msg1.setText(last_msg_body);
	// // 未读消息记录数
	// String unread_count = records
	// .getJSONObject(i).getString(
	// "unread_count");
	// if (unread_count == null
	// || unread_count.length() == 0)
	// unread_count = "0";
	// int count = Integer.parseInt(unread_count);
	// if (count > 0) {
	// txt_discussmy
	// .setVisibility(View.VISIBLE);
	// txt_discussmy.setText(String
	// .valueOf(count));
	// } else {
	// txt_discussmy
	// .setVisibility(View.INVISIBLE);
	// }
	// } else if (records.getJSONObject(i)
	// .getString("group").equals("2")) {// 赞我的
	// // 末条消息
	// String last_msg_body = records
	// .getJSONObject(i).getString(
	// "last_msg_body");
	// if (last_msg_body == null)
	// last_msg_body = "";
	// sys_last_msg2.setText(last_msg_body);
	// // 未读消息记录数
	// String unread_count = records
	// .getJSONObject(i).getString(
	// "unread_count");
	// if (unread_count == null
	// || unread_count.length() == 0)
	// unread_count = "0";
	// int count = Integer.parseInt(unread_count);
	// if (count > 0) {
	// txt_assistmy
	// .setVisibility(View.VISIBLE);
	// txt_assistmy.setText(String
	// .valueOf(count));
	// } else {
	// txt_assistmy.setVisibility(View.GONE);
	// }
	// }
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	//
	// @Override
	// public void onError(MCHttpResp resp) {
	// super.onError(resp);
	// Toast.makeText(getActivity(), resp.getErrorMessage(),
	// Toast.LENGTH_SHORT).show();
	// CPorgressDialog.hideProgressDialog();
	// }
	// });
	// // Map<String, String> parameters = new HashMap<String, String>();
	// // parameters.put("app_action", "2025");
	// // parameters.put("app_platform", "0");
	// // parameters.put("u_uid",
	// // String.valueOf(AppConfig.getInstance().getPlayerId()));
	// // HttpPostUtil mHttpPostUtil = null;
	// // try {
	// // AppConfig.getInstance().addSign(parameters);
	// // mHttpPostUtil = AppConfig.getInstance()
	// // .makeHttpPostUtil(parameters);
	// // } catch (Exception e) {
	// // e.printStackTrace();
	// // }
	// // // WEB request and deal the listener
	// // new WebRequestUtil(mHttpPostUtil).execute(new
	// // WebRequestUtilListener() {
	// //
	// // @Override
	// // public void onSucces(Map<String, Object> data) {
	// //
	// // Log.d("doUpdateSysUnredLabel",
	// // "public void onSucces(Map<String, Object> data):data="
	// // + data);
	// // records = (List<Map<String, String>>) data.get("records");
	// // for (int i = 0; i < records.size(); i++) {
	// // if (records.get(i).get("group").equals("0")) {// 系统
	// // // 末条消息
	// // String last_msg_body = (String) records.get(i).get(
	// // "last_msg_body");
	// // if (last_msg_body == null)
	// // last_msg_body = "";
	// // sys_last_msg.setText(last_msg_body);
	// // // 未读消息记录数
	// // String unread_count = (String) records.get(i).get(
	// // "unread_count");
	// // if (unread_count == null || unread_count.length() == 0)
	// // unread_count = "0";
	// // int count = Integer.parseInt(unread_count);
	// // if (count > 0) {
	// // sys_unread_msg_number.setVisibility(View.VISIBLE);
	// // sys_unread_msg_number.setText(String.valueOf(count));
	// // } else {
	// // sys_unread_msg_number.setVisibility(View.INVISIBLE);
	// // }
	// // } else if (records.get(i).get("group").equals("1")) {// 点评
	// // // 末条消息
	// // String last_msg_body = (String) records.get(i).get(
	// // "last_msg_body");
	// // if (last_msg_body == null)
	// // last_msg_body = "";
	// // sys_last_msg1.setText(last_msg_body);
	// // // 未读消息记录数
	// // String unread_count = (String) records.get(i).get(
	// // "unread_count");
	// // if (unread_count == null || unread_count.length() == 0)
	// // unread_count = "0";
	// // int count = Integer.parseInt(unread_count);
	// // if (count > 0) {
	// // txt_discussmy.setVisibility(View.VISIBLE);
	// // txt_discussmy.setText(String.valueOf(count));
	// // } else {
	// // txt_discussmy.setVisibility(View.INVISIBLE);
	// // }
	// // } else if (records.get(i).get("group").equals("2")) {// 赞我的
	// // // 末条消息
	// // String last_msg_body = (String) records.get(i).get(
	// // "last_msg_body");
	// // if (last_msg_body == null)
	// // last_msg_body = "";
	// // sys_last_msg2.setText(last_msg_body);
	// // // 未读消息记录数
	// // String unread_count = (String) records.get(i).get(
	// // "unread_count");
	// // if (unread_count == null || unread_count.length() == 0)
	// // unread_count = "0";
	// // int count = Integer.parseInt(unread_count);
	// // if (count > 0) {
	// // txt_assistmy.setVisibility(View.VISIBLE);
	// // txt_assistmy.setText(String.valueOf(count));
	// // } else {
	// // txt_assistmy.setVisibility(View.GONE);
	// // }
	// // }
	// // }
	// // pd.dismiss();
	// // }
	// //
	// // @Override
	// // public void onFail(Map<String, Object> data) {
	// // pd.dismiss();
	// // Toast.makeText(getActivity(), "操作失败:" + data.get("message"),
	// // Toast.LENGTH_SHORT).show();
	// // }
	// //
	// // @Override
	// // public void onError() {
	// // pd.dismiss();
	// // Toast.makeText(getActivity(), "操作失败.", Toast.LENGTH_SHORT)
	// // .show();
	// // }
	// // });
	//
	// }
	// @Override
	// public void onClick(View v) {
	// int id = v.getId();
	// switch (id) {
	// case R.id.rel_comment:
	// doShowSystemMessage(v, "0");
	// break;
	// case R.id.rl_discussmy:
	// doShowSystemMessage(v, "1");
	// break;
	// case R.id.rl_help:
	// doShowSystemMessage(v, "2");
	// break;
	// }
	//
	// }
	// }

}
