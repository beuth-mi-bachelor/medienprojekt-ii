package net.nanocosmos.PlayerActivity.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by Sprotte on 27.05.15.
 */
public class SurfacePlayerView extends SurfaceView {


    public SurfacePlayerView(Context context) {
        super(context);
    }

    public SurfacePlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SurfacePlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        // TODO: Get aspect ratio from stream
        int videoWidth = 20;
        int videoHeight = 18;
        double aspectScreen = (double) viewWidth / (double) viewHeight;
        double aspectVideo = (double) videoWidth / (double) videoHeight;


        if (aspectScreen > aspectVideo) {
            viewWidth = (viewHeight * videoWidth) / videoHeight;
        } else {
            viewHeight = (viewWidth * videoHeight) / videoWidth;
        }

        setMeasuredDimension(viewWidth, viewHeight);
    }
}