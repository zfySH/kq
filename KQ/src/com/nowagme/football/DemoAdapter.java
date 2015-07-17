package com.nowagme.football;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

public class DemoAdapter extends BaseAdapter {

	/**
	 * 上下文对象
	 */
	private Context context = null;

	/**
	 * 数据集合
	 */
	private List<DemoBean> datas = null;

	
	public DemoAdapter(Context context, List<DemoBean> datas) {
		this.datas = datas;
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
		

		ViewGroup layout = null;

		/**
		 * 进行ListView 的优化
		 */
		if (convertView == null) {
			layout = (ViewGroup) LayoutInflater.from(context).inflate(
					R.layout.abc_fragment_team_myteam_listview, parent, false);
		} else {
			layout = (ViewGroup) convertView;
		}

		final DemoBean bean = datas.get(position);

		
		/*
		 * 设置每一个item的文本
		 */
		TextView tvTitle = (TextView) layout.findViewById(R.id.abc_fragment_team_myteam_listview__tv_title);
		tvTitle.setText(bean.getTitle());

		
		

		ViewHolder holder = new ViewHolder();


		holder.tvTitle = tvTitle;
		
		
		/**
		 * 将数据保存到tag
		 */
		layout.setTag(holder);
		return layout;
	}
	
	/**
	 * 全选/全不选
	 * @param bool
	 */
	public void selectOrDiselectAll(boolean bool) {
		int count = this.getCount();
		for(int i=0;i<count;i++){
			datas.get(i).setHasSelected(bool);
		}
	}

	/**
	 * 增加一项的时候
	 */
	public void add(DemoBean bean) {
		this.datas.add(0, bean);

	}

	// 移除一个项目的时候
	public void remove(int position) {
		this.datas.remove(position);
	}

	
	public static class ViewHolder {

		public TextView tvTitle = null;

	}

	public List<DemoBean> getDatas() {
		return datas;
	}

}
