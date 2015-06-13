package de.beuth_hochschule.Schabuu.util;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import de.beuth_hochschule.Schabuu.ui.SurfacePlayerView;

import net.nanocosmos.nanoStream.streamer.AdaptiveBitrateControlSettings;
import net.nanocosmos.nanoStream.streamer.AspectRatio;
import net.nanocosmos.nanoStream.streamer.AudioSettings;
import net.nanocosmos.nanoStream.streamer.Logging;
import net.nanocosmos.nanoStream.streamer.NanostreamEvent;
import net.nanocosmos.nanoStream.streamer.NanostreamEventListener;
import net.nanocosmos.nanoStream.streamer.NanostreamException;
import net.nanocosmos.nanoStream.streamer.Resolution;
import net.nanocosmos.nanoStream.streamer.Rotation;
import net.nanocosmos.nanoStream.streamer.VideoSettings;
import net.nanocosmos.nanoStream.streamer.nanoStream;

public class StreamingUtils extends Activity implements NanostreamEventListener {
    //Sending Attributes
    private nanoStream streamLib;
    private int width = 320;
    private int height = 240;
    private int BIT_RATE = 250000;
    private int FRAME_RATE = 10;
    private AdaptiveBitrateControlSettings.AdaptiveBitrateControlMode abcMode = AdaptiveBitrateControlSettings.AdaptiveBitrateControlMode.QUALITY_DEGRADE_AND_FRAME_DROP;
    private VideoCamera mVideoCam = null;
    private nanoStream.VideoSourceType vsType = nanoStream.VideoSourceType.EXTERNAL;
    private Context context;

    private static final String LOG_TAG = "StreamingUtilsActivity";

    public StreamingUtils(String serverUrl, String streamName, String license, String authUser, String authPass, SurfacePlayerView surfacePlayerView, Context context,boolean audio) {
        this.context = context;

        AdaptiveBitrateControlSettings abcSettings = new AdaptiveBitrateControlSettings(abcMode);
        Logging.LogSettings logSettings = new Logging.LogSettings(Logging.LogLevel.VERBOSE, 1);

        try {

            VideoSettings vSettings = new VideoSettings();
            vSettings.setAspectRatio(AspectRatio.RATIO_KEEP_INPUT);
            vSettings.setBitrate(BIT_RATE);
            vSettings.setFrameRate(FRAME_RATE);
            vSettings.setResolution(new Resolution(width, height));

            vSettings.setVideoSourceType(nanoStream.VideoSourceType.EXTERNAL);

            AudioSettings aSettings = new AudioSettings(1, 32 * 1024, 44100);



            if(!audio){
                streamLib = new nanoStream(true, vSettings, surfacePlayerView.getHolder(), true, aSettings, license, serverUrl, streamName, authUser, authPass, this,
                        abcSettings, logSettings, true, false, "");


                mVideoCam = new VideoCamera(width, height, FRAME_RATE, surfacePlayerView.getHolder());
                mVideoCam.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                mVideoCam.setCameraOrientation(90);
                streamLib.setVideoSource(mVideoCam);
                streamLib.setRotation(Rotation.ROTATION_270);

            }else{
                streamLib = new nanoStream(false, vSettings, null, true, aSettings, license, serverUrl, streamName, authUser, authPass, this,
                        abcSettings, logSettings, true, false, "");

//                mVideoCam = new VideoCamera(width, height, FRAME_RATE, surfacePlayerView.getHolder());
//                mVideoCam.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
//                mVideoCam.setCameraOrientation(90);
//                streamLib.setVideoSource(mVideoCam);
            }



        } catch (NanostreamException en) {
            Toast.makeText(context, en.toString(), Toast.LENGTH_LONG).show();
        }
//        try {
//            if (streamLib != null) {
//                streamLib.init();
//            }
//        } catch (NanostreamException en) {
//            Toast.makeText(context, en.toString(), Toast.LENGTH_LONG).show();
//        }
    }

    public void toggleStreaming() {

        if (streamLib == null) {
            Toast.makeText(getApplicationContext(), "nanoStream failed to initialize", Toast.LENGTH_LONG).show();
            return;
        }

        if (!streamLib.hasState(nanoStream.EncoderState.RUNNING)) {
            if (!isNetworkAvailable()) {
                Toast.makeText(context, "Cannot find available network. Please check your device settings.", Toast.LENGTH_LONG).show();
                return;
            } else {
                Toast.makeText(context, "Starting...", Toast.LENGTH_SHORT).show();
            }
            if (streamLib.hasState(nanoStream.EncoderState.STOPPED) || streamLib.hasState(nanoStream.EncoderState.CREATED)) {
                try {
                    Log.d("StreamToogle", "init");
                    streamLib.init();
                } catch (NanostreamException en) {
                    Toast.makeText(context, en.toString(), Toast.LENGTH_LONG).show();
                    return;
                }
            }

            try {
                streamLib.start();
            } catch (NanostreamException en) {
                Toast.makeText(context, en.toString(), Toast.LENGTH_LONG).show();
                return;
            }


        } else {
            Toast.makeText(context, "Stopping...", Toast.LENGTH_SHORT).show();

            streamLib.stop();

        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onNanostreamEvent(NanostreamEvent nanostreamEvent) {
        this.runOnUiThread(new NotificationRunable(nanostreamEvent));
    }

    private class NotificationRunable implements Runnable {
        private NanostreamEvent m_event;

        public NotificationRunable(NanostreamEvent event) {
            m_event = event;
        }

        @Override
        public void run() {
            if (m_event.GetType() != NanostreamEvent.TYPE_RTMP_QUALITY) {
                Toast.makeText(context, m_event.GetDescription(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}