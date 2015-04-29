package net.nanocosmos.nanoStream.ui;

import net.nanocosmos.nanoStream.util.PreferenceEnum;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class StreamPreview extends SurfaceView {

	public StreamPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StreamPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StreamPreview(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		String videoResolution = prefs.getString(PreferenceEnum.PREF_RESOLUTION_KEY.getValue(), "640x480");
		String[] aspects = videoResolution.split("x");
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int camWidth = Integer.parseInt(aspects[0]);
		int camHeight = Integer.parseInt(aspects[1]);

		
		width = (height * camWidth)/camHeight;
		setMeasuredDimension(width, height);

	}
}