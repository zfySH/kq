package cn.kangeqiu.kq.activity.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.WebRequestUtil;

public class FragmentMatchView {
	private Activity context;
	private LayoutInflater inflater;
	private android.view.animation.Animation animation;

	public FragmentMatchView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
		animation = AnimationUtils.loadAnimation(context,
				R.anim.applaud_animation);
	}

	public View getView(final JSONObject json) {
		View view = inflater.inflate(R.layout.abc_fragment_match_item, null);
		TextView team_name1 = (TextView) view.findViewById(R.id.team_name1);
		ImageView team_icon1 = (ImageView) view.findViewById(R.id.team_icon1);
		TextView team_name2 = (TextView) view.findViewById(R.id.team_name2);
		ImageView team_icon2 = (ImageView) view.findViewById(R.id.team_icon2);
		ImageView img_live = (ImageView) view.findViewById(R.id.img_live);
		ImageView img_collect = (ImageView) view.findViewById(R.id.img_collect);
		TextView date_time = (TextView) view.findViewById(R.id.date_time);
		TextView match_name = (TextView) view.findViewById(R.id.match_name);
		TextView txt_score = (TextView) view.findViewById(R.id.txt_score);
		TextView txt_score1 = (TextView) view.findViewById(R.id.txt_score1);
		TextView joins = (TextView) view.findViewById(R.id.joins);
		TextView time = (TextView) view.findViewById(R.id.time);
		TextView channel = (TextView) view.findViewById(R.id.channel);
		TextView txt_quiz = (TextView) view.findViewById(R.id.txt_quiz);
		TextView txt_onthelist = (TextView) view
				.findViewById(R.id.txt_onthelist);
		TextView img_support_red = (TextView) view
				.findViewById(R.id.img_support_red);
		TextView img_support_blue = (TextView) view
				.findViewById(R.id.img_support_blue);
		LinearLayout lay_score = (LinearLayout) view
				.findViewById(R.id.lay_score);
		LinearLayout jingcai = (LinearLayout) view.findViewById(R.id.jingcai);
		LinearLayout shangbang = (LinearLayout) view
				.findViewById(R.id.shangbang);
		TextView progress_left = (TextView) view
				.findViewById(R.id.left_progress);
		TextView progress_right = (TextView) view
				.findViewById(R.id.right_progress);
		final TextView support1 = (TextView) view.findViewById(R.id.support1);
		final TextView support2 = (TextView) view.findViewById(R.id.support2);
		TextView txt_number = (TextView) view.findViewById(R.id.txt_number);

		try {
			// String num = json.getString("num");
			JSONObject team1 = json.getJSONObject("team1");
			JSONObject team2 = json.getJSONObject("team2");
			JSONObject team3 = json.getJSONObject("activity");
			JSONObject team4 = json.getJSONObject("content");

			team_name1.setText(team1.getString("name"));
			team_name2.setText(team2.getString("name"));
			// new DownAndShowImageTask(team1.getString("icon"), team_icon1)
			// .execute();
			// new DownAndShowImageTask(team2.getString("icon"), team_icon2)
			// .execute();
			ImagerLoader loader = new ImagerLoader();
			loader.LoadImage(team1.getString("icon"), team_icon1);
			loader.LoadImage(team2.getString("icon"), team_icon2);

			String time_str = json.getString("time");
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date date = null;
			Calendar calendar = null;
			try {
				date = format.parse(time_str);
				calendar = Calendar.getInstance();
				calendar.setTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			String mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
			date_time.setText(((calendar.get(Calendar.MONTH)) + 1) + "月"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "日 周"
					+ getMWeek(mWay));

			match_name.setText(json.getString("name"));

			if (Integer.parseInt(json.getString("state")) == 0) {
				img_live.setVisibility(View.GONE);
				time.setVisibility(View.VISIBLE);
				time.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":"
						+ calendar.get(Calendar.MINUTE));
				img_collect.setVisibility(View.VISIBLE);
				lay_score.setVisibility(View.GONE);
			} else if (Integer.parseInt(json.getString("state")) == 1) {
				img_live.setVisibility(View.VISIBLE);
				lay_score.setVisibility(View.VISIBLE);
				time.setVisibility(View.GONE);
				img_collect.setVisibility(View.GONE);
				txt_score.setText(team1.getString("score"));
				txt_score1.setText(team2.getString("score"));

			} else if (Integer.parseInt(json.getString("state")) == 2) {
				img_live.setVisibility(View.GONE);
				time.setVisibility(View.VISIBLE);
				img_collect.setVisibility(View.GONE);
				lay_score.setVisibility(View.VISIBLE);
				time.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":"
						+ calendar.get(Calendar.MINUTE));
				txt_score.setText(team1.getString("score"));
				txt_score1.setText(team2.getString("score"));
			}

			txt_number.setText(json.getString("join_count") + "人参与");
			channel.setText(json.getString("channel"));
			if (team3 != null) {
				jingcai.setVisibility(View.VISIBLE);
				if (Integer.parseInt(team3.getString("type")) == 0) {
					txt_quiz.setText(team3.getString("title"));
				} else {
					txt_quiz.setText("");
					jingcai.setVisibility(View.GONE);
				}

			} else if (team3 == null) {
				txt_quiz.setText("");
				jingcai.setVisibility(View.GONE);
			}
			if (team4 != null) {
				shangbang.setVisibility(View.VISIBLE);
				txt_onthelist.setText(team4.getString("body"));
			} else {
				shangbang.setVisibility(View.GONE);
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

			progress_left.setLayoutParams(new LayoutParams(progressWidth,
					LayoutParams.MATCH_PARENT));

			progress_left.setText(support_1 + "");
			progress_right.setText(support_2 + "");

			// progress_right.setLayoutParams(new LayoutParams(20,
			// LayoutParams.MATCH_PARENT));

			img_support_red.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						// json.put("num", "1");
						doPullDate("2003", json.getString("id"), "1",
								new MCHttpCallBack() {
									@Override
									public void onSuccess(MCHttpResp resp) {
										super.onSuccess(resp);
										CPorgressDialog.hideProgressDialog();
										try {
											String resultCode = resp.getJson()
													.getString("result_code");
											if (resultCode.equals("0")) {
												JSONObject map = json
														.getJSONObject("team1");
												map.put("like_count",
														(Integer.parseInt(map
																.getString("like_count")) + 1)
																+ "");
												support("1", support1, support2);
												// notifyDataSetChanged();
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
										Toast.makeText(context,
												resp.getErrorMessage(),
												Toast.LENGTH_SHORT).show();
										CPorgressDialog.hideProgressDialog();
									}
								});
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// num = 1;

				}
			});

			img_support_blue.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// num = 2;
					try {
						json.put("num", "2");
						doPullDate("2003", json.getString("id"), "2",
								new MCHttpCallBack() {
									@Override
									public void onSuccess(MCHttpResp resp) {
										super.onSuccess(resp);
										CPorgressDialog.hideProgressDialog();
										try {
											String resultCode = resp.getJson()
													.getString("result_code");
											if (resultCode.equals("0")) {
												JSONObject map = json
														.getJSONObject("team2");
												map.put("like_count",
														(Integer.parseInt(map
																.getString("like_count")) + 1)
																+ "");
												support("2", support1, support2);
												// notifyDataSetChanged();
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
										Toast.makeText(context,
												resp.getErrorMessage(),
												Toast.LENGTH_SHORT).show();
										CPorgressDialog.hideProgressDialog();
									}
								});
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			});

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return view;
	}

	private void support(String num, final TextView support1,
			final TextView support2) {
		if (num.equals("1")) {
			support1.setVisibility(View.VISIBLE);
			support2.setVisibility(View.GONE);
			support1.startAnimation(animation);
			// notifyDataSetChanged();
			new Handler().postDelayed(new Runnable() {
				public void run() {
					support1.setVisibility(View.GONE);
					// notifyDataSetChanged();
					// try {
					// json.put("num", "0");
					// } catch (Exception e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
				}
			}, 1000);

		} else if (num.equals("2")) {
			support2.setVisibility(View.VISIBLE);
			support1.setVisibility(View.GONE);
			support2.startAnimation(animation);
			// notifyDataSetChanged();
			new Handler().postDelayed(new Runnable() {
				public void run() {
					support2.setVisibility(View.GONE);
					// notifyDataSetChanged();
					// try {
					// json.put("num", "0");
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
				}
			}, 1000);
		} else if (num.equals("0")) {
			support2.setVisibility(View.GONE);
			support1.setVisibility(View.GONE);
		}
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
		pair.add(new BasicNameValuePair("u_team_index", teamIndex));

		new WebRequestUtil().execute(false,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listen);

		// Map<String, String> parameters = new HashMap<String, String>();
		// parameters.put("app_action", action);
		// parameters.put("app_platform", "0");
		// parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		// parameters.put("u_match_id", matchId);
		// parameters.put("u_team_index", teamIndex);
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

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
