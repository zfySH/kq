	package com.king.photo.activity;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.imglist.UploadUtil;
import cn.kangeqiu.kq.adapter.ExpressionAdapter;
import cn.kangeqiu.kq.adapter.ExpressionPagerAdapter;
import cn.kangeqiu.kq.utils.SmileUtils;
import cn.kangeqiu.kq.video.util.AsyncTask;
import cn.kangeqiu.kq.widget.ExpandGridView;

import com.king.photo.util.Bimp;
import com.king.photo.util.FileUtils;
import com.king.photo.util.ImageItem;
import com.king.photo.util.Res;
import com.nowagme.football.AppConfig;
import com.nowagme.util.CPorgressDialog;

/**
 * 创建动态新activity
 * 
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:48:34
 */
public class MainActivity extends AgentActivity {
	private static String picPath = "";
	private String fName = null;
	private GridView noScrollgridview;
	private GridAdapter adapter;
	private View parentView;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	public static Bitmap bimap;
	public Button upload_photo, camera, add;
	private String match_id;
	private RelativeLayout main_lay;
	TextView tv_num;// 用来显示剩余字数<br>
	int num = 140;// 限制的最大字数
	private EditText content;
	private Button iv_emoticons_checked;
	private InputMethodManager manager;
	private LinearLayout emojiIconContainer;
	private View more;
	private ViewPager expressionViewpager;
	private List<String> reslist;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Res.init(this);
		init();
		// bimap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.icon_addpic_unfocused);
		// PublicWay.activityList.add(this);
		parentView = getLayoutInflater().inflate(
				R.layout.abc_match_create_comment_activity, null);

