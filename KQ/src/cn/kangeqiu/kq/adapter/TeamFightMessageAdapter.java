package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.TeamFightPlayView;

import com.easemob.applib.model.MessageItemModel;
import com.nowagme.util.ImagerLoader;

public class TeamFightMessageAdapter extends BaseAdapter {
	List<MessageItemModel> list = new ArrayList<MessageItemModel>();
	private LayoutInflater inflater;
	private Activity context;
	private ImagerLoader loader = new ImagerLoader();

	@Override
	public int getCount() {
		return list.size();
	}

	public TeamFightMessageAdapter(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public void setItem(List<MessageItemModel> list) {
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
	public View getView(int postion, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = inflater.inflate(R.layout.abc_fight_message_item, null);
			viewHolder = new ViewHolder(view);
			view.setTag(R.id.ViewHolder, viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag(R.id.ViewHolder);
		}
		if (list.get(postion).getType().equals("0")) {
			viewHolder.content.setVisibility(View.VISIBLE);
			viewHolder.name.setVisibility(View.VISIBLE);

			viewHolder.photos.setVisibility(View.GONE);
			viewHolder.icon.setVisibility(View.GONE);
		} else if (list.get(postion).getType().equals("1")) {
			viewHolder.content.setVisibility(View.GONE);
			viewHolder.name.setVisibility(View.GONE);
			viewHolder.photos.setVisibility(View.VISIBLE);
			viewHolder.icon.setVisibility(View.VISIBLE);
			viewHolder.photoLay.removeAllViews();

			// new DownAndShowImageTask(list.get(postion).getIconUrl(),
			// viewHolder.icon).execute();
			loader.LoadImage(list.get(postion).getIconUrl(), viewHolder.icon);
			List<Map<String, String>> photos = list.get(postion).getPhotos();
			for (int i = 0; i < photos.size(); i++) {
				TeamFightPlayView icon = new TeamFightPlayView(context);
				viewHolder.photoLay.addView(icon.getView(
						photos.get(i).get("faceimg"),
						photos.get(i).get("nickname")));
			}
		}
		viewHolder.name.setText(list.get(postion).getName());
		viewHolder.content.setText(list.get(postion).getContent());

		view.setTag(R.id.MyselfMessage, list.get(postion).getContent());
		return view;
	}

	class ViewHolder {
		TextView name;
		TextView content;
		ImageView contentView;
		ImageView arron;
		HorizontalScrollView photos;
		LinearLayout photoLay;
		ImageView icon;

		// ListView photoList;

		public ViewHolder(View view) {
			name = (TextView) view.findViewById(R.id.title);
			content = (TextView) view.findViewById(R.id.content);
			contentView = (ImageView) view.findViewById(R.id.content_icon);
			arron = (ImageView) view.findViewById(R.id.arron);
			photos = (HorizontalScrollView) view.findViewById(R.id.photos);
			photoLay = (LinearLayout) view.findViewById(R.id.photo_lay);
			icon = (ImageView) view.findViewById(R.id.team_icon);
			// photoList = (ListView) view.findViewById(R.id.photo_list);
		}
	}
}
