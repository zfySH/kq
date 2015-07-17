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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.adapter.PlaceChooseComponentsAdapter;
import cn.kangeqiu.kq.adapter.PlaceComponentsAdapter;

import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.WebRequestUtilListener;

public class CreateTeamTagActivity extends BaseSimpleActivity {
	private RadioGroup group;
	private TextView tx;
	private RadioButton rb1, rb2, rb3, rb4, rb5;
	private String msg = "";

	private GridView grid1, grid2;
	private PlaceComponentsAdapter adapter;
	private PlaceChooseComponentsAdapter adapterChoose;
	private List<String> list;
	private List<String> deleteList;
	private List<String> listChoose;

	private Map<String, Integer> positionMap;

	private int index = 0;

	private Button back, save;

	private EditText desc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_team_create_tags);
		list = new ArrayList<String>();
		listChoose = new ArrayList<String>();
		adapter = new PlaceComponentsAdapter(this);
		adapterChoose = new PlaceChooseComponentsAdapter(this, 1);
		positionMap = new HashMap<String, Integer>();
		deleteList = new ArrayList<String>();

		grid1 = (GridView) findViewById(R.id.grid1);
		grid2 = (GridView) findViewById(R.id.grid2);
		back = (Button) findViewById(R.id.back);
		save = (Button) findViewById(R.id.save);
		desc = (EditText) findViewById(R.id.component_desc);

		grid1.setAdapter(adapter);
		grid2.setAdapter(adapterChoose);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String msg = adapter.getData();
				if (msg == null || msg.equals("")) {
					Toast.makeText(CreateTeamTagActivity.this, "请选择标签",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String compont_desc = desc.getText().toString();
				if (compont_desc == null || compont_desc.equals("")) {
					Toast.makeText(CreateTeamTagActivity.this, "请输入球队描述",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Intent data = new Intent();
				data.putExtra("msg", msg);
				data.putExtra("desc", compont_desc);
				// 请求代码可以自己设置，这里设置成20
				setResult(30, data);
				// 关闭掉这个Activity
				finish();
			}
		});
		grid1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				deleteList.clear();
				deleteList.add(list.get(position));
				list.remove(position);
				adapter.notifyDataSetChanged();
				adapterChoose.chiceStateByChange(deleteList);
				// adapter.chiceState(position);
			}
		});
		grid2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				adapterChoose.chiceState(position, adapter);
			}
		});
		if (msg != null && !msg.equals("")) {
			for (int i = 0; i < msg.split(",").length; i++) {
				list.add(msg.split(",")[i]);
			}
		}
		adapter.setItems(list);
		adapter.notifyDataSetChanged();
		doPullDate("1019", new WebRequestUtilListener() {

			@Override
			public void onSucces(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				List<Map<String, String>> placelist = (List<Map<String, String>>) data
						.get("components");
				for (int i = 0; i < placelist.size(); i++) {
					listChoose.add(placelist.get(i).get("name"));

				}
				adapterChoose.setItems(listChoose);

				adapterChoose.chiceStateByChange(list);
				adapterChoose.notifyDataSetChanged();
			}

			@Override
			public void onFail(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(CreateTeamTagActivity.this,
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
		// new WebRequestUtil(mHttpPostUtil).execute(listen);
	}
}
