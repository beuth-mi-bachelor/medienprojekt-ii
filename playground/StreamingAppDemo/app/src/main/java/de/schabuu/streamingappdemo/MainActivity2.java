package de.schabuu.streamingappdemo;

import android.content.Intent;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.nanocosmos.nanoStream.streamer.AdaptiveBitrateControlSettings;
import net.nanocosmos.nanoStream.streamer.NanostreamEventListener;
import net.nanocosmos.nanoStream.streamer.NanostreamException;
import net.nanocosmos.nanoStream.streamer.nanoStream;

import java.util.prefs.PreferenceChangeListener;


public class MainActivity2 extends ActionBarActivity implements NanostreamEventListener {

    private StreamPreview surface;
    long startedTimestamp = 0;

    private nanoStream streamLib;

    private int width = 640;
    private int height = 480;
    private int BIT_RATE = 500000;
    private int FRAME_RATE = 15;

    private String license = "";
    private String serverUrl = "";

    private String streamName = "";
    private String authUser = "";
    private String authPass = "";
    private AdaptiveBitrateControlSettings.AdaptiveBitrateControlMode abcMode = AdaptiveBitrateControlSettings.AdaptiveBitrateControlMode.DISABLED;

    VideoCamera mVideoCam = null;
    nanoStream.VideoSourceType vsType = nanoStream.VideoSourceType.EXTERNAL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        surface = (StreamPreview) findViewById(R.id.surface);

        loadPreferences();

        AdaptiveBitrateControlSettings abcSettings = new AdaptiveBitrateControlSettings(abcMode);

        try {
            streamLib = new nanoStream(vsType, width, height, BIT_RATE, FRAME_RATE, surface.getHolder(), 2, license, serverUrl, streamName, authUser, authPass,
                    this, abcSettings, logSettings);
            mVideoCam = new VideoCamera(width, height, FRAME_RATE, surface.getHolder());
            mVideoCam.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            streamLib.setVideoSource(mVideoCam);
        } catch (NanostreamException en) {
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

        PreferenceManager.getDefaultSharedPreferences(nanoStreamApp.getAppContext()).registerOnSharedPreferenceChangeListener(new PreferenceChangeListener());

        // This is for the Back Button and Activity Change
        Intent intent = getIntent();

        String rn = intent.getStringExtra(MainActivity.ROOM_NAME);

        TextView room = (TextView) findViewById(R.id.roomname);

        room.setText(rn);

        final Button button = (Button) findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeView(v);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeView(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
