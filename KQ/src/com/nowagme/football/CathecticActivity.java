package com.nowagme.football;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import cn.kangeqiu.kq.activity.view.CommentView;
import cn.kangeqiu.kq.adapter.Adaptercomment;
import cn.kangeqiu.kq.refresh.PullToRefreshView;
import cn.kangeqiu.kq.refresh.PullToRefreshView.OnFooterRefreshListener;
import cn.kangeqiu.kq.refresh.PullToRefreshView.OnHeaderRefreshListener;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagame.kq.activity.MyHouseActivity;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.ShareUtils;
import com.nowagme.util.WebRequestUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

public class CathecticActivity extends AgentActivity implements
		OnClickListener, OnHeaderRefreshListener, OnFooterRefreshListener {
	private int id, options_id, answer_id, correct_state;
	private TextView quiz_title, quiz_content, player_content, player_content1,
			txt_num;
	private Button btn_quiz1, btn_quiz2, btn_quiz3, abc_team__btn_back,
			btn_quiz0, btn_save;
	private Button[] btn_quiz;
	// private ListView list;
	private JSONObject activity = new JSONObject();
	private JSONArray records = new JSONArray();
	private JSONArray options = new JSONArray();
	JSONObject myoptions = new JSONObject();
	// private Adaptercomment adapter;
	private LinearLayout rel_main2;
	private RelativeLayout main_lay;
	private String coin_minus, join_state, state, activity_id;
	private static EditText edit;
	public int u_type;// 1：评论 2:回复
	public String comment_id = "";
	LinearLayout list;
	private PullToRefreshView mPullToRefreshView;
	private int page = 1;
	TextView tv_num;// 用来显示剩余字数<br>
	int num = 140;// 限制的最大字数
	CommentView view;
	private ShareUtils shareUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cathectic_item);
		u_type = 1;
		id = Integer.parseInt(getIntent().getStringExtra("id"));
		initDate();
		initview();
		// adapter = new Adaptercomment(this);
		// list.setAdapter(adapter);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(main_lay.getWindowToken(), 0);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		shareUtil = new ShareUtils(this);

		edit.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
				System.out.println("s=" + s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int number = num - s.length();
				// tv_num.setText("" + number);
				selectionStart = edit.getSelectionStart();
				selectionEnd = edit.getSelectionEnd();
				// System.out.println("start="+selectionStart+",end="+selectionEnd);
				if (temp.length() > num) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionStart;
					edit.setText(s);
					edit.setSelection(tempSelection);// 设置光标在最后
					Toast.makeText(CathecticActivity.this, "字数过长，最多140字",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void OnShare(View view) {
		shareUtil.open();
	}

	public void setCommentId(String commentId) {
		this.comment_id = commentId;
	}

	public void u_type(String nickName) {
		u_type = 2;
		if (view.getReply_nickname() != null) {
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
		quiz_title = (TextView) findViewById(R.id.quiz_title);
		quiz_content = (TextView) findViewById(R.id.quiz_content);
		player_content = (TextView) findViewById(R.id.player_content);
		player_content1 = (TextView) findViewById(R.id.player_content1);
		txt_num = (TextView) findViewById(R.id.txt_num);
		main_lay = (RelativeLayout) findViewById(R.id.main_lay);
		btn_quiz1 = (Button) findViewById(R.id.btn_quiz1);
		btn_quiz2 = (Button) findViewById(R.id.btn_quiz2);
		btn_quiz3 = (Button) findViewById(R.id.btn_quiz3);
		btn_quiz0 = (Button) findViewById(R.id.btn_quiz);
		abc_team__btn_back = (Button) findViewById(R.id.abc_team__btn_back);
		list = (LinearLayout) findViewById(R.id.list);
		rel_main2 = (LinearLayout) findViewById(R.id.rel_main2);
		btn_save = (Button) findViewById(R.id.btn_save);
		edit = (EditText) findViewById(R.id.edit);
		btn_save.setOnClickListener(this);
		btn_quiz1.setOnClickListener(this);
		btn_quiz2.setOnClickListener(this);
		btn_quiz3.setOnClickListener(this);
		abc_team__btn_back.setOnClickListener(this);

		// list.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// final int position, long id) {
		//
		//
		//
		// }
		// });
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

	/**
	 * 更新数据
	 */
	public void refreshLoad(final boolean isRefresh, final int pageNo) {

		// 调取个人信息
		doPullDate("2013", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				onFinishLoad(isRefresh);
				CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						if (isRefresh) {
							records = (JSONArray) resp.getJson().getJSONArray(
									"records");
							list.removeAllViews();
							// for (int i = 0; i < records.length(); i++) {
							// // adapter.setItem(records);
							// //
							// Utility.setListViewHeightBasedOnChildren(list);
							// view = new CommentView(
							// CathecticActivity.this);
							// list.addView(view.getView(records
							// .getJSONObject(i)));
							// }
						} else {
							JSONArray moreRecords = resp.getJson()
									.getJSONArray("records");
							if (moreRecords.length() < 1) {
								page--;
								Toast.makeText(CathecticActivity.this,
										"没有更多数据了", Toast.LENGTH_SHORT).show();
							} else {
								list.removeAllViews();
								for (int i = 0; i < moreRecords.length(); i++) {
									records.put(moreRecords.getJSONObject(i));
									view = new CommentView(
											CathecticActivity.this);
									list.addView(view.getView(moreRecords
											.getJSONObject(i)));
								}
								// adapter.setItem(records);
								// Utility.setListViewHeightBasedOnChildren(list);
							}
						}
					} else {
						Toast.makeText(CathecticActivity.this,
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
				Toast.makeText(CathecticActivity.this, resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void initDate() {

		doPullDate("2013", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						activity = (JSONObject) resp.getJson().getJSONObject(
								"activity");
						records = (JSONArray) resp.getJson().getJSONArray(
								"records");
						JSONObject match = (JSONObject) resp.getJson()
								.getJSONObject("match");
						if (activity != null) {
							// 设置分享的内容
							shareUtil.setShareContent(
									"我参与了＃"
											+ match.getJSONObject("team1")
													.getString("name")
											+ "vs"
											+ match.getJSONObject("team2")
													.getString("name") + "#竞猜："
											+ activity.getString("title")
											+ "，有机会赢正品球衣哦！",
									R.drawable.match_share_logo);

							activity_id = activity.getString("id");
							quiz_title.setText(activity.getString("title"));
							quiz_content.setText(activity.getString("body"));
							state = activity.getString("state");
							txt_num.setText(activity.getString("comment_count"));
							options = (JSONArray) activity
									.getJSONArray("options");
							Log.v("tag", "options" + options.length() + "");
							join_state = activity.getString("join_state");
							coin_minus = activity.getString("coin_minus");
							if (state.equals("0")) {
								rel_main2.setVisibility(View.GONE);
								btn_quiz0.setVisibility(View.VISIBLE);
								btn_quiz0.setText("活动尚未开始");
								btn_quiz0.setFocusable(false);
								btn_quiz0.setClickable(false);
								btn_quiz0
										.setBackgroundResource(R.drawable.abc_button_roundcorner_lost);
								player_content1.setVisibility(View.GONE);
								player_content.setText(activity.get(
										"join_count").toString()
										+ "人参与");
							} else if (state.equals("1")) {
								if (join_state.equals("0")) {
									rel_main2.setVisibility(View.VISIBLE);
									btn_quiz0.setVisibility(View.GONE);
									player_content1.setVisibility(View.GONE);
									if (options.length() == 2) {
										btn_quiz = new Button[2];
										btn_quiz[0] = btn_quiz1;
										btn_quiz[1] = btn_quiz2;
										btn_quiz3.setVisibility(View.GONE);
										btn_quiz1.setVisibility(View.VISIBLE);
										btn_quiz2.setVisibility(View.VISIBLE);
										for (int i = 0; i < options.length(); i++) {
											// options_id = Integer
											// .parseInt(options
											// .getJSONObject(i)
											// .getString("id"));
											btn_quiz[i].setText(options
													.getJSONObject(i)
													.getString("name"));
										}
									} else if (options.length() == 3) {

										btn_quiz = new Button[3];
										btn_quiz[0] = btn_quiz1;
										btn_quiz[1] = btn_quiz2;
										btn_quiz[2] = btn_quiz3;
										btn_quiz3.setVisibility(View.VISIBLE);
										btn_quiz1.setVisibility(View.VISIBLE);
										btn_quiz2.setVisibility(View.VISIBLE);
										for (int i = 0; i < options.length(); i++) {
											// options_id = Integer
											// .parseInt(options
											// .getJSONObject(i)
											// .getString("id"));
											btn_quiz[i].setText(options
													.getJSONObject(i)
													.getString("name"));
										}
									}
								} else if (join_state.equals("1")) {
									myoptions = (JSONObject) activity
											.getJSONObject("myoptions");
									btn_quiz0.setVisibility(View.VISIBLE);
									player_content1.setVisibility(View.VISIBLE);
									rel_main2.setVisibility(View.GONE);
									btn_quiz0.setText("已投注");
									btn_quiz0.setFocusable(false);
									btn_quiz0.setClickable(false);
									btn_quiz0
											.setBackgroundResource(R.drawable.abc_button_roundcorner_lost);
									answer_id = Integer.parseInt(myoptions
											.getString("answer"));
									for (int i = 0; i < options.length(); i++) {
										if (Integer.parseInt(options
												.getJSONObject(i).getString(
														"id")) == answer_id) {
											player_content1.setText("我猜："
													+ options.getJSONObject(i)
															.getString("name")
													+ "("
													+ myoptions
															.getString("coin")
													+ ")");
										}

									}

								}
								player_content.setText(activity
										.getString("join_count") + "人参与 ");
							} else if (state.equals("2")) {
								String name = "";
								rel_main2.setVisibility(View.GONE);
								btn_quiz0.setVisibility(View.VISIBLE);
								btn_quiz0.setText("已结束");
								btn_quiz0.setFocusable(false);
								btn_quiz0.setClickable(false);
								player_content.setVisibility(View.VISIBLE);

								if (join_state.equals("1")) {
									answer_id = Integer.parseInt(myoptions
											.getString("answer"));
									for (int i = 0; i < options.length(); i++) {
										if (Integer.parseInt(options
												.getJSONObject(i).getString(
														"id")) == answer_id) {
											player_content1.setText("我猜："
													+ options.getJSONObject(i)
															.getString("name")
													+ "("
													+ myoptions
															.getString("coin")
													+ ")");
										}

										correct_state = Integer
												.parseInt(options
														.getJSONObject(i)
														.getString(
																"correct_state"));
										if (correct_state == 1) {
											name = options.getJSONObject(i)
													.getString("name");
										}

									}
								} else {
									player_content1.setText("我没猜");

									for (int i = 0; i < options.length(); i++) {
										correct_state = Integer
												.parseInt(options
														.getJSONObject(i)
														.getString(
																"correct_state"));
										if (correct_state == 1) {
											name = options.getJSONObject(i)
													.getString("name");
										}
									}

								}

								player_content.setText("最终结果：" + name);

							}

						}
						list.removeAllViews();
						if (records != null) {
							for (int j = 0; j < records.length(); j++) {
								view = new CommentView(CathecticActivity.this);
								list.addView(view.getView(records
										.getJSONObject(j)));
							}

						}
						// adapter.setItem(records);
						// Utility.setListViewHeightBasedOnChildren(list);
					} else {
						Toast.makeText(CathecticActivity.this,
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
				Toast.makeText(CathecticActivity.this, resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
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
		try {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.btn_quiz1:
				intent.setClass(this, QuizActivity.class);

				intent.putExtra("options_id", options.getJSONObject(0)
						.getString("id"));

				intent.putExtra("id", id + "");
				intent.putExtra("coin_minus", coin_minus);
				intent.putExtra("join_state", join_state);
				startActivityForResult(intent, 100);
				break;
			case R.id.btn_quiz2:
				intent.setClass(this, QuizActivity.class);
				intent.putExtra("options_id", options.getJSONObject(1)
						.getString("id"));
				intent.putExtra("id", id + "");
				intent.putExtra("coin_minus", coin_minus);
				intent.putExtra("join_state", join_state);
				startActivityForResult(intent, 100);
				break;
			case R.id.btn_quiz3:
				intent.setClass(this, QuizActivity.class);
				intent.putExtra("options_id", options.getJSONObject(2)
						.getString("id"));
				intent.putExtra("id", id + "");
				intent.putExtra("coin_minus", coin_minus);
				intent.putExtra("join_state", join_state);
				startActivityForResult(intent, 100);
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

					doPullDateRecords("2009", new MCHttpCallBack() {
						@Override
						public void onSuccess(MCHttpResp resp) {
							super.onSuccess(resp);
							CPorgressDialog.hideProgressDialog();
							try {
								String resultCode = resp.getJson().getString(
										"result_code");
								if (resultCode.equals("0")) {
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(
											main_lay.getWindowToken(), 0);
									edit.setText("");
									initDate();
									Toast.makeText(CathecticActivity.this,
											"评论成功", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											CathecticActivity.this,
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
							Toast.makeText(CathecticActivity.this,
									resp.getErrorMessage(), Toast.LENGTH_SHORT)
									.show();
						}
					});
				} else if (u_type == 2) {
					doPullDateRecords("2009", new MCHttpCallBack() {
						@Override
						public void onSuccess(MCHttpResp resp) {
							super.onSuccess(resp);
							CPorgressDialog.hideProgressDialog();
							try {
								String resultCode = resp.getJson().getString(
										"result_code");
								if (resultCode.equals("0")) {
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(
											main_lay.getWindowToken(), 0);
									edit.setText("");
									initDate();
									Toast.makeText(CathecticActivity.this,
											"回复成功", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											CathecticActivity.this,
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
							Toast.makeText(CathecticActivity.this,
									resp.getErrorMessage(), Toast.LENGTH_SHORT)
									.show();
						}
					});
				}

				break;
			default:
				break;
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void doPullDateRecords(String action, MCHttpCallBack listen) {
		CPorgressDialog.showProgressDialog(this);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_body", edit.getText().toString()));

		if (u_type == 1) {
			pair.add(new BasicNameValuePair("u_content_id", activity_id));
			pair.add(new BasicNameValuePair("u_comment_id", "0"));
		} else {
			pair.add(new BasicNameValuePair("u_content_id", "0"));
			pair.add(new BasicNameValuePair("u_comment_id", view
					.getComment_id()));

		}
		pair.add(new BasicNameValuePair("u_type", "1"));

		new WebRequestUtil().execute(false,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listen);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		shareUtil.ssoResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 100:
			// btn_quiz0.setVisibility(View.VISIBLE);
			// rel_main2.setVisibility(View.GONE);
			// btn_quiz0.setText("已投注");
			// btn_quiz0.setFocusable(false);
			// btn_quiz0.setClickable(false);
			// btn_quiz0.setBackgroundColor(Color.parseColor("#8E8D8B"));
			// try {
			// myoptions = (JSONObject) activity.getJSONObject("myoptions");
			// // answer_id =
			// // Integer.parseInt(myoptions.get("answer").toString());
			// for (int i = 0; i < options.length(); i++) {
			// if (Integer.parseInt(options.getJSONObject(i).getString(
			// "id")) == answer_id) {
			// player_content1.setText("我猜："
			// + options.getJSONObject(i).getString("name")
			// + "(" + myoptions.getString("coin") + ")");
			// }
			//
			// }
			// player_content.setText(activity.getString("join_count")
			// + "人参与," + "投入最多者" + activity.getString("max_bet"));
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			initDate();
			break;

		default:
			break;
		}
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
