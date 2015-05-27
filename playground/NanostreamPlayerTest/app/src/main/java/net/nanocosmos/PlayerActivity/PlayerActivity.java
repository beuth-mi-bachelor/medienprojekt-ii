package net.nanocosmos.PlayerActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.MediaController;

import net.nanocosmos.NanostreamPlayerTest.R;
import net.nanocosmos.nanoStream.streamer.NanostreamEvent;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayer;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayer.PlayerEventListener;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayer.PlayerSettings;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayerImpl;
import net.nanocosmos.nanoStream.streamer.nanoStream;

public class PlayerActivity extends Activity implements PlayerEventListener {

    private RetainedFragment dataFragment;

    // private RelativeLayout root;
    private LinearLayout root;

    private MediaController controller;


    private static final String license = "nlic:1.2:LiveEnc:3.0:LvApp=1,LivePlg=1,H264DEC=1,H264ENC=1,RTMPsrc=1,RtmpMsg=1,RTMPx=3,NoMsg=1,Ic=0:adr,ios:20150409,20150707::0:0:smartfrog-431775-1:ncpt:90ddf42ffe204d9e1e6ac99e9df92aba";

    private static final String strStreamUrl = "rtmps://55087e44b8b38.streamlock.net/vod";
    private static final String strStreamname = "mp4:sync.mp4";

    private static final String authUser = "";
    private static final String authPass = "";

    private static final String LOG_TAG = "PlayerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        dataFragment = (RetainedFragment) fm.findFragmentByTag("data");

        NanostreamPlayer player = null;

        // create the fragment and data the first time
        if (dataFragment == null) {
            // add the fragment
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();

            player = nanoStream.createNanostreamPlayer();

            PlayerSettings settings = player.new PlayerSettings();

            settings.setLicense(license);
            settings.setUrl(strStreamUrl);
            settings.setStreamname(strStreamname);
            settings.setAuthUsername(authUser);
            settings.setAuthPassword(authPass);
            settings.setBufferTimeMs(2000);

            player.setSettings(settings);
            player.setPlayerEventListener(this);

            dataFragment.setData(player);
        } else {
            player = dataFragment.getData();
        }

        SurfacePlayerView surfaceView1 = (SurfacePlayerView) findViewById(R.id.player_view);
        surfaceView1.getHolder().addCallback(player);

        root = (LinearLayout) findViewById(R.id.root_view);

        controller = new MediaController(this, false);
        controller.setAnchorView(root);
        controller.setMediaPlayer(player);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show(5000);
        dataFragment.mPlayer.start();
        return super.onTouchEvent(event);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("ValidFragment")
    public class RetainedFragment extends Fragment {

        // data object we want to retain
        private NanostreamPlayer mPlayer = null;

        // this method is only called once for this fragment
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // retain this fragment
            setRetainInstance(true);
        }

        public void setData(NanostreamPlayer player) {
            this.mPlayer = player;
        }

        public NanostreamPlayer getData() {
            return mPlayer;
        }
    }

    @Override
    public void onPlayerEvent(NanostreamEvent event, NanostreamPlayer instance) {
        if (event.GetCode() == NanostreamEvent.TYPE_RTMP_STATUS && event.GetCode() == NanostreamEvent.CODE_STREAM_STARTED) {
            Log.d("TEST", "EVENT");
            if (instance instanceof NanostreamPlayerImpl) {
                this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        root.requestLayout();
                        Log.d("TEST", "LAYOUT REQUESTED");

                    }
                });
            }
        }

    }
}
