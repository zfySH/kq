package cn.kangeqiu.kq.activity.view;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.utils.SmileUtils;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.football.CathecticActivity;
import com.nowagme.football.MatchCommentDetailActivity;
import com.nowagme.football.VoteActivity;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.WebRequestUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchCommentView {
	private Activity context;
	private LayoutInflater inflater;
	private static final int TYPE_ONE = 0;
	private static final int TYPE_TWO = 1;
	private static final int TYPE_THREE = 2;

	// private String activitys_id;
	private String activitys_id, like_state;
	public ImageView zan;
	public ImageView zan_count;
	private JSONArray like_users = new JSONArray();
	private LinearLayout ll_zan, lay_zan;
	private String j_recordId;
	private ImagerLoader loader = new ImagerLoader();
	private String iconUrl = "", idUrl = "";

	public MatchCommentView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
		getUserInfo();
	}

	@SuppressLint("NewApi")
	public View getView(final JSONObject matchs) throws JSONException {
		View view = null;
		int viewType = getItemViewType(matchs);
		j_recordId = matchs.getString("record_id");

		if (TYPE_ONE == viewType) {
			view = inflater.inflate(R.layout.abc_match_board_item, null);
			TextView name = (TextView) view.findViewById(R.id.match_board_name);
			CircleImageView icon = (CircleImageView) view
					.findViewById(R.id.head_icon);
			TextView time = (TextView) view.findViewById(R.id.time);
			final TextView commentCount = (TextView) view
					.findViewById(R.id.zan_counts);
			TextView likeCount = (TextView) view.findViewById(R.id.enjoy_num);
			TextView tx_content = (TextView) view.findViewById(R.id.content);
			TextView recommend_msg = (TextView) view
					.findViewById(R.id.recommend_msg);
			LinearLayout ll_recomment = (LinearLayout) view
					.findViewById(R.id.ll_recomment);
			lay_zan = (LinearLayout) view.findViewById(R.id.lay_zan);
			ll_zan = (LinearLayout) view.findViewById(R.id.ll_zan);
			zan_count = (ImageView) view.findViewById(R.id.zan_count);
			View tx_yuan = (View) view.findViewById(R.id.yuan);
			LinearLayout ll_content = (LinearLayout) view
					.findViewById(R.id.ll_content);
			zan = (ImageView) view.findViewById(R.id.zan);
			LinearLayout ll_photo = (LinearLayout) view
					.findViewById(R.id.ll_photo);
			LinearLayout ll_grid1 = (LinearLayout) view
					.findViewById(R.id.ll_photo_grid1);
			LinearLayout ll_grid2 = (LinearLayout) view
					.findViewById(R.id.ll_photo_grid2);
			LinearLayout ll_grid3 = (LinearLayout) view
					.findViewById(R.id.ll_photo_grid3);

			try {
				Spannable span = SmileUtils.getSmiledText(context,
						matchs.getString("text"));
				tx_content.setText(span, BufferType.SPANNABLE);
				JSONObject content = (JSONObject) matchs
						.getJSONObject("content");

				if (content.getString("reason") != null
						&& !content.getString("reason").equals("")) {
					ll_recomment.setVisibility(View.VISIBLE);
					recommend_msg.setText(content.get("reason").toString());
				} else {
					ll_recomment.setVisibility(View.GONE);
				}
				name.setText(content.getString("nickname"));
				time.setText(content.getString("time"));
				likeCount.setText(content.getString("comment_count"));
				like_state = content.getString("like_state");
				if (like_state.equals("0")) {
					zan.setImageResource(R.drawable.abc_match_heart);
				} else {
					zan.setImageResource(R.drawable.selectedlove);
				}
				like_users = content.getJSONArray("like_users");
				if (!content.getString("like_count").equals("0")) {
					commentCount.setText(content.getString("like_count"));
				}
				if (like_users != null) {
					if (Integer.parseInt(content.get("like_count").toString()) > 6) {
						zan_count.setVisibility(View.VISIBLE);
					} else {
						zan_count.setVisibility(View.GONE);
					}
					ll_zan.removeAllViews();
					for (int i = 0; i < like_users.length(); i++) {
						idUrl = like_users.getJSONObject(i).getString("id");
						if (i >= like_users.length() - 6) {
							CircleImageView header = new CircleImageView(
									context);
							header.setLayoutParams(new LayoutParams(60, 60));
							// new DownAndShowImageTask(like_users
							// .getJSONObject(i).getString("icon"), header)
							// .execute();
							loader.LoadImage(like_users.getJSONObject(i)
									.getString("icon"), header);
							ll_zan.addView(header);
						}
					}
				}

				// new DownAndShowImageTask(content.getString("user_icon"),
				// icon)
				// .execute();
				loader.LoadImage(content.getString("user_icon"), icon);

				tx_yuan.setBackgroundResource(R.drawable.match_yuan);

				JSONArray imgs = (JSONArray) content.get("images");
				lay_zan.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						boolean flag = false;
						if (like_state.equals("0")) {
							MobclickAgent.onEvent(context, "match_zan");
							TCAgent.onEvent(context, "match_zan");
							doPullDate(flag, "2007", new MCHttpCallBack() {
								@Override
								public void onSuccess(MCHttpResp resp) {
									super.onSuccess(resp);
									try {
										String resultCode = (String) resp
												.getJson().getString(
														"result_code");
										if ("0".equals(resultCode)) {

											Toast.makeText(context, "点赞成功",
													Toast.LENGTH_SHORT).show();
											// matchs.put("like_state", "1");
											like_state = "1";
											commentCount.setText(resp.getJson()
													.getString("count_like")
													.toString());
											CircleImageView userIcon = new CircleImageView(
													context);
											userIcon.setLayoutParams(new LayoutParams(
													60, 60));
											loader.LoadImage(iconUrl, userIcon);
											ll_zan.addView(userIcon, 0);
											like_users.put(0, AppConfig
													.getInstance()
													.getPlayerId());
											zan.setImageResource(R.drawable.selectedlove);

										} else {
											Toast.makeText(context, "点赞失败",
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
									Toast.makeText(context,
											resp.getErrorMessage(),
											Toast.LENGTH_SHORT).show();
								}
							});
						} else {
							doPullDate(flag, "2008", new MCHttpCallBack() {
								@Override
								public void onSuccess(MCHttpResp resp) {
									super.onSuccess(resp);
									String resultCode;
									try {
										resultCode = (String) resp.getJson()
												.getString("result_code");

										if ("0".equals(resultCode)) {

											Toast.makeText(context, "取消点赞成功",
													Toast.LENGTH_SHORT).show();
											// matchs.put("like_state", "0");
											like_state = "0";
											for (int i = 0; i < like_users
													.length(); i++) {
												idUrl = like_users
														.getJSONObject(i)
														.getString("id");
												if (idUrl.equals(AppConfig
														.getInstance()
														.getPlayerId()
														+ "")) {
													// like_users.remove(i);
													ll_zan.removeViewAt(i);
												}
											}
											commentCount.setText(resp.getJson()
													.getString("count_like")
													.toString());
											zan.setImageResource(R.drawable.abc_match_heart);

										} else {
											Toast.makeText(context, "取消点赞失败",
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
									Toast.makeText(context,
											resp.getErrorMessage(),
											Toast.LENGTH_SHORT).show();
								}
							});
						}

					}
				});

				ll_content.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						Intent intent = new Intent();
						intent.setClass(context,
								MatchCommentDetailActivity.class);
						intent.putExtra("commentid", j_recordId);
						context.startActivity(intent);
					}
				});
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
					ImageView photo = new ImageView(context);
					// 设置当前图像的图像（position为当前图像列表的位置）
					photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					photo.setLayoutParams(new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					// 设置Gallery组件的背景风格
					// oneHolder.photo.setVisibility(View.VISIBLE);
					StringBuffer s1 = new StringBuffer(imgs.getJSONObject(0)
							.getString("url"));
					// new DownAndShowImageTask(s1.insert(s1.lastIndexOf("."),
					// "_M").toString(), photo).execute();
					loader.LoadImage(s1.insert(s1.lastIndexOf("."), "_M")
							.toString(), photo);
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
					} else if (imgs.length() > 3 && imgs.length() < 7) {
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
					} else if (imgs.length() > 6 && imgs.length() < 10) {
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (TYPE_TWO == viewType) {
			view = inflater.inflate(R.layout.abc_match_board_item_match, null);
			TextView name = (TextView) view.findViewById(R.id.match_match_name);
			View tx_yuan = (View) view.findViewById(R.id.yuan);

			Spannable span;
			try {
				span = SmileUtils.getSmiledText(context,
						matchs.getString("text"));
				name.setText(span, BufferType.SPANNABLE);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			tx_yuan.setBackgroundResource(R.drawable.match_yuan_);
		} else if (TYPE_THREE == viewType) {
			view = inflater.inflate(R.layout.abc_match_board_item_activity,
					null);
			TextView name = (TextView) view
					.findViewById(R.id.match_activity_name);
			LinearLayout ll_content = (LinearLayout) view
					.findViewById(R.id.ll_content);
			TextView type = (TextView) view.findViewById(R.id.type);
			TextView statue = (TextView) view.findViewById(R.id.status);
			// View arron = (View) view.findViewById(R.id.match_arron);
			View tx_yuan = (View) view.findViewById(R.id.yuan);
			TextView tx_counts = (TextView) view.findViewById(R.id.tx_counts);
			try {

				// activitys_id = activitys.getString("id");
				// Map<String, String> map = new HashMap<String, String>();
				// map.put("id", activitys_id);

				name.setText(matchs.getString("text"));
				final JSONObject activity = (JSONObject) matchs.get("activity");
				final String j_type = activity.getString("type");
				// final String j_recordId = matchs.getString("record_id");
				// tx_counts.setText(text)
				type.setText(j_type.equals("0") ? "" : "投票");
				type.setBackgroundResource(j_type.equals("0") ? R.drawable.img_quiz
						: R.drawable.abc_button_roundcorner_toupiao);
				statue.setText(getStatus(activity.getString("state")));
				tx_yuan.setBackgroundResource(R.drawable.match_yuan);
				tx_counts.setText("已有"
						+ activity.getString("join_count").toString() + "人参与");
				ll_content.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (j_type.equals("0")) {
							Intent intent = new Intent();
							intent.setClass(context, CathecticActivity.class);
							intent.putExtra("id", j_recordId);
							context.startActivity(intent);
						} else {
							Intent intent = new Intent();
							intent.setClass(context, VoteActivity.class);
							intent.putExtra("id", j_recordId);
							context.startActivity(intent);
						}

					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return view;
	}

	private void getUserInfo() {
		doPullDate1(true, "2030", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				String resultCode;
				try {
					resultCode = (String) resp.getJson().getString(
							"result_code");
					if ("0".equals(resultCode)) {
						iconUrl = resp.getJson().getString("icon");
					} else {
						Toast.makeText(context, "取消点赞失败", Toast.LENGTH_SHORT)
								.show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(context, resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void doPullDate1(boolean isRefresh, String action,
			MCHttpCallBack listen) {
		// CPorgressDialog.showProgressDialog(context);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		new WebRequestUtil().execute(isRefresh, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

	public int getItemViewType(JSONObject matchs) {
		try {
			if (matchs.getString("type").equals("0")) {
				return TYPE_ONE;
			} else if (matchs.getString("type").equals("1")) {
				return TYPE_TWO;
			} else {
				return TYPE_THREE;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}

	}

	// private void doPullDate4(String action, WebRequestUtilListener listen) {
	// // CPorgressDialog.showProgressDialog(context);
	// Map<String, String> parameters = new HashMap<String, String>();
	// parameters.put("app_action", action);
	// parameters.put("app_platform", "0");
	// parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
	// parameters.put("u_content_id", j_recordId);
	// HttpPostUtil mHttpPostUtil = null;
	// try {
	// AppConfig.getInstance().addSign(parameters);
	// mHttpPostUtil = AppConfig.getInstance()
	// .makeHttpPostUtil(parameters);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// // WEB request and deal the listener
	// new WebRequestUtil(mHttpPostUtil).executeWithOutCache(listen);
	// }
	private void doPullDate(boolean isRefresh, String action,
			MCHttpCallBack listen) {
		// CPorgressDialog.showProgressDialog(context);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_content_id", j_recordId));
		new WebRequestUtil().execute(isRefresh, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

	private String getStatus(String status) {
		if (status.equals("0"))
			return "未开始";
		else if (status.equals("1"))
			return "进行中";
		else if (status.equals("2"))
			return "已结束";
		return "";
	}

	private void addPhoto(int position, LinearLayout layout, JSONArray imgs) {
		ImageView photo = new ImageView(context);
		// 设置当前图像的图像（position为当前图像列表的位置）
		photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(5, 0, 0, 0);
		photo.setLayoutParams(lp);
		// 设置Gallery组件的背景风格
		// oneHolder.photo.setVisibility(View.VISIBLE);
		if (position < imgs.length())
			try {
				ImagerLoader loader = new ImagerLoader();
				loader.LoadImage(imgs.getJSONObject(position).getString("url"),
						photo);

				// new DownAndShowImageTask(imgs.getJSONObject(position)
				// .getString("url"), photo).execute();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		layout.addView(photo);
	}
}
