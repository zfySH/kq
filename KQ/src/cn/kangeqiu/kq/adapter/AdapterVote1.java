package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

@SuppressLint("UseSparseArrays")
public class AdapterVote1 extends BaseAdapter {
	private Context mContext;
	private List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
	private List<Boolean> list = new ArrayList<Boolean>();
	private boolean falg = false;
	private int count;
	int join_count;
	int width;
	int rect;

	// String count;

	public AdapterVote1(Context mContext) {
		this.mContext = mContext;
	}

	public void SetItme(List<Map<String, Object>> options) {
		this.options = options;
		// this.join_count=join_count;
		for (int i = 0; i < options.size(); i++) {
			count = Integer.parseInt(options.get(i).get("count").toString());
			join_count += count;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final OneHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.vote_item2, null);
			holder = new OneHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.txt_meeting = (TextView) convertView
					.findViewById(R.id.txt_meeting);
			holder.txt_meetingnum = (TextView) convertView
					.findViewById(R.id.txt_meetingnum);
			convertView.setTag(holder);
		} else {
			holder = (OneHolder) convertView.getTag();
		}
		if (options !=null) {
			holder.name.setText(options.get(position).get("name").toString());
			// 获取宽度
			WindowManager wm = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			width = wm.getDefaultDisplay().getWidth();
			if (join_count ==0) {
				rect=0;
			}else {
				 rect = Integer.parseInt(options.get(position).get("count")
							.toString())
							* 1000
							/ join_count
							/ 10
							+ (Integer.parseInt(options.get(position).get("count")
									.toString())
									* 1000 / join_count % 10 < 5 ? 0 : 1);
			}

			holder.txt_meeting.setWidth(((width - 350) / 100) * rect);

			holder.txt_meetingnum.setText(rect + "%");
		}
		

		return convertView;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	class OneHolder {
		private TextView name, txt_meeting, txt_meetingnum;

	}

}
