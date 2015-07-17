package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

@SuppressLint("ResourceAsColor")
public class PlaceChooseComponentsAdapter extends BaseAdapter {

	private boolean isChice[];
	private Context context;
	private List<String> components = new ArrayList<String>();

	private int sum = 0;

	private int index;

	public PlaceChooseComponentsAdapter(Context context, int index) {

		this.context = context;
		this.index = index;
	}

	public void setItems(List<String> components) {
		this.components = components;
		isChice = new boolean[components.size()];
		for (int i = 0; i < components.size(); i++) {
			isChice[i] = false;
		}
	}

	@Override
	public int getCount() {
		return components.size();
	}

	@Override
	public Object getItem(int arg0) {
		return components.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View view = arg1;
		GetView getView = null;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.abc_team_com_item, null);
			getView = new GetView();
			getView.imageView = (TextView) view.findViewById(R.id.componts);
			view.setTag(getView);
		} else {
			getView = (GetView) view.getTag();
		}
		getView.imageView.setText(components.get(arg0));
		// getView.imageView.setImageDrawable(getView(arg0));
		if (isChice[arg0] == true) {
			getView.imageView.setBackgroundResource(R.color.green);
		} else {
			getView.imageView.setBackgroundResource(R.color.trans);
		}
		return view;
	}

	static class GetView {
		TextView imageView;
	}

	public String getChoiced() {
		String msg = "";
		for (int i = 0; i < isChice.length; i++) {
			if (isChice[i]) {

				msg = msg + components.get(i) + ",";
			}
		}
		if (msg.length() > 0)
			msg = msg.substring(0, msg.lastIndexOf(","));
		return msg;
	}

	public void chiceStateByChange(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			// int post = listMap.get(list.get(i));
			for (int j = 0; j < components.size(); j++) {
				if (list.get(i).equals(components.get(j))) {
					if (!isChice[j]) {

						if (sum >= index)
							return;

						sum += 1;
					} else {
						sum -= 1;
					}
					isChice[j] = isChice[j] == true ? false : true;
				}
			}
		}

		this.notifyDataSetChanged();
	}

	public void chiceState(int post, PlaceComponentsAdapter adapter) {
		if (!isChice[post]) {

			if (sum >= index)
				return;

			sum += 1;
		}

		// isChice[post] = isChice[post] == true ? false : true;
		if (!isChice[post]) {
			isChice[post] = true;
			adapter.addData(components.get(post));
		}

		this.notifyDataSetChanged();
	}
}
