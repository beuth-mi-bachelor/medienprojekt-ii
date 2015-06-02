package net.nanocosmos.PlayerActivity.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import net.nanocosmos.NanostreamPlayerTest.R;
import net.nanocosmos.PlayerActivity.ui.SurfacePlayerView;
import net.nanocosmos.PlayerActivity.util.RecievingUtils;
import net.nanocosmos.PlayerActivity.util.StreamingUtils;

public class GameAvActivity extends Activity  {

    private static final String license = "nlic:1.2:LiveEnc:3.0:LvApp=1,LivePlg=1,H264DEC=1,H264ENC=1,RTMPsrc=1,RtmpMsg=1,RTMPx=3,NoMsg=1,Ic=0:adr,ios:20150409,20150707::0:0:smartfrog-431775-1:ncpt:90ddf42ffe204d9e1e6ac99e9df92aba";
    private static final String strStreamUrl = "rtmps://55087e44b8b38.streamlock.net/vod";
    private static final String strStreamname = "mp4:sync.mp4";

    private static String authUser = "";
    private static String authPass = "";

    private static final String LOG_TAG = "GameAvActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game_screen_guesser);


        StreamingUtils utils = new StreamingUtils(license,authUser,authPass,null,getApplicationContext());

    }
}
