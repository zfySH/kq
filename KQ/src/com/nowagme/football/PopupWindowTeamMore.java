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
public class PopupWindowTeamMore extends PopupWindow {
	private View mMenuView;
	private Button abc_btn_private_letter, abc_btn_cancel_attention,
			abc_btn_exit_login, abc_btn_man, abc_btn_girl, btn_cancel,
			item_popupwindows_camera, item_popupwindows_Photo;
	private Button btn_activity, btn_edit, btn_manager, btn_match, btn_exit;

	public PopupWindowTeamMore(Context context, OnClickListener itemsOnClick,
			int relation) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.abc_fragment_team_popwin, null);

		// 设置按钮监听
		abc_btn_private_letter = ((Button) mMenuView
				.findViewById(R.id.abc_btn_private_letter));
		abc_btn_cancel_attention = ((Button) mMenuView
				.findViewById(R.id.abc_btn_cancel_attention));
		abc_btn_exit_login = ((Button) mMenuView
				.findViewById(R.id.abc_btn_exit_login));
		abc_btn_man = ((Button) mMenuView.findViewById(R.id.abc_btn_man));
		abc_btn_girl = ((Button) mMenuView.findViewById(R.id.abc_btn_girl));
		btn_cancel = (Button) mMenuView.findViewById(R.id.abc_btn_cancel);
		item_popupwindows_camera = ((Button) mMenuView
				.findViewById(R.id.item_popupwindows_camera));
		item_popupwindows_Photo = ((Button) mMenuView
				.findViewById(R.id.item_popupwindows_Photo));

		if (relation == 1) {// 我的关注
			abc_btn_private_letter.setVisibility(View.VISIBLE);
			abc_btn_cancel_attention.setVisibility(View.VISIBLE);
			abc_btn_exit_login.setVisibility(View.GONE);
			abc_btn_man.setVisibility(View.GONE);
			abc_btn_girl.setVisibility(View.GONE);
			item_popupwindows_camera.setVisibility(View.GONE);
			item_popupwindows_Photo.setVisibility(View.GONE);
		} else if (relation == 2) {// 取消关注
			abc_btn_private_letter.setVisibility(View.GONE);
			abc_btn_cancel_attention.setVisibility(View.VISIBLE);
			abc_btn_exit_login.setVisibility(View.GONE);
			abc_btn_man.setVisibility(View.GONE);
			abc_btn_girl.setVisibility(View.GONE);
			item_popupwindows_camera.setVisibility(View.GONE);
			item_popupwindows_Photo.setVisibility(View.GONE);
		} else if (relation == 0) {// 退出登录
			abc_btn_private_letter.setVisibility(View.GONE);
			abc_btn_cancel_attention.setVisibility(View.GONE);
			abc_btn_exit_login.setVisibility(View.VISIBLE);
			abc_btn_man.setVisibility(View.GONE);
			abc_btn_girl.setVisibility(View.GONE);
			item_popupwindows_camera.setVisibility(View.GONE);
			item_popupwindows_Photo.setVisibility(View.GONE);
		} else if (relation == 3) {// 性别
			abc_btn_private_letter.setVisibility(View.GONE);
			abc_btn_cancel_attention.setVisibility(View.GONE);
			abc_btn_exit_login.setVisibility(View.GONE);
			abc_btn_man.setVisibility(View.VISIBLE);
			abc_btn_girl.setVisibility(View.VISIBLE);
			item_popupwindows_camera.setVisibility(View.GONE);
			item_popupwindows_Photo.setVisibility(View.GONE);
		} else if (relation == 4) {// 头像
			abc_btn_private_letter.setVisibility(View.GONE);
			abc_btn_cancel_attention.setVisibility(View.GONE);
			abc_btn_exit_login.setVisibility(View.GONE);
			abc_btn_man.setVisibility(View.GONE);
			abc_btn_girl.setVisibility(View.GONE);
			item_popupwindows_camera.setVisibility(View.VISIBLE);
			item_popupwindows_Photo.setVisibility(View.VISIBLE);
		}
		item_popupwindows_camera.setOnClickListener(itemsOnClick);
		item_popupwindows_Photo.setOnClickListener(itemsOnClick);
		abc_btn_private_letter.setOnClickListener(itemsOnClick);
		abc_btn_cancel_attention.setOnClickListener(itemsOnClick);
		abc_btn_exit_login.setOnClickListener(itemsOnClick);
		abc_btn_man.setOnClickListener(itemsOnClick);
		abc_btn_girl.setOnClickListener(itemsOnClick);
		btn_cancel.setOnClickListener(itemsOnClick);
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

	public PopupWindowTeamMore(Activity context, OnClickListener itemsOnClick,
			int relation, int captainTeamId) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.abc_fragment_team_popwin, null);

		// 设置按钮监听
		btn_activity = ((Button) mMenuView.findViewById(R.id.abc_btn_activity));
		btn_edit = ((Button) mMenuView.findViewById(R.id.abc_btn_edit));
		btn_manager = ((Button) mMenuView.findViewById(R.id.abc_btn_manager));
		btn_match = ((Button) mMenuView.findViewById(R.id.abc_btn_match));
		btn_exit = ((Button) mMenuView.findViewById(R.id.abc_btn_exit));
		btn_cancel = (Button) mMenuView.findViewById(R.id.abc_btn_cancel);

		if (relation == 2) {
			btn_activity.setVisibility(View.VISIBLE);
			btn_edit.setVisibility(View.VISIBLE);
			btn_manager.setVisibility(View.VISIBLE);
			btn_match.setVisibility(View.GONE);
			btn_exit.setVisibility(View.GONE);
		} else if (relation == 1) {
			btn_activity.setVisibility(View.GONE);
			btn_edit.setVisibility(View.GONE);
			btn_manager.setVisibility(View.GONE);
			if (captainTeamId > 0) {
				btn_match.setVisibility(View.VISIBLE);
				btn_exit.setVisibility(View.VISIBLE);
			} else if (captainTeamId == 0) {
				btn_match.setVisibility(View.GONE);
				btn_exit.setVisibility(View.VISIBLE);
			}
		} else if (relation == 0) {
			if (captainTeamId > 0) {
				btn_activity.setVisibility(View.GONE);
				btn_edit.setVisibility(View.GONE);
				btn_manager.setVisibility(View.GONE);
				btn_match.setVisibility(View.VISIBLE);
				btn_exit.setVisibility(View.GONE);
			}
		}

		btn_activity.setOnClickListener(itemsOnClick);
		btn_edit.setOnClickListener(itemsOnClick);
		btn_manager.setOnClickListener(itemsOnClick);
		btn_match.setOnClickListener(itemsOnClick);
		btn_exit.setOnClickListener(itemsOnClick);
		btn_cancel.setOnClickListener(itemsOnClick);
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
