package cn.kangeqiu.kq.activity.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.football.CathecticActivity;
import com.nowagme.football.VoteActivity;

public class MatchActivityView {
	private Activity context;
	private LayoutInflater inflater;
	private static final int TYPE_ONE = 0;
	private static final int TYPE_TWO = 1;
	private static final int TYPE_THREE = 2;

	public MatchActivityView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(final JSONObject matchs) throws JSONException {
		View view = null;
		view = inflater.inflate(R.layout.abc_match_activity_item, null);
		TextView name = (TextView) view.findViewById(R.id.tx_name);
		TextView counts = (TextView) view.findViewById(R.id.tx_counts);
		TextView type = (TextView) view.findViewById(R.id.type);
		TextView statue = (TextView) view.findViewById(R.id.status);
		LinearLayout ll_content = (LinearLayout) view
				.findViewById(R.id.ll_content);
		// View arron = (View) view.findViewById(R.id.match_arron);

		name.setText(matchs.getString("title"));
		counts.setText("已有" + matchs.getString("join_count") + "人参加");
		type.setText(matchs.getString("type").equals("0") ? "" : "投票");
		type.setBackgroundResource(matchs.getString("type").equals("0") ? R.drawable.img_quiz
				: R.drawable.abc_button_roundcorner_toupiao);

		statue.setText(getStatus(matchs.getString("state")));
		ll_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					if (matchs.getString("type").equals("0")) {
						Intent intent = new Intent();
						intent.putExtra("id", matchs.getString("id"));
						intent.setClass(context, CathecticActivity.class);
						context.startActivity(intent);
					} else {
						Intent intent = new Intent();
						intent.putExtra("id", matchs.getString("id"));
						intent.setClass(context, VoteActivity.class);
						context.startActivity(intent);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		return view;
	}

	private String getStatus(String status) {
		if (status.equals("0"))
			return "未开始";
		else if (status.equals("1"))
			return "进行中";
		else if (status.equals("2"))
			return "已结束";
		return "";
	}
}
