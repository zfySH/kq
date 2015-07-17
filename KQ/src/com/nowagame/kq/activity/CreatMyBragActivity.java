package com.nowagame.kq.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;

public class CreatMyBragActivity extends AgentActivity implements
		OnClickListener {

	// private Button goBack_Btn;
	private TextView referenceQuestions_TextView, referenceBet_TextView, date,
			time;
	private PopupWindow referencePop;
	private View referenceView;
	private ListView questions_ListView;
	private CreatMyBragActivityReferenceAdapter adapter;
	// private ArrayList array;
	private EditText referenceQuestions_EditText, referenceBet_EditText;
	private RelativeLayout rl_main;
	// private boolean isQFalg = true;
	private int type;
	private Button sure_TextView;
	private JSONArray records = new JSONArray();
	LinearLayout lay_time;
	private static final int SHOW_DATAPICK = 0;
	private static final int DATE_DIALOG_ID = 1;
	private static final int SHOW_TIMEPICK = 2;
	private static final int TIME_DIALOG_ID = 3;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;
	private String roomId = "",dateTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_creatbrag);

		init();
		AddOnClickListener();

	}

	public void back(View view) {
		finish();
	}

	private void init() {
		roomId = getIntent().getStringExtra("roomId");

		rl_main = (RelativeLayout) findViewById(R.id.rl_main);

		referenceQuestions_TextView = (TextView) findViewById(R.id.referenceQuestions_TextView);
		referenceQuestions_EditText = (EditText) findViewById(R.id.referenceQuestions_EditText);

		referenceBet_TextView = (TextView) findViewById(R.id.referenceBet_TextView);
		referenceBet_EditText = (EditText) findViewById(R.id.referenceBet_EditText);
		lay_time = (LinearLayout) findViewById(R.id.lay_time);
		sure_TextView = (Button) findViewById(R.id.sure_TextView);

		date = (TextView) findViewById(R.id.tx_date);
		time = (TextView) findViewById(R.id.tx_time);

		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		mHour = c.get(Calendar.HOUR_OF_DAY);
		c.set(Calendar.DAY_OF_MONTH, mDay + 1);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mMinute = c.get(Calendar.MINUTE);
		Date d = new Date(mYear - 1900, mMonth, mDay, mHour, mMinute);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateTime = format.format(d);
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
			// isQFalg = true;
			type = 0;
			openPop();
		} else if (v == referenceBet_TextView) {
			// isQFalg = false;
			type = 1;
			openPop();
		} else if (v == sure_TextView) {
			type = 2;

			if (4 <= referenceQuestions_EditText.getText().toString().length()
					&& referenceQuestions_EditText.getText().toString()
							.length() <= 30
					&& referenceBet_EditText.getText().toString().length() >= 4
					&& referenceBet_EditText.getText().toString().length() <= 30) {
				doPullDate(false, "2039", new MCHttpCallBack() {
					@Override
					public void onSuccess(MCHttpResp resp) {
						super.onSuccess(resp);
						try {
							String resultCode = resp.getJson().getString(
									"result_code");
							Date d = new Date(mYear - 1900, mMonth, mDay, mHour, mMinute);
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							dateTime = format.format(d);
							Date date = null;
							date = format.parse(dateTime);
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							Calendar c = Calendar.getInstance();
							Log.e("tag", "calendar.after(c.getTime())"+calendar.after(c.getTime()));
							Log.e("tag", "calendar"+calendar.getTime());
							Log.e("tag", "calendar.after)"+c.getTime());
							if (calendar.getTime().after(c.getTime())==true) {
								if (resultCode.equals("0")) {
									Toast.makeText(CreatMyBragActivity.this,
											"成功发起吹牛", Toast.LENGTH_SHORT).show();
									Intent intent = new Intent();
									// intent.setClass(CreatMyGuessActivity.this,
									// BragShareActivity.class);
									intent.putExtra("id",
											resp.getJson().getString("brag_id"));

									setResult(-1, intent);
									CreatMyBragActivity.this.finish();

									// CreatMyBragActivity.this.finish();
									// Intent intent = new Intent();
									// intent.setClass(CreatMyBragActivity.this,
									// BragShareActivity.class);
									// intent.putExtra("id",
									// resp.getJson().getString("brag_id"));
									// CreatMyBragActivity.this.startActivity(intent);
								} else {
									Toast.makeText(CreatMyBragActivity.this,
											resp.getJson().getString("message"),
											Toast.LENGTH_SHORT).show();
								}
							}else {
								Toast.makeText(CreatMyBragActivity.this, "请至少选择大于当前的时间哦~", Toast.LENGTH_LONG).show();
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
				Toast.makeText(this, "问题或赌注长度不符合要求", Toast.LENGTH_SHORT).show();
			}

		} else if (v == date) {
			Message msg = new Message();
			if (date.equals((TextView) v)) {
				msg.what = CreatMyBragActivity.SHOW_DATAPICK;
			}
			CreatMyBragActivity.this.dateandtimeHandler.sendMessage(msg);
		} else if (v == time) {
			Message msg = new Message();
			if (time.equals((TextView) v)) {
				msg.what = CreatMyBragActivity.SHOW_TIMEPICK;
			}
			CreatMyBragActivity.this.dateandtimeHandler.sendMessage(msg);
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
		if (type == 2) {
			pair.add(new BasicNameValuePair("u_room_id", roomId));
			pair.add(new BasicNameValuePair("u_question",
					referenceQuestions_EditText.getText().toString()));
			pair.add(new BasicNameValuePair("u_bet", referenceBet_EditText
					.getText().toString()));
				pair.add(new BasicNameValuePair("u_end_time", getEndTime()));
			
		} else if (type == 0) {
			pair.add(new BasicNameValuePair("u_type", "0"));
		} else if (type == 1) {
			pair.add(new BasicNameValuePair("u_type", "1"));
		}

		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}
	private String getEndTime() {

		return dateTime;
	}

	private void openPop() {
		// 关闭键盘
		((InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(CreatMyBragActivity.this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		referencePop = new PopupWindow(this);
		referenceView = getLayoutInflater().inflate(
				R.layout.abc_activity_creatbrag_referencepop, null);
		referencePop.setBackgroundDrawable(new ColorDrawable(0));
		referencePop.setWidth(LayoutParams.MATCH_PARENT);
		referencePop.setHeight(LayoutParams.WRAP_CONTENT);
		referencePop.setBackgroundDrawable(new BitmapDrawable());
		referencePop.setFocusable(true);
		referencePop.setOutsideTouchable(true);
		referencePop.setContentView(referenceView);

		questions_ListView = (ListView) referenceView
				.findViewById(R.id.questions_ListView);
		// listView 数据源
		initreference();

		adapter = new CreatMyBragActivityReferenceAdapter(this, records);
		questions_ListView.setAdapter(adapter);
		questions_ListView.setOnItemClickListener(new OnItemClickListener());
		if (referencePop.isShowing()) {
			referencePop.dismiss();
		} else {
			// View view = getLayoutInflater().inflate(
			// R.layout.abc_activity_creatbrag, null);
			// referencePop.showAtLocation(, Gravity.CENTER, 0, 0);
			referencePop.showAtLocation(rl_main, Gravity.CENTER, 0, 0);
		}
	}

	class OnItemClickListener implements
			android.widget.AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int option,
				long arg3) {
			try {
				if (type == 0) {

					referenceQuestions_EditText
							.setText(records.getJSONObject(option)
									.getString("name").toString());

				} else if (type == 1) {
					referenceBet_EditText.setText(records.getJSONObject(option)
							.getString("name").toString());
				}
				referencePop.dismiss();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void doPullDate() {
		doPullDate(true, "2040", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						records = resp.getJson().getJSONArray("records");
						adapter.setItem(records);

					} else {
						Toast.makeText(CreatMyBragActivity.this,
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
				Toast.makeText(CreatMyBragActivity.this,
						resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}

		});

	}

	private long chooseTime;

	// 加载参考数据
	private void initreference() {
		/**
		 * isQFalg true（参考问题） or false（参考赌注）. 默认ture
		 */
		doPullDate();
	}

	private boolean canChoose() {
		// 1800000三十分钟
		return (System.currentTimeMillis() + 1800000) < chooseTime ? true
				: false;
	}

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
		date.setText(new StringBuilder().append(mYear).append("年")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("月").append((mDay < 10) ? "0" + mDay : mDay)
				.append("日"));
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
