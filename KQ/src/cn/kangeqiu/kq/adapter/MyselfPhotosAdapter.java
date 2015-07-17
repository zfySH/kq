package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import cn.kangeqiu.kq.R;

import com.nowagme.football.DownAndShowImageTask;

public class MyselfPhotosAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<String> list = new ArrayList<String>();

	@Override
	public int getCount() {
		return list.size();
	}

	public MyselfPhotosAdapter(Activity context) {
		inflater = context.getLayoutInflater();
	}

	public void setItem(List<String> list) {
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
	public View getView(final int postion, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = inflater.inflate(R.layout.abc_item_published_grida, null);
			viewHolder = new ViewHolder(view);
			view.setTag(R.id.ViewHolder, viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag(R.id.ViewHolder);
		}

		new DownAndShowImageTask(list.get(postion), viewHolder.photo).execute();
		return view;
	}

	class ViewHolder {
		ImageView photo;

		public ViewHolder(View view) {
			photo = (ImageView) view.findViewById(R.id.item_grida_image);
		}
	}
}
