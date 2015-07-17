package com.jingyi.MiChat.viewholder;

import android.support.v7.widget.RecyclerView;

import com.jingyi.MiChat.widget.recyclerview.MCLoadingMoreView;

public class ListViewLoadMoreViewHolder extends RecyclerView.ViewHolder{
	public MCLoadingMoreView mLoadMoreView;

	public ListViewLoadMoreViewHolder(MCLoadingMoreView moreView) {
		super(moreView);
		this.mLoadMoreView = moreView;
	}

}
