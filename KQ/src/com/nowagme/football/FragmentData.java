package com.nowagme.football;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.IntegralView;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.util.WebRequestUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.TabPageIndicator;

public class FragmentData extends Fragment {

	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "[" + FragmentData.class.getName()
			+ "]";
	// private static final String[] TITLE = new String[] { "中超", "英超", "意甲",
	// "西甲", "德甲", "中超", "英超", "意甲", "西甲" };
	private final List<String> TITLE = new ArrayList<String>();
	private final List<Fragment> mfragment = new ArrayList<Fragment>();
	private ViewPager pager;
	private int id;
	private TabPageIndicator indicator;
	private IntegralView mFragment;
	private View view;
	private Activity context;
	private JSONArray records = new JSONArray();
	private FragmentPagerAdapter adapter;
	private String club_name;

	@Override
	public void onAttach(Activity activity) {
		logi("onAttach(Activity activity)");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		logi("onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)");

		if (view != null) {
			container = (ViewGroup) view.getParent();
			if (null != container) {
				container.removeView(view);
			}
		}
		view = inflater.inflate(R.layout.abc_fragment_data, null);
		adapter = new TabPageIndicatorAdapter(getChildFragmentManager());
		pager = (ViewPager) view.findViewById(R.id.vpager);
		indicator = (TabPageIndicator) view.findViewById(R.id.indicator);
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);

		// indicator.setOnPageChangeListener(new OnPageChangeListener() {
		//
		// @Override
		// public void onPageSelected(int arg0) {
		// // Toast.makeText(getActivity(), TITLE[arg0],
		// // Toast.LENGTH_SHORT)
		// // .show();
		// // id=arg0;
		// }
		//
		// @Override
		// public void onPageScrolled(int arg0, float arg1, int arg2) {
		//
		// }
		//
		// @Override
		// public void onPageScrollStateChanged(int arg0) {
		//
		// }
		// });
		return view;
	}

	private void initData() {
		doPullDate(true, "2055", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				// CPorgressDialog.hideProgressDialog();
				try {
					TITLE.clear();
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						records = resp.getJson().getJSONArray("records");
						for (int i = 0; i < records.length(); i++) {
							try {
								club_name = records.getJSONObject(i)
										.getString("club_name").toString();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							TITLE.add(club_name);
							mfragment.add(new IntegralView(club_name));

						}

						adapter.notifyDataSetChanged();

						indicator.notifyDataSetChanged();
					} else {
						Toast.makeText(getActivity(),
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
				Toast.makeText(getActivity(), resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	class TabPageIndicatorAdapter extends FragmentPagerAdapter {
		public TabPageIndicatorAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return mfragment.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// return club_name;
			return TITLE.get(position);
		}

		@Override
		public int getCount() {
			return TITLE.size();
		}

	}

	/**
	 * log information
	 * 
	 * @param msg
	 */
	public static void logi(String msg) {
		Log.i(TAG, CLASS_NAME + " " + msg);
	}

	private void doPullDate(boolean isLazyLoad, String action,
			MCHttpCallBack listen) {
		// CPorgressDialog.showProgressDialog(getActivity());

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("app_version", "1"));
		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);

	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("data"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		TCAgent.onPageStart(getActivity(), "data");

	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("data"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
											// onPageEnd 在onPause
											// 之前调用,因为 onPause 中会保存信息
		TCAgent.onPageEnd(getActivity(), "data");
	}
}
