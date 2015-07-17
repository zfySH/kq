package com.nowagme.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.easemob.applib.model.MessageItemModel;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;

public class SelectMapActivity extends BaseSimpleActivity implements
		OnClickListener, OnMapClickListener, BDLocationListener,
		OnGetPoiSearchResultListener, OnGetSuggestionResultListener {
	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["
			+ SelectMapActivity.class.getName() + "]";

	private Button btn_back, btn_save, btn_search;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private List<Marker> delMarkers = new ArrayList<Marker>();
	GeoCoder mSearch = null; // 搜索模块

	// 定位相关
	LocationClient mLocClient;
	boolean isFirstLoc = true;// 是否首次定位
	private LatLng selectedLatLng;

	private Context context;
	@SuppressWarnings("unused")
	private boolean progressShow;

	BitmapDescriptor bd = BitmapDescriptorFactory
			.fromResource(R.drawable.abc_icon_gcoding);

	String map_latitude, map_longitude, map_address, map_name, court_id;

	private RadioButton map_rd, court_rd;
	private RadioGroup group;
	private ListView courts;

	private EditText search_edit;
	private List<MessageItemModel> list = new ArrayList<MessageItemModel>();

	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;

	private int style = 0;// 0=地图；1=球场

	private String type;
	private LinearLayout abc_fragment_address__tab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		logi("onCreate");
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.abc_select_map);
		context = this;

		// 获取调用者传递来的参数
		initIntentData();

		// 初始化视图
		initView();

		// 初始化控件
		initData();

	}

	private void doPullDate(String action, WebRequestUtilListener listen) {
		CPorgressDialog.showProgressDialog(this);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		parameters.put("u_serach_word", search_edit.getText().toString());

		HttpPostUtil mHttpPostUtil = null;
		try {
			AppConfig.getInstance().addSign(parameters);
			mHttpPostUtil = AppConfig.getInstance()
					.makeHttpPostUtil(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// WEB request and deal the listener
		new WebRequestUtil(mHttpPostUtil).executeWithOutCache(listen);
	}

	/**
	 * 获取调用者传递来的参数
	 */
	private void initIntentData() {
		// get data
		Bundle extras = getIntent().getExtras();
		map_latitude = extras.getString("map_latitude");
		map_longitude = extras.getString("map_longitude");
		type = extras.getString("type");
		// map_name = extras.getString("map_name");
		// map_address = extras.getString("map_address");
		if (map_latitude.length() > 0) {
			selectedLatLng = new LatLng(Double.parseDouble(map_latitude),
					Double.parseDouble(map_longitude));
		}
	}

	/**
	 * 初始化控件.
	 */
	private void initView() {
		logi("initView");
		btn_back = (Button) findViewById(R.id.abc_select_map__btn_back);
		btn_save = (Button) findViewById(R.id.abc_select_map__btn_save);
		btn_search = (Button) findViewById(R.id.search_btn);
		mMapView = (MapView) findViewById(R.id.bmapView);
		courts = (ListView) findViewById(R.id.court_list);
		map_rd = (RadioButton) findViewById(R.id.map_radio);
		court_rd = (RadioButton) findViewById(R.id.count_radio);
		group = (RadioGroup) findViewById(R.id.address_group);
		search_edit = (EditText) findViewById(R.id.search_edit);
		abc_fragment_address__tab = (LinearLayout) findViewById(R.id.abc_fragment_address__tab);
		map_rd.setChecked(true);

		// courts.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1,
		// int position, long arg3) {
		// adapter.setChioced(position);
		//
		// map_latitude = list.get(position).getLat();
		// map_longitude = list.get(position).getLng();
		// map_address = list.get(position).getContent();
		// map_name = list.get(position).getName();
		// court_id = list.get(position).getId();
		// }
		// });

		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == map_rd.getId()) {
					style = 0;
					mMapView.setVisibility(View.VISIBLE);
					courts.setVisibility(View.GONE);
				} else if (checkedId == court_rd.getId()) {
					style = 1;
					mMapView.setVisibility(View.GONE);
					courts.setVisibility(View.VISIBLE);
				}
			}
		});
		// btn_search.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// String search = search_edit.getText().toString();
		// if (search.equals("") || search.length() <= 0) {
		// Toast.makeText(SelectMapActivity.this, "请输入关键字",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		//
		// // 关键字搜索球场
		// if (style == 1) {
		// doPullDate("1086", new WebRequestUtilListener() {
		//
		// @Override
		// public void onSucces(Map<String, Object> data) {
		// CPorgressDialog.hideProgressDialog();
		// List<Map<String, String>> courts = (List<Map<String, String>>) data
		// .get("courts");
		// list.clear();
		// for (int i = 0; i < courts.size(); i++) {
		// MessageItemModel item = new MessageItemModel(
		// "0", courts.get(i).get("name"), courts
		// .get(i).get("addr"), courts
		// .get(i).get("icon"));
		// item.setLat(courts.get(i).get("lat"));
		// item.setLng(courts.get(i).get("lng"));
		// item.setId(courts.get(i).get("id"));
		// list.add(item);
		//
		// }
		// adapter.setItem(list);
		// }
		//
		// @Override
		// public void onFail(Map<String, Object> data) {
		// CPorgressDialog.hideProgressDialog();
		// Toast.makeText(SelectMapActivity.this,
		// String.valueOf(data.get("message")),
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// @Override
		// public void onError() {
		// CPorgressDialog.hideProgressDialog();
		// }
		// });
		// } else if (style == 0) {// 关键字搜索地图
		// /**
		// * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
		// */
		// // mSuggestionSearch
		// // .requestSuggestion((new SuggestionSearchOption())
		// // .keyword(cs.toString()).city(city));
		// mPoiSearch.searchInCity((new PoiCitySearchOption())
		// .city("上海").keyword(search).pageNum(10));
		// }
		// }
		// });

		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);

		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);

		// mSearch
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			@Override
			public void onGetGeoCodeResult(GeoCodeResult arg0) {

			}

			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR) {
					Toast.makeText(context, "抱歉，未能找到结果", Toast.LENGTH_LONG)
							.show();
					return;
				}
				map_address = result.getAddress();
				Toast.makeText(context, result.getAddress(), Toast.LENGTH_LONG)
						.show();
			}
		});

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(this);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		btn_back.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		mBaiduMap.setOnMapClickListener(this);

		// 定位到上次选择的地图点
		if (selectedLatLng != null) {
			MapStatusUpdate u = MapStatusUpdateFactory
					.newLatLng(selectedLatLng);
			mBaiduMap.animateMapStatus(u);

			// 删除旧的标记
			if (delMarkers != null && delMarkers.size() > 0) {
				for (Marker oldMarker : delMarkers) {
					oldMarker.remove();
				}
			}
			OverlayOptions oo = new MarkerOptions().position(selectedLatLng)
					.icon(bd).zIndex(9).draggable(true);
			Marker mMarker = (Marker) (mBaiduMap.addOverlay(oo));
			delMarkers.add(mMarker);
		}

		// 根据来源重新初始化界面
		initViewByFrom();
	}

	/**
	 * 根据来源重新初始化界面
	 */
	private void initViewByFrom() {
		if (type != null && !type.equals("")) {
			if (type.equals("map")) {
				abc_fragment_address__tab.setVisibility(View.GONE);
				btn_save.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 初始化数据.
	 */
	private void initData() {

		logi("initData");

		// 获取球员信息.
		doPullData();
	}

	/**
	 * 获取球员信息
	 */
	private void doPullData() {

		logi("doPullData()");

	}

	/**
	 * log information
	 * 
	 * @param msg
	 */
	public static void logi(String msg) {
		Log.i(TAG, CLASS_NAME + " " + msg);
	}

	@Override
	public void onClick(View v) {
		logi("onClick(View v)");
		int id = v.getId();
		switch (id) {
		case R.id.abc_select_map__btn_back:// 返回
			logi("R.id.abc_select_map__btn_back");
			finish();
			break;

		case R.id.abc_select_map__btn_save:// 保存
			logi("R.id.abc_select_map__btn_save");
			// Intent newIntent = new Intent(context,
			// CreateActivityActivity.class);
			// Bundle extras = new Bundle();
			// extras.putString("map_latitude", map_latitude);
			// extras.putString("map_longitude", map_longitude);
			// extras.putString("map_address", map_address);
			// extras.putString("map_name", map_name);
			// extras.putString("court_id", court_id);
			//
			// newIntent.putExtras(extras);
			// setResult(RESULT_OK, newIntent);
			// finish();
			break;
		}
	}

	@Override
	public void onMapClick(LatLng point) {

		map_latitude = String.valueOf(point.latitude);
		map_longitude = String.valueOf(point.longitude);
		court_id = "0";
		map_name = "";
		// 删除旧的标记
		if (delMarkers != null && delMarkers.size() > 0) {
			for (Marker oldMarker : delMarkers) {
				oldMarker.remove();
			}
		}
		OverlayOptions oo = new MarkerOptions().position(point).icon(bd)
				.zIndex(9).draggable(true);
		Marker mMarker = (Marker) (mBaiduMap.addOverlay(oo));
		delMarkers.add(mMarker);

		// 获取点击点的地址
		// 反Geo搜索
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
	}

	@Override
	public boolean onMapPoiClick(MapPoi poi) {
		map_latitude = String.valueOf(poi.getPosition().latitude);
		map_longitude = String.valueOf(poi.getPosition().longitude);
		court_id = "0";
		map_name = poi.getName();

		// 删除旧的标记
		if (delMarkers != null && delMarkers.size() > 0) {
			for (Marker oldMarker : delMarkers) {
				oldMarker.remove();
			}
		}
		OverlayOptions oo = new MarkerOptions().position(poi.getPosition())
				.icon(bd).zIndex(9).draggable(true);
		Marker mMarker = (Marker) (mBaiduMap.addOverlay(oo));
		delMarkers.add(mMarker);

		Toast.makeText(context, poi.getName(), Toast.LENGTH_LONG).show();
		// 反Geo搜索
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(poi
				.getPosition()));
		return false;
	}

	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// 退出时销毁定位
		mLocClient.stop();
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		mMapView.onDestroy();
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();
		super.onDestroy();
		// 回收 bitmap 资源
		bd.recycle();
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		// map view 销毁后不在处理新接收的位置
		if (location == null || mMapView == null)
			return;
		MyLocationData locData = new MyLocationData.Builder()
				.accuracy(location.getRadius())
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();
		mBaiduMap.setMyLocationData(locData);
		if (isFirstLoc && selectedLatLng == null) {
			isFirstLoc = false;
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	@Override
	public void onReceivePoi(BDLocation location) {

	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(SelectMapActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(SelectMapActivity.this,
					result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(SelectMapActivity.this, "未找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(SelectMapActivity.this, strInfo, Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult arg0) {
		// TODO Auto-generated method stub

	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			return true;
		}
	}
}
