package com.nowagme.football;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.util.ImagerLoader;

public class AdapterSysMsg extends BaseAdapter {

	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["
			+ AdapterSysMsg.class.getName() + "]";
	private ImagerLoader loader = new ImagerLoader();

	private String type = "";
	/**
	 * 上下文对象
	 */
	private Context context = null;

	/**
	 * 数据集合
	 */
	private List<Map<String, Object>> datas = null;

	public void setType(String type) {
		this.type = type;
	}

	public AdapterSysMsg(Context context) {
		this.context = context;

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
	public View getView(final int position, View convertView, ViewGroup parent) {
		OneHolder oneholder = null;
		TwoHolder twoholder = null;
		/**
		 * 进行ListView 的优化
		 */
		if (type.equals("0")) {
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

			// restore view state:must do if not will occurs error.
			oneholder.tv_title.setText(datas.get(position).get("nickname")
					.toString());
			// new DownAndShowImageTask(
			// datas.get(position).get("icon").toString(),
			// oneholder.iv_avatar).execute();
			loader.LoadImage(datas.get(position).get("icon").toString(),
					oneholder.iv_avatar);

		} else if (type.equals("1")) {
			if (convertView == null) {
				convertView = (ViewGroup) LayoutInflater.from(context).inflate(
						R.layout.abc_comment_msg_listview, parent, false);
				twoholder = new TwoHolder();
				twoholder.tv_body = (TextView) convertView
						.findViewById(R.id.abc_sys_msg__listview__tv_body);
				twoholder.tv_title = (TextView) convertView
						.findViewById(R.id.abc_sys_msg__listview__tv_title);
				twoholder.tv_time = (TextView) convertView
						.findViewById(R.id.abc_sys_msg__listview__tv_time);
				twoholder.iv_avatar = (ImageView) convertView
						.findViewById(R.id.abc_sys_msg__listview__iv_avatar);
				twoholder.tv_unread_flag = (TextView) convertView
						.findViewById(R.id.abc_sys_msg__listview__tv_unread_flag);
				twoholder.iv_comment = (ImageView) convertView
						.findViewById(R.id.iv_comment);
				twoholder.comment_name = (TextView) convertView
						.findViewById(R.id.comment_name);
				twoholder.comment_content = (TextView) convertView
						.findViewById(R.id.comment_content);
				twoholder.btn_reply = (Button) convertView
						.findViewById(R.id.btn_reply);
				convertView.setTag(twoholder);
				Log.d("TagListView", "新创建ViewHolder");
			} else {
				twoholder = (TwoHolder) convertView.getTag();
				Log.d("TagListView", "缓存中获取ViewHolder");
			}

			final Map<String, String> comment = (Map<String, String>) datas
					.get(position).get("comment");
			// restore view state:must do if not will occurs error.
			twoholder.tv_title.setText(comment.get("nickname").toString());
			// new DownAndShowImageTask(comment.get("user_icon").toString(),
			// twoholder.iv_avatar).execute();
			loader.LoadImage(comment.get("user_icon").toString(),
					twoholder.iv_avatar);
			twoholder.tv_time.setText(comment.get("time"));
			twoholder.tv_body.setText(comment.get("body"));

			final Map<String, String> content = (Map<String, String>) datas
					.get(position).get("content");
			String contentImg = content.get("image");
			if (contentImg == null || contentImg.equals("")) {
				twoholder.iv_comment.setVisibility(View.GONE);
			} else {
				twoholder.iv_comment.setVisibility(View.VISIBLE);
				// new DownAndShowImageTask(contentImg, twoholder.iv_comment)
				// .execute();
				loader.LoadImage(contentImg, twoholder.iv_comment);
			}
			twoholder.comment_name.setText(content.get("nickname"));
			twoholder.comment_content.setText(content.get("text"));
			twoholder.btn_reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, Respondcomment.class);
					intent.putExtra("u_content_id", content.get("id")
							.toString());
					intent.putExtra("u_comment_id", comment.get("id")
							.toString());
					intent.putExtra("name", comment.get("nickname").toString());
					context.startActivity(intent);

				}

			});
		} else if (type.equals("2")) {
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

			// restore view state:must do if not will occurs error.
			oneholder.tv_title.setText(datas.get(position).get("nickname")
					.toString());
			// new DownAndShowImageTask(
			// datas.get(position).get("icon").toString(),
			// oneholder.iv_avatar).execute();
			loader.LoadImage(datas.get(position).get("icon").toString(),
					oneholder.iv_avatar);
			oneholder.tv_body.setText(datas.get(position).get("fun_count")
					.toString()
					+ " 粉丝 | "
					+ datas.get(position).get("attention_count").toString()
					+ " 关注");

		}

		return convertView;
	}

	/**
	 * 增加一项的时候
	 */
	public void add(Map<String, Object> bean) {
		this.datas.add(0, bean);

	}

	// 移除一个项目的时候
	public void remove(int position) {
		this.datas.remove(position);
	}

	public static class OneHolder {

		public TextView tv_body;
		public TextView tv_time;
		private ImageView iv_avatar;
		private TextView tv_unread_flag;
		public TextView tv_title;

	}

	public static class TwoHolder {

		public TextView tv_body;
		public TextView tv_time;
		private ImageView iv_avatar;
		private TextView tv_unread_flag;
		public TextView tv_title;
		private ImageView iv_comment;
		private TextView comment_name;
		private TextView comment_content;
		public Button btn_reply;
	}

	public List<Map<String, Object>> getDatas() {
		return datas;
	}

	public void setDatas(List<Map<String, Object>> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	/**
	 * log information
	 * 
	 * @param msg
	 */
	public static void logi(String msg) {
		Log.i(TAG, CLASS_NAME + " " + msg);
	}

}
