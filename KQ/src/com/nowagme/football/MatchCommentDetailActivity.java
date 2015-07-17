package com.nowagme.football;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.CommentView;
import cn.kangeqiu.kq.refresh.PullToRefreshView;
import cn.kangeqiu.kq.refresh.PullToRefreshView.OnFooterRefreshListener;
import cn.kangeqiu.kq.refresh.PullToRefreshView.OnHeaderRefreshListener;
import cn.kangeqiu.kq.utils.SmileUtils;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.WebRequestUtil;
import com.nowagme.util.WebRequestUtilListener;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchCommentDetailActivity extends AgentActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener, OnClickListener {
	private String commentId = "", like_state, userId;
	private int page = 1;
	private TextView tx_name, time, tx_content, zan_counts, txt_vote;
	private CircleImageView head_icon;
	private LinearLayout ll_photo, ll_grid1, ll_grid2, ll_grid3, ll_zan, list;
	// private ListView list;
	// private Adaptercomment1 adapter;
	private JSONArray records = new JSONArray();
	private JSONArray like_users = new JSONArray();
	private JSONObject content = null;
	private String content_id;
	private static EditText edit;
	private Button btn_save;
	private RelativeLayout main_lay, rel_name;
	public int u_type1;
	public ImageView zan;
	public ImageView zan_count;
	private PullToRefreshView mPullToRefreshView;
	private ImagerLoader loader = new ImagerLoader();
	private String reply_nickname = "";
	LinearLayout lay_zan;
	TextView tv_num;// 用来显示剩余字数<br>
	int num = 140;// 限制的最大字数
	CommentView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_match_comment_detail);
		u_type1 = 1;
		initHandle();
		initData(true);
		initView();
		// adapter = new Adaptercomment1(this);
		// list.setAdapter(adapter);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(main_lay.getWindowToken(), 0);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		edit.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
				System.out.println("s=" + s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int number = num - s.length();
				// tv_num.setText("" + number);
				selectionStart = edit.getSelectionStart();
				selectionEnd = edit.getSelectionEnd();
				// System.out.println("start="+selectionStart+",end="+selectionEnd);
				if (temp.length() > num) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionStart;
					edit.setText(s);
					edit.setSelection(tempSelection);// 设置光标在最后
					Toast.makeText(MatchCommentDetailActivity.this,
							"字数过长，最多140字", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void setType(int type, String nickName) {
		u_type1 = type;
		reply_nickname = nickName;
	}

	public void u_type1() {
		if (u_type1 == 2) {
			if (reply_nickname != null && !reply_nickname.equals("")) {
				edit.setHint("回复" + reply_nickname + ":");

			} else {
				edit.setHint("回复:");
			}
			edit.setHintTextColor(Color.parseColor("#B4B4B4"));
		}
	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}

	private void initView() {

		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setLastUpdated(new Date().toLocaleString());
		tx_name = (TextView) findViewById(R.id.tx_name);
		head_icon = (CircleImageView) findViewById(R.id.head_icon);
		time = (TextView) findViewById(R.id.time);
		txt_vote = (TextView) findViewById(R.id.txt_num);
		tx_content = (TextView) findViewById(R.id.content);
		zan_counts = (TextView) findViewById(R.id.zan_counts);
		zan_count = (ImageView) findViewById(R.id.zan_count);
		ll_photo = (LinearLayout) findViewById(R.id.ll_photo);
		ll_grid1 = (LinearLayout) findViewById(R.id.ll_photo_grid1);
		ll_grid2 = (LinearLayout) findViewById(R.id.ll_photo_grid2);
		ll_grid3 = (LinearLayout) findViewById(R.id.ll_photo_grid3);
		main_lay = (RelativeLayout) findViewById(R.id.main_lay);
		lay_zan = (LinearLayout) findViewById(R.id.lay_zan);
		rel_name = (RelativeLayout) findViewById(R.id.rel_name);
		ll_zan = (LinearLayout) findViewById(R.id.ll_zan);
		list = (LinearLayout) findViewById(R.id.list);
		btn_save = (Button) findViewById(R.id.btn_save);
		edit = (EditText) findViewById(R.id.edit);
		// lay_zan = (Button) findViewById(R.id.lay_zan);
		zan = (ImageView) findViewById(R.id.zan);
		btn_save.setOnClickListener(this);
		rel_name.setOnClickListener(this);
		// btn_save.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {}
		// });
	}

	/**
	 * 更新数据
	 */
	public void refreshLoad(final boolean isRefresh, final int pageNo) {
		// initData

		// 调取个人信息
		doPullDate(false, "2019", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				onFinishLoad(isRefresh);
				CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						if (isRefresh) {
							content = (JSONObject) resp.getJson()
									.getJSONObject("content");
							records = (JSONArray) resp.getJson().getJSONArray(
									"records");
							like_state = content.get("like_state").toString();
							if (like_state.equals("0")) {
								zan.setImageResource(R.drawable.abc_match_heart);
								// Drawable drawable =
								// getResources().getDrawable(
								// R.drawable.abc_match_heart);
								// // / 这一步必须要做,否则不会显示.
								// drawable.setBounds(0, 0,
								// drawable.getMinimumWidth(),
								// drawable.getMinimumHeight());
								// lay_zan.setCompoundDrawables(drawable, null,
								// null, null);
							} else {
								zan.setImageResource(R.drawable.selectedlove);
								// Drawable drawable =
								// getResources().getDrawable(
								// R.drawable.selectedlove);
								// // / 这一步必须要做,否则不会显示.
								// drawable.setBounds(0, 0,
								// drawable.getMinimumWidth(),
								// drawable.getMinimumHeight());
								// lay_zan.setCompoundDrawables(drawable, null,
								// null, null);

							}
							like_users = content.getJSONArray("like_users");
							if (Integer.parseInt(content.get("like_count")
									.toString()) > 6) {
								zan_count.setVisibility(View.VISIBLE);
							} else {
								zan_count.setVisibility(View.GONE);
							}
							ll_zan.removeAllViews();
							for (int i = 0; i < like_users.length(); i++) {

								if (i >= like_users.length() - 6) {
									CircleImageView header = new CircleImageView(
											MatchCommentDetailActivity.this);
									header.setLayoutParams(new LayoutParams(55,
											55));
									header.setPadding(10, 0, 10, 0);
									loader.LoadImage(
											like_users.getJSONObject(i)
													.getString("icon"), header);
									ll_zan.addView(header);
								}
							}
							// for (int i = 0; i < records.length(); i++) {
							//
							// view = new CommentView(
							// MatchCommentDetailActivity.this);
							// list.addView(view.getView(records
							// .getJSONObject(i)));
							// // adapter.setItem(records);
							// //
							// Utility.setListViewHeightBasedOnChildren(list);
							// }
						} else {
							JSONArray moreRecords = (JSONArray) resp.getJson()
									.getJSONArray("records");
							if (moreRecords.length() < 1) {
								page--;
								Toast.makeText(MatchCommentDetailActivity.this,
										"没有更多数据了", Toast.LENGTH_SHORT).show();
							} else {
								list.removeAllViews();
								for (int i = 0; i < moreRecords.length(); i++) {
									records.put(moreRecords.get(i));
									view = new CommentView(
											MatchCommentDetailActivity.this);
									list.addView(view.getView(moreRecords
											.getJSONObject(i)));
								}

								// adapter.setItem(records);
								// Utility.setListViewHeightBasedOnChildren(list);
							}
						}
					} else {
						Toast.makeText(MatchCommentDetailActivity.this,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(MatchCommentDetailActivity.this,
						resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@SuppressLint("NewApi")
	private void initData(boolean isLazyLoad) {
		doPullDate(isLazyLoad, "2019", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				// CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						content = (JSONObject) resp.getJson().getJSONObject(
								"content");
						records = (JSONArray) resp.getJson().getJSONArray(
								"records");
						list.removeAllViews();
						if (records != null) {
							for (int i = 0; i < records.length(); i++) {
								view = new CommentView(
										MatchCommentDetailActivity.this);
								list.addView(view.getView(records
										.getJSONObject(i)));
							}

						}
						if (content != null) {
							content_id = content.getString("id");

							userId = content.get("user_id").toString();
							tx_name.setText(content.get("nickname").toString());
							time.setText(content.get("time").toString());
							// new DownAndShowImageTask(content.get("user_icon")
							// .toString(), head_icon).execute();
							loader.LoadImage(content.get("user_icon")
									.toString(), head_icon);
							Spannable span = SmileUtils.getSmiledText(
									MatchCommentDetailActivity.this, content
											.get("text").toString());
							tx_content.setText(span, BufferType.SPANNABLE);
							if (!content.get("like_count").equals("0")) {
								zan_counts.setVisibility(View.VISIBLE);
								zan_counts.setText(content.get("like_count")
										.toString());
							} else {
								zan_counts.setVisibility(View.GONE);
							}

							txt_vote.setText(content.get("comment_count")
									.toString());
							like_state = content.get("like_state").toString();
							if (like_state.equals("0")) {
								zan.setImageResource(R.drawable.abc_match_heart);
								// Drawable drawable =
								// getResources().getDrawable(
								// R.drawable.abc_match_heart);
								// // / 这一步必须要做,否则不会显示.
								// drawable.setBounds(0, 0,
								// drawable.getMinimumWidth(),
								// drawable.getMinimumHeight());
								// lay_zan.setCompoundDrawables(drawable, null,
								// null, null);
							} else if (like_state.equals("1")) {
								zan.setImageResource(R.drawable.selectedlove);
								// Drawable drawable =
								// getResources().getDrawable(
								// R.drawable.selectedlove);
								// // / 这一步必须要做,否则不会显示.
								// drawable.setBounds(0, 0,
								// drawable.getMinimumWidth(),
								// drawable.getMinimumHeight());
								// lay_zan.setCompoundDrawables(drawable, null,
								// null, null);

							}
							like_users = content.getJSONArray("like_users");
							if (Integer.parseInt(content.get("like_count")
									.toString()) > 6) {
								zan_count.setVisibility(View.VISIBLE);
							} else {
								zan_count.setVisibility(View.GONE);
							}
							ll_zan.removeAllViews();
							for (int i = 0; i < like_users.length(); i++) {

								if (i >= like_users.length() - 6) {
									CircleImageView header = new CircleImageView(
											MatchCommentDetailActivity.this);
									header.setLayoutParams(new LayoutParams(55,
											55));
									header.setPadding(10, 0, 10, 0);
									loader.LoadImage(
											like_users.getJSONObject(i)
													.getString("icon"), header);
									ll_zan.addView(header);
								}
							}

							JSONArray imgs = (JSONArray) content
									.getJSONArray("images");

							if (imgs.length() < 1) {
								// oneHolder.photo.setVisibility(View.GONE);
								ll_photo.removeAllViews();
								ll_grid1.removeAllViews();
								ll_grid2.removeAllViews();
								ll_grid3.removeAllViews();
							} else if (imgs.length() == 1) {
								ll_photo.removeAllViews();
								ll_grid1.removeAllViews();
								ll_grid2.removeAllViews();
								ll_grid3.removeAllViews();
								ImageView photo = new ImageView(
										MatchCommentDetailActivity.this);
								// 设置当前图像的图像（position为当前图像列表的位置）
								photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
								photo.setLayoutParams(new LayoutParams(
										LayoutParams.WRAP_CONTENT,
										LayoutParams.WRAP_CONTENT));
								// 设置Gallery组件的背景风格
								// oneHolder.photo.setVisibility(View.VISIBLE);
								StringBuffer s1 = new StringBuffer(imgs
										.getJSONObject(0).getString("url"));
								// new DownAndShowImageTask(s1.insert(
								// s1.lastIndexOf("."), "_M").toString(),
								// photo).execute();

								loader.LoadImage(
										s1.insert(s1.lastIndexOf("."), "_M")
												.toString(), photo);
								photo.setTag(s1);
								photo.setClickable(true);
								photo.setFocusable(true);
								// photo.setOnClickListener(new
								// OnClickListener() {
								//
								// @Override
								// public void onClick(View view) {
								// MatchCommentDetailActivity.this
								// .startActivity(new Intent(
								// MatchCommentDetailActivity.this,
								// ShowBigImage.class)
								// .putExtra(
								// "remotepath",
								// view.getTag()
								// .toString()
								// .replace(
								// "_M",
								// "_L")));
								// }
								// });
								ll_photo.addView(photo);

							} else {
								if (imgs.length() < 4) {
									// 一行
									ll_grid1.removeAllViews();
									ll_grid2.removeAllViews();
									ll_grid3.removeAllViews();
									for (int i = 0; i < 3; i++) {
										addPhoto(i, ll_grid1, imgs);
									}
								} else if (imgs.length() > 3
										&& imgs.length() < 7) {
									// 两行
									ll_grid1.removeAllViews();
									ll_grid2.removeAllViews();
									ll_grid3.removeAllViews();
									for (int i = 0; i < 3; i++) {
										addPhoto(i, ll_grid1, imgs);
									}
									for (int i = 3; i < 6; i++) {
										addPhoto(i, ll_grid2, imgs);
									}
								} else if (imgs.length() > 6
										&& imgs.length() < 10) {
									// 三行
									ll_grid1.removeAllViews();
									ll_grid2.removeAllViews();
									ll_grid3.removeAllViews();
									for (int i = 0; i < 3; i++) {
										addPhoto(i, ll_grid1, imgs);
									}
									for (int i = 3; i < 6; i++) {
										addPhoto(i, ll_grid2, imgs);
									}
									for (int i = 6; i < 9; i++) {
										addPhoto(i, ll_grid3, imgs);
									}
								}
								// oneHolder.photo.setVisibility(View.GONE);
								// oneHolder.ll_content.removeView(oneHolder.photo);
								ll_photo.removeAllViews();
							}
						}
					} else {
						Toast.makeText(MatchCommentDetailActivity.this,
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
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(MatchCommentDetailActivity.this,
						resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void onClzan(View v) {
		Intent intent = new Intent(this, HelpActivity.class);
		intent.putExtra("content_id", content_id);
		startActivity(intent);

	}

	public void onpraise(View v) {
		boolean flag = false;
		if (like_state.equals("0")) {
			MobclickAgent.onEvent(this, "match_zan");
			TCAgent.onEvent(this, "match_zan");

			doPullDate4(flag, "2007", new MCHttpCallBack() {
				@Override
				public void onSuccess(MCHttpResp resp) {
					super.onSuccess(resp);
					// CPorgressDialog.hideProgressDialog();
					try {
						String resultCode = (String) resp.getJson().getString(
								"result_code");
						if ("0".equals(resultCode)) {

							Toast.makeText(MatchCommentDetailActivity.this,
									"点赞成功", Toast.LENGTH_SHORT).show();
							 content.put("like_state", "1");
//							like_state = "1";
							initData(false);
						} else {
							Toast.makeText(MatchCommentDetailActivity.this,
									"点赞失败", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(MCHttpResp resp) {
					super.onError(resp);
					CPorgressDialog.hideProgressDialog();
					Toast.makeText(MatchCommentDetailActivity.this,
							resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
				}
			});

		} else if (like_state.equals("1")) {
			doPullDate4(flag, "2008", new MCHttpCallBack() {
				@Override
				public void onSuccess(MCHttpResp resp) {
					super.onSuccess(resp);
					String resultCode;
					// CPorgressDialog.hideProgressDialog();
					try {
						resultCode = (String) resp.getJson().getString(
								"result_code");

						if ("0".equals(resultCode)) {
							Toast.makeText(MatchCommentDetailActivity.this,
									"取消点赞成功", Toast.LENGTH_SHORT).show();
							 content.put("like_state", "0");
//							like_state = "0";
							initData(false);

						} else {
							Toast.makeText(MatchCommentDetailActivity.this,
									"取消点赞失败", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(MCHttpResp resp) {
					super.onError(resp);
					CPorgressDialog.hideProgressDialog();
					Toast.makeText(MatchCommentDetailActivity.this,
							resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
				}
			});

		}

	}

	private void doPullDate4(boolean isRefresh, String action,
			MCHttpCallBack listen) {
		// CPorgressDialog.showProgressDialog(this);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_content_id", content_id));
		new WebRequestUtil().execute(isRefresh, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

	private void initHandle() {
		commentId = getIntent().getStringExtra("commentid");
	}

	public static class Utility {
		public static void setListViewHeightBasedOnChildren(ListView listView) {
			ListAdapter listAdapter = listView.getAdapter();
			if (listAdapter == null) {

				return;
			}

			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight
					+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			listView.setLayoutParams(params);
		}
	}

	private void doPullDate3(String action, WebRequestUtilListener listen) {
		CPorgressDialog.showProgressDialog(this);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		parameters.put("u_body", edit.getText().toString());
		parameters.put("u_content_id", content_id);
		parameters.put("u_comment_id", view.getComment_id());
		parameters.put("u_type", "0");
		Log.v("tag", "parameters" + parameters);
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

	private void doPullDate2(String action, WebRequestUtilListener listen) {
		// CPorgressDialog.showProgressDialog(this);
		//
		// ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		// pair.add(new BasicNameValuePair("app_action", action));
		// pair.add(new BasicNameValuePair("app_platform", "0"));
		// pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
		// .getPlayerId() + ""));
		// pair.add(new BasicNameValuePair("u_body",
		// edit.getText().toString()));
		// pair.add(new BasicNameValuePair("u_content_id", content_id));
		// pair.add(new BasicNameValuePair("u_comment_id", "0"));
		// pair.add(new BasicNameValuePair("u_type", "0"));
		//
		// new WebRequestUtil().execute(
		// AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
		// pair, listen);
		CPorgressDialog.showProgressDialog(this);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		parameters.put("u_body", edit.getText().toString());
		parameters.put("u_content_id", content_id);
		parameters.put("u_comment_id", "0");
		parameters.put("u_type", "0");
		Log.v("tag", "parameters" + parameters);
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

	private void doPullDate(boolean isLazyLoad, String action,
			MCHttpCallBack listen) {
		// CPorgressDialog.showProgressDialog(this);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_page", page + ""));
		pair.add(new BasicNameValuePair("u_content_id", commentId));

		new WebRequestUtil().execute(false,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listen);

		// Map<String, String> parameters = new HashMap<String, String>();
		// parameters.put("app_action", action);
		// parameters.put("app_platform", "0");
		// parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		// parameters.put("u_page", page + "");
		// parameters.put("u_content_id", commentId);
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

	private void addPhoto(int position, LinearLayout layout, JSONArray imgs) {
		ImageView photo = new ImageView(this);
		// 设置当前图像的图像（position为当前图像列表的位置）
		photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(5, 0, 0, 0);
		photo.setLayoutParams(lp);
		// 设置Gallery组件的背景风格
		// oneHolder.photo.setVisibility(View.VISIBLE);
		try {
			if (position < imgs.length())
				// new DownAndShowImageTask(imgs.getJSONObject(position)
				// .getString("url"), photo).execute();
				loader.LoadImage(imgs.getJSONObject(position).getString("url"),
						photo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		layout.addView(photo);
	}

	private void onFinishLoad(boolean isRefresh) {
		if (isRefresh)
			mPullToRefreshView.onHeaderRefreshComplete("更新于:"
					+ new Date().toLocaleString());
		else
			mPullToRefreshView.onFooterRefreshComplete();
		// comment_list.stopRefresh();
		// comment_list.stopLoadMore();
		// comment_list.setRefreshTime(DateUtil.date2String());
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		page++;
		refreshLoad(false, page);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		refreshLoad(true, 1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rel_name:
			Intent intent = new Intent();
			intent.putExtra("userId", userId);
			intent.setClass(this, FragmentPersonActivity.class);
			startActivity(intent);
			break;

		case R.id.btn_save:

			if (edit.getText().toString() == null
					|| edit.getText().toString().equals("")) {
				Toast.makeText(MatchCommentDetailActivity.this, "请输入内容",
						Toast.LENGTH_SHORT).show();

				return;

			}

			MobclickAgent.onEvent(this, "match_reply");
			TCAgent.onEvent(this, "match_reply");

			if (u_type1 == 1) {
				doPullDate2("2009", new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						String resultCode = (String) data.get("result_code");
						if ("0".equals(resultCode)) {
							try {
								Toast.makeText(MatchCommentDetailActivity.this,
										"评论成功", Toast.LENGTH_SHORT).show();
								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(
										main_lay.getWindowToken(), 0);
								edit.setText("");
								initData(false);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Toast.makeText(MatchCommentDetailActivity.this,
									"评论失败", Toast.LENGTH_SHORT).show();
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									main_lay.getWindowToken(), 0);
							edit.setText("");
						}
					}

					@Override
					public void onFail(Map<String, Object> data) {
						Toast.makeText(MatchCommentDetailActivity.this,
								String.valueOf(data.get("message")),
								Toast.LENGTH_SHORT).show();
						CPorgressDialog.hideProgressDialog();
					}

					@Override
					public void onError() {
						CPorgressDialog.hideProgressDialog();
					}
				});
			} else {
				doPullDate3("2009", new WebRequestUtilListener() {

					@Override
					public void onSucces(Map<String, Object> data) {
						CPorgressDialog.hideProgressDialog();
						String resultCode = (String) data.get("result_code");
						if ("0".equals(resultCode)) {
							try {
								Toast.makeText(MatchCommentDetailActivity.this,
										"回复成功", Toast.LENGTH_SHORT).show();
								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(
										main_lay.getWindowToken(), 0);
								edit.setText("");
								initData(false);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Toast.makeText(MatchCommentDetailActivity.this,
									"回复失败", Toast.LENGTH_SHORT).show();
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									main_lay.getWindowToken(), 0);
							edit.setText("");
						}
					}

					@Override
					public void onFail(Map<String, Object> data) {
						Toast.makeText(MatchCommentDetailActivity.this,
								String.valueOf(data.get("message")),
								Toast.LENGTH_SHORT).show();
						CPorgressDialog.hideProgressDialog();
					}

					@Override
					public void onError() {
						CPorgressDialog.hideProgressDialog();
					}
				});
			}

			break;

		default:
			break;
		}

	}
}
