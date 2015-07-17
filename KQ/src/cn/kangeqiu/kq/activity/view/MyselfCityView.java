package cn.kangeqiu.kq.activity.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.adapter.MyselfMessageAdapter;

import com.easemob.applib.model.MessageItemModel;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.WebRequestUtil;

public class MyselfCityView {
	private Activity context;
	private LayoutInflater inflater;
	private ListView list;
	private MyselfMessageAdapter adapter;
	private List<MessageItemModel> listModel;
	// private int step = 0;
	private Map<Integer, String> map = new HashMap<Integer, String>();
	private int type = 0;

	// private Map<String, String> county_map = new HashMap<String, String>();
	// private String country_name = "";

	private JSONArray areas = new JSONArray();
	private JSONObject wtf = null;
	// private String parentId = "0";

	private String countryName = "";
	private String provinceId = "";
	private String cityId = "";

	public void back() {

		// if (step == 0)
		context.finish();
		// else {
		// step--;
		// String tId = map.get(step);
		// initView(tId);
		//
		// }

	}

	public MyselfCityView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
		listModel = new ArrayList<MessageItemModel>();
		adapter = new MyselfMessageAdapter(context);

	}

	public View getView(String msg) {
		View view = inflater.inflate(R.layout.abc_myself_message_citys_item,
				null);
		list = (ListView) view.findViewById(R.id.city_list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// MessageItemModel item = listModel.get(position);
				// country_name += (listModel.get(position)).getName();
				try {
					countryName += "  "
							+ areas.getJSONObject(position).getString("name");
					if (wtf.get("type").toString().equals("0")) {// 省份
						// step++;
						// map.put(step, parentId);
						provinceId = areas.getJSONObject(position).getString(
								"id");
						type = 1;
						doPullDate(provinceId);
					} else {// 城市
						// 点击保存
						cityId = areas.getJSONObject(position).getString("id")
								.toString();
						// doPullDate(item.getId());
						back2Home();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
		doPullDate("");
		return view;
	}

	private void back2Home() {
		Intent intent = new Intent();
		intent.putExtra("name", countryName);
		intent.putExtra("provinceId", provinceId);
		intent.putExtra("cityId", cityId);
		// 请求代码可以自己设置，这里设置成20
		context.setResult(20, intent);
		context.finish();
	}

	private void doPullDate(String provinceId) {
		CPorgressDialog.showProgressDialog(context);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", "2022"));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_type", type + ""));
		pair.add(new BasicNameValuePair("u_province_id", provinceId));

		new WebRequestUtil().execute(true,
				AppConfig.getInstance().makeUrl(Integer.parseInt("2022")),
				pair, new MCHttpCallBack() {
					@Override
					public void onSuccess(MCHttpResp resp) {
						super.onSuccess(resp);
						CPorgressDialog.hideProgressDialog();
						try {
							String resultCode = resp.getJson().getString(
									"result_code");
							if (resultCode.equals("0")) {
								wtf = resp.getJson();
								areas = (JSONArray) resp.getJson()
										.getJSONArray("areas");
								adapter.setItem(areas);
							} else {
								Toast.makeText(context,
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
						Toast.makeText(context, resp.getErrorMessage(),
								Toast.LENGTH_SHORT).show();
						CPorgressDialog.hideProgressDialog();
					}
				});

	}

	/**
	 * log information
	 * 
	 * @param msg
	 */
	public static void logi(String msg) {
		Log.i("TAG", " " + msg);
	}

	// @Override
	// public void onItemClick(AdapterView<?> arg0, View view, int position,
	// long arg3) {
	// MessageItemModel item = listModel.get(position);
	// if (item.isShow()) {
	// step++;
	// initView(item.getId());
	// }
	// }
}
