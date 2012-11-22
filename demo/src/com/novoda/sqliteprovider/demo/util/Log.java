package com.novoda.sqliteprovider.demo.util;

public final class Log {

	private static final String TAG = "SQLiteProviderDemo";
	
	public static void i(String msg){
		android.util.Log.i(TAG, msg);
	}

	public static void e(String msg) {
		android.util.Log.e(TAG, msg);
	}
	
}
