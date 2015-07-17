package cn.kangeqiu.kq.activity.view;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.ChatActivity;
import cn.kangeqiu.kq.activity.MainActivity;
import cn.kangeqiu.kq.adapter.AdapterMatchHourse;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.DateUtil;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;

public class MessageHourseView {
	private Activity context;
	private LayoutInflater inflater;
	public static XListView lv_match;
	private AdapterMatchHourse adapter;
	// private JSONArray matchs = new JSONArray();
	private int page;
	private boolean hidden;
	private int sum = 0;

	public MessageHourseView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView() {
		View view = inflater.inflate(R.layout.abc_match_all_item, null);
		adapter = new AdapterMatchHourse(context);
		lv_match = (XListView) view.findViewById(R.id.match_list);
		lv_match.setAdapter(adapter);
		lv_match.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				try {

					if (EMGroupManager.getInstance().getGroup(
							new_list.get(position - 1).getString("huanxin_id")) == null) {
						Toast.makeText(context, "您还不是该房间的成员，请联系房间管理员",
								Toast.LENGTH_SHORT).show();
					} else {
						// 进入聊天页面
						Intent intent = new Intent(context, ChatActivity.class);
						// it is group chat
						intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
						intent.putExtra("groupId", new_list.get(position - 1)
								.getString("huanxin_id"));
						intent.putExtra("roomId", new_list.get(position - 1)
								.getString("id"));
						context.startActivity(intent);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		lv_match.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				page = 1;
				doShowNearby(true, false);
			}

			@Override
			public void onLoadMore() {
				page++;
				doShowNearby(false, false);
			}
		});
		lv_match.setPullLoadEnable(true);
		lv_match.setPullRefreshEnable(true);
		doFirstShowNearby();
		return view;
	}

	// //**
	// * 第1次打开附近显示数据.
	// *//*
	public void doFirstShowNearby() {
		page = 1;
		doShowNearby(true, true);
	}

	private List<JSONObject> new_list = new ArrayList<JSONObject>();

	public void doShowNearby(final boolean isRefresh, boolean isLazyLoad) {
		doPullDate(isLazyLoad, "2037", new MCHttpCallBack() {

			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);

				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						JSONArray an_matchs = (JSONArray) resp.getJson()
								.getJSONArray("records");
						JSONArray new_matchs = new JSONArray();
						new_list.clear();
						if (isRefresh) {
							sum = 0;
							for (int i = 0; i < an_matchs.length(); i++) {
								EMConversation conversation = EMChatManager
										.getInstance()
										.getConversation(
												an_matchs
														.getJSONObject(i)
														.getString("huanxin_id"));
								sum += conversation.getUnreadMsgCount();
								if (conversation.getUnreadMsgCount() <= 0) {
									// an_matchs.remove(i + 1);
									// new_matchs.put(an_matchs.getJSONObject(i));
									new_list.add(an_matchs.getJSONObject(i));
								} else {
									// new_matchs.put(0,
									// an_matchs.getJSONObject(i));
									new_list.add(0, an_matchs.getJSONObject(i));
								}
							}

							// matchs = new_matchs;
							adapter.setItem(new_list);
						} else {
							for (int i = 0; i < an_matchs.length(); i++) {
								// matchs.put(an_matchs.get(i));
								new_list.add(an_matchs.getJSONObject(i));
								EMConversation conversation = EMChatManager
										.getInstance()
										.getConversation(
												an_matchs
														.getJSONObject(i)
														.getString("huanxin_id"));
								sum += conversation.getUnreadMsgCount();
							}

							int itemsCount = (an_matchs == null) ? 0
									: an_matchs.length();
							// 刷新显示
							if (itemsCount > 0) {
								adapter.setItem(new_list);
							} else {
								Toast.makeText(context, "没有更多数据了.",
										Toast.LENGTH_SHORT).show();
								if (page > 1)
									page--;
							}

						}

						((MainActivity) context).refreshMessage(sum);
					} else {
						Toast.makeText(context,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 数据载入关闭提示;
				onFinishLoad();
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				Toast.makeText(context, resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
				// 数据载入关闭提示;
				onFinishLoad();
			}
		});

	}

	private void doPullDate(boolean isLazyLoad, String action,
			MCHttpCallBack listen) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_page", page + ""));
		pair.add(new BasicNameValuePair("app_version", UpdataUtil
				.getAppVersion(context)));

		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

	public void onHiddenChanged(boolean hidden) {
		// super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			doFirstShowNearby();
		}
	}

	public void onResume() {
		// super.onResume();
		if (!hidden && !((MainActivity) context).isConflict) {
			doFirstShowNearby();
		}
	}

	private void onFinishLoad() {
		lv_match.stopRefresh();
		lv_match.stopLoadMore();
		lv_match.setRefreshTime(DateUtil.date2String());
	}
}
