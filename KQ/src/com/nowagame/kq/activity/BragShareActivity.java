package com.nowagame.kq.activity;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.ShareUtils;
import com.nowagme.util.WebRequestUtil;

public class BragShareActivity extends AgentActivity implements OnClickListener {

	private TextView txt_name, txt_question, txt_bet, txt_time;
	private ImageView iv_person;
	private String id;
	private JSONObject brag;

	ImagerLoader loader = new ImagerLoader();
	private ShareUtils shareUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_bragshare);
		id = getIntent().getStringExtra("id");
		shareUtil = new ShareUtils(this);
		init();
		initData(true);
		AddOnClickListener();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		shareUtil.ssoResult(requestCode, resultCode, data);
	}

	public void OnShare(View view) {
		shareUtil.open();
	}

	public void back(View view) {
		finish();
	}

	private void init() {
		txt_name = (TextView) findViewById(R.id.txt_name);
		txt_question = (TextView) findViewById(R.id.txt_question);
		txt_bet = (TextView) findViewById(R.id.txt_bet);
		txt_time = (TextView) findViewById(R.id.txt_time);
		iv_person = (ImageView) findViewById(R.id.abc_fragment_person__iv_person);
	}

	private void AddOnClickListener() {

	}

	@Override
	public void onClick(View v) {

	}

	/**
	 * 初始化数据.
	 */
	public void initData(final boolean isMe) {

		// 调取个人信息
		doPullDate(isMe, "2042", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						txt_name.setText(resp.getJson().getString("nickname"));
						// new DownAndShowImageTask(resp.getJson().getString(
						// "icon"), iv_person).execute();
						loader.LoadImage(resp.getJson().getString("icon"),
								iv_person);
						brag = (JSONObject) resp.getJson()
								.getJSONObject("brag");
						txt_question.setText(brag.getString("question"));
						txt_bet.setText(brag.getString("bet"));
						txt_time.setText(brag.getString("end_time"));
						// 设置分享的内容
						shareUtil.setShareContent(brag.getString("question"),
								R.drawable.chuiniu_share_logo);
					} else {
						Toast.makeText(BragShareActivity.this,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(BragShareActivity.this, resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void doPullDate(boolean isLazyLoad, String action,
			MCHttpCallBack listen) {
		CPorgressDialog.showProgressDialog(BragShareActivity.this);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("app_version", "1"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_brag_id", id));
		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}
}
