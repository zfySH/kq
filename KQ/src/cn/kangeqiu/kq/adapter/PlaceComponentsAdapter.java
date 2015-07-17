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
public class PlaceComponentsAdapter extends BaseAdapter {

	private boolean isChice[];
	private Context context;
	private List<String> components = new ArrayList<String>();

	private int sum = 0;

	public PlaceComponentsAdapter(Context context) {

		this.context = context;
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

	public void addData(String data) {
		components.add(data);
		this.setItems(components);
		this.notifyDataSetChanged();
	}

	public String getData() {
		String msg = "";
		for (int i = 0; i < components.size(); i++) {
			if(i>0) msg += ",";
			msg+=components.get(i);
		}
		if (msg.equals(""))
			return null;
		return msg;
	}

	public void chiceState(int post) {
		// if (!isChice[post]) {
		//
		// if (sum >= 2)
		// return;
		//
		// sum += 1;
		// } else {
		// sum -= 1;
		// }
		//
		// isChice[post] = isChice[post] == true ? false : true;
		this.notifyDataSetChanged();
	}
}
