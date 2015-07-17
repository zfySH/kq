package com.nowagme.football;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.VoteFirstView;
import cn.kangeqiu.kq.activity.view.VoteSecondView;
import cn.kangeqiu.kq.adapter.AdapterVote;
import cn.kangeqiu.kq.adapter.AdapterVote1;
import cn.kangeqiu.kq.adapter.Adaptercomment;
import cn.kangeqiu.kq.refresh.PullToRefreshView;
import cn.kangeqiu.kq.refresh.PullToRefreshView.OnFooterRefreshListener;
import cn.kangeqiu.kq.refresh.PullToRefreshView.OnHeaderRefreshListener;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

public class VoteActivity extends AgentActivity implements OnClickListener,
		OnHeaderRefreshListener, OnFooterRefreshListener {
	private String id, answer_count, state, join_state, options_id, answer_id,
			join_count, activity_id, comment_id;
	private TextView vote_title, vote_content, txt_logoff1, player3, txt_num;
	private RelativeLayout main_lay;
	private Button btn_vote, abc_team__btn_back, btn_save;
	private ListView list;
	private TextView[] name;
	// private ImageView img_check, img_check1, img_check2;
	private Adaptercomment adapter;
	private AdapterVote adapter1;
	private AdapterVote1 adapter2;
	private boolean flag = false;
	private JSONObject activity = new JSONObject();
	private JSONArray records = new JSONArray();
	private JSONArray options = new JSONArray();
	private JSONObject myoptions = new JSONObject();
	private static EditText edit;
	public int u_type, count;

	private LinearLayout ll_content;
	private List<Boolean> isCheck = new ArrayList<Boolean>();
	private PullToRefreshView mPullToRefreshView;
	private int page = 1;

	// private int answer_id;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vote_item);
		u_type = 1;
		id = getIntent().getStringExtra("id");
		initDate();
		initview();
		adapter = new Adaptercomment(this);
		adapter2 = new AdapterVote1(this);
		list.setAdapter(adapter);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(main_lay.getWindowToken(), 0);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	public void setCommentId(String commentId) {
		this.comment_id = commentId;
	}

	public void u_type(String nickName) {
		u_type = 2;
		if (adapter.getNickname() != null) {
			edit.setHint("回复" + nickName + ":");

		} else {
			edit.setHint("回复:");
		}
		edit.setHintTextColor(Color.parseColor("#B4B4B4"));
	}

	private void initview() {

		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setLastUpdated(new Date().toLocaleString());
		vote_title = (TextView) findViewById(R.id.vote_title);
		vote_content = (TextView) findViewById(R.id.vote_content);
		txt_logoff1 = (TextView) findViewById(R.id.txt_logoff1);
		txt_num = (TextView) findViewById(R.id.txt_num);
		main_lay = (RelativeLayout) findViewById(R.id.main_lay);
		btn_save = (Button) findViewById(R.id.btn_save);
		edit = (EditText) findViewById(R.id.edit);
		player3 = (TextView) findViewById(R.id.content);
		// img_check = (ImageView) findViewById(R.id.img_check);
		btn_vote = (Button) findViewById(R.id.btn_vote);
		abc_team__btn_back = (Button) findViewById(R.id.abc_team__btn_back);
		list = (ListView) findViewById(R.id.list);
		// list1 = (ListView) findViewById(R.id.list1);
		// list2 = (ListView) findViewById(R.id.list2);

		ll_content = (LinearLayout) findViewById(R.id.ll_content);
		// list1.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// if (answer_count.equals("1")) {
		// adapter1.setboolean(position);
		//
		// } else {
		// adapter1.setboolean1(position,
		// Integer.parseInt(answer_count));
		// ;
		// }
		//
		// }
		// });
		btn_vote.setOnClickListener(this);
		abc_team__btn_back.setOnClickListener(this);
		btn_save.setOnClickListener(this);

	}

	/**
	 * 更新数据
	 */
	public void refreshLoad(final boolean isRefresh, final int pageNo) {

		// 调取个人信息
		doPullDate("2013", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				CPorgressDialog.hideProgressDialog();
				onFinishLoad(isRefresh);
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						if (isRefresh) {
							records = (JSONArray) resp.getJson().getJSONArray(
									"records");
							for (int i = 0; i < records.length(); i++) {
								adapter.setItem(records);
								Utility.setListViewHeightBasedOnChildren(list);
							}
						} else {
							JSONArray moreRecords = resp.getJson()
									.getJSONArray("records");
							if (moreRecords.length() < 1) {
								page--;
								Toast.makeText(VoteActivity.this, "没有更多数据了",
										Toast.LENGTH_SHORT).show();
							} else {
								for (int i = 0; i < moreRecords.length(); i++) {
									records.put(moreRecords.getJSONObject(i));
								}
								adapter.setItem(records);
								Utility.setListViewHeightBasedOnChildren(list);
							}
						}
					} else {
						Toast.makeText(VoteActivity.this,
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
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(VoteActivity.this, resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void changeState(int position) {
		if (answer_count.equals("1")) {
			// adapter1.setboolean(position);
			isCheck.clear();
			for (int i = 0; i < options.length(); i++) {
				isCheck.add(false);
			}
			isCheck.set(position, true);
		} else {
			int num = 0;
			if (isCheck.get(position)) {
				isCheck.set(position, false);
			} else {
				for (int i = 0; i < isCheck.size(); i++) {
					if (isCheck.get(i)) {
						num++;
					}
				}
				if (num < Integer.parseInt(answer_count)) {
					isCheck.set(position, true);
				}
			}
		}
		ll_content.removeAllViews();
		try {
			for (int i = 0; i < options.length(); i++) {
				VoteFirstView voteView = new VoteFirstView(VoteActivity.this);
				ll_content.addView(voteView.getView(options.getJSONObject(i),
						i, isCheck.get(i)));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class Utility {
		public static void setListViewHeightBasedOnChildren(ListView listView) {
			ListAdapter listAdapter = listView.getAdapter();
			if (listAdapter == null) {

				return;
			}

			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight
					+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			listView.setLayoutParams(params);
		}
	}

	private void initDate() {

		doPullDate("2013", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				int sum = 0;
				CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						activity = (JSONObject) resp.getJson().getJSONObject(
								"activity");
						records = (JSONArray) resp.getJson().getJSONArray(
								"records");
						if (activity != null) {
							activity_id = activity.getString("id");
							adapter.setItem(records);
							Utility.setListViewHeightBasedOnChildren(list);
							state = activity.getString("state");
							vote_title.setText(activity.getString("title"));
							vote_content.setText(activity.getString("body"));
							txt_num.setText(activity.getString("comment_count"));
							player3.setText("已有"
									+ activity.getString("join_count") + "人参与");
							join_count = activity.getString("join_count");
							options = (JSONArray) activity.get("options");
							for (int i = 0; i < options.length(); i++) {
								count = Integer.parseInt(options.getJSONObject(
										i).getString("count"));
								sum += count;
							}
							answer_count = activity.get("answer_count")
									.toString();
							join_state = activity.get("join_state").toString();
							if (state.equals("1")) {
								ll_content.removeAllViews();
								btn_vote.setVisibility(View.VISIBLE);
								ll_content.setVisibility(View.VISIBLE);

								if (join_state.equals("0")) {
									for (int i = 0; i < options.length(); i++) {
										isCheck.add(false);
										VoteFirstView voteView = new VoteFirstView(
												VoteActivity.this);
										ll_content.addView(voteView.getView(
												options.getJSONObject(i), i,
												false));
									}
									txt_logoff1.setText("最多可选"
											+ activity.get("answer_count")
													.toString() + "项");
								} else if (join_state.equals("1")) {
									myoptions = (JSONObject) activity
											.get("myoptions");
									btn_vote.setVisibility(View.GONE);
									for (int i = 0; i < options.length(); i++) {
										VoteSecondView voteView = new VoteSecondView(
												VoteActivity.this, sum);
										ll_content.addView(voteView
												.getView(options
														.getJSONObject(i)));
									}

									answer_id = myoptions.get("answer")
											.toString();
									String str = "";

									for (int i = 0; i < options.length(); i++) {
										for (int j = 0; j < answer_id
												.split(",").length; j++) {

											if (options
													.getJSONObject(i)
													.getString("id")
													.equals(answer_id
															.split(",")[j])) {
												str += ","
														+ options
																.getJSONObject(
																		i)
																.getString(
																		"name");

											}
										}

									}
									txt_logoff1.setText("我投给了："
											+ str.substring(1));
								}

							} else if (state.equals("2")) {
								ll_content.removeAllViews();
								btn_vote.setVisibility(View.VISIBLE);
								ll_content.setVisibility(View.VISIBLE);
								btn_vote.setText("已结束");
								btn_vote.setFocusable(false);
								btn_vote.setClickable(false);
								btn_vote.setBackgroundResource(R.drawable.abc_button_roundcorner_lost);

								if (join_state.equals("0")) {
									for (int i = 0; i < options.length(); i++) {
										Log.v("demoTAG", "sum" + sum);
										VoteSecondView voteView = new VoteSecondView(
												VoteActivity.this, sum);
										ll_content.addView(voteView
												.getView(options
														.getJSONObject(i)));

									}
									txt_logoff1.setText("我没猜");
								} else if (join_state.equals("1")) {
									myoptions = (JSONObject) activity
											.get("myoptions");
									for (int i = 0; i < options.length(); i++) {
										VoteSecondView voteView = new VoteSecondView(
												VoteActivity.this, sum);
										ll_content.addView(voteView
												.getView(options
														.getJSONObject(i)));
										Log.v("demoTAG", "sum" + sum);
									}

									answer_id = myoptions.get("answer")
											.toString();
									String str = "";

									for (int i = 0; i < options.length(); i++) {
										for (int j = 0; j < answer_id
												.split(",").length; j++) {

											if (options
													.getJSONObject(i)
													.getString("id")
													.equals(answer_id
															.split(",")[j])) {
												str += ","
														+ options
																.getJSONObject(
																		i)
																.getString(
																		"name");

											}
										}

									}
									txt_logoff1.setText("我投给了："
											+ str.substring(1));
								}

							} else {

								ll_content.removeAllViews();
								btn_vote.setVisibility(View.VISIBLE);
								ll_content.setVisibility(View.GONE);
								btn_vote.setText("活动尚未开始");
								btn_vote.setFocusable(false);
								btn_vote.setClickable(false);
								btn_vote.setBackgroundResource(R.drawable.abc_button_roundcorner_lost);

							}

						}
					} else {
						Toast.makeText(VoteActivity.this,
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
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(VoteActivity.this, resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
			}
		}

		);
	}

	private void doPullDate(String action, MCHttpCallBack listen) {
		CPorgressDialog.showProgressDialog(this);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_page", page + ""));
		pair.add(new BasicNameValuePair("u_activity_id", id + ""));

		new WebRequestUtil().execute(true,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listen);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_vote:

			doPullDate1("2014", new WebRequestUtilListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onSucces(Map<String, Object> data) {
					String resultCode = (String) data.get("result_code");
					if ("0".equals(resultCode)) {
						try {
							Toast.makeText(VoteActivity.this, "投票成功",
									Toast.LENGTH_SHORT).show();
							initDate();

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(VoteActivity.this, "投票失败",
								Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onFail(Map<String, Object> data) {
					Toast.makeText(VoteActivity.this,
							String.valueOf(data.get("message")),
							Toast.LENGTH_SHORT).show();

				}

				@Override
				public void onError() {
				}
			});
			break;
		case R.id.abc_team__btn_back:
			finish();
			break;
		case R.id.btn_save:
			if (edit.getText().toString() == null
					|| edit.getText().toString().equals("")) {
				Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();

				return;

			}
			MobclickAgent.onEvent(this, "match_reply");
			TCAgent.onEvent(this, "match_reply");

			if (u_type == 1) {
				doPullDateRecords("2009", new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						String resultCode = (String) data.get("result_code");
						if ("0".equals(resultCode)) {
							try {
								Toast.makeText(VoteActivity.this, "评论成功",
										Toast.LENGTH_SHORT).show();
								edit.setText("");
								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(
										main_lay.getWindowToken(), 0);
								initDate();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Toast.makeText(VoteActivity.this, "评论失败",
									Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFail(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(VoteActivity.this,
								String.valueOf(data.get("message")),
								Toast.LENGTH_SHORT).show();

					}

					@Override
					public void onError() {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(VoteActivity.this, "请检查您的网络",
								Toast.LENGTH_SHORT).show();
					}
				});
			} else {
				doPullDateRecords("2009", new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						String resultCode = (String) data.get("result_code");
						if ("0".equals(resultCode)) {
							try {
								Toast.makeText(VoteActivity.this, "回复成功",
										Toast.LENGTH_SHORT).show();
								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(
										main_lay.getWindowToken(), 0);
								edit.setText("");
								initDate();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Toast.makeText(VoteActivity.this, "回复失败",
									Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFail(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(VoteActivity.this,
								String.valueOf(data.get("message")),
								Toast.LENGTH_SHORT).show();

					}

					@Override
					public void onError() {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(VoteActivity.this, "请检查您的网络",
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			break;
		default:
			break;
		}

	}

	private void doPullDateRecords(String action, WebRequestUtilListener listen) {
		CPorgressDialog.showProgressDialog("发送中", VoteActivity.this);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		parameters.put("u_body", edit.getText().toString());

		if (u_type == 1) {
			parameters.put("u_content_id", activity_id);
			parameters.put("u_comment_id", "0");
		} else {
			parameters.put("u_content_id", "0");
			parameters.put("u_comment_id", adapter.getComment_id());
		}
		parameters.put("u_type", "1");
		Log.v("tag", "parameters" + parameters);
		HttpPostUtil mHttpPostUtil = null;
		try {
			AppConfig.getInstance().addSign(parameters);
			mHttpPostUtil = AppConfig.getInstance()
					.makeHttpPostUtil(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// WEB request and deal the listener
		new WebRequestUtil(mHttpPostUtil).executeWithOutCache(listen);
	}

	public String getOptionId() {
		String str = "";
		try {
			for (int i = 0; i < isCheck.size(); i++) {
				if (isCheck.get(i)) {
					str += "," + options.getJSONObject(i).getString("id");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (str.equals(""))
			return "";
		else
			return str.substring(1);
	}

	private void doPullDate1(String action, WebRequestUtilListener listen) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		parameters.put("u_page", "1");
		parameters.put("u_activity_id", id + "");
		parameters.put("u_answer", getOptionId());
		parameters.put("u_coin", "0");
		// Toast.makeText(VoteActivity.this, "parameters" + parameters,
		// 0).show();
		HttpPostUtil mHttpPostUtil = null;
		try {
			AppConfig.getInstance().addSign(parameters);
			mHttpPostUtil = AppConfig.getInstance()
					.makeHttpPostUtil(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// WEB request and deal the listener
		new WebRequestUtil(mHttpPostUtil).executeWithOutCache(listen);
	}

	private void onFinishLoad(boolean isRefresh) {
		if (isRefresh)
			mPullToRefreshView.onHeaderRefreshComplete("更新于:"
					+ new Date().toLocaleString());
		else
			mPullToRefreshView.onFooterRefreshComplete();
		// comment_list.stopRefresh();
		// comment_list.stopLoadMore();
		// comment_list.setRefreshTime(DateUtil.date2String());
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		page++;
		refreshLoad(false, page);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		refreshLoad(true, 1);
	}

}
