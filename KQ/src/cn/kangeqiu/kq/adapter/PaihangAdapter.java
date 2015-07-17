package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.util.ImagerLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class PaihangAdapter extends BaseAdapter {
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private LayoutInflater inflater;
	private Activity context;
	private ImagerLoader loader = new ImagerLoader();

	@Override
	public int getCount() {
		return list.size();
	}

	public PaihangAdapter(Activity context) {
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
	public View getView(int postion, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = inflater.inflate(R.layout.abc_match_panghang_item, null);
			viewHolder = new ViewHolder(view);
			view.setTag(R.id.ViewHolder, viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag(R.id.ViewHolder);
		}
		viewHolder.name.setText(String.valueOf(list.get(postion)
				.get("nickname")));
		loader.LoadImage(String.valueOf(list.get(postion).get("icon")), viewHolder.icon);
		// new
		// DownAndShowImageTask(String.valueOf(list.get(postion).get("icon")),
		// viewHolder.icon).execute();
		viewHolder.counts.setText(list.get(postion).get("count_comment")
				.toString()
				+ " 评论 | "
				+ list.get(postion).get("count_like").toString()
				+ " 赞");

		int rank_i = Integer.parseInt(list.get(postion).get("rank").toString());
		if (rank_i == 0)
			viewHolder.rank.setBackgroundResource(R.drawable.abc_first);
		else if (rank_i == 1)
			viewHolder.rank.setBackgroundResource(R.drawable.abc_second);
		else if (rank_i == 2)
			viewHolder.rank.setBackgroundResource(R.drawable.abc_third);
		else
			viewHolder.rank.setBackgroundResource(R.drawable.trans_bg);
		// switch (postion) {
		// case 0:
		// viewHolder.rank.setBackgroundResource(R.drawable.abc_first);
		// break;
		// case 1:
		// viewHolder.rank.setBackgroundResource(R.drawable.abc_second);
		// break;
		// case 2:
		// viewHolder.rank.setBackgroundResource(R.drawable.abc_third);
		// break;
		// default:
		// viewHolder.rank.setVisibility(View.GONE);
		// break;
		//
		// }
		return view;
	}

	class ViewHolder {
		CircleImageView icon;
		TextView name;
		TextView counts;
		ImageView rank;

		public ViewHolder(View view) {
			icon = (CircleImageView) view.findViewById(R.id.head);
			name = (TextView) view.findViewById(R.id.tx_name);
			counts = (TextView) view.findViewById(R.id.tx_counts);
			rank = (ImageView) view.findViewById(R.id.rank);
		}
	}
}
