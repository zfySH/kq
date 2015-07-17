package cn.kangeqiu.kq.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.MyselfBallAgeView;
import cn.kangeqiu.kq.activity.view.MyselfCityView;
import cn.kangeqiu.kq.activity.view.MyselfDatepickView;
import cn.kangeqiu.kq.activity.view.MyselfLocateView;
import cn.kangeqiu.kq.activity.view.MyselfPlaceView;
import cn.kangeqiu.kq.activity.view.MyselfSingleView;
import cn.kangeqiu.kq.activity.view.MyselfTagsView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.JsonUtil;
import com.nowagme.util.LocationUtil;
import com.nowagme.util.WebCallResultUtil;

public class MyselfMessageDetailActivity extends BaseSimpleActivity {
	private Button saveBtn;
	private LinearLayout content;
	private String msg;
	private String type;

	private MyselfCityView cityView;
	private MyselfLocateView locateView;

	private LocationUtil locationUtil;
	private GeoCoder mSearch;
	private String action = "";

	private MyselfBallAgeView ballview = null;
	private MyselfPlaceView placeview = null;
	private MyselfTagsView tagview = null;
	private MyselfSingleView sexview = null;
	private MyselfDatepickView dateview = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_myself_message_detail);
		type = getIntent().getStringExtra("type");
		msg = getIntent().getStringExtra("content");

		saveBtn = (Button) findViewById(R.id.save);
		content = (LinearLayout) findViewById(R.id.content);

