/*
package net.nanocosmos.PlayerActivity.util;

import android.content.SharedPreferences;
import android.util.Log;

import net.nanocosmos.nanoStream.streamer.AdaptiveBitrateControlSettings;
/*
/**
 * Created by Sprotte on 28.05.15.
 */
/*
public class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener
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
/*
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
}*/