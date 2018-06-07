package com.hc.wxstcdemo.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	private static Toast toast;
	public static void toast(Context context, String text) {
		if (toast == null) {
			toast = Toast.makeText(context, text, 0);
		}
		toast.setText(text);
		toast.show();
	}

}
