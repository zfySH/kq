package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;

import com.nowagme.football.AppConfig;
import com.nowagme.football.Constant;
import com.nowagme.football.DownAndShowImageTask;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeamActivityPlayerAdapter extends BaseAdapter {
	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private LayoutInflater inflater;
	private Activity context;
	private String creater_id = "";
	private int relation;
	RemoveItem remove;

	public RemoveItem getRemove() {
		return remove;
	}

	public void setRemove(RemoveItem remove) {
		this.remove = remove;
	}

	public void setInterface(RemoveItem remove) {
		this.remove = remove;
	}

	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["
			+ TeamActivityPlayerAdapter.class.getName() + "]";

	@Override
	public int getCount() {
		return list.size();
	}

	public TeamActivityPlayerAdapter(Activity context,
			List<Map<String, String>> map) {
		inflater = context.getLayoutInflater();
		this.list = map;
		this.context = context;
	}

	/**
	 * log information
	 * 
	 * @param msg
	 */
	public static void logi(String msg) {
		Log.i(TAG, CLASS_NAME + " " + msg);
	}

	public void setRelation(int relation) {
		this.relation = relation;
	}

	public void setCreateId(String createId) {
		this.creater_id = createId;
	}

	public void setItem(List<Map<String, String>> list) {
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
			view = inflater.inflate(R.layout.abc_activity_player_item, null);
			viewHolder = new ViewHolder(view);
			view.setTag(R.id.ViewHolder, viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag(R.id.ViewHolder);
		}
		viewHolder.name.setText(list.get(postion).get("nickname"));
		// 显示球员的性别和年龄
		viewHolder.sex_age.setText(list.get(postion).get("age"));
		int sexType = Integer.parseInt(list.get(postion).get("sex"));
		int sexResId = Constant.getResourceOfPlayerSex(sexType);
		Drawable drawable = context.getResources().getDrawable(sexResId);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		viewHolder.sex_age.setCompoundDrawables(drawable, null, null, null);

		// 显示球场位置
		viewHolder.place.setText(list.get(postion).get("place"));

		// 头像
		new DownAndShowImageTask(list.get(postion).get("faceimg"),
				viewHolder.icon).execute();

		if (creater_id != null && !creater_id.equals("")) {
			viewHolder.tichu.setVisibility(View.GONE);
			if (list.get(postion).get("id").equals(creater_id)) {
				viewHolder.creater_icon.setVisibility(View.VISIBLE);
				viewHolder.creater_icon.setText("发起人");
			} else {
				viewHolder.creater_icon.setVisibility(View.GONE);
			}
		} else {

			// 判断自己是不是队长
			final int me = AppConfig.getInstance().getPlayerId();
			final int memberId = Integer.parseInt(list.get(postion).get("id"));
			boolean isCaption = (this.relation == 2);

			if (list.get(postion).get("role") != null
					&& !list.get(postion).get("role").equals("")) {
				viewHolder.creater_icon.setVisibility(View.VISIBLE);
				viewHolder.creater_icon.setText(list.get(postion).get("role"));
				viewHolder.tichu.setVisibility(View.GONE);

			} else {
				if (isCaption) {
					viewHolder.tichu.setVisibility(View.VISIBLE);
					viewHolder.tichu
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {
									logi("onClick:postion=" + postion
											+ ",memberId=" + memberId);
									Map<String, String> parameters = new HashMap<String, String>();
									String action = "1051";
									parameters.put("app_action", action);
									parameters.put("app_platform", "0");
									parameters
											.put("u_player_id", memberId + "");
									parameters.put("u_uid", me + "");
									doPullDate(action,
											new WebRequestUtilListener() {
												@Override
												public void onSucces(
														Map<String, Object> data) {
													// TODO Auto-generated
													// method stub
													Toast.makeText(
															context,
															"onSucces"
																	+ data.get("result_code"),
															0).show();
													list.remove(memberId);
													notifyDataSetChanged();

												}

												@Override
												public void onFail(
														Map<String, Object> data) {
													// TODO Auto-generated
													// method stub
													Toast.makeText(
															context,
															"onFail"
																	+ data.get("message"),
															0).show();
												}

												@Override
												public void onError() {
													// TODO Auto-generated
													// method stub
												}
											}, parameters);

								}

							});
				}

			}

		}

		String team_name = list.get(postion).get("team_name");
		if (team_name != null && !team_name.equals("")) {
			viewHolder.team_name.setText(team_name);
		}

		String team_icon = list.get(postion).get("team_icon");
		if (team_icon != null && !team_icon.equals("")) {
			new DownAndShowImageTask(team_icon, viewHolder.team_icon).execute();
		}

		return view;
	}

	private void doPullDate(String action, WebRequestUtilListener listen,
			Map<String, String> parameters) {

		HttpPostUtil mHttpPostUtil = null;
		try {
			AppConfig.getInstance().addSign(parameters);
			mHttpPostUtil = AppConfig.getInstance()
					.makeHttpPostUtil(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// WEB request and deal the listener
		new WebRequestUtil(mHttpPostUtil).executeWithOutCache(listen);

	}

	class ViewHolder {
		TextView name;
		Button sex_age;
		Button place;
		Button tichu;
		ImageView icon;
		CircleImageView team_icon;
		TextView team_name;
		Button creater_icon;

		public ViewHolder(View view) {
			name = (TextView) view.findViewById(R.id.abc_player_name);
			/*
			 * sex_age = (Button) view.findViewById(R.id.abc_btn_sex_age); place
			 * = (Button) view.findViewById(R.id.abc_place);
			 */
			icon = (ImageView) view.findViewById(R.id.abc_face);

			team_icon = (CircleImageView) view.findViewById(R.id.abc_team_icon);
			team_name = (TextView) view.findViewById(R.id.abc_team_name);

			creater_icon = (Button) view.findViewById(R.id.creater_icon);
			// tichu = (Button) view.findViewById(R.id.creater_tichu);
		}
	}

	public interface RemoveItem {
		public void remove();
	}

}
