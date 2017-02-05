package com.bluewatcher.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.bluewatcher.R;

/**
 * @version $Revision$
 */
public class WhatsNew {
	
	public static void show(Activity activity) {
		if(!InstallationInfo.isVersionFirstExecution(activity))
			return;
		
		DialogFragment dialog = new WhatsNewDialogFragment(activity);
		dialog.setCancelable(false);
		dialog.show(activity.getFragmentManager(), "WhatsNewDialogFragment");
	}
	
	private static class WhatsNewDialogFragment extends DialogFragment {
		
		private Activity activity;
		
		public WhatsNewDialogFragment(Activity activity) {
			this.activity = activity;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
			alertDialogBuilder.setTitle(activity.getString(R.string.app_name) + " " + activity.getString(R.string.app_version));
			alertDialogBuilder.setMessage(activity.getString(R.string.whats_new));
			alertDialogBuilder.setPositiveButton(activity.getString(R.string.ok_text), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					InstallationInfo.updateVersion(activity);
				}
			});
			return alertDialogBuilder.create();
		}
	}
}
