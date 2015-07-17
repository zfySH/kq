package com.nowagame.kq.activity;

import java.util.ArrayList;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.DateUtil;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;

public class MyGuessActivity extends AgentActivity implements OnClickListener {

	// private Button add_Btn;

	private XListView brag_ListView;
	private MyGuessActivityAdapter adapter;
	private JSONArray records;
	private int page;
	private int id = 0;
	private String roomId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_mybrag);
		((TextView) findViewById(R.id.title)).setText("我的竞猜");

		roomId = getIntent().getStringExtra("roomId");

		init();
		AddOnClickListener();
		adapter = new MyGuessActivityAdapter(this);
		brag_ListView.setAdapter(adapter);
	}

	private void init() {
		brag_ListView = (XListView) findViewById(R.id.brag_ListView);
		brag_ListView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				page = 1;
				doShowNearby(true, false);
			}

			@Override
			public void onLoadMore() {
				page++;
				doShowNearby(false, false);
			}
		});
		brag_ListView.setPullLoadEnable(true);
		brag_ListView.setPullRefreshEnable(true);
		doFirstShowNearby();

		if (roomId != null && !roomId.equals("")) {
			brag_ListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					try {
						Intent intent = new Intent();
						intent.setClass(MyGuessActivity.this,
								ChooseMyGuessActivity.class);
						intent.putExtra(
								"id",
								records.getJSONObject(position - 1).getString(
										"id"));
						MyGuessActivity.this.startActivity(intent);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
		}
	}

	// //**
	// * 第1次打开附近显示数据.
	// *//*
	public void doFirstShowNearby() {
		page = 1;
		doShowNearby(true, true);
	}

	public void doShowNearby(final boolean isRefresh, boolean isLazyLoad) {
		String action = "";
		if (roomId != null && !roomId.equals(""))
			action = "2058";
		else
			action = "2056";
		doPullDate(isLazyLoad, action, new MCHttpCallBack() {

			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						JSONArray record = (JSONArray) resp.getJson()
								.getJSONArray("records");
						if (isRefresh) {
							records = record;
							adapter.setItem(records);
						} else {
							for (int i = 0; i < record.length(); i++) {
								records.put(record.get(i));
							}

							int itemsCount = (record == null) ? 0 : record
									.length();
							// 刷新显示
							if (itemsCount > 0) {
								adapter.setItem(records);
							} else {
								Toast.makeText(MyGuessActivity.this,
										"没有更多数据了.", Toast.LENGTH_SHORT).show();
								if (page > 1)
									page--;
							}

						}

					} else {
						Toast.makeText(MyGuessActivity.this,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 数据载入关闭提示;
				onFinishLoad();
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				Toast.makeText(MyGuessActivity.this, resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
				// 数据载入关闭提示;
				onFinishLoad();
			}
		});

	}

	public void back(View view) {
		finish();
	}

	private void AddOnClickListener() {
		// brag_ListView.setOnItemClickListener(new OnItemClickListener());
	}

	@Override
	public void onClick(View v) {
		// if (v == add_Btn) {
		// Log.v("tag", ">>>   点击了添加  >>>>");
		// startActivity(new Intent(this, CreatMyBragActivity.class));
		// }
	}

	// class OnItemClickListener implements
	// android.widget.AdapterView.OnItemClickListener {
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int option,
	// long arg3) {
	// Log.v("tag", "点击了>>>>" + option);
	// Intent intent = new Intent();
	// intent.setClass(MyGuessActivity.this, BragDetailActivity.class);
	//
	// try {
	// intent.putExtra("id", records.getJSONObject(option - 1)
	// .getString("id"));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// startActivity(intent);
	//
	// }
	// }

	private void doPullDate(boolean isLazyLoad, String action,
			MCHttpCallBack listen) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("app_version", UpdataUtil
				.getAppVersion(this)));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_page", page + ""));
		if (roomId != null && !roomId.equals(""))
			pair.add(new BasicNameValuePair("u_room_id", roomId));
		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

	private void onFinishLoad() {
		brag_ListView.stopRefresh();
		brag_ListView.stopLoadMore();
		brag_ListView.setRefreshTime(DateUtil.date2String());
	}

}
