package com.nowagme.football;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.adapter.AdapterQuiz;

import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;

public class QuizActivity extends AgentActivity implements OnClickListener {
	private Button btn_fifty, btn_hundred, btn_two_hundred, btn_finhundred,
			btn_thousand, btn_two_thousand, btn_cancel, btn_cathectic,
			abc_team__btn_back;
	private String options_id, id, join_state;
	public static String coin_minus = null;
	private Button[] btn_quiz;
	public String[] temp = null;
	private GridView grid;
	private AdapterQuiz adapter;
	private int i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz_item);
		initData();
		initView();

	}

	private void initView() {
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_cathectic = (Button) findViewById(R.id.btn_cathectic);
		abc_team__btn_back = (Button) findViewById(R.id.abc_team__btn_back);
		grid = (GridView) findViewById(R.id.grid);
		adapter = new AdapterQuiz(this);
		adapter.SetItme(temp);
		grid.setAdapter(adapter);

		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				i = position;
				adapter.setboolean(position);
			}
		});
		btn_cancel.setOnClickListener(this);
		btn_cathectic.setOnClickListener(this);
		abc_team__btn_back.setOnClickListener(this);
	}

	private void initData() {
		options_id = getIntent().getStringExtra("options_id");
		id = getIntent().getStringExtra("id");
		coin_minus = getIntent().getStringExtra("coin_minus");
		temp = coin_minus.split(",");
		join_state = getIntent().getStringExtra("coin_minus");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			finish();
			break;
		case R.id.btn_cathectic:
			doPullDate("2014", new WebRequestUtilListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onSucces(Map<String, Object> data) {
					String resultCode = (String) data.get("result_code");
					if ("0".equals(resultCode)) {
						try {
							Toast.makeText(QuizActivity.this, "竞猜成功",
									Toast.LENGTH_SHORT).show();
							setResult(100);
							finish();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(QuizActivity.this, "竞猜失败",
								Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onFail(Map<String, Object> data) {
					Toast.makeText(QuizActivity.this,
							String.valueOf(data.get("message")),
							Toast.LENGTH_SHORT).show();

				}

				@Override
				public void onError() {
				}
			});
			break;
		case R.id.abc_team__btn_back:
			finish();
			break;
		default:
			break;
		}

	}

	private void doPullDate(String action, WebRequestUtilListener listen) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", "2014");
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		parameters.put("u_page", "1");
		parameters.put("u_activity_id", id + "");
		parameters.put("u_answer", options_id + "");
		parameters.put("u_coin", temp[i]);
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
