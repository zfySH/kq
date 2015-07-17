package com.nowagame.kq.activity;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

public class CreatMyBragActivityReferenceAdapter extends BaseAdapter {

	private Activity context;
	private JSONArray array = new JSONArray();
//	private ArrayList array;

	public CreatMyBragActivityReferenceAdapter(Activity context, JSONArray array) {
		this.context = context;
		this.array = array;
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
	public void setItem(JSONArray array) {
		if (array == null)
			return;
		this.array = array;
		notifyDataSetChanged();

	}
	
	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		Holder holder;
		if (view == null) {

			holder = new Holder();
			view = context.getLayoutInflater().inflate(
					R.layout.abc_activity_creatbrag_referencepop_item, null);

			holder.reference_TextView = (TextView) view
					.findViewById(R.id.reference_TextView);

			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		try {
			holder.reference_TextView.setText(array.getJSONObject(position).getString("name").toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;
	}

	static class Holder {
		TextView reference_TextView;
	}
}
