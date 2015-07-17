package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

public class AdapterQuiz extends BaseAdapter {
	private Context mContext;
	String[] temp;
	List<Boolean> list = new ArrayList<Boolean>();

	public AdapterQuiz(Context mContext) {
		this.mContext = mContext;

	}

	public void SetItme(String[] temp) {
		this.temp = temp;
		for (int i = 0; i < temp.length; i++) {
			list.add(false);
		}
		notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		return temp.length;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setboolean(int position) {
		list.clear();
		for (int i = 0; i < temp.length; i++) {
			list.add(false);
		}
		list.set(position, true);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final OneHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.quiz_item1, null);
			holder = new OneHolder();
			holder.btn_fifty = (TextView) convertView
					.findViewById(R.id.btn_fifty);
			convertView.setTag(holder);
		} else {
			holder = (OneHolder) convertView.getTag();
		}

		holder.btn_fifty.setText(temp[position]);
		
		
		if (list.get(position)) {
			holder.btn_fifty.setTextColor(Color.parseColor("#ffffff"));
			holder.btn_fifty.setBackgroundColor(Color.parseColor("#FF9A42"));
		} else {
			holder.btn_fifty.setTextColor(Color.parseColor("#000000"));
			holder.btn_fifty.setBackgroundColor(Color.parseColor("#ffffff"));
		}
		return convertView;
	}

	class OneHolder {
		private TextView btn_fifty;

	}

}
