package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import de.beuth_hochschule.Schabuu.R;
import de.beuth_hochschule.Schabuu.data.ServerConnector;
import de.beuth_hochschule.Schabuu.data.ServerConnectorImplementation;
import de.beuth_hochschule.Schabuu.ui.SurfacePlayerView;
import de.beuth_hochschule.Schabuu.util.RecievingUtils;

public class GameActivity extends Activity  {

    private static final String license = "nlic:1.2:LiveEnc:3.0:LvApp=1,LivePlg=1,H264DEC=1,H264ENC=1,RTMPsrc=1,RtmpMsg=1,RTMPx=3,NoMsg=1,Ic=0:adr,ios:20150409,20150707::0:0:smartfrog-431775-1:ncpt:90ddf42ffe204d9e1e6ac99e9df92aba";
    // rtmp://ws2.nanocosmos.net/live
    // rtmps://55087e44b8b38.streamlock.net/vod
    private static final String strStreamUrl = "rtmp://ws2.nanocosmos.net/live";
    private static final String strStreamname = "Schabuu2";

    private static String authUser = "";
    private static String authPass = "";

    private static final String LOG_TAG = "GameActivity";

    private ServerConnector _server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _server = ServerConnectorImplementation.getInstance();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game_screen_guesser);

        RecievingUtils utils = new RecievingUtils(this,license,strStreamUrl,strStreamname,authUser,authPass);
        SurfacePlayerView surfaceView = (SurfacePlayerView) findViewById(R.id.view);
        surfaceView.getHolder().addCallback(utils.GetPlayer());
        utils.StartPlayer();
    }


    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        _server.setPlayerInActive();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        _server.setPlayerActive();
    }
}
