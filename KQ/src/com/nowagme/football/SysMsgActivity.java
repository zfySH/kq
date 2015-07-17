package com.nowagme.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;

import com.nowagme.football.AdapterSysMsg.OneHolder;
import com.nowagme.football.AdapterSysMsg.TwoHolder;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.JsonUtil;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;

public class SysMsgActivity extends BaseSimpleActivity implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener {
	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["
			+ SysMsgActivity.class.getName() + "]";

	private static final int REQUEST_CODE_PERSON_ATTENTION = 1;
	private static final int REQUEST_CODE_PERSON_JOIN_TEAM = 2;
	private static final int REQUEST_CODE_PERSON_MAKE_DUEL = 3;
	private static final int REQUEST_CODE_PERSON_ENROLL_DUEL = 4;
	private static final int REQUEST_CODE_TEAM_TRANNING = 5;

	private static final int CONTEXT_MENU_DELETE = 0;// 长按菜单－删除

	private ListView lv_listview;
	// private Button btn_back;
	private AdapterSysMsg adapter = null;
	private TextView abc_sys_msg__tv_title;

	private Context context;
	@SuppressWarnings("unused")
	private boolean progressShow;

	private String type = "0";
	private int page = 1;
	private List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		logi("onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_sys_msg);
		context = this;

		initHandle();
		// 初始化视图
		initView();

		// 初始化控件
		initData();

	}

	private void initHandle() {
		type = getIntent().getStringExtra("type");

	}

	/**
	 * 初始化控件.
	 */
	private void initView() {
		logi("initView");
		abc_sys_msg__tv_title = (TextView) findViewById(R.id.abc_sys_msg__tv_title);
		lv_listview = (ListView) findViewById(R.id.abc_sys_msg__lv_listview);
		// btn_back = (Button) findViewById(R.id.abc_sys_msg__btn_back);

		// set on click listener
		// btn_back.setOnClickListener(this);
		lv_listview.setOnItemClickListener(this);
		lv_listview.setOnItemLongClickListener(this);
		if (type.equals("0"))
			abc_sys_msg__tv_title.setText("系统消息");
		else if (type.equals("1"))
			abc_sys_msg__tv_title.setText("评论");
		else if (type.equals("2"))
			abc_sys_msg__tv_title.setText("赞");
	}

	/**
	 * 返回按钮.
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}

	/**
	 * 初始化数据.
	 */
	private void initData() {
		logi("initData");

		// 设置 ListView列表数据
		adapter = new AdapterSysMsg(this);
		adapter.setType(type);

		lv_listview.setAdapter(adapter);

		String action = "";
		if (type.equals("0"))
			action = "2018";
		else if (type.equals("1"))
			action = "2026";
		else if (type.equals("2"))
			action = "2028";
		// 获取系统消息
		doPullData(action, new WebRequestUtilListener() {

			@Override
			public void onSucces(Map<String, Object> data) {
				logi("onSucces");
				logi(data.toString());
				// 获取全部消息

				records = (List<Map<String, Object>>) data.get("records");
				// 设置ListView适配器数据源
				adapter.setDatas(records);
				adapter.notifyDataSetChanged();
				CPorgressDialog.hideProgressDialog();
			}

			@Override
			public void onFail(Map<String, Object> data) {
				logi("onFail");
				CPorgressDialog.hideProgressDialog();
				logi(data.toString());
				Toast.makeText(context, "操作失败:" + data.get("message"),
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError() {
				logi("onError");
				CPorgressDialog.hideProgressDialog();
			}

		});
	}

	/**
	 * 获取球队信息
	 */
	private void doPullData(String action, WebRequestUtilListener listener) {
		logi("doPullData()");
		CPorgressDialog.showProgressDialog(this);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_uid",
				String.valueOf(AppConfig.getInstance().getPlayerId()));
		parameters.put("u_page", page + "");
		if (action.equals("2018")) {
			parameters.put("u_search_text", "");
			parameters.put("u_user_id",
					String.valueOf(AppConfig.getInstance().getPlayerId()));

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
		new WebRequestUtil(mHttpPostUtil).executeWithOutCache(listener);

	}

	/**
	 * log information
	 * 
	 * @param msg
	 */
	public static void logi(String msg) {
		Log.i(TAG, CLASS_NAME + " " + msg);
	}

	@Override
	public void onClick(View v) {
		logi("onClick(View v)");

		// int id = v.getId();
		// switch (id) {
		// case R.id.abc_sys_msg__btn_back:
		// finish();
		// break;
		// }
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View v, int position,
			long id) {
		logi("onItemClick");
		if (!(v.getTag() instanceof OneHolder)
				&& !(v.getTag() instanceof TwoHolder)) {
			return;
		}

		if (type.equals("0") || type.equals("2")) {
			String userId = records.get(position).get("id").toString();
			Intent intent = new Intent();
			intent.putExtra("userId", userId);
			intent.setClass(this, FragmentPersonActivity.class);
			startActivity(intent);
		} else if (type.equals("1")) {
			Map<String, String> content = (Map<String, String>) records.get(
					position).get("content");

			Intent intent = new Intent();
			intent.setClass(context, MatchCommentDetailActivity.class);
			intent.putExtra("commentid", content.get("id").toString());
			context.startActivity(intent);
		}

		// BeanSysMsg bean = this.adapter.getDatas().get(position);
		// int state = bean.getState();
		// if (state == BeanSysMsg.STATE_SENT) {
		// // 阅读未读的消息
		// doUnreadMessage(bean);
		// } else {
		// // 阅读已读的消息
		// doDealMessage(bean);
		// }

	}

	/**
	 * 处理消息
	 * 
	 * @param bean
	 */
	public void doDealMessage(final BeanSysMsg bean) {
		int type = bean.getType();
		Intent intent = null;
		Bundle bundle = null;
		Map<String, Object> mapdata = null;
		String teamId = null;
		switch (type) {
		case BeanSysMsg.TYPE_ATTENTION_PLAYER:// 关注某人
			logi("BeanSysMsg.TYPE_ATTENTION_PLAYER");
			// intent = new Intent(context, PersonActivity.class);
			// bundle = new Bundle();
			// bundle.putInt("player_id", bean.getSendor());
			// bundle.putInt(AppConfig.FROM_PARAM_NAME, bean.getType());
			// intent.putExtras(bundle);
			// startActivityForResult(intent, REQUEST_CODE_PERSON_ATTENTION);
			break;

		case BeanSysMsg.TYPE_JOIN_TEAM:// 申请加入球队
			logi("BeanSysMsg.TYPE_JOIN_TEAM");
			// intent = new Intent(context, PersonActivity.class);
			// bundle = new Bundle();
			// bundle.putInt("player_id", bean.getSendor());
			// bundle.putInt(AppConfig.FROM_PARAM_NAME, bean.getType());
			// bundle.putString(AppConfig.FROM_PARAM_KEY, bean.getKey());
			// intent.putExtras(bundle);
			// startActivityForResult(intent, REQUEST_CODE_PERSON_JOIN_TEAM);
			break;

		case BeanSysMsg.TYPE_MAKE_DUEL:// 发起约战
			logi("BeanSysMsg.TYPE_MAKE_DUEL");
			intent = new Intent(context, TeamAcceptFightActivity.class);
			bundle = new Bundle();
			bundle.putInt(AppConfig.FROM_PARAM_NAME, bean.getType());
			bundle.putString(AppConfig.FROM_PARAM_KEY, bean.getKey());
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUEST_CODE_PERSON_MAKE_DUEL);
			break;

		case BeanSysMsg.TYPE_ENROLL_DUEL:// 约战报名
			logi("BeanSysMsg.TYPE_ENROLL_DUEL");
			intent = new Intent(context, TeamPlayerJoinFightActivity.class);
			bundle = new Bundle();
			bundle.putInt(AppConfig.FROM_PARAM_NAME, bean.getType());
			bundle.putString(AppConfig.FROM_PARAM_KEY, bean.getKey());
			bundle.putString("type", "msg");
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUEST_CODE_PERSON_ENROLL_DUEL);
			break;
		case BeanSysMsg.TYPE_CONFIRM_DUEL:// 确认分数
			logi("BeanSysMsg.TYPE_CONFIRM_DUEL");
			intent = new Intent(context, TeamPlayerJoinFightActivity.class);
			bundle = new Bundle();
			bundle.putInt(AppConfig.FROM_PARAM_NAME, bean.getType());
			bundle.putString(AppConfig.FROM_PARAM_KEY, bean.getKey());
			bundle.putString("type", "msg");
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUEST_CODE_PERSON_ENROLL_DUEL);
			break;
		case BeanSysMsg.TYPE_ACCEPT_DUEL:// 同意分数
			logi("BeanSysMsg.TYPE_ACCEPT_DUEL");
			intent = new Intent(context, TeamPlayerJoinFightActivity.class);
			bundle = new Bundle();
			bundle.putInt(AppConfig.FROM_PARAM_NAME, bean.getType());
			bundle.putString(AppConfig.FROM_PARAM_KEY, bean.getKey());
			bundle.putString("type", "msg");
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUEST_CODE_PERSON_ENROLL_DUEL);
			break;
		case BeanSysMsg.TYPE_REFUSE_DUEL:// 拒绝分数
			logi("BeanSysMsg.TYPE_REFUSE_DUEL");
			intent = new Intent(context, TeamPlayerJoinFightActivity.class);
			bundle = new Bundle();
			bundle.putInt(AppConfig.FROM_PARAM_NAME, bean.getType());
			bundle.putString(AppConfig.FROM_PARAM_KEY, bean.getKey());
			bundle.putString("type", "msg");
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUEST_CODE_PERSON_ENROLL_DUEL);
			break;

