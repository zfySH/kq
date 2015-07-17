package cn.kangeqiu.kq.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.utils.SmileUtils;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchAdapter extends BaseAdapter {
	JSONArray list = new JSONArray();
	private LayoutInflater inflater;
	ViewHolder viewHolder = null;
	private Timer timer;
	private Activity context;
	private LayoutParams params;
	private android.view.animation.Animation animation;
	private int type;// 0:所有比赛 1:关注比赛

	@Override
	public int getCount() {
		return list.length();
	}

	public MatchAdapter(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public void setItem(JSONArray list, int type) {
		if (list == null)
			return;

		this.list = list;
		this.type = type;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int arg0) {
		try {
			return list.getJSONObject(arg0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int postion, View view, ViewGroup arg2) {

		if (view == null) {
			view = inflater.inflate(R.layout.abc_fragment_match_item, null);
			viewHolder = new ViewHolder(view);
			view.setTag(R.id.ViewHolder, viewHolder);
			animation = AnimationUtils.loadAnimation(context,
					R.anim.applaud_animation);
		} else {
			viewHolder = (ViewHolder) view.getTag(R.id.ViewHolder);
		}
		try {

			String num = "0";
			JSONObject team1 = list.getJSONObject(postion).getJSONObject(
					"team1");
			JSONObject team2 = list.getJSONObject(postion).getJSONObject(
					"team2");
			viewHolder.team_name1.setText(team1.getString("name"));
			viewHolder.team_name2.setText(team2.getString("name"));

			viewHolder.team_icon1.setTag(R.id.iconUrl, team1.getString("icon"));
			viewHolder.team_icon2.setTag(R.id.iconUrl, team2.getString("icon"));
			// new DownAndShowImageTask(team1.getString("icon"),
			// viewHolder.team_icon1, true).execute();
			// new DownAndShowImageTask(team2.getString("icon"),
			// viewHolder.team_icon2, true).execute();
			// /**
			// * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
			// */
			ImagerLoader loader = new ImagerLoader();
			loader.LoadImage(team1.getString("icon"), viewHolder.team_icon1);
			loader.LoadImage(team2.getString("icon"), viewHolder.team_icon2);

			String time = list.getJSONObject(postion).getString("time");
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date date = null;
			Calendar calendar = null;

			try {
				date = format.parse(time);
				calendar = Calendar.getInstance();
				calendar.setTime(date);
			} catch (java.text.ParseException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			String mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));

			if (!list.getJSONObject(postion).isNull("isShowDate")) {
				if (list.getJSONObject(postion).getBoolean("isShowDate")) {
					viewHolder.ll_date_time.setVisibility(View.VISIBLE);
					viewHolder.date_time
							.setText(((calendar.get(Calendar.MONTH)) + 1) + "月"
									+ calendar.get(Calendar.DAY_OF_MONTH)
									+ "日 星期" + getMWeek(mWay));
				} else
					viewHolder.ll_date_time.setVisibility(View.GONE);
			}
			viewHolder.match_name.setText(list.getJSONObject(postion)
					.getString("name"));

			if (type == 0) {// 所有比赛
				viewHolder.all_title.setVisibility(View.VISIBLE);
				viewHolder.all_title.setText(list.getJSONObject(postion)
						.getString("name"));
				viewHolder.match_name.setVisibility(View.GONE);
				viewHolder.txt_number.setVisibility(View.GONE);
				viewHolder.ll_details.setVisibility(View.GONE);

				if (Integer.parseInt(list.getJSONObject(postion).getString(
						"state")) == 2) {
					viewHolder.channel.setText("已完场");
					viewHolder.channel
							.setTextColor(Color.parseColor("#8A8A8A"));

				} else {
					viewHolder.channel.setText(list.getJSONObject(postion)
							.getString("channel"));
					viewHolder.channel
							.setTextColor(Color.parseColor("#44CDD7"));
				}
			} else {

				if (Integer.parseInt(list.getJSONObject(postion).getString(
						"state")) == 2) {
					viewHolder.all_title.setVisibility(View.VISIBLE);
					viewHolder.all_title.setText("已完场");
				} else {
					viewHolder.all_title.setVisibility(View.GONE);
				}
				viewHolder.match_name.setVisibility(View.VISIBLE);
				viewHolder.txt_number.setVisibility(View.VISIBLE);
				viewHolder.ll_details.setVisibility(View.VISIBLE);
				viewHolder.channel.setText(list.getJSONObject(postion)
						.getString("channel"));
				// viewHolder.img_collect
				// .setBackgroundResource(R.drawable.img_collect);
				// viewHolder.img_collect.setClickable(false);
			}

			if (Integer
					.parseInt(list.getJSONObject(postion).getString("state")) == 0) {
				viewHolder.img_live.setVisibility(View.GONE);
				viewHolder.time.setVisibility(View.VISIBLE);

				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int min = calendar.get(Calendar.MINUTE);
				viewHolder.time.setText((hour == 0 ? hour + "0" : hour) + ":"
						+ (min == 0 ? min + "0" : min));
				viewHolder.img_collect.setVisibility(View.VISIBLE);
				viewHolder.lay_score.setVisibility(View.GONE);
			} else if (Integer.parseInt(list.getJSONObject(postion).getString(
					"state")) == 1) {
				viewHolder.img_live.setVisibility(View.VISIBLE);
				viewHolder.lay_score.setVisibility(View.VISIBLE);
				viewHolder.time.setVisibility(View.GONE);
				viewHolder.img_collect.setVisibility(View.GONE);
				viewHolder.txt_score.setText(team1.getString("score"));
				viewHolder.txt_score1.setText(team2.getString("score"));
			} else if (Integer.parseInt(list.getJSONObject(postion).getString(
					"state")) == 2) {
				viewHolder.img_live.setVisibility(View.GONE);
				viewHolder.time.setVisibility(View.GONE);
				viewHolder.img_collect.setVisibility(View.GONE);
				viewHolder.lay_score.setVisibility(View.VISIBLE);
				// viewHolder.time.setText(calendar.get(Calendar.HOUR_OF_DAY)
				// + ":" + calendar.get(Calendar.MINUTE));
				viewHolder.txt_score.setText(team1.getString("score"));
				viewHolder.txt_score1.setText(team2.getString("score"));
			}

			viewHolder.txt_number.setText(list.getJSONObject(postion)
					.getString("join_count") + "人参与");

			if (!list.getJSONObject(postion).isNull("activity")) {
				viewHolder.jingcai.setVisibility(View.VISIBLE);
				viewHolder.toupiao.setVisibility(View.VISIBLE);
				if (Integer.parseInt(list.getJSONObject(postion)
						.getJSONObject("activity").getString("type")) == 0) {
					viewHolder.txt_quiz.setText(list.getJSONObject(postion)
							.getJSONObject("activity").getString("title"));
				} else {
					viewHolder.txt_quiz.setText("");
					viewHolder.jingcai.setVisibility(View.GONE);
				}
				if (Integer.parseInt(list.getJSONObject(postion)
						.getJSONObject("activity").getString("type")) == 1) {
					viewHolder.txt_toupiao.setText(list.getJSONObject(postion)
							.getJSONObject("activity").getString("title"));
				} else {
					viewHolder.txt_toupiao.setText("");
					viewHolder.toupiao.setVisibility(View.GONE);
				}

			} else {
				viewHolder.txt_quiz.setText("");
				viewHolder.jingcai.setVisibility(View.GONE);
				viewHolder.txt_toupiao.setText("");
				viewHolder.toupiao.setVisibility(View.GONE);
			}
			if (!list.getJSONObject(postion).isNull("content")) {
				viewHolder.shangbang.setVisibility(View.VISIBLE);

				Spannable span = SmileUtils.getSmiledText(context, list
						.getJSONObject(postion).getJSONObject("content")
						.getString("body"));
				viewHolder.txt_onthelist.setText(span, BufferType.SPANNABLE);
			} else {
				viewHolder.shangbang.setVisibility(View.GONE);
			}

			int support_1 = Integer.parseInt(team1.getString("like_count"));
			int support_2 = Integer.parseInt(team2.getString("like_count"));

			// 获取宽度
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			int width = wm.getDefaultDisplay().getWidth();

			int restWidth = dip2px(context, 110);

			int progressWidth = 0;
			progressWidth = (width - restWidth) * support_1
					/ (support_1 + support_2);

			viewHolder.progress_left.setLayoutParams(new LayoutParams(
					progressWidth, LayoutParams.MATCH_PARENT));

			viewHolder.progress_left.setText(support_1 + "");
			viewHolder.progress_right.setText(support_2 + "");

			// viewHolder.progress_right.setLayoutParams(new LayoutParams(20,
			// LayoutParams.MATCH_PARENT));

			if (!list.getJSONObject(postion).isNull("attention_state")) {
				String attentionState = list.getJSONObject(postion).getString(
						"attention_state");
				if (attentionState.equals("1")) {
					viewHolder.img_collect
							.setBackgroundResource(R.drawable.img_collect);
					// viewHolder.img_collect.setClickable(false);
					viewHolder.img_collect.setClickable(true);
					viewHolder.img_collect
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(final View view) {
									try {
										doPullDate("2060",
												list.getJSONObject(postion)
														.getString("id"), null,
												new MCHttpCallBack() {
													@Override
													public void onSuccess(
															MCHttpResp resp) {
														super.onSuccess(resp);
														CPorgressDialog
																.hideProgressDialog();
														try {
															String resultCode = resp
																	.getJson()
																	.getString(
																			"result_code");
															if (resultCode
																	.equals("0")) {
																Toast.makeText(
																		context,
																		"取消关注成功",
																		Toast.LENGTH_SHORT)
																		.show();
																list.getJSONObject(
																		postion)
																		.put("attention_state",
																				"0");
																notifyDataSetChanged();
																// ((MainActivity)
																// context)
																// .refreshMatch();
																view.setBackgroundResource(R.drawable.img_uncollect);

																// JSONArray
																// newList = new
																// JSONArray();
																// for (int i =
																// 0; i < list
																// .length();
																// i++) {
																// if (i !=
																// postion)
																// newList.put(list
																// .getJSONObject(i));
																// }
																//
																// list =
																// newList;
																// notifyDataSetChanged();
																// view.setClickable(false);
															} else {
																Toast.makeText(
																		context,
																		resp.getJson()
																				.getString(
																						"message"),
																		Toast.LENGTH_SHORT)
																		.show();
															}
														} catch (Exception e) {
															// TODO
															// Auto-generated
															// catch block
															e.printStackTrace();
														}
													}

													@Override
													public void onError(
															MCHttpResp resp) {
														super.onError(resp);
														CPorgressDialog
																.hideProgressDialog();
													}
												});
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
				} else {
					viewHolder.img_collect
							.setBackgroundResource(R.drawable.img_uncollect);
					viewHolder.img_collect.setClickable(true);
					viewHolder.img_collect
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(final View view) {
									try {
										MobclickAgent.onEvent(context,
												"match_attention");
										TCAgent.onEvent(context,
												"match_attention");

										doPullDate("2031",
												list.getJSONObject(postion)
														.getString("id"), null,
												new MCHttpCallBack() {
													@Override
													public void onSuccess(
															MCHttpResp resp) {
														super.onSuccess(resp);
														CPorgressDialog
																.hideProgressDialog();
														try {
															String resultCode = resp
																	.getJson()
																	.getString(
																			"result_code");
															if (resultCode
																	.equals("0")) {
																Toast.makeText(
																		context,
																		"关注成功",
																		Toast.LENGTH_SHORT)
																		.show();
																list.getJSONObject(
																		postion)
																		.put("attention_state",
																				"1");
																notifyDataSetChanged();

																// ((MainActivity)
																// context)
																// .refreshMatch();
																view.setBackgroundResource(R.drawable.img_collect);
																// view.setClickable(false);
															} else {
																Toast.makeText(
																		context,
																		resp.getJson()
																				.getString(
																						"message"),
																		Toast.LENGTH_SHORT)
																		.show();
															}
														} catch (Exception e) {
															// TODO
															// Auto-generated
															// catch block
															e.printStackTrace();
														}
													}

													@Override
													public void onError(
															MCHttpResp resp) {
														super.onError(resp);
														CPorgressDialog
																.hideProgressDialog();
													}
												});
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
				}
			} else {
				viewHolder.img_collect
						.setBackgroundResource(R.drawable.img_collect);
				viewHolder.img_collect
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(final View view) {
								try {
									doPullDate("2060",
											list.getJSONObject(postion)
													.getString("id"), null,
											new MCHttpCallBack() {
												@SuppressLint("NewApi")
												@Override
												public void onSuccess(
														MCHttpResp resp) {
													super.onSuccess(resp);
													CPorgressDialog
															.hideProgressDialog();
													try {
														String resultCode = resp
																.getJson()
																.getString(
																		"result_code");
														if (resultCode
																.equals("0")) {
															Toast.makeText(
																	context,
																	"取消关注成功",
																	Toast.LENGTH_SHORT)
																	.show();

															list.getJSONObject(
																	postion)
																	.put("attention_state",
																			"0");
															notifyDataSetChanged();

															view.setBackgroundResource(R.drawable.img_uncollect);
															// ((MainActivity)
															// context)
															// .refreshMatch();
															// view.setClickable(false);
														} else {
															Toast.makeText(
																	context,
																	resp.getJson()
																			.getString(
																					"message"),
																	Toast.LENGTH_SHORT)
																	.show();
														}
													} catch (Exception e) {
														// TODO
														// Auto-generated
														// catch block
														e.printStackTrace();
													}
												}

												@Override
												public void onError(
														MCHttpResp resp) {
													super.onError(resp);
													CPorgressDialog
															.hideProgressDialog();
												}
											});
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
			}
			viewHolder.img_support_red.setClickable(true);
			viewHolder.img_support_blue.setClickable(true);

			viewHolder.img_support_red
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							try {
								MobclickAgent.onEvent(context, "match_support");
								TCAgent.onEvent(context, "match_support");
								list.getJSONObject(postion).put("num", "1");
								doPullDate("2003", list.getJSONObject(postion)
										.getString("id"), "1",
										new MCHttpCallBack() {
											@Override
											public void onSuccess(
													MCHttpResp resp) {
												super.onSuccess(resp);
												CPorgressDialog
														.hideProgressDialog();
												try {
													String resultCode = resp
															.getJson()
															.getString(
																	"result_code");
													if (resultCode.equals("0")) {
														JSONObject map = (JSONObject) list
																.getJSONObject(
																		postion)
																.getJSONObject(
																		"team1");
														map.put("like_count",
																(Integer.parseInt(map
																		.getString("like_count")) + 1)
																		+ "");

														notifyDataSetChanged();
													} else {
														Toast.makeText(
																context,
																resp.getJson()
																		.getString(
																				"message"),
																Toast.LENGTH_SHORT)
																.show();
													}
												} catch (Exception e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}

											@Override
											public void onError(MCHttpResp resp) {
												super.onError(resp);
												CPorgressDialog
														.hideProgressDialog();
												Toast.makeText(context,
														resp.getErrorMessage(),
														Toast.LENGTH_SHORT)
														.show();
											}
										});
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// num = 1;

						}
					});

			viewHolder.img_support_blue
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// num = 2;
							try {
								MobclickAgent.onEvent(context, "match_support");
								TCAgent.onEvent(context, "match_support");

								list.getJSONObject(postion).put("num", "2");
								doPullDate("2003", list.getJSONObject(postion)
										.getString("id"), "2",
										new MCHttpCallBack() {
											@Override
											public void onSuccess(
													MCHttpResp resp) {
												super.onSuccess(resp);
												CPorgressDialog
														.hideProgressDialog();
												try {
													String resultCode = resp
															.getJson()
															.getString(
																	"result_code");
													if (resultCode.equals("0")) {
														JSONObject map = (JSONObject) list
																.getJSONObject(
																		postion)
																.getJSONObject(
																		"team2");
														map.put("like_count",
																(Integer.parseInt(map
																		.getString("like_count")) + 1)
																		+ "");

														notifyDataSetChanged();
													} else {
														Toast.makeText(
																context,
																resp.getJson()
																		.getString(
																				"message"),
																Toast.LENGTH_SHORT)
																.show();
													}
												} catch (Exception e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}

											@Override
											public void onError(MCHttpResp resp) {
												super.onError(resp);
												CPorgressDialog
														.hideProgressDialog();
												Toast.makeText(context,
														resp.getErrorMessage(),
														Toast.LENGTH_SHORT)
														.show();
											}
										});
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						}
					});
			if (list.getJSONObject(postion).getString("num").equals("1")) {
				viewHolder.support1.setVisibility(View.VISIBLE);
				viewHolder.support2.setVisibility(View.GONE);
				viewHolder.support1.startAnimation(animation);
				notifyDataSetChanged();
				new Handler().postDelayed(new Runnable() {
					public void run() {
						viewHolder.support1.setVisibility(View.GONE);
						notifyDataSetChanged();
						try {
							list.getJSONObject(postion).put("num", "0");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, 1000);

			} else if (list.getJSONObject(postion).getString("num").equals("2")) {
				viewHolder.support2.setVisibility(View.VISIBLE);
				viewHolder.support1.setVisibility(View.GONE);
				viewHolder.support2.startAnimation(animation);
				notifyDataSetChanged();
				new Handler().postDelayed(new Runnable() {
					public void run() {
						viewHolder.support2.setVisibility(View.GONE);
						notifyDataSetChanged();
						try {
							list.getJSONObject(postion).put("num", "0");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 1000);
			} else if (list.getJSONObject(postion).getString("num").equals("0")) {
				viewHolder.support2.setVisibility(View.GONE);
				viewHolder.support1.setVisibility(View.GONE);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return view;
	}

	private String getMWeek(String week) {
		String mWay = "";
		if ("1".equals(week)) {
			mWay = "日";
		} else if ("2".equals(week)) {
			mWay = "一";
		} else if ("3".equals(week)) {
			mWay = "二";
		} else if ("4".equals(week)) {
			mWay = "三";
		} else if ("5".equals(week)) {
			mWay = "四";
		} else if ("6".equals(week)) {
			mWay = "五";
		} else if ("7".equals(week)) {
			mWay = "六";
		}
		return mWay;
	}

	private void doPullDate(String action, String matchId, String teamIndex,
			MCHttpCallBack listen) {
		CPorgressDialog.showProgressDialog(context);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_match_id", matchId));
		if (action.equals("2003"))
			pair.add(new BasicNameValuePair("u_team_index", teamIndex));
		else if (action.equals("2031") || action.equals("2060"))
			pair.add(new BasicNameValuePair("app_version", ""
					+ UpdataUtil.getAppVersion(context)));
		new WebRequestUtil().execute(false,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listen);

	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	class ViewHolder {
		TextView team_name1;
		CircleImageView team_icon1, team_icon2;
		TextView team_name2;
		ImageView img_live;
		TextView img_collect;
		TextView date_time;
		TextView match_name;
		TextView txt_score, txt_score1;
		TextView joins;
		TextView channel;
		TextView txt_quiz;
		TextView txt_onthelist;
		TextView time;
		TextView support1, support2;
		LinearLayout lay_score, ll_date_time;
		LinearLayout jingcai, shangbang, toupiao;
		TextView progress_left, progress_right;
		TextView txt_number;
		TextView txt_toupiao;
		Button img_support_red, img_support_blue;
		TextView all_title;
		LinearLayout ll_details;

		public ViewHolder(View view) {
			team_name1 = (TextView) view.findViewById(R.id.team_name1);
			team_icon1 = (CircleImageView) view.findViewById(R.id.team_icon1);
			team_name2 = (TextView) view.findViewById(R.id.team_name2);
			team_icon2 = (CircleImageView) view.findViewById(R.id.team_icon2);
			img_live = (ImageView) view.findViewById(R.id.img_live);
			img_collect = (TextView) view.findViewById(R.id.img_collect);
			date_time = (TextView) view.findViewById(R.id.date_time);
			match_name = (TextView) view.findViewById(R.id.match_name);
			txt_score = (TextView) view.findViewById(R.id.txt_score);
			txt_score1 = (TextView) view.findViewById(R.id.txt_score1);
			joins = (TextView) view.findViewById(R.id.joins);
			time = (TextView) view.findViewById(R.id.time);
			channel = (TextView) view.findViewById(R.id.channel);
			txt_quiz = (TextView) view.findViewById(R.id.txt_quiz);
			txt_onthelist = (TextView) view.findViewById(R.id.txt_onthelist);
			img_support_red = (Button) view.findViewById(R.id.img_support_red);
			img_support_blue = (Button) view
					.findViewById(R.id.img_support_blue);
			lay_score = (LinearLayout) view.findViewById(R.id.lay_score);
			ll_date_time = (LinearLayout) view.findViewById(R.id.ll_date_time);
			jingcai = (LinearLayout) view.findViewById(R.id.jingcai);
			shangbang = (LinearLayout) view.findViewById(R.id.shangbang);
			toupiao = (LinearLayout) view.findViewById(R.id.toupiao);
			progress_left = (TextView) view.findViewById(R.id.left_progress);
			progress_right = (TextView) view.findViewById(R.id.right_progress);
			support1 = (TextView) view.findViewById(R.id.support1);
			support2 = (TextView) view.findViewById(R.id.support2);
			txt_number = (TextView) view.findViewById(R.id.txt_number);
			txt_toupiao = (TextView) view.findViewById(R.id.txt_toupiao);
			all_title = (TextView) view.findViewById(R.id.all_title);
			ll_details = (LinearLayout) view.findViewById(R.id.ll_details);
		}
	}

}
