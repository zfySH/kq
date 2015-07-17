package cn.kangeqiu.kq.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
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
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.WebRequestUtilListener;

public class CreateTeamTimeActivity extends BaseSimpleActivity {
	private Button back, save;
	private ListView list;
	private MyselfMessageAdapter adapter;
	private List<MessageItemModel> listModel;

	private Map<String, String> city = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_team_create_tags);
		back = (Button) findViewById(R.id.back);
		save = (Button) findViewById(R.id.save);
		list = (ListView) findViewById(R.id.city_list);

		city.put("country", "");
		city.put("province", "");
		city.put("city", "");
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
		// String msg = adapter.getData();
		// if (msg == null || msg.equals("")) {
		// Toast.makeText(CreateTeamTimeActivity.this, "请选择标签",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		// Intent data = new Intent();
		// data.putExtra("msg", msg);
		// // 请求代码可以自己设置，这里设置成20
		// setResult(20, data);
		// // 关闭掉这个Activity
		// finish();
		// }
		// });
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

	private void initView(String parentId) {
		initData("1019", parentId, new WebRequestUtilListener() {

			@Override
			public void onSucces(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();

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
					MessageItemModel item = new MessageItemModel("", list
							.get(i).get("name"), isShow, id);
					listModel.add(item);
				}

				// adapter.setItem(listModel);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onFail(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(CreateTeamTimeActivity.this,
						String.valueOf(data.get("message")), Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onError() {
				CPorgressDialog.hideProgressDialog();
			}
		});
	}

	private void initData(String action, String parentId,
			WebRequestUtilListener listen) {
		CPorgressDialog.showProgressDialog(this);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_uid", "" + AppConfig.getInstance().getPlayerId());
		parameters.put("u_parent_id", parentId);
		HttpPostUtil mHttpPostUtil = null;
		try {
			AppConfig.getInstance().addSign(parameters);
			mHttpPostUtil = AppConfig.getInstance()
					.makeHttpPostUtil(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// WEB request and deal the listener
		// new WebRequestUtil(mHttpPostUtil).execute(listen);
	}

	// private void doPullDate(String parentId) {
	// CPorgressDialog.showProgressDialog(this);
	// Map<String, String> parameters = new HashMap<String, String>();
	// parameters.put("app_action", "1013");
	// parameters.put("app_platform", "0");
	// parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
	// parameters.put("u_area_id", parentId);
	// parameters.put("u_type", "1");
	// HttpPostUtil mHttpPostUtil = null;
	// try {
	// AppConfig.getInstance().addSign(parameters);
	// mHttpPostUtil = AppConfig.getInstance()
	// .makeHttpPostUtil(parameters);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// // WEB request and deal the listener
	// new WebRequestUtil(mHttpPostUtil).execute(new WebRequestUtilListener() {
	// @Override
	// public void onSucces(Map<String, Object> data) {
	// logi("onSucces");
	// logi(data.toString());
	// CPorgressDialog.hideProgressDialog();
	// Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
	// context.finish();
	// }
	//
	// @Override
	// public void onFail(Map<String, Object> data) {
	// logi("onFail");
	// CPorgressDialog.hideProgressDialog();
	// Toast.makeText(context, data.get("message").toString(),
	// Toast.LENGTH_SHORT).show();
	// logi(data.toString());
	// }
	//
	// @Override
	// public void onError() {
	// CPorgressDialog.hideProgressDialog();
	// Toast.makeText(context, "请检查您的网络", Toast.LENGTH_SHORT).show();
	// logi("onError");
	// }
	//
	// });
	// }
}
