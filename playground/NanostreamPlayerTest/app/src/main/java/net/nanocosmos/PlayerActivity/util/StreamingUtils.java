package net.nanocosmos.PlayerActivity.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;

import net.nanocosmos.PlayerActivity.ui.SurfacePlayerView;
import net.nanocosmos.nanoStream.streamer.AdaptiveBitrateControlSettings;
import net.nanocosmos.nanoStream.streamer.Logging;
import net.nanocosmos.nanoStream.streamer.NanostreamEvent;
import net.nanocosmos.nanoStream.streamer.NanostreamEventListener;
import net.nanocosmos.nanoStream.streamer.NanostreamException;
import net.nanocosmos.nanoStream.streamer.nanoStream;

/**
 * Created by Sprotte on 01.06.15.
 */
public class StreamingUtils extends Activity implements NanostreamEventListener {
    private RecievingUtils.RetainedFragment dataFragment ;
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
    private Context context;

    private static final String LOG_TAG = "StreamingUtilsActivity";

    public StreamingUtils(RecievingUtils.RetainedFragment dataFragment,String license,String authUser,String authPass,SurfacePlayerView surfacePlayerView,Context context) {
        this.dataFragment = dataFragment;
        this.context = context;
        loadPreferences();

        AdaptiveBitrateControlSettings abcSettings = new AdaptiveBitrateControlSettings(abcMode);
        Logging.LogSettings logSettings = new Logging.LogSettings(Logging.LogLevel.VERBOSE, 1);

        try
        {
            streamLib = new nanoStream(vsType, width, height, BIT_RATE, FRAME_RATE, surfacePlayerView.getHolder(), 2, license, serverUrl, streamName, authUser, authPass,
                    (net.nanocosmos.nanoStream.streamer.NanostreamEventListener) this, abcSettings, logSettings);
            mVideoCam = new VideoCamera(width, height, FRAME_RATE, surfacePlayerView.getHolder());
            mVideoCam.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            streamLib.setVideoSource(mVideoCam);
        } catch (NanostreamException en)
        {
            Toast.makeText(context, en.toString(), Toast.LENGTH_LONG).show();
        }
        try
        {
            if (streamLib != null)
            {
                streamLib.init();
            }
        } catch (NanostreamException en)
        {
            Toast.makeText(context, en.toString(), Toast.LENGTH_LONG).show();
        }
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
                Toast.makeText(context, m_event.GetDescription(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
