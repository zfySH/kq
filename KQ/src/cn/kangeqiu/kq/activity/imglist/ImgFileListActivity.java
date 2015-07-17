package cn.kangeqiu.kq.activity.imglist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.MainActivity;

import com.nowagme.football.CreateCommentActivity;

public class ImgFileListActivity extends BaseSimpleActivity implements
		OnItemClickListener {

	ListView listView;
	Util util;
	ImgFileListAdapter listAdapter;
	List<FileTraversal> locallist;

	ArrayList<String> listfile = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imgfilelist);
		listView = (ListView) findViewById(R.id.listView1);
		util = new Util(this);
		locallist = util.LocalImgFileList();
		List<HashMap<String, String>> listdata = new ArrayList<HashMap<String, String>>();
		Bitmap bitmap[] = null;
		if (locallist != null) {
			bitmap = new Bitmap[locallist.size()];
			for (int i = 0; i < locallist.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("filecount", locallist.get(i).filecontent.size() + "张");
				map.put("imgpath",
						locallist.get(i).filecontent.get(0) == null ? null
								: (locallist.get(i).filecontent.get(0)));
				map.put("filename", locallist.get(i).filename);
				listdata.add(map);
			}
		}
		listAdapter = new ImgFileListAdapter(this, listdata);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);

	}

	public void back(View view) {
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this, ImgsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable("data", locallist.get(arg2));
		intent.putExtras(bundle);
		startActivityForResult(intent, CreateCommentActivity.REQUEST_CODE_LOCAL);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
			case CreateCommentActivity.REQUEST_CODE_LOCAL:
				Bundle bundle = data.getExtras();

				if (bundle != null) {
					if (bundle.getStringArrayList("files") != null) {
						listfile = bundle.getStringArrayList("files");

						Intent intent = new Intent(this, MainActivity.class);
						Bundle fileBundle = new Bundle();
						bundle.putStringArrayList("files", listfile);
						intent.putExtras(bundle);
						setResult(1, intent);
						finish();
					}
				}
				break;

			}
		}
	}
}
