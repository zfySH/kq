package cn.kangeqiu.kq.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.util.ImagerLoader;

public class AdapterHelp extends BaseAdapter {

	private Context context = null;
	private List<Map<String, Object>> datas = null;
	ImagerLoader loader = new ImagerLoader();

	public AdapterHelp(Context context) {
		this.context = context;
	}

	public void setDatas(List<Map<String, Object>> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		OneHolder oneholder = null;
		if (convertView == null) {
			convertView = (ViewGroup) LayoutInflater.from(context).inflate(
					R.layout.abc_sys_msg_listview, parent, false);
			oneholder = new OneHolder();
			oneholder.tv_body = (TextView) convertView
					.findViewById(R.id.abc_sys_msg__listview__tv_body);
			oneholder.tv_title = (TextView) convertView
					.findViewById(R.id.abc_sys_msg__listview__tv_title);
			oneholder.tv_time = (TextView) convertView
					.findViewById(R.id.abc_sys_msg__listview__tv_time);
			oneholder.iv_avatar = (ImageView) convertView
					.findViewById(R.id.abc_sys_msg__listview__iv_avatar);
			oneholder.tv_unread_flag = (TextView) convertView
					.findViewById(R.id.abc_sys_msg__listview__tv_unread_flag);

			convertView.setTag(oneholder);
		} else {
			oneholder = (OneHolder) convertView.getTag();
		}

		oneholder.tv_title.setText(datas.get(position).get("nickname")
				.toString());
		// new DownAndShowImageTask(datas.get(position).get("icon").toString(),
		// oneholder.iv_avatar).execute();
		loader.LoadImage(datas.get(position).get("icon").toString(), oneholder.iv_avatar);
		oneholder.tv_body
				.setText(datas.get(position).get("fun_count").toString()
						+ " 粉丝 | "
						+ datas.get(position).get("attention_count").toString()
						+ " 关注");

		return convertView;
	}

	public static class OneHolder {

		public TextView tv_body;
		public TextView tv_time;
		private ImageView iv_avatar;
		private TextView tv_unread_flag;
		public TextView tv_title;
	}
}
