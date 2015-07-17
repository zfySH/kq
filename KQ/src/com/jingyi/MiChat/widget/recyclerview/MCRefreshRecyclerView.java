package com.jingyi.MiChat.widget.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.jingyi.MiChat.adapter.MCRecyclerViewAdapter;
import com.jingyi.MiChat.widget.MCRecyleViewItemDivider;

public class MCRefreshRecyclerView extends SwipeRefreshLayout {
	private Activity ctx;
	private RecyclerView mRecyclerView;
	private MCRecyclerViewAdapter mAdapter;
	protected LayoutInflater inflater;
	// 默认是locked
	private DispatchTouchListener mDispatchTouchListener;
	private OnRefreshListener mOnRefreshListener;
	private OnMCScrollListener scrollListener;
	public LoadMoreListener mLoadMoreListener;
	protected boolean mLoadcomplete = true;
	private boolean mIsLoading = false;
	private LinearLayoutManager mlinearLayoutManager;

	private float startY;

	ViewStub mViewStub;
	ImageView loading_view_error_image;
	TextView loading_view_error_text;
	TextView loading_view_error_btn;
	View loading_error_view;

	private static final int DEFAULT_CIRCLE_TARGET = 64;

	public MCRefreshRecyclerView(Context context) {
		super(context);
		ctx = (Activity) context;
		init();
	}

