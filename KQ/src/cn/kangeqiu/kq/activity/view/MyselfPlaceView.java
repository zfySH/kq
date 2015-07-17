package cn.kangeqiu.kq.activity.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.adapter.PlaceChooseComponentsAdapter;
import cn.kangeqiu.kq.adapter.PlaceComponentsAdapter;

import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.JsonUtil;
import com.nowagme.util.WebCallResultUtil;

public class MyselfPlaceView {
	private Activity context;
	private LayoutInflater inflater;
	private GridView grid1, grid2;
	private PlaceComponentsAdapter adapter;
	private PlaceChooseComponentsAdapter adapterChoose;
	private List<String> list;
	private List<String> deleteList;
	private List<String> listChoose;

	private Map<String, Integer> positionMap;

	public MyselfPlaceView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public String getPlaces() {
		return adapter.getData();
	}

	public View getView(String msg) {
		View view = inflater.inflate(R.layout.abc_myself_message_place_item,
				null);
		list = new ArrayList<String>();
		listChoose = new ArrayList<String>();
		adapter = new PlaceComponentsAdapter(context);
		adapterChoose = new PlaceChooseComponentsAdapter(context,1);
		positionMap = new HashMap<String, Integer>();
		deleteList = new ArrayList<String>();

		grid1 = (GridView) view.findViewById(R.id.grid1);
		grid2 = (GridView) view.findViewById(R.id.grid2);

		grid1.setAdapter(adapter);
		grid2.setAdapter(adapterChoose);

		grid1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				deleteList.clear();
				deleteList.add(list.get(position));
				list.remove(position);
				adapter.notifyDataSetChanged();
				adapterChoose.chiceStateByChange(deleteList);
				// adapter.chiceState(position);
			}
		});
		grid2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				adapterChoose.chiceState(position, adapter);
			}
		});
		for (int i = 0; i < msg.split(",").length; i++) {
			list.add(msg.split(",")[i]);
		}
		adapter.setItems(list);
		adapter.notifyDataSetChanged();
		initDate();
		return view;
	}

	private void initDate() {
		CPorgressDialog.showProgressDialog(context);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", "1007");
		parameters.put("app_platform", "0");
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
	 * 调取球场位置信息成功.
	 */
	private void doPensonSuccess(Map<String, Object> data) {
		logi("doPensonSuccess()");
		CPorgressDialog.hideProgressDialog();
		List<Map<String, String>> placelist = (List<Map<String, String>>) data
				.get("places");
		for (int i = 0; i < placelist.size(); i++) {
			listChoose.add(placelist.get(i).get("name"));

		}
		adapterChoose.setItems(listChoose);

		adapterChoose.chiceStateByChange(list);
		adapterChoose.notifyDataSetChanged();

	}

	/**
	 * 调取球场位置信息失败.
	 */
	private void doPensonFail(Map<String, Object> data) {
		logi("doPensonFail()");
		CPorgressDialog.hideProgressDialog();
		Toast.makeText(context, data.get("message").toString(),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * 调取球场位置信息错误.
	 */
	private void doPensonErr() {
		logi("doPensonErr()");
		CPorgressDialog.hideProgressDialog();

	}

	class postTask extends AsyncTask<HttpPostUtil, Integer, WebCallResultUtil> {
		@Override
		protected void onProgressUpdate(Integer... values) {
			logi("void onProgressUpdate(Integer... values)");
		}

		@Override
		protected void onPostExecute(WebCallResultUtil result) {
			logi("void onPostExecute(String result)");
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
			logi("String doInBackground(HttpPostUtil... args)");
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
			logi("message=" + message);
			mWebCallResultUtil.setResponseText(message);
			return mWebCallResultUtil;
		}

	}

	/**
	 * log information
	 * 
	 * @param msg
	 */
	public static void logi(String msg) {
		Log.i("TAG", " " + msg);
	}
}
