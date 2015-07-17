package com.jingyi.MiChat.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.view.MatchTopView;

import com.jingyi.MiChat.core.bitmap.BitmapHelper;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;

public class ImageListAdapter extends MCRecyclerViewAdapter {

	private List<String> list;
	private BitmapUtils mBitmapUtils;
	private MatchTopView topView = null;

	

	// public View getTopView() {
	// if (topView != null)
	// return topView.getTopView();
	// return null;
	// }

	public ImageListAdapter(Context context) {
		super(context);
		mBitmapUtils = BitmapHelper.getBitmapUtils(context);
		mBitmapUtils.configDefaultLoadingImage(R.drawable.ic_launcher);
		mBitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_launcher);
		mBitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
	}

	// public void setImgSrcList(List<String> srcList) {
	// this.list = srcList;
	// }

	private String getItem(int position) {
		return null;
	}

	@Override
	protected int getHeaderItemCount() {
		return 1;
	}

	@Override
	protected int getContentItemCount() {
		return 0;
	}

	@Override
	protected ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent,
			int headerViewType) {
		topView = new MatchTopView(ctx, mInflater);
		ImageListViewHolder viewHolder = new ImageListViewHolder(
				topView.getView());
		return viewHolder;
	}

	@Override
	protected ViewHolder onCreateContentItemViewHolder(ViewGroup parent,
			int contentViewType) {
		// final View contentView = mInflater.inflate(
		// R.layout.test_image_list_item, null);
		// ImageListViewHolder viewHolder = new
		// ImageListViewHolder(contentView);
		// return viewHolder;
		return null;
	}

	@Override
	protected void onBindHeaderItemViewHolder(ViewHolder headerViewHolder,
			int position) {
		// TODO Auto-generated method stub
		final String src = getItem(position);
		ImageListViewHolder viewHolder = null;
		if (headerViewHolder instanceof ImageListViewHolder) {
			viewHolder = (ImageListViewHolder) headerViewHolder;
		}
		if (viewHolder != null) {
			// mBitmapUtils.display(viewHolder.mImage, src,
			// new ImageListLoaderCallBack(viewHolder));
		}
	}

	@Override
	protected void onBindContentItemViewHolder(ViewHolder contentViewHolder,
			int position) {
		// final String src = getItem(position);
		// ImageListViewHolder viewHolder = null;
		// if (contentViewHolder instanceof ImageListViewHolder) {
		// viewHolder = (ImageListViewHolder) contentViewHolder;
		// }
		// if (viewHolder != null) {
		// mBitmapUtils.display(viewHolder.mImage, src,
		// new ImageListLoaderCallBack(viewHolder));
		// }

	}

	private class ImageListLoaderCallBack extends
			DefaultBitmapLoadCallBack<ImageView> {
		private ImageListViewHolder holder;

		public ImageListLoaderCallBack(ImageListViewHolder vh) {
			this.holder = vh;
		}

		@Override
		public void onLoading(ImageView container, String uri,
				BitmapDisplayConfig config, long total, long current) {
			// this.holder.mProgressBar.setProgress((int) (current * 100 /
			// total));
		}

		@Override
		public void onLoadCompleted(ImageView container, String uri,
				Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
			fadeInDisplay(container, bitmap);
			// this.holder.mProgressBar.setProgress(100);
		}

	}

	private static final ColorDrawable TRANSPARENT_DRAWABLE = new ColorDrawable(
			android.R.color.transparent);

	private void fadeInDisplay(ImageView imageView, Bitmap bitmap) {
		final TransitionDrawable transitionDrawable = new TransitionDrawable(
				new Drawable[] { TRANSPARENT_DRAWABLE,
						new BitmapDrawable(imageView.getResources(), bitmap) });
		imageView.setImageDrawable(transitionDrawable);
		transitionDrawable.startTransition(500);
	}

}
