package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.nowagme.football.AppConfig;
import com.nowagme.util.WebRequestUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMatchHourse extends BaseAdapter {
	private LayoutInflater inflater;
	private List<JSONObject> records = new ArrayList<JSONObject>();
	private OneHolder oneHolder;
	String state = "", id = "";
	/**
	 * 上下文对象
	 */
	private Activity context = null;

	public AdapterMatchHourse(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();

	}

	public void setItem(List<JSONObject> records) {
		if (records == null)
			return;
		this.records = records;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return records == null ? 0 : records.size();
	}

	@Override
	public Object getItem(int position) {
		try {
			return records.get(position);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		oneHolder = new OneHolder();

		if (view == null) {
			view = inflater.inflate(R.layout.abc_activity_match_house_item,
					null);
			oneHolder.hourseName = (TextView) view
					.findViewById(R.id.houseName_textView);
			oneHolder.matchName = (TextView) view
					.findViewById(R.id.matchName_textView);
			oneHolder.creater_img = (CircleImageView) view
					.findViewById(R.id.creater_img);
			oneHolder.createrName = (TextView) view
					.findViewById(R.id.createPerson_textView);
			oneHolder.joinedNum = (TextView) view
					.findViewById(R.id.joined_textView);
			oneHolder.ll_Joiners = (LinearLayout) view
					.findViewById(R.id.ll_joiners);
			oneHolder.unread_msg_number = (TextView) view
					.findViewById(R.id.unread_msg_number);
			view.setTag(oneHolder);

		} else {
			oneHolder = (OneHolder) view.getTag();
		}
		try {
			oneHolder.hourseName.setText(records.get(position)
					.getString("name"));
			oneHolder.joinedNum.setText(records.get(position).getString(
					"member_count")
					+ "人");

			JSONObject creater = records.get(position).getJSONObject("creator");
			oneHolder.createrName.setText(creater.getString("nickname"));
			// new DownAndShowImageTask(creater.getString("icon"),
			// oneHolder.creater_img).execute();

			JSONObject match = records.get(position).getJSONObject("match");
			oneHolder.matchName
					.setText(match.getJSONObject("team1").getString("name")
							+ "  vs  "
							+ match.getJSONObject("team2").getString("name"));

			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(
							records.get(position).getString("huanxin_id"));
			if (conversation.getUnreadMsgCount() > 0) {
				// 显示与此用户的消息未读数
				oneHolder.unread_msg_number.setText(String.valueOf(conversation
						.getUnreadMsgCount()));
				oneHolder.unread_msg_number.setVisibility(View.VISIBLE);
			} else {
				oneHolder.unread_msg_number.setVisibility(View.INVISIBLE);
			}

			// oneHolder.ll_Joiners.removeAllViews();
			// for (int i = 0; i < records.getJSONObject(position)
			// .getJSONArray("members").length(); i++) {
			//
			// CircleImageView header = new CircleImageView(context);
			// header.setLayoutParams(new LayoutParams(50, 50));
			//
			// new DownAndShowImageTask(records.getJSONObject(position)
			// .getJSONArray("members").getJSONObject(i)
			// .getString("icon"), header).execute();
			//
			// oneHolder.ll_Joiners.addView(header);
			// }
			// new
			// DownAndShowImageTask(records.getJSONObject(position).getString(
			// "icon"), oneHolder.logo).execute();
			// oneHolder.name.setText(records.getJSONObject(position).getString(
			// "nickname"));
			//
			// state = records.getJSONObject(position).getString("state");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return view;
	}

	private void doPullDate(int position, String action, MCHttpCallBack listen) {

		try {
			ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
			pair.add(new BasicNameValuePair("app_action", action));
			pair.add(new BasicNameValuePair("app_platform", "0"));
			pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
					.getPlayerId() + ""));
			pair.add(new BasicNameValuePair("u_user_id", records.get(position)
					.getString("id")));
			new WebRequestUtil().execute(false, AppConfig.getInstance()
					.makeUrl(Integer.parseInt("2025")), pair, listen);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class OneHolder {
		private TextView hourseName;
		private TextView matchName;
		private CircleImageView creater_img;
		private TextView createrName;
		private TextView joinedNum;
		private LinearLayout ll_Joiners;
		private TextView unread_msg_number;
	}

}
