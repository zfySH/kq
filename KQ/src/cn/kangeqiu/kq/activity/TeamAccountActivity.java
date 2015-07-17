package cn.kangeqiu.kq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;

public class TeamAccountActivity extends BaseSimpleActivity {
	private Button back, save, pay, outpay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_team_account);
		back = (Button) findViewById(R.id.abc_fragment_person__btn_logout);
		save = (Button) findViewById(R.id.save);
		pay = (Button) findViewById(R.id.pay_btn);
		outpay = (Button) findViewById(R.id.outpay_btn);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				TeamAccountActivity.this.finish();
			}
		});

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();
				intent.setClass(TeamAccountActivity.this,
						TeamAccountManagerActivity.class);
				TeamAccountActivity.this.startActivity(intent);
			}
		});
		pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(TeamAccountActivity.this,
						TeamAccountPayActivity.class);
				TeamAccountActivity.this.startActivity(intent);
			}
		});
		outpay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(TeamAccountActivity.this,
						TeamAccountOutPayActivity.class);
				TeamAccountActivity.this.startActivity(intent);
			}
		});

	}
}
