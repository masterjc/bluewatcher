package com.bluewatcher.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.media.MediaPlayer;
import android.util.Log;

import com.bluewatcher.R;
import com.bluewatcher.config.ConfigurationManager;
import com.bluewatcher.control.ControlOption;

/**
 * @version $Revision$
 */
public class CameraController {
	
	public interface CameraEventListener {
		void onPictureTaken(byte[] data, Camera camera);
	}
	
	private static final String START_CAMERA = "START_CAMERA";
	private static CameraController INSTANCE;
	
	private CameraEventListener listener;
	
	public static CameraController getInstance() {
		if( INSTANCE == null ) {
			INSTANCE = new CameraController();
		}
		return INSTANCE;
	}
	
	public enum CameraType {
		FRONT, BACK;
	}

	private final static String TAG = CameraController.class.getSimpleName();

	private Camera camera;
	private MediaPlayer mediaPlayer;

	public Camera acquireCamera(Context context, CameraType cameraType) {
		try {
			camera = Camera.open();
			mediaPlayer = MediaPlayer.create(context, R.raw.shutter);
			mediaPlayer.setLooping(false);
		}
		catch (Exception e) {
			Log.e(TAG, "Error acquiring camera instance: " + e.getMessage());
			return null;
		}

		return camera;
	}

	public void releaseCamera() {
		if (camera == null)
			return;
		camera.release();
		mediaPlayer.release();
	}
	
	public void setCameraEventListener(CameraEventListener listener) {
		this.listener = listener;
	}
	
	private AutoFocusCallback takePhotoAutoFocus = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if( success ) {
				mediaPlayer.start();
				camera.takePicture(null, null, mPicture);
			}
		}
	};

	@ControlOption
	public void takePhoto() {
		if (camera == null)
			return;
		camera.autoFocus(takePhotoAutoFocus);
	}
	
	public static void setStartCamera() {
		ConfigurationManager.getInstance().save(START_CAMERA, Boolean.TRUE.toString());
	}
	
	public static void resetStartCamera() {
		ConfigurationManager.getInstance().save(START_CAMERA, Boolean.FALSE.toString());
	}
	
	public static boolean isStartCamera() {
		String findMe = ConfigurationManager.getInstance().load(START_CAMERA, "false");
		return Boolean.parseBoolean(findMe);
	}
	
	private PictureCallback mPicture = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if(listener != null) {
				listener.onPictureTaken(data, camera);
			}
		}
	};

}
