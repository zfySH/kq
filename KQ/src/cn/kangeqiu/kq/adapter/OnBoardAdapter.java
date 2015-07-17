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

public class OnBoardAdapter extends BaseAdapter {
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private LayoutInflater inflater;

	private static final int ITEM_LAYOUT_TYPE_COUNT = 3;

	private static final int TYPE_ONE = 0;
	private static final int TYPE_TWO = 1;
	private static final int TYPE_THREE = 2;

	private Activity context;
	private ImagerLoader loader = new ImagerLoader();

	@Override
	public int getCount() {
		return list.size();
	}

	public OnBoardAdapter(Activity context) {
		inflater = context.getLayoutInflater();
		this.context = context;
	}

	@Override
	public int getViewTypeCount() {
		return ITEM_LAYOUT_TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		if (list.get(position).get("type").equals("0")) {
			return TYPE_ONE;
		} else if (list.get(position).get("type").equals("1")) {
			return TYPE_TWO;
		} else {
			return TYPE_THREE;
		}

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
		OneHolder oneHolder = new OneHolder();

		TwoHolder twoHolder = new TwoHolder();

		ThreeHolder threeHolder = new ThreeHolder();
		int layoutType = getItemViewType(postion);

		if (TYPE_ONE == layoutType) {
			if (view == null) {
				view = inflater.inflate(R.layout.abc_match_board_item, null);
				oneHolder.name = (TextView) view.findViewById(R.id.tx_name);
				oneHolder.icon = (CircleImageView) view
						.findViewById(R.id.head_icon);
				oneHolder.time = (TextView) view.findViewById(R.id.time);
				oneHolder.commentCount = (TextView) view
						.findViewById(R.id.comment_num);
				oneHolder.likeCount = (TextView) view
						.findViewById(R.id.enjoy_num);
				oneHolder.tx_content = (TextView) view
						.findViewById(R.id.content);
				oneHolder.tx_yuan = (View) view.findViewById(R.id.yuan);
				oneHolder.ll_content = (LinearLayout) view
						.findViewById(R.id.ll_photo);
				oneHolder.ll_grid1 = (LinearLayout) view
						.findViewById(R.id.ll_photo_grid1);
				oneHolder.ll_grid2 = (LinearLayout) view
						.findViewById(R.id.ll_photo_grid2);
				oneHolder.ll_grid3 = (LinearLayout) view
						.findViewById(R.id.ll_photo_grid3);

				view.setTag(oneHolder);
			} else {
				oneHolder = (OneHolder) view.getTag();
			}

			oneHolder.tx_content.setText(list.get(postion).get("text")
					.toString());

			Map<String, Object> content = (Map<String, Object>) list.get(
					postion).get("content");
			oneHolder.name.setText(content.get("nickname").toString());
			oneHolder.time.setText(content.get("time").toString());
			oneHolder.commentCount.setText(content.get("comment_count")
					.toString());
			oneHolder.likeCount.setText(content.get("like_count").toString());
			// new DownAndShowImageTask(content.get("user_icon").toString(),
			// oneHolder.icon).execute();
			loader.LoadImage(content.get("user_icon").toString(), oneHolder.icon);
			oneHolder.tx_yuan.setBackgroundResource(R.drawable.match_yuan);

			List<Map<String, String>> imgs = (List<Map<String, String>>) content
					.get("images");
			if (imgs.size() < 1) {
				// oneHolder.photo.setVisibility(View.GONE);
				oneHolder.ll_content.removeAllViews();
				oneHolder.ll_grid1.removeAllViews();
				oneHolder.ll_grid2.removeAllViews();
				oneHolder.ll_grid3.removeAllViews();
			} else if (imgs.size() == 1) {
				oneHolder.ll_content.removeAllViews();
				oneHolder.ll_grid1.removeAllViews();
				oneHolder.ll_grid2.removeAllViews();
				oneHolder.ll_grid3.removeAllViews();
				ImageView photo = new ImageView(context);
				// 设置当前图像的图像（position为当前图像列表的位置）
				photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				photo.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, 200));
				// 设置Gallery组件的背景风格
				// oneHolder.photo.setVisibility(View.VISIBLE);
				// new DownAndShowImageTask(imgs.get(0).get("url").toString(),
				// photo).execute();
				loader.LoadImage(imgs.get(0).get("url").toString(), photo);

				oneHolder.ll_content.addView(photo);

			} else {
				if (imgs.size() < 4) {
					// 一行
					oneHolder.ll_grid1.removeAllViews();
					oneHolder.ll_grid2.removeAllViews();
					oneHolder.ll_grid3.removeAllViews();
					for (int i = 0; i < 3; i++) {
						addPhoto(i, oneHolder.ll_grid1, imgs);
					}
				} else if (imgs.size() > 3 && imgs.size() < 7) {
					// 两行
					oneHolder.ll_grid1.removeAllViews();
					oneHolder.ll_grid2.removeAllViews();
					oneHolder.ll_grid3.removeAllViews();
					for (int i = 0; i < 3; i++) {
						addPhoto(i, oneHolder.ll_grid1, imgs);
					}
					for (int i = 3; i < 6; i++) {
						addPhoto(i, oneHolder.ll_grid2, imgs);
					}
				} else if (imgs.size() > 6 && imgs.size() < 10) {
					// 三行
					oneHolder.ll_grid1.removeAllViews();
					oneHolder.ll_grid2.removeAllViews();
					oneHolder.ll_grid3.removeAllViews();
					for (int i = 0; i < 3; i++) {
						addPhoto(i, oneHolder.ll_grid1, imgs);
					}
					for (int i = 3; i < 6; i++) {
						addPhoto(i, oneHolder.ll_grid2, imgs);
					}
				}
				// oneHolder.photo.setVisibility(View.GONE);
				// oneHolder.ll_content.removeView(oneHolder.photo);
				oneHolder.ll_content.removeAllViews();
			}

		} else if (TYPE_TWO == layoutType) {
			if (view == null) {
				view = inflater.inflate(R.layout.abc_match_board_item_match,
						null);
				twoHolder.name = (TextView) view.findViewById(R.id.tx_name);
				twoHolder.tx_yuan = (View) view.findViewById(R.id.yuan);
				view.setTag(twoHolder);
			} else {
				twoHolder = (TwoHolder) view.getTag();
			}

			twoHolder.name.setText(list.get(postion).get("text").toString());
			twoHolder.tx_yuan.setBackgroundResource(R.drawable.match_yuan_);
		} else if (TYPE_THREE == layoutType) {
			if (view == null) {
				view = inflater.inflate(R.layout.abc_match_board_item_activity,
						null);
				threeHolder.name = (TextView) view.findViewById(R.id.tx_name);
				threeHolder.type = (TextView) view.findViewById(R.id.type);
				threeHolder.statue = (TextView) view.findViewById(R.id.status);
				threeHolder.tx_yuan = (View) view.findViewById(R.id.yuan);
				view.setTag(threeHolder);
			} else {
				threeHolder = (ThreeHolder) view.getTag();
			}
			threeHolder.name.setText(list.get(postion).get("text").toString());
			Map<String, String> activity = (Map<String, String>) list.get(
					postion).get("activity");

			threeHolder.type.setText(activity.get("type").equals("0") ? "竞猜"
					: "投票");
			threeHolder.type.setBackgroundResource(activity.get("type").equals(
					"0") ? R.drawable.abc_button_roundcorner_jingcai
					: R.drawable.abc_button_roundcorner_toupiao);
			threeHolder.statue.setText(getStatus(activity.get("state")));
			threeHolder.tx_yuan.setBackgroundResource(R.drawable.match_yuan);
		}

		// new DownAndShowImageTask(list.get(postion).get("faceimg"),
		// viewHolder.icon).execute();
		return view;
	}

	class OneHolder {
		private TextView name;
		private CircleImageView icon;
		private TextView time;
		private TextView commentCount;
		private TextView likeCount;
		private TextView tx_content;
		private View tx_yuan;
		// private ImageView photo;

		private LinearLayout ll_content;
		private LinearLayout ll_grid1;
		private LinearLayout ll_grid2;
		private LinearLayout ll_grid3;
	}

	class TwoHolder {
		private TextView name;
		private View tx_yuan;
	}

	class ThreeHolder {
		private TextView name;
		private TextView counts;
		private TextView type;
		private TextView statue;
		private View tx_yuan;
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
			loader.LoadImage(imgs.get(position).get("url").toString(), photo);

		// new DownAndShowImageTask(imgs.get(position).get("url").toString(),
		// photo).execute();

		layout.addView(photo);
	}

	private String getStatus(String status) {
		if (status.equals("0"))
			return "未开始";
		else if (status.equals("1"))
			return "进行中";
		else if (status.equals("2"))
			return "已结束";
		return "";
	}
}
