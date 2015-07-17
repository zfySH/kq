package com.nowagme.football;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;

import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.MyActivityManager;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;

/**
 * 注册页
 * 
 */
public class RegisterFirstActivity extends BaseSimpleActivity {
	private EditText userNameEditText;
	private TextView title;

	private String username;
	private Context context;
	@SuppressWarnings("unused")
	private boolean progressShow;
	private int type;
	MyActivityManager mam = MyActivityManager.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_register_first);
		mam.pushOneActivity(RegisterFirstActivity.this);
		type = getIntent().getIntExtra("type", 0);

		context = this;
		userNameEditText = (EditText) findViewById(R.id.abc_activity_register_first__et_username);
		title = (TextView) findViewById(R.id.title);

		if (type == 1)
			title.setText("新用户注册");
		else if (type == 2)
			title.setText("找回密码");
	}

	/**
	 * 下一步
	 * 
	 * @param view
	 */
	public void registerFirst(View view) {
		String st1 = getResources().getString(
				R.string.User_name_cannot_be_empty);
		username = userNameEditText.getText().toString().trim();
		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, st1, Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		}

		if (!TextUtils.isEmpty(username)) {
			// 开始注册
			doRegisterFirst();
		}
	}

	/**
	 * 下一步
	 */
	public void doRegisterFirst() {
		progressShow = true;
		final ProgressDialog pd = new ProgressDialog(context);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.abc_data_loding));
		pd.show();

		Map<String, String> parameters = new HashMap<String, String>();
		String action = "";
		if (type == 1)
			action = "1096";
		else if (type == 2)
			action = "1108";

		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_mobile", username);
		parameters.put("u_type", "0");
		HttpPostUtil mHttpPostUtil = null;
		try {
			AppConfig.getInstance().addSign(parameters);
			mHttpPostUtil = AppConfig.getInstance()
					.makeHttpPostUtil(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// WEB request and deal the listener
		new WebRequestUtil(mHttpPostUtil)
				.executeWithOutCache(new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						// 成功
						pd.dismiss();
						doAfterRegisterFirst(String.valueOf(data.get("uid")));
					}

					@Override
					public void onFail(Map<String, Object> data) {
						pd.dismiss();
						Toast.makeText(context, "操作失败:" + data.get("message"),
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError() {
						pd.dismiss();
					}
				});

	}

	/**
	 * 下一步成功后执行的操作.
	 */
	public void doAfterRegisterFirst(String uid) {
		// 返回登录页面
		Intent intent = new Intent(this, RegisterSecondActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("username", username);
		bundle.putInt("type", type);
		bundle.putString("uid", uid);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void back(View view) {
		finish();
	}

}
