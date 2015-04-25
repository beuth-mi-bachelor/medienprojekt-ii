package net.nanocosmos.nanoStream.activities;

import java.util.ArrayList;

import net.nanocosmos.nanoStream.R;
import net.nanocosmos.nanoStream.nanoStreamApp;
import net.nanocosmos.nanoStream.streamer.Logging;
import net.nanocosmos.nanoStream.streamer.NanostreamException;
import net.nanocosmos.nanoStream.streamer.nanoResults;
import net.nanocosmos.nanoStream.streamer.AdaptiveBitrateControlSettings;
import net.nanocosmos.nanoStream.streamer.AdaptiveBitrateControlSettings.AdaptiveBitrateControlMode;
import net.nanocosmos.nanoStream.streamer.NanostreamEvent;
import net.nanocosmos.nanoStream.streamer.NanostreamEventListener;
import net.nanocosmos.nanoStream.streamer.nanoStream;
import net.nanocosmos.nanoStream.streamer.nanoStream.EncoderState;
import net.nanocosmos.nanoStream.ui.StreamPreview;
import net.nanocosmos.nanoStream.util.PreferenceEnum;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		NanostreamEventListener {
	private StreamPreview surface;
	long startedTimestamp = 0;

	private nanoStream streamLib;

	private int width = 320;
	private int height = 320;
	private int BIT_RATE = 500000;
	private int FRAME_RATE = 15;
	// TODO: REPLACE WITH YOUR LICENSE
	private String license = "nlic:1.2:LiveEnc:1.1:LivePlg=1,H264ENC=1,MP4=1,RtmpMsg=1,RTMPx=3,Resz=1,Demo=1,Ic=1,NoMsg=1,Tm=600,T1=300:adr:20150424,20151102::0:0:nanocosmos-471231-27:ncpt:1d1837e4a6d31d51b739b8af0816f2c2";
    private String serverUrl = "rtmp://ws2.nanocosmos.net/live";
	// TODO: CHANGE STREAM NAME TO UNIQUE VALUE
	private String streamName = "nanoStream";
	private String authUser = "";
	private String authPass = "";
	private AdaptiveBitrateControlMode abcMode = AdaptiveBitrateControlMode.DISABLED;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(this.getClass().getName(), "create");
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		surface = (StreamPreview) findViewById(R.id.surface);

		loadPreferences();

		AdaptiveBitrateControlSettings abcSettings = new AdaptiveBitrateControlSettings(abcMode);
		Logging.LogSettings logSettings = new Logging.LogSettings(Logging.LogLevel.VERBOSE, 1);

		try {
			streamLib = new nanoStream(nanoStream.VideoSourceType.INTERNAL_FRONT, width, height, BIT_RATE,
					FRAME_RATE, surface.getHolder(), 2, license, serverUrl,
					streamName, authUser, authPass, this, abcSettings, logSettings);
		} catch (NanostreamException en) {
			Toast.makeText(getApplicationContext(), en.toString(),
					Toast.LENGTH_LONG).show();
		}
		try {
			if (streamLib != null) {
				streamLib.init();
			}
		} catch (NanostreamException en) {
			Toast.makeText(getApplicationContext(), en.toString(),
					Toast.LENGTH_LONG).show();
		}

		PreferenceManager.getDefaultSharedPreferences(
				nanoStreamApp.getAppContext())
				.registerOnSharedPreferenceChangeListener(
						new PreferenceChangeListener());
	}

	@Override
	protected void onRestart() {
		Log.i(this.getClass().getName(), "restart");
		super.onRestart();
		SurfaceView surface = (SurfaceView) findViewById(R.id.surface);
		surface.setVisibility(View.INVISIBLE);
		loadPreferences();

		AdaptiveBitrateControlSettings abcSettings = new AdaptiveBitrateControlSettings(abcMode);
		Logging.LogSettings logSettings = new Logging.LogSettings(Logging.LogLevel.VERBOSE, 1);

		try {
			streamLib = new nanoStream(nanoStream.VideoSourceType.INTERNAL_FRONT, width, height, BIT_RATE,
					FRAME_RATE, surface.getHolder(), 2, license, serverUrl,
					streamName, authUser, authPass, this, abcSettings, logSettings);
		} catch (NanostreamException en) {
			Toast.makeText(getApplicationContext(), en.toString(),
					Toast.LENGTH_LONG).show();
		}

		try {
			if (streamLib != null) {
				streamLib.init();
			}
		} catch (NanostreamException en) {
			Toast.makeText(getApplicationContext(), en.toString(),
					Toast.LENGTH_LONG).show();
		}
		surface.setVisibility(View.VISIBLE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item = menu.findItem(R.id.startStreaming);
		item.setIcon(R.drawable.but_start);
		return true;
	}

	@Override
	protected void onPause() {
		Log.i(this.getClass().getName(), "pause");
		super.onPause();

	}

	@Override
	protected void onResume() {
		Log.i(this.getClass().getName(), "resume");
		super.onResume();
	}

	@Override
	protected void onStart() {
		Log.i(this.getClass().getName(), "start");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.i(this.getClass().getName(), "stop");

		super.onStop();

		if (streamLib != null) {
			streamLib.release();

			streamLib = null;

		}
		invalidateOptionsMenu();
	}

	@Override
	protected void onDestroy() {
		Log.i(this.getClass().getName(), "destroy");

		super.onDestroy();
	}

	/**
	 *
	 * @param clicked
	 *            - Der geclickte Menu-Eintrag.
	 */
	public void toggleStreaming(MenuItem clicked) {
		if (streamLib == null) {
			Toast.makeText(getApplicationContext(),
					"nanoStream failed to initialize", Toast.LENGTH_LONG)
					.show();
			return;
		}

		if (!streamLib.hasState(EncoderState.RUNNING)) {
			if (!isNetworkAvailable()) {
				Toast.makeText(
						getApplicationContext(),
						"Cannot find available network. Please check your device settings.",
						Toast.LENGTH_LONG).show();
				return;
			} else {
				Toast.makeText(getApplicationContext(), "Starting...",
						Toast.LENGTH_SHORT).show();
			}
			if (streamLib.hasState(nanoStream.EncoderState.STOPPED)
					|| streamLib.hasState(nanoStream.EncoderState.CREATED)) {
				try {
					Log.d("StreamToogle", "init");
					streamLib.init();
				} catch (NanostreamException en) {
					Toast.makeText(getApplicationContext(), en.toString(),
							Toast.LENGTH_LONG).show();
					return;
				}
			}

			try {
				streamLib.start();
			} catch (NanostreamException en) {
				Toast.makeText(getApplicationContext(), en.toString(),
						Toast.LENGTH_LONG).show();
				return;
			}

			clicked.setIcon(getResources().getDrawable(R.drawable.but_stop));
		} else {
			Toast.makeText(getApplicationContext(), "Stopping...",
					Toast.LENGTH_SHORT).show();

			streamLib.stop();
			clicked.setIcon(getResources().getDrawable(R.drawable.but_start));
		}
	}

	public void toggleCamera(MenuItem clicked) {
		if (streamLib == null) {
			Toast.makeText(getApplicationContext(),
					"nanoStream failed to initialize", Toast.LENGTH_LONG)
					.show();
			return;
		}

		try {
			streamLib.rotateCamera();
		} catch (NanostreamException e) {
			if (e.getCode() == nanoResults.N_CAMERA_NOSECOND) {
				Toast.makeText(
						getApplicationContext(),
						nanoResults
								.GetDescription(nanoResults.N_CAMERA_NOSECOND),
						Toast.LENGTH_LONG).show();
			} else {
				e.printStackTrace();
			}
		}
		if (!streamLib.hasState(EncoderState.RUNNING)) {
			invalidateOptionsMenu();
		}

	}

	public void viewAbout(MenuItem clicked) {
		Toast.makeText(this,
				"nanoStream-Version : " + nanoStream.getVersion().fullVersion,
				Toast.LENGTH_LONG).show();
	}

	/**
	 *
	 * @param clicked
	 *            - Der geclickte Menu-Eintrag.
	 */
	public void showSettings(MenuItem clicked) {
		if (streamLib == null) {
			Toast.makeText(getApplicationContext(),
					"nanoStream failed to initialize", Toast.LENGTH_LONG)
					.show();
			return;
		}

		Toast.makeText(getApplicationContext(), "Showing Settings...",
				Toast.LENGTH_LONG).show();
		Intent intent = new Intent(this, PreferenceActivity.class);

		intent.putExtra("de.nanocosmos.streamer.VideoEncoder.resolutions",
				streamLib.getCapabilities().listAvailableVideoResolutions());
		intent.putIntegerArrayListExtra(
				"de.nanocosmos.streamer.VideoEncoder.fps",
				(ArrayList<Integer>) streamLib.getCapabilities()
						.listAvailableVideoFramerates());

		startActivity(intent);
	}

	public boolean isNetworkAvailable() {
		Context context = getApplicationContext();

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

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
	// NanostreamEventListener
	public void onNanostreamEvent(NanostreamEvent event) {
		this.runOnUiThread(new NotificationRunable(event));
	}

	private class NotificationRunable implements Runnable {
		private NanostreamEvent m_event;

		public NotificationRunable(NanostreamEvent event) {
			m_event = event;
		}

		@Override
		public void run() {
			if (m_event.GetType() != NanostreamEvent.TYPE_RTMP_QUALITY) {
				Toast.makeText(getApplicationContext(),
						m_event.GetDescription(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class PreferenceChangeListener implements
			OnSharedPreferenceChangeListener {

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {

			Log.i(this.getClass().getName(), "Preference changed");

			if (PreferenceEnum.PREF_RESOLUTION_KEY.equalsValue(key)) {
				String size = sharedPreferences.getString(key, "320x240");
				String[] sizes = size.split("x");

				if (sizes.length > 2) {
					throw new RuntimeException(new IllegalArgumentException(
							"Wrong resolution value."));
				}

				width = Integer.parseInt(sizes[0]);
				height = Integer.parseInt(sizes[1]);
			}

			if (PreferenceEnum.PREF_BITRATE_KEY.equalsValue(key)) {
				String value = sharedPreferences.getString(key, "500000");
				BIT_RATE = Integer.parseInt(value);
			}

			if (PreferenceEnum.PREF_FPS_KEY.equalsValue(key)) {
				FRAME_RATE = Integer.parseInt(sharedPreferences.getString(key,
						"15"));
			}

			if (PreferenceEnum.PREF_URI_KEY.equalsValue(key)) {
				serverUrl = sharedPreferences.getString(
						PreferenceEnum.PREF_URI_KEY.getValue(), serverUrl);
			}

			if (PreferenceEnum.PREF_CODE_KEY.equalsValue(key)) {
				streamName = sharedPreferences.getString(
						PreferenceEnum.PREF_CODE_KEY.getValue(), streamName);
			}

			if (PreferenceEnum.PREF_AUTH_USER_KEY.equalsValue(key)) {
				authUser = sharedPreferences.getString(
						PreferenceEnum.PREF_AUTH_USER_KEY.getValue(), authUser);
			}

			if (PreferenceEnum.PREF_AUTH_PASS_KEY.equalsValue(key)) {
				authPass = sharedPreferences.getString(
						PreferenceEnum.PREF_AUTH_PASS_KEY.getValue(), authPass);
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

			if (PreferenceEnum.PREF_ABC_MODE.equalsValue(key)) {
				switch (Integer.parseInt(sharedPreferences.getString(
						PreferenceEnum.PREF_ABC_MODE.getValue(),
						String.valueOf(0)))) {
				case 0: {
					abcMode = AdaptiveBitrateControlMode.DISABLED;
					break;
				}
				case 1: {
					abcMode = AdaptiveBitrateControlMode.QUALITY_DEGRADE;
					break;
				}
				case 2: {
					abcMode = AdaptiveBitrateControlMode.FRAME_DROP;
					break;
				}
				case 3: {
					abcMode = AdaptiveBitrateControlMode.QUALITY_DEGRADE_AND_FRAME_DROP;
					break;
				}
				default: {
					abcMode = AdaptiveBitrateControlMode.DISABLED;
					break;
				}
				}
			}
		}
	}

	private void loadPreferences() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(nanoStreamApp.getAppContext());

		BIT_RATE = Integer.parseInt(prefs.getString(
				PreferenceEnum.PREF_BITRATE_KEY.getValue(),
				String.valueOf(BIT_RATE)));
		FRAME_RATE = Integer.parseInt(prefs.getString(
				PreferenceEnum.PREF_FPS_KEY.getValue(),
				String.valueOf(FRAME_RATE)));
		String size = prefs.getString(
				PreferenceEnum.PREF_RESOLUTION_KEY.getValue(), width + "x"
						+ height);
		String[] sizeParts = size.split("x");
		width = Integer.parseInt(sizeParts[0]);
		height = Integer.parseInt(sizeParts[1]);
		serverUrl = prefs.getString(PreferenceEnum.PREF_URI_KEY.getValue(),
				serverUrl);
		streamName = prefs.getString(PreferenceEnum.PREF_CODE_KEY.getValue(),
				streamName);
		authUser = prefs.getString(
				PreferenceEnum.PREF_AUTH_USER_KEY.getValue(), authUser);
		authPass = prefs.getString(
				PreferenceEnum.PREF_AUTH_PASS_KEY.getValue(), authPass);

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

		switch (Integer.parseInt(prefs.getString(
				PreferenceEnum.PREF_ABC_MODE.getValue(), String.valueOf(0)))) {
		case 0: {
			abcMode = AdaptiveBitrateControlMode.DISABLED;
			break;
		}
		case 1: {
			abcMode = AdaptiveBitrateControlMode.QUALITY_DEGRADE;
			break;
		}
		case 2: {
			abcMode = AdaptiveBitrateControlMode.FRAME_DROP;
			break;
		}
		case 3: {
			abcMode = AdaptiveBitrateControlMode.QUALITY_DEGRADE_AND_FRAME_DROP;
			break;
		}
		default: {
			abcMode = AdaptiveBitrateControlMode.DISABLED;
			break;
		}
		}
	}
}
