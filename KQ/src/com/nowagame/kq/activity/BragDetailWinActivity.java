package com.nowagame.kq.activity;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;

public class BragDetailWinActivity extends AgentActivity implements OnClickListener {

	private ImageView share_ImageView, iv_person;
	private String id;
	private JSONObject brag;
	private TextView txt_name, txt_question, txt_bet, txt_time;
	private Button btn_win, btn_lost;
//	private LinearLayout my_win;
	private String state = "";
	ImagerLoader loader = new ImagerLoader();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_bragdetail_win);
		id = getIntent().getStringExtra("id");
		init();
		initData(true);
		AddOnClickListener();
	}

	public void back(View view) {
		finish();
	}

	private void init() {
		share_ImageView = (ImageView) findViewById(R.id.share_ImageView);
		txt_name = (TextView) findViewById(R.id.txt_name);
		txt_question = (TextView) findViewById(R.id.txt_question);
		txt_bet = (TextView) findViewById(R.id.txt_bet);
		txt_time = (TextView) findViewById(R.id.txt_time);
		iv_person = (ImageView) findViewById(R.id.abc_fragment_person__iv_person);
		btn_win = (Button) findViewById(R.id.btn_win);
		btn_lost = (Button) findViewById(R.id.btn_lost);
//		my_win = (LinearLayout) findViewById(R.id.my_win);
	}

	private void AddOnClickListener() {
		share_ImageView.setOnClickListener(this);
		btn_win.setOnClickListener(this);
		btn_lost.setOnClickListener(this);
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
						//	"icon"), iv_person).execute();
						loader.LoadImage(resp.getJson().getString(
								"icon"), iv_person);
						brag = (JSONObject) resp.getJson()
								.getJSONObject("brag");
						txt_question.setText(brag.getString("question"));
						txt_bet.setText(brag.getString("bet"));
						txt_time.setText(brag.getString("end_time"));
						String do_flag = brag.getString("do_flag");
						String state = brag.getString("state");
						if (do_flag.equals("1")) {
							btn_win.setClickable(true);
							btn_lost.setClickable(true);
							btn_win.setVisibility(View.VISIBLE);
							btn_lost.setVisibility(View.VISIBLE);
						} else {
							btn_win.setClickable(false);
							btn_lost.setClickable(false);
							if (state.endsWith("0")) {
//								my_win.setVisibility(View.VISIBLE);
//								my_win.setBackgroundResource(R.drawable.my_lost);
								btn_win.setVisibility(View.GONE);
								btn_lost.setVisibility(View.VISIBLE);
							} else if (state.endsWith("1")) {
//								my_win.setVisibility(View.VISIBLE);
//								my_win.setBackgroundResource(R.drawable.my_win);
								btn_win.setVisibility(View.VISIBLE);
								btn_lost.setVisibility(View.GONE);
							} else {
//								my_win.setVisibility(View.GONE);
								btn_win.setVisibility(View.GONE);
								btn_lost.setVisibility(View.GONE);
							}
						}
					} else {
						Toast.makeText(BragDetailWinActivity.this,
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
				Toast.makeText(BragDetailWinActivity.this, resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v == share_ImageView) {
			Intent intent = new Intent();
			intent.setClass(BragDetailWinActivity.this, BragShareActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
		} else if (v == btn_win) {
			state = "1";

			doPullDate(false, "2057", new MCHttpCallBack() {
				@Override
				public void onSuccess(MCHttpResp resp) {
					super.onSuccess(resp);
					CPorgressDialog.hideProgressDialog();
					try {
						String resultCode = resp.getJson().getString(
								"result_code");
//						my_win.setVisibility(View.VISIBLE);
						if (resultCode.equals("0")) {
							Toast.makeText(BragDetailWinActivity.this, "我赢了",
									Toast.LENGTH_SHORT).show();
							initData(true);
						} else {
							Toast.makeText(BragDetailWinActivity.this,
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
					Toast.makeText(BragDetailWinActivity.this,
							resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
				}
			});
		} else if (v == btn_lost) {
			state = "0";
			doPullDate(false, "2057", new MCHttpCallBack() {
				@Override
				public void onSuccess(MCHttpResp resp) {
					super.onSuccess(resp);
					CPorgressDialog.hideProgressDialog();
					try {
						String resultCode = resp.getJson().getString(
								"result_code");
						if (resultCode.equals("0")) {
							Toast.makeText(BragDetailWinActivity.this, "我输了",
									Toast.LENGTH_SHORT).show();
							initData(true);
						} else {
							Toast.makeText(BragDetailWinActivity.this,
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
					Toast.makeText(BragDetailWinActivity.this,
							resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
				}
			});
		}

	}

	private void doPullDate(boolean isLazyLoad, String action,
			MCHttpCallBack listen) {
		CPorgressDialog.showProgressDialog(BragDetailWinActivity.this);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("app_version", UpdataUtil
				.getAppVersion(this)));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_brag_id", id));
		if (action.equals("2057")) {
			pair.add(new BasicNameValuePair("u_state", state));
		}
		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

}
