package com.nowagme.football;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.adapter.AdapterHelp;

import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;

public class HelpActivity extends AgentActivity {
	private ListView lv_listview;
	private AdapterHelp adapter = null;
	private String content_id;
	private List<Map<String, Object>> records = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_sys_msg);
		initHandle();
		initData();
		initview();
		adapter = new AdapterHelp(this);
		lv_listview.setAdapter(adapter);
	}

	private void initHandle() {
		content_id = getIntent().getStringExtra("content_id");

	}

	private void initview() {
		lv_listview = (ListView) findViewById(R.id.abc_sys_msg__lv_listview);
		lv_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("userId", records.get(position).get("id")
						.toString());
				intent.setClass(HelpActivity.this, FragmentPersonActivity.class);
				startActivity(intent);

			}
		});

	}

	private void initData() {
		doPullDate("2027", new WebRequestUtilListener() {

			@Override
			public void onSucces(Map<String, Object> data) {

				records = (List<Map<String, Object>>) data.get("records");
				if (records != null) {
					adapter.setDatas(records);
					adapter.notifyDataSetChanged();

					Log.v("tag", "records______________________" + records);
				}

			}

			@Override
			public void onFail(Map<String, Object> data) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError() {
				// TODO Auto-generated method stub

			}
		});
	}

	private void doPullDate(String action, WebRequestUtilListener listen) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		parameters.put("u_page", "1");
		parameters.put("u_content_id", content_id);
		Log.v("tag", "parameters______________________" + parameters);
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

	/**
	 * 返回按钮.
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}
}
