package com.nowagame.kq.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.ChatActivity;
import cn.kangeqiu.kq.adapter.MemberChooseAdapter;
import cn.kangeqiu.kq.model.MemberInfoModel;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;
import com.umeng.analytics.MobclickAgent;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreatMyHouseActivity extends AgentActivity implements
		OnClickListener {

	private RelativeLayout aboutMatch_Rel, rl_create;
	private EditText creatHouseName_EditText;
	private GridView noScrollgridview;
	private CircleImageView team_icon1, team_icon2;
	private TextView team_name1, team_name2, all_title, txt_score1, txt_score2,
			channel;
	private LinearLayout show_score;

	private List<MemberInfoModel> memberList = new ArrayList<MemberInfoModel>();
	private MemberChooseAdapter adapter;
	private String createrIcon;
	private String matchId = "";
	private String memberIds = "";
	private ImagerLoader loader = new ImagerLoader();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_creathouse);

		init();
		initHandle();
		initData();
		AddOnClickListener();
	}

	private void initHandle() {
		String match = getIntent().getStringExtra("match");
		if (match != null && !match.equals(""))
			initMatch(match);
	}

	private void initData() {
		doPullDate(true, "2030", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);

				try {
					memberList.clear();
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						createrIcon = resp.getJson().getString("icon");
						initCreaterAdd();
						initAddRemove(true);
						adapter.setItems(memberList, 0, 0);
					} else {
						Toast.makeText(CreatMyHouseActivity.this,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				Toast.makeText(CreatMyHouseActivity.this,
						resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void doPullDate(boolean isLazyLoad, String action,
			MCHttpCallBack listen) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		if (action.equals("2033")) {
			pair.add(new BasicNameValuePair("app_version", UpdataUtil
					.getAppVersion(this)));
			pair.add(new BasicNameValuePair("u_match_id", matchId));
			pair.add(new BasicNameValuePair("u_name", creatHouseName_EditText
					.getText().toString()));
			pair.add(new BasicNameValuePair("u_member_ids", adapter.getId()));

		}
		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

	public void back(View view) {
		finish();
	}

	private void initCreaterAdd() {
		// 创建者
		MemberInfoModel member = new MemberInfoModel();
		member.setIcon(createrIcon);
		member.setType(0);
		memberList.add(member);

	}

	private void initAddRemove(boolean isAdd) {
		// add
		MemberInfoModel add = new MemberInfoModel();
		add.setIcon(String.valueOf(R.drawable.add_people));
		add.setType(2);
		memberList.add(add);
		// remove
		if (!isAdd) {
			MemberInfoModel remove = new MemberInfoModel();
			remove.setIcon(String.valueOf(R.drawable.reduce_people));
			remove.setType(3);
			memberList.add(remove);
		}

	}

	private void canEnter() {
		rl_create.setClickable(true);
		rl_create
				.setBackgroundResource(R.drawable.abc_button_roundcorner_create);
	}

	private void canNotEnter() {
		rl_create.setClickable(false);
		rl_create
				.setBackgroundResource(R.drawable.abc_button_roundcorner_uncreate);
	}

	private void init() {
		rl_create = (RelativeLayout) findViewById(R.id.rl_create);
		aboutMatch_Rel = (RelativeLayout) findViewById(R.id.aboutMatch_Rel);
		creatHouseName_EditText = (EditText) findViewById(R.id.creatHouseName_EditText);
		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		team_icon1 = (CircleImageView) findViewById(R.id.team_icon1);
		team_icon2 = (CircleImageView) findViewById(R.id.team_icon2);
		team_name1 = (TextView) findViewById(R.id.team_name1);
		team_name2 = (TextView) findViewById(R.id.team_name2);
		all_title = (TextView) findViewById(R.id.all_title);
		txt_score1 = (TextView) findViewById(R.id.txt_score1);
		txt_score2 = (TextView) findViewById(R.id.txt_score2);
		channel = (TextView) findViewById(R.id.channel);
		show_score = (LinearLayout) findViewById(R.id.show_score);

		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new MemberChooseAdapter(this);
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (memberList.get(position).getType() == 2) {// 增加成员
					Intent intent = new Intent();
					intent.setClass(CreatMyHouseActivity.this,
							HourseMemberChooseActivity.class);
					CreatMyHouseActivity.this.startActivityForResult(intent, 0);
				} else if (memberList.get(position).getType() == 3) {
					adapter.setItems(memberList, 1, 1);
				}
			}
		});
		creatHouseName_EditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				String content = creatHouseName_EditText.getText().toString();
				if (content.length() >= 4 && !matchId.equals("")) {
					canEnter();
				} else {
					canNotEnter();
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		rl_create.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// CPorgressDialog.showProgressDialog(CreatMyHouseActivity.this);
				doPullDate(true, "2033", new MCHttpCallBack() {
					@Override
					public void onSuccess(MCHttpResp resp) {
						super.onSuccess(resp);

						try {
							memberList.clear();
							String resultCode = resp.getJson().getString(
									"result_code");
							if (resultCode.equals("0")) {
								Toast.makeText(CreatMyHouseActivity.this,
										"创建成功", Toast.LENGTH_SHORT).show();
								MobclickAgent
										.onEvent(CreatMyHouseActivity.this,
												"room_done");

								// 进入聊天页面
								Intent intent = new Intent(
										CreatMyHouseActivity.this,
										ChatActivity.class);
								// it is group chat
								intent.putExtra("chatType",
										ChatActivity.CHATTYPE_GROUP);
								intent.putExtra("groupId", resp.getJson()
										.getString("room_huanxin_id"));
								intent.putExtra("roomId", resp.getJson()
										.getString("room_id"));
								CreatMyHouseActivity.this.startActivity(intent);
								CreatMyHouseActivity.this.finish();
							} else {
								Toast.makeText(CreatMyHouseActivity.this,
										resp.getJson().getString("message"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onError(MCHttpResp resp) {
						super.onError(resp);
						Toast.makeText(CreatMyHouseActivity.this,
								resp.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				});
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 20:
			String icons = data.getStringExtra("icon");
			memberIds = data.getStringExtra("id");

			memberList.clear();
			initCreaterAdd();// 只有创建者头像
			for (int i = 0; i < icons.split(",").length; i++) {
				// 普通成员
				MemberInfoModel member = new MemberInfoModel();
				member.setIcon(String.valueOf(icons.split(",")[i]));
				member.setId(String.valueOf(memberIds.split(",")[i]));
				member.setType(1);
				memberList.add(member);
			}
			// if (icons.split(",").length > 0)
			initAddRemove(false);// 操作按钮添加
			// else
			// initAddRemove(true);// 操作按钮添加
			adapter.setItems(memberList, 1, 0);

			break;
		case 30:
			String match = data.getStringExtra("match");
			initMatch(match);
			break;
		}
	}

	private void initMatch(String match) {
		try {

			JSONObject matchJson = new JSONObject(match);

			show_score.setVisibility(View.VISIBLE);

			loader.LoadImage(
					matchJson.getJSONObject("team1").getString("icon"),
					team_icon1);
			loader.LoadImage(
					matchJson.getJSONObject("team2").getString("icon"),
					team_icon2);
			// new DownAndShowImageTask(matchJson.getJSONObject("team1")
			// .getString("icon"), team_icon1).execute();
			// new DownAndShowImageTask(matchJson.getJSONObject("team2")
			// .getString("icon"), team_icon2).execute();

			team_name1.setText(matchJson.getJSONObject("team1").getString(
					"name"));
			team_name2.setText(matchJson.getJSONObject("team2").getString(
					"name"));
			all_title.setText(matchJson.getString("name"));
			txt_score1.setText(matchJson.getJSONObject("team1").getString(
					"score"));
			txt_score2.setText(matchJson.getJSONObject("team2").getString(
					"score"));
			channel.setText(matchJson.getString("channel"));

			matchId = matchJson.getString("id");
			String content = creatHouseName_EditText.getText().toString();
			if (content.length() >= 4 && !matchId.equals("")) {
				canEnter();
			} else {
				canNotEnter();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void AddOnClickListener() {
		aboutMatch_Rel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// 相关比赛
		if (v == aboutMatch_Rel) {
			startActivityForResult(new Intent(this, AboutMatchActivity.class),
					0);
		}
	}

}
