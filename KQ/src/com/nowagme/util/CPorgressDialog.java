/**
 * 
 */
package com.nowagme.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/**
 * @author Rubby
 * 
 */
public class CPorgressDialog {

	private static ProgressDialog pdialog;

	public static boolean isProgressDialogShow() {
		if (pdialog == null)
			return false;

		return pdialog.isShowing();
	}

	public static void showProgressDialog(Context context) {
		String message = "正在加载中，请稍后...";
		showProgressDialog(message, context, Cancel);
	}

	public static void showProgressDialog(String message, Context context) {
		showProgressDialog(message, context, Cancel);
	}

	public static void showProgressDialog(String message, Context context,
			OnCancelListener cancel) {
		if (pdialog == null) {
			pdialog = new ProgressDialog(context);
			pdialog.setCanceledOnTouchOutside(false);
			pdialog.setOnCancelListener(cancel);
		}

		if (!pdialog.isShowing()) {
			pdialog = new ProgressDialog(context);
			pdialog.setCanceledOnTouchOutside(false);
			pdialog.setMessage(message);
			pdialog.setOnCancelListener(cancel);
			pdialog.show();
		}
	}

	public static void exchangeProgressDialogMessage(String message) {
		if (pdialog != null) {
			pdialog.setMessage(message);
		}
	}

	public static void hideProgressDialog() {
		if (pdialog != null && pdialog.isShowing()) {
			pdialog.dismiss();
			pdialog = null;
		}
	}

	static OnCancelListener Cancel = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface dialog) {
			hideProgressDialog();
		}
	};
}
