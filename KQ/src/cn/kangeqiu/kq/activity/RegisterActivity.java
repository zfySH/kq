/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.kangeqiu.kq.activity;

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
import android.widget.Toast;
import cn.kangeqiu.kq.R;

import com.nowagme.football.AppConfig;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;

/**
 * 注册页
 * 
 */
public class RegisterActivity extends BaseActivity {
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;

	private String username, pwd;
	private Context context;
	@SuppressWarnings("unused")
	private boolean progressShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		context = this;
		userNameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
	}

	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		String st1 = getResources().getString(
				R.string.User_name_cannot_be_empty);
		String st2 = getResources()
				.getString(R.string.Password_cannot_be_empty);
		String st3 = getResources().getString(
				R.string.Confirm_password_cannot_be_empty);
		String st4 = getResources().getString(R.string.Two_input_password);
		String st5 = getResources().getString(R.string.Is_the_registered);
		final String st6 = getResources().getString(
				R.string.Registered_successfully);
		username = userNameEditText.getText().toString().trim();
		pwd = passwordEditText.getText().toString().trim();
		String confirm_pwd = confirmPwdEditText.getText().toString().trim();
		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, st1, Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, st2, Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, st3, Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		} else if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, st4, Toast.LENGTH_SHORT).show();
			return;
		}

		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			// 开始注册
			doRegister();
		}
	}

	/**
	 * 开始注册
	 */
	public void doRegister() {
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
		parameters.put("app_action", "1065");
		parameters.put("app_platform", "0");
		parameters.put("u_mobile", username);
		parameters.put("u_pwd", pwd);
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
						// 注册成功
						doAfterRegister();
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
						Toast.makeText(context, "操作失败.", Toast.LENGTH_SHORT)
								.show();
					}
				});

	}

	/**
	 * 注册成功后执行的操作.
	 */
	public void doAfterRegister() {
		// 返回登录页面
		Intent intent = new Intent(this, LoginActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("username", username);
		bundle.putString("pwd", pwd);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		// 返回
		finish();
	}

	public void back(View view) {
		finish();
	}

}