		case BeanSysMsg.TYPE_TRANNING:// 收到队长发起队内训练活动的通知
		case BeanSysMsg.TYPE_DUEL_RESPONSE:// 队长响应约战后约战球队双方收到的系统通知
			logi("BeanSysMsg.TYPE_TRANNING|BeanSysMsg.TYPE_DUEL_RESPONSE");
			intent = new Intent(context, TeamActivityActivity.class);
			bundle = new Bundle();
			bundle.putInt(AppConfig.FROM_PARAM_NAME, bean.getType());
			bundle.putString(AppConfig.FROM_PARAM_KEY, bean.getKey());
			bundle.putString("type", "msg");
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUEST_CODE_TEAM_TRANNING);
			break;

		case BeanSysMsg.TYPE_MATCH_CANCEL:// 活动取消后，参与活动的成员收到的系统通知
			logi("BeanSysMsg.TYPE_MATCH_CANCEL");
			break;

		case BeanSysMsg.TYPE_JOIN_TEAM_OK:// 欢迎加入球队
			logi("BeanSysMsg.TYPE_JOIN_TEAM_OK");
			try {
				mapdata = JsonUtil.parse(bean.getKey());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (mapdata == null) {
				Toast.makeText(context, "没有发现消息的key参数.", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			teamId = (String) mapdata.get("team_id");

			// intent = new Intent(context, TeamActivity.class);
			// bundle = new Bundle();
			// bundle.putInt("team_id", Integer.parseInt(teamId));
			// intent.putExtras(bundle);
			// startActivityForResult(intent, REQUEST_CODE_TEAM_TRANNING);
			break;

		case BeanSysMsg.TYPE_DUEL_RESPONSE_CAPTAIN:// 响应约战后约战发起人收到的系统消息
			logi("BeanSysMsg.TYPE_DUEL_RESPONSE_CAPTAIN");
			try {
				mapdata = JsonUtil.parse(bean.getKey());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (mapdata == null) {
				Toast.makeText(context, "没有发现消息的key参数.", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			// teamId = (String) mapdata.get("team_id");
			// intent = new Intent(context, TeamActivity.class);
			// bundle = new Bundle();
			// bundle.putInt("team_id", Integer.parseInt(teamId));
			// intent.putExtras(bundle);
			// startActivityForResult(intent, REQUEST_CODE_TEAM_TRANNING);
			break;
		}
	}

	/**
	 * 阅读未读的消息.
	 * 
	 * @param bean
	 */
	public void doUnreadMessage(final BeanSysMsg bean) {
		logi("doReadMessage()");
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
		parameters.put("app_action", "1060");
		parameters.put("app_platform", "0");
		parameters.put("u_uid",
				String.valueOf(AppConfig.getInstance().getPlayerId()));
		parameters.put("u_msg_id", String.valueOf(bean.getId()));
		HttpPostUtil mHttpPostUtil = null;
		try {
			AppConfig.getInstance().addSign(parameters);
			mHttpPostUtil = AppConfig.getInstance()
					.makeHttpPostUtil(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// WEB request and deal the listener
		new WebRequestUtil(mHttpPostUtil)
				.executeWithOutCache(new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						logi("onSucces");
						pd.dismiss();
						readSysMsg(bean);
						doDealMessage(bean);
					}

					@Override
					public void onFail(Map<String, Object> data) {
						logi("onFail");
						pd.dismiss();
						logi(data.toString());
						Toast.makeText(context, "操作失败:" + data.get("message"),
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError() {
						logi("onError");
						pd.dismiss();
					}
				});

	}

	/**
	 * 将消息设置为已读.
	 * 
	 * @param bean
	 */
	private void readSysMsg(BeanSysMsg bean) {
		List<Map<String, Object>> datas = adapter.getDatas();
		int count = ((datas == null) ? 0 : datas.size());
		for (int i = 0; i < count; i++) {
			if (Integer.parseInt(datas.get(i).get("id").toString()) == bean
					.getId()) {
				// datas.get(i).setState(BeanSysMsg.STATE_READED);
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 重新刷新数据
		// doPullData();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> listview, View v,
			int position, long id) {
		logi("onItemLongClick:" + position);
		if (!(v.getTag() instanceof OneHolder)) {
			return true;
		}
		// BeanSysMsg bean = this.adapter.getDatas().get(position);
		// 长按listview显示菜单
		listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuinfo) {
				menu.setHeaderTitle("可以执行的操作");
				menu.add(0, CONTEXT_MENU_DELETE, 0, "删除");
			}
		});
		return false;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = info.position;
		Map<String, Object> bean = this.adapter.getDatas().get(position);
		int messageId = Integer.parseInt(bean.get("id").toString());
		logi("onContextItemSelected:" + position);
		int itemId = item.getItemId();
		switch (itemId) {
		case CONTEXT_MENU_DELETE:// 删除
			logi("CONTEXT_MENU_DELETE");
			doDeleteMessage(messageId, position);
			break;
		}
		return true;
	}

	/**
	 * 删除消息.
	 * 
	 * @param messageId
	 */
	public void doDeleteMessage(int messageId, final int position) {
		logi("doDeleteMessage(" + messageId + ")");
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
		parameters.put("app_action", "1061");
		parameters.put("app_platform", "0");
		parameters.put("u_uid",
				String.valueOf(AppConfig.getInstance().getPlayerId()));
		parameters.put("u_msg_id", String.valueOf(messageId));
		HttpPostUtil mHttpPostUtil = null;
		try {
			AppConfig.getInstance().addSign(parameters);
			mHttpPostUtil = AppConfig.getInstance()
					.makeHttpPostUtil(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// WEB request and deal the listener
		new WebRequestUtil(mHttpPostUtil)
				.executeWithOutCache(new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						logi("onSucces");
						pd.dismiss();
						adapter.remove(position);
						adapter.notifyDataSetChanged();
					}

					@Override
					public void onFail(Map<String, Object> data) {
						logi("onFail");
						pd.dismiss();
						logi(data.toString());
						Toast.makeText(context, "操作失败:" + data.get("message"),
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError() {
						logi("onError");
						pd.dismiss();
					}
				});

	}

}
