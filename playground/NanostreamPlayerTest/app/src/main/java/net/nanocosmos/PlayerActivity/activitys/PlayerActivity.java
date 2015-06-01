package net.nanocosmos.PlayerActivity.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;

import net.nanocosmos.NanostreamPlayerTest.R;
import net.nanocosmos.PlayerActivity.ui.SurfacePlayerView;
import net.nanocosmos.PlayerActivity.util.PreferenceEnum;
import net.nanocosmos.PlayerActivity.util.VideoCamera;
import net.nanocosmos.nanoStream.streamer.AdaptiveBitrateControlSettings;
import net.nanocosmos.nanoStream.streamer.Logging;
import net.nanocosmos.nanoStream.streamer.NanostreamEvent;
import net.nanocosmos.nanoStream.streamer.NanostreamEventListener;
import net.nanocosmos.nanoStream.streamer.NanostreamException;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayer;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayer.PlayerEventListener;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayer.PlayerSettings;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayerImpl;
import net.nanocosmos.nanoStream.streamer.nanoStream;

public class PlayerActivity extends Activity implements PlayerEventListener , NanostreamEventListener {

    //Sending Attributes
    private nanoStream streamLib;
    private int width = 640;
    private int height = 480;
    private int BIT_RATE = 500000;
    private int FRAME_RATE = 15;
    private String license_stream = "nlic:1.2:LiveEnc:1.1:LivePlg=1,H264ENC=1,MP4=1,RTMPsrc=1,RtmpMsg=1,RTMPm=4,RTMPx=3,Resz=1,Demo=1,Ic=1,NoMsg=1:adr:20150429,20151213::0:0:nanocosmos-471231-28:ncpt:28cd49a163eaf61a48484c9e17a5d808";
    private String serverUrl = "rtmp://ws2.nanocosmos.net/live";
    private String streamName = "Schabuu2";
    private AdaptiveBitrateControlSettings.AdaptiveBitrateControlMode abcMode = AdaptiveBitrateControlSettings.AdaptiveBitrateControlMode.DISABLED;
    private VideoCamera mVideoCam = null;
    private nanoStream.VideoSourceType vsType = nanoStream.VideoSourceType.EXTERNAL;
    private SharedPreferences getPrefs;

    //Recieving Attributes
    private RetainedFragment dataFragment;
    private LinearLayout root;
    private MediaController controller;
    private static final String license = "nlic:1.2:LiveEnc:3.0:LvApp=1,LivePlg=1,H264DEC=1,H264ENC=1,RTMPsrc=1,RtmpMsg=1,RTMPx=3,NoMsg=1,Ic=0:adr,ios:20150409,20150707::0:0:smartfrog-431775-1:ncpt:90ddf42ffe204d9e1e6ac99e9df92aba";
    private static final String strStreamUrl = "rtmp://ws2.nanocosmos.net/live";
    private static final String strStreamname = "Schabuu2";

    private static String authUser = "";
    private static String authPass = "";

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

