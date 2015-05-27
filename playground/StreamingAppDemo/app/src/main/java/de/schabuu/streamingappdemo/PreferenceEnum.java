package de.schabuu.streamingappdemo;

public enum PreferenceEnum {
	PREF_FPS_KEY("prefFPS"), 
	PREF_URI_KEY("prefURI"), 
	PREF_RESOLUTION_KEY("prefResolution"), 
	PREF_BITRATE_KEY("prefBitrate"), 
	PREF_CODE_KEY("prefStreamingCode"), 
	PREF_AUTH_USER_KEY("prefAuthUser"), 
	PREF_AUTH_PASS_KEY("prefAuthPass"),
	PREF_ABC_MODE("prefABCMode");
	
/*	PREF_ABC_MIN_BITRATE("prefABCMinBitrate"),
	PREF_ABC_MIN_FRAMERATE("prefABCMinFramerate"),
	PREF_ABC_FLUSH_BUFFER_THRESHOLD("prefABCFlushBufferThreshold");*/
	
	private final String value;

	private PreferenceEnum(String v) {
		value = v;
	}

	public String getValue() {
		return value;
	}

	public boolean equalsValue(String eq) {
		return value.equals(eq);
	}
	
}
