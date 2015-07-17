package cn.kangeqiu.kq;

import android.app.Activity;
import android.os.Bundle;

import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

public class AgentActivity extends Activity {

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		TCAgent.onResume(this);

	}



	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		TCAgent.onPause(this);
	}
}
