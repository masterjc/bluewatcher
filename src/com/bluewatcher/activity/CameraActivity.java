package com.bluewatcher.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.bluewatcher.R;
import com.bluewatcher.camera.CameraController;
import com.bluewatcher.camera.CameraController.CameraEventListener;
import com.bluewatcher.camera.CameraController.CameraType;
import com.bluewatcher.camera.CameraPreview;
import com.bluewatcher.camera.SingleMediaScanner;
import com.bluewatcher.util.MediaFileHelper;
import com.bluewatcher.util.MediaFileHelper.MediaType;

public class CameraActivity extends Activity {
	private final static String TAG = CameraActivity.class.getSimpleName();
	
	private CameraPreview mPreview;
	private Button captureButton;
	private Button saveButton;
	private Button cancelButton;
	private ToggleButton flashConfig;
	
	
	private byte[] data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			finish();
			return;
		}
		
		CameraController.getInstance().setCameraEventListener(cameraListener);
		
		final Camera camera = CameraController.getInstance().acquireCamera(getApplicationContext(), CameraType.BACK);
		if( camera == null ) {
			Toast.makeText(this, R.string.error_acquiring_camera, Toast.LENGTH_LONG).show();
			return;
		}
			
		mPreview = new CameraPreview(this, camera);
		mPreview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				camera.autoFocus(null);
			}
		});
		
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		
		flashConfig = (ToggleButton) findViewById(R.id.flash_config);
		flashConfig.setChecked(true);
		Parameters p = camera.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_AUTO);
		camera.setParameters(p);
		
		flashConfig.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Parameters p = camera.getParameters();
				if( isChecked ) {
					p.setFlashMode(Parameters.FLASH_MODE_AUTO);
				} else {
					p.setFlashMode(Parameters.FLASH_MODE_OFF);
				}
				camera.setParameters(p);
			}
		});
		captureButton = (Button) findViewById(R.id.button_capture);
		saveButton = (Button) findViewById(R.id.button_save);
		cancelButton = (Button) findViewById(R.id.button_cancel);
		saveButton.setOnClickListener(new SaveImageListener(getApplicationContext()));
		cancelButton.setOnClickListener(new CancelImageListener());
		saveButton.setVisibility(View.GONE);
		cancelButton.setVisibility(View.GONE);
		
		captureButton.setOnClickListener(
			    new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			            CameraController.getInstance().takePhoto();
			        }
			    }
			);
	}

	protected void onDestroy() {
		super.onDestroy();
		CameraController.getInstance().releaseCamera();
	}
	
	private void setPhoto(byte[] photo) {
		this.data = photo;
	}
	
	private class SaveImageListener implements OnClickListener {
		private Context context;
		
		SaveImageListener(Context context) {
			this.context = context;
		}
		
		@Override
		public void onClick(View v) {
			if( data == null ) {
				finish();
				return;
			}
				
			File pictureFile = MediaFileHelper.getOutputMediaFile(MediaType.IMAGE);
			if (pictureFile == null) {
				Log.d(TAG, "Error creating media file, check storage permissions");
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
				new SingleMediaScanner(context, pictureFile);
			}
			catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			}
			catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
			finish();
		}
		
	}
	
	private class CancelImageListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}
	
	private CameraEventListener cameraListener = new CameraEventListener() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			mPreview.setClickable(false);
			captureButton.setVisibility(View.GONE);
			flashConfig.setVisibility(View.GONE);
			saveButton.setVisibility(View.VISIBLE);
			cancelButton.setVisibility(View.VISIBLE);
			setPhoto(data);
			
		}
	};
}
