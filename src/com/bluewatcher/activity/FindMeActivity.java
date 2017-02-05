package com.bluewatcher.activity;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bluewatcher.R;
import com.bluewatcher.app.finder.PhoneFinderConfig;
import com.bluewatcher.app.finder.PhoneFinderConfigManager;

public class FindMeActivity extends Activity {
	public static final String EXTRA_FIND_ME = "EXTRA_FIND_ME";

	private Button foundButton;
	private Thread finisherThread;
	private MediaPlayer mediaPlayer;
	private AudioManager audioManager;
	private int originalVolume;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		PhoneFinderConfigManager.setFindingFlag();
		Log.i(FindMeActivity.class.getName(), "FindMeActivity called!!!");
		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_me);
		
		foundButton = (Button) findViewById(R.id.phone_found_button);
		foundButton.setOnClickListener(new FoundButtonListener());

		final PhoneFinderConfig config = getConfig();
		if (config == null) {
			finish();
			return;
		}
		
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, config.getVolume(), 0);
		mediaPlayer = MediaPlayer.create(this.getBaseContext(), R.raw.findme);
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
		
		finisherThread = new Thread(new FinisherThread(config));
		finisherThread.start();
	}
	
	@Override
	protected void onDestroy() {
		PhoneFinderConfigManager.resetFindingFlag();
		mediaPlayer.stop();
		mediaPlayer.release();
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
		finisherThread.interrupt();
		super.onDestroy();
	}
	
	private class FoundButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			onBackPressed();
		}
	}

	private class FinisherThread implements Runnable {
		private PhoneFinderConfig config;

		public FinisherThread(PhoneFinderConfig config) {
			this.config = config;
		}

		public void run() {
			int seconds = config.getDelayTime();
			while (seconds > 0) {
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					return;
				}
				seconds--;
			}
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	private PhoneFinderConfig getConfig() {
		try {
			return PhoneFinderConfigManager.load(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		}
		catch (JSONException e) {
			return null;
		}
	}
}
