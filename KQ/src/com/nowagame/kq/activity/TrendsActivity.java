package com.nowagame.kq.activity;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.MatchHotlineView;
import cn.kangeqiu.kq.refresh.PullToRefreshView;
import cn.kangeqiu.kq.refresh.PullToRefreshView.OnFooterRefreshListener;
import cn.kangeqiu.kq.refresh.PullToRefreshView.OnHeaderRefreshListener;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.WebRequestUtil;

public class TrendsActivity extends AgentActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private PullToRefreshView mPullToRefreshView;
	private LinearLayout ll_main;
	private int page = 1;
	private JSONArray records = new JSONArray();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trends_activity);
		initView();
		initData(true);
	}

	public void back(View view) {
		finish();
	}

	private void initView() {
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setLastUpdated(new Date().toLocaleString());
		ll_main = (LinearLayout) findViewById(R.id.ll_main);

	}

	/**
	 * 更新数据
	 */
	public void refreshLoad(final boolean isRefresh, final int pageNo) {
		boolean flag = false;
		// 调取个人信息
		doPullDate(pageNo, flag, "2015", new MCHttpCallBack() {
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
										TrendsActivity.this);
								ll_main.addView(view.getView(records
										.getJSONObject(i)));
							}
						} else {
							JSONArray moreRecords = (JSONArray) resp.getJson()
									.get("records");
							if (moreRecords.length() < 1) {
								page--;
								Toast.makeText(TrendsActivity.this, "没有更多数据了",
										Toast.LENGTH_SHORT).show();
							} else {

								for (int i = 0; i < moreRecords.length(); i++) {
									MatchHotlineView view = new MatchHotlineView(
											TrendsActivity.this);
									ll_main.addView(view.getView(records
											.getJSONObject(i)));
								}

							}
						}
					} else {
						Toast.makeText(TrendsActivity.this,
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
				Toast.makeText(TrendsActivity.this, resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 初始化数据.
	 */
	public void initData(final boolean isMe) {

		// 调取个人信息
		doPullDate(1, isMe, "2015", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						ll_main.removeAllViews();
						records = resp.getJson().getJSONArray("records");
						for (int i = 0; i < records.length(); i++) {
							MatchHotlineView view = new MatchHotlineView(
									TrendsActivity.this);
							ll_main.addView(view.getView(records
									.getJSONObject(i)));
						}
					} else {
						Toast.makeText(TrendsActivity.this,
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
				Toast.makeText(TrendsActivity.this, resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void doPullDate(int pageNo, boolean isRefresh, String action,
			MCHttpCallBack listen) {
		CPorgressDialog.showProgressDialog(TrendsActivity.this);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_user_id", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_page", pageNo + ""));
		new WebRequestUtil().execute(isRefresh, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

	private void onFinishLoad(boolean isRefresh) {
		if (isRefresh)
			mPullToRefreshView.onHeaderRefreshComplete("更新于:"
					+ new Date().toLocaleString());
		else
			mPullToRefreshView.onFooterRefreshComplete();
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
