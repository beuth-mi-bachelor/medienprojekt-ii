package de.beuth_hochschule.Schabuu.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.beuth_hochschule.Schabuu.R;
import de.beuth_hochschule.Schabuu.data.ServerConnector;
import de.beuth_hochschule.Schabuu.data.ServerConnectorImplementation;
import de.beuth_hochschule.Schabuu.ui.SurfacePlayerView;
import de.beuth_hochschule.Schabuu.util.RecievingUtils;
import de.beuth_hochschule.Schabuu.util.StreamingUtils;

public class GameAvActivity extends Activity {

    private String license_stream = "nlic:1.2:LiveEnc:1.1:LivePlg=1,H264ENC=1,MP4=1,RTMPsrc=1,RtmpMsg=1,RTMPm=4,RTMPx=3,Resz=1,Demo=1,Ic=1,NoMsg=1:adr:20150429,20151213::0:0:nanocosmos-471231-28:ncpt:28cd49a163eaf61a48484c9e17a5d808";
    private String serverUrl = "rtmp://ws2.nanocosmos.net/live";
    private String streamName = "foobar";


    private static final String license = "nlic:1.2:LiveEnc:3.0:LvApp=1,LivePlg=1,H264DEC=1,H264ENC=1,RTMPsrc=1,RtmpMsg=1,RTMPx=3,NoMsg=1,Ic=0:adr,ios:20150409,20150707::0:0:smartfrog-431775-1:ncpt:90ddf42ffe204d9e1e6ac99e9df92aba";
    private static final String strStreamUrl = "rtmp://ws2.nanocosmos.net/live";
    private String strStreamname = "PaulTest";

    private static String authUser = "";
    private static String authPass = "";



    private static final String LOG_TAG = "GameAvActivity";

    private ServerConnector _server;

    private StreamingUtils utils = null;

    private TextView descriptionTextView;
    private TextView teamTextView;
    private ImageView iconView;
    private LinearLayout loadingBackground;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Typeface geoBold = Typeface.createFromAsset(getAssets(), "font/geomanist_font_family/Geomanist-Bold.otf");
        Typeface awesome = Typeface.createFromAsset(getAssets(), "font/font_awesome/FontAwesome.otf");
         intent = getIntent();
        _server = ServerConnectorImplementation.getInstance();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gamescreen_av);


        TextView word_one = (TextView) findViewById(R.id.word_one);
        TextView word_two = (TextView) findViewById(R.id.word_two);
        TextView word_three = (TextView) findViewById(R.id.word_three);
        TextView word_four = (TextView) findViewById(R.id.word_four);
        TextView word_five = (TextView) findViewById(R.id.word_five);
        TextView word_six = (TextView) findViewById(R.id.word_six);
        TextView score1 = (TextView) findViewById(R.id.score1);
        TextView score2 = (TextView) findViewById(R.id.score2);
        TextView time_left = (TextView) findViewById(R.id.time_left);
        TextView solution = (TextView) findViewById(R.id.loesungswort);
        TextView player_name = (TextView) findViewById(R.id.player_name);

        word_one.setTypeface(geoBold);
        word_two.setTypeface(geoBold);
        word_three.setTypeface(geoBold);
        word_four.setTypeface(geoBold);
        word_five.setTypeface(geoBold);
        word_six.setTypeface(geoBold);
        score1.setTypeface(geoBold);
        score2.setTypeface(geoBold);
        time_left.setTypeface(geoBold);
        solution.setTypeface(geoBold);
        player_name.setTypeface(geoBold);

        player_name.setAlpha(0.5f);

        word_one.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        word_two.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        word_three.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        word_four.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        word_five.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        word_six.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        score1.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        score2.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        time_left.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));
        solution.setShadowLayer(1, 1, 1, Color.parseColor("#ff333333"));

        time_left.setText("00:00");

        /*

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!STREAM"+intent.getStringExtra("STREAM_VIDEO"));
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!AUDIO"+intent.getStringExtra("STREAM_AUDIO"));
       if(intent.getStringExtra("MODE").equals("CAM")){
           utils = new StreamingUtils(serverUrl, intent.getStringExtra("STREAM_VIDEO"), license, authUser, authPass, (SurfacePlayerView) findViewById(R.id.view), getApplicationContext(),false);
           utils.toggleStreaming();
       }
       if(intent.getStringExtra("MODE").equals("AUDIO")){
           //utils = new StreamingUtils(serverUrl, intent.getStringExtra("STREAM_AUDIO"), license, authUser, authPass, (SurfacePlayerView) findViewById(R.id.view_audio), getApplicationContext(),true);
           //utils.toggleStreaming();


           RecievingUtils utils = new RecievingUtils(this, license, strStreamUrl,intent.getStringExtra("STREAM_VIDEO") , authUser, authPass);
           SurfacePlayerView surfaceView = (SurfacePlayerView) findViewById(R.id.view);
           surfaceView.getHolder().addCallback(utils.GetPlayer());
           utils.StartPlayer();


       }
       */

    }
    private void createLoadingScreen() {
        descriptionTextView = (TextView) findViewById(R.id.description);
        teamTextView = (TextView) findViewById(R.id.team_value);
        iconView = (ImageView) findViewById(R.id.imageView);
        loadingBackground = (LinearLayout) findViewById(R.id.loading_screen);
        descriptionTextView.setText(getResources().getString(R.string.guesser_description));

        if (intent.getStringExtra("TEAM") != null && intent.getStringExtra("TEAM").equals("0")) {
            loadingBackground.setBackgroundColor(getResources().getColor(R.color.schabuu_green));
        } else
            loadingBackground.setBackgroundColor(getResources().getColor(R.color.schabuu_blue));
        if (intent.getStringExtra("TEAM") != null) {
            teamTextView.append(intent.getStringExtra("TEAM"));
        }
        iconView.setImageDrawable(getResources().getDrawable(R.drawable.guesser_icon));
        if (intent.getStringExtra("STREAM_VIDEO") != null) {
            strStreamname = intent.getStringExtra("STREAM_VIDEO");
        }
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
