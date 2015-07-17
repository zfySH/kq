package com.nowagme.football;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.BaseActivity;
import cn.kangeqiu.kq.activity.view.MyScrollView;
import cn.kangeqiu.kq.activity.view.MyScrollView.OnScrollListener;
import cn.kangeqiu.kq.refresh.PullToRefreshView;
import cn.kangeqiu.kq.refresh.PullToRefreshView.OnFooterRefreshListener;
import cn.kangeqiu.kq.refresh.PullToRefreshView.OnHeaderRefreshListener;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.king.photo.activity.MainActivity;
import com.nineoldandroids.view.ViewHelper;
import com.nowagame.kq.activity.CreatMyHouseActivity;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.JsonUtil;
import com.nowagme.util.RefreshListener;
import com.nowagme.util.ShareUtils;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeamActivityActivity extends BaseActivity implements
		OnScrollListener, OnHeaderRefreshListener, OnFooterRefreshListener {

	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["
			+ TeamActivityActivity.class.getName() + "]";
	private MyScrollView myScrollView;
	private LinearLayout mBuyLayout;
	private LinearLayout mTopBuyLayout, ll_top, ll_small;
	private TextView On_the_list, Ranking, exercise, Hotline;
	// private ImageButton phone_btn, address_btn;

	private String match_id;
	private String key;

	// private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private int currIndex;// 当前页卡编号
	private int bmpW;// 横线图片宽度
	private int offset;// 图片移动的偏移量
	private ImageView image;
	private int currentTabIndex;
	private Fragment[] fragments;
	private TextView[] mTabs;
	private int[] action_colors = new int[2];
	private float from;
	private TextView match_subject, team_name1_tx, team_name2_tx, type_txt,
			match_name, match_score, score_small;
	private ImageView team_icon1, team_icon2, team_icon1_small,
			team_icon2_small;
	private int screenWidth = 0;
	// private Button create_comment, create_tanmu, create_hourse;
	private PullToRefreshView mPullToRefreshView;
	private LinearLayout quiz;

	private Fragment secondFragment, thirdFragment, fourthFragment;
	private FragmentOnBoard btFragment;

	private JSONObject match = null;
	private ImagerLoader loader = new ImagerLoader();
	private static final int REQUEST_CODE_CREATE_COMMENT = 0;
	private ShareUtils shareUtil;
	private Button create_comment, create_tanmu, create_hourse;
	private boolean isUpdate = true;
	private Timer timer = new Timer(true);
	private boolean isOk = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_team_activity);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		shareUtil = new ShareUtils(this);
		initView();
		initHandle();
		initData();

		if (isUpdate) {
			// 启动定时器
			timer.schedule(task, 0, 10 * 1000);

		}
	}

	private TimerTask task = new TimerTask() {
		public void run() {
			Message msg = new Message();
			msg.what = 1;
			handler.sendMessage(msg);
		}
	};
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (isOk)
					getScore();
				// todo something....
			}
		}

	};

	private void getScore() {
		isOk = false;
		doPullDate("2065", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						refreshScore(resp.getJson().getJSONObject("match"));
						isOk = true;
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
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isUpdate = false;
	}

	// public void showCreateHourse(boolean isHourse) {
	//
	// if (isHourse) {
	// create_hourse.setVisibility(View.VISIBLE);
	//
	// create_tanmu.setVisibility(View.GONE);
	// create_comment.setVisibility(View.GONE);
	// } else {
	// create_hourse.setVisibility(View.GONE);
	//
	// create_tanmu.setVisibility(View.VISIBLE);
	// create_comment.setVisibility(View.VISIBLE);
	// }
	// }

	public void OnShare(View view) {
		MobclickAgent.onEvent(this, "match_share");
		TCAgent.onEvent(this, "match_share");

		shareUtil.open();
	}

	/**
	 * log information
	 * 
	 * @param attribute2
	 */
	public static void logi(String s) {
		Log.i(TAG, CLASS_NAME + " " + s);
	}

	public void refreshScore(JSONObject match) {
		try {
			JSONObject team1 = (JSONObject) match.getJSONObject("team1");
			JSONObject team2 = (JSONObject) match.getJSONObject("team2");

			match_score.setText(team1.getString("score") + ":"
					+ team2.getString("score"));
			type_txt.setText(getStatus(match.getString("state")));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void refresh(JSONObject match) throws JSONException {
		this.match = match;
		JSONObject team1 = (JSONObject) match.getJSONObject("team1");

		loader.LoadImage(team1.getString("icon"), team_icon1);
		loader.LoadImage(team1.getString("icon"), team_icon1_small);

		// new DownAndShowImageTask(team1.getString("icon"),
		// team_icon1).execute();
		// new DownAndShowImageTask(team1.getString("icon"), team_icon1_small)
		// .execute();
		team_name1_tx.setText(team1.getString("name"));

		JSONObject team2 = (JSONObject) match.getJSONObject("team2");
		loader.LoadImage(team2.getString("icon"), team_icon2);
		loader.LoadImage(team2.getString("icon"), team_icon2_small);
		// new DownAndShowImageTask(team2.getString("icon"),
		// team_icon2).execute();
		// new DownAndShowImageTask(team2.getString("icon"), team_icon2_small)
		// .execute();
		team_name2_tx.setText(team2.getString("name"));

		type_txt.setText(getStatus(match.getString("state")));
		match_name.setText(match.getString("name"));
		match_score.setText(team1.getString("score") + ":"
				+ team2.getString("score"));
		score_small.setText(team1.getString("score") + ":"
				+ team2.getString("score"));

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		Calendar calendar = null;

		try {
			date = format.parse(match.getString("time"));
			calendar = Calendar.getInstance();
			calendar.setTime(date);
		} catch (java.text.ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		// 设置分享的内容
		shareUtil.setShareContent(
				((calendar.get(Calendar.MONTH)) + 1) + "月"
						+ calendar.get(Calendar.DAY_OF_MONTH) + "日"
						+ calendar.get(Calendar.HOUR_OF_DAY) + "点"
						+ "，我和百万球迷一起观看＃" + team1.getString("name") + "vs"
						+ team2.getString("name") + "#，你也一起来吧！",
				R.drawable.match_share_logo);
	}

	private void initData() {
		getScore();
	}

	/**
	 * 设置活动ID
	 */
	private void setMatchId() {
		Map<String, Object> mapdata;
		try {
			mapdata = JsonUtil.parse(key);
			match_id = (String) mapdata.get("match_id");
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(TeamActivityActivity.this, "未收到活动ID参数",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void initHandle() {

		Bundle extras = this.getIntent().getExtras();

		String type = extras.getString("type");
		if (type == null)
			type = "";
		if (type.equals("msg")) {// 来自系统消息通知
			this.from = extras.getInt(AppConfig.FROM_PARAM_NAME);
			this.key = extras.getString(AppConfig.FROM_PARAM_KEY);
			setMatchId();
		} else {
			match_id = String.valueOf(extras.getInt("match_id"));// 获取活动ID
		}

	}

	public String getMatchId() {
		return match_id;
	}

	public void back(View view) {
		finish();
	}

	@SuppressLint("NewApi")
	public void setTitleBarAlpha(int y) {
		int imageScrollY = (int) (y * 0.5);
		// if (imageScrollY > dip2px(this, 120)) {
		// mBookCategorImage.scrollTo(0, dip2px(this, 120));
		// mBookCategorImageBlur.scrollTo(0, dip2px(this, 120));
		// } else {
		// mBookCategorImage.scrollTo(0, imageScrollY);
		// mBookCategorImageBlur.scrollTo(0, imageScrollY);
		// }

		int topBgHeight = dip2px(this, 46);

		if (y > topBgHeight) {
			// Drawable drawable = mBookCategorImageBlur.getBackground();
			// if (drawable != null) {
			float realY = (y * 1.0f - topBgHeight);
			float realPercent = realY / dip2px(this, 20);
			int alpha = (int) (realPercent * 255);
			Log.i("tag", "alpha:" + alpha);
			if (alpha < 0) {
				alpha = 255;
			} else if (alpha > 255) {
				alpha = 0;
			}

			// match_subject.setAlpha(alpha);
			match_subject.setVisibility(View.VISIBLE);
			// }

			int height = dip2px(this, 50);
			int translationY = height - (y - topBgHeight);
			if (translationY < 0)
				translationY = 0;
			if (translationY > height)
				translationY = height;
			ViewHelper.setTranslationY(ll_small, translationY);
			// mBookName.scrollTo(0, translationY);
			ll_small.setVisibility(View.VISIBLE);
		} else {
			ll_small.setVisibility(View.GONE);
			// match_subject.setVisibility(View.GONE);
		}
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	private void initView() {
		// mPager = (ViewPager) findViewById(R.id.viewpager);
		// image = (ImageView) findViewById(R.id.cursor);
		// fragment_container = (RelativeLayout)
		// findViewById(R.id.fragment_container);
		//
		// LayoutParams para;
		// para = image.getLayoutParams();
		//
		// DisplayMetrics dm = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(dm);
		// int screenW = dm.widthPixels;
		//
		// para.height = 4;
		// para.width = bmpW;
		// image.setLayoutParams(para);
		// offset = (screenW / 4 - bmpW) / 2;
		//
		// // imgageview设置平移，使下划线平移到初始位置（平移一个offset）
		// Matrix matrix = new Matrix();
		// matrix.postTranslate(offset, 0);
		// image.setImageMatrix(matrix);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setLastUpdated(new Date().toLocaleString());

		ll_top = (LinearLayout) findViewById(R.id.ll_scroll);
		ll_small = (LinearLayout) findViewById(R.id.ll_small);
		match_subject = (TextView) findViewById(R.id.abc_personal_activity_create__et_subject);
		quiz = (LinearLayout) findViewById(R.id.activity);
		team_name1_tx = (TextView) findViewById(R.id.team_name1);
		team_name2_tx = (TextView) findViewById(R.id.team_name2);
		team_icon1 = (ImageView) findViewById(R.id.team_icon1);
		team_icon2 = (ImageView) findViewById(R.id.team_icon2);
		type_txt = (TextView) findViewById(R.id.txt_type);
		match_name = (TextView) findViewById(R.id.txt_name);
		match_score = (TextView) findViewById(R.id.score);
		score_small = (TextView) findViewById(R.id.score_small);

		team_icon1_small = (CircleImageView) findViewById(R.id.team_icon1_small);
		team_icon2_small = (CircleImageView) findViewById(R.id.team_icon2_small);
		action_colors[0] = this.getResources().getColor(
				R.color.nearby_action_selected);
		action_colors[1] = this.getResources().getColor(R.color.text_color1);

		create_comment = (Button) findViewById(R.id.create_comment);
		create_tanmu = (Button) findViewById(R.id.create_tanmu);
		create_hourse = (Button) findViewById(R.id.create_hourse);
		create_comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MobclickAgent.onEvent(TeamActivityActivity.this,
						"match_dongtai");
				TCAgent.onEvent(TeamActivityActivity.this, "match_dongtai");

				createComment();
			}
		});
		create_tanmu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MobclickAgent.onEvent(TeamActivityActivity.this, "match_danmu");
				createTanmu();
			}
		});
		create_hourse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MobclickAgent.onEvent(TeamActivityActivity.this, "room_creat");
				TCAgent.onEvent(TeamActivityActivity.this, "room_creat");
				createHourse();
			}
		});
		// phone_btn = (ImageButton) findViewById(R.id.phone_btn);
		// address_btn = (ImageButton) findViewById(R.id.address_icon);
		myScrollView = (MyScrollView) findViewById(R.id.scrollView);
		mBuyLayout = (LinearLayout) findViewById(R.id.buy);
		mTopBuyLayout = (LinearLayout) findViewById(R.id.main_bottom);
		On_the_list = (TextView) findViewById(R.id.On_the_list);
		exercise = (TextView) findViewById(R.id.exercise);
		Hotline = (TextView) findViewById(R.id.Hotline);
		Ranking = (TextView) findViewById(R.id.Ranking);
		// On_the_list = (Button)
		// findViewById(R.id.buy).findViewById(R.id.On_the_list);
		// Ranking = (Button) findViewById(R.id.buy).findViewById(R.id.Ranking);
		// exercise = (Button)
		// findViewById(R.id.buy).findViewById(R.id.exercise);
		// Hotline = (Button) findViewById(R.id.buy).findViewById(R.id.Hotline);
		myScrollView.setOnScrollListener(this, ll_top);
		mTabs = new TextView[4];
		mTabs[0] = Hotline;
		mTabs[1] = On_the_list;
		mTabs[2] = exercise;
		mTabs[3] = Ranking;
		mTabs[0].setSelected(true);
		On_the_list.setOnClickListener(new txListener(1));
		exercise.setOnClickListener(new txListener(2));
		Hotline.setOnClickListener(new txListener(0));
		Ranking.setOnClickListener(new txListener(3));
		findViewById(R.id.main_lay).getViewTreeObserver()
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						onScroll(myScrollView.getScrollY());
					}
				});

		// fragmentList = new ArrayList<Fragment>();
		btFragment = new FragmentOnBoard();
		secondFragment = new com.nowagme.football.FragmentActivity();
		// Fragment secondFragment = TestFragment
		// .newInstance("this is second fragment");
		thirdFragment = new FragmentHotLine();
		fourthFragment = new FragmentMatchHourse();
		fragments = new Fragment[] { thirdFragment, btFragment, secondFragment,
				fourthFragment };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, thirdFragment)
				.show(thirdFragment).commit();
		// 给ViewPager设置适配器
		// mPager.setAdapter(new MyFragmentPagerAdapter(
		// getSupportFragmentManager(), fragmentList));
		// mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
		// mPager.setOnPageChangeListener(new MyOnPageChangeListener());//
		// 页面变化时的监听器
		myScrollView.setOnTouchListener(new OnTouchListener() {
			int lastX, lastY;

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
				case MotionEvent.ACTION_UP:
					int upX = (int) event.getRawX();
					int upY = (int) event.getRawY();

					// if (upY - lastY < 0) {// 上滑
					// myScrollView.scrollBy(0, 200 - (lastY - upY));
					// }
				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_SCROLL:

				}
				return false;
			}
		});

	}

	public void createHourse() {
		if (match != null && !match.equals("")) {

			Intent intent = new Intent();
			intent.setClass(TeamActivityActivity.this,
					CreatMyHouseActivity.class);
			intent.putExtra("match", match.toString());
			TeamActivityActivity.this.startActivity(intent);
		} else {
			Toast.makeText(TeamActivityActivity.this, "未获取比赛信息，请稍等",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void createComment() {
		Intent intent = new Intent();
		intent.setClass(TeamActivityActivity.this, MainActivity.class);
		intent.putExtra("matchId", match_id);
		TeamActivityActivity.this.startActivityForResult(intent,
				REQUEST_CODE_CREATE_COMMENT);
	}

	public void createTanmu() {
		Intent intent = new Intent();
		intent.setClass(TeamActivityActivity.this, MatchTanmuActivity.class);
		intent.putExtra("match_id", match_id);
		TeamActivityActivity.this.startActivity(intent);
	}

	public void scrollUp() {

		myScrollView.smoothScrollTo(0, 0);
	}

	public void setTitle() {
		match_subject.setVisibility(View.VISIBLE);
	}

	@Override
	public void onScroll(int scrollY) {
		int mBuyLayout2ParentTop = Math.max(scrollY, mBuyLayout.getTop());
		mTopBuyLayout.layout(0, mBuyLayout2ParentTop, mTopBuyLayout.getWidth(),
				mBuyLayout2ParentTop + mTopBuyLayout.getHeight());
	}

	private void doPullDate(String action, MCHttpCallBack listen) {
		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("app_version", UpdataUtil
				.getAppVersion(this)));

		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_match_id", match_id));

		new WebRequestUtil().execute(false,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listen);

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		private int one = offset * 2 + bmpW;// 两个相邻页面的偏移量

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			Animation animation = new TranslateAnimation(currIndex * one, arg0
					* one, 0, 0);// 平移动画
			currIndex = arg0;
			animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
			animation.setDuration(200);// 动画持续时间0.2秒
			image.startAnimation(animation);// 是用ImageView来显示动画的
			int i = currIndex + 1;
			Toast.makeText(TeamActivityActivity.this, "您选择了第" + i + "个页卡",
					Toast.LENGTH_SHORT).show();
		}
	}

	public class txListener implements View.OnClickListener {
		private int index = 0;

		public txListener(int i) {
			index = i;

		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// mPager.setCurrentItem(index);
			if (currentTabIndex != index) {
				FragmentTransaction trx = getSupportFragmentManager()
						.beginTransaction();
				// trx.hide(fragments[currentTabIndex]);
				if (!fragments[index].isAdded()) {
					trx.hide(fragments[currentTabIndex])
							.add(R.id.fragment_container, fragments[index])
							.commit();
				} else
					trx.hide(fragments[currentTabIndex]).show(fragments[index])
							.commit();
				if (index == 1) {
					quiz.setVisibility(View.VISIBLE);
					MobclickAgent.onEvent(TeamActivityActivity.this,
							"match_saikuang");
					TCAgent.onEvent(TeamActivityActivity.this, "match_saikuang");

				} else {
					quiz.setVisibility(View.GONE);
				}
				if (index == 3) {
					create_hourse.setVisibility(View.VISIBLE);
					create_tanmu.setVisibility(View.GONE);
					create_comment.setVisibility(View.GONE);
				} else {
					create_hourse.setVisibility(View.GONE);
					create_tanmu.setVisibility(View.VISIBLE);
					create_comment.setVisibility(View.VISIBLE);
				}
				if (index == 2) {
					MobclickAgent.onEvent(TeamActivityActivity.this,
							"match_event");
					TCAgent.onEvent(TeamActivityActivity.this, "match_event");

				}
				if (index == 0) {
					MobclickAgent.onEvent(TeamActivityActivity.this,
							"match_fabu");
					TCAgent.onEvent(TeamActivityActivity.this, "match_fabu");

				}

				if (index == 0) {
					create_tanmu.setVisibility(View.VISIBLE);
					create_comment.setVisibility(View.VISIBLE);
					create_hourse.setVisibility(View.GONE);
				} else if (index == 1) {
					create_tanmu.setVisibility(View.VISIBLE);
					create_comment.setVisibility(View.VISIBLE);
					create_hourse.setVisibility(View.GONE);
				} else if (index == 2) {
					create_tanmu.setVisibility(View.VISIBLE);
					create_comment.setVisibility(View.VISIBLE);
					create_hourse.setVisibility(View.GONE);
				} else if (index == 3) {
					create_tanmu.setVisibility(View.GONE);
					create_comment.setVisibility(View.GONE);
					create_hourse.setVisibility(View.VISIBLE);
				}
			}
			mTabs[currentTabIndex].setSelected(false);
			// // 把当前tab设为选中状态
			mTabs[index].setSelected(true);
			// mTabs[currentTabIndex].set
			currentTabIndex = index;

		}
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

	/**
	 * btFragment = new FragmentOnBoard(); secondFragment = new
	 * FragmentMatchHourse(); thirdFragment = new
	 * com.nowagme.football.FragmentActivity(); fourthFragment = new
	 * FragmentHotLine();
	 */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		switch (currentTabIndex) {
		case 0:
			((FragmentHotLine) thirdFragment).loadMore(new RefreshListener() {

				@Override
				public void onCompleted() {
					mPullToRefreshView.onFooterRefreshComplete();
				}
			});

			break;

		case 1:
			((FragmentOnBoard) btFragment).loadMore(new RefreshListener() {

				@Override
				public void onCompleted() {
					mPullToRefreshView.onFooterRefreshComplete();
				}
			});

			break;
		case 2:
			((com.nowagme.football.FragmentActivity) secondFragment)
					.loadMore(new RefreshListener() {

						@Override
						public void onCompleted() {
							mPullToRefreshView.onFooterRefreshComplete();
						}
					});
			break;
		case 3:
			((FragmentMatchHourse) fourthFragment)
					.loadMore(new RefreshListener() {

						@Override
						public void onCompleted() {
							mPullToRefreshView.onFooterRefreshComplete();
						}
					});
			break;
		}
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		switch (currentTabIndex) {
		case 0:
			((FragmentHotLine) thirdFragment).refresh(new RefreshListener() {

				@Override
				public void onCompleted() {
					mPullToRefreshView.onHeaderRefreshComplete("更新于:"
							+ new Date().toLocaleString());
				}
			});

			break;
		case 1:
			((FragmentOnBoard) btFragment).refresh(new RefreshListener() {

				@Override
				public void onCompleted() {
					mPullToRefreshView.onHeaderRefreshComplete("更新于:"
							+ new Date().toLocaleString());
				}
			});

			break;
		case 2:
			((com.nowagme.football.FragmentActivity) secondFragment)
					.refresh(new RefreshListener() {

						@Override
						public void onCompleted() {
							mPullToRefreshView.onHeaderRefreshComplete("更新于:"
									+ new Date().toLocaleString());
						}
					});
			break;
		case 3:
			((FragmentMatchHourse) fourthFragment)
					.refresh(new RefreshListener() {

						@Override
						public void onCompleted() {
							mPullToRefreshView.onHeaderRefreshComplete("更新于:"
									+ new Date().toLocaleString());
						}
					});
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// /** 使用SSO授权必须添加如下代码 */
		// UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
		// requestCode);
		// if (ssoHandler != null) {
		// ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		// }
		shareUtil.ssoResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_CREATE_COMMENT:
				((FragmentHotLine) thirdFragment).refresh(null);
				if (currentTabIndex != 0) {
					FragmentTransaction trx = getSupportFragmentManager()
							.beginTransaction();
					// trx.hide(fragments[currentTabIndex]);
					if (!fragments[0].isAdded()) {
						trx.hide(fragments[currentTabIndex])
								.add(R.id.fragment_container, fragments[0])
								.commit();
					} else
						trx.hide(fragments[currentTabIndex]).show(fragments[0])
								.commit();

					MobclickAgent.onEvent(TeamActivityActivity.this,
							"match_fabu");
					TCAgent.onEvent(TeamActivityActivity.this, "match_fabu");

					create_tanmu.setVisibility(View.VISIBLE);
					create_comment.setVisibility(View.VISIBLE);
					create_hourse.setVisibility(View.GONE);
				}
				mTabs[currentTabIndex].setSelected(false);
				// // 把当前tab设为选中状态
				mTabs[0].setSelected(true);
				// mTabs[currentTabIndex].set
				currentTabIndex = 0;
				break;

			}
		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("live"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		TCAgent.onPageStart(TeamActivityActivity.this, "live");

	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("live"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
											// onPageEnd 在onPause
											// 之前调用,因为 onPause 中会保存信息
		TCAgent.onPageEnd(TeamActivityActivity.this, "live");

	}
}
