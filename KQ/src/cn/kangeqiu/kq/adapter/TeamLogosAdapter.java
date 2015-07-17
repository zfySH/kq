package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import cn.kangeqiu.kq.R;

import com.nowagme.util.ImagerLoader;

public class TeamLogosAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<String> list = new ArrayList<String>();
	private Vector<Boolean> mImage_bs = new Vector<Boolean>(); // 定义一个向量作为选中与否容器
	ImagerLoader loader = new ImagerLoader();

	@Override
	public int getCount() {
		return list.size();
	}

	public TeamLogosAdapter(Activity context) {
		inflater = context.getLayoutInflater();
	}

	public void setItem(List<String> list) {
		if (list == null)
			return;

		this.list = list;
		for (int i = 0; i < list.size(); i++)
			mImage_bs.add(false);

		notifyDataSetChanged();
	}

	public int getChoiced() {
		for (int i = 0; i < list.size(); i++) {
			if (mImage_bs.get(i))
				return i;
		}
		return -1;
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void chiceState(int position) {
		for (int i = 0; i < list.size(); i++)
			mImage_bs.setElementAt(false, i);

		mImage_bs.setElementAt(true, position); // 直接取反即可
		notifyDataSetChanged(); // 通知适配器进行更新
	}

	@Override
	public View getView(final int postion, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = inflater.inflate(R.layout.abc_team_icon, null);
			viewHolder = new ViewHolder(view);
			view.setTag(R.id.ViewHolder, viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag(R.id.ViewHolder);
		}

		// new DownAndShowImageTask(list.get(postion),
		// viewHolder.photo).execute();
		loader.LoadImage(list.get(postion), viewHolder.photo);
		final ImageView isChecked = (ImageView) view
				.findViewById(R.id.is_checked);

		if (mImage_bs.elementAt(postion))
			isChecked.setVisibility(View.VISIBLE);
		else
			isChecked.setVisibility(View.INVISIBLE);
		return view;
	}

	class ViewHolder {
		ImageView photo;

		public ViewHolder(View view) {
			photo = (ImageView) view.findViewById(R.id.item_grida_image);
		}
	}
}
