package com.nowagme.football;

import java.util.ArrayList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.MyScrollView;
import cn.kangeqiu.kq.activity.view.MyScrollView.OnScrollListener;

import com.jingyi.MiChat.adapter.ImageListAdapter;
import com.jingyi.MiChat.widget.recyclerview.MCRefreshRecyclerView;
import com.nowagme.util.JsonUtil;

public class MatchActivityActivity extends FragmentActivity implements
		OnScrollListener {

	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["
			+ MatchActivityActivity.class.getName() + "]";
	private LinearLayout mBuyLayout;
	private LinearLayout mTopBuyLayout, ll_small;
	private Button On_the_list, Ranking, exercise, Hotline;
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
	private Button[] mTabs;
	private int[] action_colors = new int[2];
	private float from;
	private TextView match_subject, team_name1_tx, team_name2_tx;
	private ImageView team_icon1, team_icon2;
	private int screenWidth = 0;
	private Button create_comment, create_tanmu;

	//
	private MCRefreshRecyclerView board_list;
	private ImageListAdapter adapter;

	private RelativeLayout rl_main;
	private MyScrollView myScrollView;
	private View ll_top;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_match_activity_activity);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;

		initView();
		initHandle();
		initData();
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
			Toast.makeText(MatchActivityActivity.this, "未收到活动ID参数",
					Toast.LENGTH_SHORT).show();
		}
	}

	public String getMatchId() {
		return match_id;
	}

	private void initData() {
		View topView = getLayoutInflater()
				.inflate(R.layout.abc_match_top, null);
		// board_list.addHeaderView(topView);
		// board_list.oncr

		// board_list.setAdapter(null);
	}

	public void back(View view) {
		finish();
	}

	OnRefreshListener onRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {

			board_list.setRefreshing(true);
		}
	};

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

		int topBgHeight = dip2px(46);

		if (y > topBgHeight) {
			// Drawable drawable = mBookCategorImageBlur.getBackground();
			// if (drawable != null) {
			float realY = (y * 1.0f - topBgHeight);
			float realPercent = realY / dip2px(20);
			int alpha = (int) (realPercent * 255);
			Log.i("tag", "alpha:" + alpha);
			if (alpha < 0) {
				alpha = 255;
			} else if (alpha > 255) {
				alpha = 0;
			}

			// match_subject.setAlpha(alpha);
			// match_subject.setVisibility(View.VISIBLE);
			// }

			int height = dip2px(50);
			int translationY = height - (y - topBgHeight);
			if (translationY < 0)
				translationY = 0;
			if (translationY > height)
				translationY = height;
			// ViewHelper.setTranslationY(ll_small, translationY);
			// mBookName.scrollTo(0, translationY);
			// ll_small.setVisibility(View.VISIBLE);
		} else {
			// ll_small.setVisibility(View.GONE);
			// match_subject.setVisibility(View.GONE);
		}
	}

	private void initView() {
		board_list = (MCRefreshRecyclerView) findViewById(R.id.mListView);
		rl_main = (RelativeLayout) findViewById(R.id.main_lay);
		myScrollView = (MyScrollView) findViewById(R.id.scrollView);
		// match_subject = (TextView)
		// findViewById(R.id.abc_personal_activity_create__et_subject);
		board_list.setOnRefreshListener(onRefreshListener);
		ll_small = (LinearLayout) findViewById(R.id.ll_small);

		if (adapter == null) {
			adapter = new ImageListAdapter(this);
			board_list.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
		// ll_top = adapter.getTopView();
		myScrollView.setOnScrollListener(this, ll_small);
		// board_list.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View arg0, MotionEvent event) {
		// int action = event.getAction();
		// switch (action) {
		// case MotionEvent.ACTION_MOVE:
		// case MotionEvent.ACTION_SCROLL:
		// int upY = (int) event.getRawY();
		// int max = dip2px(100);
		// float scaleX = upY * 1.0f / max;
		// if (scaleX > 0.5f) {
		// scaleX = 0.5f;
		// }
		// ViewHelper.setScaleX(adapter.getTopView(), 1 - scaleX);
		// ViewHelper.setScaleY(adapter.getTopView(), 1 - scaleX);
		// }
		// return false;
		// }
		// });
	}

	/**
	 * log information
	 * 
	 * @param attribute2
	 */
	public static void logi(String s) {
		Log.i(TAG, CLASS_NAME + " " + s);
	}

	public int dip2px(float dipValue) {
		float scale = getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	@Override
	public void onScroll(int scrollY) {
		// TODO Auto-generated method stub

	}
}
