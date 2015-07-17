package com.nowagme.football;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.imglist.ImgFileListActivity;
import cn.kangeqiu.kq.activity.imglist.UploadUtil;
import cn.kangeqiu.kq.adapter.ExpressionAdapter;
import cn.kangeqiu.kq.adapter.ExpressionPagerAdapter;
import cn.kangeqiu.kq.adapter.MatchPhotoAdapter;
import cn.kangeqiu.kq.utils.SmileUtils;
import cn.kangeqiu.kq.widget.ExpandGridView;
import cn.kangeqiu.kq.widget.PasteEditText;

import com.king.photo.activity.AlbumActivity;
import com.nowagme.util.Bimp;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.FileUtils;

public class CreateCommentActivity extends BaseSimpleActivity {
	private static final int TAKE_PICTURE = 0x000001;
	public static final int REQUEST_CODE_LOCAL = 19;

	private Bitmap bm = null;
	private static String picPath = "";
	private String fName = null;
	private ArrayList<String> listfile = new ArrayList<String>();

	private GridView photoGridview;
	private MatchPhotoAdapter adapter;
	private String match_id;
	public Button upload_photo, camera;
	private PasteEditText content;
	private Button iv_emoticons_checked;
	private InputMethodManager manager;
	private LinearLayout emojiIconContainer;
	private View more;
	private ViewPager expressionViewpager;
	private List<String> reslist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_match_create_comment_activity);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initHandle();
		initView();
	}

	private void initHandle() {
		match_id = getIntent().getStringExtra("matchId");
	}

	private void initView() {
		upload_photo = (Button) findViewById(R.id.upload_photo);
		upload_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CreateCommentActivity.this,
						AlbumActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_translate_in,
						R.anim.activity_translate_out);

			}
		});
		more = findViewById(R.id.more);
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		iv_emoticons_checked = (Button) findViewById(R.id.iv_emoticons_checked);
		photoGridview = (GridView) findViewById(R.id.photo_gridview);
		content = (PasteEditText) findViewById(R.id.content);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		// 表情list
		reslist = getExpressionRes(35);
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		// iv_emoticons_checked = (ImageView)
		// findViewById(R.id.iv_emoticons_checked);

		adapter = new MatchPhotoAdapter(this);
		photoGridview.setAdapter(adapter);

	}

	public void EmoticonsChecked(View view) {
		more.setVisibility(View.VISIBLE);
		emojiIconContainer.setVisibility(View.VISIBLE);
		hideKeyboard();
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}

	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this,
				1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					// if (buttonSetModeKeyboard.getVisibility() !=
					// View.VISIBLE) {

					if (filename != "delete_expression") { // 不是删除键，显示表情
						// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
						Class clz = Class
								.forName("cn.kangeqiu.kq.utils.SmileUtils");
						Field field = clz.getField(filename);
						content.append(SmileUtils.getSmiledText(
								CreateCommentActivity.this,
								(String) field.get(null)));
					} else { // 删除文字或者表情
						if (!TextUtils.isEmpty(content.getText())) {

							int selectionStart = content.getSelectionStart();// 获取光标的位置
							if (selectionStart > 0) {
								String body = content.getText().toString();
								String tempStr = body.substring(0,
										selectionStart);
								int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
								if (i != -1) {
									CharSequence cs = tempStr.substring(i,
											selectionStart);
									if (SmileUtils.containsKey(cs.toString()))
										content.getEditableText().delete(i,
												selectionStart);
									else
										content.getEditableText().delete(
												selectionStart - 1,
												selectionStart);
								} else {
									content.getEditableText().delete(
											selectionStart - 1, selectionStart);
								}
							}
						}

					}
					// }
				} catch (Exception e) {
				}

			}
		});
		return view;
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	public void back(View view) {
		finish();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

				String fileName = String.valueOf(System.currentTimeMillis());
				bm = (Bitmap) data.getExtras().get("data");
				FileUtils.saveBitmap(bm, fileName);

				picPath = Environment.getExternalStorageDirectory()
						+ "/Photo_She/" + fileName + ".JPEG";
				// f = new File(Environment.getExternalStorageDirectory()
				// + "/Photo_She/", fileName + ".JPEG");
				fName = fileName + ".JPEG";

				// bmpUpload(fName, picPath);
			}
			break;

		case REQUEST_CODE_LOCAL:
			if (data != null) {
				Bundle bundle = data.getExtras();

				if (bundle != null) {
					if (bundle.getStringArrayList("files") != null) {
						listfile = bundle.getStringArrayList("files");
						adapter.setItem(listfile);
					}
				}
			}
			break;
		}
	}

	public void PickPhoto(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ImgFileListActivity.class);
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	public void Photo(View view) {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	public void Upload(View view) {
		if (content.getText().toString() == null
				|| content.getText().toString().equals("")) {
			Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
			return;
		}
		uploadFileWithTask();
	}

	/**
	 * 上传文件.
	 */
	public void uploadFileWithTask() {
		CPorgressDialog.showProgressDialog("正在发布...", this);
		// 开始上传
		UploadUtil uploadUtil = getUploadUtil();
		new UploadTask().execute(uploadUtil);

	}

	public UploadUtil getUploadUtil() {

		// String appSecret = "de3jkfg74slftG$*&a_@";
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", "2002");
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		parameters.put("u_type", "0");
		parameters.put("u_record_id", match_id);
		parameters.put("u_body", content.getText().toString());

		for (int i = 0; i < listfile.size(); i++) {
			String fileKey = "u_file" + (i + 1);
			String fileName = listfile.get(i).substring(
					listfile.get(i).lastIndexOf("/") + 1);
			parameters.put(fileKey, fileName);
		}
		// parameters.put(imageParamName, imageParamValue);
		// parameters.put(imageParamName2, imageParamValue2);
		// String sign = "";
		try {
			AppConfig.getInstance().addSign(parameters);
			// sign = StringUtil.makeSign(appSecret, hm, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// hm.put("app_sign", sign);

		for (int i = 0; i < listfile.size(); i++) {
			String fileKey = "u_file" + (i + 1);
			parameters.remove(fileKey);
		}
		// hm.remove(imageParamName);
		// hm.remove(imageParamName2);

		Map<String, String> files = new HashMap<String, String>();
		for (int i = 0; i < listfile.size(); i++) {
			String fileKey = "u_file" + (i + 1);
			files.put(fileKey, listfile.get(i));
		}
		return new UploadUtil(AppConfig.host + AppConfig.urlMSDK,
				Charset.forName("UTF-8"), parameters, files);
	}

	class UploadTask extends
			AsyncTask<UploadUtil, Integer, Map<String, Object>> {

		@Override
		protected void onProgressUpdate(Integer... values) {

		}

		@Override
		protected void onPostExecute(Map<String, Object> data) {
			CPorgressDialog.hideProgressDialog();
			String resultCode = (String) data.get("result_code");
			String message = (String) data.get("message");
			String jsonData = "JSON:";
			if ("0".equals(resultCode)) {
				try {
					Toast.makeText(CreateCommentActivity.this, "发布成功",
							Toast.LENGTH_SHORT).show();
					// Map<String, Object> mData = JsonUtil.parse(message);
					// jsonData += "result_code=" + data.get("result_code") +
					// ",";
					// List<Map<String, Object>> photos = (List<Map<String,
					// Object>>) data
					// .get("photos");
					// for (Map<String, Object> m : photos) {
					// for (String key : m.keySet()) {
					// jsonData += "photos." + key + "=" + m.get(key)
					// + ",";
					// }
					// }
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(CreateCommentActivity.this, "发布失败",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected Map<String, Object> doInBackground(UploadUtil... args) {
			Map<String, Object> data = new HashMap<String, Object>();
			UploadUtil uploadUtil = args[0];
			String message = null;
			try {
				boolean isOK = uploadUtil.upload();
				if (isOK) {
					message = uploadUtil.getResponseText();
					data.put("result_code", "0");
				} else {
					message = "上传失败.";
					data.put("result_code", "-1");
				}
			} catch (Exception e) {
				e.printStackTrace();
				message = "上传失败:" + e.getMessage();
				data.put("result_code", "-2");
			}
			data.put("message", message);
			return data;
		}

	}
}
