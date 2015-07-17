package cn.kangeqiu.kq.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.adapter.MyselfMessageAdapter;

import com.easemob.applib.model.MessageItemModel;
import com.nowagme.football.AppConfig;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.JsonUtil;
import com.nowagme.util.WebCallResultUtil;

public class CreateTeamCityActivity extends BaseSimpleActivity {

	private Button back, save;
	private ListView list;
	private MyselfMessageAdapter adapter;
	private List<MessageItemModel> listModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_create_team_city);
		back = (Button) findViewById(R.id.back);
		save = (Button) findViewById(R.id.save);
		listModel = new ArrayList<MessageItemModel>();
		adapter = new MyselfMessageAdapter(this);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		save.setVisibility(View.INVISIBLE);
		// save.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// Intent data = new Intent();
		// data.putExtra("msg", msg);
		// // 请求代码可以自己设置，这里设置成20
		// setResult(20, data);
		// // 关闭掉这个Activity
		// finish();
		// }
		// });

		list = (ListView) findViewById(R.id.city_list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				MessageItemModel item = listModel.get(position);
				if (item.isShow()) {
					// step++;
					// map.put(step, parentId);
					initView(item.getId());
				} else {
					// 点击保存
					// doPullDate(item.getId());
					Intent data = new Intent();
					data.putExtra("msg", item.getId());
					// 请求代码可以自己设置，这里设置成20
					setResult(40, data);
					// 关闭掉这个Activity
					finish();
				}
			}

		});
		initView("0");
	}

	// private void doPullDate(String action, WebRequestUtilListener listen) {
	// CPorgressDialog.showProgressDialog(this);
	// Map<String, String> parameters = new HashMap<String, String>();
	// parameters.put("app_action", action);
	// parameters.put("app_platform", "0");
	// HttpPostUtil mHttpPostUtil = null;
	// try {
	// AppConfig.getInstance().addSign(parameters);
	// mHttpPostUtil = AppConfig.getInstance()
	// .makeHttpPostUtil(parameters);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// // WEB request and deal the listener
	// new WebRequestUtil(mHttpPostUtil).execute(listen);
	// }

	private void initView(String parentId) {

		initDate(parentId);
	}

	private void initDate(String parentId) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", "1011");
		parameters.put("app_platform", "0");
		parameters.put("u_uid", "" + AppConfig.getInstance().getPlayerId());
		parameters.put("u_parent_id", parentId);
		try {
			AppConfig.getInstance().addSign(parameters);
			new postTask().execute(AppConfig.getInstance().makeHttpPostUtil(
					parameters));
		} catch (Exception e) {
			e.printStackTrace();
			doPensonErr();
		}
	}

	/**
	 * 调取城市信息成功.
	 */
	private void doPensonSuccess(Map<String, Object> data) {

		listModel.clear();
		List<Map<String, String>> list = (List<Map<String, String>>) data
				.get("areas");
		for (int i = 0; i < list.size(); i++) {
			String child = list.get(i).get("child");
			boolean isShow = false;
			if (child.equals("true"))
				isShow = true;
			else
				isShow = false;

			String id = list.get(i).get("id");
			MessageItemModel item = new MessageItemModel("", list.get(i).get(
					"name"), isShow, id);
			listModel.add(item);
		}

//		adapter.setItem(listModel);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 调取城市信息失败.
	 */
	private void doPensonFail(Map<String, Object> data) {
		Toast.makeText(this, data.get("message").toString(), Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * 调取城市信息错误.
	 */
	private void doPensonErr() {
		Toast.makeText(this, "请检查您的网络", Toast.LENGTH_SHORT).show();
	}

	class postTask extends AsyncTask<HttpPostUtil, Integer, WebCallResultUtil> {
		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		@Override
		protected void onPostExecute(WebCallResultUtil result) {
			String responseText = result.getResponseText();
			if (result.isCallRight()) {
				try {
					Map<String, Object> data = JsonUtil.parse(responseText);
					String result_code = (String) data.get("result_code");
					if ("0".equals(result_code)) {
						doPensonSuccess(data);
					} else {
						doPensonFail(data);
					}
				} catch (Exception e) {
					e.printStackTrace();
					doPensonErr();
				}
			} else {
				doPensonErr();
			}
		}

		@Override
		protected WebCallResultUtil doInBackground(HttpPostUtil... args) {
			HttpPostUtil httpPostUtil = args[0];
			String message = null;
			WebCallResultUtil mWebCallResultUtil = new WebCallResultUtil();
			try {
				boolean isOK = httpPostUtil.submit();
				if (isOK) {
					message = httpPostUtil.getResponseText();
					mWebCallResultUtil.setCallRight(true);
				} else {
					message = "提交失败.";
					mWebCallResultUtil.setCallRight(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
				mWebCallResultUtil.setCallRight(false);
				message = "提交错误:" + e.getMessage();
			}
			mWebCallResultUtil.setResponseText(message);
			return mWebCallResultUtil;
		}

	}

}
