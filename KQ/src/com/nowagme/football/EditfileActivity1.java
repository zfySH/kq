package com.nowagme.football;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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
import android.util.Log;
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
import cn.kangeqiu.kq.Constant;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.MainActivity;
import cn.kangeqiu.kq.activity.MyselfMessageDetailActivity;
import cn.kangeqiu.kq.activity.imglist.UploadUtil;
import cn.kangeqiu.kq.db.UserDao;
import cn.kangeqiu.kq.domain.User;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.jingyi.MiChat.application.BaseApplication;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.util.Bimp;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.FileUtils;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.MyActivityManager;
import com.nowagme.util.ToolUtil;
import com.nowagme.util.WebRequestUtil;

public class EditfileActivity1 extends BaseSimpleActivity implements
		OnClickListener {

	private Button abc_team__btn_save;
	private ImageView abc_faceimg;
	private TextView arron_sex, arron_domicile, abc_team__title;
	private EditText arron_name, arron_age;
	private Context context;
	private RelativeLayout name_btn, sex_btn, age_btn, domicile_btn, main_lay;
	private int relation;
	private PopupWindowTeamMore pop;
	private Map<String, Object> user = null;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int RESULT_CODE_CITY = 20;

	private static final int TAKE_PICTURE = 0x000001;
	private Bitmap bm = null;
	private static String picPath = "";
	private String fName = null;
	private String provinceId = null;
	private String cityId = null;
	private String username = null;
	private String password = null, uid = null;
	private ImagerLoader loader = new ImagerLoader();
	MyActivityManager mam = MyActivityManager.getInstance();
	// private boolean progressShow;
	// private ProgressDialog pd;
	private String currentUsername;
	private String currentPassword;
	private boolean isOther = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_item1);
		mam.pushOneActivity(EditfileActivity1.this);
		context = this;
		relation = 3;
		initHandle();
		init();

	}

	public void back(View view) {
		finish();
	}

	private void initHandle() {
		// Bundle bundle = getIntent().getExtras();
		// SerializableMap serializableMap = (SerializableMap)
		// bundle.get("user");
		// user = serializableMap.getMap();
		// provinceId = ((Map<String, String>) user.get("province")).get("id");
		// cityId = ((Map<String, String>) user.get("city")).get("id");
		// 获取来源参数
		Bundle extras = this.getIntent().getExtras();
		if (extras != null && extras.getString("uid") != null) {
			username = extras.getString("username");
			password = extras.getString("pwd");
			uid = extras.getString("uid");
		} else
			isOther = true;
	}

	private void init() {

		abc_team__btn_save = (Button) findViewById(R.id.abc_team__btn_save);
		abc_team__title = (TextView) findViewById(R.id.abc_team__title);
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
		user = new HashMap<String, Object>();
		abc_team__title.setText("加入我们");
		if (user == null) {
			arron_name.setText(user.get("nickname").toString());
			arron_sex.setText(getSex(user.get("sex").toString()));
			arron_age.setText(user.get("age").toString());

			arron_domicile.setText(((Map<String, String>) user.get("province"))
					.get("name").toString()
					+ "  "
					+ ((Map<String, String>) user.get("city")).get("name")
							.toString());
			// new DownAndShowImageTask(user.get("icon").toString(),
			// abc_faceimg)
			// .execute();
			loader.LoadImage(user.get("icon").toString(), abc_faceimg);
		}
		abc_team__btn_save.setOnClickListener(this);
		abc_faceimg.setOnClickListener(this);
		name_btn.setOnClickListener(this);
		sex_btn.setOnClickListener(this);
		age_btn.setOnClickListener(this);
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
								// Toast.makeText(EditfileActivity1.this,
								// "编辑成功",
								// Toast.LENGTH_SHORT).show();
								if (!isOther) {
									doLogin();
								} else {
									startActivity(new Intent(
											EditfileActivity1.this,
											MainActivity.class));
									finish();
								}

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
						Toast.makeText(EditfileActivity1.this,
								resp.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				});

			} else {
				Toast.makeText(EditfileActivity1.this, msg, Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.abc_faceimg:
			pop = new PopupWindowTeamMore(this, new PopupWinBtnOnclick(), 4);
			pop.showAtLocation(main_lay, Gravity.BOTTOM, 0, 0);
			break;
		case R.id.name_btn:
			break;
		case R.id.sex_btn:
			pop = new PopupWindowTeamMore(this, new PopupWinBtnOnclick(),
					relation);
			pop.showAtLocation(main_lay, Gravity.BOTTOM, 0, 0);
			break;
		case R.id.age_btn:
			break;
		case R.id.domicile_btn:
			Intent intent = new Intent();
			intent.setClass(EditfileActivity1.this,
					MyselfMessageDetailActivity.class);
			EditfileActivity1.this.startActivityForResult(intent, 0);
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
		// CPorgressDialog.showProgressDialog(this);

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		if (uid == null)
			pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
					.getPlayerId() + ""));
		else
			pair.add(new BasicNameValuePair("u_uid", uid));
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
		if (uid == null)
			parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		else
			parameters.put("u_uid", uid);

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
					Toast.makeText(EditfileActivity1.this, "修改成功",
							Toast.LENGTH_SHORT).show();

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
				Toast.makeText(EditfileActivity1.this, "修改失败",
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

	/**
	 * 登录
	 */
	private void doLogin() {
		// progressShow = true;
		// pd = new ProgressDialog(EditfileActivity1.this);
		// pd.setCanceledOnTouchOutside(false);
		// pd.setOnCancelListener(new OnCancelListener() {
		//
		// @Override
		// public void onCancel(DialogInterface dialog) {
		// progressShow = false;
		// }
		// });
		// pd.setMessage("正在验证身份信息");
		// pd.show();
		doPullDate(null, "1001", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						int playerId = 0;
						JSONArray list = resp.getJson().getJSONArray("user");
						// List<Map<String, Object>> list = (List<Map<String,
						// Object>>) data
						// .get("user");
						playerId = Integer.parseInt(list.getJSONObject(0)
								.getString("id"));
						String nickname = (String) (list.getJSONObject(0)
								.getString("nickname"));
						AppConfig.getInstance().setPlayerId(playerId);
						// doShowTabMenu();
						// 这里开始自动登录环信.
						// 环信用户名和密码均为:playerId
						// 设置环信用户昵称
						BaseApplication.currentUserNick = nickname;
						currentUsername = String.valueOf(playerId);
						currentPassword = String.valueOf(playerId);
						// 开始登录环信
						doLoginHuanXin();
					} else {
						// pd.dismiss();
						Toast.makeText(getApplicationContext(),
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
				try {
					Toast.makeText(EditfileActivity1.this,
							resp.getJson().getString("message"),
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		// Map<String, String> parameters = new HashMap<String, String>();
	}

	private void doLoginHuanXin() {

		final long start = System.currentTimeMillis();
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(currentUsername, currentPassword,
				new EMCallBack() {

					@Override
					public void onSuccess() {
						// umeng自定义事件，开发者可以把这个删掉

						// 登陆成功，保存用户名密码
						BaseApplication.getInstance().setUserName(
								currentUsername);
						BaseApplication.getInstance().setPassword(
								currentPassword);

						// String editName =
						// usernameEditText.getText().toString()
						// .trim();
						// String editPwd =
						// passwordEditText.getText().toString()
						// .trim();
						ToolUtil.putValue(String.class, "name", username);
						ToolUtil.putValue(String.class, "pwd", password);
						ToolUtil.putValue(String.class, "uid", currentUsername);
						//
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(getApplicationContext(),
										R.string.list_is_for, 1).show();
							}
						});
						try {
							// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
							// ** manually load all local groups and
							// conversations in case we are auto login
							EMGroupManager.getInstance().loadAllGroups();
							EMChatManager.getInstance().loadAllConversations();
							// 处理好友和群组
							processContactsAndGroups();
						} catch (Exception e) {
							e.printStackTrace();
							// 取好友或者群聊失败，不让进入主页面
							runOnUiThread(new Runnable() {
								public void run() {
									BaseApplication.getInstance().logout(null);
									Toast.makeText(getApplicationContext(),
											R.string.login_failure_failed, 1)
											.show();
								}
							});
							return;
						}
						// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
						boolean updatenick = EMChatManager.getInstance()
								.updateCurrentUserNick(
										BaseApplication.currentUserNick.trim());
						if (!updatenick) {
							Log.v("LoginActivity",
									"update current user nick fail");
						}
						// if (!EditfileActivity1.this.isFinishing())
						// 进入主页面

						// if (state.equals("1")) {// 首次登录
						// startActivity(new Intent(LoginActivity.this,
						// EditfileActivity1.class));
						// } else
						startActivity(new Intent(EditfileActivity1.this,
								MainActivity.class));
						finish();
					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(final int code, final String message) {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(
										getApplicationContext(),
										getString(R.string.Login_failed)
												+ message, Toast.LENGTH_SHORT)
										.show();
							}
						});
					}
				});

	}

	private void doPullDate(String loginType, String action,
			MCHttpCallBack listener) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		if (action.equals("1001")) {
			pair.add(new BasicNameValuePair("u_mobile", username));
			pair.add(new BasicNameValuePair("u_pwd", password));
			pair.add(new BasicNameValuePair("u_type", "0"));
		}
		new WebRequestUtil().execute(false,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listener);
	}

	private void processContactsAndGroups() throws EaseMobException {
		// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
		List<String> usernames = EMContactManager.getInstance()
				.getContactUserNames();
		System.out.println("----------------" + usernames.toString());
		EMLog.d("roster", "contacts size: " + usernames.size());
		Map<String, User> userlist = new HashMap<String, User>();
		for (String username : usernames) {
			User user = new User();
			user.setUsername(username);
			setUserHearder(username, user);
			userlist.put(username, user);
		}
		// 添加user"申请与通知"
		User newFriends = new User();
		newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
		String strChat = getResources().getString(
				R.string.Application_and_notify);
		newFriends.setNick(strChat);

		userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
		// 添加"群聊"
		User groupUser = new User();
		String strGroup = getResources().getString(R.string.group_chat);
		groupUser.setUsername(Constant.GROUP_USERNAME);
		groupUser.setNick(strGroup);
		groupUser.setHeader("");
		userlist.put(Constant.GROUP_USERNAME, groupUser);

		// 存入内存
		BaseApplication.getInstance().setContactList(userlist);
		// 存入db
		UserDao dao = new UserDao(EditfileActivity1.this);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);

		// 获取黑名单列表
		List<String> blackList = EMContactManager.getInstance()
				.getBlackListUsernamesFromServer();
		// 保存黑名单
		EMContactManager.getInstance().saveBlackList(blackList);

		// 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
		EMGroupManager.getInstance().getGroupsFromServer();
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}
}
