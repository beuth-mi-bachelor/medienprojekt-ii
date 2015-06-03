package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import de.beuth_hochschule.Schabuu.R;
import de.beuth_hochschule.Schabuu.ui.SurfacePlayerView;
import de.beuth_hochschule.Schabuu.util.StreamingUtils;

public class GameAvActivity extends Activity  {

    private String license_stream = "nlic:1.2:LiveEnc:1.1:LivePlg=1,H264ENC=1,MP4=1,RTMPsrc=1,RtmpMsg=1,RTMPm=4,RTMPx=3,Resz=1,Demo=1,Ic=1,NoMsg=1:adr:20150429,20151213::0:0:nanocosmos-471231-28:ncpt:28cd49a163eaf61a48484c9e17a5d808";
    private String serverUrl = "rtmp://ws2.nanocosmos.net/live";
    private String streamName = "Schabuu2";

    private static String authUser = "";
    private static String authPass = "";

    private static final String LOG_TAG = "GameAvActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gamescreen_av);


        StreamingUtils utils = new StreamingUtils(serverUrl,streamName,license_stream,authUser,authPass,(SurfacePlayerView) findViewById(R.id.view),getApplicationContext());
        utils.toggleStreaming();
    }


}
