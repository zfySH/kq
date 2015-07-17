package cn.kangeqiu.kq.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.easemob.applib.utils.HXPreferenceUtils;
import com.nowagme.football.BeanMyFriends;

public class AdapterMyFriends extends BaseAdapter {
	private LayoutInflater inflater;

	/**
	 * 上下文对象
	 */
	private Activity context = null;

	/**
	 * 数据集合
	 */
	private List<BeanMyFriends> datas = null;

	public AdapterMyFriends(Activity context) {

		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public void setData(List<BeanMyFriends> datas) {
		this.datas = datas;
		this.notifyDataSetChanged();
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
	public View getView(final int position, View view, ViewGroup parent) {
		OneHolder oneHolder = new OneHolder();

		if (view == null) {
			view = inflater.inflate(R.layout.abc_fragment_nearby_player_item,
					null);
			oneHolder.abc_player_name = (TextView) view
					.findViewById(R.id.abc_player_name);
			oneHolder.abc_content = (TextView) view
					.findViewById(R.id.abc_content);
			oneHolder.text_icon = (TextView) view.findViewById(R.id.text_icon);
			oneHolder.creater_icon = (ImageButton) view
					.findViewById(R.id.creater_icon);
			oneHolder.abc_face = (ImageView) view.findViewById(R.id.abc_face);

			oneHolder.abc_face1 = (ImageView) view.findViewById(R.id.abc_face1);

			oneHolder.concern_icon = (ImageButton) view
					.findViewById(R.id.concern_icon);
			/*
			 * oneHolder.name = (TextView) view
			 * .findViewById(R.id.abc_fragment_nearby_listview__tv_name);
			 * oneHolder.logo = (ImageView) view
			 * .findViewById(R.id.abc_fragment_nearby__listview__iv_faceimg);
			 * oneHolder.dis = (Button) view
			 * .findViewById(R.id.abc_fragment_nearby__listview__btn_gps_and_time
			 * ); oneHolder.sexAge = (Button) view
			 * .findViewById(R.id.abc_fragment_nearby__listview__btn_sex_age);
			 * oneHolder.teamImg = (ImageView) view .findViewById(R.id.
			 * abc_fragment_nearby__listview__iv_player_team_faceimg);
			 * oneHolder.teamName = (TextView) view
			 * .findViewById(R.id.abc_fragment_nearby__listview__iv_player_team_name
			 * ); oneHolder.midle = (LinearLayout) view
			 * .findViewById(R.id.abc_fragment_nearby__listview__ll_middle_tags
			 * );
			 */
			view.setTag(oneHolder);
		} else {
			oneHolder = (OneHolder) view.getTag();
		}
		oneHolder.abc_player_name.setText("夏日的阳光");
		oneHolder.abc_content.setText("夏日的阳光");

		if (HXPreferenceUtils.num.equals("关注")) {
			oneHolder.abc_face.setImageResource(R.drawable.abc_activity_time);
			oneHolder.creater_icon.setVisibility(View.VISIBLE);
			oneHolder.concern_icon.setVisibility(View.GONE);
//			oneHolder.creater_icon.setImageResource(R.drawable.abc_activity_time);
			oneHolder.text_icon.setVisibility(View.GONE);

		} else if (HXPreferenceUtils.num.equals("粉丝")) {
			oneHolder.abc_face.setImageResource(R.drawable.abc_activity_time);
			oneHolder.creater_icon.setVisibility(View.GONE);
			oneHolder.concern_icon.setVisibility(View.VISIBLE);
			oneHolder.text_icon.setVisibility(View.GONE);
//			oneHolder.concern_icon.setImageResource(R.drawable.abc_activity_time);
		} else if (HXPreferenceUtils.num.equals("积分")) {
			oneHolder.abc_face1.setImageResource(R.drawable.abc_activity_time);
			oneHolder.creater_icon.setVisibility(View.GONE);
			oneHolder.concern_icon.setVisibility(View.GONE);
			oneHolder.text_icon.setVisibility(View.VISIBLE);
		}

		// final BeanMyFriends bean = datas.get(position);
		//
		// /*
		// * 设置每一个item的文本
		// */
		// oneHolder.name.setText(bean.getName());
		// String gps = bean.getDis();
		// String time = bean.getTime();
		// // oneHolder.dis.setText(gps + " " + time);
		// oneHolder.dis.setText(gps + " ");
		// String url = bean.getFaceimg();
		// if (url != null && !url.equals(""))
		// new DownAndShowImageTask(url, oneHolder.logo).execute();
		// // (1)显示球员的球队信息
		// String playerTeamName = bean.getTeamName();
		// String playerTeamFaceimg = bean.getTeamFaceImg();
		// if (playerTeamName == null) {
		// playerTeamName = "";
		// }
		// oneHolder.teamName.setText(playerTeamName);
		// if (playerTeamFaceimg != null && playerTeamFaceimg.length() > 0) {
		// new DownAndShowImageTask(playerTeamFaceimg, oneHolder.teamImg)
		// .execute();
		// } else {
		// oneHolder.teamImg.setVisibility(View.INVISIBLE);
		// }
		//
		// // (2)显示球员的性别和年龄
		// String age = bean.getAge();
		// oneHolder.sexAge.setText(age);
		// int sex = Integer.parseInt(bean.getSex());
		// int sexResId = Constant.getResourceOfPlayerSex(sex);
		// Drawable drawable = context.getResources().getDrawable(sexResId);
		// drawable.setBounds(0, 0, drawable.getMinimumWidth(),
		// drawable.getMinimumHeight());
		// oneHolder.sexAge.setCompoundDrawables(drawable, null, null, null);
		//
		// // (3)显示球员的球场位置
		// oneHolder.midle.removeAllViews();
		// String playerPlace = bean.getPlace();
		// Log.d("playerTAG", playerPlace);
		// if (playerPlace != null && playerPlace.length() > 0) {
		// String[] places = playerPlace.split(",");
		// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.WRAP_CONTENT,
		// LinearLayout.LayoutParams.WRAP_CONTENT);
		// params.height = DisplayUtil.dip2px(context, 25);
		// params.leftMargin = DisplayUtil.dip2px(context, 5);
		// params.width = DisplayUtil.dip2px(context, 55);
		// for (String placeName : places) {
		// Button btn_place = new Button(context);
		// btn_place.setText(placeName);
		// btn_place
		// .setBackgroundResource(R.drawable.abc_button_roundcorner_green);
		// btn_place.setTextColor(Color.parseColor("#EBF9F6"));
		// btn_place.setTextSize(15);
		// btn_place.setPadding(5, 1, 1, 1);
		// btn_place.setLayoutParams(params);
		// btn_place.setFocusable(false);
		// oneHolder.midle.addView(btn_place);
		// // addedViews.add(btn_place);
		// }
		// }
		return view;
	}

	class OneHolder {
		// private TextView name;
		// private ImageView logo;
		// private Button dis;
		// private ImageView teamImg;
		// private TextView teamName;
		// private Button sexAge;
		// private LinearLayout midle;

		private ImageButton creater_icon, concern_icon;
		private ImageView abc_face, abc_face1;
		private TextView abc_player_name, abc_content, text_icon;

	}

}
