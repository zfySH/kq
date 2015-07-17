package cn.kangeqiu.kq.activity;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.kangeqiu.kq.BaseSimpleActivity;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.adapter.PhotoChooseAdapter;
import cn.kangeqiu.kq.task.AsyncImageLoader;

import com.nowagme.football.AppConfig;
import com.nowagme.util.Bimp;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.FileUtils;
import com.nowagme.util.HttpPostUtil;
import com.nowagme.util.UploadUtil;
import com.nowagme.util.UploadUtil.OnUploadProcessListener;
import com.nowagme.util.WebRequestUtilListener;

public class MyselfPhotosActivity extends BaseSimpleActivity {
	private GridView noScrollgridview;
	private PhotoWallAdapter adapter;

	private PhotoChooseAdapter adapterChoose;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;

	private View parentView;
	private static String picPath = "";
	// private File f = null;
	private String fName = null;

	private String TAG = "she";
	private Bitmap bm = null;

	private AsyncImageLoader asyncImageLoader;

	private String[] array;

	private Button back, delete;
	private Button choose, cancel;

	// private ImageDownloader imageDown = new ImageDownloader();

	private String deleteId = "";

	public static final int REQUEST_CODE_LOCAL = 19;

	// private Type type;
	// private CommonModel<TeamDetailsModel> getPhotosModel;
	// private CommonModel deleteModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);

		parentView = getLayoutInflater().inflate(
				R.layout.abc_activity_selectimg, null);
		asyncImageLoader = new AsyncImageLoader();
		setContentView(parentView);

		back = (Button) parentView.findViewById(R.id.back);
		delete = (Button) parentView.findViewById(R.id.delete);

