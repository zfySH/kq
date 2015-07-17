package cn.kangeqiu.kq.activity.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.ChatActivity;

import com.easemob.chat.EMGroupManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchHourseView {
	private Activity context;
	private LayoutInflater inflater;

	public MatchHourseView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(final JSONObject matchs, String matchTitle)
			throws JSONException {
		View view = null;
		view = inflater.inflate(R.layout.abc_activity_match_house_item, null);
		TextView hourseName = (TextView) view
				.findViewById(R.id.houseName_textView);
		TextView matchName = (TextView) view
				.findViewById(R.id.matchName_textView);
		CircleImageView creater_img = (CircleImageView) view
				.findViewById(R.id.creater_img);
		TextView createrName = (TextView) view
				.findViewById(R.id.createPerson_textView);
		TextView joinedNum = (TextView) view.findViewById(R.id.joined_textView);
		LinearLayout ll_Joiners = (LinearLayout) view
				.findViewById(R.id.ll_joiners);
		LinearLayout ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
		// View arron = (View) view.findViewById(R.id.match_arron);
		matchName.setText(matchTitle);
		hourseName.setText(matchs.getString("name"));
		joinedNum.setText(matchs.getString("member_count") + " 人");

		JSONObject creater = matchs.getJSONObject("creator");
		createrName.setText(creater.getString("nickname"));
		// new DownAndShowImageTask(creater.getString("icon"), creater_img)
		// .execute();
		ll_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try {
					if (EMGroupManager.getInstance().getGroup(
							matchs.getString("huanxin_id")) == null) {
						Toast.makeText(context, "您还不是该房间的成员，请联系房间管理员",
								Toast.LENGTH_SHORT).show();
					} else {
						MobclickAgent.onEvent(context, "match_room");
						TCAgent.onEvent(context, "match_room");

						// 进入聊天页面
						Intent intent = new Intent(context, ChatActivity.class);
						// it is group chat
						intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
						intent.putExtra("groupId",
								matchs.getString("huanxin_id"));
						intent.putExtra("roomId", matchs.getString("id"));
						context.startActivity(intent);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		return view;
	}
}
