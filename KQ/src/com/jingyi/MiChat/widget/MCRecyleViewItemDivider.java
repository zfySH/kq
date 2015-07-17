package com.jingyi.MiChat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MCRecyleViewItemDivider extends RecyclerView.ItemDecoration{

	Paint paint = new Paint();

	public MCRecyleViewItemDivider(Context context) {
	
	}

	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDraw(c, parent, state);
	}

	@Override
	public void onDrawOver(Canvas c, RecyclerView parent,
			RecyclerView.State state) {
		super.onDrawOver(c, parent, state);
		paint.setColor(Color.parseColor("#dcdcdc"));
		for (int i = 0, size = parent.getChildCount(); i < size; i++) {
			View child = parent.getChildAt(i);
			c.drawLine(child.getLeft(), child.getBottom(), child.getRight(),
					child.getBottom(), paint);
		}
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
			RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
	}



}
