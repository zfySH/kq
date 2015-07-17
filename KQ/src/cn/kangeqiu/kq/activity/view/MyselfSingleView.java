package cn.kangeqiu.kq.activity.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import cn.kangeqiu.kq.R;

public class MyselfSingleView {
	private Activity context;
	private LayoutInflater inflater;
	private RadioButton btnMan;
	private RadioButton btnWoman;

	public MyselfSingleView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(String msg) {
		View view = inflater.inflate(R.layout.abc_myself_message_singer_item,
				null);
		btnMan = (RadioButton) view.findViewById(R.id.man);
		btnWoman = (RadioButton) view.findViewById(R.id.woman);
		if (msg.equals("男"))
			btnMan.setChecked(true);
		else if (msg.equals("女"))
			btnWoman.setChecked(true);
		else {
			btnMan.setChecked(false);
			btnWoman.setChecked(false);
		}
		return view;
	}

	public String getSex() {
		if (btnMan.isChecked())
			return "1";
		else if (btnWoman.isChecked())
			return "2";
		else
			return "0";
	}
}
