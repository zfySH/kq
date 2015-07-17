package cn.kangeqiu.kq.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.nineoldandroids.view.ViewHelper;
import com.nowagme.football.TeamActivityActivity;

/**
 * @blog http://blog.csdn.net/xiaanming
 * 
 * @author xiaanming
 * 
 */
public class MyScrollView extends ScrollView {
	private OnScrollListener onScrollListener;
	private View view;

	public MyScrollView(Context context) {
		this(context, null);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * ���ù����ӿ�
	 * 
	 * @param onScrollListener
	 */
	public void setOnScrollListener(OnScrollListener onScrollListener, View view) {
		this.onScrollListener = onScrollListener;
		this.view = view;
	}

	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (onScrollListener != null) {
			onScrollListener.onScroll(t);
		}

		Context context = getContext();
		int max = dip2px(context, 100);
		float scaleX = t * 1.0f / max;
		if (scaleX > 0.5f) {
			scaleX = 0.5f;
		}
		ViewHelper.setScaleX(view, 1 - scaleX);
		ViewHelper.setScaleY(view, 1 - scaleX);
		// ViewHelper.set
		((TeamActivityActivity) context).setTitleBarAlpha(t);
	}

	/**
	 * 
	 * �����Ļص��ӿ�
	 * 
	 * @author xiaanming
	 * 
	 */
	public interface OnScrollListener {
		/**
		 * �ص������� ����MyScrollView������Y�������
		 * 
		 * @param scrollY
		 *            ��
		 */
		public void onScroll(int scrollY);
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

}
