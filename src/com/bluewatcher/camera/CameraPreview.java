package com.bluewatcher.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @version $Revision$
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private final static String TAG = CameraController.class.getSimpleName();

	private SurfaceHolder mHolder;
	private Camera mCamera;

	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;

		mHolder = getHolder();
		mHolder.addCallback(this);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
			
			mCamera.setDisplayOrientation(90);
		}
		catch (IOException e) {
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (mHolder.getSurface() == null)
			return;

		try {
			mCamera.stopPreview();
		}
		catch (Exception e) {
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		}
		catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

}
