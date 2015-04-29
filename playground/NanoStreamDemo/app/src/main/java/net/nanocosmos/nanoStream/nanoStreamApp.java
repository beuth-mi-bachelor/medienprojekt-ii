package net.nanocosmos.nanoStream;

import android.app.Application;
import android.content.Context;

public class nanoStreamApp extends Application {
	private static Context context;
	
	public void onCreate() {
		super.onCreate();
		nanoStreamApp.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return nanoStreamApp.context;
	}

}
