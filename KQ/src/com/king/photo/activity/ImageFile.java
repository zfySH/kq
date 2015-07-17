package com.king.photo.activity;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import cn.kangeqiu.kq.AgentActivity;

import com.king.photo.adapter.FolderAdapter;
import com.king.photo.util.Bimp;
import com.king.photo.util.PublicWay;
import com.king.photo.util.Res;

/**
 * 这个类主要是用来进行显示包含图片的文件夹
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:48:06
 */
public class ImageFile extends AgentActivity {

	private FolderAdapter folderAdapter;
	private Button bt_cancel;
	private Context mContext;
	public static String match_id;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(Res.getLayoutID("plugin_camera_image_file"));
		PublicWay.activityList.add(this);
		 match_id = getIntent().getStringExtra("matchId");
		mContext = this;
		bt_cancel = (Button) findViewById(Res.getWidgetID("cancel"));
		bt_cancel.setOnClickListener(new CancelListener());
		GridView gridView = (GridView) findViewById(Res.getWidgetID("fileGridView"));
		TextView textView = (TextView) findViewById(Res.getWidgetID("headerTitle"));
		textView.setText(Res.getString("photo"));
		folderAdapter = new FolderAdapter(this);
		gridView.setAdapter(folderAdapter);
	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		public void onClick(View v) {	
			//清空选择的图片
			Bimp.tempSelectBitmap.clear();
//			Intent intent = new Intent();
//			intent.setClass(mContext, MainActivity.class);
//			intent.putExtra("matchId", match_id);
//			startActivity(intent);
			finish();
			
		}
	}

//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
////			Intent intent = new Intent();
////			intent.setClass(mContext, MainActivity.class);
////			intent.putExtra("matchId", match_id);
////			startActivity(intent);
//			finish();
//		}
//		
//		return true;
//	}

}
