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
public class PopupWindowNearby extends PopupWindow {
    private View mMenuView;
    
    public PopupWindowNearby(Activity context,OnClickListener itemsOnClick) {  
        super(context);  
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        mMenuView = inflater.inflate(R.layout.abc_fragment_nearby_popwin, null);
        
        //设置按钮监听
        ((Button)mMenuView.findViewById(R.id.abc_fragment_nearby_popwin__btn_personal)).setOnClickListener(itemsOnClick);
        ((Button)mMenuView.findViewById(R.id.abc_fragment_nearby_popwin__btn_team)).setOnClickListener(itemsOnClick);
        ((Button)mMenuView.findViewById(R.id.abc_fragment_nearby_popwin__btn_court)).setOnClickListener(itemsOnClick);
        ((Button)mMenuView.findViewById(R.id.abc_fragment_nearby_popwin__btn_match)).setOnClickListener(itemsOnClick);
        ((Button)mMenuView.findViewById(R.id.abc_fragment_nearby_popwin__btn_all)).setOnClickListener(itemsOnClick);
        
        //设置SelectPicPopupWindow的View  
        this.setContentView(mMenuView);  
        //设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(LayoutParams.MATCH_PARENT);  
        //设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.WRAP_CONTENT);  
        //设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);  
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
    } 
    
    

}
