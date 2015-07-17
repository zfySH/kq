package com.nowagame.kq.activity;

import java.util.ArrayList;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.adapter.AdapterMember;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.DateUtil;
import com.nowagme.util.WebRequestUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

public class HourseMemberChooseActivity extends AgentActivity {
	private int page;
	private JSONArray matchs = new JSONArray();
	private AdapterMember adapter;
	private XListView memberList;
	private String roomId = "";
	private String memberId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_myhouse);
		if (getIntent().getStringExtra("roomId") != null)
			roomId = getIntent().getStringExtra("roomId");
		if (getIntent().getStringExtra("memberId") != null)
			memberId = getIntent().getStringExtra("memberId");

		initView();
		initData();
	}

	public void back(View view) {
		finish();
	}

	private void initView() {
		((TextView) findViewById(R.id.title)).setText("好友");
		((Button) findViewById(R.id.add_Btn))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						try {
							String icons = "";
							String ids = "";
							for (int i = 0; i < matchs.length(); i++) {

								if (matchs.getJSONObject(i).getBoolean(
										"isCheck")) {
									icons += ","
											+ matchs.getJSONObject(i)
													.getString("icon");
									ids += ","
											+ matchs.getJSONObject(i)
													.getString("id");
								}

							}

							if (!icons.equals("")) {
								MobclickAgent.onEvent(
										HourseMemberChooseActivity.this,
										"room_yaoqing");
								TCAgent.onEvent(
										HourseMemberChooseActivity.this,
										"room_yaoqing");
								if (roomId == null || roomId.equals("")) {
									Intent mIntent = new Intent();
									mIntent.putExtra("icon",
											icons.substring(1, icons.length()));
									mIntent.putExtra("id",
											ids.substring(1, ids.length()));
									// 设置结果，并进行传送
									HourseMemberChooseActivity.this.setResult(
											20, mIntent);
									finish();
								} else {
									Intent mIntent = new Intent();
									mIntent.putExtra("icon",
											icons.substring(1, icons.length()));
									mIntent.putExtra("id",
											ids.substring(1, ids.length()));

									setResult(RESULT_OK, mIntent);
									finish();
								}
							} else {
								Toast.makeText(HourseMemberChooseActivity.this,
										"请选择成员", Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		memberList = (XListView) findViewById(R.id.hourse_list);

		adapter = new AdapterMember(HourseMemberChooseActivity.this);
		memberList.setAdapter(adapter);

		memberList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				try {
					if (an_matchs.getJSONObject(position - 1).getBoolean(
							"isMember"))
						Toast.makeText(getApplicationContext(),
								"好友已是房间会员，请不要重复添加", Toast.LENGTH_SHORT).show();
					else
						adapter.choose(position - 1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		memberList.setXListViewListener(new IXListViewListener() {
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
		memberList.setPullLoadEnable(true);
		memberList.setPullRefreshEnable(true);
	}

	public void initData() {
		page = 1;
		doShowNearby(true, true);
	}

	private JSONArray an_matchs = new JSONArray();

	public void doShowNearby(final boolean isRefresh, boolean isLazyLoad) {
		doPullDate(isLazyLoad, "2020", new MCHttpCallBack() {

			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);

				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						an_matchs = (JSONArray) resp.getJson().getJSONArray(
								"records");
						w: for (int i = 0; i < an_matchs.length(); i++) {
							for (int j = 0; j < memberId.split(",").length; j++) {
								if (an_matchs.getJSONObject(i).getString("id")
										.equals(memberId.split(",")[j])) {
									an_matchs.getJSONObject(i).put("isMember",
											true);
									an_matchs.getJSONObject(i).put("isCheck",
											false);
									continue w;
								}
							}
							an_matchs.getJSONObject(i).put("isMember", false);
							an_matchs.getJSONObject(i).put("isCheck", false);
						}
						if (isRefresh) {
							matchs = an_matchs;
							adapter.setItem(matchs);
						} else {
							for (int i = 0; i < an_matchs.length(); i++) {
								matchs.put(an_matchs.get(i));
								EMConversation conversation = EMChatManager
										.getInstance()
										.getConversation(
												an_matchs
														.getJSONObject(i)
														.getString("huanxin_id"));
							}

							int itemsCount = (an_matchs == null) ? 0
									: an_matchs.length();
							// 刷新显示
							if (itemsCount > 0) {
								adapter.setItem(matchs);
							} else {
								Toast.makeText(HourseMemberChooseActivity.this,
										"没有更多数据了.", Toast.LENGTH_SHORT).show();
								if (page > 1)
									page--;
							}

						}

					} else {
						Toast.makeText(HourseMemberChooseActivity.this,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				// 数据载入关闭提示;
				onFinishLoad();
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				Toast.makeText(HourseMemberChooseActivity.this,
						resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
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
		pair.add(new BasicNameValuePair("u_search_text", ""));
		pair.add(new BasicNameValuePair("u_user_id", AppConfig.getInstance()
				.getPlayerId() + ""));
		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

	private void onFinishLoad() {
		memberList.stopRefresh();
		memberList.stopLoadMore();
		memberList.setRefreshTime(DateUtil.date2String());
	}
}
