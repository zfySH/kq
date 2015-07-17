package cn.kangeqiu.kq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;

public class TeamAccountPayActivity extends BaseSimpleActivity {
	private Button back, save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_team_account_pay);
		back = (Button) findViewById(R.id.abc_fragment_person__btn_logout);
		save = (Button) findViewById(R.id.save);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				TeamAccountPayActivity.this.finish();
			}
		});

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();
				intent.setClass(TeamAccountPayActivity.this,
						TeamScoreManagerActivity.class);
				TeamAccountPayActivity.this.startActivity(intent);
			}
		});

	}
}
