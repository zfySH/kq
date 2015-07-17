package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.football.DownAndShowImageTask;

public class TeamFightScoreManagerAdapter extends BaseAdapter {
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private LayoutInflater inflater;
	private Activity context;

	@Override
	public int getCount() {
		return list.size();
	}

	public TeamFightScoreManagerAdapter(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public void setItem(List<Map<String, String>> list) {
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
	public View getView(int postion, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = inflater
					.inflate(R.layout.abc_fight_score_message_item, null);
			viewHolder = new ViewHolder(view);
			view.setTag(R.id.ViewHolder, viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag(R.id.ViewHolder);
		}
		viewHolder.name.setText(String.valueOf(list.get(postion)
				.get("nickname")));
		new DownAndShowImageTask(String.valueOf(list.get(postion)
				.get("faceimg")), viewHolder.icon).execute();
		viewHolder.goleEdit.setText(String.valueOf(list.get(postion).get(
				"count_jq")));
		viewHolder.goleEdit.setText(String.valueOf(list.get(postion).get(
				"count_zg")));
		return view;
	}

	class ViewHolder {
		ImageView icon;
		TextView name;
		EditText goleEdit;
		EditText assitEdit;

		public ViewHolder(View view) {
			icon = (ImageView) view.findViewById(R.id.player_icon);
			name = (TextView) view.findViewById(R.id.name);
			goleEdit = (EditText) view.findViewById(R.id.gole_edit);
			assitEdit = (EditText) view.findViewById(R.id.assit_edit);
		}
	}
}
