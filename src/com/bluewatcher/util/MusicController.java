package com.bluewatcher.util;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.view.KeyEvent;

import com.bluewatcher.control.ControlOption;

/**
 * @version $Revision$
 */
public class MusicController {
	private AudioManager audioManager;
	private Activity activity;
	
	public MusicController(Activity activity) {
		this.activity = activity;
		audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
	}
	
	@ControlOption
	public void togglePlayPause() {
		MediaKeySimulator.simulateMediaKey(activity, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
	}
	
	@ControlOption
	public void playNext() {
		MediaKeySimulator.simulateMediaKey(activity, KeyEvent.KEYCODE_MEDIA_NEXT);
	}
	
	@ControlOption
	public void playPrevious() {
		MediaKeySimulator.simulateMediaKey(activity, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
	}	
	
	@ControlOption
	public void volumeUp() {
		audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	}
	
	@ControlOption
	public void volumeDown() {
		audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
	}
	
	
}
