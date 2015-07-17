package cn.kangeqiu.kq.activity.view;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.football.TeamActivityActivity;

public class TeamActivityView {
	private Activity context;
	private LayoutInflater inflater;
	private TextView name, address;
	private TextView icon, time;
	private LinearLayout main;
	private Button wait_response;

	public TeamActivityView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(final Map<String, String> msg, String teamName) {
		View view = inflater.inflate(R.layout.abc_team_activity_item, null);
		name = (TextView) view
				.findViewById(R.id.abc_fragment_nearby_listview__tv_name);
		address = (TextView) view.findViewById(R.id.abc_tv_address);
		icon = (TextView) view.findViewById(R.id.abc_tl_logo);
		time = (TextView) view.findViewById(R.id.abc_tv_time);
		main = (LinearLayout) view.findViewById(R.id.main);
		wait_response = (Button) view
				.findViewById(R.id.abc_fragment_nearby__listview__btn_activity_wait_response);

		// name.setText(msg.get("nickname"));
		// address.setText(msg.get("signature"));
		time.setText(msg.get("time"));
		address.setText(msg.get("addr"));
		int kind = Integer.parseInt(msg.get("kind"));
		if (kind == 0) {// 挑战
			icon.setText("战");
			name.setText(msg.get("team1_name") + "-" + msg.get("team2_name"));
		} else if (kind == 1) {// 约站
			icon.setText("约");
			name.setText(teamName + "  VS  ");
			wait_response.setVisibility(View.VISIBLE);
		} else if (kind == 2) {// 训练
			icon.setText("训");
			name.setText(msg.get("title"));
		}

		main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setClass(context, TeamActivityActivity.class);
				bundle.putInt("match_id", Integer.parseInt(msg.get("id")));
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		return view;
	}
}
