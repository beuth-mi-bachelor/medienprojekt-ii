package net.nanocosmos.PlayerActivity.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import net.nanocosmos.NanostreamPlayerTest.R;
import net.nanocosmos.PlayerActivity.ui.SurfacePlayerView;
import net.nanocosmos.PlayerActivity.util.RecievingUtils;
import net.nanocosmos.PlayerActivity.util.StreamingUtils;

public class GameActivity extends Activity  {
    /*
    private static final String license = "nlic:1.2:LiveEnc:3.0:LvApp=1,LivePlg=1,H264DEC=1,H264ENC=1,RTMPsrc=1,RtmpMsg=1,RTMPx=3,NoMsg=1,Ic=0:adr,ios:20150409,20150707::0:0:smartfrog-431775-1:ncpt:90ddf42ffe204d9e1e6ac99e9df92aba";
    private static final String strStreamUrl = "rtmps://55087e44b8b38.streamlock.net/vod";
    private static final String strStreamname = "mp4:sync.mp4";
*/

    private String license_stream = "nlic:1.2:LiveEnc:1.1:LivePlg=1,H264ENC=1,MP4=1,RTMPsrc=1,RtmpMsg=1,RTMPm=4,RTMPx=3,Resz=1,Demo=1,Ic=1,NoMsg=1:adr:20150429,20151213::0:0:nanocosmos-471231-28:ncpt:28cd49a163eaf61a48484c9e17a5d808";
    private String serverUrl = "rtmp://ws2.nanocosmos.net/live";
    private String streamName = "Schabuu2";

    private static String authUser = "";
    private static String authPass = "";

    private static final String LOG_TAG = "GameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game_screen_guesser);
        /*
        RecievingUtils utils = new RecievingUtils(this,license,strStreamUrl,strStreamname,authUser,authPass);
        SurfacePlayerView surfaceView = (SurfacePlayerView) findViewById(R.id.view);
        surfaceView.getHolder().addCallback(utils.GetPlayer());
        utils.StartPlayer();
        */
        StreamingUtils blub = new StreamingUtils(serverUrl,streamName,license_stream,authUser,authPass,(SurfacePlayerView) findViewById(R.id.view),getApplicationContext());
    }
}
