package cn.kangeqiu.kq.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;

import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.WebRequestUtilListener;

public class TeamMatchTypeActivity extends BaseSimpleActivity {
	private RadioGroup group;
	private TextView tx;
	// private RadioButton rb1, rb2, rb3, rb4, rb5;
	private String msg = "";
	private RadioGroup indicator;

	private List<RadioButton> rb_list = new ArrayList<RadioButton>();

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_team_fight_type);
		group = (RadioGroup) findViewById(R.id.indicator);
		tx = (TextView) findViewById(R.id.rb);
		indicator = (RadioGroup) findViewById(R.id.indicator);
		// rb1 = (RadioButton) findViewById(R.id.rb0);
		// rb2 = (RadioButton) findViewById(R.id.rb1);
		// rb3 = (RadioButton) findViewById(R.id.rb2);
		// rb4 = (RadioButton) findViewById(R.id.rb3);
		// rb5 = (RadioButton) findViewById(R.id.rb4);

		doPullDate("1025", new WebRequestUtilListener() {

			@Override
			public void onSucces(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				List<Map<String, String>> uType = (List<Map<String, String>>) data
						.get("u_type");
				for (int i = 0; i < uType.size(); i++) {
					Drawable drawable = TeamMatchTypeActivity.this
							.getResources().getDrawable(
									R.drawable.team_fight_type_selector);
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight());
					RadioButton tempButton = new RadioButton(
							TeamMatchTypeActivity.this);
					tempButton
							.setBackgroundResource(R.drawable.team_fight_type_selector);
					tempButton.setButtonDrawable(android.R.color.transparent);
					// 设置RadioButton的背景图片
					tempButton.setButtonDrawable(null);
					tempButton.setTextSize(12);
					tempButton.setPadding(0, 5, 0, 5);
					tempButton.setTextColor(R.color.text_color);
					tempButton.setText(String.valueOf(uType.get(i).get("name")));
					group.addView(tempButton);

					rb_list.add(tempButton);
				}
			}

			@Override
			public void onFail(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(TeamMatchTypeActivity.this,
						String.valueOf(data.get("message")), Toast.LENGTH_SHORT)
						.show();

			}

			@Override
			public void onError() {
				CPorgressDialog.hideProgressDialog();
			}
		});
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 在这个函数里面用来改变选择的radioButton的数值，以及与其值相关的 //任何操作，详见下文
				// tx.setText(group.getChildAt(index))
				RadioButton btn = (RadioButton) TeamMatchTypeActivity.this
						.findViewById(checkedId);
				if (btn.isChecked()) {
					tx.setVisibility(View.VISIBLE);
					tx.setText(btn.getText().toString());
					msg = btn.getText().toString();
				}
			}
		});

		tx.setClickable(true);
		tx.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				msg = "";
				tx.setVisibility(View.INVISIBLE);
				for (int i = 0; i < rb_list.size(); i++) {
					rb_list.get(i).setChecked(false);
				}
				// rb1.setChecked(false);
				// rb2.setChecked(false);
				// rb3.setChecked(false);
				// rb4.setChecked(false);
				// rb5.setChecked(false);

			}
		});

		((Button) findViewById(R.id.back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						TeamMatchTypeActivity.this.finish();
					}
				});
		((Button) findViewById(R.id.save))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						Intent data = new Intent();
						data.putExtra("msg", msg);
						// 请求代码可以自己设置，这里设置成20
						setResult(30, data);
						// 关闭掉这个Activity
						finish();
					}
				});
	}

	private void doPullDate(String action, WebRequestUtilListener listen) {
		CPorgressDialog.showProgressDialog(this);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		HttpPostUtil mHttpPostUtil = null;
		try {
			AppConfig.getInstance().addSign(parameters);
			mHttpPostUtil = AppConfig.getInstance()
					.makeHttpPostUtil(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// WEB request and deal the listener
		// new WebRequestUtil(mHttpPostUtil).execute(listen);
	}

}
