package cn.kangeqiu.kq.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.adapter.TeamLogosAdapter;

import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.WebRequestUtilListener;

public class TeamCreatImgActivity extends BaseSimpleActivity {
	private GridView grid;
	private TeamLogosAdapter adapter;
	private List<String> photoUrls = new ArrayList<String>();
	private List<Map<String, String>> icons = new ArrayList<Map<String, String>>();
	private Button back, save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_create_team_logo);
		initView();
		initData();
	}

	private void initView() {
		grid = (GridView) findViewById(R.id.team_default_grid);
		back = (Button) findViewById(R.id.back);
		save = (Button) findViewById(R.id.save);

		adapter = new TeamLogosAdapter(this);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				adapter.chiceState(position);
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int position = adapter.getChoiced();
				if (position < 0) {
					Toast.makeText(TeamCreatImgActivity.this, "请选择队徽",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					String iconId = icons.get(position).get("id");
					String iconUrl = photoUrls.get(position);
					
					Intent data = new Intent();
					data.putExtra("id", iconId);
					data.putExtra("url", iconUrl);
					// 请求代码可以自己设置，这里设置成20
					setResult(50, data);
					// 关闭掉这个Activity
					finish();
				}
			}
		});
	}

	private void initData() {
		doPullDate("1062", new WebRequestUtilListener() {

			@Override
			public void onSucces(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				icons = (List<Map<String, String>>) data.get("icons");
				for (int i = 0; i < icons.size(); i++) {
					photoUrls.add(icons.get(i).get("file"));
				}
				adapter.setItem(photoUrls);
			}

			@Override
			public void onFail(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(TeamCreatImgActivity.this,
						String.valueOf(data.get("message")), Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onError() {
				CPorgressDialog.hideProgressDialog();

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
//		new WebRequestUtil(mHttpPostUtil).execute(listen);
	}

}
