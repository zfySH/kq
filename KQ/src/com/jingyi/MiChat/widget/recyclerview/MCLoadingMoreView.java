package com.jingyi.MiChat.widget.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import cn.kangeqiu.kq.R;

import com.jingyi.MiChat.widget.progressbar.CircleProgressBar;

public class MCLoadingMoreView extends LinearLayout {
	
	private CircleProgressBar mProgressBar;
	public MCLoadingMoreView(Context context) {
		super(context);
		
	}

	public MCLoadingMoreView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	
	@Override
	protected void onFinishInflate() {
		init();
	}

	private void init() {
		mProgressBar = (CircleProgressBar)findViewById(R.id.progress_bar);
		
	}
	
	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if(mProgressBar != null){
			mProgressBar.setVisibility(visibility);
		}
	}

}
