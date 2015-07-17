package com.nowagme.football;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.MessageActivity;

import com.easemob.EMCallBack;
import com.jingyi.MiChat.application.BaseApplication;
import com.umeng.fb.FeedbackAgent;

public class SeetingActivity extends BaseSimpleActivity implements
		OnClickListener {
	private LinearLayout ll_exit;
	private int relation;
	private PopupWindowTeamMore pop;
	private RelativeLayout main_lay, rl_feedback;// rl_message
	private Context context;
	public static int num = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_item);
		num = 1;
		context = this;
		relation = 0;
		init();
	}

	private void init() {
		ll_exit = (LinearLayout) findViewById(R.id.ll_exit);
		main_lay = (RelativeLayout) findViewById(R.id.main_lay);
		// rl_message= (RelativeLayout) findViewById(R.id.rl_message);
		rl_feedback = (RelativeLayout) findViewById(R.id.rl_feedback);
		ll_exit.setOnClickListener(this);
		// rl_message.setOnClickListener(this);
		rl_feedback.setOnClickListener(this);
	}

	public void back(View view) {
		finish();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.ll_exit:
			pop = new PopupWindowTeamMore(context, new PopupWinBtnOnclick(),
					relation);
			pop.showAtLocation(main_lay, Gravity.BOTTOM, 0, 0);
			break;
		// case R.id.rl_message:
		// intent.setClass(SeetingActivity.this, MessageActivity.class);
		// startActivity(intent);
		// break;
		case R.id.rl_feedback:
			// intent.setClass(SeetingActivity.this, FeedBackActivity.class);
			// startActivity(intent);
			FeedbackAgent agent = new FeedbackAgent(context);
			agent.startFeedbackActivity();
			break;
		default:
			break;
		}

	}

	/**
	 * 更多
	 * 
	 * @author zjq
	 * 
	 */
	private class PopupWinBtnOnclick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.abc_btn_exit_login:
				pop.dismiss();
				doLogout();
				break;
			case R.id.abc_btn_cancel:
				pop.dismiss();
				break;
			}
		}

	}

	/**
	 * logout
	 */
	void doLogout() {
		final ProgressDialog pd = new ProgressDialog(context);
		String st = getResources().getString(R.string.Are_logged_out);
		pd.setMessage(st);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		BaseApplication.getInstance().logout(new EMCallBack() {

			@Override
			public void onSuccess() {
				((Activity) context).runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						// 重新显示登陆页面
						setResult(300);
						finish();

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

}
