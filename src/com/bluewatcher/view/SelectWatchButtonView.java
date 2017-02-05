package com.bluewatcher.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.bluewatcher.BlueWatcherHelp;
import com.bluewatcher.R;
import com.bluewatcher.activity.WatchSelectorActivity;
import com.bluewatcher.ble.BluetoothClientManager;
import com.bluewatcher.util.InstallationInfo;

@SuppressLint("ValidFragment")
public class SelectWatchButtonView {
	private static final int SELECT_WATCH_REQUEST = 1;
	
	private Button selectWatchButton;
	private Activity activity;
	private BluetoothClientManager bleManager;
	
	public SelectWatchButtonView(Activity activity, BluetoothClientManager bleManager) {
		this.activity = activity;
		this.bleManager = bleManager;
		selectWatchButton = (Button) activity.findViewById(R.id.select_watch_button);
	}
	
	public void registerListenersForResult() {
		selectWatchButton.setOnClickListener(new SelectWatchButtonListener() );
	}
	
	private class SelectWatchButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			bleManager.stopAutoWatchConnect();
			if( InstallationInfo.isFirstConnection() ) {
				HelpDialogFragment fragment = new HelpDialogFragment();
				fragment.setCancelable(false);
				fragment.show(activity.getFragmentManager(), "HelpDialogFragment");
				
			} else {
				startWatchSelectorActivity();
			}
		}
	}
	
	private void startWatchSelectorActivity() {
		final Intent selectWatch = new Intent(activity, WatchSelectorActivity.class);
		activity.startActivityForResult(selectWatch, SELECT_WATCH_REQUEST);
	}
	
	@SuppressLint("ValidFragment")
	private class HelpDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
			alertDialogBuilder.setTitle(activity.getString(R.string.app_name) + " " + activity.getString(R.string.app_version));
			alertDialogBuilder.setMessage(activity.getString(R.string.first_connection_warning));
			alertDialogBuilder.setPositiveButton(activity.getString(R.string.ok_text), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					InstallationInfo.setFirstConnectionDone();
					startWatchSelectorActivity();
				}
			});
			alertDialogBuilder.setNegativeButton(activity.getString(R.string.more_info), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BlueWatcherHelp.HELP_URL));
					startActivity(browserIntent);
				}
			});

			return alertDialogBuilder.create();
		}
	}

}
