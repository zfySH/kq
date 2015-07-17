package com.nowagme.football;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.ChatActivity;
import cn.kangeqiu.kq.activity.FriendsActivity;
import cn.kangeqiu.kq.activity.LoginActivity;
import cn.kangeqiu.kq.activity.view.MatchHotlineView;
import cn.kangeqiu.kq.refresh.PullToRefreshView;
import cn.kangeqiu.kq.refresh.PullToRefreshView.OnFooterRefreshListener;
import cn.kangeqiu.kq.refresh.PullToRefreshView.OnHeaderRefreshListener;

import com.easemob.EMCallBack;
import com.easemob.applib.model.SerializableMap;
import com.easemob.applib.utils.HXPreferenceUtils;
import com.google.gson.Gson;
import com.jingyi.MiChat.application.BaseApplication;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.WebRequestUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentPersonActivity extends AgentActivity implements
		OnClickListener, OnHeaderRefreshListener, OnFooterRefreshListener {

	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["
			+ FragmentPersonActivity.class.getName() + "]";
	private boolean isAttention = false;
	private boolean progressShow;
	private ProgressDialog pd;
	private Context context;
	private PopupWindow popw = null;
	private LinearLayout ll_popup;
	private PopupWindowTeamMore pop;
	private TextView tv_person_nickname, tv_person_id, fragment_person__title,
			txt_adder, txt_num1, txt_num2, txt_num3;
	private ImageView iv_person;
	private ImageButton img_setting;
	private Button btn_logout, btn_edit, btn_concern, btn_private, sexage;
	private LinearLayout lay_head, lay_album, lay_friends, lay_feedback,
			lay_personal_activity, main_lay, back, lay_concern;
	private int relation;
	private RelativeLayout rel_trends, rel_concern, rel_follower;
	private String teamChatGroupId;

	// private XListView comment_list;
	// private MatchHotLineAdapter adapter;

	private int page = 1;
	private JSONArray records = new JSONArray();
	private JSONObject user = null;

	private String userId = "";
	private LinearLayout ll_main;
	private PullToRefreshView mPullToRefreshView;
	private ImagerLoader loader = new ImagerLoader();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_fragment_person);
		context = this;
		relation = 2;
		initHandle();
		initView();
		initData();
	}

	private void initHandle() {
		userId = getIntent().getStringExtra("userId");
	}

	/**
	 * log information
	 * 
	 * @param msg
	 */
	public static void logi(String msg) {
		Log.i(TAG, CLASS_NAME + " " + msg);
	}

	public void back(View view) {
		finish();
	}

	/*
	 * 初始化控件
	 */
	private void initView() {
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setLastUpdated(new Date().toLocaleString());
		ll_main = (LinearLayout) findViewById(R.id.ll_main);

		lay_concern = (LinearLayout) findViewById(R.id.lay_concern);
		// adapter = new MatchHotLineAdapter(this);
		btn_edit = (Button) findViewById(R.id.btn_edit);

		btn_concern = (Button) findViewById(R.id.btn_concern);
		btn_private = (Button) findViewById(R.id.btn_private);
		sexage = (Button) findViewById(R.id.abc_fragment_nearby__listview__btn_sex_age);
		rel_trends = (RelativeLayout) findViewById(R.id.rel_trends);
		rel_concern = (RelativeLayout) findViewById(R.id.rel_concern);
		rel_follower = (RelativeLayout) findViewById(R.id.rel_follower);
		// rel_integral = (RelativeLayout) findViewById(R.id.rel_integral);
		img_setting = (ImageButton) findViewById(R.id.img_setting);
		main_lay = (LinearLayout) findViewById(R.id.main_lay);
		back = (LinearLayout) findViewById(R.id.back);
		// comment_list = (XListView) findViewById(R.id.list);
		fragment_person__title = (TextView) findViewById(R.id.fragment_person__title);
		iv_person = (CircleImageView) findViewById(R.id.abc_fragment_person__iv_person);
		txt_adder = (TextView) findViewById(R.id.txt_adder);
		txt_num1 = (TextView) findViewById(R.id.txt_num1);
		txt_num2 = (TextView) findViewById(R.id.txt_num2);
		txt_num3 = (TextView) findViewById(R.id.txt_num3);
		btn_edit.setOnClickListener(this);
		btn_concern.setOnClickListener(this);
		btn_private.setOnClickListener(this);
		rel_trends.setOnClickListener(this);
		rel_concern.setOnClickListener(this);
		rel_follower.setOnClickListener(this);
		// rel_integral.setOnClickListener(this);
		img_setting.setOnClickListener(this);
	}

	/**
	 * 更新数据
	 */
	public void refreshLoad(final boolean isRefresh, final int pageNo) {
		logi("initData()");

		// 调取个人信息
		doPullDate(pageNo, "2015", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				CPorgressDialog.hideProgressDialog();
				onFinishLoad(isRefresh);
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						if (isRefresh) {
							ll_main.removeAllViews();
							records = (JSONArray) resp.getJson().getJSONArray(
									"records");

							for (int i = 0; i < records.length(); i++) {
								MatchHotlineView view = new MatchHotlineView(
										FragmentPersonActivity.this);
								ll_main.addView(view.getView(records
										.getJSONObject(i)));
							}
							// adapter.setItem(records);
						} else {
							JSONArray moreRecords = (JSONArray) resp.getJson()
									.get("records");
							if (moreRecords.length() < 1) {
								page--;
								Toast.makeText(FragmentPersonActivity.this,
										"没有更多数据了", Toast.LENGTH_SHORT).show();
							} else {

								for (int i = 0; i < moreRecords.length(); i++) {
									MatchHotlineView view = new MatchHotlineView(
											FragmentPersonActivity.this);
									ll_main.addView(view.getView(records
											.getJSONObject(i)));
								}
							}
						}
					} else {
						Toast.makeText(context,
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
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(FragmentPersonActivity.this,
						resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 初始化数据.
	 */
	public void initData() {
		logi("initData()");

		// 调取个人信息
		doPullDate(1, "2015", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						user = (JSONObject) resp.getJson()
								.getJSONObject("user");
						if (user.get("attention_state") != null) {
							isAttention = user.getString("attention_state")
									.equals("0") ? false : true;
							// userId=user.get("id").toString();
							if (isAttention) {
								btn_concern.setText("");
								btn_concern
										.setBackgroundResource(R.drawable.has_attention);
							} else {
								btn_concern.setText("关注");
								btn_concern
										.setBackgroundResource(R.drawable.abc_button_roundcorner_gray);
							}
						}
						if (user != null) {
							if (user.getString("edit_state").equals("0")) {// 不能编辑
								btn_edit.setVisibility(View.GONE);
								lay_concern.setVisibility(View.VISIBLE);
								fragment_person__title.setText(user.get(
										"nickname").toString());
								back.setVisibility(View.VISIBLE);
								img_setting.setVisibility(View.GONE);

							} else if (user.getString("edit_state").equals("1")) {// 编辑
								btn_edit.setVisibility(View.VISIBLE);
								lay_concern.setVisibility(View.GONE);
								fragment_person__title.setText("我");
								back.setVisibility(View.VISIBLE);
								img_setting.setVisibility(View.VISIBLE);
							}
							loader.LoadImage(user.getString("icon"), iv_person);
							// new DownAndShowImageTask(user.getString("icon"),
							// iv_person).execute();
							int sex = Integer.parseInt(user.getString("sex"));
							if (sex == 1) {
								sexage.setBackgroundResource(R.drawable.man);
							} else if (sex == 2) {
								sexage.setBackgroundResource(R.drawable.gril);
							}
							int sexResId = Constant.getResourceOfPlayerSex(sex);
							Drawable drawable = context.getResources()
									.getDrawable(sexResId);
							drawable.setBounds(0, 0,
									drawable.getMinimumWidth(),
									drawable.getMinimumHeight());
							sexage.setCompoundDrawables(drawable, null, null,
									null);
							// -－年龄
							sexage.setText(user.getString("age"));

							txt_adder.setText(((JSONObject) user
									.getJSONObject("province"))
									.getString("name")

									+ "  "
									+ ((JSONObject) user.getJSONObject("city"))
											.getString("name"));
							txt_num1.setText(user.getString("content_count"));
							txt_num2.setText(user.getString("attention_count"));
							txt_num3.setText(user.getString("fun_count"));
							// txt_num4.setText(user.getString("score"));

						}
						records = resp.getJson().getJSONArray("records");
						ll_main.removeAllViews();
						if (records != null) {
							for (int i = 0; i < records.length(); i++) {
								MatchHotlineView view = new MatchHotlineView(
										FragmentPersonActivity.this);
								ll_main.addView(view.getView(records
										.getJSONObject(i)));
							}
						}
					} else {
						Toast.makeText(context,
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
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(FragmentPersonActivity.this,
						resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void doPullDate(int pageNo, String action, MCHttpCallBack listen) {
		CPorgressDialog.showProgressDialog(this);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_user_id", userId));
		boolean isLazyLoad = false;
		if (action.equals("2015")) {
			isLazyLoad = true;
			pair.add(new BasicNameValuePair("u_page", pageNo + ""));
		}
		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);

	}

	private void pop() {

		popw = new PopupWindow(FragmentPersonActivity.this);

		View view = getLayoutInflater().inflate(
				R.layout.item_popupwindows_connen, null);
		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

		popw.setWidth(LayoutParams.MATCH_PARENT);
		popw.setHeight(LayoutParams.WRAP_CONTENT);
		popw.setBackgroundDrawable(new BitmapDrawable());
		popw.setFocusable(true);
		popw.setOutsideTouchable(true);
		popw.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view.findViewById(R.id.cancel);
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popw.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popw.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				doPullDate(0, "2017", new MCHttpCallBack() {
					@Override
					public void onSuccess(MCHttpResp resp) {
						super.onSuccess(resp);
						CPorgressDialog.hideProgressDialog();
						try {
							String resultCode = resp.getJson().getString(
									"result_code");
							if (resultCode.equals("0")) {
								Toast.makeText(context, "取消关注成功",
										Toast.LENGTH_SHORT).show();
								btn_concern
										.setBackgroundResource(R.drawable.abc_button_roundcorner_gray);
								btn_concern.setText("关注");
								isAttention = false;
							} else {
								Toast.makeText(context,
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
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(FragmentPersonActivity.this,
								resp.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				});
				popw.dismiss();
				ll_popup.clearAnimation();
			}
		});

	}

	@Override
	public void onClick(View v) {
		logi("onClick(View v)");
		int id = v.getId();
		Intent intent = new Intent();
		switch (id) {
		case R.id.btn_edit:// 编辑
			intent.setClass(this, EditfileActivity.class);
			// 传递数据
			// final SerializableMap myMap = new SerializableMap();
			// myMap.setMap(user);// 将map数据添加到封装的myMap中
			Gson gson = new Gson();
			// 将返回的JSON数据转换为对象JsonRequestResult
			SerializableMap myMap = gson.fromJson(user.toString(),
					SerializableMap.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("user", myMap);
			intent.putExtras(bundle);
			this.startActivityForResult(intent, 0);
			break;
		case R.id.btn_concern:// 关注
			if (!isAttention) {
				doPullDate(0, "2016", new MCHttpCallBack() {
					@Override
					public void onSuccess(MCHttpResp resp) {
						super.onSuccess(resp);
						CPorgressDialog.hideProgressDialog();
						try {
							String resultCode = resp.getJson().getString(
									"result_code");
							if (resultCode.equals("0")) {
								Toast.makeText(context, "关注成功",
										Toast.LENGTH_SHORT).show();
								btn_concern.setText("");
								btn_concern
										.setBackgroundResource(R.drawable.has_attention);
								isAttention = true;
							} else {
								Toast.makeText(context,
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
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(FragmentPersonActivity.this,
								resp.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				});
			} else {

				// pop = new PopupWindowTeamMore(context,
				// new PopupWinBtnOnclick(), relation);
				// // pop.showAsDropDown(v);
				//
				// pop.showAtLocation(main_lay, Gravity.CENTER, 0, 0);
				pop();
				popw.showAtLocation(main_lay, Gravity.CENTER, 0, 0);

			}
			break;

		case R.id.btn_private:// 私信
			// intent.setClass(context, ChatActivity.class);
			// intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
			// // intent.putExtra("groupId", teamChatGroupId);
			// startActivity(intent);
			// startActivity(intent);
			try {
				startActivity(new Intent(context, ChatActivity.class).putExtra(
						"nickname", user.getString("nickname")).putExtra(
						"userId", user.getString("id")));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.rel_trends:// 动态
			// intent.setClass(this.getActivity(), MyselfMessageActivity.class);
			// this.getActivity().startActivity(intent);
			break;
		case R.id.rel_concern:// 关注
			try {
				HXPreferenceUtils.num = "关注";
				intent.setClass(this, FriendsActivity.class);
				intent.putExtra("userId", String.valueOf(user.getString("id")));
				this.startActivity(intent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case R.id.rel_follower:// 粉丝
			try {
				HXPreferenceUtils.num = "粉丝";
				intent.setClass(this, FriendsActivity.class);
				intent.putExtra("userId", String.valueOf(user.getString("id")));
				this.startActivity(intent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		// case R.id.rel_integral:// 积分
		//
		// try {
		// HXPreferenceUtils.num = "积分";
		// if (userId.equals(AppConfig.getInstance().getPlayerId() + "")) {
		// intent.setClass(this, FriendsActivity.class);
		// intent.putExtra("userId",
		// String.valueOf(user.getString("id")));
		// this.startActivity(intent);
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// break;
		case R.id.img_setting:// 设置
			// doLogout();
			intent.setClass(this, SeetingActivity.class);
			this.startActivity(intent);
			break;
		}
	}

	/**
	 * 关注或者取消关注.
	 * 
	 * @param webRequestUtilListener
	 */
	// public void doAttention(String action,
	// WebRequestUtilListener webRequestUtilListener) {
	// logi("doAttention()");
	// progressShow = true;
	// pd = new ProgressDialog(context);
	// pd.setCanceledOnTouchOutside(false);
	// pd.setOnCancelListener(new OnCancelListener() {
	// @Override
	// public void onCancel(DialogInterface dialog) {
	// progressShow = false;
	// }
	// });
	// pd.setMessage(getString(R.string.abc_data_loding));
	// pd.show();
	// // doPullData(action, webRequestUtilListener);
	// }

	/**
	 * 更多
	 * 
	 * @author zjq
	 * 
	 */
	private class PopupWinBtnOnclick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// int dataType = -1;
			int id = v.getId();
			switch (id) {
			case R.id.abc_btn_cancel_attention:// 取消关注
				doPullDate(0, "2017", new MCHttpCallBack() {
					@Override
					public void onSuccess(MCHttpResp resp) {
						super.onSuccess(resp);
						CPorgressDialog.hideProgressDialog();
						try {
							String resultCode = resp.getJson().getString(
									"result_code");
							if (resultCode.equals("0")) {
								Toast.makeText(context, "取消关注成功",
										Toast.LENGTH_SHORT).show();
								btn_concern
										.setBackgroundResource(R.drawable.abc_button_roundcorner_gray);
								btn_concern.setText("关注");
								isAttention = false;
							} else {
								Toast.makeText(context,
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
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(FragmentPersonActivity.this,
								resp.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				});
				pop.dismiss();
				break;
			// case R.id.abc_btn_exit_login:// 退出登录
			// logi("R.id.abc_fragment_nearby_popwin__btn_court");
			// pop.dismiss();
			// break;
			// case R.id.abc_btn_man:// 男
			// logi("R.id.abc_fragment_nearby_popwin__btn_match");
			// pop.dismiss();
			// // doFighter();
			// break;
			// case R.id.abc_btn_girl:// 女
			// logi("R.id.abc_fragment_nearby_popwin__btn_all");
			// pop.dismiss();
			// // doExitTeam();
			// break;
			case R.id.abc_btn_cancel:
				logi("R.id.abc_fragment_nearby_popwin__btn_cacel");
				pop.dismiss();
				break;
			}
		}
	}

	/**
	 * logout
	 */
	void doLogout() {
		final ProgressDialog pd = new ProgressDialog(this);
		String st = getResources().getString(R.string.Are_logged_out);
		pd.setMessage(st);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		BaseApplication.getInstance().logout(new EMCallBack() {

			@Override
			public void onSuccess() {
				FragmentPersonActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						// 重新显示登陆页面
						FragmentPersonActivity.this.finish();
						startActivity(new Intent(FragmentPersonActivity.this,
								LoginActivity.class));

					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, String message) {

			}
		});
	}

	// @Override
	// public void onRefresh() {
	// refreshLoad(true, 1);
	// }
	//
	// @Override
	// public void onLoadMore() {
	// page++;
	// refreshLoad(false, page);
	// }

	private void onFinishLoad(boolean isRefresh) {
		if (isRefresh)
			mPullToRefreshView.onHeaderRefreshComplete("更新于:"
					+ new Date().toLocaleString());
		else
			mPullToRefreshView.onFooterRefreshComplete();
		// comment_list.stopRefresh();
		// comment_list.stopLoadMore();
		// comment_list.setRefreshTime(DateUtil.date2String());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 20:
			initData();
			break;

		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		page++;
		refreshLoad(false, page);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		refreshLoad(true, 1);
	}
}
