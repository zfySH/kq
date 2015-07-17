package cn.kangeqiu.kq.activity;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;

public class EditActivity extends BaseActivity {
	private EditText editText;
	private String roomId = "";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_edit);

		editText = (EditText) findViewById(R.id.edittext);
		String title = getIntent().getStringExtra("title");
		String data = getIntent().getStringExtra("data");
		roomId = getIntent().getStringExtra("roomId");
		if (title != null)
			((TextView) findViewById(R.id.tv_title)).setText(title);
		if (data != null)
			editText.setText(data);
		editText.setSelection(editText.length());

	}

	public void save(View view) {
		// setResult(RESULT_OK,
		// new Intent().putExtra("data", editText.getText().toString()));
		// finish();\

		if (!TextUtils.isEmpty(editText.getText().toString())) {
			CPorgressDialog.showProgressDialog(this);
			doPullDate(false, "2052", new MCHttpCallBack() {
				@Override
				public void onSuccess(MCHttpResp resp) {
					super.onSuccess(resp);
					CPorgressDialog.hideProgressDialog();
					try {
						String resultCode = resp.getJson().getString(
								"result_code");
						if (resultCode.equals("0")) {
							setResult(RESULT_OK, new Intent().putExtra("data",
									editText.getText().toString()));
							finish();
						} else {
							Toast.makeText(EditActivity.this,
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
				}
			});
		} else {
			Toast.makeText(EditActivity.this, "房间名不能为空", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void doPullDate(boolean isLazyLoad, String action,
			MCHttpCallBack listen) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("app_version", UpdataUtil
				.getAppVersion(this)));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_room_id", roomId));
		pair.add(new BasicNameValuePair("u_room_name", editText.getText()
				.toString()));

		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}
}
