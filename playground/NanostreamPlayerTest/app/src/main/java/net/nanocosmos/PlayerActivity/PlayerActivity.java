package net.nanocosmos.PlayerActivity;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.http.RequestLine;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import net.nanocosmos.nanoStream.streamer.NanostreamEvent;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayer.PlayerEventListener;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayerImpl;
import net.nanocosmos.nanoStream.streamer.nanoStream;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayer;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayer.PlayerSettings;
import net.nanocosmos.nanoStream.streamer.nanoResults;

public class PlayerActivity extends Activity implements PlayerEventListener
{

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
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, 1.0F);

		root = new LinearLayout(this);
		root.setOrientation(LinearLayout.VERTICAL);
		root.setLayoutParams(containerParams);
		root.setBackgroundColor(Color.BLACK);

		// find the retained fragment on activity restarts
		FragmentManager fm = getFragmentManager();
		dataFragment = (RetainedFragment) fm.findFragmentByTag("data");

		NanostreamPlayer player = null;

		// create the fragment and data the first time
		if (dataFragment == null)
		{
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
		} else
		{
			player = dataFragment.getData();
		}

		LinearLayout.LayoutParams surfaceParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, 0.5F);
		surfaceParams1.gravity = Gravity.CENTER;
		surfaceParams1.weight = 0.5f;

		SurfacePlayerView surfaceView1 = new SurfacePlayerView(this);
		surfaceView1.setLayoutParams(surfaceParams1);
		surfaceView1.getHolder().addCallback(player);

		root.addView(surfaceView1);

		controller = new MediaController(this, false);
		controller.setAnchorView(root);
		controller.setMediaPlayer(player);

		setContentView(root);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		controller.show(5000);
		return super.onTouchEvent(event);
	}

	protected void onDestroy()
	{
		super.onDestroy();
	}

	private class SurfacePlayerView extends SurfaceView
	{
		SurfacePlayerView(Context context)
		{
			super(context);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
		{
			int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
			int viewHeight = MeasureSpec.getSize(heightMeasureSpec);

			// TODO: Get aspect ratio from stream
			int videoWidth = 16;
			int videoHeight = 9;
			double aspectScreen = (double) viewWidth / (double) viewHeight;
			double aspectVideo = (double) videoWidth / (double) videoHeight;


			if (aspectScreen > aspectVideo)
			{
				viewWidth = (viewHeight * videoWidth) / videoHeight;
			} else
			{
				viewHeight = (viewWidth * videoHeight) / videoWidth;
			}

			setMeasuredDimension(viewWidth, viewHeight);
		}
	}

	public class RetainedFragment extends Fragment
	{

		// data object we want to retain
		private NanostreamPlayer mPlayer = null;

		// this method is only called once for this fragment
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			// retain this fragment
			setRetainInstance(true);
		}

		public void setData(NanostreamPlayer player)
		{
			this.mPlayer = player;
		}

		public NanostreamPlayer getData()
		{
			return mPlayer;
		}
	}

	@Override
	public void onPlayerEvent(NanostreamEvent event, NanostreamPlayer instance)
	{
		if (event.GetCode() == NanostreamEvent.TYPE_RTMP_STATUS && event.GetCode() == NanostreamEvent.CODE_STREAM_STARTED)
		{
			Log.d("TEST", "EVENT");
			if (instance instanceof NanostreamPlayerImpl)
			{
				this.runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						root.requestLayout();
						Log.d("TEST", "LAYOUT REQUESTED");

					}
				});
			}
		}

	}
}
