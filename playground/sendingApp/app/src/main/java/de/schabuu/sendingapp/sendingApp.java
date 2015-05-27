package de.schabuu.sendingapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by Felix on 27.05.15.
 */
public class sendingApp extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        sendingApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return sendingApp.context;
    }
}
