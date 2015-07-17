package com.nowagame.kq.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.TrueView;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;

public class BragDetailActivity extends AgentActivity implements
		OnClickListener {

	private ImageView share_ImageView, iv_person;
	private String id, create_time;
	int newTime;
	private JSONObject brag;
	private TextView txt_name, txt_question, txt_bet, txt_time, txt, text;
	private Button btn_win, btn_lost, btn_xin, btn_no;
	private ImageView my_win;
	private String state = "", state1;
	ImagerLoader loader = new ImagerLoader();
	private JSONArray members = new JSONArray();
	LinearLayout lay, lay_txt, lay1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_bragdetail);
		id = getIntent().getStringExtra("id");
		init();
		initData(true);
		AddOnClickListener();
	}

	public void back(View view) {
		finish();
	}

	private void init() {
		share_ImageView = (ImageView) findViewById(R.id.share_ImageView);
		txt_name = (TextView) findViewById(R.id.txt_name);
		txt = (TextView) findViewById(R.id.txt);
		txt_question = (TextView) findViewById(R.id.txt_question);
		txt_bet = (TextView) findViewById(R.id.txt_bet);
		txt_time = (TextView) findViewById(R.id.txt_time);
		iv_person = (ImageView) findViewById(R.id.abc_fragment_person__iv_person);
		btn_win = (Button) findViewById(R.id.btn_win);
		btn_lost = (Button) findViewById(R.id.btn_lost);
		my_win = (ImageView) findViewById(R.id.my_win);
		lay = (LinearLayout) findViewById(R.id.lay);
		lay_txt = (LinearLayout) findViewById(R.id.lay_txt);
		lay1 = (LinearLayout) findViewById(R.id.lay1);
		btn_xin = (Button) findViewById(R.id.btn_xin);
		btn_no = (Button) findViewById(R.id.btn_no);
		text = (TextView) findViewById(R.id.text);
	}

	private void AddOnClickListener() {
		share_ImageView.setOnClickListener(this);
		btn_win.setOnClickListener(this);
		btn_lost.setOnClickListener(this);
		btn_xin.setOnClickListener(this);
		btn_no.setOnClickListener(this);
	}

	/**
	 * 初始化数据.
	 */
	public void initData(final boolean isMe) {

		// 调取个人信息
		doPullDate(isMe, "2042", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
//				CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						txt_name.setText(resp.getJson().getString("nickname"));
						// new DownAndShowImageTask(resp.getJson().getString(
						// "icon"), iv_person).execute();
						loader.LoadImage(resp.getJson().getString("icon"),
								iv_person);
						brag = (JSONObject) resp.getJson()
								.getJSONObject("brag");
						create_time = brag.getString("create_time");
						SimpleDateFormat format = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date date = null;
						Calendar calendar = null;
						date = format.parse(create_time);
						calendar = Calendar.getInstance();
						calendar.setTime(date);
						newTime = calendar.get(Calendar.MINUTE) + 30;
						members = resp.getJson().getJSONArray("members");
						String brag_id = brag.getString("user_id");
						txt_question.setText(brag.getString("question"));
						txt_bet.setText(brag.getString("bet"));
						txt_time.setText(brag.getString("end_time"));
						String do_flag = brag.getString("do_flag");
						String state = brag.getString("state");
						txt.setText(brag.getString("member_count") + "人参与了");
						if (do_flag.equals("1")) {
							String user_id = "";
							lay.setVisibility(View.VISIBLE);
							if (brag_id.equals(AppConfig.getInstance()
									.getPlayerId() + "")) {
								lay_txt.setVisibility(View.GONE);

								btn_win.setClickable(true);
								btn_lost.setClickable(true);
								btn_win.setVisibility(View.VISIBLE);
								btn_lost.setVisibility(View.VISIBLE);
								btn_win.setText("我赢了");
								btn_lost.setText("我输了");

							} else {
								if (members != null) {
									for (int i = 0; i < members.length(); i++) {
										user_id = members.getJSONObject(i)
												.getString("user_id");
										state1 = members.getJSONObject(i)
												.getString("state");
										if (!user_id.equals(AppConfig
												.getInstance().getPlayerId()
												+ "")) {
											btn_no.setVisibility(View.VISIBLE);
											btn_xin.setVisibility(View.VISIBLE);
											btn_xin.setText("我信");
											btn_no.setText("不信");
										}
										if (user_id.equals(AppConfig
												.getInstance().getPlayerId()
												+ "")) {
											lay_txt.setVisibility(View.VISIBLE);
											btn_no.setVisibility(View.GONE);
											btn_xin.setVisibility(View.GONE);
											if (state1.endsWith("0")) {
//												text.setText("我选择“不信");
											} else if (state1.endsWith("1")) {
//												text.setText("我选择了“我信”");
											} else {
												btn_no.setVisibility(View.VISIBLE);
												btn_xin.setVisibility(View.VISIBLE);
											}
										}
									}
								}
								if (members.length() == 0) {
									btn_no.setVisibility(View.VISIBLE);
									btn_xin.setVisibility(View.VISIBLE);
									btn_xin.setText("我信");
									btn_no.setText("不信");
								} else {
									for (int i = 0; i < members.length(); i++) {
										user_id = members.getJSONObject(i)
												.getString("user_id");
										if (user_id.equals(AppConfig
												.getInstance().getPlayerId()
												+ "")) {

											btn_no.setVisibility(View.GONE);
											btn_xin.setVisibility(View.GONE);
										}
									}

								}
							}

						} else {
							lay.setVisibility(View.GONE);
							lay_txt.setVisibility(View.VISIBLE);
							btn_no.setVisibility(View.GONE);
							btn_xin.setVisibility(View.GONE);
							if (brag_id.equals(AppConfig.getInstance()
									.getPlayerId() + "")) {
								btn_win.setVisibility(View.GONE);
								btn_lost.setVisibility(View.GONE);
								if (state.endsWith("0")) {
									my_win.setVisibility(View.VISIBLE);
									my_win.setImageResource(R.drawable.my_lost);
								} else if (state.endsWith("1")) {
									lay.setVisibility(View.GONE);
									my_win.setVisibility(View.VISIBLE);
									my_win.setImageResource(R.drawable.my_win);

								} else {
									my_win.setVisibility(View.GONE);
									lay.setVisibility(View.GONE);
								}
							}

						}
						lay1.removeAllViews();
						if (members != null) {
							for (int i = 0; i < members.length(); i++) {
								TrueView view = new TrueView(
										BragDetailActivity.this);
								lay1.addView(view.getView(members
										.getJSONObject(i)));
							}
						}

					} else {
						Toast.makeText(BragDetailActivity.this,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(BragDetailActivity.this, resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v == share_ImageView) {
			Intent intent = new Intent();
			intent.setClass(BragDetailActivity.this, BragShareActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
		} else if (v == btn_win) {
			
			if (Calendar.MINUTE < newTime) {
				Toast.makeText(BragDetailActivity.this, "发布半小时之内无法录入结果",
						Toast.LENGTH_SHORT).show();
			} else {
				state = "1";
				doPullDate(false, "2057", new MCHttpCallBack() {
					@Override
					public void onSuccess(MCHttpResp resp) {
						super.onSuccess(resp);
//						CPorgressDialog.hideProgressDialog();
						try {
							String resultCode = resp.getJson().getString(
									"result_code");
							// my_win.setVisibility(View.VISIBLE);
							if (resultCode.equals("0")) {
								Toast.makeText(BragDetailActivity.this, "我赢了",
										Toast.LENGTH_SHORT).show();
								initData(true);
							} else {
								Toast.makeText(BragDetailActivity.this,
										resp.getJson().getString("message"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(MCHttpResp resp) {
						super.onError(resp);
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(BragDetailActivity.this,
								resp.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				});
			}

		} else if (v == btn_lost) {
			
			if (Calendar.MINUTE < newTime) {
				Toast.makeText(BragDetailActivity.this, "发布半小时之内无法录入结果",
						Toast.LENGTH_SHORT).show();
			} else {
				state = "0";
				doPullDate(false, "2057", new MCHttpCallBack() {
					@Override
					public void onSuccess(MCHttpResp resp) {
						super.onSuccess(resp);
//						CPorgressDialog.hideProgressDialog();
						try {
							String resultCode = resp.getJson().getString(
									"result_code");
							if (resultCode.equals("0")) {
								Toast.makeText(BragDetailActivity.this, "我输了",
										Toast.LENGTH_SHORT).show();
								initData(true);
							} else {
								Toast.makeText(BragDetailActivity.this,
										resp.getJson().getString("message"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(MCHttpResp resp) {
						super.onError(resp);
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(BragDetailActivity.this,
								resp.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				});
			}
		} else if (v == btn_xin) {
			state1 = "1";
			doPullDate(false, "2064", new MCHttpCallBack() {
				@Override
				public void onSuccess(MCHttpResp resp) {
					super.onSuccess(resp);
//					CPorgressDialog.hideProgressDialog();
					try {
						String resultCode = resp.getJson().getString(
								"result_code");
						if (resultCode.equals("0")) {
							Toast.makeText(BragDetailActivity.this, "我信",
									Toast.LENGTH_SHORT).show();
							initData(true);
						} else {
							Toast.makeText(BragDetailActivity.this,
									resp.getJson().getString("message"),
									Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(MCHttpResp resp) {
					super.onError(resp);
					CPorgressDialog.hideProgressDialog();
					Toast.makeText(BragDetailActivity.this,
							resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
				}
			});
		} else if (v == btn_no) {
			state1 = "0";
			doPullDate(false, "2064", new MCHttpCallBack() {
				@Override
				public void onSuccess(MCHttpResp resp) {
					super.onSuccess(resp);
//					CPorgressDialog.hideProgressDialog();
					try {
						String resultCode = resp.getJson().getString(
								"result_code");
						if (resultCode.equals("0")) {
							Toast.makeText(BragDetailActivity.this, "不信",
									Toast.LENGTH_SHORT).show();
							initData(true);
						} else {
							Toast.makeText(BragDetailActivity.this,
									resp.getJson().getString("message"),
									Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(MCHttpResp resp) {
					super.onError(resp);
					CPorgressDialog.hideProgressDialog();
					Toast.makeText(BragDetailActivity.this,
							resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
				}
			});
		}

	}

	private void doPullDate(boolean isLazyLoad, String action,
			MCHttpCallBack listen) {
//		CPorgressDialog.showProgressDialog(BragDetailActivity.this);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("app_version", UpdataUtil
				.getAppVersion(this)));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_brag_id", id));
		if (action.equals("2057")) {
			pair.add(new BasicNameValuePair("u_state", state));
		} else if (action.equals("2064")) {
			pair.add(new BasicNameValuePair("u_state", state1));
		}
		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

}
