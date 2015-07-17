package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.util.ImagerLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchHotLineAdapter extends BaseAdapter {
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private LayoutInflater inflater;
	private Activity context;
	private ImagerLoader loader = new ImagerLoader();

	@Override
	public int getCount() {
		return list.size();
	}

	public MatchHotLineAdapter(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public void setItem(List<Map<String, Object>> list) {
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
			view = inflater.inflate(R.layout.abc_match_hotline_item, null);
			viewHolder = new ViewHolder(view);
			view.setTag(R.id.ViewHolder, viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag(R.id.ViewHolder);
		}
		viewHolder.tx_content.setText(list.get(postion).get("text").toString());

		viewHolder.name.setText(list.get(postion).get("nickname").toString());
		viewHolder.time.setText(list.get(postion).get("time").toString());
		viewHolder.commentCount.setText(list.get(postion).get("comment_count")
				.toString());
		viewHolder.likeCount.setText(list.get(postion).get("like_count")
				.toString());
		// new
		// DownAndShowImageTask(list.get(postion).get("user_icon").toString(),
		// viewHolder.icon).execute();
		loader.LoadImage(list.get(postion).get("user_icon").toString(),
				viewHolder.icon);
		List<Map<String, String>> imgs = (List<Map<String, String>>) list.get(
				postion).get("images");
		if (imgs.size() < 1) {
			// oneHolder.photo.setVisibility(View.GONE);
			viewHolder.ll_content.removeAllViews();
			viewHolder.ll_grid1.removeAllViews();
			viewHolder.ll_grid2.removeAllViews();
			viewHolder.ll_grid3.removeAllViews();
		} else if (imgs.size() == 1) {
			viewHolder.ll_content.removeAllViews();
			viewHolder.ll_grid1.removeAllViews();
			viewHolder.ll_grid2.removeAllViews();
			viewHolder.ll_grid3.removeAllViews();
			ImageView photo = new ImageView(context);
			// 设置当前图像的图像（position为当前图像列表的位置）
			photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			photo.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					200));
			// 设置Gallery组件的背景风格
			// oneHolder.photo.setVisibility(View.VISIBLE);
			// new DownAndShowImageTask(imgs.get(0).get("url").toString(),
			// photo)
			// .execute();
			loader.LoadImage(imgs.get(0).get("url").toString(), photo);
			viewHolder.ll_content.addView(photo);

		} else {
			if (imgs.size() < 4) {
				// 一行
				viewHolder.ll_grid1.removeAllViews();
				viewHolder.ll_grid2.removeAllViews();
				viewHolder.ll_grid3.removeAllViews();
				for (int i = 0; i < 3; i++) {
					addPhoto(i, viewHolder.ll_grid1, imgs);
				}
			} else if (imgs.size() > 3 && imgs.size() < 7) {
				// 两行
				viewHolder.ll_grid1.removeAllViews();
				viewHolder.ll_grid2.removeAllViews();
				viewHolder.ll_grid3.removeAllViews();
				for (int i = 0; i < 3; i++) {
					addPhoto(i, viewHolder.ll_grid1, imgs);
				}
				for (int i = 3; i < 6; i++) {
					addPhoto(i, viewHolder.ll_grid2, imgs);
				}
			} else if (imgs.size() > 6 && imgs.size() < 10) {
				// 三行
				viewHolder.ll_grid1.removeAllViews();
				viewHolder.ll_grid2.removeAllViews();
				viewHolder.ll_grid3.removeAllViews();
				for (int i = 0; i < 3; i++) {
					addPhoto(i, viewHolder.ll_grid1, imgs);
				}
				for (int i = 3; i < 6; i++) {
					addPhoto(i, viewHolder.ll_grid2, imgs);
				}
			}
			// oneHolder.photo.setVisibility(View.GONE);
			// oneHolder.ll_content.removeView(oneHolder.photo);
			viewHolder.ll_content.removeAllViews();
		}
		return view;
	}

	class ViewHolder {
		private TextView name;
		private CircleImageView icon;
		private TextView time;
		private TextView commentCount;
		private TextView likeCount;
		private TextView tx_content;
		private LinearLayout ll_content;
		private LinearLayout ll_grid1;
		private LinearLayout ll_grid2;
		private LinearLayout ll_grid3;

		public ViewHolder(View view) {
			name = (TextView) view.findViewById(R.id.tx_name);
			icon = (CircleImageView) view.findViewById(R.id.head_icon);
			time = (TextView) view.findViewById(R.id.time);
			commentCount = (TextView) view.findViewById(R.id.comment_num);
			likeCount = (TextView) view.findViewById(R.id.enjoy_num);
			tx_content = (TextView) view.findViewById(R.id.content);
			ll_content = (LinearLayout) view.findViewById(R.id.ll_photo);
			ll_grid1 = (LinearLayout) view.findViewById(R.id.ll_photo_grid1);
			ll_grid2 = (LinearLayout) view.findViewById(R.id.ll_photo_grid2);
			ll_grid3 = (LinearLayout) view.findViewById(R.id.ll_photo_grid3);
		}
	}

	private void addPhoto(int position, LinearLayout layout,
			List<Map<String, String>> imgs) {
		ImageView photo = new ImageView(context);
		// 设置当前图像的图像（position为当前图像列表的位置）
		photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		photo.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 200, 1.0f));
		// 设置Gallery组件的背景风格
		// oneHolder.photo.setVisibility(View.VISIBLE);
		if (position < imgs.size())
			// new
			// DownAndShowImageTask(imgs.get(position).get("url").toString(),
			// photo).execute();
		loader.LoadImage(imgs.get(position).get("url").toString(), photo);
		layout.addView(photo);
	}
}
