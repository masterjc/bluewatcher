package com.bluewatcher.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;

import com.bluewatcher.R;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

public class License {
	private static final String APP_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjK4zF5t51SVmSw9714J8ZgLYnxkKTAeN4USQgR6WTvKpkL8gwO49aTv9Hf4u/8BI6eh5VIlgzdzkKpsoT6r+ZlFQlfLrl1Xl9MWFm7kNCIUVizCZI9JJct6EYMr4hK0DPiSKJA6XxhuIFLo3oeuOBZ711iR2165cloiTygPgqKnKALfmVzmBRNIORBCx18LvMlcoMsPG1qFC/k1wF9TSD8EEqATaoh3npClP7zPgbCLnRrP2T5IoNMKhi2D8LFXcuc6wvsguF7np/QZ+lXNXQ/ykJ38xBIu6tevmXM5Gxl6nNBgOpNz/73BRR/oxi5vipvWtCMP0k6vjwVsyQrPt5QIDAQAB";

	private static final byte[] SALT = new byte[] { 46, 65, 30, -128, -112, 55, 74, -64, 51, 88, 96, -66, 77, -117, -44, -113, 111, 42, -64, 88 };

	private class BlueWatcherLicenseCheckerCallback implements LicenseCheckerCallback {
		private Activity activity;

		BlueWatcherLicenseCheckerCallback(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void allow(int reason) {
		}

		@Override
		public void dontAllow(int reason) {
			if (activity.isFinishing())
				return;
			activity.runOnUiThread(new Runnable() {
			    public void run() {
			    	DialogFragment dialogFragment = new LicenseDialogFragment(activity);
			    	dialogFragment.setCancelable(false);
			    	dialogFragment.show(activity.getFragmentManager(), "LicenseDialogFragment");
			    }
			});
		}

		@Override
		public void applicationError(int errorCode) {
			activity.runOnUiThread(new Runnable() {
			    public void run() {
					DialogFragment dialogFragment = new LicenseDialogFragment(activity);
					dialogFragment.setCancelable(false);
					dialogFragment.show(activity.getFragmentManager(), "LicenseDialogFragment");
			    }
			});
				
		}
	}

	private LicenseCheckerCallback licenseCallback;
	private LicenseChecker licenseChecker;

	public License(Activity activity) {
		String deviceId = Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID);
		licenseCallback = new BlueWatcherLicenseCheckerCallback(activity);
		licenseChecker = new LicenseChecker(activity, new ServerManagedPolicy(activity, new AESObfuscator(License.SALT, activity.getPackageName(),
				deviceId)), License.APP_PUBLIC_KEY);
	}

	public void checkLicensing() {
		licenseChecker.checkAccess(licenseCallback);
	}

	public void destroy() {
		licenseChecker.onDestroy();
	}

	class LicenseDialogFragment extends DialogFragment {
		private Activity activity;
		private Integer errorNum;

		public LicenseDialogFragment(Activity activity, Integer error) {
			this.activity = activity;
			this.errorNum = error;
		}
		
		public LicenseDialogFragment(Activity activity) {
			this.activity = activity;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
			alertDialogBuilder.setTitle(getString(R.string.app_name));
			if( errorNum == null ) {
				alertDialogBuilder.setMessage(getString(R.string.not_licensed_app));
				alertDialogBuilder.setPositiveButton(getString(R.string.buy_app_button), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/details?id="
								+ activity.getPackageName()));
						startActivity(marketIntent);
						activity.finish();
					}
				});
			} else {
				alertDialogBuilder.setMessage(getString(R.string.license_check_error) + " (" + errorNum.intValue() + ")");
			}
			alertDialogBuilder.setNegativeButton(getString(R.string.exit_settings), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.finish();
				}
			});
			return alertDialogBuilder.create();
		}
	}

}
