package com.nowagame.kq.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("InlinedApi")
public class CreatMyGuessActivity extends AgentActivity implements
		OnClickListener {

	// private Button goBack_Btn;
	private TextView referenceQuestions_TextView, referenceBet_TextView,
			sure_TextView, date, time, team1_win_rate, team2_win_rate,
			team_none_win_rate, match_name;
	private PopupWindow referencePop;
	private View referenceView;
	// private ListView questions_ListView;
	private CreatMyBragActivityReferenceAdapter adapter;
	// private ArrayList array;
	private EditText referenceBet_EditText;
	private RelativeLayout rl_main;
	// private boolean isQFalg = true;
	private CircleImageView team1_icon, team2_icon;

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

	private String roomId = "";
	// 默认
	private String team1_win_rate_str = "", team2_win_rate_str = "",
			none_win_rate_str = "", deposit = "", start_time = "";
	// 自定义
	private String my_team1_win_rate_str = "", my_team2_win_rate_str = "",
			my_none_win_rate_str = "";
	private ImagerLoader loader = new ImagerLoader();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_creatguess);

		init();
		AddOnClickListener();
		initData();
	}

	private void initData() {
		doPullDate(false, "2046", new MCHttpCallBack() {
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
						team1_win_rate.setText("主胜 " + team1_win_rate_str);
						team2_win_rate.setText("客胜 " + team2_win_rate_str);
						team_none_win_rate.setText("平 " + none_win_rate_str);
						loader.LoadImage(match.getJSONObject("team1")
								.getString("icon"), team1_icon);
						loader.LoadImage(match.getJSONObject("team2")
								.getString("icon"), team2_icon);
						// new DownAndShowImageTask(match.getJSONObject("team1")
						// .getString("icon"), team1_icon).execute();
						// new DownAndShowImageTask(match.getJSONObject("team2")
						// .getString("icon"), team2_icon).execute();
						start_time = match.getString("time");

						date.setText(start_time.substring(0,
								start_time.indexOf(" ")));
						time.setText(start_time.substring(start_time
								.indexOf(" ")));
					} else {
						Toast.makeText(CreatMyGuessActivity.this,
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
				Toast.makeText(CreatMyGuessActivity.this,
						resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void back(View view) {
		finish();
	}

	private void init() {
		roomId = getIntent().getStringExtra("roomId");

		rl_main = (RelativeLayout) findViewById(R.id.rl_main);

		referenceQuestions_TextView = (TextView) findViewById(R.id.referenceQuestions_TextView);
		// referenceQuestions_EditText = (EditText)
		// findViewById(R.id.referenceQuestions_EditText);
		match_name = (TextView) findViewById(R.id.match_name);
		referenceBet_TextView = (TextView) findViewById(R.id.referenceBet_TextView);
		referenceBet_EditText = (EditText) findViewById(R.id.referenceBet_EditText);

		sure_TextView = (TextView) findViewById(R.id.sure_TextView);

		date = (TextView) findViewById(R.id.tx_date);
		time = (TextView) findViewById(R.id.tx_time);

		team1_icon = (CircleImageView) findViewById(R.id.team1_icon);
		team2_icon = (CircleImageView) findViewById(R.id.team2_icon);
		team1_win_rate = (TextView) findViewById(R.id.team1_win_rate);
		team2_win_rate = (TextView) findViewById(R.id.team2_win_rate);
		team_none_win_rate = (TextView) findViewById(R.id.team_none_win_rate);

		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		updateDateDisplay();
		updateTimeDisplay();
	}

	private void AddOnClickListener() {
		referenceQuestions_TextView.setOnClickListener(this);
		referenceBet_TextView.setOnClickListener(this);
		sure_TextView.setOnClickListener(this);
		date.setClickable(true);
		time.setClickable(true);
		date.setOnClickListener(this);
		time.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v == referenceQuestions_TextView) {
			MobclickAgent.onEvent(CreatMyGuessActivity.this, "room_peilv");
			TCAgent.onEvent(CreatMyGuessActivity.this, "room_peilv");

			openPop();
		} else if (v == sure_TextView) {
			Log.v("tag", ">>>   点击了完成  >>>>");

			if (referenceBet_EditText.getText().toString() != null
					&& !referenceBet_EditText.getText().toString().equals("")) {
				CPorgressDialog.showProgressDialog(CreatMyGuessActivity.this);
				doPullDate(false, "2045", new MCHttpCallBack() {
					@Override
					public void onSuccess(MCHttpResp resp) {
						super.onSuccess(resp);
						CPorgressDialog.hideProgressDialog();

						try {
							String resultCode = resp.getJson().getString(
									"result_code");
							if (resultCode.equals("0")) {
								MobclickAgent.onEvent(
										CreatMyGuessActivity.this,
										"room_pankou1");
								TCAgent.onEvent(CreatMyGuessActivity.this,
										"room_pankou1");

								Toast.makeText(CreatMyGuessActivity.this,
										"成功发起竞猜", Toast.LENGTH_SHORT).show();

								Intent intent = new Intent();
								// intent.setClass(CreatMyGuessActivity.this,
								// BragShareActivity.class);
								intent.putExtra("id",
										resp.getJson().getString("wager_id"));
								intent.putExtra("price", referenceBet_EditText
										.getText().toString());
								setResult(-1, intent);
								CreatMyGuessActivity.this.finish();
							} else {
								Toast.makeText(CreatMyGuessActivity.this,
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
					}

				});
			} else {
				Toast.makeText(this, "请输入保证金", Toast.LENGTH_SHORT).show();
			}

		} else if (v == date) {
			Message msg = new Message();
			if (date.equals((TextView) v)) {
				msg.what = CreatMyGuessActivity.SHOW_DATAPICK;
			}
			CreatMyGuessActivity.this.dateandtimeHandler.sendMessage(msg);
		} else if (v == time) {
			Message msg = new Message();
			if (time.equals((TextView) v)) {
				msg.what = CreatMyGuessActivity.SHOW_TIMEPICK;
			}
			CreatMyGuessActivity.this.dateandtimeHandler.sendMessage(msg);
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
		pair.add(new BasicNameValuePair("u_room_id", roomId));
		if (action.equals("2045")) {
			pair.add(new BasicNameValuePair("u_team1_win_rate",
					my_team1_win_rate_str));
			pair.add(new BasicNameValuePair("u_team2_win_rate",
					my_team2_win_rate_str));
			pair.add(new BasicNameValuePair("u_none_win_rate",
					my_none_win_rate_str));
			pair.add(new BasicNameValuePair("u_deposit", referenceBet_EditText
					.getText().toString()));
			pair.add(new BasicNameValuePair("u_end_time", getEndTime()));

		}
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
				.hideSoftInputFromWindow(CreatMyGuessActivity.this
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

		Button done = (Button) referenceView.findViewById(R.id.done);

		final EditText et_win1_rat = (EditText) referenceView
				.findViewById(R.id.et_win1_rat);
		final EditText et_win2_rat = (EditText) referenceView
				.findViewById(R.id.et_win2_rat);
		final EditText et_none_win_rat = (EditText) referenceView
				.findViewById(R.id.et_none_win_rat);
		et_win1_rat.setText(team1_win_rate_str);
		et_win2_rat.setText(team2_win_rate_str);
		et_none_win_rat.setText(none_win_rate_str);

		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String msg = chargeEditData(et_win1_rat.getText().toString(),
						et_win2_rat.getText().toString(), et_none_win_rat
								.getText().toString());
				if (msg != null) {
					Toast.makeText(CreatMyGuessActivity.this, msg,
							Toast.LENGTH_SHORT).show();
				} else {
					MobclickAgent.onEvent(CreatMyGuessActivity.this,
							"room_peilv2");
					TCAgent.onEvent(CreatMyGuessActivity.this,
							"room_peilv2");
					// team1_win_rate, team2_win_rate,
					// team_none_win_rate
					my_team1_win_rate_str = et_win1_rat.getText().toString();
					my_team2_win_rate_str = et_win2_rat.getText().toString();
					my_none_win_rate_str = et_none_win_rat.getText().toString();
					team1_win_rate.setText("主胜 " + my_team1_win_rate_str);
					team2_win_rate.setText("客胜 " + my_team2_win_rate_str);
					team_none_win_rate.setText("平 " + my_none_win_rate_str);
					referencePop.dismiss();
				}
			}
		});
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

	private String chargeEditData(String team1_rate, String team2_rate,
			String none_rate) {
		if (team1_rate == null || team1_rate.equals(""))
			return "主胜率不能为空";
		if (team2_rate == null || team2_rate.equals(""))
			return "客胜率不能为空";
		if (none_rate == null || none_rate.equals(""))
			return "平率不能为空";
		if (new Double(team1_rate) < new Double(team1_win_rate_str))
			return "主胜率不能小于默认主胜率";
		if (new Double(team2_rate) < new Double(team2_win_rate_str))
			return "客胜率不能小于默认客胜率";
		if (new Double(none_rate) < new Double(none_win_rate_str))
			return "平率不能小于默认平率";
		if (new Double(team1_rate) > 99.99)
			return "主胜率不能大于99.99";
		if (new Double(team2_rate) > 99.99)
			return "客胜率不能大于99.99";
		if (new Double(none_rate) > 99.99)
			return "平率不能大于99.99";
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

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
					true);
		}

		return null;
	}

	/**
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			updateDateDisplay();
		}
	};
	/**
	 * 时间控件事件
	 */
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;

			updateTimeDisplay();
		}
	};

	/**
	 * 更新时间显示
	 */
	private void updateTimeDisplay() {
		time.setText(new StringBuilder().append(mHour).append(":")
				.append((mMinute < 10) ? "0" + mMinute : mMinute));
	}

	/**
	 * 更新日期显示
	 */
	private void updateDateDisplay() {
		date.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
	}

	/**
	 * 处理日期和时间控件的Handler
	 */
	Handler dateandtimeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_DATAPICK:
				showDialog(DATE_DIALOG_ID);
				break;
			case SHOW_TIMEPICK:
				showDialog(TIME_DIALOG_ID);
				break;
			}
		}

	};
}
