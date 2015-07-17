package cn.kangeqiu.kq.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class ResizeLayout extends RelativeLayout {
	private static int count = 0;

	public ResizeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		Log.v("onSizeChanged " + count++, "=>onResize called! w=" + w + ",h="
				+ h + ",oldw=" + oldw + ",oldh=" + oldh);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		Log.v("onLayout " + count++, "=>OnLayout called! l=" + l + ", t=" + t
				+ ",r=" + r + ",b=" + b);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		Log.v("onMeasure " + count++, "=>onMeasure called! widthMeasureSpec="
				+ widthMeasureSpec + ", heightMeasureSpec=" + heightMeasureSpec);
	}
}