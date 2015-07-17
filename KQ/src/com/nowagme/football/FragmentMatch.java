package com.nowagme.football;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.MainActivity;
import cn.kangeqiu.kq.activity.view.MatchAllView;
import cn.kangeqiu.kq.activity.view.MatchImportView;
import cn.kangeqiu.kq.adapter.MyPagerAdapter;

import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("ValidFragment")
public class FragmentMatch extends Fragment {

	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["
			+ FragmentMatch.class.getName() + "]";

	private View view;
	public static ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片
	private TextView all, important;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	Context context;
	private int mColorRes = -1;
	private ImageButton personal;

	public FragmentMatch() {
		this(R.color.red);
	}

	public FragmentMatch(int colorRes) {
		mColorRes = colorRes;
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		logi("onCreateView()");
		view = inflater.inflate(R.layout.abc_fragment_team, null);
		initView();
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		logi("onCreate()");
		super.onCreate(savedInstanceState);

	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	private MatchAllView allView;
	private MatchImportView importantView;

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) view.findViewById(R.id.vPager);
		allView = new MatchAllView(getActivity());
		importantView = new MatchImportView(getActivity());
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getActivity().getLayoutInflater();
		listViews.add(allView.getView(false));
		listViews.add(importantView.getView(false));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/*
	 * 初始化控件
	 */
	private void initView() {
		logi("initView()");
		all = (TextView) view.findViewById(R.id.all);
		important = (TextView) view.findViewById(R.id.important);
		personal = (ImageButton) view.findViewById(R.id.personal);
		InitViewPager();
		all.setOnClickListener(new MyOnClickListener(0));
		important.setOnClickListener(new MyOnClickListener(1));
		personal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity fac = (MainActivity) getActivity();
				fac.openMenu();
			}
		});
		// 按钮点击事件

	}

	public void refreshMatch() {
		if (allView != null)
			allView.doFirstShowNearby();

		if (importantView != null)
			importantView.doFirstShowNearby();
		// initView();
	}

	/**
	 * log information
	 * 
	 * @param msg
	 */
	public static void logi(String msg) {
		Log.i(TAG, CLASS_NAME + " " + msg);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		@SuppressLint("ResourceAsColor")
		@Override
		public void onPageSelected(int arg0) {

			refreshMatch();
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					// animation = new TranslateAnimation(one, 0, 0, 0);
					all.setBackgroundResource(R.drawable.abc_button_viewpager_white_left);
					all.setTextColor(getActivity().getResources().getColor(
							R.color.top_bar_normal_bg));
					important.setBackgroundResource(R.drawable.trans_bg);
					important.setTextColor(getActivity().getResources()
							.getColor(R.color.white));

				}
				// else if (currIndex == 2) {
				// animation = new TranslateAnimation(two, 0, 0, 0);
				// }
				break;
			case 1:
				if (currIndex == 0) {
					// animation = new TranslateAnimation(offset, one, 0, 0);
					all.setBackgroundResource(R.color.transparent);
					all.setTextColor(getActivity().getResources().getColor(
							R.color.white));
					important
							.setBackgroundResource(R.drawable.abc_button_viewpager_white_right);
					important.setTextColor(getActivity().getResources()
							.getColor(R.color.top_bar_normal_bg));
				}
				// else if (currIndex == 2) {
				// animation = new TranslateAnimation(two, one, 0, 0);
				// }
				break;
			// case 2:
			// if (currIndex == 0) {
			// animation = new TranslateAnimation(offset, two, 0, 0);
			// } else if (currIndex == 1) {
			// animation = new TranslateAnimation(one, two, 0, 0);
			// }
			// break;
			}
			currIndex = arg0;
			// animation.setFillAfter(true);// True:图片停在动画结束位置
			// animation.setDuration(300);
			// cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("match"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		TCAgent.onPageStart(getActivity(), "match");

	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("match"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
											// onPageEnd 在onPause
											// 之前调用,因为 onPause 中会保存信息
		TCAgent.onPageEnd(getActivity(), "match");

	}
}