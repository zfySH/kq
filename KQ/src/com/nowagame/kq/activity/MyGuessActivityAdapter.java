package com.nowagame.kq.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.util.ImagerLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyGuessActivityAdapter extends BaseAdapter {

	private Activity context;
	private JSONArray array = new JSONArray();
	ImagerLoader loader = new ImagerLoader();

	public MyGuessActivityAdapter(Activity context) {
		this.context = context;
	}

	public void setItem(JSONArray array) {
		if (array == null)
			return;

		this.array = array;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return array.length() == 0 ? 0 : array.length();
	}

	@Override
	public Object getItem(int position) {
		try {
			return array.length() == 0 ? null : array.getJSONObject(position);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		Holder holder;
		if (view == null) {

			holder = new Holder();
			view = context.getLayoutInflater().inflate(
					R.layout.abc_activity_myguess_item, null);

			holder.team_icon1 = (CircleImageView) view
					.findViewById(R.id.team_icon1);
			holder.team_icon2 = (CircleImageView) view
					.findViewById(R.id.team_icon2);
			holder.team_name1 = (TextView) view.findViewById(R.id.team_name1);
			holder.team_name2 = (TextView) view.findViewById(R.id.team_name2);
			holder.team_answer = (TextView) view.findViewById(R.id.team_answer);
			holder.match_name_time = (TextView) view
					.findViewById(R.id.match_name_time);

			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		try {
			JSONObject record = array.getJSONObject(position);
			JSONObject match = record.getJSONObject("match");
			JSONObject team1 = match.getJSONObject("team1");
			JSONObject team2 = match.getJSONObject("team2");

			loader.LoadImage(team1.getString("icon"), holder.team_icon1);
			loader.LoadImage(team2.getString("icon"), holder.team_icon2);

			// new DownAndShowImageTask(team1.getString("icon"),
			// holder.team_icon1)
			// .execute();
			// new DownAndShowImageTask(team2.getString("icon"),
			// holder.team_icon2)
			// .execute();
			holder.team_name1.setText(team1.getString("name"));
			holder.team_name2.setText(team2.getString("name"));
			holder.match_name_time.setText(match.getString("name") + "  "
					+ match.getString("time"));
			if (record.getString("state").equals("0")) {// 未开奖
				holder.team_answer.setText("未开奖");
				holder.team_answer.setTextColor(context.getResources()
						.getColor(R.color.text_color));
			} else if (record.getString("state").equals("1")) {// 未中奖
				holder.team_answer.setText("未中奖");
				holder.team_answer.setTextColor(context.getResources()
						.getColor(R.color.text_color));

			} else if (record.getString("state").equals("2")) {// 已中奖
				holder.team_answer.setText("已中奖");
				holder.team_answer.setTextColor(context.getResources()
						.getColor(R.color.orange));

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;
	}

	static class Holder {
		CircleImageView team_icon1, team_icon2;
		TextView team_name1, team_name2, team_answer, match_name_time;
	}
}
