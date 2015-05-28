package net.nanocosmos.PlayerActivity.util;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

import net.nanocosmos.nanoStream.streamer.Logging;
import net.nanocosmos.nanoStream.streamer.Logging.LogLevel;
import net.nanocosmos.nanoStream.streamer.NanostreamVideoSource;
import net.nanocosmos.nanoStream.streamer.Resolution;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoCamera extends NanostreamVideoSource
{
	private static final String TAG = "VideoCamera";

	private Camera mCamera = null;
	private List<VideoFrameCallback> mVideoFrameCallbackList = null;
	private SurfaceHolder mHolder = null;
	private HolderCallback mHolderCallback = null;
	private PreviewFrameCallback mFrameCallback = null;
	private int mWidth = 640;
	private int mHeight = 480;
	private int mFramerate = 15;
	private VideoFormat mVideoFormat = null;

	public VideoCamera(final int width, final int height, final int framerate, final SurfaceHolder holder)
	{
		mVideoFormat = new VideoFormat(VideoEncoding.VIDEO_ENCODING_RAW_NV21, width, height, framerate);
		mHolder = holder;
		mFrameCallback = new PreviewFrameCallback();
		mVideoFrameCallbackList = new ArrayList<VideoFrameCallback>();
	}

	public void startCamera(int id)
	{
		mCamera = Camera.open(id);

		mHolderCallback = new HolderCallback();
		mHolder.addCallback(mHolderCallback);
//		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		Parameters params = mCamera.getParameters();

		Resolution res = checkPreviewSize(mCamera.getParameters(), mVideoFormat.getWidth(), mVideoFormat.getHeight());
		mVideoFormat.setWidth(res.w);
		mVideoFormat.setHeight(res.h);

		params.setPreviewSize(mVideoFormat.getWidth(), mVideoFormat.getHeight());
		params.setPreviewFrameRate(mFramerate);
		params.setPreviewFormat(ImageFormat.NV21);

		try
		{
			mCamera.setParameters(params);
		} catch (RuntimeException e)
		{
			e.printStackTrace();
		}
		mVideoFormat.setWidth(mCamera.getParameters().getPreviewSize().width);
		mVideoFormat.setHeight(mCamera.getParameters().getPreviewSize().height);
	}

	@Override
	public void addVideoFrameCallback(VideoFrameCallback arg0)
	{
		mVideoFrameCallbackList.add(arg0);
	}

	@Override
	public VideoFormat getVideoFormat()
	{
		return mVideoFormat;
	}

	@Override
	public void release()
	{
		stopVideoSource();

		if (mVideoFrameCallbackList != null)
		{
			for (VideoFrameCallback c : mVideoFrameCallbackList)
			{
				mVideoFrameCallbackList.remove(c);
			}
			mVideoFrameCallbackList = null;
		}

		if (mCamera != null)
		{
			mCamera.release();
			mCamera = null;
		}

		if (mHolder != null)
		{
			if (mHolderCallback != null)
			{
				mHolder.removeCallback(mHolderCallback);
				mHolderCallback = null;
			}
			mHolder = null;
		}
	}

	@Override
	public void removeVideoFrameCallback(VideoFrameCallback arg0)
	{
		mVideoFrameCallbackList.remove(arg0);
	}

	@Override
	public void startVideoSource()
	{
		Logging.log(LogLevel.DEBUG, TAG, "Start VideoSource");
		mCamera.setPreviewCallback(mFrameCallback);
		mCamera.setErrorCallback(new ErrorCallback()
		{

			@Override
			public void onError(int error, Camera camera)
			{
				Logging.log(LogLevel.ERROR, TAG, "Error Code " + error);
			}
		});
	}

	@Override
	public void stopVideoSource()
	{
		Logging.log(LogLevel.DEBUG, TAG, "Stop VideoSource");

		if (mCamera != null)
		{
			mCamera.setPreviewCallback(null);
			mCamera.setErrorCallback(null);
		}
	}

	private class PreviewFrameCallback implements PreviewCallback
	{
		private static final String TAG = "PreviewFrameCallback";

		@Override
		public void onPreviewFrame(byte[] data, Camera camera)
		{
			// data can be null if preview buffers are too small
			if (null == data)
			{
				Logging.log(LogLevel.DEBUG, TAG, "Error - Received null buffer from camera - returning");
				return;
			}

			Size s = camera.getParameters().getPreviewSize();

			try
			{
				for (VideoFrameCallback c : mVideoFrameCallbackList)
				{
					ByteBuffer d = ByteBuffer.wrap(data);
					c.onVideoFrame(d, data.length, System.currentTimeMillis());
				}
			} catch (Exception e)
			{
				Logging.log(LogLevel.ERROR, TAG, "ERROR", e);
			} finally
			{
				camera.addCallbackBuffer(data);
			}
		}
	}

	@Override
	public Resolution[] getSupportedResolutions()
	{
		if (mCamera == null)
		{
			// return empty list;
			return new Resolution[0];
		}
		List<Resolution> possible = new ArrayList<Resolution>();
		List<Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
		int i = 0;
		Size lastSmallest = null;
		int lastSmallestPos = 0;
		for (Size sz : sizes)
		{
			if ((sz.width % 16 == 0) && (sz.height % 8 == 0) && (sz.width <= 1280) && (sz.height <= 720))
			{
				possible.add(new Resolution(sz.width, sz.height));
				if (lastSmallest == null || (lastSmallest.height > sz.height || lastSmallest.width > sz.width))
				{
					lastSmallest = sz;
					lastSmallestPos = i;
				}
				i++;
			}
		}
		if (lastSmallestPos > 0)
		{
			Collections.reverse(possible);
		}
		Resolution[] res = new Resolution[possible.size()];
		res = possible.toArray(res);
		return res;
	}

	public Resolution checkPreviewSize(Camera.Parameters params, int w, int h)
	{
		Logging.log(LogLevel.INFO, this.getClass().getName(), "Looking for suitable preview size");
		Resolution res = new Resolution(w, h);

		List<Size> supportedPreviewSize = params.getSupportedPreviewSizes();

		if (null != supportedPreviewSize)
		{
			boolean exactMatch = false;

			for (int i = 0; i < supportedPreviewSize.size(); i++)
			{
				Size size = supportedPreviewSize.get(i);

				if (null == size || 0 >= size.width || 0 >= size.height)
				{
					continue;
				}

				if (size.width == w && size.height == h)
				{
					exactMatch = true;
					break;
				}
			}

			if (!exactMatch)
			{
				long bestSizeDiff = -1;

				for (int i = 0; i < supportedPreviewSize.size(); i++)
				{
					Size size = supportedPreviewSize.get(i);

					if (null == size || 0 >= size.width || 0 >= size.height)
					{
						continue;
					}

					long currSizeDiff = ((long) (size.width - w) * (long) (size.width - w)) + ((long) (size.height - h) * (long) (size.height - h));

					if (0 > bestSizeDiff || (currSizeDiff < bestSizeDiff))
					{
						bestSizeDiff = currSizeDiff;
						res.h = size.height;
						res.w = size.width;
					}
				}
			}
		}

		return res;
	}

	@SuppressWarnings("deprecation")
	public List<Integer> getSupportedFramerates()
	{

		return mCamera.getParameters().getSupportedPreviewFrameRates();
	}

	private class HolderCallback implements SurfaceHolder.Callback
	{

		@Override
		public void surfaceCreated(SurfaceHolder holder)
		{

			Logging.log(LogLevel.INFO, this.getClass().getName(), "surfaceCreated");
			try
			{
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
		{

			Logging.log(LogLevel.INFO, this.getClass().getName(), "surfaceChanged");

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder)
		{

			Logging.log(LogLevel.INFO, this.getClass().getName(), "surfaceDestroyed");

		}

	}
}
