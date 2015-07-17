package cn.kangeqiu.kq.activity.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.ChatActivity;
import cn.kangeqiu.kq.activity.MainActivity;
import cn.kangeqiu.kq.adapter.ChatAllHistoryAdapter;
import cn.kangeqiu.kq.db.InviteMessgeDao;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.jingyi.MiChat.application.BaseApplication;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.football.SysMsgActivity;
import com.nowagme.util.WebRequestUtil;

public class ChatnewsView implements OnClickListener {
	private Activity context;
	private LayoutInflater inflater;
	private InputMethodManager inputMethodManager;
	private ListView listView;
	private ChatAllHistoryAdapter adapter;
	private EditText query;
	private ImageButton clearSearch;
	public RelativeLayout errorItem, rl_comment, rl_help, rl_discussmy;
	public TextView errorText, sys_last_msg, sys_unread_msg_number,
			txt_discussmy, txt_assistmy, sys_last_msg1, sys_last_msg2;

	private List<EMConversation> conversationList = new ArrayList<EMConversation>();

	@SuppressWarnings("unused")
	private boolean progressShow;
	private boolean hidden;
	private JSONArray records = new JSONArray();

	public ChatnewsView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView() {
		View v = inflater.inflate(R.layout.abc_fragment_news, null);
		inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		sys_last_msg = (TextView) v.findViewById(R.id.sys_last_msg);
		sys_last_msg1 = (TextView) v.findViewById(R.id.sys_last_msg1);
		sys_last_msg2 = (TextView) v.findViewById(R.id.sys_last_msg2);

		sys_unread_msg_number = (TextView) v
				.findViewById(R.id.sys_unread_msg_number);
		txt_discussmy = (TextView) v.findViewById(R.id.txt_discussmy);
		txt_assistmy = (TextView) v.findViewById(R.id.unread_assistmy);
		errorItem = (RelativeLayout) v.findViewById(R.id.rl_error_item);
		rl_comment = (RelativeLayout) v.findViewById(R.id.rel_comment);
		rl_discussmy = (RelativeLayout) v.findViewById(R.id.rl_discussmy);
		rl_help = (RelativeLayout) v.findViewById(R.id.rl_help);
		rl_comment.setOnClickListener(this);
		rl_help.setOnClickListener(this);
		rl_discussmy.setOnClickListener(this);
		errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
		conversationList.addAll(loadConversationsWithRecentChat());
		listView = (ListView) v.findViewById(R.id.list);
		adapter = new ChatAllHistoryAdapter(context, 1, conversationList);
		// 设置adapter
		listView.setAdapter(adapter);

		// 载入末条系统消息和未读消息数
		doLoadCmdMessageOfLastBodyAndUnreadCount();

		final String st2 = context.getString(R.string.Cant_chat_with_yourself);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EMConversation conversation = adapter.getItem(position);
				String username = conversation.getUserName();
				if (username
						.equals(BaseApplication.getInstance().getUserName()))
					Toast.makeText(context, st2, Toast.LENGTH_SHORT).show();
				else {
					// 进入聊天页面
					Intent intent = new Intent(context, ChatActivity.class);
					if (conversation.isGroup()) {
						// it is group chat
						intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
						intent.putExtra("groupId", username);
					} else {
						// it is single chat
						intent.putExtra("userId", username);
					}
					context.startActivity(intent);
				}
			}
		});
		// 注册上下文菜单
		context.registerForContextMenu(listView);

		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 隐藏软键盘
				hideSoftKeyboard();
				return false;
			}

		});

		listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				context.getMenuInflater().inflate(R.menu.delete_message, menu);
			}
		});
		// listView.on
		return v;
	}

	/**
	 * 系统通知的点击事件.
	 */
	public void doShowSystemMessage(View v, String type) {
		Log.d("demoTAG", "doShowSystemMessage()");
		Intent intent = new Intent(context, SysMsgActivity.class);
		intent.putExtra("type", type);
		context.startActivity(intent);
	}

	/**
	 * 更新系统消息的未读记录数.
	 * 
	 * @param unreadCount
	 */
	public void doUpdateSysUnredLabel(int unreadCount) {
		Log.d("doUpdateSysUnredLabel", "doUpdateSysUnredLabel(" + unreadCount
				+ ")");
		if (unreadCount > 0) {
			sys_unread_msg_number.setVisibility(View.VISIBLE);
			sys_unread_msg_number.setText(String.valueOf(unreadCount));
		} else {
			sys_unread_msg_number.setVisibility(View.INVISIBLE);
		}
	}

	void hideSoftKeyboard() {
		if (context.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (context.getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(context
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// super.onCreateContextMenu(menu, v, menuInfo);
		// if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
		context.getMenuInflater().inflate(R.menu.delete_message, menu);
		// }
	}

	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_message) {
			EMConversation tobeDeleteCons = adapter
					.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			// 删除此会话
			EMChatManager.getInstance().deleteConversation(
					tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup());
			InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(context);
			inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
			adapter.remove(tobeDeleteCons);
			adapter.notifyDataSetChanged();

			// 更新消息未读数
			((MainActivity) context).updateUnreadLabel();

			return true;
		}
		return context.onContextItemSelected(item);
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		conversationList.clear();
		conversationList.addAll(loadConversationsWithRecentChat());
		if (adapter != null)
			adapter.notifyDataSetChanged();

		// 更新系统通知区域的显示.
		// 载入末条系统消息和未读消息数
		doLoadCmdMessageOfLastBodyAndUnreadCount();
	}

	public void onHiddenChanged(boolean hidden) {
		// super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	public void onResume() {
		// super.onResume();
		if (!hidden && !((MainActivity) context).isConflict) {
			refresh();
		}
	}

	/**
	 * 获取末条透传消息内容和全部未读的消息记录数.
	 */
	public void doLoadCmdMessageOfLastBodyAndUnreadCount() {
		// progressShow = true;
		// final ProgressDialog pd = new ProgressDialog(getActivity());
		// pd.setCanceledOnTouchOutside(false);
		// pd.setOnCancelListener(new OnCancelListener() {
		// @Override
		// public void onCancel(DialogInterface dialog) {
		// progressShow = false;
		// }
		// });
		// pd.setMessage(getString(R.string.abc_data_loding));
		// pd.show();
		// CPorgressDialog.showProgressDialog(context);

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
						// CPorgressDialog.hideProgressDialog();
						try {
							records = (JSONArray) resp.getJson().getJSONArray(
									"records");
							for (int i = 0; i < records.length(); i++) {
								if (records.getJSONObject(i).getString("group")
										.equals("0")) {// 系统
									// 末条消息
									String last_msg_body = records
											.getJSONObject(i).getString(
													"last_msg_body");
									if (last_msg_body == null)
										last_msg_body = "";
									sys_last_msg.setText(last_msg_body);
									// 未读消息记录数
									String unread_count = records
											.getJSONObject(i).getString(
													"unread_count");
									if (unread_count == null
											|| unread_count.length() == 0)
										unread_count = "0";
									int count = Integer.parseInt(unread_count);
									if (count > 0) {
										sys_unread_msg_number
												.setVisibility(View.VISIBLE);
										sys_unread_msg_number.setText(String
												.valueOf(count));
									} else {
										sys_unread_msg_number
												.setVisibility(View.INVISIBLE);
									}
								} else if (records.getJSONObject(i)
										.getString("group").equals("1")) {// 点评
									// 末条消息
									String last_msg_body = records
											.getJSONObject(i).getString(
													"last_msg_body");
									if (last_msg_body == null)
										last_msg_body = "";
									sys_last_msg1.setText(last_msg_body);
									// 未读消息记录数
									String unread_count = records
											.getJSONObject(i).getString(
													"unread_count");
									if (unread_count == null
											|| unread_count.length() == 0)
										unread_count = "0";
									int count = Integer.parseInt(unread_count);
									if (count > 0) {
										txt_discussmy
												.setVisibility(View.VISIBLE);
										txt_discussmy.setText(String
												.valueOf(count));
									} else {
										txt_discussmy
												.setVisibility(View.INVISIBLE);
									}
								} else if (records.getJSONObject(i)
										.getString("group").equals("2")) {// 赞我的
									// 末条消息
									String last_msg_body = records
											.getJSONObject(i).getString(
													"last_msg_body");
									if (last_msg_body == null)
										last_msg_body = "";
									sys_last_msg2.setText(last_msg_body);
									// 未读消息记录数
									String unread_count = records
											.getJSONObject(i).getString(
													"unread_count");
									if (unread_count == null
											|| unread_count.length() == 0)
										unread_count = "0";
									int count = Integer.parseInt(unread_count);
									if (count > 0) {
										txt_assistmy
												.setVisibility(View.VISIBLE);
										txt_assistmy.setText(String
												.valueOf(count));
									} else {
										txt_assistmy.setVisibility(View.GONE);
									}
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					@Override
					public void onError(MCHttpResp resp) {
						super.onError(resp);
						Toast.makeText(context, resp.getErrorMessage(),
								Toast.LENGTH_SHORT).show();
						// CPorgressDialog.hideProgressDialog();
					}
				});
	}

	/**
	 * 获取所有会话
	 * 
	 * @param context
	 * @return +
	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		List<EMConversation> list = new ArrayList<EMConversation>();
		// 过滤掉messages seize为0的conversation
		for (EMConversation conversation : conversations.values()) {
			if (conversation.getAllMessages().size() != 0
					&& !conversation.isGroup())
				list.add(conversation);
		}
		// 排序
		sortConversationByLastChatTime(list);
		return list;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(
			List<EMConversation> conversationList) {
		Collections.sort(conversationList, new Comparator<EMConversation>() {
			@Override
			public int compare(final EMConversation con1,
					final EMConversation con2) {

				EMMessage con2LastMessage = con2.getLastMessage();
				EMMessage con1LastMessage = con1.getLastMessage();
				if (con2LastMessage.getMsgTime() == con1LastMessage
						.getMsgTime()) {
					return 0;
				} else if (con2LastMessage.getMsgTime() > con1LastMessage
						.getMsgTime()) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.rel_comment:
			doShowSystemMessage(v, "0");
			break;
		case R.id.rl_discussmy:
			doShowSystemMessage(v, "1");
			break;
		case R.id.rl_help:
			doShowSystemMessage(v, "2");
			break;
		}

	}

}
