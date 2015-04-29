package net.nanocosmos.nanoStream.ui;

import java.util.ArrayList;
import java.util.List;

import net.nanocosmos.nanoStream.R;
import net.nanocosmos.nanoStream.util.PreferenceEnum;
import net.nanocosmos.nanoStream.streamer.Resolution;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.EditTextPreference;
import android.preference.ListPreference;

public class PreferenceFragment extends android.preference.PreferenceFragment implements OnSharedPreferenceChangeListener {

	private ListPreference resolutions;
	private ListPreference framerates;
	// Streaming Code, Streaming URl, Auth (User,password) ,Bitrate/Resolution
	private EditTextPreference server;
	private ListPreference bitrates;
	private EditTextPreference code;
	private EditTextPreference authUser;
	private EditTextPreference authPass;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		bitrates = (ListPreference) findPreference(PreferenceEnum.PREF_BITRATE_KEY.getValue());
		bitrates.setSummary(getBitString(bitrates.getValue()) + "bit/s");
		resolutions = (ListPreference) findPreference(PreferenceEnum.PREF_RESOLUTION_KEY.getValue());
		server = (EditTextPreference) findPreference(PreferenceEnum.PREF_URI_KEY.getValue());
		server.setSummary(server.getText());
		code = (EditTextPreference)findPreference(PreferenceEnum.PREF_CODE_KEY.getValue());
		code.setSummary(code.getText());
		authUser = (EditTextPreference)findPreference(PreferenceEnum.PREF_AUTH_USER_KEY.getValue());
		authUser.setSummary(authUser.getText());
		authPass = (EditTextPreference)findPreference(PreferenceEnum.PREF_AUTH_PASS_KEY.getValue());
		authPass.setSummary(authPass.getText());
		Parcelable[] possible =  getActivity().getIntent().getExtras().getParcelableArray("de.nanocosmos.streamer.VideoEncoder.resolutions");
		ArrayList<CharSequence> resolutionEntries = new ArrayList<CharSequence>();
		// TODO : Begrenzen
		for (Parcelable sz : possible) {
			Resolution res = (Resolution) sz;
			resolutionEntries.add(res.w + "x" + res.h);

		}

		resolutions.setEntries(resolutionEntries.toArray(new CharSequence[resolutionEntries.size()]));
		resolutions.setEntryValues(resolutionEntries.toArray(new CharSequence[resolutionEntries.size()]));
		resolutions.setSummary(resolutions.getValue());
		framerates = (ListPreference) findPreference(PreferenceEnum.PREF_FPS_KEY.getValue());
		List<Integer> framerateList = getActivity().getIntent().getExtras().getIntegerArrayList("de.nanocosmos.streamer.VideoEncoder.fps");

		CharSequence[] framerateEntries = new CharSequence[framerateList.size()];
		CharSequence[] framerateEntryValues = new CharSequence[framerateList.size()];
		int i1 = 0;
		for (int fps : framerateList) {
			framerateEntries[i1] = (fps) + " fps";
			framerateEntryValues[i1] = (fps) + "";
			i1++;
		}
		framerates.setEntries(framerateEntries);
		framerates.setEntryValues(framerateEntryValues);
		framerates.setSummary(framerates.getValue() + " fps");

		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (PreferenceEnum.PREF_RESOLUTION_KEY.equalsValue(key)) {
			resolutions.setSummary(resolutions.getValue());
		}
		if (PreferenceEnum.PREF_FPS_KEY.equalsValue(key)) {
			framerates.setSummary(framerates.getValue() + " fps");
		}
		if (PreferenceEnum.PREF_URI_KEY.equalsValue(key)) {
			server.setSummary(sharedPreferences.getString(key, ""));
		}
		if (PreferenceEnum.PREF_CODE_KEY.equalsValue(key)){
			code.setSummary(sharedPreferences.getString(key, ""));
		}
		if (PreferenceEnum.PREF_BITRATE_KEY.equalsValue(key)) {
			String value = sharedPreferences.getString(key, "300000");
			value = getBitString(value);
			bitrates.setSummary(value + "bit/s");
		}
		
		if (PreferenceEnum.PREF_AUTH_USER_KEY.equalsValue(key)){
			authUser.setSummary(sharedPreferences.getString(key, ""));
		}
		if (PreferenceEnum.PREF_AUTH_PASS_KEY.equalsValue(key)){
			authPass.setSummary(sharedPreferences.getString(key, ""));
		}

	}

	private String getBitString(String value) {
		int calcCount = 0;
		while (Integer.parseInt(value) >= 1000) {
			int numeric = Integer.parseInt(value);
			value = Integer.toString(numeric / 1000);
			calcCount++;
		}
		switch (calcCount) {
		case 1:
			value += " k";
			break;
		case 2:
			value += " m";
			break;
		case 3:
			value += " g";
			break;
		default:
			break;
		}
		return value;
	}

}
