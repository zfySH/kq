package cn.kangeqiu.kq.activity.view;

import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.util.ImagerLoader;

public class ShooterView {
	private Activity context;
	private LayoutInflater inflater;
	ImagerLoader loader = new ImagerLoader();

	public ShooterView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(JSONObject json) {
		View view = inflater.inflate(R.layout.shooter_item, null);
		TextView txt_team = (TextView) view.findViewById(R.id.txt_team);
		TextView txt_player = (TextView) view.findViewById(R.id.txt_player);
		TextView txt_goal = (TextView) view.findViewById(R.id.txt_goal);
		TextView txt_num = (TextView) view.findViewById(R.id.txt_num);
		try {
			txt_team.setText(json.getString("team_name"));
			txt_player.setText(json.getString("player_name"));
			txt_goal.setText(json.getString("in_ball_count"));
			txt_num.setText(json.getString("order_number"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;
	}
}
