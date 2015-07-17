package com.nowagame.kq.activity;

import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.kangeqiu.kq.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MineActivityAdapter extends BaseAdapter {

	private Activity context;
	private int[] imageArray;
	private String[] imageNameArray;
	JSONObject list;

	public MineActivityAdapter(Activity context, int[] imageArray,
			String[] imageNameArray) {
		this.context = context;
		this.imageArray = imageArray;
		this.imageNameArray = imageNameArray;
	}
	public void setItem(JSONObject list) {
		if (list == null)
			return;
		this.list = list;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return imageArray.length == 0 ? 0 : imageArray.length;
	}

	@Override
	public Object getItem(int position) {
		return imageArray.length == 0 ? null : imageArray[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		Holder holder;
		if (view == null) {
			
			holder = new Holder();
			view = context.getLayoutInflater().inflate(R.layout.abc_activity_mine_item, null);
			
			holder.imgDHCC = (CircleImageView) view.findViewById(R.id.imgDHCC);
			holder.name_textView = (TextView) view.findViewById(R.id.name_textView);
			holder.count = (TextView) view.findViewById(R.id.count);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		
		holder.imgDHCC.setImageResource(imageArray[position]);
		holder.name_textView.setText(imageNameArray[position]);
		holder.count.setText(imageNameArray[position]);
		return view;
	}

	static class Holder {
		de.hdodenhof.circleimageview.CircleImageView imgDHCC;
		TextView name_textView, line1, line2,count;
	}
}
