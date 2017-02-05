package com.bluewatcher.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * @version $Revision$
 */
public class MediaFileHelper {
	public static final String BLUEWATCHER_MEDIA_DIR = "BlueWatcher";
	public enum MediaType {
		IMAGE, VIDEO;
	}
	public static final int MEDIA_TYPE_VIDEO = 2;

	public static Uri getOutputMediaFileUri(MediaType type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	@SuppressLint("SimpleDateFormat")
	public static File getOutputMediaFile(MediaType type) {
		Boolean hasSdCard = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

		File mediaStorageDir = null;

		if (hasSdCard) {
			mediaStorageDir = new File(Environment.getExternalStorageDirectory(), BLUEWATCHER_MEDIA_DIR);
		} else {
			mediaStorageDir = new File(Environment.getDataDirectory(), BLUEWATCHER_MEDIA_DIR);
		}

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(BLUEWATCHER_MEDIA_DIR, "failed to create directory");
				return null;
			}
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MediaType.IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		}
		else if (type == MediaType.VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		}
		else {
			return null;
		}

		return mediaFile;
	}
}
