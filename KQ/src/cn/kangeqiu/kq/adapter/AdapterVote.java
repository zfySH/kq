package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

@SuppressLint("UseSparseArrays")
public class AdapterVote extends BaseAdapter {
	private Context mContext;
	private List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
	private List<Boolean> list = new ArrayList<Boolean>();
	private boolean falg = false;
	

	public AdapterVote(Context mContext) {
		this.mContext = mContext;
	}

	public void SetItme(List<Map<String, Object>> options) {
		this.options = options;
		for (int i = 0; i < options.size(); i++) {
			list.add(false);
		}
		notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		return options == null ? 0 : options.size();
	}

	@Override
	public Object getItem(int arg0) {
		return options.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public String getOptionsid() {
		String str = "";
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i)) {
				str+=","+options.get(i).get("id").toString();
			}
		}
		
		return str.substring(1);

	}

	public void setboolean(int position) {
		list.clear();
		for (int i = 0; i < options.size(); i++) {
			list.add(false);
		}
		list.set(position, true);
		notifyDataSetChanged();
	}
	
	public void setboolean1(int position,int count) {
		int num=0;
		if (list.get(position)) {
			list.set(position, false);
		}else {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i)) {
					num++;
				}
			}
			if (num<count) {
				list.set(position, true);
			}
		}
			
	
		
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final OneHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.vote_item1, null);
			holder = new OneHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.txt_meeting = (TextView) convertView
					.findViewById(R.id.txt_meeting);
			holder.txt_meetingnum = (TextView) convertView
					.findViewById(R.id.txt_meetingnum);
			holder.img_check = (ImageView) convertView
					.findViewById(R.id.img_check);
			convertView.setTag(holder);
		} else {
			holder = (OneHolder) convertView.getTag();
		}

		if (list.get(position)) {
			holder.img_check.setImageResource(R.drawable.img_check);
			falg = false;
		} else {
			holder.img_check.setImageResource(R.drawable.img_notcheck);
			falg = true;
		}

		holder.name.setText(options.get(position).get("name").toString());
		holder.txt_meeting
				.setText(options.get(position).get("name").toString());
		holder.txt_meetingnum.setText(options.get(position).get("count")
				.toString());

		return convertView;
	}

	class OneHolder {
		private TextView name, txt_meeting, txt_meetingnum;

		private ImageView img_check;

	}

}
