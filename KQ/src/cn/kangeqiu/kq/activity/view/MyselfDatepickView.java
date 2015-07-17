package cn.kangeqiu.kq.activity.view;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.wheelview.JudgeDate;
import cn.kangeqiu.kq.activity.view.wheelview.ScreenInfo;
import cn.kangeqiu.kq.activity.view.wheelview.WheelMain;

public class MyselfDatepickView {
	private Activity context;
	private LayoutInflater inflater;
	WheelMain wheelMain;

	public MyselfDatepickView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(String msg) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View timepickerview = inflater.inflate(
				R.layout.abc_myself_message_timepicker_item, null);
		ScreenInfo screenInfo = new ScreenInfo(context);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		wheelMain.setSTART_YEAR(1940);
		wheelMain.setEND_YEAR(2015);
		// DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat format2 = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		if (JudgeDate.isDate(msg, "yyyy-MM-dd")) {
			try {
				calendar.setTime(format1.parse(msg));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (JudgeDate.isDate(msg, "yyyyMMdd")) {
			try {
				calendar.setTime(format2.parse(msg));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

			try {
				String date = (calendar.get(Calendar.YEAR) - 25) + "0101";
				calendar.setTime(format2.parse(date));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		wheelMain.initDateTimePicker(year, month, day);
		return timepickerview;
	}

	public String getDate() {
		return wheelMain.getTime();
	}

	public String getYear() {
		return wheelMain.getYear();
	}

	public String getMonth() {
		return wheelMain.getMonth();
	}

	public String getDay() {
		return wheelMain.getDay();
	}
}
