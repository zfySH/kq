package cn.kangeqiu.kq.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

public class MyselfMessageAdapter extends BaseAdapter {
	JSONArray list = new JSONArray();
	private LayoutInflater inflater;
	private Activity context;

	@Override
	public int getCount() {
		return list.length();
	}

	public MyselfMessageAdapter(Activity context) {
		inflater = context.getLayoutInflater();
		this.context = context;
	}

	public void setItem(JSONArray list) {
		if (list == null)
			return;

		this.list = list;

		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int arg0) {
		try {
			return list.getJSONObject(arg0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int postion, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = inflater.inflate(R.layout.abc_myself_message_item, null);
			viewHolder = new ViewHolder(view);
			view.setTag(R.id.ViewHolder, viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag(R.id.ViewHolder);
		}
		// if (postion == 0 || postion == 4) {
		// view.setPadding(0, 20, 0, 0);
		// }

		// 删除动态生成的View
		// eraseView(viewHolder.contentLayout);

		try {
			viewHolder.name.setText(list.getJSONObject(postion).getString(
					"name"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// String type = list.get(postion).getType();
		// if (type.equals("place")) {
		// viewHolder.content.setVisibility(View.GONE);
		// TextView tv = new TextView(context);
		// tv.setText(list.get(postion).getContent());
		// tv.setTextColor(Color.WHITE);
		// tv.setBackgroundResource(R.drawable.abc_button_roundcorner_light_orange);
		// viewHolder.contentLayout.addView(tv);
		// needEreaseList.add(tv);
		// } else if (type.equals("tag")) {
		// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.WRAP_CONTENT,
		// LinearLayout.LayoutParams.WRAP_CONTENT);
		// params.height = DisplayUtil.dip2px(context, 20);
		// params.leftMargin = DisplayUtil.dip2px(context, 8);
		// viewHolder.content.setVisibility(View.GONE);
		// String[] tags = list.get(postion).getContent().split(",");
		// for (int i = 0; i < tags.length; i++) {
		// TextView tv = new TextView(context);
		// tv.setText(tags[i]);
		// tv.setTextColor(Color.WHITE);
		// tv.setBackgroundResource(R.drawable.abc_button_roundcorner_dark_green);
		// tv.setLayoutParams(params);
		// viewHolder.contentLayout.addView(tv);
		// needEreaseList.add(tv);
		// }
		// } else {
		// viewHolder.content.setVisibility(View.VISIBLE);
		// viewHolder.content.setText(list.get(postion).getContent());
		// }

		// view.setTag(R.id.MyselfMessage, list.get(postion).getContent());
		return view;
	}

	class ViewHolder {
		TextView name;
		TextView content;
		ImageView contentView;
		ImageView arron;

		LinearLayout contentLayout;

		public ViewHolder(View view) {
			name = (TextView) view.findViewById(R.id.title);
			content = (TextView) view.findViewById(R.id.content);
			contentView = (ImageView) view.findViewById(R.id.content_icon);
			arron = (ImageView) view.findViewById(R.id.arron);
			contentLayout = (LinearLayout) view
					.findViewById(R.id.content_with_bg);
		}
	}
}
