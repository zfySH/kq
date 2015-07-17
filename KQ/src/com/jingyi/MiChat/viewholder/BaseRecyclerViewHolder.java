package com.jingyi.MiChat.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 所有的ViewHolder都要继承BaseRecyclerViewHolder
 * @author zhangyongfeng02
 *
 */
public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder{
	private View mView;
	public BaseRecyclerViewHolder(View view) {
		super(view);
		mView = view;
	}
	
	public View getView(){
		return mView;
	}

}
