package com.nowagme.football;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.kangeqiu.kq.R;

public class FragmentFinder extends Fragment {
	
	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["+ FragmentFinder.class.getName() + "]";
	

	@Override
	public void onAttach(Activity activity) {
		logi("onAttach(Activity activity)");
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		logi("onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)");
		View v = inflater.inflate(R.layout.abc_fragment_finder, null);
		return v;
	}
	
	/**
	  * log information
	  * @param msg
	  */
	 public static void logi(String msg){
		 Log.i(TAG, CLASS_NAME+" "+msg);
	 }
}
