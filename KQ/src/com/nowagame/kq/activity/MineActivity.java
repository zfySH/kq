package com.nowagame.kq.activity;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.FriendsActivity;

import com.easemob.applib.model.SerializableMap;
import com.easemob.applib.utils.HXPreferenceUtils;
import com.google.gson.Gson;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.medicine.app.fragments.MenuFragment;
import com.nowagme.football.AppConfig;
import com.nowagme.football.Constant;
import com.nowagme.football.EditfileActivity;
import com.nowagme.football.SeetingActivity;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class MineActivity extends Fragment implements OnClickListener {

	private int[] imageArray = new int[] { R.drawable.myhouse,
			R.drawable.mybrag, R.drawable.mybandicap, R.drawable.myintegral,
			R.drawable.myballcoin };
	private String[] imageNameArray = new String[] { "我的房间", "我的吹牛", "我的竞猜",
			"我的积分", "我的球币" };

	// private MineActivityAdapter adapter;
	private Context context;
	// private ListView detail_ListView;
	private ImageButton set_Btn;
	private RelativeLayout detail_Rel;
	private LinearLayout dynamic_Lin, frient_Lin, fans_Lin;
	private String userId = "";
	private JSONObject user = null;
	private TextView name_Text, txt_adder, txt_num1, txt_num2, txt_num3,
			room_count, brag_count, wager_count, score, money;
	private ImageView iv_person;
	private Button sexage;
	private ImagerLoader loader = new ImagerLoader();
	public static int intr = 0;
	public static final int REQUEST_CODE_GUANZHU = 1;
	public static final int REQUEST_CODE_FANS = 2;
	private MenuFragment menuFragment;
	RelativeLayout rel_myhouse, rel_mybrag, rel_mybandicap, rel_myintegral,
			rel_myballcoin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.abc_activity_mine, null);
		context = getActivity();
		init(v);

		intr = 1;
		// adapter = new MineActivityAdapter(getActivity(), imageArray,
		// imageNameArray);
		// detail_ListView.setAdapter(adapter);

		AddOnClickListener();
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	private void init(View v) {
		// TODO Auto-generated method stub
		set_Btn = (ImageButton) v.findViewById(R.id.img_setting);
		// detail_ListView = (ListView) v.findViewById(R.id.detail_ListView);
		detail_Rel = (RelativeLayout) v.findViewById(R.id.detail_Rel);
		dynamic_Lin = (LinearLayout) v.findViewById(R.id.rel_trends);
		frient_Lin = (LinearLayout) v.findViewById(R.id.rel_concern);
		fans_Lin = (LinearLayout) v.findViewById(R.id.rel_follower);
		name_Text = (TextView) v.findViewById(R.id.name_Text);
		txt_adder = (TextView) v.findViewById(R.id.txt_adder);
		iv_person = (CircleImageView) v
				.findViewById(R.id.abc_fragment_person__iv_person);
		sexage = (Button) v
				.findViewById(R.id.abc_fragment_nearby__listview__btn_sex_age);
		txt_num1 = (TextView) v.findViewById(R.id.txt_num1);
		txt_num2 = (TextView) v.findViewById(R.id.txt_num2);
		txt_num3 = (TextView) v.findViewById(R.id.txt_num3);
		room_count = (TextView) v.findViewById(R.id.room_count);
		brag_count = (TextView) v.findViewById(R.id.brag_count);
		wager_count = (TextView) v.findViewById(R.id.wager_count);
		score = (TextView) v.findViewById(R.id.score);
		money = (TextView) v.findViewById(R.id.money);
		rel_myhouse = (RelativeLayout) v.findViewById(R.id.rel_myhouse);
		rel_mybrag = (RelativeLayout) v.findViewById(R.id.rel_mybrag);
		rel_mybandicap = (RelativeLayout) v.findViewById(R.id.rel_mybandicap);
		rel_myintegral = (RelativeLayout) v.findViewById(R.id.rel_myintegral);
		rel_myballcoin = (RelativeLayout) v.findViewById(R.id.rel_myballcoin);
	}

	private void AddOnClickListener() {
		set_Btn.setOnClickListener(this);
		detail_Rel.setOnClickListener(this);
		dynamic_Lin.setOnClickListener(this);
		frient_Lin.setOnClickListener(this);
		fans_Lin.setOnClickListener(this);
		rel_myhouse.setOnClickListener(this);
		rel_mybrag.setOnClickListener(this);
		rel_mybandicap.setOnClickListener(this);
		rel_myintegral.setOnClickListener(this);
		rel_myballcoin.setOnClickListener(this);
		// detail_ListView.setOnItemClickListener(new OnItemClickListener());
	}

	/**
	 * 初始化数据.
	 */
	public void initData() {

		// 调取个人信息
		doPullDate(1, "2062", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						user = (JSONObject) resp.getJson()
								.getJSONObject("user");
						if (user != null) {
							Log.i("tag", "user++++++++" + user);
							userId = user.getString("id");
							name_Text.setText(user.getString("nickname"));
							loader.LoadImage(user.getString("icon"), iv_person);
							int sex = Integer.parseInt(user.getString("sex"));
							if (sex == 1) {
								sexage.setBackgroundResource(R.drawable.man);
							} else if (sex == 2) {
								sexage.setBackgroundResource(R.drawable.gril);
							}
							int sexResId = Constant.getResourceOfPlayerSex(sex);
							Drawable drawable = context.getResources()
									.getDrawable(sexResId);
							drawable.setBounds(0, 0,
									drawable.getMinimumWidth(),
									drawable.getMinimumHeight());
							sexage.setCompoundDrawables(drawable, null, null,
									null);
							// -－年龄
							sexage.setText(user.getString("age"));

							txt_adder.setText(((JSONObject) user
									.getJSONObject("province"))
									.getString("name")
									+ "  "
									+ ((JSONObject) user.getJSONObject("city"))
											.getString("name"));
							txt_num1.setText(user.getString("content_count"));
							txt_num2.setText(user.getString("attention_count"));
							txt_num3.setText(user.getString("fun_count"));
							// adapter.setItem(user);
							room_count.setText(user.getString("room_count"));
							brag_count.setText(user.getString("brag_count"));
							wager_count.setText(user.getString("wager_count"));
							score.setText(user.getString("score"));
							money.setText(user.getString("money"));

						}

					} else {
						Toast.makeText(context,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				Toast.makeText(getActivity(), resp.getErrorMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void doPullDate(int pageNo, String action, MCHttpCallBack listen) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("app_version", UpdataUtil
				.getAppVersion(context)));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		// pair.add(new BasicNameValuePair("u_user_id", AppConfig.getInstance()
		// .getPlayerId() + ""));
		// pair.add(new BasicNameValuePair("u_page", pageNo + ""));
		new WebRequestUtil().execute(true,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listen);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		if (v == set_Btn) {
			intent.setClass(this.getActivity(), SeetingActivity.class);
			this.getActivity().startActivityForResult(intent, 0);
		} else if (v == detail_Rel) {
			intent.setClass(this.getActivity(), EditfileActivity.class);
			// 传递数据
			// final SerializableMap myMap = new SerializableMap();

			Gson gson = new Gson();
			// 将返回的JSON数据转换为对象JsonRequestResult
			SerializableMap myMap = gson.fromJson(user.toString(),
					SerializableMap.class);
			// myMap.setMap(user);// 将map数据添加到封装的myMap中
			Bundle bundle = new Bundle();
			bundle.putSerializable("user", myMap);
			intent.putExtras(bundle);
			this.getActivity().startActivityForResult(intent, 0);
		} else if (v == dynamic_Lin) {
			intent.setClass(this.getActivity(), TrendsActivity.class);
			this.getActivity().startActivity(intent);
		} else if (v == frient_Lin) {
			try {
				HXPreferenceUtils.num = "关注";
				intent.setClass(this.getActivity(), FriendsActivity.class);
				intent.putExtra("userId", String.valueOf(user.getString("id")));
				this.getActivity().startActivityForResult(intent,
						REQUEST_CODE_GUANZHU);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (v == fans_Lin) {
			try {
				HXPreferenceUtils.num = "粉丝";
				intent.setClass(this.getActivity(), FriendsActivity.class);
				intent.putExtra("userId", String.valueOf(user.getString("id")));
				this.getActivity().startActivityForResult(intent,
						REQUEST_CODE_FANS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (v == rel_myhouse) {
			intent = new Intent(context, MyHouseActivity.class);
			startActivity(intent);
		} else if (v == rel_mybrag) {
			intent = new Intent(context, MyBragActivity.class);
			startActivity(intent);

		} else if (v == rel_mybandicap) {
			intent = new Intent(context, MyGuessActivity.class);
			startActivity(intent);
		} else if (v == rel_myintegral) {

			try {
				HXPreferenceUtils.num = "积分";
				intent.setClass(context, FriendsActivity.class);
				intent.putExtra("userId", String.valueOf(user.getString("id")));
				startActivity(intent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (v == rel_myballcoin) {
			intent = new Intent(context, MyBallActivity.class);
			startActivity(intent);
		}
	}

	class OnItemClickListener implements
			android.widget.AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int option,
				long arg3) {
			Log.v("tag", "点击了>>>>" + option);
			Intent intent = null;
			switch (option) {
			case 0:
				intent = new Intent(context, MyHouseActivity.class);
				break;
			case 1:
				intent = new Intent(context, MyBragActivity.class);
				break;
			case 2:
				intent = new Intent(context, MyGuessActivity.class);
				break;
			case 3:
				try {
					intent = new Intent();
					HXPreferenceUtils.num = "积分";
					intent.setClass(context, FriendsActivity.class);
					intent.putExtra("userId",
							String.valueOf(user.getString("id")));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 4:
				intent = new Intent(context, MyBallActivity.class);
				break;

			default:
				break;
			}
			startActivity(intent);
		}
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// // TODO Auto-generated method stub
	// super.onActivityResult(requestCode, resultCode, data);
	// if (resultCode == RESULT_OK) {
	//
	// }
	// }
}