	public MCRefreshRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctx = (Activity) context;
		init();
	}

	public MCRefreshRecyclerView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs);
		ctx = (Activity) context;
		init();
	}

	private void init() {
		setColorSchemeColors(R.color.top_nav_bg);
		inflater = LayoutInflater.from(ctx);

		FrameLayout frameLayout = new FrameLayout(ctx);
		frameLayout.addView(getChildView(ctx));
		mViewStub = new ViewStub(ctx);
		mViewStub.setLayoutResource(R.layout.loading_error_view);
		frameLayout.addView(mViewStub);

		addView(frameLayout);
	}

	RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
			if (scrollListener != null) {
				scrollListener.onScrollStateChanged(recyclerView, newState);
			}
		};

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			if (mLoadMoreListener != null) {
				try {

					int lastVisibleItem = mlinearLayoutManager
							.findLastVisibleItemPosition();
					int totalItemCount = mlinearLayoutManager.getItemCount();
					if (lastVisibleItem == totalItemCount - 1 && dy > 0) {
						if (mIsLoading || mLoadcomplete) {
							// 不加载
						} else {
							if (mAdapter != null) {
								mAdapter.showLoadMore(true);
							}
							mLoadMoreListener.loadMore();
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

			if (scrollListener != null) {
				scrollListener.onScrolled(recyclerView, dx, dy);
			}
		}
	};

	/***
	 * 获取和创建当前container中的view
	 * 
	 * @return
	 */
	protected View getChildView(Context context) {
		if (mRecyclerView == null) {
			mRecyclerView = new RecyclerView(context);
			mRecyclerView.setFadingEdgeLength(0);
			mlinearLayoutManager = new LinearLayoutManager(context);
			mRecyclerView.setHasFixedSize(false);
			mRecyclerView.setLayoutManager(mlinearLayoutManager);
		}
		return mRecyclerView;
	}

	private void superRefreshing(boolean isLoading) {
		super.setRefreshing(isLoading);
	}

	Runnable startRefreshRunnable = new Runnable() {

		@Override
		public void run() {
			if (mIsLoading) {
				superRefreshing(true);
			}
		}
	};

	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN
				&& mDispatchTouchListener != null) {
			mDispatchTouchListener.dispatchTouchEvent();
		}
		return super.dispatchTouchEvent(ev);
	};

	public void setAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
		if (adapter instanceof MCRecyclerViewAdapter) {
			mAdapter = (MCRecyclerViewAdapter) adapter;
			mAdapter.setLoadComplete(mLoadcomplete);
		}

		if (mRecyclerView != null)
			mRecyclerView.setAdapter(adapter);
	}

	public int findLastVisibleItemPosition() {
		return mlinearLayoutManager.findLastVisibleItemPosition();
	}

	public int findFirstVisibleItemPosition() {
		return mlinearLayoutManager.findFirstVisibleItemPosition();
	}

	public int getTotalItemCount() {
		return mlinearLayoutManager.getItemCount();
	}

	public void setProgressPosition(float top) {
		final DisplayMetrics metrics = getResources().getDisplayMetrics();
		float mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
		setProgressViewEndTarget(false, (int) (mSpinnerFinalOffset + top));
	}

	public void resetProgressPosition() {
		final DisplayMetrics metrics = getResources().getDisplayMetrics();
		float mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
		setProgressViewEndTarget(false, (int) mSpinnerFinalOffset);
	}

	public void setDivider() {
		if (mRecyclerView != null) {
			mRecyclerView.addItemDecoration(new MCRecyleViewItemDivider(ctx));
		}
	}

	public void scrollToPosition(int position) {
		mlinearLayoutManager.scrollToPosition(position);
	}

	/***
	 * 子view如果有滚动的话，那么和下拉刷新冲突，这里要返回子View，listView或者scrollview
	 * 
	 * @return
	 */
	protected View getScrollView() {
		return getChildView(ctx);
	}

	@Override
	public boolean canChildScrollUp() {
		if (android.os.Build.VERSION.SDK_INT < 14) {
			if (getScrollView() instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) getScrollView();
				return absListView.getChildCount() > 0
						&& (absListView.getFirstVisiblePosition() > 0 || absListView
								.getChildAt(0).getTop() < absListView
								.getPaddingTop());
			} else {
				return getScrollView().getScrollY() > 0;
			}
		} else {
			return ViewCompat.canScrollVertically(getScrollView(), -1);
		}
	}

	@Override
	public void setOnRefreshListener(OnRefreshListener listener) {
		super.setOnRefreshListener(listener);
		mOnRefreshListener = listener;
	}

	@Override
	public void setRefreshing(boolean isLoading) {
		if (loading_error_view != null) {
			loading_error_view.setVisibility(View.GONE);
		}
		getChildView(ctx).setVisibility(View.VISIBLE);
		mIsLoading = isLoading;
		if (!isLoading) {
			super.setRefreshing(isLoading);
		} else {
			// 由于setRefreshing不能在onCreate里调用，所以这里做个startRefreshing的方法方便使用
			postDelayed(startRefreshRunnable, 200);
		}
	}

	public void setLoadingError(String msg) {
		super.setRefreshing(false);
		if (loading_error_view == null) {
			loading_error_view = mViewStub.inflate();
			loading_view_error_text = (TextView) loading_error_view
					.findViewById(R.id.loading_view_error_text);
			loading_view_error_btn = (TextView) loading_error_view
					.findViewById(R.id.loading_view_error_btn);
			if (loading_view_error_btn != null) {
				loading_view_error_btn
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								setRefreshing(true);
								if (mOnRefreshListener != null)
									mOnRefreshListener.onRefresh();
							}
						});
			}
		}
		if (loading_view_error_text != null) {
			loading_view_error_text.setText(msg);
		}
		loading_error_view.setVisibility(View.VISIBLE);
		getChildView(ctx).setVisibility(View.GONE);
	}

	public void setLoadingNoData(String msg) {
		super.setRefreshing(false);
		if (loading_error_view == null) {
			loading_error_view = mViewStub.inflate();
			loading_view_error_image = (ImageView) loading_error_view
					.findViewById(R.id.loading_view_error_image);
			loading_view_error_image.setVisibility(View.GONE);
			loading_view_error_text = (TextView) loading_error_view
					.findViewById(R.id.loading_view_error_text);
			loading_view_error_btn = (TextView) loading_error_view
					.findViewById(R.id.loading_view_error_btn);
			loading_view_error_btn.setVisibility(View.GONE);
		}
		if (loading_view_error_text != null) {
			loading_view_error_text.setText(msg);
		}
		loading_error_view.setVisibility(View.VISIBLE);
		getChildView(ctx).setVisibility(View.GONE);
	}

	/**
	 * 设置数据加载完成并关闭加载更多功能
	 * 
	 * @param loadcomplete
	 *            true表示加载完成，关闭加载更多
	 */
	public void setLoadMoreComplete(boolean loadcomplete) {
		mLoadcomplete = loadcomplete;
		if (mAdapter != null) {
			mAdapter.setLoadComplete(loadcomplete);
		}
	}

	/**
	 * 设置加载更多界面时候显示
	 * 
	 * @param show
	 *            true-显示 false-不显示
	 */
	public void setLoadMoreShow(boolean show) {
		if (mAdapter != null) {
			mAdapter.showLoadMore(show);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			startY = ev.getY();
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			if (Math.abs(ev.getY() - startY) > 10) {
				ViewParent parent = getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
					return super.onInterceptTouchEvent(ev);
				}
			}
		} else if (ev.getAction() == MotionEvent.ACTION_UP
				|| ev.getAction() == MotionEvent.ACTION_CANCEL) {
			ViewParent parent = getParent();
			if (parent != null) {
				parent.requestDisallowInterceptTouchEvent(false);
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	public void setLoadMoreListener(LoadMoreListener listener) {
		mLoadMoreListener = listener;
		if (mRecyclerView != null) {
			mRecyclerView.setOnScrollListener(onScrollListener);
		}
		setLoadMoreComplete(false);
	}

	public void setOnQDScrollListener(OnMCScrollListener listener) {
		this.scrollListener = listener;
		if (mRecyclerView != null) {
			mRecyclerView.setOnScrollListener(onScrollListener);
		}
	}

	public void setDispatchTouchListener(DispatchTouchListener l) {
		mDispatchTouchListener = l;
	}

	public interface DispatchTouchListener {
		public void dispatchTouchEvent();
	}

	public interface OnMCScrollListener {
		void onScrollStateChanged(RecyclerView recyclerView, int newState);

		void onScrolled(RecyclerView recyclerView, int dx, int dy);
	}

	public interface LoadMoreListener {
		public void loadMore();
	}
}
