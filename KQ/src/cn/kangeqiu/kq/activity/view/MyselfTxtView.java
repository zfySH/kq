package cn.kangeqiu.kq.activity.view;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.MyselfMessageDetailActivity;

public class MyselfTxtView {
	private Activity context;
	private LayoutInflater inflater;

	public MyselfTxtView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(String msg) {
		View view = inflater
				.inflate(R.layout.abc_myself_message_txt_item, null);
		final EditText txt = (EditText) view.findViewById(R.id.txt);
		txt.setText(msg);

		txt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				((MyselfMessageDetailActivity) context).commitContent(txt
						.getText().toString());
			}
		});
		return view;
	}
}
