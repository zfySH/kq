package cn.kangeqiu.kq.activity.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.football.CathecticActivity;
import com.nowagme.football.VoteActivity;

public class BaseView {

	private Activity context;
	private LayoutInflater inflater;
	Button btn_quiz;
	TextView text_content, txt_count;
	LinearLayout rel_type;
	String type;

	public BaseView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(final JSONObject activitys) throws JSONException {
		View view = inflater.inflate(R.layout.abc_activity_activitys, null);
		btn_quiz = (Button) view.findViewById(R.id.btn_quiz);
		text_content = (TextView) view.findViewById(R.id.text_content);
		txt_count = (TextView) view.findViewById(R.id.text_num);
		rel_type = (LinearLayout) view.findViewById(R.id.rel_type);
		type = activitys.getString("type");
		btn_quiz.setText(type.equals("0") ? "竞猜" : "投票");
		btn_quiz.setBackgroundResource(type.equals("0") ? R.drawable.abc_button_roundcorner_light_orange
				: R.drawable.abc_button_roundcorner_toupiao);
		text_content.setText(activitys.getString("title"));
		txt_count.setText(activitys.getString("join_count") + "人参与");
		final String j_recordId = activitys.getString("id");
		rel_type.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (type.equals("0")) {
					Intent intent = new Intent();
					intent.setClass(context, CathecticActivity.class);
					intent.putExtra("id", j_recordId);
					context.startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(context, VoteActivity.class);
					intent.putExtra("id", j_recordId);
					context.startActivity(intent);
				}

			}
		});
		return view;
	}

}
