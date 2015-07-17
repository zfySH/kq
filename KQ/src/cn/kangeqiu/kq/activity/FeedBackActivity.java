package cn.kangeqiu.kq.activity;

import android.os.Bundle;
import cn.kangeqiu.kq.BaseSimpleActivity;

public class FeedBackActivity extends BaseSimpleActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.abc_activity_myself_feedback);
		
	}

	// public void back(View view) {
	// finish();
	// }

	// public void save(View view) {
	// progressShow = true;
	// pd = new ProgressDialog(this);
	// pd.setCanceledOnTouchOutside(false);
	// pd.setOnCancelListener(new OnCancelListener() {
	// @Override
	// public void onCancel(DialogInterface dialog) {
	// progressShow = false;
	// }
	// });
	// pd.setMessage(getString(R.string.abc_data_loding));
	// pd.show();
	// doPullDate("", new WebRequestUtilListener() {
	//
	// @Override
	// public void onSucces(Map<String, Object> data) {
	// pd.dismiss();
	// Toast.makeText(FeedBackActivity.this, "提交成功",
	// Toast.LENGTH_SHORT).show();
	// }
	//
	// @Override
	// public void onFail(Map<String, Object> data) {
	// pd.dismiss();
	// Toast.makeText(FeedBackActivity.this,
	// String.valueOf(data.get("message")), Toast.LENGTH_SHORT)
	// .show();
	// }
	//
	// @Override
	// public void onError() {
	// pd.dismiss();
	// }
	// });
	// }
	//
	// private void doPullDate(String action, WebRequestUtilListener listen) {
	// Map<String, String> parameters = new HashMap<String, String>();
	// parameters.put("app_action", action);
	// parameters.put("app_platform", "0");
	// parameters.put("u_uid", AppConfig.getInstance().getPlayerId() + "");
	// HttpPostUtil mHttpPostUtil = null;
	// try {
	// AppConfig.getInstance().addSign(parameters);
	// mHttpPostUtil = AppConfig.getInstance()
	// .makeHttpPostUtil(parameters);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// // WEB request and deal the listener
	// // new WebRequestUtil(mHttpPostUtil).execute(listen);
	// }
}
