package cn.kangeqiu.kq.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

public class AdapterIntegral extends BaseAdapter {
	private LayoutInflater inflater;
	private JSONArray records = new JSONArray();
	private OneHolder oneHolder;
	String mode = "", icon = "", id = "";
	/**
	 * 上下文对象
	 */
	private Activity context = null;

	public AdapterIntegral(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();

	}

	public void setItem(JSONArray records) {
		if (records == null)
			return;

		this.records = records;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return records == null ? 0 : records.length();
	}

	@Override
	public Object getItem(int position) {
		try {
			return records.getJSONObject(position);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		oneHolder = new OneHolder();

		if (view == null) {
			view = inflater.inflate(R.layout.integral_item, null);
			oneHolder.name = (TextView) view.findViewById(R.id.txt_name);
			oneHolder.logo = (ImageView) view
					.findViewById(R.id.abc_fragment_nearby__listview__iv_faceimg);
			oneHolder.txt_num = (TextView) view.findViewById(R.id.txt_num);
			oneHolder.txt_additional = (TextView) view
					.findViewById(R.id.txt_additional);

			view.setTag(oneHolder);

		} else {
			oneHolder = (OneHolder) view.getTag();
		}
		try {
			id = records.getJSONObject(position).getString("id");
			icon = records.getJSONObject(position).getString("icon");
			if (icon.equals("0")) {
				oneHolder.logo.setImageResource(R.drawable.img_jin_task);
			} else if (icon.equals("1")) {
				oneHolder.logo.setImageResource(R.drawable.img_quiz);
			} else if (icon.equals("2")) {
				oneHolder.logo.setImageResource(R.drawable.img_jin_vote);
			} else {
				oneHolder.logo.setImageResource(R.drawable.img_jin_system);
			}
			oneHolder.name.setText(records.getJSONObject(position).getString(
					"text"));
			oneHolder.txt_num.setText(records.getJSONObject(position).getString("score"));
			oneHolder.txt_additional.setText(records.getJSONObject(position)
					.getString("additional"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return view;
	}

	class OneHolder {
		private TextView name, txt_additional, txt_num;
		private ImageView logo;
	}

}
