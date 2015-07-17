package com.nowagme.football;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;

public class SeekActivity extends BaseSimpleActivity {
	
	private ImageButton img_seek;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seek1);
		initView();
	}
	
	public void initView(){
		
		img_seek=(ImageButton) findViewById(R.id.img_seek);
		img_seek.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
	}
	
	/**
	 * 后退
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}

}
