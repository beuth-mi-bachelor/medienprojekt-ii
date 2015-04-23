package de.schabuu.streamfromcamera;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.nanocosmos.nanoStream.streamer.NanostreamEvent;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayer.PlayerEventListener;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayer;

public class Recording extends Activity implements PlayerEventListener {

    private static final String license = "nlic:1.2:LiveEnc:1.1:LivePlg=1,H264ENC=1,MP4=1,Resz=1,RTMPx=3,RtmpMsg=1,Demo=1,Ic=1,NoMsg=1,Tm=600,T1=300:adr:20150423,20150507::0:0:demo-0-2:ncpt:f049c0c9ff69a937f608871fa008dffa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recording, menu);
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

    @Override
    public void onPlayerEvent(NanostreamEvent nanostreamEvent, NanostreamPlayer nanostreamPlayer) {

    }
}
