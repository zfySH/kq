package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.model.MemberInfoModel;

import com.nowagme.util.ImagerLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberChooseAdapter extends BaseAdapter {

	private Activity context;
	private List<MemberInfoModel> components = new ArrayList<MemberInfoModel>();
	private LayoutInflater inflater;
	private int type = 0, mold = 0;
	private String texts = "";
	private ImagerLoader loader = new ImagerLoader();

	public MemberChooseAdapter(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();

	}

	public void setItems(List<MemberInfoModel> components, int type, int mold) {
		this.components = components;
		this.type = type;
		this.mold = mold;
		notifyDataSetChanged();
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
	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = inflater.inflate(R.layout.abc_item_member, null);
			viewHolder = new ViewHolder(view);
			view.setTag(R.id.ViewHolder, viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag(R.id.ViewHolder);
		}

		// if (type == 0) {// 只能增加
		// if (position == components.size() - 1) {
		// viewHolder.icon.setBackgroundResource(R.drawable.add_member);
		// } else {
		// new DownAndShowImageTask(components.get(position).getIcon(),
		// viewHolder.icon).execute();
		// }
		// } else if (type == 1) {// 增加or删除
		// if (position == components.size() - 2) {
		// viewHolder.icon.setBackgroundResource(R.drawable.add_member);
		// } else if (position == components.size() - 1) {
		// viewHolder.icon
		// .setBackgroundResource(R.drawable.umeng_socialize_switchimage_unchoose);
		//
		// } else {
		// new DownAndShowImageTask(components.get(position).getIcon(),
		// viewHolder.icon).execute();
		// }
		// }
		//
		// if (view.getTag() == null
		// || !view.getTag().toString()
		// .equals(components.get(position).getIcon())) {
		viewHolder.icon
				.setTag(R.id.iconUrl, components.get(position).getIcon());
		// }

		if (components.get(position).getType() == 0) {
			loader.LoadImage(components.get(position).getIcon(),
					viewHolder.icon);

			viewHolder.img_delete.setVisibility(view.GONE);
		} else if (components.get(position).getType() == 1) {
			loader.LoadImage(components.get(position).getIcon(),
					viewHolder.icon);
			if (mold == 0) {
				viewHolder.img_delete.setVisibility(view.GONE);
			} else {
				viewHolder.img_delete.setVisibility(view.VISIBLE);
			}
		} else if (components.get(position).getType() == 2) {
			viewHolder.icon.setImageResource(Integer.parseInt(components.get(
					position).getIcon()));
			viewHolder.img_delete.setVisibility(view.GONE);
		} else if (components.get(position).getType() == 3) {

			viewHolder.icon.setImageResource(Integer.parseInt(components.get(
					position).getIcon()));

			viewHolder.img_delete.setVisibility(view.GONE);
		} else {
			viewHolder.icon.setBackgroundResource(R.drawable.trans_bg);
		}
		viewHolder.img_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				components.remove(position);
				notifyDataSetChanged();
			}
		});

		if (components.size() == 3) {
			components.remove(2);
			notifyDataSetChanged();
		}

		return view;
	}

	public String getId() {
		notifyDataSetChanged();
		String temp = "";
		for (int i = 0; i < components.size(); i++) {
			if (components.get(i).getId() != null) {
				temp += "," + components.get(i).getId();
			}
		}
		if (temp.length() > 0)
			texts = temp.substring(1);

		return texts;
	}

	class ViewHolder {
		CircleImageView icon;
		ImageButton img_delete;

		public ViewHolder(View view) {
			icon = (CircleImageView) view.findViewById(R.id.item_grida_image);
			img_delete = (ImageButton) view.findViewById(R.id.img_delete);
		}
	}
}