		setContentView(parentView);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		Init();
		content.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start,
					int before, int count) {
				temp = s;
				System.out.println("s=" + s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int number = num - s.length();
//				tv_num.setText("" + number);
				selectionStart = content.getSelectionStart();
				selectionEnd = content.getSelectionEnd();
				// System.out.println("start="+selectionStart+",end="+selectionEnd);
				if (temp.length() > num) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionStart;
					content.setText(s);
					content.setSelection(tempSelection);// 设置光标在最后
					Toast.makeText(MainActivity.this, "字数过长，最多140字", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void init() {
		match_id = getIntent().getStringExtra("matchId");

	}

	public void Init() {
		upload_photo = (Button) findViewById(R.id.upload_photo);
		upload_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(MainActivity.this,
				// AlbumActivity.class);
				// intent.putExtra("matchId", match_id);
				// startActivity(intent);
				// overridePendingTransition(R.anim.activity_translate_in,
				// R.anim.activity_translate_out);
				pop();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(main_lay.getWindowToken(), 0);
				// ll_popup.startAnimation(AnimationUtils.loadAnimation(
				// MainActivity.this, R.anim.activity_translate_in));
				pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
			}
		});
		more = findViewById(R.id.more);
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		iv_emoticons_checked = (Button) findViewById(R.id.iv_emoticons_checked);
		main_lay = (RelativeLayout) findViewById(R.id.main_lay);
		// add = (Button) findViewById(R.id.add);
		content = (EditText) findViewById(R.id.content);
		noScrollgridview = (GridView) findViewById(R.id.photo_gridview);

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

		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		// adapter.update();
		noScrollgridview.setAdapter(adapter);
		// noScrollgridview
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					// pop();
					// InputMethodManager imm = (InputMethodManager)
					// getSystemService(Context.INPUT_METHOD_SERVICE);
					// imm.hideSoftInputFromWindow(main_lay.getWindowToken(),
					// 0);
					// // ll_popup.startAnimation(AnimationUtils.loadAnimation(
					// // MainActivity.this, R.anim.activity_translate_in));
					// pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
				} else {
					Intent intent = new Intent(MainActivity.this,
							GalleryActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});

	}

	public void onEdit(View view) {
		more.setVisibility(View.GONE);
		emojiIconContainer.setVisibility(View.GONE);

	}

	public void EmoticonsChecked(View view) {
		more.setVisibility(View.VISIBLE);
		emojiIconContainer.setVisibility(View.VISIBLE);
		hideKeyboard();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(main_lay.getWindowToken(), 0);
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
								MainActivity.this, (String) field.get(null)));
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

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}

	private void pop() {

		pop = new PopupWindow(MainActivity.this);

		View view = getLayoutInflater().inflate(R.layout.item_popupwindows,
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
				// TODO Auto-generated method stub
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
				Intent intent = new Intent(MainActivity.this,
						AlbumActivity.class);
				intent.putExtra("matchId", match_id);
				startActivity(intent);
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

	}

	public void Upload(View view) {

		if (Bimp.tempSelectBitmap.size() <= 0) {
			if (content.getText().toString() == null
					|| content.getText().toString().equals("")) {
				Toast.makeText(this, "请输入内容或者选择照片", Toast.LENGTH_SHORT).show();
				return;
			}
			

		}
		uploadFileWithTask();
	}

	public void back(View view) {
		finish();
		Bimp.tempSelectBitmap.clear();
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
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", "2002");
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		parameters.put("u_type", "0");
		parameters.put("u_record_id", match_id);
		parameters.put("u_body", content.getText().toString());

		for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
			String fileKey = "u_file" + (i + 1);
			String fileName = Bimp.tempSelectBitmap
					.get(i)
					.getImagePath()
					.substring(
							Bimp.tempSelectBitmap.get(i).getImagePath()
									.lastIndexOf("/") + 1);
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

		// for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
		// String fileKey = "u_file" + (i + 1);
		// parameters.remove(fileKey);
		// }
		// hm.remove(imageParamName);
		// hm.remove(imageParamName2);

		Map<String, String> files = new HashMap<String, String>();
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
			String fileKey = "u_file" + (i + 1);
			files.put(fileKey, Bimp.tempSelectBitmap.get(i).getImagePath());
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
					Bimp.tempSelectBitmap.clear();
					Toast.makeText(MainActivity.this, "发布成功",
							Toast.LENGTH_SHORT).show();
					setResult(RESULT_OK);
					finish();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(MainActivity.this, "发布失败", Toast.LENGTH_SHORT)
						.show();
			}
		}

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

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			if (Bimp.tempSelectBitmap.size() == 9) {
				return 9;
			}
			return (Bimp.tempSelectBitmap.size());
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.abc_item_published_grida, parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				holder.delete = (ImageButton) convertView
						.findViewById(R.id.img_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.tempSelectBitmap.size()) {
				// holder.image.setImageBitmap(BitmapFactory.decodeResource(
				// getResources(), R.drawable.icon_addpic_unfocused));
				//
				holder.delete.setVisibility(View.GONE);
				//
				// if (position == 9) {
				// holder.image.setVisibility(View.GONE);
				//
				// }
			} else {
				holder.delete.setVisibility(View.VISIBLE);
				holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position)
						.getBitmap());
				holder.image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (position == Bimp.tempSelectBitmap.size()) {
							// InputMethodManager imm = (InputMethodManager)
							// getSystemService(Context.INPUT_METHOD_SERVICE);
							// imm.hideSoftInputFromWindow(
							// main_lay.getWindowToken(), 0);
							// pop.showAtLocation(parentView, Gravity.BOTTOM, 0,
							// 0);
						} else {
							Intent intent = new Intent(MainActivity.this,
									GalleryActivity.class);
							intent.putExtra("position", "1");
							intent.putExtra("ID", position);
							startActivity(intent);
						}

					}
				});
				holder.delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Bimp.tempSelectBitmap.remove(position);
						notifyDataSetChanged();

					}
				});

			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
			public ImageButton delete;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.tempSelectBitmap.size()) {
							// Message message = new Message();
							// message.what = 1;
							// handler.sendMessage(message);
							break;
						} else {
							Bimp.max += 1;
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		// adapter.update();
		adapter.notifyDataSetChanged();
		super.onRestart();
	}

	private static final int TAKE_PICTURE = 0x000001;

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		openCameraIntent.putExtra("matchId", match_id);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 1 && resultCode == RESULT_OK) {
				String fileName = String.valueOf(System.currentTimeMillis());
				String path = FileUtils.SDPATH + fileName + ".JPEG";
				Bitmap bm = (Bitmap) data.getExtras().get("data");
				FileUtils.saveBitmap(bm, fileName);
				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(bm);
				takePhoto.setImagePath(path);
				Bimp.tempSelectBitmap.add(takePhoto);

			}
			break;

		}
	}

	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// for (int i = 0; i < PublicWay.activityList.size(); i++) {
	// if (null != PublicWay.activityList.get(i)) {
	// PublicWay.activityList.get(i).finish();
	// }
	// }
	// System.exit(0);
	// }
	// return true;
	// }

}
