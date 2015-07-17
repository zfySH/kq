package com.nowagme.football;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import cn.kangeqiu.kq.R;

@SuppressLint("ViewConstructor")
public class PopupWindowActivityMore extends PopupWindow {
	private View mMenuView;
	private Button btn_activity_edit, btn_activity_cancel, btn_cancel,btn_cancel_bao;

	public PopupWindowActivityMore(Activity context,
			OnClickListener itemsOnClick, int attribute) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.abc_fragment_activity_popwin,
				null);

		// 设置按钮监听
		btn_activity_edit = ((Button) mMenuView
				.findViewById(R.id.abc_btn_activity_edit));
		btn_activity_cancel = ((Button) mMenuView
				.findViewById(R.id.abc_btn_activity_cancel));
		btn_cancel = (Button) mMenuView.findViewById(R.id.abc_btn_cancel);
		btn_cancel_bao= (Button) mMenuView.findViewById(R.id.abc_btn_activity_cancel_baoming);

		if ((attribute & 4) == 4){
			btn_activity_edit.setVisibility(View.VISIBLE);
		}else{
			btn_activity_edit.setVisibility(View.GONE);
			
		}
		if ((attribute & 8) == 8){
			btn_activity_cancel.setVisibility(View.VISIBLE);
		}else{
			btn_activity_cancel.setVisibility(View.GONE);
		}
		if ((attribute & 2) == 2){
			btn_cancel_bao.setVisibility(View.VISIBLE);
		}else{
			btn_cancel_bao.setVisibility(View.GONE);
		}
		
		btn_activity_edit.setOnClickListener(itemsOnClick);
		btn_activity_cancel.setOnClickListener(itemsOnClick);
		btn_cancel.setOnClickListener(itemsOnClick);
		btn_cancel_bao.setOnClickListener(itemsOnClick);
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new ColorDrawable(
				android.graphics.Color.TRANSPARENT));
		// this.showAtLocation(mMenuView, Gravity.RIGHT, 0, 0);
	}
}
