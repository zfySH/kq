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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
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
import com.nowagme.football.MatchCommentDetailActivity;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.WebRequestUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchHotlineView {
	private Activity context;
	private LayoutInflater inflater;
	private static final int TYPE_ONE = 0;
	private static final int TYPE_TWO = 1;
	private static final int TYPE_THREE = 2;
	public ImageButton zan;
	public ImageView zan_count;
	private JSONArray like_users = new JSONArray();
	private LinearLayout ll_zan;
	private String like_state, id;
	private TextView likeCount;
	private ImagerLoader loader = new ImagerLoader();
	LinearLayout lay_zan;
	private String iconUrl = "", idUrl = "";
	private JSONObject matchs = new JSONObject();

	public MatchHotlineView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
		getUserInfo();
	}

	@SuppressLint("NewApi")
	public View getView(final JSONObject matchs) throws JSONException {
		View view = null;
		view = inflater.inflate(R.layout.abc_match_hotline_item, null);
		TextView name = (TextView) view.findViewById(R.id.tx_name);
		CircleImageView icon = (CircleImageView) view
				.findViewById(R.id.head_icon);
		TextView time = (TextView) view.findViewById(R.id.time);
		TextView commentCount = (TextView) view.findViewById(R.id.enjoy_num);
		likeCount = (TextView) view.findViewById(R.id.zan_counts);
		TextView tx_content = (TextView) view.findViewById(R.id.content);
		// View arron = (View) view.findViewById(R.id.match_arron);
		LinearLayout ll_content = (LinearLayout) view
				.findViewById(R.id.ll_content);
		lay_zan = (LinearLayout) view.findViewById(R.id.lay_zan);
		ll_zan = (LinearLayout) view.findViewById(R.id.ll_zan);
		zan_count = (ImageView) view.findViewById(R.id.zan_count);
		zan = (ImageButton) view.findViewById(R.id.zan);
		LinearLayout ll_photo = (LinearLayout) view.findViewById(R.id.ll_photo);
		LinearLayout ll_grid1 = (LinearLayout) view
				.findViewById(R.id.ll_photo_grid1);
		LinearLayout ll_grid2 = (LinearLayout) view
				.findViewById(R.id.ll_photo_grid2);
		LinearLayout ll_grid3 = (LinearLayout) view
				.findViewById(R.id.ll_photo_grid3);

		ll_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try {
					Intent intent = new Intent();
					intent.setClass(context, MatchCommentDetailActivity.class);
					intent.putExtra("commentid", matchs.getString("id"));
					context.startActivity(intent);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		Spannable span = SmileUtils.getSmiledText(context,
				matchs.getString("text"));
		tx_content.setText(span, BufferType.SPANNABLE);

		name.setText(matchs.getString("nickname"));
		time.setText(matchs.getString("time"));
		commentCount.setText(matchs.getString("comment_count"));

		id = matchs.getString("id").toString();
		if (!matchs.getString("like_count").equals("0")) {
			likeCount.setText(matchs.getString("like_count"));
		}

		like_state = matchs.get("like_state").toString();
		if (like_state.equals("0")) {
			zan.setImageResource(R.drawable.abc_match_heart);
		} else {
			zan.setImageResource(R.drawable.selectedlove);

		}
		like_users = matchs.getJSONArray("like_users");
		if (Integer.parseInt(matchs.get("like_count").toString()) > 6) {
			zan_count.setVisibility(View.VISIBLE);
		} else {
			zan_count.setVisibility(View.GONE);
		}
		ll_zan.removeAllViews();
		for (int i = 0; i < like_users.length(); i++) {
			idUrl = like_users.getJSONObject(i).getString("id");
			if (i >= like_users.length() - 6) {
				CircleImageView header = new CircleImageView(context);
				header.setLayoutParams(new LayoutParams(60, 60));
				loader.LoadImage(like_users.getJSONObject(i).getString("icon"),
						header);
				ll_zan.addView(header);
			}
		}

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
								String resultCode = (String) resp.getJson()
										.getString("result_code");
								if ("0".equals(resultCode)) {

									Toast.makeText(context, "点赞成功",
											Toast.LENGTH_SHORT).show();
									// matchs.put("like_state", "1");
									like_state = "1";
									likeCount
											.setText(resp.getJson()
													.getString("count_like")
													.toString());

									CircleImageView userIcon = new CircleImageView(
											context);
									userIcon.setLayoutParams(new LayoutParams(
											60, 60));
									loader.LoadImage(iconUrl, userIcon);
									ll_zan.addView(userIcon, 0);
									like_users.put(0, new JSONObject().put(
											"id", AppConfig.getInstance()
													.getPlayerId()));
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
							Toast.makeText(context, resp.getErrorMessage(),
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
								resultCode = (String) resp.getJson().getString(
										"result_code");

								if ("0".equals(resultCode)) {

									Toast.makeText(context, "取消点赞成功",
											Toast.LENGTH_SHORT).show();
									// matchs.put("like_state", "0");
									like_state = "0";
									for (int i = 0; i < like_users.length(); i++) {
										idUrl = like_users.getJSONObject(i)
												.getString("id");
										if (idUrl.equals(AppConfig
												.getInstance().getPlayerId()
												+ "")) {
											// like_users.remove(i);
											ll_zan.removeViewAt(i);
										}
									}

									likeCount
											.setText(resp.getJson()
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
							Toast.makeText(context, resp.getErrorMessage(),
									Toast.LENGTH_SHORT).show();
						}
					});

				}

			}
		});
		// new DownAndShowImageTask(matchs.getString("user_icon"),
		// icon).execute();
		loader.LoadImage(matchs.getString("user_icon"), icon);

		JSONArray imgs = (JSONArray) matchs.get("images");
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
			photo.setScaleType(ImageView.ScaleType.FIT_CENTER);
			photo.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			// 设置Gallery组件的背景风格
			// oneHolder.photo.setVisibility(View.VISIBLE);
			StringBuffer s1 = new StringBuffer(imgs.getJSONObject(0).getString(
					"url"));
			// new DownAndShowImageTask(s1.insert(s1.lastIndexOf("."), "_M")
			// .toString(), photo).execute();
			loader.LoadImage(s1.insert(s1.lastIndexOf("."), "_M").toString(),
					photo);

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

	private void doPullDate(boolean isRefresh, String action,
			MCHttpCallBack listen) {
		// CPorgressDialog.showProgressDialog(context);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_content_id", id));
		new WebRequestUtil().execute(isRefresh, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
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

	private void addPhoto(int position, LinearLayout layout, JSONArray imgs)
			throws JSONException {
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

		{
			ImagerLoader loader = new ImagerLoader();
			loader.LoadImage(imgs.getJSONObject(position).getString("url"),
					photo);
		}
		// new DownAndShowImageTask(imgs.getJSONObject(position).getString(
		// "url"), photo).execute();

		layout.addView(photo);
	}
}
