package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

public class MatchActivityAdapter extends BaseAdapter {
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private LayoutInflater inflater;
	private Activity context;

	@Override
	public int getCount() {
		return list.size();
	}

	public MatchActivityAdapter(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public void setItem(List<Map<String, Object>> list) {
		if (list == null)
			return;

		this.list = list;

		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int postion, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = inflater.inflate(R.layout.abc_match_activity_item, null);
			viewHolder = new ViewHolder(view);
			view.setTag(R.id.ViewHolder, viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag(R.id.ViewHolder);
		}
		viewHolder.name.setText(String.valueOf(list.get(postion).get("title")));
		viewHolder.counts.setText("已有"
				+ list.get(postion).get("join_count").toString() + "人参加");
		viewHolder.type.setText(list.get(postion).get("type").toString()
				.equals("0") ? "竞猜" : "投票");
		viewHolder.type.setBackgroundResource(list.get(postion).get("type").toString()
						.equals("0") ? R.drawable.abc_button_roundcorner_jingcai
						: R.drawable.abc_button_roundcorner_toupiao);
		viewHolder.statue.setText(getStatus(list.get(postion).get("state")
				.toString()));
		return view;
	}

	private String getStatus(String status) {
		if (status.equals("0"))
			return "未开始";
		else if (status.equals("1"))
			return "进行中";
		else if (status.equals("2"))
			return "已结束";
		return "";
	}

	class ViewHolder {
		TextView name;
		TextView counts;
		TextView type;
		TextView statue;

		public ViewHolder(View view) {
			name = (TextView) view.findViewById(R.id.tx_name);
			counts = (TextView) view.findViewById(R.id.tx_counts);
			type = (TextView) view.findViewById(R.id.type);
			statue = (TextView) view.findViewById(R.id.status);
		}
	}
}
