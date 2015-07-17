package cn.kangeqiu.kq.activity.view;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.R.drawable;
import cn.kangeqiu.kq.adapter.IntegralAdapter;
import cn.kangeqiu.kq.adapter.ShooterAdapter;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.WebRequestUtil;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("ValidFragment")
public class IntegralView extends Fragment implements OnClickListener {

	private LinearLayout list;
	private IntegralAdapter adapter;
	private ShooterAdapter adapter1;
	private JSONArray records = new JSONArray();
	private JSONArray shooterrecords = new JSONArray();

	private String record;
	private Activity context;
	private TextView txt_income, txt_expend;
	private LinearLayout rel_income, rel_expend;
	private RelativeLayout top, top1;
	private int type, id;
	private View view;

	public IntegralView(String records) {
		this.record = records;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view != null) {
			container = (ViewGroup) view.getParent();
			if (null != container) {
				container.removeView(view);
			}
		}
		adapter = new IntegralAdapter(getActivity());
		adapter1 = new ShooterAdapter(getActivity());
		initData();
		type = 0;
		view = inflater.inflate(R.layout.integral_view, container, false);
		initView(view);

		// initView();

		return view;
	}

	private void initView() {
		list.removeAllViews();
		try {
			if (type == 0) {
				// list.setAdapter(adapter);
				for (int i = 0; i < records.length(); i++) {
					TeamScoreView view = new TeamScoreView(getActivity());

					list.addView(view.getView(records.getJSONObject(i)));

				}
			} else if (type == 2) {
				// list.setAdapter(adapter1);
				for (int i = 0; i < shooterrecords.length(); i++) {
					ShooterView view = new ShooterView(getActivity());
					list.addView(view.getView(shooterrecords.getJSONObject(i)));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initData() {
		doPullDate(true, "2054", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				// CPorgressDialog.hideProgressDialog();
				try {

					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						records = resp.getJson().getJSONArray("records");
						// adapter.setItem(records);
						// initView();

						doPullDate(true, "2059", new MCHttpCallBack() {
							@Override
							public void onSuccess(MCHttpResp resp) {
								super.onSuccess(resp);
								// CPorgressDialog.hideProgressDialog();
								try {

									String resultCode = resp.getJson()
											.getString("result_code");
									if (resultCode.equals("0")) {
										shooterrecords = resp.getJson()
												.getJSONArray("records");
										// adapter1.setItem(records);
										initView();
									} else {
										Toast.makeText(
												getActivity(),
												resp.getJson().getString(
														"message"),
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
								Toast.makeText(getActivity(),
										resp.getErrorMessage(),
										Toast.LENGTH_SHORT).show();
							}
						});
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

	private void initView(View v) {
		txt_income = (TextView) v.findViewById(R.id.txt_income);
		txt_expend = (TextView) v.findViewById(R.id.txt_expend);
		rel_income = (LinearLayout) v.findViewById(R.id.rel_income);
		rel_expend = (LinearLayout) v.findViewById(R.id.rel_expend);
		list = (LinearLayout) v.findViewById(R.id.list);
		top = (RelativeLayout) v.findViewById(R.id.top);
		top1 = (RelativeLayout) v.findViewById(R.id.top1);
		rel_income.setOnClickListener(this);
		rel_expend.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rel_income:
			// list.setDividerHeight((int) 0.5);
			txt_income.setTextColor(Color.parseColor("#ffffff"));
			txt_expend.setTextColor(Color.parseColor("#9F9D9E"));
			rel_income.setBackgroundColor(Color.parseColor("#44CDD7"));
			rel_expend
					.setBackgroundResource(drawable.abc_button_roundcorner_green1);
			top.setVisibility(View.VISIBLE);
			top1.setVisibility(View.GONE);
			type = 0;
			initView();
			break;
		case R.id.rel_expend:
			// list.setDividerHeight(30);
			txt_income.setTextColor(Color.parseColor("#9F9D9E"));
			txt_expend.setTextColor(Color.parseColor("#ffffff"));
			rel_expend.setBackgroundColor(Color.parseColor("#44CDD7"));
			rel_income
					.setBackgroundResource(drawable.abc_button_roundcorner_green1);
			top.setVisibility(View.GONE);
			top1.setVisibility(View.VISIBLE);
			type = 2;
			initView();
			break;
		}
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
		pair.add(new BasicNameValuePair("u_club_name", record));
		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
