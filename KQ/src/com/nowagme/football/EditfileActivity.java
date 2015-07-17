package com.nowagme.football;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.MyselfMessageDetailActivity;
import cn.kangeqiu.kq.activity.imglist.UploadUtil;

import com.easemob.applib.model.SerializableMap;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.util.Bimp;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.FileUtils;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.WebRequestUtil;

public class EditfileActivity extends BaseSimpleActivity implements
		OnClickListener {

	private Button abc_team__btn_save;
	private ImageView abc_faceimg;
	private TextView arron_sex, arron_domicile;
	private EditText arron_name, arron_age;
	private Context context;
	private RelativeLayout name_btn, sex_btn, age_btn, domicile_btn, main_lay;
	private int relation;
	private PopupWindowTeamMore pop;
	private SerializableMap user = null;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int RESULT_CODE_CITY = 20;

	private static final int TAKE_PICTURE = 0x000001;
	private Bitmap bm = null;
	private static String picPath = "";
	private String fName = null;
	String provinceId = null;
	String cityId = null;
	String username = null;
	String password = null;
	ImagerLoader loader = new ImagerLoader();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_item1);
		context = this;
		relation = 3;
		initHandle();
		init();

	}

	public void back(View view) {
		finish();
	}

	private void initHandle() {
		Bundle bundle = getIntent().getExtras();
		user = (SerializableMap) bundle.get("user");
		// user = new JSONObject(serializableMap);
		provinceId = user.getProvince().get("id").toString();
		cityId = user.getCity().get("id").toString();

		// 获取来源参数
		Bundle extras = this.getIntent().getExtras();
		username = extras.getString("username");
		password = extras.getString("pwd");

	}

	private void init() {

		abc_team__btn_save = (Button) findViewById(R.id.abc_team__btn_save);

		abc_faceimg = (ImageView) findViewById(R.id.abc_faceimg);
		arron_sex = (TextView) findViewById(R.id.arron_sex);
		sex_btn = (RelativeLayout) findViewById(R.id.sex_btn);
		name_btn = (RelativeLayout) findViewById(R.id.name_btn);
		age_btn = (RelativeLayout) findViewById(R.id.age_btn);
		main_lay = (RelativeLayout) findViewById(R.id.main_lay);
		domicile_btn = (RelativeLayout) findViewById(R.id.domicile_btn);
		arron_name = (EditText) findViewById(R.id.arron_name);
		arron_age = (EditText) findViewById(R.id.arron_age);
		arron_domicile = (TextView) findViewById(R.id.arron_domicile);
		// arron_name.setSelection(arron_name.length());
		// arron_age.setSelection(arron_age.length());

		if (user != null) {
			arron_name.setText(user.getNickname());

			arron_sex.setText(getSex(user.getSex()));
			arron_age.setText(user.getAge());

			arron_domicile.setText(user.getProvince().get("name").toString()
					+ "  " + user.getCity().get("name").toString());
			// new DownAndShowImageTask(user.getIcon(), abc_faceimg).execute();
			loader.LoadImage(user.getIcon(), abc_faceimg);
		}
		abc_team__btn_save.setOnClickListener(this);
		abc_faceimg.setOnClickListener(this);
		arron_name.setOnClickListener(this);
		sex_btn.setOnClickListener(this);
		arron_age.setOnClickListener(this);
		domicile_btn.setOnClickListener(this);
	}

	private String getSex(String sex) {
		if (sex.equals("1"))
			return "男";
		else if (sex.equals("2"))
			return "女";
		else
			return "";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.abc_team__btn_save:
			String msg = isNull();
			if (msg == null) {
				doPullDate("2021", new MCHttpCallBack() {
					@Override
					public void onSuccess(MCHttpResp resp) {
						super.onSuccess(resp);
						CPorgressDialog.hideProgressDialog();
						try {
							String resultCode = resp.getJson().getString(
									"result_code");
							if (resultCode.equals("0")) {
								Toast.makeText(EditfileActivity.this, "修改成功",
										Toast.LENGTH_SHORT).show();
								setResult(200);
								finish();
							} else {
								Toast.makeText(context,
										resp.getJson().getString("message"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onError(MCHttpResp resp) {
						super.onError(resp);
						CPorgressDialog.hideProgressDialog();
						Toast.makeText(EditfileActivity.this,
								resp.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				});
			} else {
				Toast.makeText(EditfileActivity.this, msg, Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.abc_faceimg:
			pop = new PopupWindowTeamMore(this, new PopupWinBtnOnclick(), 4);
			pop.showAtLocation(main_lay, Gravity.BOTTOM, 0, 0);
			break;
		case R.id.arron_name:
			arron_name.setSelection(arron_name.length());
			break;
		case R.id.sex_btn:
			pop = new PopupWindowTeamMore(this, new PopupWinBtnOnclick(),
					relation);
			pop.showAtLocation(main_lay, Gravity.BOTTOM, 0, 0);
			break;
		case R.id.arron_age:
			arron_age.setSelection(arron_age.length());
			break;
		case R.id.domicile_btn:
			Intent intent = new Intent();
			intent.setClass(EditfileActivity.this,
					MyselfMessageDetailActivity.class);
			EditfileActivity.this.startActivityForResult(intent, 0);
			break;

		default:
			break;
		}

	}

	private String getSex_(String sex) {
		if (sex.equals("男"))
			return "1";
		else if (sex.equals("女"))
			return "2";
		else
			return "0";
	}

	private String isNull() {
		if (TextUtils.isEmpty(arron_name.getText().toString()))
			return "昵称不能为空";
		if (TextUtils.isEmpty(arron_age.getText().toString()))
			return "年龄不能为空";
		if (TextUtils.isEmpty(arron_sex.getText().toString()))
			return "性别不能为空";
		if (TextUtils.isEmpty(arron_domicile.getText().toString()))
			return "居住地不能为空";
		return null;
	}

	private void doPullDate(String action, MCHttpCallBack listen) {
		CPorgressDialog.showProgressDialog(this);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_nickname", arron_name.getText()
				.toString()));
		pair.add(new BasicNameValuePair("u_sex", getSex_(arron_sex.getText()
				.toString())));
		pair.add(new BasicNameValuePair("u_age", arron_age.getText().toString()));
		pair.add(new BasicNameValuePair("u_province_id", provinceId));
		pair.add(new BasicNameValuePair("u_city_id", cityId));

		new WebRequestUtil().execute(false,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listen);
	}

	/**
	 * 更多
	 * 
	 * @author zjq
	 * 
	 */
	private class PopupWinBtnOnclick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.abc_btn_man:
				arron_sex.setText("男");
				pop.dismiss();
				break;
			case R.id.abc_btn_girl:
				arron_sex.setText("女");
				pop.dismiss();
				break;
			case R.id.abc_btn_cancel:
				pop.dismiss();
				break;
			case R.id.item_popupwindows_camera:
				photo();
				pop.dismiss();
				break;
			case R.id.item_popupwindows_Photo:
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
				pop.dismiss();
				break;
			}
		}

	}

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		case RESULT_CODE_CITY:
			Bundle b = data.getExtras(); // data为B中回传的Intent
			String name = b.getString("name");
			provinceId = b.getString("provinceId");
			cityId = b.getString("cityId");
			arron_domicile.setText(name);

			break;

		}
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

				String fileName = String.valueOf(System.currentTimeMillis());
				bm = (Bitmap) data.getExtras().get("data");
				FileUtils.saveBitmap(bm, fileName);

				picPath = Environment.getExternalStorageDirectory()
						+ "/Photo_She/" + fileName + ".JPEG";
				fName = fileName + ".JPEG";

				upload(fName, picPath);
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
			upload(picturePath.substring(picturePath.lastIndexOf("/") + 1),
					picturePath);

			picPath = picturePath;
			// sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			upload(file.getAbsolutePath().substring(
					file.getAbsolutePath().lastIndexOf("/") + 1),
					file.getAbsolutePath());
			picPath = file.getAbsolutePath();
			// sendPicture(file.getAbsolutePath());
		}

	}

	private void upload(String fName, String picPath) {
		CPorgressDialog.showProgressDialog("正在上传...", this);
		// 开始上传
		UploadUtil uploadUtil = getUploadUtil(fName, picPath);
		new UploadTask().execute(uploadUtil);
	}

	public UploadUtil getUploadUtil(String fName, String picPath) {

		// String appSecret = "de3jkfg74slftG$*&a_@";
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", "2023");
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");

		parameters.put("u_file1", fName);
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

		// for (int i = 0; i < listfile.size(); i++) {
		// String fileKey = "u_file" + (i + 1);
		// parameters.remove(fileKey);
		// }
		// hm.remove(imageParamName);
		// hm.remove(imageParamName2);

		Map<String, String> files = new HashMap<String, String>();
		files.put("u_file1", picPath);
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
					BitmapFactory.Options option = new BitmapFactory.Options();
					option.inSampleSize = 2;
					Bitmap bm = BitmapFactory.decodeFile(picPath, option);
					abc_faceimg.setImageBitmap(bm);
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
				Toast.makeText(EditfileActivity.this, "修改失败",
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
					message = "修改失败.";
					data.put("result_code", "-1");
				}
			} catch (Exception e) {
				e.printStackTrace();
				message = "修改失败:" + e.getMessage();
				data.put("result_code", "-2");
			}
			data.put("message", message);
			return data;
		}

	}
}
