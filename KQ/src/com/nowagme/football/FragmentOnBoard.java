package com.nowagme.football;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.BaseView;
import cn.kangeqiu.kq.activity.view.MatchCommentView;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.util.RefreshListener;
import com.nowagme.util.WebRequestUtil;

public class FragmentOnBoard extends Fragment {

	// private MyListView board;
	// private OnBoardAdapter adapter;
	private LinearLayout ll_main;
	private JSONArray records = new JSONArray();
	private JSONArray activitys = new JSONArray();
	private String activitys_id;
	private int page = 1;
	private String matchId = "";
	private View rootView;
	private Button create_comment, create_tanmu;

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
		// View rootView = inflater.inflate(R.layout.abc_match_on_board,
		// container, false);// 关联布局文件

		// ((TeamActivityActivity) getActivity()).showCreateHourse(false);

		return rootView;
	}

	private void initView(View rootView2) {
		ll_main = (LinearLayout) rootView.findViewById(R.id.ll_main);
		// create_comment = (Button) rootView.findViewById(R.id.create_comment);
		// create_tanmu = (Button) rootView.findViewById(R.id.create_tanmu);
		// create_comment.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// ((TeamActivityActivity) getActivity()).createComment();
		// }
		// });
		// create_tanmu.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// ((TeamActivityActivity) getActivity()).createTanmu();
		//
		// }
		// });
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

	// 刷新
	private void initDate(boolean isLazyLoad, final boolean isRefresh,
			final RefreshListener listener) {
		int page_ = isRefresh ? 1 : page;
		doPullDate(isLazyLoad, page_, "2004", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				// TODO Auto-generated method stub
				super.onSuccess(resp);
				if (listener != null) {
					listener.onCompleted();
				}
				if (isRefresh)
					ll_main.removeAllViews();
				try {
					records = (JSONArray) resp.getJson().get("records");
					activitys = (JSONArray) resp.getJson().get("activitys");
					JSONObject match = (JSONObject) resp.getJson().get("match");
					if (match != null) {
						((TeamActivityActivity) getActivity()).refresh(match);
					}
					if (activitys != null) {
						// for (int i = 0; i < activitys.length(); i++) {
						if (activitys.length() > 0) {
							BaseView view = new BaseView(getActivity());
							ll_main.addView(view.getView(activitys
									.getJSONObject(0)));
							activitys_id = activitys.getJSONObject(0)
									.getString("id");
						}
						// }

					}
					if (records != null) {
						// adapter.setItem(records);
						if (!isRefresh) {
							if (records.length() < 1) {
								Toast.makeText(getActivity(), "没有更多数据了",
										Toast.LENGTH_SHORT).show();
								page--;
							}
						}
						for (int i = 0; i < records.length(); i++) {
							MatchCommentView view = new MatchCommentView(
									getActivity());
							ll_main.addView(view.getView(records
									.getJSONObject(i)));
						}

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// setListViewHeightBasedOnChildren(board);
				// ((TeamActivityActivity) getActivity()).scrollUp();
				// ((TeamActivityActivity) getActivity()).setTitle();
			}

			@Override
			public void onError(MCHttpResp resp) {
				// TODO Auto-generated method stub
				super.onError(resp);
				if (listener != null) {
					listener.onCompleted();
				}
			}
		}

		// new WebRequestUtilListener() {
		//
		// @SuppressWarnings("unchecked")
		// @Override
		// public void onSucces(Map<String, Object> data) {
		// if (listener != null) {
		// listener.onCompleted();
		// }
		// if (isRefresh)
		// ll_main.removeAllViews();
		//
		// records = (List<Map<String, Object>>) data.get("records");
		// Map<String, Object> match = (Map<String, Object>) data
		// .get("match");
		// if (match != null)
		// ((TeamActivityActivity) getActivity()).refresh(match);
		// if (records != null) {
		// // adapter.setItem(records);
		// if (!isRefresh) {
		// if (records.size() < 1) {
		// Toast.makeText(getActivity(), "没有更多数据了",
		// Toast.LENGTH_SHORT).show();
		// page--;
		// }
		// }
		// for (int i = 0; i < records.size(); i++) {
		// MatchCommentView view = new MatchCommentView(
		// getActivity());
		// ll_main.addView(view.getView(records.get(i)));
		// }
		//
		// }
		// // setListViewHeightBasedOnChildren(board);
		// // ((TeamActivityActivity) getActivity()).scrollUp();
		// // ((TeamActivityActivity) getActivity()).setTitle();
		// }
		//
		// @Override
		// public void onFail(Map<String, Object> data) {
		// Toast.makeText(getActivity(),
		// String.valueOf(data.get("message")), Toast.LENGTH_SHORT)
		// .show();
		// if (listener != null) {
		// listener.onCompleted();
		// }
		// }
		//
		// @Override
		// public void onError() {
		//
		// if (listener != null) {
		// listener.onCompleted();
		// }
		// }
		// }
		);
	}

	private void doPullDate(boolean isLazyLoad, int page, String action,
			MCHttpCallBack listener) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_page", page + ""));
		pair.add(new BasicNameValuePair("u_match_id",
				((TeamActivityActivity) getActivity()).getMatchId()));
		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listener);
	}
}
