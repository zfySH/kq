package cn.kangeqiu.kq.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

public class ShooterAdapter extends BaseAdapter {

	private Activity context;
	private JSONArray array = new JSONArray();

	public ShooterAdapter(Activity context) {
		this.context = context;
		notifyDataSetChanged();
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
			return array.length() == 0 ? null : array.get(position);
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
					R.layout.shooter_item, null);
			holder.txt_team = (TextView) view
					.findViewById(R.id.txt_team);
			holder.txt_player = (TextView) view
					.findViewById(R.id.txt_player);
			holder.txt_goal = (TextView) view
					.findViewById(R.id.txt_goal);
			holder.txt_num = (TextView) view
					.findViewById(R.id.txt_num);

			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		try {
			holder.txt_team.setText(array.getJSONObject(position).getString("team_name"));
			holder.txt_player.setText(array.getJSONObject(position).getString("player_name"));
			holder.txt_goal.setText(array.getJSONObject(position).getString("in_ball_count"));
			holder.txt_num.setText(array.getJSONObject(position).getString("order_number"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;
	}

	static class Holder {
		TextView txt_player, txt_goal, txt_team,txt_num;
	}
}
