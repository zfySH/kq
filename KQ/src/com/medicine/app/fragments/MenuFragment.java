package com.medicine.app.fragments;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.InvitationcodeActivity;
import cn.kangeqiu.kq.activity.MainActivity;

import com.easemob.applib.model.SerializableMap;
import com.google.gson.Gson;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagame.kq.activity.CreatMyGuessActivity;
import com.nowagame.kq.activity.MyActivitys;
import com.nowagame.kq.activity.MyAddfrendsActivity;
import com.nowagame.kq.activity.MypaihangActivity;
import com.nowagme.football.AppConfig;
import com.nowagme.football.EditfileActivity;
import com.nowagme.football.SeetingActivity;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.WebRequestUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuFragment extends Fragment implements OnClickListener {
	Fragment mFragment = null;
	private RelativeLayout drawer_1, drawer_2, drawer_3, drawer_4, drawer_5,
			drawer_6, menu, drawer_7;
	private CircleImageView abc_faceimg;
	private TextView tet_name;
	private LinearLayout lay_setting, lin_register;
	ImagerLoader loader = new ImagerLoader();
	private JSONObject user = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.menu_frame_one, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		menu = (RelativeLayout) view.findViewById(R.id.menu);
		menu.invalidate();
		drawer_7 = (RelativeLayout) view.findViewById(R.id.drawer_7);
		drawer_2 = (RelativeLayout) view.findViewById(R.id.drawer_2);
		// drawer_3 = (RelativeLayout) view.findViewById(R.id.drawer_3);
		// drawer_4 = (RelativeLayout) view.findViewById(R.id.drawer_4);
		drawer_5 = (RelativeLayout) view.findViewById(R.id.drawer_5);
		drawer_6 = (RelativeLayout) view.findViewById(R.id.drawer_6);
		abc_faceimg = (CircleImageView) view.findViewById(R.id.abc_faceimg);
		tet_name = (TextView) view.findViewById(R.id.tet_name);
		lay_setting = (LinearLayout) view.findViewById(R.id.lay_setting);
		lin_register = (LinearLayout) view.findViewById(R.id.lin_register);
		drawer_7.setOnClickListener(this);
		drawer_2.setOnClickListener(this);
		// drawer_3.setOnClickListener(this);
		// drawer_4.setOnClickListener(this);
		drawer_5.setOnClickListener(this);
		drawer_6.setOnClickListener(this);
		lay_setting.setOnClickListener(this);
		lin_register.setOnClickListener(this);
		inidate();
	}

	public void inidate() {
		doPullDate(0, "2030", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					user = resp.getJson();
					loader.LoadImage(resp.getJson().getString("icon"),
							abc_faceimg);
					tet_name.setText(resp.getJson().getString("nickname"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				CPorgressDialog.hideProgressDialog();
			}
		});

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.drawer_7:
			intent.setClass(this.getActivity(), MyAddfrendsActivity.class);
			this.startActivity(intent);
			break;
		case R.id.drawer_2:
			intent.setClass(this.getActivity(), MyActivitys.class);
			this.startActivity(intent);
			break;
		// case R.id.drawer_3:
		// break;
		// case R.id.drawer_4:
		// break;
		case R.id.drawer_5:
			intent.setClass(this.getActivity(), InvitationcodeActivity.class);
			this.startActivity(intent);
			break;
		case R.id.drawer_6:
			MobclickAgent.onEvent(getActivity(), "data_paihang");
			TCAgent.onEvent(getActivity(), "data_paihang");

			intent.setClass(this.getActivity(), MypaihangActivity.class);
			this.startActivity(intent);
			break;
		case R.id.lin_register:
			intent.setClass(getActivity(), EditfileActivity.class);
			Gson gson = new Gson();
			// 将返回的JSON数据转换为对象JsonRequestResult
			SerializableMap myMap = gson.fromJson(user.toString(),
					SerializableMap.class);
			// myMap.setMap(user);// 将map数据添加到封装的myMap中
			Bundle bundle = new Bundle();
			bundle.putSerializable("user", myMap);
			intent.putExtras(bundle);
			this.getActivity().startActivityForResult(intent, 1);
			break;
		case R.id.lay_setting:
			intent.setClass(this.getActivity(), SeetingActivity.class);
			this.getActivity().startActivityForResult(intent, 0);
			break;
		}

		if (mFragment != null)
			switchFragment(mFragment);
	}

	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		if (getActivity() instanceof MainActivity) {
			MainActivity fca = (MainActivity) getActivity();
			fca.switchContent(fragment);
		}
	}

	private long exitTime = 0;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getActivity(), "�ٰ�һ�η�������",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
				Vibrator vibrator = (Vibrator) getActivity().getSystemService(
						Context.VIBRATOR_SERVICE);
				long[] pattern = { 100, 200, 100, 200 };
				vibrator.vibrate(pattern, -1);
			} else {
				// ExitActivity.getInstance().exit();
			}
			return true;
		}
		return true;
	}

	private void doPullDate(int pageNo, String action, MCHttpCallBack listen) {
		CPorgressDialog.showProgressDialog(getActivity());

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		boolean isLazyLoad = false;
		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);

	}

}
