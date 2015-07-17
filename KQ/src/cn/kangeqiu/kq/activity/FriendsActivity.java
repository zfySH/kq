package cn.kangeqiu.kq.activity;

import java.util.ArrayList;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.adapter.AdapterIntegral;
import cn.kangeqiu.kq.adapter.Adapterconcern;
import cn.kangeqiu.kq.adapter.Adapterfans;

import com.easemob.applib.utils.HXPreferenceUtils;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.football.FragmentPersonActivity;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.DateUtil;
import com.nowagme.util.WebRequestUtil;

public class FriendsActivity extends BaseSimpleActivity implements
		OnItemClickListener, OnClickListener, IXListViewListener {
	private XListView lv_match;
	private Button back;
	private LinearLayout abc_fragment_team__tab, rel_income, rel_expend,
			main_lay;
	private EditText search_friends, search_for_fans;
	private TextView fragment_person__title, txt_income, txt_expend;
	private JSONArray records = new JSONArray();
	private Adapterconcern adapter;
	private Adapterfans adapter_fans;
	private AdapterIntegral adapter_Integral;
	private String userId = "", action;
	private int page, mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_myself_friends);

		initHandle();
		if (HXPreferenceUtils.num.equals("关注")) {
			action = "2020";
		} else if (HXPreferenceUtils.num.equals("粉丝")) {
			action = "2018";
		} else if (HXPreferenceUtils.num.equals("积分")) {
			action = "2024";
		}
		initView();
		doFirstShowNearby();
		adapter_Integral = new AdapterIntegral(FriendsActivity.this);
		adapter = new Adapterconcern(FriendsActivity.this, userId);
		adapter_fans = new Adapterfans(FriendsActivity.this, userId);
	}

	private void initHandle() {
		userId = getIntent().getStringExtra("userId");
	}

	public void doFirstShowNearby() {
		page = 1;
		mode = 0;
		doShowNearby(true);
	}

	private void initView() {
		lv_match = (XListView) findViewById(R.id.list);
		back = (Button) findViewById(R.id.back);
		search_friends = (EditText) findViewById(R.id.search_friends);
		search_for_fans = (EditText) findViewById(R.id.search_for_fans);
		abc_fragment_team__tab = (LinearLayout) findViewById(R.id.abc_fragment_team__tab);
		fragment_person__title = (TextView) findViewById(R.id.fragment_person__title);
		main_lay = (LinearLayout) findViewById(R.id.main_lay);
		txt_income = (TextView) findViewById(R.id.txt_income);
		txt_expend = (TextView) findViewById(R.id.txt_expend);
		rel_income = (LinearLayout) findViewById(R.id.rel_income);
		rel_expend = (LinearLayout) findViewById(R.id.rel_expend);
		rel_income.setOnClickListener(this);
		rel_expend.setOnClickListener(this);
		back.setOnClickListener(this);
		if (HXPreferenceUtils.num.equals("关注")) {
			search_friends.setVisibility(View.VISIBLE);
			search_for_fans.setVisibility(View.GONE);
			abc_fragment_team__tab.setVisibility(View.GONE);
			fragment_person__title.setText("我的关注");

		} else if (HXPreferenceUtils.num.equals("粉丝")) {
			search_friends.setVisibility(View.GONE);
			search_for_fans.setVisibility(View.VISIBLE);
			abc_fragment_team__tab.setVisibility(View.GONE);
			fragment_person__title.setText("我的粉丝");
		} else if (HXPreferenceUtils.num.equals("积分")) {
			search_friends.setVisibility(View.GONE);
			search_for_fans.setVisibility(View.GONE);
			fragment_person__title.setText("我的积分");
			abc_fragment_team__tab.setVisibility(View.VISIBLE);
		}
		initData();
		search_friends.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND
						|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))

				{

					initData();

					return true;

				}
				return false;
			}
		});

		search_for_fans.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND
						|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))

				{

					initData();

					return true;

				}
				return false;
			}
		});
		lv_match.setOnItemClickListener(this);
		lv_match.setXListViewListener(this);
		lv_match.setPullLoadEnable(true);
		lv_match.setPullRefreshEnable(true);
	}

	public void doShowNearby(final boolean isRefresh) {
		// 调取个人信息
		doPullDate(action, new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						if (isRefresh) {
							records = (JSONArray) resp.getJson().getJSONArray(
									"records");
							for (int i = 0; i < records.length(); i++) {
								if (HXPreferenceUtils.num.equals("关注")) {
									adapter.setItem(records);
									lv_match.setAdapter(adapter);
								} else if (HXPreferenceUtils.num.equals("粉丝")) {
									adapter_fans.setItem(records);
									lv_match.setAdapter(adapter_fans);
								} else if (HXPreferenceUtils.num.equals("积分")) {

									if (mode == 0) {
										adapter_Integral.setItem(records);
										lv_match.setAdapter(adapter_Integral);
									} else if (mode == 1) {
										adapter_Integral.setItem(records);
										lv_match.setAdapter(adapter_Integral);
									}
								}
							}
						} else {
							JSONArray moreRecords = resp.getJson()
									.getJSONArray("records");
							if (moreRecords.length() < 1) {
								page--;
								Toast.makeText(FriendsActivity.this, "没有更多数据了",
										Toast.LENGTH_SHORT).show();
							} else {
								for (int i = 0; i < moreRecords.length(); i++) {
									records.put(moreRecords.getJSONObject(i));
								}
								if (HXPreferenceUtils.num.equals("关注")) {
									adapter.setItem(records);
									lv_match.setAdapter(adapter);
								} else if (HXPreferenceUtils.num.equals("粉丝")) {
									adapter_fans.setItem(records);
									lv_match.setAdapter(adapter_fans);
								} else if (HXPreferenceUtils.num.equals("积分")) {

									if (mode == 0) {
										adapter_Integral.setItem(records);
										lv_match.setAdapter(adapter_Integral);
									} else if (mode == 1) {
										adapter_Integral.setItem(records);
										lv_match.setAdapter(adapter_Integral);
									}
								}

							}
						}
					} else {
						Toast.makeText(FriendsActivity.this,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}
					onFinishLoad();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				CPorgressDialog.hideProgressDialog();
				onFinishLoad();
			}
		});

	}

	public void initData() {
		doPullDate(action, new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				CPorgressDialog.hideProgressDialog();
				try {
					records = (JSONArray) resp.getJson()
							.getJSONArray("records");
					if (records != null) {

						if (HXPreferenceUtils.num.equals("关注")) {
							adapter.setItem(records);
							lv_match.setAdapter(adapter);
						} else if (HXPreferenceUtils.num.equals("粉丝")) {
							adapter_fans.setItem(records);
							lv_match.setAdapter(adapter_fans);
						} else if (HXPreferenceUtils.num.equals("积分")) {

							if (mode == 0) {
								adapter_Integral.setItem(records);
								lv_match.setAdapter(adapter_Integral);
							} else if (mode == 1) {
								adapter_Integral.setItem(records);
								lv_match.setAdapter(adapter_Integral);
							}
						}

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
			}
		});

	}

	private void doPullDate(String action, MCHttpCallBack listen) {
		CPorgressDialog.showProgressDialog(this);
		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_page", page + ""));
		if (HXPreferenceUtils.num.equals("关注")) {
			pair.add(new BasicNameValuePair("u_search_text", search_friends
					.getText().toString()));
			pair.add(new BasicNameValuePair("u_user_id", userId));
		} else if (HXPreferenceUtils.num.equals("粉丝")) {
			pair.add(new BasicNameValuePair("u_search_text", search_for_fans
					.getText().toString()));
			pair.add(new BasicNameValuePair("u_user_id", userId));
		} else if (HXPreferenceUtils.num.equals("积分")) {
			if (mode == 0) {
				pair.add(new BasicNameValuePair("u_mode", "0"));
			} else if (mode == 1) {
				pair.add(new BasicNameValuePair("u_mode", "1"));
			}
		}
		new WebRequestUtil().execute(true,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listen);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		try {
			if (HXPreferenceUtils.num.equals("关注")) {
				Intent intent = new Intent();
				intent.putExtra("userId", records.getJSONObject(position - 1)
						.getString("id"));
				intent.setClass(this, FragmentPersonActivity.class);
				startActivity(intent);
			} else if (HXPreferenceUtils.num.equals("粉丝")) {
				Intent intent = new Intent();
				intent.putExtra("userId", records.getJSONObject(position - 1)
						.getString("id"));
				intent.setClass(this, FragmentPersonActivity.class);
				startActivity(intent);
			} else if (HXPreferenceUtils.num.equals("积分")) {
//				Toast.makeText(this, "粉丝", 0).show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rel_income:
			txt_income.setTextColor(Color.parseColor("#44CDD7"));
			txt_expend.setTextColor(Color.parseColor("#383838"));
			mode = 0;
			initData();
			break;
		case R.id.rel_expend:
			txt_income.setTextColor(Color.parseColor("#383838"));
			txt_expend.setTextColor(Color.parseColor("#44CDD7"));
			mode = 1;
			initData();
			break;
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		page = 1;
		doShowNearby(true);

	}

	@Override
	public void onLoadMore() {
		page++;
		doShowNearby(false);

	}

	private void onFinishLoad() {
		lv_match.stopRefresh();
		lv_match.stopLoadMore();
		lv_match.setRefreshTime(DateUtil.date2String());
	}
}
