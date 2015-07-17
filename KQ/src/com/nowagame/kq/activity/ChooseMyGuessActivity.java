package com.nowagame.kq.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.GuessCommentView;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.ShareUtils;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("InlinedApi")
public class ChooseMyGuessActivity extends AgentActivity implements
		OnClickListener {

	// private Button goBack_Btn;
	private TextView referenceQuestions_TextView, referenceBet_TextView,
			sure_TextView, date, time, team1_win_rate, team2_win_rate,
			team1_win_sum, none_win_sum, team2_win_sum, team_none_win_rate,
			match_name;
	private PopupWindow referencePop;
	private View referenceView;
	// private ListView questions_ListView;
	private CreatMyBragActivityReferenceAdapter adapter;
	// private ArrayList array;
	private EditText et_price;
	private RelativeLayout rl_main;
	// private boolean isQFalg = true;
	private CircleImageView team1_icon, team2_icon;
	private LinearLayout ll_report, ll_team1, ll_none, ll_team2;

	private JSONArray records = new JSONArray();

	private static final int SHOW_DATAPICK = 0;
	private static final int DATE_DIALOG_ID = 1;
	private static final int SHOW_TIMEPICK = 2;
	private static final int TIME_DIALOG_ID = 3;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;

	private String wagerId = "";
	private String roomId = "";
	// 默认
	private String team1_win_rate_str = "", team2_win_rate_str = "",
			none_win_rate_str = "", deposit = "", start_time = "";
	// 自定义
	private String my_team1_win_rate_str = "", my_team2_win_rate_str = "",
			my_none_win_rate_str = "";

	private String winType = "";
	ImagerLoader loader = new ImagerLoader();

	private TextView team1_name, team2_name;
	private ShareUtils shareUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_chooseguess);
		shareUtil = new ShareUtils(this);
		wagerId = getIntent().getStringExtra("id");
		init();
		AddOnClickListener();
		initData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		shareUtil.ssoResult(requestCode, resultCode, data);
	}

	public void OnShare(View view) {
		shareUtil.open();
	}

	private void initData() {
		doPullDate(false, "2048", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {

						JSONObject match = resp.getJson()
								.getJSONObject("match");
						match_name.setText(match.getString("name"));
						team1_win_rate_str = match.getString("team1_win_rate");
						team2_win_rate_str = match.getString("team2_win_rate");
						none_win_rate_str = match.getString("none_win_rate");

						my_team1_win_rate_str = match
								.getString("team1_win_rate");
						my_team2_win_rate_str = match
								.getString("team2_win_rate");
						my_none_win_rate_str = match.getString("none_win_rate");
						team1_win_rate.setText("主胜\n" + team1_win_rate_str);
						team2_win_rate.setText("客胜\n" + team2_win_rate_str);
						team_none_win_rate.setText("平\n" + none_win_rate_str);
						loader.LoadImage(match.getJSONObject("team1")
								.getString("icon"), team1_icon);
						loader.LoadImage(match.getJSONObject("team2")
								.getString("icon"), team2_icon);
						team1_name.setText(match.getJSONObject("team1")
								.getString("name"));
						team2_name.setText(match.getJSONObject("team2")
								.getString("name"));
						// new DownAndShowImageTask(match.getJSONObject("team1")
						// .getString("icon"), team1_icon).execute();
						// new DownAndShowImageTask(match.getJSONObject("team2")
						// .getString("icon"), team2_icon).execute();

						JSONObject wager = resp.getJson()
								.getJSONObject("wager");
						team1_win_sum.setText(wager
								.getString("max_team1_deposit"));
						team2_win_sum.setText(wager
								.getString("max_team2_deposit"));
						none_win_sum.setText(wager
								.getString("max_none_deposit"));
						JSONArray records = resp.getJson().getJSONArray(
								"records");
						ll_report.removeAllViews();
						for (int i = 0; i < records.length(); i++) {
							GuessCommentView view = new GuessCommentView(
									ChooseMyGuessActivity.this);
							ll_report.addView(view.getView(records
									.getJSONObject(i)));
						}

					} else {
						Toast.makeText(ChooseMyGuessActivity.this,
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
				Toast.makeText(ChooseMyGuessActivity.this,
						resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void back(View view) {
		finish();
	}

	public void Team1win(View view) {
		team1_win_rate.setTextColor(getResources().getColor(R.color.white));
		ll_team1.setBackgroundResource(R.drawable.abc_button_roundcorner_create);

		team2_win_rate
				.setTextColor(getResources().getColor(R.color.text_color));
		ll_team2.setBackgroundResource(R.drawable.abc_button_roundcorner_chuiniu);
		team_none_win_rate.setTextColor(getResources().getColor(
				R.color.text_color));
		ll_none.setBackgroundResource(R.drawable.abc_button_roundcorner_chuiniu);

		winType = "0";
	}

	public void Team2win(View view) {
		team2_win_rate.setTextColor(getResources().getColor(R.color.white));
		ll_team2.setBackgroundResource(R.drawable.abc_button_roundcorner_create);

		team1_win_rate
				.setTextColor(getResources().getColor(R.color.text_color));
		ll_team1.setBackgroundResource(R.drawable.abc_button_roundcorner_chuiniu);
		team_none_win_rate.setTextColor(getResources().getColor(
				R.color.text_color));
		ll_none.setBackgroundResource(R.drawable.abc_button_roundcorner_chuiniu);

		winType = "1";
	}

	public void Nonewin(View view) {
		team_none_win_rate.setTextColor(getResources().getColor(R.color.white));
		ll_none.setBackgroundResource(R.drawable.abc_button_roundcorner_create);

		team1_win_rate
				.setTextColor(getResources().getColor(R.color.text_color));
		ll_team1.setBackgroundResource(R.drawable.abc_button_roundcorner_chuiniu);
		team2_win_rate
				.setTextColor(getResources().getColor(R.color.text_color));
		ll_team2.setBackgroundResource(R.drawable.abc_button_roundcorner_chuiniu);

		winType = "2";
	}

	public void OnReduce(View view) {
		int reduce = Integer.parseInt(et_price.getText().toString()) - 50;

		if (reduce < 0)
			et_price.setText("0");
		else
			et_price.setText(String.valueOf(reduce));

	}

	public void OnAdd(View view) {
		int add = Integer.parseInt(et_price.getText().toString()) + 50;
		et_price.setText(String.valueOf(add));
	}

	private void init() {
		roomId = getIntent().getStringExtra("roomId");

		ll_team1 = (LinearLayout) findViewById(R.id.ll_team1);
		ll_team2 = (LinearLayout) findViewById(R.id.ll_team2);
		ll_none = (LinearLayout) findViewById(R.id.ll_none);

		rl_main = (RelativeLayout) findViewById(R.id.rl_main);
		ll_report = (LinearLayout) findViewById(R.id.ll_report);
		referenceQuestions_TextView = (TextView) findViewById(R.id.referenceQuestions_TextView);
		// referenceQuestions_EditText = (EditText)
		// findViewById(R.id.referenceQuestions_EditText);
		match_name = (TextView) findViewById(R.id.match_name);
		referenceBet_TextView = (TextView) findViewById(R.id.referenceBet_TextView);
		et_price = (EditText) findViewById(R.id.et_price);
		// referenceBet_EditText = (EditText)
		// findViewById(R.id.referenceBet_EditText);

		sure_TextView = (TextView) findViewById(R.id.sure_TextView);
		team1_win_sum = (TextView) findViewById(R.id.team1_win_sum);
		none_win_sum = (TextView) findViewById(R.id.none_win_sum);
		team2_win_sum = (TextView) findViewById(R.id.team2_win_sum);

		team1_icon = (CircleImageView) findViewById(R.id.team1_icon);
		team2_icon = (CircleImageView) findViewById(R.id.team2_icon);
		team1_win_rate = (TextView) findViewById(R.id.team1_win_rate);
		team2_win_rate = (TextView) findViewById(R.id.team2_win_rate);
		team_none_win_rate = (TextView) findViewById(R.id.team_none_win_rate);

		team1_name = (TextView) findViewById(R.id.team1_name);

		team2_name = (TextView) findViewById(R.id.team2_name);
		Button done = (Button) findViewById(R.id.done);
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String msg = chargeEditData();
				if (msg != null) {

					Toast.makeText(ChooseMyGuessActivity.this, msg,
							Toast.LENGTH_SHORT).show();
				} else {
					// team1_win_rate, team2_win_rate,
					// team_none_win_rate
					CPorgressDialog
							.showProgressDialog(ChooseMyGuessActivity.this);
					doPullDate(false, "2047", new MCHttpCallBack() {
						@Override
						public void onSuccess(MCHttpResp resp) {
							super.onSuccess(resp);
							CPorgressDialog.hideProgressDialog();
							try {
								String resultCode = resp.getJson().getString(
										"result_code");
								if (resultCode.equals("0")) {
									Toast.makeText(ChooseMyGuessActivity.this,
											"投注成功", Toast.LENGTH_SHORT).show();
									initData();
								} else {
									Toast.makeText(
											ChooseMyGuessActivity.this,
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
							Toast.makeText(ChooseMyGuessActivity.this,
									resp.getErrorMessage(), Toast.LENGTH_SHORT)
									.show();
						}
					});
				}
			}
		});
	}

	private void AddOnClickListener() {
		referenceQuestions_TextView.setOnClickListener(this);
		referenceBet_TextView.setOnClickListener(this);
		sure_TextView.setOnClickListener(this);
		// date.setClickable(true);
		// time.setClickable(true);
		// date.setOnClickListener(this);
		// time.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v == referenceQuestions_TextView) {
			openPop();
		} else if (v == sure_TextView) {
			Log.v("tag", ">>>   点击了完成  >>>>");

		}
	}

	private void doPullDate(boolean isLazyLoad, String action,
			MCHttpCallBack listen) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("app_version", UpdataUtil
				.getAppVersion(this)));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_wager_id", wagerId));
		if (action.equals("2047")) {
			pair.add(new BasicNameValuePair("u_type", winType));
			pair.add(new BasicNameValuePair("u_bet_score", et_price.getText()
					.toString()));

		}

		// pair.add(new BasicNameValuePair("u_room_id", roomId));
		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

	private String getEndTime() {
		Date d = new Date(mYear - 1900, mMonth, mDay, mHour, mMinute);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(d);
	}

	private void openPop() {
		// 关闭键盘
		((InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(ChooseMyGuessActivity.this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		referencePop = new PopupWindow(this);
		referenceView = getLayoutInflater().inflate(
				R.layout.abc_activity_creatguess_referencepop, null);
		referencePop.setBackgroundDrawable(new ColorDrawable(0));
		referencePop.setWidth(LayoutParams.MATCH_PARENT);
		referencePop.setHeight(LayoutParams.WRAP_CONTENT);
		referencePop.setBackgroundDrawable(new BitmapDrawable());
		referencePop.setFocusable(true);
		referencePop.setOutsideTouchable(true);
		referencePop.setContentView(referenceView);

		final EditText et_win1_rat = (EditText) referenceView
				.findViewById(R.id.et_win1_rat);
		final EditText et_win2_rat = (EditText) referenceView
				.findViewById(R.id.et_win2_rat);
		final EditText et_none_win_rat = (EditText) referenceView
				.findViewById(R.id.et_none_win_rat);
		et_win1_rat.setText(team1_win_rate_str);
		et_win2_rat.setText(team2_win_rate_str);
		et_none_win_rat.setText(none_win_rate_str);

		// listView 数据源
		// initreference();

		// questions_ListView.setOnItemClickListener(new OnItemClickListener());
		if (referencePop.isShowing()) {
			referencePop.dismiss();
		} else {
			// View view = getLayoutInflater().inflate(
			// R.layout.abc_activity_creatbrag, null);
			// referencePop.showAtLocation(, Gravity.CENTER, 0, 0);
			referencePop.showAtLocation(rl_main, Gravity.CENTER, 0, 0);
		}
	}

	private String chargeEditData() {
		String msg = et_price.getText().toString();
		if (msg == null || msg.equals("")) {
			return "请输入投注积分";
		} else if (winType.equals("")) {
			return "请选择投注对象";
		}
		return null;
	}
	// class OnItemClickListener implements
	// android.widget.AdapterView.OnItemClickListener {
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int option,
	// long arg3) {
	// Log.v("tag", "点击了>>>>" + option);
	// try {
	// if (type == 0) {
	// //
	// // referenceQuestions_EditText
	// // .setText(records.getJSONObject(option)
	// // .getString("name").toString());
	//
	// } else if (type == 1) {
	// referenceBet_EditText.setText(records.getJSONObject(option)
	// .getString("name").toString());
	// }
	// referencePop.dismiss();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }

}
