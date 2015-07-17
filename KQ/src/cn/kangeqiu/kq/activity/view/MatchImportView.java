package cn.kangeqiu.kq.activity.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.adapter.MatchAdapter;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.football.TeamActivityActivity;
import com.nowagme.util.DateUtil;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;

public class MatchImportView {
	private Activity context;
	private LayoutInflater inflater;
	public static XListView lv_match;
	private MatchAdapter adapter;
	private JSONArray matchs;
	private int page;

	public MatchImportView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(final boolean isCreateHourse) {
		View view = inflater.inflate(R.layout.abc_match_all_item, null);
		adapter = new MatchAdapter(context);
		lv_match = (XListView) view.findViewById(R.id.match_list);
		// lv_match = (LinearLayout) view.findViewById(R.id.match_list);
		lv_match.setAdapter(adapter);
		lv_match.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				try {
					if (!isCreateHourse) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						intent.setClass(context, TeamActivityActivity.class);
						bundle.putInt(
								"match_id",
								Integer.parseInt(matchs.getJSONObject(
										position - 1).getString("id")));
						intent.putExtras(bundle);
						context.startActivity(intent);

					} else {
						Intent mIntent = new Intent();
						mIntent.putExtra("match",
								matchs.getJSONObject(position - 1).toString());
						// 设置结果，并进行传送
						context.setResult(30, mIntent);
						context.finish();
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		lv_match.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				page = 1;
				doShowNearby(true);
			}

			@Override
			public void onLoadMore() {
				page++;
				doShowNearby(false);
			}
		});
		lv_match.setPullLoadEnable(true);
		lv_match.setPullRefreshEnable(true);
		doFirstShowNearby();
		return view;
	}

	// //**
	// * 第1次打开附近显示数据.
	// *//*
	public void doFirstShowNearby() {
		page = 1;
		doShowNearby(true);
	}

	public void doShowNearby(final boolean isRefresh) {
		doPullDate("2032", new MCHttpCallBack() {

			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						JSONArray an_matchs = (JSONArray) resp.getJson()
								.getJSONArray("matchs");
						SimpleDateFormat format = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date date = null;
						Calendar calendar = null;
						String parseDate = "";
						for (int i = 0; i < an_matchs.length(); i++) {
							an_matchs.getJSONObject(i).put("num", "0");

							// 日期
							String time = an_matchs.getJSONObject(i).getString(
									"time");
							try {
								date = format.parse(time);
								calendar = Calendar.getInstance();
								calendar.setTime(date);
								String newDate = ((calendar.get(Calendar.MONTH)) + 1)
										+ "月"
										+ calendar.get(Calendar.DAY_OF_MONTH);
								if (!parseDate.equals(newDate)) {
									an_matchs.getJSONObject(i).put(
											"isShowDate", true);
									parseDate = newDate;
								} else {
									an_matchs.getJSONObject(i).put(
											"isShowDate", false);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if (isRefresh) {
							matchs = an_matchs;
							adapter.setItem(matchs, 1);
							// for (int i = 0; i < matchs.length(); i++) {
							// FragmentMatchView matchView = new
							// FragmentMatchView(
							// getActivity());
							// lv_match.addView(matchView.getView(matchs
							// .getJSONObject(i)));
							// }
						} else {
							for (int i = 0; i < an_matchs.length(); i++) {
								matchs.put(an_matchs.get(i));
							}

							int itemsCount = (an_matchs == null) ? 0
									: an_matchs.length();
							// 刷新显示
							if (itemsCount > 0) {
								// for (int i = 0; i < matchs.length(); i++) {
								// FragmentMatchView matchView = new
								// FragmentMatchView(
								// getActivity());
								// lv_match.addView(matchView.getView(matchs
								// .getJSONObject(i)));
								// }
								adapter.setItem(matchs, 1);
							} else {
								Toast.makeText(context, "没有更多数据了.",
										Toast.LENGTH_SHORT).show();
								if (page > 1)
									page--;
							}

						}

					} else {
						Toast.makeText(context,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 数据载入关闭提示;
				onFinishLoad();
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				Toast.makeText(context, resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
				// 数据载入关闭提示;
				onFinishLoad();
			}
		});

	}

	private void doPullDate(String action, MCHttpCallBack listen) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_page", page + ""));
		pair.add(new BasicNameValuePair("app_version", UpdataUtil
				.getAppVersion(context)));

		new WebRequestUtil().execute(true,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listen);

		// CPorgressDialog.showProgressDialog(getActivity());
		// Map<String, String> parameters = new HashMap<String, String>();
		// parameters.put("app_action", action);
		// parameters.put("app_platform", "0");
		// parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		// parameters.put("u_page", page + "");
		//
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
	}

	private void onFinishLoad() {
		lv_match.stopRefresh();
		lv_match.stopLoadMore();
		lv_match.setRefreshTime(DateUtil.date2String());
	}
}
