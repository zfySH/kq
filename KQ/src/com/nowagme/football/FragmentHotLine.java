package com.nowagme.football;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.MatchHotlineView;
import cn.kangeqiu.kq.adapter.MatchHotLineAdapter;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.util.RefreshListener;
import com.nowagme.util.WebRequestUtil;

public class FragmentHotLine extends Fragment {

	private ListView board;
	private MatchHotLineAdapter adapter;
	private LinearLayout ll_main;
	private JSONArray records = new JSONArray();
	private int page = 1;
	private View rootView;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		if (null == rootView) {
			rootView = inflater.inflate(R.layout.abc_match_on_board, container,
					false);
			initView(rootView);
		} else {
			ViewGroup mViewGroup = (ViewGroup) rootView.getParent();
			mViewGroup.removeView(rootView);
		}

		// board = (ListView) rootView.findViewById(R.id.board_list);
		// adapter = new MatchHotLineAdapter(getActivity());
		// board.setAdapter(adapter);
		// ((TeamActivityActivity) getActivity()).showCreateHourse(false);

		return rootView;
	}

	private void initView(View rootView2) {
		ll_main = (LinearLayout) rootView.findViewById(R.id.ll_main);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initDate(true, true, null);
	}

	public void refresh(RefreshListener listener) {
		initDate(false, true, listener);
	}

	public void loadMore(RefreshListener listener) {
		page++;
		initDate(false, false, listener);
	}

	public void initDate(boolean isLazyLoad, final boolean isRefresh,
			final RefreshListener listener) {
		int page_ = isRefresh ? 1 : page;
		doPullDate(isLazyLoad, page_, "2006", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						if (listener != null) {
							listener.onCompleted();
						}
						if (isRefresh)
							ll_main.removeAllViews();
						records = (JSONArray) resp.getJson().getJSONArray(
								"records");
						JSONObject match = (JSONObject) resp.getJson()
								.getJSONObject("match");
						if (match != null)
							((TeamActivityActivity) getActivity())
									.refresh(match);
						if (records != null) {
							if (!isRefresh) {
								if (records.length() < 1) {
									Toast.makeText(getActivity(), "没有更多数据了",
											Toast.LENGTH_SHORT).show();
									page--;
								}
							}
							for (int i = 0; i < records.length(); i++) {
								MatchHotlineView view = new MatchHotlineView(
										getActivity());
								ll_main.addView(view.getView(records
										.getJSONObject(i)));
							}

						}
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
			}
		});
	}

	private void doPullDate(boolean isLazyLoad, int page, String action,
			MCHttpCallBack listen) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_page", page + ""));
		pair.add(new BasicNameValuePair("u_match_id",
				((TeamActivityActivity) getActivity()).getMatchId()));

		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

}