		choose = (Button) parentView.findViewById(R.id.right);
		cancel = (Button) parentView.findViewById(R.id.cancel);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyselfPhotosActivity.this.finish();
			}
		});
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deleteAction();
			}
		});

		choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deletePicModel();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showPicModel();
			}
		});
		initView();
	}

	private void showPicModel() {
		back.setVisibility(View.VISIBLE);
		delete.setVisibility(View.GONE);

		choose.setVisibility(View.VISIBLE);
		cancel.setVisibility(View.GONE);
		initPhotos();
	}

	private void deletePicModel() {
		back.setVisibility(View.GONE);
		delete.setVisibility(View.VISIBLE);

		choose.setVisibility(View.GONE);
		cancel.setVisibility(View.VISIBLE);

		array = new String[Bimp.bitmapUrls.size()];
		for (int i = 0; i < Bimp.bitmapUrls.size(); i++) {
			array[i] = (String) Bimp.bitmapUrls.get(i);
		}
		adapterChoose = new PhotoChooseAdapter(MyselfPhotosActivity.this, 0,
				array, noScrollgridview);
		noScrollgridview.setAdapter(adapterChoose);
		// adapter.update();
		adapterChoose.notifyDataSetChanged();

		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				adapterChoose.chiceState(position);
			}
		});
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
				initPhotos();
			}

			@Override
			public void initUpload(int fileSize) {

			}
		}); // 设置监听器监听上传状态

		Map<String, String> params = new HashMap<String, String>();
		params.put("app_action", "1003");
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

	private void doPullDate(String action, WebRequestUtilListener listen) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("app_action", action);
		parameters.put("app_platform", "0");
		parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		if (action.equals("1010"))
			parameters.put("u_photo_ids", deleteId);

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

	private void deleteAction() {
		deleteId = adapterChoose.getDeleteBmpId();
		if (deleteId.equals("")) {
			Toast.makeText(this, "请选择照片", Toast.LENGTH_SHORT).show();
			return;
		}
		doPullDate("1010", new WebRequestUtilListener() {

			@Override
			public void onSucces(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(MyselfPhotosActivity.this, "删除成功",
						Toast.LENGTH_SHORT).show();
				initPhotos();
			}

			@Override
			public void onFail(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(MyselfPhotosActivity.this,
						data.get("message").toString(), Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onError() {
				CPorgressDialog.hideProgressDialog();
			}
		});
	}

	private void initView() {
		pop = new PopupWindow(MyselfPhotosActivity.this);

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
				// Intent intent = new Intent(MyselfPhotosActivity.this,
				// AlbumActivity.class);
				// startActivity(intent);

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

		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// adapter = new GridAdapter(this);

		// array = new String[Bimp.bitmapUrls.size()];
		// for (int i = 0; i < Bimp.bitmapUrls.size(); i++) {
		// array[i] = (String) Bimp.bitmapUrls.get(i);
		// }
		// adapter = new PhotoWallAdapter(this, 0, array, noScrollgridview);
		// noScrollgridview.setAdapter(adapter);

		initPhotos();
	}

	private void initPhotos() {

		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == array.length - 1) {
					Log.i("ddddddd", "----------");
					ll_popup.startAnimation(AnimationUtils.loadAnimation(
							MyselfPhotosActivity.this,
							R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
				} else {
					// Intent intent = new Intent(MyselfPhotosActivity.this,
					// GalleryActivity.class);
					// intent.putExtra("url", array[arg2]);
					// startActivity(intent);
				}
			}
		});
		Bimp.bitmapUrls.clear();

		doPullDate("1009", new WebRequestUtilListener() {

			@Override
			public void onSucces(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(MyselfPhotosActivity.this, "加载成功",
						Toast.LENGTH_SHORT).show();

				List<Map<String, String>> photos = (List<Map<String, String>>) data
						.get("photos");
				for (int i = 0; i < photos.size(); i++) {
					String photoId = photos.get(i).get("id");
					String photoUrl = photos.get(i).get("url");
					Bimp.bitmapUrls.add(photoUrl);
					Bimp.bitmapMap.put(photoUrl, photoId);
				}
				array = new String[Bimp.bitmapUrls.size() + 1];
				for (int i = 0; i < Bimp.bitmapUrls.size(); i++) {
					array[i] = (String) Bimp.bitmapUrls.get(i);
				}
				adapter = new PhotoWallAdapter(MyselfPhotosActivity.this, 0,
						array, noScrollgridview);
				// adapter = new MyselfPhotosAdapter(MyselfPhotosActivity.this);
				// adapter.setItem(Bimp.bitmapUrls);
				noScrollgridview.setAdapter(adapter);
				// adapter.update();
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onFail(Map<String, Object> data) {
				CPorgressDialog.hideProgressDialog();
				Toast.makeText(MyselfPhotosActivity.this,
						String.valueOf(data.get("message")), Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onError() {
				CPorgressDialog.hideProgressDialog();
			}
		});

	}

	private static final int TAKE_PICTURE = 0x000001;

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
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

	public class PhotoWallAdapter extends ArrayAdapter<String> implements
			OnScrollListener {

		/**
		 * 记录所有正在下载或等待下载的任务。
		 */
		private Set<BitmapWorkerTask> taskCollection;

		/**
		 * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
		 */
		private LruCache<String, Bitmap> mMemoryCache;

		/**
		 * GridView的实例
		 */
		private GridView mPhotoWall;

		/**
		 * 第一张可见图片的下标
		 */
		private int mFirstVisibleItem;

		/**
		 * 一屏有多少张图片可见
		 */
		private int mVisibleItemCount;

		/**
		 * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。
		 */
		private boolean isFirstEnter = true;

		private String[] urls;

		public PhotoWallAdapter(Context context, int textViewResourceId,
				String[] objects, GridView photoWall) {
			super(context, textViewResourceId, objects);
			urls = objects;
			mPhotoWall = photoWall;
			taskCollection = new HashSet<BitmapWorkerTask>();
			// 获取应用程序最大可用内存
			int maxMemory = (int) Runtime.getRuntime().maxMemory();
			int cacheSize = maxMemory / 8;
			// 设置图片缓存大小为程序最大可用内存的1/8
			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
				@SuppressLint("NewApi")
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return bitmap.getByteCount();
				}
			};
			mPhotoWall.setOnScrollListener(this);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final String url = getItem(position);
			View view;
			if (convertView == null) {
				view = LayoutInflater.from(getContext()).inflate(
						R.layout.abc_item_published_grida, null);
			} else {
				view = convertView;
			}
			final ImageView photo = (ImageView) view
					.findViewById(R.id.item_grida_image);
			// 给ImageView设置一个Tag，保证异步加载图片时不会乱序
			if (position == Bimp.bitmapUrls.size()) {
				photo.setImageResource(R.drawable.icon_addpic_unfocused);
			} else {
				photo.setTag(url);
				setImageView(url, photo);
			}
			return view;
		}

		/**
		 * 给ImageView设置图片。首先从LruCache中取出图片的缓存，设置到ImageView上。如果LruCache中没有该图片的缓存，
		 * 就给ImageView设置一张默认图片。
		 * 
		 * @param imageUrl
		 *            图片的URL地址，用于作为LruCache的键。
		 * @param imageView
		 *            用于显示图片的控件。
		 */
		private void setImageView(String imageUrl, ImageView imageView) {
			Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
			} else {
				imageView.setImageResource(R.drawable.ic_my_empty);
			}
		}

		/**
		 * 将一张图片存储到LruCache中。
		 * 
		 * @param key
		 *            LruCache的键，这里传入图片的URL地址。
		 * @param bitmap
		 *            LruCache的键，这里传入从网络上下载的Bitmap对象。
		 */
		public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
			if (getBitmapFromMemoryCache(key) == null) {
				mMemoryCache.put(key, bitmap);
			}
		}

		/**
		 * 从LruCache中获取一张图片，如果不存在就返回null。
		 * 
		 * @param key
		 *            LruCache的键，这里传入图片的URL地址。
		 * @return 对应传入键的Bitmap对象，或者null。
		 */
		public Bitmap getBitmapFromMemoryCache(String key) {
			if (key == null)
				return null;
			else
				return mMemoryCache.get(key);
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 仅当GridView静止时才去下载图片，GridView滑动时取消所有正在下载的任务
			if (scrollState == SCROLL_STATE_IDLE) {
				loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
			} else {
				cancelAllTasks();
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			mFirstVisibleItem = firstVisibleItem;
			mVisibleItemCount = visibleItemCount;
			// 下载的任务应该由onScrollStateChanged里调用，但首次进入程序时onScrollStateChanged并不会调用，
			// 因此在这里为首次进入程序开启下载任务。
			if (isFirstEnter && visibleItemCount > 0) {
				loadBitmaps(firstVisibleItem, visibleItemCount);
				isFirstEnter = false;
			}
		}

		/**
		 * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
		 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
		 * 
		 * @param firstVisibleItem
		 *            第一个可见的ImageView的下标
		 * @param visibleItemCount
		 *            屏幕中总共可见的元素数
		 */
		private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
			try {
				for (int i = firstVisibleItem; i < firstVisibleItem
						+ visibleItemCount; i++) {
					String imageUrl = array[i];
					Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
					if (bitmap == null) {
						BitmapWorkerTask task = new BitmapWorkerTask();
						taskCollection.add(task);
						task.execute(imageUrl);
					} else {
						ImageView imageView = (ImageView) mPhotoWall
								.findViewWithTag(imageUrl);
						if (imageView != null && bitmap != null) {
							imageView.setImageBitmap(bitmap);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 取消所有正在下载或等待下载的任务。
		 */
		public void cancelAllTasks() {
			if (taskCollection != null) {
				for (BitmapWorkerTask task : taskCollection) {
					task.cancel(false);
				}
			}
		}

		/**
		 * 异步下载图片的任务。
		 * 
		 * @author guolin
		 */
		class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

			/**
			 * 图片的URL地址
			 */
			private String imageUrl;

			@Override
			protected Bitmap doInBackground(String... params) {
				imageUrl = params[0];
				// 在后台开始下载图片
				Bitmap bitmap = downloadBitmap(params[0]);
				if (bitmap != null) {
					// 图片下载完成后缓存到LrcCache中
					addBitmapToMemoryCache(params[0], bitmap);
				}
				return bitmap;
			}

			@Override
			protected void onPostExecute(Bitmap bitmap) {
				super.onPostExecute(bitmap);
				// 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
				ImageView imageView = (ImageView) mPhotoWall
						.findViewWithTag(imageUrl);
				if (imageView != null && bitmap != null) {
					imageView.setImageBitmap(bitmap);
				}
				taskCollection.remove(this);
			}

			/**
			 * 建立HTTP请求，并获取Bitmap对象。
			 * 
			 * @param imageUrl
			 *            图片的URL地址
			 * @return 解析后的Bitmap对象
			 */
			private Bitmap downloadBitmap(String imageUrl) {
				Bitmap bitmap = null;
				HttpURLConnection con = null;
				try {
					URL url = new URL(imageUrl);
					con = (HttpURLConnection) url.openConnection();
					con.setConnectTimeout(5 * 1000);
					con.setReadTimeout(10 * 1000);
					bitmap = BitmapFactory.decodeStream(con.getInputStream());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (con != null) {
						con.disconnect();
					}
				}
				return bitmap;
			}

		}

	}

	// @Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出程序时结束所有的下载任务
		adapter.cancelAllTasks();
		if (adapterChoose != null)
			adapterChoose.cancelAllTasks();
	}

}
