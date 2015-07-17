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

public class MyselfBallAgeView {
	private Activity context;
	private LayoutInflater inflater;
	WheelMain wheelMain;

	public MyselfBallAgeView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(String msg) {
		View view = inflater.inflate(R.layout.abc_myself_message_ballage_item,
				null);
		ScreenInfo screenInfo = new ScreenInfo(context);
		wheelMain = new WheelMain(view);
		wheelMain.screenheight = screenInfo.getHeight();
		wheelMain.setSTART_YEAR(0);
		wheelMain.setEND_YEAR(20);
		// DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		if (JudgeDate.isDate(msg, "yyyyMMdd")) {
			try {
				calendar.setTime(format.parse(msg));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				String date = (calendar.get(Calendar.YEAR) - 25) + "0101";
				calendar.setTime(format.parse(date));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		wheelMain.initDateTimePicker(Integer.parseInt(msg), month, day);

		return view;
	}

	public String getYear() {
		return wheelMain.getYear();
	}
}
