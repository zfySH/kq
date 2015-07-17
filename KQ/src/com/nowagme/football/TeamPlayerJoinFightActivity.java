package com.nowagme.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.adapter.TeamFightMessageAdapter;

import com.easemob.applib.model.MessageItemModel;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.JsonUtil;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;

public class TeamPlayerJoinFightActivity extends BaseSimpleActivity {
	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["
			+ TeamAcceptFightActivity.class.getName() + "]";
	private int from;
	private String key;
	private String msgId;
	private boolean progressShow;

	private LinearLayout bottom_lay, join_lay, input_score_lay, confirm_lay;
	private Button back, commitBtn, confirmBtn, refuseBtn, scoreManager1,
			scoreManager2;
	private String match_id = "";
	private String msg_id = "";
	private String captain_team_id = "";
	private String team_id1 = "";
	private String team_id2 = "";
	// 自己球队得分
	private String scoreMy = "";
	// 对方球队得分
	private String scoreOther = "";

	private EditText score1, score2;
	private TextView team_name1, team_name2, title;
	private ImageView team_icon1, team_icon2;
	List<MessageItemModel> message = new ArrayList<MessageItemModel>();
	private ListView list;
	private TeamFightMessageAdapter adapter;

	private Map<String, Object> data = new HashMap<String, Object>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_fight_message);
		initView();

