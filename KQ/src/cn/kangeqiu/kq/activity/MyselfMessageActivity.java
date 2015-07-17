package cn.kangeqiu.kq.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.adapter.MyselfMessageAdapter;

import com.easemob.applib.model.MessageItemModel;
import com.nowagme.football.AppConfig;
import com.nowagme.football.DownAndShowImageTask;
import com.nowagme.util.Bimp;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.FileUtils;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.JsonUtil;
import com.nowagme.util.UploadUtil;
import com.nowagme.util.UploadUtil.OnUploadProcessListener;
import com.nowagme.util.WebCallResultUtil;

public class MyselfMessageActivity extends BaseSimpleActivity {
	private ListView list;
	private MyselfMessageAdapter adapter;
	private LayoutInflater inflater;
	private List<MessageItemModel> messageItem = new ArrayList<MessageItemModel>();

	// private Button back;
	private String content = "";
	private static final String TAG = "demoTAG";
	private static final String CLASS_NAME = "["
			+ MyselfMessageActivity.class.getName() + "]";

	private View headView;
	private ImageView iv;

	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	public static final int REQUEST_CODE_LOCAL = 19;
	private View parentView;
	private static final int TAKE_PICTURE = 0x000001;

	private Bitmap bm = null;
	private static String picPath = "";
	private String fName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abc_activity_myself_message);

		initView();
		initHandle();
		initDate();
	}

	public void back(View view) {
		finish();
	}

	private void initView() {
		pop = new PopupWindow(MyselfMessageActivity.this);
		View view = getLayoutInflater().inflate(R.layout.abc_item_popupwindows,
				null);
		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);
		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);

		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				photo();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent;
				if (Build.VERSION.SDK_INT < 19) {
					intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");

				} else {
					intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				}
				startActivityForResult(intent, REQUEST_CODE_LOCAL);

				// overridePendingTransition(R.anim.activity_translate_in,
				// R.anim.activity_translate_out);
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});

		adapter = new MyselfMessageAdapter(this);
		inflater = getLayoutInflater();
		// back = (Button) findViewById(R.id.abc_fragment_person__btn_logout);
		list = (ListView) findViewById(R.id.message_list);
		parentView = (View) findViewById(R.id.main);
		// iv = (ImageView) findViewById(R.id.abc_fragment_person__iv_person);
	}

	private void initHandle() {
		headView = inflater.inflate(R.layout.abc_myself_message_top, null);
		list.addHeaderView(headView);
		list.setAdapter(adapter);
		iv = (ImageView) headView
				.findViewById(R.id.abc_fragment_person__iv_person);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				logi("onItemClick:position=" + position + ",id=" + id);

				if (position == 0) {
					ll_popup.startAnimation(AnimationUtils.loadAnimation(
							MyselfMessageActivity.this,
							R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
				} else {
					if (messageItem.get(position - 1).isShow()) {
						Intent intent = new Intent();
						intent.setClass(MyselfMessageActivity.this,
								MyselfMessageDetailActivity.class);
						intent.putExtra("title", messageItem.get(position - 1)
								.getName());
						intent.putExtra("type", messageItem.get(position - 1)
								.getType());
						intent.putExtra("content",
								view.getTag(R.id.MyselfMessage).toString());
						MyselfMessageActivity.this.startActivityForResult(
								intent, 100);
					}
				}
			}

		});
	}

	private void initDate() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", "1014");
		parameters.put("app_platform", "0");
		parameters.put("u_uid", "" + AppConfig.getInstance().getPlayerId());
		try {
			AppConfig.getInstance().addSign(parameters);
			new postTask().execute(AppConfig.getInstance().makeHttpPostUtil(
					parameters));
		} catch (Exception e) {
			e.printStackTrace();
			doPensonErr();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (20 == resultCode) {
			String msg = data.getExtras().getString("msg");
			String action = data.getExtras().getString("action");

			if (action.equals("1056")) {// 姓名
				// messageItem.get(0).setContent(msg);
			} else if (action.equals("1042")) {// 球龄
				// messageItem.get(4).setContent(msg);

			} else if (action.equals("1008")) {// 场上位置
				messageItem.get(3).setContent(msg);
			} else if (action.equals("1044")) {// 个人标签
				messageItem.get(5).setContent(msg);
			} else if (action.equals("1057")) {// 性别
				// String sex = "";
				// if (msg.equals("1"))
				// sex = "男";
				// else if (msg.equals("2"))
				// sex = "女";
				// messageItem.get(7).setContent(sex);
			} else if (action.equals("1012")) {// 生日
				messageItem.get(2).setContent(msg);
			} else if (action.equals("1047")) {// 个人签名
				messageItem.get(6).setContent(msg);

			} else if (action.equals("1046")) {// 工作行业
				messageItem.get(4).setContent(msg);

			} else if (action.equals("1013")) {// 居住地 籍贯
				int type = data.getExtras().getInt("type");
				if (type == 0) {// 籍贯
					messageItem.get(1).setContent(msg);
				} else if (type == 1) {// 居住地
					messageItem.get(0).setContent(msg);
				}
			}
			// adapter.setItem(messageItem);
		}

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

				bmpUpload(fName, picPath);
			}
			break;

		case REQUEST_CODE_LOCAL:
			if (data != null) {
				Uri selectedImage = data.getData();
				if (selectedImage != null) {
					sendPicByUri(selectedImage);
				}
			}
			break;
		}
	}

	/**
	 * 根据图库图片uri发送图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage, null, null,
				null, null);
		String st8 = getResources().getString(R.string.cant_find_pictures);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			bmpUpload(picturePath.substring(picturePath.lastIndexOf("/") + 1),
					picturePath);
			// sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			bmpUpload(
					file.getAbsolutePath().substring(
							file.getAbsolutePath().lastIndexOf("/") + 1),
					file.getAbsolutePath());

			// sendPicture(file.getAbsolutePath());
		}

	}

	private void bmpUpload(String fName, String picPath) {
		CPorgressDialog.showProgressDialog("正在提交照片", this);
		// 文件提交 只支持单文件
		String fileKey = "u_file1";
		UploadUtil uploadUtil = UploadUtil.getInstance();
		uploadUtil.setOnUploadProcessListener(new OnUploadProcessListener() {

			@Override
			public void onUploadProcess(int uploadSize) {

			}

			@Override
			public void onUploadDone(int responseCode, String message) {
				CPorgressDialog.hideProgressDialog();

				// TODO Auto-generated method stub
				initDate();
			}

			@Override
			public void initUpload(int fileSize) {

			}
		}); // 设置监听器监听上传状态

		Map<String, String> params = new HashMap<String, String>();
		params.put("app_action", "1006");
		params.put("app_platform", "0");
		params.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		params.put("u_file1", fName);
		try {
			AppConfig.getInstance().addSign(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String requestURL = AppConfig.host + AppConfig.urlMSDK;

		uploadUtil.uploadFile(picPath, fileKey, requestURL, params);
	}

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	/**
	 * 调取个人信息成功.
	 */
	private void doPensonSuccess(Map<String, Object> data) {
		logi("doPensonSuccess()");

		String url = String.valueOf(data.get("icon"));

		new DownAndShowImageTask(url, iv).execute();

		// messageItem.add(new MessageItemModel("text", "名字",
		// data.get("nickname")
		// .toString()));
		// messageItem.add(new MessageItemModel("", "用户ID", data.get("id")
		// .toString(), false));
		// messageItem.add(new MessageItemModel("", "球员号码", "", false));
		// messageItem.add(new MessageItemModel("img", "我的二维码", "",
		// R.drawable.abc_fragment_person__iv_person_qr, true));

		// messageItem.add(new MessageItemModel("ballage", "球龄", data.get(
		// "veteran").toString()));
		messageItem.add(new MessageItemModel("livecity", "居住地", data.get(
				"live_country").toString()
				+ data.get("live_province").toString()
				+ data.get("live_city").toString()));
		messageItem.add(new MessageItemModel("nativecity", "籍贯", data.get(
				"native_country").toString()
				+ data.get("native_province").toString()
				+ data.get("native_city").toString()));
		messageItem.add(new MessageItemModel("date", "生日", data.get("birth")
				.toString()));
		messageItem.add(new MessageItemModel("place", "场上位置", data.get("place")
				.toString()));
		messageItem.add(new MessageItemModel("work", "工作行业", data.get(
				"industry").toString()));
		messageItem.add(new MessageItemModel("tag", "个人标签", data.get("tag")
				.toString()));

		messageItem.add(new MessageItemModel("ltext", "个人签名", data.get(
				"signature").toString()));

		// String sex = "";
		// if (data.get("sex") != null) {
		// if (data.get("sex").toString().equals("1"))
		// sex = "男";
		// else if (data.get("sex").toString().equals("0"))
		// sex = "女";
		// }
		// messageItem.add(new MessageItemModel("single", "性别", sex));

		// adapter.setItem(messageItem);
	}

	/**
	 * 调取个人信息失败.
	 */
	private void doPensonFail(Map<String, Object> data) {
		logi("doPensonFail()");
		Toast.makeText(this, data.get("message").toString(), Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * 调取个人信息错误.
	 */
	private void doPensonErr() {
		logi("doPensonErr()");
	}

	class postTask extends AsyncTask<HttpPostUtil, Integer, WebCallResultUtil> {
		@Override
		protected void onProgressUpdate(Integer... values) {
			logi("void onProgressUpdate(Integer... values)");
		}

		@Override
		protected void onPostExecute(WebCallResultUtil result) {
			logi("void onPostExecute(String result)");
			String responseText = result.getResponseText();
			if (result.isCallRight()) {
				try {
					Map<String, Object> data = JsonUtil.parse(responseText);
					String result_code = (String) data.get("result_code");
					if ("0".equals(result_code)) {
						doPensonSuccess(data);
					} else {
						doPensonFail(data);
					}
				} catch (Exception e) {
					e.printStackTrace();
					doPensonErr();
				}
			} else {
				doPensonErr();
			}
		}

		@Override
		protected WebCallResultUtil doInBackground(HttpPostUtil... args) {
			logi("String doInBackground(HttpPostUtil... args)");
			HttpPostUtil httpPostUtil = args[0];
			String message = null;
			WebCallResultUtil mWebCallResultUtil = new WebCallResultUtil();
			try {
				boolean isOK = httpPostUtil.submit();
				if (isOK) {
					message = httpPostUtil.getResponseText();
					mWebCallResultUtil.setCallRight(true);
				} else {
					message = "提交失败.";
					mWebCallResultUtil.setCallRight(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
				mWebCallResultUtil.setCallRight(false);
				message = "提交错误:" + e.getMessage();
			}
			logi("message=" + message);
			mWebCallResultUtil.setResponseText(message);
			return mWebCallResultUtil;
		}

	}

	/**
	 * log information
	 * 
	 * @param msg
	 */
	public static void logi(String msg) {
		Log.i(TAG, CLASS_NAME + " " + msg);
	}

}
