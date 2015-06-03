package de.beuth_hochschule.Schabuu.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import net.nanocosmos.nanoStream.streamer.NanostreamPlayer;
import net.nanocosmos.nanoStream.streamer.nanoStream;


/**
 * Created by Sprotte on 01.06.15.
 */
public class RecievingUtils {
    private RetainedFragment dataFragment;
    private NanostreamPlayer player;

    public RecievingUtils(Activity displayActivity, String license, String strStreamUrl, String strStreamname, String authUser, String authPass) {

        FragmentManager fm = displayActivity.getFragmentManager();
        dataFragment = (RetainedFragment) fm.findFragmentByTag("data");

        player = null;

        // create the fragment and data the first time
        if (dataFragment == null) {
            // add the fragment
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();

            player = nanoStream.createNanostreamPlayer();
            NanostreamPlayer.PlayerSettings settings = player.new PlayerSettings();
            settings.setLicense(license);
            settings.setUrl(strStreamUrl);
            settings.setStreamname(strStreamname);
            settings.setAuthUsername(authUser);
            settings.setAuthPassword(authPass);
            settings.setBufferTimeMs(2000);

            player.setSettings(settings);
            dataFragment.setData(player);
        } else {
            player = dataFragment.getData();
        }
    }
    public NanostreamPlayer GetPlayer(){
        return player;
    }
    public void StartPlayer(){
        dataFragment.mPlayer.start();
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
}
