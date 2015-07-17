package cn.kangeqiu.kq;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import cn.kangeqiu.kq.activity.MainActivity;
import cn.kangeqiu.kq.utils.CommonUtils;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.EasyUtils;
import com.jingyi.MiChat.manager.MCThemeManager;
import com.jingyi.MiChat.utils.SystemBarTintUtil;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseSimpleActivity extends Activity {
	public int mTheme;
	private View nightView;
	protected SystemBarTintUtil mTintUtil;

	private static final int notifiId = 11;
	protected NotificationManager notificationManager ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// setNightDayTheme();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
				&& getTranslucentStatus()) {
			setTranslucentStatus(true);
			mTintUtil = new SystemBarTintUtil(this);
			mTintUtil
					.setStatusBarTintColor(getAttrColor(R.attr.top_nav_background));
			mTintUtil.setStatusBarTintEnabled(true);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		// onresume时，取消notification显示
		EMChatManager.getInstance().activityResumed();
		// umeng
		MobclickAgent.onResume(this);
		TCAgent.onResume(this);

		mTheme = MCThemeManager.getQDTheme();
		setNight(mTheme);
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		TCAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// umeng
		// MobclickAgent.onPause(this);
	}

	/**
	 * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下 如果不需要，注释掉即可
	 * 
	 * @param message
	 */
	protected void notifyNewMessage(EMMessage message) {
		// 如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
		// 以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
		if (!EasyUtils.isAppRunningForeground(this)) {
			return;
		}

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(getApplicationInfo().icon)
				.setWhen(System.currentTimeMillis()).setAutoCancel(true);

		String ticker = CommonUtils.getMessageDigest(message, this);
		String st = getResources().getString(R.string.expression);
		if (message.getType() == Type.TXT)
			ticker = ticker.replaceAll("\\[.{2,3}\\]", st);
		// 设置状态栏提示
		mBuilder.setTicker(message.getFrom() + ": " + ticker);

		// 必须设置pendingintent，否则在2.3的机器上会有bug
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, notifiId,
				intent, PendingIntent.FLAG_ONE_SHOT);
		mBuilder.setContentIntent(pendingIntent);

		Notification notification = mBuilder.build();
		notificationManager.notify(notifiId, notification);
		notificationManager.cancel(notifiId);
	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}

	public void setNightDayTheme() {
		mTheme = MCThemeManager.getQDTheme();
		mTheme = mTheme == 1 ? 0 : 1;
		MCThemeManager.setQDTheme(mTheme);
		setNight(mTheme);
	}

	private void setNight(int theme) {
		ViewGroup vg = (ViewGroup) getWindow().getDecorView();
		if (theme == 1) {
			if (nightView == null) {
				nightView = new View(this);
				nightView.setBackgroundColor(0xb6000000);
			}
			if (nightView.getParent() == null)
				vg.addView(nightView, new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			if (nightView != null)
				vg.removeView(nightView);
		}

	}

	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);

		if (mTintUtil != null) {
			if (on) {
				mTintUtil.setStatusBarTintEnabled(true);
			} else {
				mTintUtil.setStatusBarTintEnabled(false);
			}
		}
	}

	protected boolean getTranslucentStatus() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			int themeResId = packageInfo.applicationInfo.theme;
			if (themeResId == R.style.QDNoTransparentTheme)
				return false;
			return true;
		} catch (Exception e) {
			return true;
		}
	}

	public Drawable getAttrDrawable(int attrId) {
		TypedArray a = obtainStyledAttributes(new int[] { attrId });
		return a.getDrawable(0);
	}

	public int getAttrColor(int attrId) {
		TypedArray a = obtainStyledAttributes(new int[] { attrId });
		return a.getColor(0, 0);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