//		locationUtil = LocationUtil.getInstance(this);

		// SDKInitializer.initialize(getApplicationContext());
		// mSearch = GeoCoder.newInstance();
		// mSearch.setOnGetGeoCodeResultListener(geoListener);

		// 居住地
		// locateView = new MyselfLocateView(this);
		// content.addView(locateView.getView(msg));
		cityView = new MyselfCityView(MyselfMessageDetailActivity.this);
		content.addView(cityView.getView(msg));

		saveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// if (msg == null || msg.equals("")) {
				// Toast.makeText(MyselfMessageDetailActivity.this, "不能为空",
				// Toast.LENGTH_SHORT).show();
				// return;
				// }

				initDate(action);
			}
		});
		((TextView) findViewById(R.id.title)).setText(getIntent()
				.getStringExtra("title"));

		// if (type.equals("text")) {
		// action = "1056";
		// MyselfTxtView view = new MyselfTxtView(
		// MyselfMessageDetailActivity.this);
		// content.addView(view.getView(msg));
		// } else if (type.equals("ballage")) {
		// action = "1042";
		// ballview = new MyselfBallAgeView(MyselfMessageDetailActivity.this);
		// content.addView(ballview.getView(msg));
		// } else if (type.equals("place")) {
		// action = "1008";
		// placeview = new MyselfPlaceView(MyselfMessageDetailActivity.this);
		// content.addView(placeview.getView(msg));
		// } else if (type.equals("tag")) {
		// action = "1044";
		// tagview = new MyselfTagsView(MyselfMessageDetailActivity.this);
		// content.addView(tagview.getView(msg));
		// } else if (type.equals("single")) {
		// action = "1057";
		// sexview = new MyselfSingleView(MyselfMessageDetailActivity.this);
		// content.addView(sexview.getView(msg));
		// } else if (type.equals("date")) {
		// action = "1012";
		// dateview = new MyselfDatepickView(MyselfMessageDetailActivity.this);
		// content.addView(dateview.getView(msg));
		// } else if (type.equals("ltext")) {
		// action = "1047";
		// MyselfLargeTxtView view = new MyselfLargeTxtView(
		// MyselfMessageDetailActivity.this);
		// content.addView(view.getView(msg));
		// } else if (type.equals("nativecity")) {
		// // cityView = new MyselfCityView(MyselfMessageDetailActivity.this,
		// 0);
		// // content.addView(cityView.getView(msg));
		// // back.setOnClickListener(new OnClickListener() {
		// //
		// // @Override
		// // public void onClick(View arg0) {
		// // cityView.back();
		// // }
		// // });
		// } else if (type.equals("work")) {
		// action = "1046";
		// workview = new MyselfWorkView(this);
		// content.addView(workview.getView(msg));
		// } else if (type.equals("livecity")) {
		//
		// // locateView = new MyselfLocateView(this);
		// // content.addView(locateView.getView(msg));
		// // cityView = new MyselfCityView(MyselfMessageDetailActivity.this,
		// // 1);
		// // content.addView(cityView.getView(msg));
		// // back.setOnClickListener(new OnClickListener() {
		// //
		// // @Override
		// // public void onClick(View arg0) {
		// // cityView.back();
		// // }
		// // });
		// }
	}

	public void back(View view) {
		finish();
	}

	private void initDate(String action) {
		CPorgressDialog.showProgressDialog(this);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_uid", "" + AppConfig.getInstance().getPlayerId());
		if (action.equals("1056")) {
			parameters.put("u_nickname", msg);
		} else if (action.equals("1042")) {
			msg = ballview.getYear();
			parameters.put("u_veteran", msg + "");

		} else if (action.equals("1008")) {
			msg = placeview.getPlaces();
			parameters.put("u_place", msg);

		} else if (action.equals("1044")) {
			msg = tagview.getPlaces();
			parameters.put("u_tag", msg);
		} else if (action.equals("1057")) {
			msg = sexview.getSex();
			parameters.put("u_sex", msg);
		} else if (action.equals("1012")) {
			msg = dateview.getDate();
			parameters.put("u_year", dateview.getYear());
			parameters.put("u_month", dateview.getMonth());
			parameters.put("u_day", dateview.getDay());

		} else if (action.equals("1047")) {
			parameters.put("u_signature", msg);
		} else if (action.equals("1046")) {
			parameters.put("u_job", msg);
		}
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
	 * 提交信息成功.
	 */
	private void doPensonSuccess(Map<String, Object> data) {
		logi("doPensonSuccess()+++++++++++++++++++++++++++++++++++");
		CPorgressDialog.hideProgressDialog();
		Intent intent = new Intent();
		intent.putExtra("msg", msg);
		intent.putExtra("action", action);
		// 请求代码可以自己设置，这里设置成20
		setResult(20, intent);
		finish();
		Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();

	}

	/**
	 * 提交信息失败.
	 */
	private void doPensonFail(Map<String, Object> data) {
		logi("doPensonFail()");
		CPorgressDialog.hideProgressDialog();

		Toast.makeText(this, data.get("message").toString(), Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * 提交信息错误.
	 */
	private void doPensonErr() {
		CPorgressDialog.hideProgressDialog();

		logi("doPensonErr()");
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

	public void commitContent(String msg) {
		this.msg = msg;
	}

	OnGetGeoCoderResultListener geoListener = new OnGetGeoCoderResultListener() {
		// 反地理编码查询结果回调函数
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				// 没有检测到结果
				Toast.makeText(MyselfMessageDetailActivity.this, "抱歉，未能找到结果",
						Toast.LENGTH_LONG).show();
			}
			// Toast.makeText(MyselfMessageDetailActivity.this,
			// "位置：" + result.getAddress(), Toast.LENGTH_LONG).show();
			locateView.setCity(result.getAddress());
		}

		// 地理编码查询结果回调函数
		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				// 没有检测到结果
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 按下的如果是BACK，同时没有重复
			// if (type.equals("nativecity"))
			// cityView.back();
			// else
				finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// if (type.equals("livecity")) {
		// locationUtil.stopScan();
		// }

	}

	@Override
	protected void onPause() {
		super.onPause();
		// if (type.equals("livecity")) {
		// locationUtil.stopScan();
		// mSearch.destroy();
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
		// if (type.equals("livecity")) {
		// locationUtil.startScan(listener);
		// }
	}

	private BDLocationListener listener = new BDLocationListener() {
		@Override
		public void onReceivePoi(BDLocation arg0) {
		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				Log.i("tag", "更新坐标");
				// userLocation = location;
				// editText.setText("设备位置信息\n\n经度：");
				// editText.append(String.valueOf(location.getLongitude()));
				// editText.append("\n纬度：");
				// editText.append(String.valueOf(location.getLatitude()));
				try {
					// 将用户输入的经纬度值转换成int类型
					int longitude = (int) (1000000 * Double.parseDouble(String
							.valueOf(location.getLongitude())));
					int latitude = (int) (1000000 * Double.parseDouble(String
							.valueOf(location.getLatitude())));

					LatLng latLng = new LatLng(

					Float.parseFloat(String.valueOf(location.getLatitude())),

					(Float.parseFloat(String.valueOf(location.getLongitude()))));

					mSearch.reverseGeoCode(new ReverseGeoCodeOption()
							.location(latLng));
					// 查询该经纬度值所对应的地址位置信息
					// mMKSearch.reverseGeocode(new GeoPoint(latitude,
					// longitude));
				} catch (Exception e) {
					// addressTextView.setText("查询出错，请检查您输入的经纬度值！");
					Toast.makeText(MyselfMessageDetailActivity.this, "",
							Toast.LENGTH_LONG).show();
				}
			} else {

			}
			// editText.getEditableText().clear();
		}
	};
}
