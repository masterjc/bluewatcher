package com.bluewatcher.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bluewatcher.BlueWatcherHelp;
import com.bluewatcher.R;

/**
 * @version $Revision$
 */
public class LollipopDetection {
	
	public static void show(Activity activity) {
		if( !android.os.Build.VERSION.RELEASE.startsWith("5.") )
			return;
		if( InstallationInfo.consentLollipopProblems() )
			return;
		DialogFragment dialog = new LollipopDialogFragment(activity);
		dialog.setCancelable(false);
		dialog.show(activity.getFragmentManager(), "LollipopDialogFragment");
	}
	
	@SuppressLint("ValidFragment")
	private static class LollipopDialogFragment extends DialogFragment {
		
		private Activity activity;
		
		public LollipopDialogFragment(Activity activity) {
			this.activity = activity;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
			alertDialogBuilder.setTitle(activity.getString(R.string.lollipop_detected_header));
			alertDialogBuilder.setMessage(activity.getString(R.string.lollipop_detected));
			alertDialogBuilder.setPositiveButton(activity.getString(R.string.lets_try_button), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialogBuilder.setNegativeButton(activity.getString(R.string.more_info), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BlueWatcherHelp.SUPPORTED_URL));
					startActivity(browserIntent);
				}
			});
			alertDialogBuilder.setNeutralButton(activity.getString(R.string.i_know_button), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					InstallationInfo.setLollipopProblemsConsent();
				}
			});
			return alertDialogBuilder.create();
		}
	}
}
