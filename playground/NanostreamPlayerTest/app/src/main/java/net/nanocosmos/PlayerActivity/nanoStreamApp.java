package net.nanocosmos.PlayerActivity;

/**
 * Created by Sprotte on 28.05.15.
 */
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
