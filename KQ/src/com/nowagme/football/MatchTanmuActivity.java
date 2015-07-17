package com.nowagme.football;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import master.flame.danmaku.controller.DrawHandler.Callback;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.AcFunDanmakuParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.kangeqiu.kq.AgentActivity;
import cn.kangeqiu.kq.R;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.WebRequestUtil;

public class MatchTanmuActivity extends AgentActivity {
	private IDanmakuView mDanmakuView;
	private BaseDanmakuParser mParser;

	private EditText ed_comment;

	private RelativeLayout rootView;

	private boolean canFinish = false;

	private String match_id;

	private String max_id = "0";

	private JSONArray comments = null;

	private long time_point_start = 0;

	private boolean isUpdate = true;

	private boolean isOk = false;

	private Timer timer = new Timer(true);

	private String createrName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tanmu);

		initData();
		initHandle();
		initView();
	}

	private void initData() {
		doPullDate("2030", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						createrName = resp.getJson().getString("nickname");
					} else {
						Toast.makeText(MatchTanmuActivity.this,
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
			}

		});
	}

	private void initHandle() {
		match_id = getIntent().getStringExtra("match_id");
	}

	private void initView() {
		comments = new JSONArray();
		mDanmakuView = (IDanmakuView) findViewById(R.id.sv_danmaku);
		ed_comment = (EditText) findViewById(R.id.ed_comment);
		rootView = (RelativeLayout) findViewById(R.id.root);
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {

						// 比较Activity根布局与当前布局的大小
						int heightDiff = rootView.getRootView().getHeight()
								- rootView.getHeight();
						if (heightDiff > 100) {
							// 大小超过100时，一般为显示虚拟键盘事件
							canFinish = true;
						} else {
							// 大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
							if (canFinish)
								MatchTanmuActivity.this.finish();
						}
					}
				});

		// 弹幕设置
		DanmakuGlobalConfig.DEFAULT.setDanmakuStyle(
				DanmakuGlobalConfig.DANMAKU_STYLE_STROKEN, 3)
				.setDuplicateMergingEnabled(false);
		if (mDanmakuView != null) {

			// mParser =
			// createParser(this.getResources().openRawResource(
			// R.raw.comments));

			mDanmakuView.setCallback(new Callback() {

				@Override
				public void updateTimer(DanmakuTimer timer) {

				}

				@Override
				public void prepared() {
					mDanmakuView.start();
				}
			});

			// mDanmakuView.showFPS(true);
			mDanmakuView.enableDanmakuDrawingCache(true);
			getTanmu(true);

			if (isUpdate) {
				// 启动定时器
				timer.schedule(task, 0, 10 * 1000);

			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (isOk)
					getTanmu(false);
				// todo something....
			}
		}
	};
	private TimerTask task = new TimerTask() {
		public void run() {
			Message msg = new Message();
			msg.what = 1;
			handler.sendMessage(msg);
		}
	};

	public void getTanmu(final boolean isFirst) {
		isOk = false;
		doPullDate("2012", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						JSONArray records = resp.getJson().getJSONArray(
								"records");

						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						for (int i = 0; i < records.length(); i++) {
							if (isFirst) {
								if (i == 0) {
									time_point_start = sdf.parse(
											records.getJSONObject(0).getString(
													"time")).getTime();
								}
							}
							if (i == records.length() - 1) {
								max_id = records.getJSONObject(i).getString(
										"id");
							}

							JSONObject comment = new JSONObject();
							long time_point = (sdf.parse(
									records.getJSONObject(i).getString("time"))
									.getTime() - time_point_start) / 1000;
							comment.put("c", time_point + ",16777215,1,25");
							comment.put("m", records.getJSONObject(i)
									.getString("text"));
							comments.put(comment);
							if (!isFirst) {
								addHttpDanmaku(false, records.getJSONObject(i)
										.getString("text"));
							}
						}
						if (isFirst) {
							mParser = createAcParser(new ByteArrayInputStream(
									comments.toString().getBytes()));
							mDanmakuView.prepare(mParser);
						}

						isOk = true;
					} else {
						Toast.makeText(MatchTanmuActivity.this,
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
			}
		}

		);
	}

	public void Send(View view) {
		if (ed_comment.getText().toString() == null
				|| ed_comment.getText().toString().equals("")) {
			Toast.makeText(this, "请输入弹幕内容", Toast.LENGTH_SHORT).show();
			return;
		}
		addDanmaku(false, ed_comment.getText().toString());
	}

	private BaseDanmakuParser createAcParser(InputStream stream) {

		if (stream == null) {
			return new BaseDanmakuParser() {

				@Override
				protected Danmakus parse() {
					return new Danmakus();
				}
			};
		}

		ILoader loader = DanmakuLoaderFactory
				.create(DanmakuLoaderFactory.TAG_ACFUN);

		try {
			loader.load(stream);
		} catch (IllegalDataException e) {
			e.printStackTrace();
		}

		BaseDanmakuParser parser = new AcFunDanmakuParser();
		// BaseDanmakuParser parser = new BiliDanmukuParser();
		IDataSource<?> dataSource = loader.getDataSource();
		parser.load(dataSource);
		return parser;

	}

	private void addHttpDanmaku(boolean islive, String content) {
		BaseDanmaku danmaku = DanmakuFactory
				.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
		// for(int i=0;i<100;i++){
		// }
		danmaku.text = content;
		danmaku.padding = 5;
		danmaku.priority = 1;
		danmaku.isLive = islive;
		danmaku.time = mDanmakuView.getCurrentTime() + 1200;
		danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
		danmaku.textColor = Color.WHITE;
		// danmaku.textShadowColor = Color.WHITE;
		// danmaku.underlineColor = Color.GREEN;
		mDanmakuView.addDanmaku(danmaku);
	}

	private void addDanmaku(boolean islive, String content) {
		if (mParser.getDisplayer() == null) {
			Toast.makeText(this, "正在连接弹幕服务器", Toast.LENGTH_SHORT).show();
			return;
		}

		BaseDanmaku danmaku = DanmakuFactory
				.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
		// for(int i=0;i<100;i++){
		// }
		if (createrName != null && !createrName.equals(""))
			content = createrName + ":" + content;

		danmaku.text = content;
		danmaku.padding = 5;
		danmaku.priority = 1;
		danmaku.isLive = islive;
		danmaku.time = mDanmakuView.getCurrentTime() + 1200;
		danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
		danmaku.textColor = Color.WHITE;
		// danmaku.textShadowColor = Color.WHITE;
		// danmaku.underlineColor = Color.GREEN;
		// danmaku.borderColor = Color.GREEN;
		mDanmakuView.addDanmaku(danmaku);

		doPullDate("2011", new MCHttpCallBack() {
			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				CPorgressDialog.hideProgressDialog();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						ed_comment.setText("");
					} else {
						Toast.makeText(MatchTanmuActivity.this,
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
			}
		});

	}

	private void doPullDate(String action, MCHttpCallBack listen) {
		// Map<String, String> parameters = new HashMap<String, String>();
		// parameters.put("app_action", action);
		// parameters.put("app_platform", "0");
		// parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
		// parameters.put("u_match_id", match_id);
		// if (action.equals("2012"))
		// parameters.put("u_start_id", max_id);
		// else if (action.equals("2011"))
		// parameters.put("u_text", ed_comment.getText().toString());
		//
		// HttpPostUtil mHttpPostUtil = null;
		// try {
		// AppConfig.getInstance().addSign(parameters);
		// mHttpPostUtil = AppConfig.getInstance()
		// .makeHttpPostUtil(parameters);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// // WEB request and deal the listener
		// new WebRequestUtil(mHttpPostUtil).executeWithOutCache(listen);
		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		if (action.equals("2012")) {
			pair.add(new BasicNameValuePair("u_match_id", match_id));
			pair.add(new BasicNameValuePair("u_start_id", max_id));
		} else if (action.equals("2011")) {
			pair.add(new BasicNameValuePair("u_match_id", match_id));
			pair.add(new BasicNameValuePair("u_text", ed_comment.getText()
					.toString()));
		}
		new WebRequestUtil().execute(false,
				AppConfig.getInstance().makeUrl(Integer.parseInt(action)),
				pair, listen);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isUpdate = false;
	}
}