		// 获取访问来源信息
		Bundle extras = this.getIntent().getExtras();
		String type = extras.getString("type");
		if (type.equals("msg")) {
			this.from = extras.getInt(AppConfig.FROM_PARAM_NAME);
			this.key = extras.getString(AppConfig.FROM_PARAM_KEY);
			logi(AppConfig.FROM_PARAM_NAME + "=" + from + ","
					+ AppConfig.FROM_PARAM_KEY + "=" + key);
			// 根据来源重新初始化界面
			initViewByFrom();
			// 设置duel_id的值
			setDuelId();
		} else if (type.equals("team")) {
			this.match_id = String.valueOf(extras.getInt("matchId"));
			hideAction();
		}
		// 加载数据
		initData();
		initClick();
	}

	private void initData() {
		doPullDate("1066", new WebRequestUtilListener() {

			@Override
			public void onSucces(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				TeamPlayerJoinFightActivity.this.data = data;
				// 约战编号
				String id = String.valueOf(data.get("id"));

				// 队长id
				captain_team_id = String.valueOf(data.get("captain_team_id"));
				// 发起方球队编号
				team_id1 = String.valueOf(data.get("team_id1"));
				// 发起方球队名称
				String team_name_home = String.valueOf(data.get("team_name1"));
				// 发起方球队队徽
				String team_faceimg1 = String.valueOf(data.get("team_faceimg1"));
				// 被约方球队编号
				team_id2 = String.valueOf(data.get("team_id2"));
				// 被约方球队名称
				String team_name_away = String.valueOf(data.get("team_name2"));
				// 被约方球队队徽
				String team_faceimg2 = String.valueOf(data.get("team_faceimg2"));
				// 约战发起时间
				String create_time = String.valueOf(data.get("create_time"));
				// 约战发起地址
				String addr = String.valueOf(data.get("addr"));
				// 比赛类型
				String type = String.valueOf(data.get("type"));
				// 费用形式
				String fee_kind = String.valueOf(data.get("fee_kind"));
				// 开始时间
				String start_time = String.valueOf(data.get("start_time"));
				// 结束时间
				String end_time = String.valueOf(data.get("end_time"));
				// 约战主题
				String subject = String.valueOf(data.get("subject"));
				// 联系人
				String contact_person = String.valueOf(data
						.get("contact_person"));
				// 联系电话
				String contact_telephone = String.valueOf(data
						.get("contact_telephone"));
				// 比赛规则
				String match_rule = String.valueOf(data.get("match_rule"));

				// 发起方
				List<Map<String, String>> team1 = (List<Map<String, String>>) data
						.get("team1");
				// 被约方
				List<Map<String, String>> team2 = (List<Map<String, String>>) data
						.get("team2");
				// 比分状态
				String chk_score_state = String.valueOf(data
						.get("chk_score_state"));
				// 比分－发起方
				String score = String.valueOf(data.get("score"));
				// 比分-被约方
				String agree_score = String.valueOf(data.get("agree_score"));
				// 比分填写者id
				String score_player_id = String.valueOf(data
						.get("score_player_id"));
				new DownAndShowImageTask(team_faceimg1, team_icon1).execute();
				new DownAndShowImageTask(team_faceimg2, team_icon2).execute();

				team_name1.setText(team_name_home);
				team_name2.setText(team_name_away);

				title.setText(subject);

				if (captain_team_id.equals(team_id1)
						|| captain_team_id.equals(team_id2)) {
					if (chk_score_state.equals("0")) {
						// 等待输入比分
						input_score_lay.setVisibility(View.VISIBLE);
						confirm_lay.setVisibility(View.GONE);
					} else if (chk_score_state.equals("1")) {
						// 等待确认比分
						showConfirmBtn(score_player_id, score, agree_score);

					} else if (chk_score_state.equals("2")) {
						// 比分已确认
						showScore(score, agree_score);
						input_score_lay.setVisibility(View.VISIBLE);
						if (captain_team_id.equals(team_id1)) {
							scoreManager1.setVisibility(View.VISIBLE);
							scoreManager2.setVisibility(View.GONE);
						} else if (captain_team_id.equals(team_id2)) {
							scoreManager1.setVisibility(View.GONE);
							scoreManager2.setVisibility(View.VISIBLE);
						}

						commitBtn.setVisibility(View.GONE);
						confirm_lay.setVisibility(View.GONE);
					}
				} else {
					input_score_lay.setVisibility(View.GONE);
					confirm_lay.setVisibility(View.GONE);
				}

				message.add(new MessageItemModel("0", "约战主题", subject));
				message.add(new MessageItemModel("0", "约战地点", addr));
				message.add(new MessageItemModel("0", "开赛时间", create_time));
				message.add(new MessageItemModel("0", "比赛形式", type));
				message.add(new MessageItemModel("0", "费用形式", fee_kind));
				message.add(new MessageItemModel("0", "联系人", contact_person));
				message.add(new MessageItemModel("0", "联系电话", contact_telephone));
				message.add(new MessageItemModel("0", "比赛规则", match_rule));
				message.add(new MessageItemModel("1", "我方球员", team1,
						team_faceimg1));
				message.add(new MessageItemModel("1", "对方球员", team2,
						team_faceimg2));
				// message.add(new MessageItemModel("0", "评论", ""));
				adapter.setItem(message);
			}

			@Override
			public void onFail(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(TeamPlayerJoinFightActivity.this,
						String.valueOf(data.get("message")), Toast.LENGTH_SHORT)
						.show();

			}

			@Override
			public void onError() {
				CPorgressDialog.hideProgressDialog();

			}
		});
	}

	private void showConfirmBtn(String scorePlayer, String score,
			String agreeScore) {
		String playId = String.valueOf(AppConfig.getInstance().getPlayerId());
		if (playId.equals(scorePlayer)) {
			confirm_lay.setVisibility(View.GONE);
			input_score_lay.setVisibility(View.GONE);
		} else {
			showScore(score, agreeScore);
			input_score_lay.setVisibility(View.VISIBLE);
			commitBtn.setVisibility(View.GONE);
			confirm_lay.setVisibility(View.VISIBLE);

		}
	}

	private void showScore(String score, String agreeScore) {
		score1.setText(score);
		score2.setText(agreeScore);
		score1.setEnabled(false);
		score2.setEnabled(false);
		// input_score_lay.setVisibility(View.VISIBLE);
		// commitBtn.setVisibility(View.GONE);
		// confirm_lay.setVisibility(View.VISIBLE);

	}

	private void initClick() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				TeamPlayerJoinFightActivity.this.finish();
			}
		});
		join_lay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doPullDate("1038", new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamPlayerJoinFightActivity.this,
								"报名成功", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFail(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamPlayerJoinFightActivity.this,
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
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String s1 = score1.getText().toString();
				String s2 = score2.getText().toString();
				if (s1 == null || s1.equals("") || s2 == null || s2.equals("")) {
					Toast.makeText(TeamPlayerJoinFightActivity.this, "请输入比分",
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (captain_team_id.equals(team_id1)) {
					scoreMy = s1;
					scoreOther = s2;
				} else if (captain_team_id.equals(team_id2)) {
					scoreMy = s2;
					scoreOther = s1;
				}

				doPullDate("1034", new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamPlayerJoinFightActivity.this,
								"比分提交成功", Toast.LENGTH_SHORT).show();
						finish();
					}

					@Override
					public void onFail(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamPlayerJoinFightActivity.this,
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

		confirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doPullDate("1036", new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamPlayerJoinFightActivity.this,
								"确认比分", Toast.LENGTH_SHORT).show();
						finish();
					}

					@Override
					public void onFail(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamPlayerJoinFightActivity.this,
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
		refuseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doPullDate("1035", new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamPlayerJoinFightActivity.this,
								"拒绝比分", Toast.LENGTH_SHORT).show();
						finish();
					}

					@Override
					public void onFail(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamPlayerJoinFightActivity.this,
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
		scoreManager1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// fightData
				Intent intent = new Intent();
				intent.setClass(TeamPlayerJoinFightActivity.this,
						TeamFightScoreManagerActivity.class);
				// intent.putExtra("subject",
				// String.valueOf(fightData.get("subject")));
				intent.putExtra("subject", String.valueOf(data.get("subject")));
				intent.putExtra("createTime",
						String.valueOf(data.get("create_time")));
				intent.putExtra("addr", String.valueOf(data.get("addr")));
				intent.putExtra("teamName1",
						String.valueOf(data.get("team_name1")));
				intent.putExtra("teamFaceimg1",
						String.valueOf(data.get("team_faceimg1")));
				intent.putExtra("teamName2",
						String.valueOf(data.get("team_name2")));
				intent.putExtra("teamFaceimg2",
						String.valueOf(data.get("team_faceimg2")));
				intent.putExtra("score", String.valueOf(data.get("score")));
				intent.putExtra("agreeScore",
						String.valueOf(data.get("agree_score")));
				intent.putExtra("matchId", match_id);

				TeamPlayerJoinFightActivity.this.startActivity(intent);
			}
		});
		scoreManager2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(TeamPlayerJoinFightActivity.this,
						TeamFightScoreManagerActivity.class);
				// intent.putExtra("subject",
				// String.valueOf(fightData.get("subject")));
				intent.putExtra("subject", String.valueOf(data.get("subject")));
				intent.putExtra("createTime",
						String.valueOf(data.get("create_time")));
				intent.putExtra("addr", String.valueOf(data.get("addr")));
				intent.putExtra("teamName1",
						String.valueOf(data.get("team_name1")));
				intent.putExtra("teamFaceimg1",
						String.valueOf(data.get("team_faceimg1")));
				intent.putExtra("teamName2",
						String.valueOf(data.get("team_name2")));
				intent.putExtra("teamFaceimg2",
						String.valueOf(data.get("team_faceimg2")));
				intent.putExtra("score", String.valueOf(data.get("score")));
				intent.putExtra("agreeScore",
						String.valueOf(data.get("agree_score")));
				intent.putExtra("matchId", match_id);

				TeamPlayerJoinFightActivity.this.startActivity(intent);
			}
		});
	}

	private void initView() {
		bottom_lay = (LinearLayout) findViewById(R.id.abc_person_ll_bottom);
		join_lay = (LinearLayout) findViewById(R.id.abc_person__ll_bottom_join_team);
		input_score_lay = (LinearLayout) findViewById(R.id.input_score);
		confirm_lay = (LinearLayout) findViewById(R.id.confirm_part);
		back = (Button) findViewById(R.id.back);
		team_name1 = (TextView) findViewById(R.id.team_name1);
		team_name2 = (TextView) findViewById(R.id.team_name2);
		team_icon1 = (ImageView) findViewById(R.id.team_icon1);
		team_icon2 = (ImageView) findViewById(R.id.team_icon2);
		title = (TextView) findViewById(R.id.subject);
		list = (ListView) findViewById(R.id.message_list);

		commitBtn = (Button) findViewById(R.id.commit_score);
		confirmBtn = (Button) findViewById(R.id.confirm_score);
		refuseBtn = (Button) findViewById(R.id.refuse_score);
		scoreManager1 = (Button) findViewById(R.id.score_manager1);
		scoreManager2 = (Button) findViewById(R.id.score_manager2);

		score1 = (EditText) findViewById(R.id.score1);
		score2 = (EditText) findViewById(R.id.score2);
		adapter = new TeamFightMessageAdapter(this);
		list.setAdapter(adapter);
	}

	private void setDuelId() {
		Map<String, Object> mapdata;
		try {
			mapdata = JsonUtil.parse(key);
			match_id = (String) mapdata.get("match_id");
			msg_id = (String) mapdata.get("msg_id");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据来源重新初始化界面
	 */
	private void initViewByFrom() {
		if (from == AppConfig.FROM_NONE_MESSAGE) {// 非消息
			hideAction();
		}
		if (from == BeanSysMsg.TYPE_MAKE_DUEL) {// 消息:发起约战
			// 判断是否要显示操作按钮:根据消息ID去服务器上获取该消息的最新状态，如果状态为已处理，则不能再进行操作
			doShowOrHideAction();
		}
		if (from == BeanSysMsg.TYPE_ENROLL_DUEL) {// 消息：报名约战
			doShowOrHideAction();
		}
		if (from == BeanSysMsg.TYPE_CONFIRM_DUEL) {// 消息：确认分数
			hideAction();
		}
		if (from == BeanSysMsg.TYPE_ACCEPT_DUEL) {// 消息：同意分数
			hideAction();
		}
		if (from == BeanSysMsg.TYPE_REFUSE_DUEL) {// 消息：拒绝分数
			hideAction();
		}
	}

	/**
	 * 显示或隐藏接受或拒绝约战的操作.
	 */
	public void doShowOrHideAction() {
		logi("doShowOrHideAction()");

		try {
			Map<String, Object> mapdata = JsonUtil.parse(key);
			msgId = (String) mapdata.get("msg_id");

			progressShow = true;
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setCanceledOnTouchOutside(false);
			pd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					progressShow = false;
				}
			});
			pd.setMessage(getString(R.string.abc_data_loding));
			pd.show();

			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("app_action", "1063");
			parameters.put("app_platform", "0");
			parameters.put("u_uid",
					String.valueOf(AppConfig.getInstance().getPlayerId()));
			parameters.put("u_msg_id", msgId);
			HttpPostUtil mHttpPostUtil = null;
			try {
				AppConfig.getInstance().addSign(parameters);
				mHttpPostUtil = AppConfig.getInstance().makeHttpPostUtil(
						parameters);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// WEB request and deal the listener
			new WebRequestUtil(mHttpPostUtil)
					.executeWithOutCache(new WebRequestUtilListener() {

						@Override
						public void onSucces(Map<String, Object> data) {
							pd.dismiss();
							int state = Integer.parseInt((String) data
									.get("state"));
							switch (state) {
							case BeanSysMsg.STATE_DONE:// 消息已经处理过了
								hideAction();
								break;
							default:
								showAction();
								break;
							}

						}

						@Override
						public void onFail(Map<String, Object> data) {
							logi("onFail()");
							pd.dismiss();
							Toast.makeText(TeamPlayerJoinFightActivity.this,
									"关注失败:" + data.get("message"),
									Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onError() {
							logi("onError()");
							pd.dismiss();
						}
					});

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 显示同意或拒绝约战操作按钮.
	 */
	private void showAction() {
		bottom_lay.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏同意或拒绝约战操作按钮.
	 */
	private void hideAction() {
		bottom_lay.setVisibility(View.GONE);

	}

	/**
	 * log information
	 * 
	 * @param msg
	 */
	public static void logi(String msg) {
		Log.i(TAG, CLASS_NAME + " " + msg);
	}

	private void doPullDate(String action, WebRequestUtilListener listen) {
		CPorgressDialog.showProgressDialog(this);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		if (action.equals("1066") || action.equals("1035")
				|| action.equals("1036")) {
			parameters.put("u_match_id", match_id);

		}
		if (action.equals("1034")) {
			parameters.put("u_match_id", match_id);
			parameters.put("u_score", scoreMy);
			parameters.put("u_other_score", scoreOther);

		}
		if (action.equals("1038")) {
			parameters.put("u_match_id", match_id);
			parameters.put("u_msg_id", msg_id);

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
}