        final Button button = (Button) findViewById(R.id.button_stream);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toggleStreaming();
            }
        });
        //getPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        createRecievingStuff((SurfacePlayerView) findViewById(R.id.cam_view));

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

    @Override
    public void onNanostreamEvent(NanostreamEvent nanostreamEvent) {
        this.runOnUiThread(new NotificationRunable(nanostreamEvent));
    }

    private class NotificationRunable implements Runnable
    {
        private NanostreamEvent m_event;

        public NotificationRunable(NanostreamEvent event)
        {
            m_event = event;
        }

        @Override
        public void run()
        {
            if (m_event.GetType() != NanostreamEvent.TYPE_RTMP_QUALITY)
            {
                Toast.makeText(getApplicationContext(), m_event.GetDescription(), Toast.LENGTH_SHORT).show();
            }
        }
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
            //Log.d("TEST", "EVENT");
            if (instance instanceof NanostreamPlayerImpl) {
                this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        root.requestLayout();
                        //Log.d("TEST", "LAYOUT REQUESTED");

                    }
                });
            }
        }

    }

    public void toggleStreaming() {
        if (streamLib == null) {
            Toast.makeText(getApplicationContext(), "nanoStream failed to initialize", Toast.LENGTH_LONG).show();
            return;
        }

        if (!streamLib.hasState(nanoStream.EncoderState.RUNNING)) {
            if (!isNetworkAvailable()) {
                Toast.makeText(getApplicationContext(), "Cannot find available network. Please check your device settings.", Toast.LENGTH_LONG).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Starting...", Toast.LENGTH_SHORT).show();
            }
            if (streamLib.hasState(nanoStream.EncoderState.STOPPED) || streamLib.hasState(nanoStream.EncoderState.CREATED)) {
                try {
                    Log.d("StreamToogle", "init");
                    streamLib.init();
                } catch (NanostreamException en) {
                    Toast.makeText(getApplicationContext(), en.toString(), Toast.LENGTH_LONG).show();
                    return;
                }
            }

            try {
                streamLib.start();
            } catch (NanostreamException en) {
                Toast.makeText(getApplicationContext(), en.toString(), Toast.LENGTH_LONG).show();
                return;
            }


        } else {
            Toast.makeText(getApplicationContext(), "Stopping...", Toast.LENGTH_SHORT).show();

            streamLib.stop();

        }
    }

    public boolean isNetworkAvailable()
    {
        Context context = getApplicationContext();

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null)
            {
                for (int i = 0; i < info.length; i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void createRecievingStuff(SurfacePlayerView surface){
        loadPreferences();

        AdaptiveBitrateControlSettings abcSettings = new AdaptiveBitrateControlSettings(abcMode);
        Logging.LogSettings logSettings = new Logging.LogSettings(Logging.LogLevel.VERBOSE, 1);

        try
        {
            streamLib = new nanoStream(vsType, width, height, BIT_RATE, FRAME_RATE, surface.getHolder(), 2, license, serverUrl, streamName, authUser, authPass,
                    this, abcSettings, logSettings);
            mVideoCam = new VideoCamera(width, height, FRAME_RATE, surface.getHolder());
            mVideoCam.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            streamLib.setVideoSource(mVideoCam);
        } catch (NanostreamException en)
        {
            Toast.makeText(getApplicationContext(), en.toString(), Toast.LENGTH_LONG).show();
        }
        try
        {
            if (streamLib != null)
            {
                streamLib.init();
            }
        } catch (NanostreamException en)
        {
            Toast.makeText(getApplicationContext(), en.toString(), Toast.LENGTH_LONG).show();
        }

        //PreferenceManager.getDefaultSharedPreferences(nanoStreamApp.getAppContext()).registerOnSharedPreferenceChangeListener(new PreferenceChangeListener());

    }


    private void loadPreferences()
    {
        //SharedPreferences prefs = getPrefs;
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(nanoStreamApp.getAppContext());
        BIT_RATE = 500000;
        FRAME_RATE = 15;

        width = 640;
        height = 480;


		/*
		 * abcMinBitrate = AdaptiveBitrateControlSettings.DEFAULT_MIN_BITRATE;
		 * //
		 * Integer.parseInt(prefs.getString(PreferenceEnum.PREF_ABC_MIN_BITRATE
		 * .getValue(), String.valueOf(abcMinBitrate))); abcMinFramerate =
		 * AdaptiveBitrateControlSettings.DEFAULT_MIN_FRAMERATE;
		 * //Integer.parseInt
		 * (prefs.getString(PreferenceEnum.PREF_ABC_MIN_FRAMERATE.getValue(),
		 * String.valueOf(abcMinFramerate))); abcFlushBufferThreshold =
		 * AdaptiveBitrateControlSettings.DEFAULT_FLUSH_BUFFER_THRESHOLD;
		 * //Integer
		 * .parseInt(prefs.getString(PreferenceEnum.PREF_ABC_FLUSH_BUFFER_THRESHOLD
		 * .getValue(), String.valueOf(abcFlushBufferThreshold)));
		 */


                abcMode = AdaptiveBitrateControlSettings.AdaptiveBitrateControlMode.QUALITY_DEGRADE_AND_FRAME_DROP;



    }

    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener
    {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {

            Log.i(this.getClass().getName(), "Preference changed");

            if (PreferenceEnum.PREF_RESOLUTION_KEY.equalsValue(key))
            {
                String size = sharedPreferences.getString(key, "640x480");
                String[] sizes = size.split("x");

                if (sizes.length > 2)
                {
                    throw new RuntimeException(new IllegalArgumentException("Wrong resolution value."));
                }

                width = Integer.parseInt(sizes[0]);
                height = Integer.parseInt(sizes[1]);
            }

            if (PreferenceEnum.PREF_BITRATE_KEY.equalsValue(key))
            {
                String value = sharedPreferences.getString(key, "500000");
                BIT_RATE = Integer.parseInt(value);
            }

            if (PreferenceEnum.PREF_FPS_KEY.equalsValue(key))
            {
                FRAME_RATE = Integer.parseInt(sharedPreferences.getString(key, "15"));
            }

            if (PreferenceEnum.PREF_URI_KEY.equalsValue(key))
            {
                serverUrl = sharedPreferences.getString(PreferenceEnum.PREF_URI_KEY.getValue(), serverUrl);
            }

            if (PreferenceEnum.PREF_CODE_KEY.equalsValue(key))
            {
                streamName = sharedPreferences.getString(PreferenceEnum.PREF_CODE_KEY.getValue(), streamName);
            }

            if (PreferenceEnum.PREF_AUTH_USER_KEY.equalsValue(key))
            {
                authUser = sharedPreferences.getString(PreferenceEnum.PREF_AUTH_USER_KEY.getValue(), authUser);
            }

            if (PreferenceEnum.PREF_AUTH_PASS_KEY.equalsValue(key))
            {
                authPass = sharedPreferences.getString(PreferenceEnum.PREF_AUTH_PASS_KEY.getValue(), authPass);
            }

			/*
			 * if(PreferenceEnum.PREF_ABC_MIN_BITRATE.equalsValue(key)) {
			 * abcMinBitrate =
			 * Integer.parseInt(sharedPreferences.getString(PreferenceEnum
			 * .PREF_ABC_MIN_BITRATE.getValue(),
			 * String.valueOf(abcMinBitrate))); }
			 *
			 * if(PreferenceEnum.PREF_ABC_MIN_FRAMERATE.equalsValue(key)) {
			 * abcMinFramerate =
			 * Integer.parseInt(sharedPreferences.getString(PreferenceEnum
			 * .PREF_ABC_MIN_FRAMERATE.getValue(),
			 * String.valueOf(abcMinFramerate))); }
			 *
			 * if(PreferenceEnum.PREF_ABC_FLUSH_BUFFER_THRESHOLD.equalsValue(key)
			 * ) { abcFlushBufferThreshold =
			 * Integer.parseInt(sharedPreferences.getString
			 * (PreferenceEnum.PREF_ABC_FLUSH_BUFFER_THRESHOLD.getValue(),
			 * String.valueOf(abcFlushBufferThreshold))); }
			 */

            if (PreferenceEnum.PREF_ABC_MODE.equalsValue(key))
            {
                switch (Integer.parseInt(sharedPreferences.getString(PreferenceEnum.PREF_ABC_MODE.getValue(), String.valueOf(0))))
                {
                    case 0:
                    {
                        abcMode = AdaptiveBitrateControlSettings.AdaptiveBitrateControlMode.DISABLED;
                        break;
                    }
                    case 1:
                    {
                        abcMode = AdaptiveBitrateControlSettings.AdaptiveBitrateControlMode.QUALITY_DEGRADE;
                        break;
                    }
                    case 2:
                    {
                        abcMode = AdaptiveBitrateControlSettings.AdaptiveBitrateControlMode.FRAME_DROP;
                        break;
                    }
                    case 3:
                    {
                        abcMode = AdaptiveBitrateControlSettings.AdaptiveBitrateControlMode.QUALITY_DEGRADE_AND_FRAME_DROP;
                        break;
                    }
                    default:
                    {
                        abcMode = AdaptiveBitrateControlSettings.AdaptiveBitrateControlMode.DISABLED;
                        break;
                    }
                }
            }
        }
    }
}
