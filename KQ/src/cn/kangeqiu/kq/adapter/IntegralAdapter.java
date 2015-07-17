package cn.kangeqiu.kq.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.util.ImagerLoader;

public class IntegralAdapter extends BaseAdapter {

	private Activity context;
	private JSONArray array = new JSONArray();
	ImagerLoader loader = new ImagerLoader();

	public IntegralAdapter(Activity context) {
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
					R.layout.integral_view_item, null);

			holder.team_icon = (ImageView) view.findViewById(R.id.team_icon);
			holder.txt_num = (TextView) view.findViewById(R.id.txt_num);
			holder.txt_team = (TextView) view.findViewById(R.id.txt_team);
			holder.txt_integral = (TextView) view
					.findViewById(R.id.txt_integral);
			holder.txt_out = (TextView) view.findViewById(R.id.txt_out);
			holder.txt_fu = (TextView) view.findViewById(R.id.txt_fu);
			holder.txt_ping = (TextView) view.findViewById(R.id.txt_ping);
			holder.txt_sheng = (TextView) view.findViewById(R.id.txt_sheng);
			holder.txt_sai = (TextView) view.findViewById(R.id.txt_sai);
			holder.rel = (LinearLayout) view.findViewById(R.id.rel);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		try {
			holder.txt_team.setText(array.getJSONObject(position).getString(
					"team_name"));
			holder.txt_integral.setText(array.getJSONObject(position)
					.getString("score"));
			holder.txt_out.setText(array.getJSONObject(position).getString(
					"in_ball_count")
					+ "/"
					+ array.getJSONObject(position)
							.getString("lost_ball_count"));
			holder.txt_fu.setText(array.getJSONObject(position).getString(
					"lose_count"));
			holder.txt_ping.setText(array.getJSONObject(position).getString(
					"equal_count"));
			holder.txt_sheng.setText(array.getJSONObject(position).getString(
					"win_count"));
			holder.txt_sai.setText(array.getJSONObject(position).getString(
					"team_count"));
			holder.txt_num.setText(array.getJSONObject(position).getString(
					"order_number"));
			loader.LoadImage(array.getJSONObject(position).getString(
					"team_icon"), holder.team_icon);
			if (Integer.parseInt(array.getJSONObject(position).getString(
					"order_number"))  <=3) {
				holder.rel.setBackgroundColor(Color.parseColor("#44CDD7"));
			}else {
				holder.rel.setBackgroundColor(Color.parseColor("#8E8D8B"));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;
	}

	static class Holder {
		ImageView team_icon;
		TextView txt_team, txt_integral, txt_out, txt_fu, txt_ping, txt_sheng,
				txt_sai, txt_num;
		LinearLayout rel;
	}
}
