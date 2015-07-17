package com.nowagme.football;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;

public class TeamActivityDetailsActivity extends BaseSimpleActivity {
//	private Button back;

	private String subject, start_time, end_time, address, remark, team_name1,
			team_name2;
	private TextView subject_tx, start_time_tx, end_time_tx, address_tx,
			remark_tx;
	private int kind;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_team_activity_detail);

		subject = getIntent().getStringExtra("subject");
		start_time = getIntent().getStringExtra("start_time");
		end_time = getIntent().getStringExtra("end_time");
		address = getIntent().getStringExtra("address");
		remark = getIntent().getStringExtra("remark");

		team_name1 = getIntent().getStringExtra("team_name1");
		team_name2 = getIntent().getStringExtra("team_name2");
		kind = getIntent().getIntExtra("kind", -1);
		initView();
		initHandle();
	}
	
	/**
	 * 后退
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}

	private void initHandle() {

		if (kind == 2 || kind == 20)
			subject_tx.setText(subject);
		else if (kind == 0 || kind == 1) {
			if (team_name2 != null && !team_name2.equals("")) {
				subject_tx.setText(team_name1 + " VS " + team_name2);
			} else {
				subject_tx.setText(team_name1 + " VS ?");
			}
		}

		start_time_tx.setText(start_time);
		end_time_tx.setText(end_time);
		address_tx.setText(address);
		remark_tx.setText(remark);

	}

	private void initView() {
		subject_tx = (TextView) findViewById(R.id.subject);
		start_time_tx = (TextView) findViewById(R.id.abc_start_time);
		end_time_tx = (TextView) findViewById(R.id.abc_end_time);
		address_tx = (TextView) findViewById(R.id.match_address);
		remark_tx = (TextView) findViewById(R.id.instruction);

	}
}
