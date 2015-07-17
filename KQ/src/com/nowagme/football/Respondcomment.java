package com.nowagme.football;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;

import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;

public class Respondcomment extends AgentActivity {
	private Button save;
	private EditText edit;
	private String u_content_id, u_comment_id, nickname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_match_respond_comment_activity);
		initHandle();
		initview();
		edit.setHint("回复" + nickname + ":");
	}

	public void back(View view) {
		finish();
	}

	private void initview() {
		edit = (EditText) findViewById(R.id.edit);
		save = (Button) findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (edit.getText().toString() == null
						|| edit.getText().toString().equals("")) {
					Toast.makeText(Respondcomment.this, "请输入内容",
							Toast.LENGTH_SHORT).show();

					return;

				}
				doPullDate("2009", new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						String resultCode = (String) data.get("result_code");
						if ("0".equals(resultCode)) {
							try {
								Toast.makeText(Respondcomment.this, "回复成功",
										Toast.LENGTH_SHORT).show();
								edit.setText("");
								finish();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Toast.makeText(Respondcomment.this, "回复失败",
									Toast.LENGTH_SHORT).show();
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
		});
	}

	private void initHandle() {
		u_content_id = getIntent().getStringExtra("u_content_id");
		u_comment_id = getIntent().getStringExtra("u_comment_id");
		nickname = getIntent().getStringExtra("name");

	}

	private void doPullDate(String action, WebRequestUtilListener listen) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		parameters.put("u_body", edit.getText().toString());
		parameters.put("u_content_id", "0");
		parameters.put("u_comment_id", u_comment_id);
		parameters.put("u_type", "0");
		Log.v("tag", "parameters" + parameters);
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
