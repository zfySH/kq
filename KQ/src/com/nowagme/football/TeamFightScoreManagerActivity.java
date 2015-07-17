package com.nowagme.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.TeamFightScoreView;

import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;

public class TeamFightScoreManagerActivity extends BaseSimpleActivity {
	private TextView name, time, address, team_name1, team_name2, score_tx;
	private ImageView team_icon1, team_icon2;
	private Map<String, Object> fightData = new HashMap<String, Object>();

	// private ListView playerList;
	// private TeamFightScoreManagerAdapter adapter;
	private String match_id = "";
	private List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
	private LinearLayout player_lay;

	private List<TeamFightScoreView> scoreList = new ArrayList<TeamFightScoreView>();

	private Button back, save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_fight_score_manager);

		setMatchId();
		initView();
		initData();
		initClick();
	}

	private void initClick() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String msg = chargeMsg();
				if (msg != null) {
					Toast.makeText(TeamFightScoreManagerActivity.this, msg,
							Toast.LENGTH_SHORT).show();
					return;
				}

				doPullDate("1037", new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamFightScoreManagerActivity.this,
								"保存成功", Toast.LENGTH_SHORT).show();
						TeamFightScoreManagerActivity.this.finish();
					}

					@Override
					public void onFail(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamFightScoreManagerActivity.this,
								String.valueOf(data.get("message")),
								Toast.LENGTH_SHORT).show();

					}

					@Override
					public void onError() {
						CPorgressDialog.hideProgressDialog();

					}

				});
			}

		});
	}

	private String chargeMsg() {
		for (int i = 0; i < scoreList.size(); i++) {
			if (scoreList.get(i).chargeMsg() != null) {
				return scoreList.get(i).chargeMsg();

			}
		}
		return null;
	}

	private void initData() {
		doPullDate("1072", new WebRequestUtilListener() {

			@Override
			public void onSucces(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				dataList = (List<Map<String, String>>) data.get("players");
				// adapter.setItem(dataList);
				for (int i = 0; i < dataList.size(); i++) {
					TeamFightScoreView view = new TeamFightScoreView(
							TeamFightScoreManagerActivity.this);
					player_lay.addView(view.getView(dataList.get(i)));
					scoreList.add(view);
				}
			}

			@Override
			public void onFail(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(TeamFightScoreManagerActivity.this,
						String.valueOf(data.get("message")), Toast.LENGTH_SHORT)
						.show();

			}

			@Override
			public void onError() {
				CPorgressDialog.hideProgressDialog();

			}
		});
	}

	private void doPullDate(String action, WebRequestUtilListener listen) {
		CPorgressDialog.showProgressDialog(this);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		parameters.put("u_match_id", match_id);

		if (action.equals("1037")) {
			String msg = "";
			for (int i = 0; i < scoreList.size(); i++) {
				if (scoreList.get(i).getMsg() != null) {
					msg += scoreList.get(i).getMsg();
				}
			}

			parameters.put("u_info", msg.substring(0, msg.length() - 1));

		}
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

	private void setMatchId() {
		match_id = getIntent().getStringExtra("matchId");
	}

	private void initView() {
		// adapter = new TeamFightScoreManagerAdapter(this);
		String subject = getIntent().getStringExtra("subject");
		String createTime = getIntent().getStringExtra("createTime");
		String addr = getIntent().getStringExtra("addr");
		String teamName1 = getIntent().getStringExtra("teamName1");
		String teamFaceimg1 = getIntent().getStringExtra("teamFaceimg1");
		String teamName2 = getIntent().getStringExtra("teamName2");
		String teamFaceimg2 = getIntent().getStringExtra("teamFaceimg2");
		String score = getIntent().getStringExtra("score");
		String agreeScore = getIntent().getStringExtra("agreeScore");

		// playerList = (ListView) findViewById(R.id.player_list);
		name = (TextView) findViewById(R.id.name);
		time = (TextView) findViewById(R.id.time);
		address = (TextView) findViewById(R.id.address);
		team_name1 = (TextView) findViewById(R.id.team_name1);
		team_name2 = (TextView) findViewById(R.id.team_name2);
		team_icon1 = (ImageView) findViewById(R.id.team_icon1);
		team_icon2 = (ImageView) findViewById(R.id.team_icon2);
		score_tx = (TextView) findViewById(R.id.score);
		player_lay = (LinearLayout) findViewById(R.id.player_message);
		back = (Button) findViewById(R.id.back);
		save = (Button) findViewById(R.id.save);

		// playerList.setAdapter(adapter);
		name.setText(subject);
		time.setText(createTime);
		address.setText(addr);
		team_name1.setText(teamName1);
		team_name2.setText(teamName2);
		new DownAndShowImageTask(teamFaceimg1, team_icon1).execute();
		new DownAndShowImageTask(teamFaceimg2, team_icon2).execute();
		score_tx.setText(score + ":" + agreeScore);
	}
}
