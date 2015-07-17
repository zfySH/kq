package com.nowagame.kq.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.kangeqiu.kq.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyHouseActivityAdapter extends BaseAdapter {

	private Activity context;
	private ArrayList array;

	public MyHouseActivityAdapter(Activity context, ArrayList array) {
		this.context = context;
		this.array = array;
	}

	@Override
	public int getCount() {
		return array.size() == 0 ? 0 : array.size();
	}

	@Override
	public Object getItem(int position) {
		return array.size() == 0 ? null : array.get(position);
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
					R.layout.abc_activity_myhouse_item, null);

			holder.teamOneLogo_DHCC = (CircleImageView) view.findViewById(R.id.teamOneLogo_DHCC);
			holder.teamTwoLogo_DHCC = (CircleImageView) view.findViewById(R.id.teamTwoLogo_DHCC);
			
			holder.state_textView = (TextView) view.findViewById(R.id.state_textView);
			holder.houseName_textView = (TextView) view.findViewById(R.id.houseName_textView);
			holder.createPerson_textView = (TextView) view.findViewById(R.id.createPerson_textView);
			holder.teamOneName_textView = (TextView) view.findViewById(R.id.teamOneName_textView);
			holder.teamTwoName_textView = (TextView) view.findViewById(R.id.teamTwoName_textView);
			holder.score_textView = (TextView) view.findViewById(R.id.score_textView);
			holder.tv_textView = (TextView) view.findViewById(R.id.tv_textView);
			
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		return view;
	}

	static class Holder {
		de.hdodenhof.circleimageview.CircleImageView teamOneLogo_DHCC,
				teamTwoLogo_DHCC;
		TextView state_textView, houseName_textView, createPerson_textView,
				teamOneName_textView, teamTwoName_textView, score_textView,
				tv_textView;
	}
}
