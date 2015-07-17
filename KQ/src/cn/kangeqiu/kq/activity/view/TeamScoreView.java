package cn.kangeqiu.kq.activity.view;

import org.json.JSONObject;

import com.nowagme.util.ImagerLoader;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

public class TeamScoreView {
	private Activity context;
	private LayoutInflater inflater;
	ImagerLoader loader = new ImagerLoader();

	public TeamScoreView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(JSONObject json) {
		View view = inflater.inflate(R.layout.integral_view_item, null);
		ImageView team_icon = (ImageView) view.findViewById(R.id.team_icon);
		TextView txt_num = (TextView) view.findViewById(R.id.txt_num);
		TextView txt_team = (TextView) view.findViewById(R.id.txt_team);
		TextView txt_integral = (TextView) view.findViewById(R.id.txt_integral);
		TextView txt_out = (TextView) view.findViewById(R.id.txt_out);
		TextView txt_fu = (TextView) view.findViewById(R.id.txt_fu);
		TextView txt_ping = (TextView) view.findViewById(R.id.txt_ping);
		TextView txt_sheng = (TextView) view.findViewById(R.id.txt_sheng);
		TextView txt_sai = (TextView) view.findViewById(R.id.txt_sai);
		LinearLayout rel = (LinearLayout) view.findViewById(R.id.rel);
		try {
			txt_team.setText(json.getString("team_name"));
			txt_integral.setText(json.getString("score"));
			txt_out.setText(json.getString("in_ball_count") + "/"
					+ json.getString("lost_ball_count"));
			txt_fu.setText(json.getString("lose_count"));
			txt_ping.setText(json.getString("equal_count"));
			txt_sheng.setText(json.getString("win_count"));
			txt_sai.setText(json.getString("team_count"));
			txt_num.setText(json.getString("order_number"));
			loader.LoadImage(json.getString("team_icon"), team_icon);
			if (Integer.parseInt(json.getString("order_number")) <= 3) {
				rel.setBackgroundColor(Color.parseColor("#44CDD7"));
			} else {
				rel.setBackgroundColor(Color.parseColor("#8E8D8B"));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;
	}
}
