package com.nowagame.kq.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.MatchAllView;
import cn.kangeqiu.kq.activity.view.MatchImportView;
import cn.kangeqiu.kq.adapter.MyPagerAdapter;

public class AboutMatchActivity extends AgentActivity implements OnClickListener {
	public static ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private ImageView cursor_important, cusor_all;// 动画图片
	private TextView all, important;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_aboutmatch);

		init();

	}

	public void back(View view) {
		finish();
	}

	private void init() {
		important = (TextView) findViewById(R.id.important);
		all = (TextView) findViewById(R.id.all);
		important.setOnClickListener(new MyOnClickListener(0));
		all.setOnClickListener(new MyOnClickListener(1));
		InitViewPager();
		InitImageView();
	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor_important = (ImageView) findViewById(R.id.cursor_important);
		cusor_all = (ImageView) findViewById(R.id.cursor_all);
		// bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
		// .getWidth();// 获取图片宽度
		// DisplayMetrics dm = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(dm);
		// int screenW = dm.widthPixels;// 获取分辨率宽度
		// bmpW = screenW / 2;
		// cursor.setLayoutParams(new LayoutParams(bmpW, 5));
		// // offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		// offset = 0;
		// Matrix matrix = new Matrix();
		// matrix.postTranslate(offset, 0);
		// cursor.setImageMatrix(matrix);// 设置动画初始位置
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

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(new MatchImportView(this).getView(true));
		listViews.add(new MatchAllView(this).getView(true));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量

		// int two = one * 2;// 页卡1 -> 页卡3 偏移量

		@SuppressLint("ResourceAsColor")
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					// animation = new TranslateAnimation(one, 0, 0, 0);
					cursor_important
							.setBackgroundResource(R.color.top_bar_normal_bg);
					cusor_all.setBackgroundResource(R.color.trans);

				}
				// else if (currIndex == 2) {
				// animation = new TranslateAnimation(two, 0, 0, 0);
				// }
				break;
			case 1:
				if (currIndex == 0) {
					// animation = new TranslateAnimation(offset, one, 0, 0);
					cursor_important.setBackgroundResource(R.color.trans);
					cusor_all.setBackgroundResource(R.color.top_bar_normal_bg);
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

	@Override
	public void onClick(View v) {
	}

}
