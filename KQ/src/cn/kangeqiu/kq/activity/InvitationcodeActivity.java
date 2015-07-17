package cn.kangeqiu.kq.activity;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

public class InvitationcodeActivity extends AgentActivity {
	private EditText edit;

	// private Button
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invitationcode_activity);
		initview();
	}

	public void back(View view) {
		finish();
	}

	private void initview() {
		edit = (EditText) findViewById(R.id.edit);

	}

	public void onAdd(View v) {
		if (edit.getText().toString() == null
				|| edit.getText().toString().equals(""))
			Toast.makeText(InvitationcodeActivity.this, "请输入房间邀请码",
					Toast.LENGTH_SHORT).show();
		else {
			CPorgressDialog.showProgressDialog(this);
			doPullDate(false, "2044", new MCHttpCallBack() {

				@Override
				public void onSuccess(MCHttpResp resp) {
					super.onSuccess(resp);
					CPorgressDialog.hideProgressDialog();
					try {
						String resultCode = resp.getJson().getString(
								"result_code");
						if (resultCode.equals("0")) {
							Toast.makeText(InvitationcodeActivity.this,
									"成功加入房间", Toast.LENGTH_SHORT).show();
							MobclickAgent.onEvent(InvitationcodeActivity.this,
									"match_room");
							TCAgent.onEvent(InvitationcodeActivity.this,
									"room_creat");
							// 进入聊天页面
							Intent intent = new Intent(
									InvitationcodeActivity.this,
									ChatActivity.class);
							// it is group chat
							intent.putExtra("chatType",
									ChatActivity.CHATTYPE_GROUP);
							intent.putExtra("groupId", resp.getJson()
									.getString("room_huanxin_id"));
							intent.putExtra("roomId",
									resp.getJson().getString("room_id"));
							InvitationcodeActivity.this.startActivity(intent);
							InvitationcodeActivity.this.finish();
						} else {
							Toast.makeText(InvitationcodeActivity.this,
									resp.getJson().getString("message"),
									Toast.LENGTH_SHORT).show();
						}

					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(
								getApplicationContext(),
								getResources().getString(
										R.string.Exit_the_group_chat_failure)
										+ " " + e.getMessage(), 1).show();
					}
				}
			});
		}
	}

	private void doPullDate(boolean isLazyLoad, String action,
			MCHttpCallBack listen) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("app_version", UpdataUtil
				.getAppVersion(this)));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_code", edit.getText().toString()));

		// pair.add(new BasicNameValuePair("u_room_id", roomId));

		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}
}
