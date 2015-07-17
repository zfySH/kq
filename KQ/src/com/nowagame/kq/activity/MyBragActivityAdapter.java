package com.nowagame.kq.activity;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.football.DownAndShowImageTask;
import com.nowagme.util.ImagerLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyBragActivityAdapter extends BaseAdapter {

	private Activity context;
	private JSONArray array = new JSONArray();
	String action="";
	private ImagerLoader loader = new ImagerLoader();

	public MyBragActivityAdapter(Activity context) {
		this.context = context;
	}

	public void setItem(JSONArray array,String action) {
		if (array == null)
			return;

		this.array = array;
		this.action=action;
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
					R.layout.abc_activity_mybrag_item, null);

			holder.head_DHCC = (ImageView) view
					.findViewById(R.id.head_DHCC);

			holder.name_textView = (TextView) view
					.findViewById(R.id.name_textView);
			holder.gamble_textView = (TextView) view
					.findViewById(R.id.gamble_textView);
			holder.bet_textView = (TextView) view
					.findViewById(R.id.bet_textView);
			holder.time_textView = (TextView) view
					.findViewById(R.id.time_textView);
			holder.rel = (RelativeLayout) view
					.findViewById(R.id.rel);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		try {
			if (action.equals("2043")) {
				holder.rel.setVisibility(View.VISIBLE);
				loader.LoadImage(array.getJSONObject(position).getString("user_icon"),holder.head_DHCC);
				
				holder.gamble_textView.setText(array.getJSONObject(position)
						.getString("question").toString());
			}
			
			holder.bet_textView.setText(array.getJSONObject(position)
					.getString("bet").toString());
			holder.time_textView.setText(array.getJSONObject(position)
					.getString("end_time").toString());
			holder.name_textView.setText(array.getJSONObject(position)
					.getString("user_name").toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;
	}

	static class Holder {
		RelativeLayout rel;
		ImageView head_DHCC;
		TextView name_textView, gamble_textView, bet_textView, time_textView;
	}
}
