package com.nowagme.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.JsonUtil;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;

public class TeamAcceptFightActivity extends BaseSimpleActivity {

	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["
			+ TeamAcceptFightActivity.class.getName() + "]";
	private ListView list;
	private TeamFightMessageAdapter adapter;
	private String duel_id = "";

	private Context context;
	private int from;
	private String key;
	@SuppressWarnings("unused")
	private boolean progressShow;

	List<MessageItemModel> message = new ArrayList<MessageItemModel>();
	private ImageView icon;
	private TextView team_name, team_id, title;
	private LinearLayout refuse_lay, agree_lay, bottom_lay;
	private Button back;
	private String msgId;
	private ImagerLoader loader = new ImagerLoader();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_accept_fight_team);
		context = this;
		list = (ListView) findViewById(R.id.message_list);
		icon = (ImageView) findViewById(R.id.team_icon);
		team_name = (TextView) findViewById(R.id.team_name);
		team_id = (TextView) findViewById(R.id.team_id);
		title = (TextView) findViewById(R.id.subject);
		refuse_lay = (LinearLayout) findViewById(R.id.abc_person__ll_bottom_refuse_fight);
		agree_lay = (LinearLayout) findViewById(R.id.abc_person__ll_bottom_agree_fight);
		bottom_lay = (LinearLayout) findViewById(R.id.abc_person_ll_bottom);
		back = (Button) findViewById(R.id.back);

		adapter = new TeamFightMessageAdapter(this);
		list.setAdapter(adapter);

		// 获取访问来源信息
		Bundle extras = this.getIntent().getExtras();
		this.from = extras.getInt(AppConfig.FROM_PARAM_NAME);
		this.key = extras.getString(AppConfig.FROM_PARAM_KEY);
		logi(AppConfig.FROM_PARAM_NAME + "=" + from + ","
				+ AppConfig.FROM_PARAM_KEY + "=" + key);

		// 根据来源重新初始化界面
		initViewByFrom();

		// 设置duel_id的值
		setDuelId();

		initData();

		initClick();
	}

	private void initClick() {
		refuse_lay.setClickable(true);
		agree_lay.setClickable(true);

		refuse_lay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doPullDate("1028", new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamAcceptFightActivity.this, "拒绝约战",
								Toast.LENGTH_SHORT).show();
						TeamAcceptFightActivity.this.finish();
					}

					@Override
					public void onFail(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamAcceptFightActivity.this,
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
		agree_lay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doPullDate("1027", new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamAcceptFightActivity.this, "接受约战",
								Toast.LENGTH_SHORT).show();
						TeamAcceptFightActivity.this.finish();
					}

					@Override
					public void onFail(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(TeamAcceptFightActivity.this,
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
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void setDuelId() {
		Map<String, Object> mapdata;
		try {
			mapdata = JsonUtil.parse(key);
			duel_id = (String) mapdata.get("duel_id");
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
			final ProgressDialog pd = new ProgressDialog(context);
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
							Toast.makeText(context,
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

	private void initData() {
		doPullDate("1064", new WebRequestUtilListener() {

			@Override
			public void onSucces(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				// 约战编号
				String id = String.valueOf(data.get("id"));
				// 发起方球队编号
				String team_id1 = String.valueOf(data.get("team_id1"));
				// 发起方球队名称
				String team_name1 = String.valueOf(data.get("team_name1"));
				// 发起方球队队徽
				String team_faceimg1 = String.valueOf(data.get("team_faceimg1"));
				// 被约方球队编号
				String team_id2 = String.valueOf(data.get("team_id2"));
				// 被约方球队名称
				String team_name2 = String.valueOf(data.get("team_name2"));
				// 被约方球队队徽
				String team_faceimg2 = String.valueOf(data.get("team_faceimg2"));
				// 约战发起时间
				String create_time = String.valueOf(data.get("create_time"));
				// 约战发起时间
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

				// new DownAndShowImageTask(team_faceimg1, icon).execute();
				loader.LoadImage(team_faceimg1, icon);

				team_name.setText(team_name1);
				team_id.setText("球队ID：" + team_id1);
				title.setText(subject);

				message.add(new MessageItemModel("0", "约战主题", subject));
				message.add(new MessageItemModel("0", "约战地点", ""));
				message.add(new MessageItemModel("0", "开赛时间", create_time));
				message.add(new MessageItemModel("0", "比赛形式", type));
				message.add(new MessageItemModel("0", "费用形式", fee_kind));
				message.add(new MessageItemModel("0", "联系人", contact_person));
				message.add(new MessageItemModel("0", "联系电话", contact_telephone));
				message.add(new MessageItemModel("0", "比赛规则", match_rule));
				// message.add(new MessageItemModel("0", "我方球员", ""));
				// message.add(new MessageItemModel("0", "对方球员", ""));
				// message.add(new MessageItemModel("0", "评论", ""));
				adapter.setItem(message);
			}

			@Override
			public void onFail(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(TeamAcceptFightActivity.this,
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
		if (action.equals("1064")) {
			parameters.put("u_duel_id", duel_id);

		}
		if (action.equals("1028") || action.equals("1027")) {
			parameters.put("u_duel_id", duel_id);
			parameters.put("u_msg_id", msgId);

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
