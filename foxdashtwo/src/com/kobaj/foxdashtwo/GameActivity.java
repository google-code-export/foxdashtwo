package com.kobaj.foxdashtwo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.kobaj.math.Constants;

public class GameActivity extends Activity
{
	/** Called when the activity is first created. */
	
	private PowerManager.WakeLock wl;
	
	public static com.kobaj.opengl.MyGLSurfaceView mGLView;
	
	// saving state
	protected String shared_prefs_name = "com.kobaj.foxdashtwo_prefs";
	public static SharedPreferences mPrefs;
	public static SharedPreferences.Editor ed;
	
	public static Handler itself;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// do context first, a lot of stuff relies on it.
		Constants.context = getApplicationContext();
		
		Constants.resources = this.getResources();
		
		// grabbing save states
		mPrefs = getSharedPreferences(shared_prefs_name, 0);
		ed = mPrefs.edit();
		
		// keeping the screen on
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
		
		// volume controls
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		// findout out the dpi
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = getWindowManager().getDefaultDisplay();
		display.getMetrics(metrics);
		Constants.dip_scale = ((double) metrics.densityDpi) / DisplayMetrics.DENSITY_HIGH;
		
		// fonts and text scale
		Constants.sd_scale = metrics.scaledDensity;
		
		// touchy
		com.kobaj.math.Constants.input_manager = new com.kobaj.input.InputManager();
		
		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity
		mGLView = new com.kobaj.opengl.MyGLSurfaceView(this);
		setContentView(mGLView);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		wl.release();
		
		// save user settings
		UserSettings.saveUserSettings();
		
		ed.commit();
		
		mGLView.onScreenPause();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		wl.acquire();
		
		// load user settings
		UserSettings.loadUserSettings();
		
		mGLView.onResume();
	}
	
	// input
	
	@Override
	public boolean onKeyDown(int i, KeyEvent event)
	{
		if (i == KeyEvent.KEYCODE_VOLUME_DOWN || i == KeyEvent.KEYCODE_VOLUME_UP)
			return false;
		
		// game.input_manager.eventUpdateDown(i, event);
		com.kobaj.math.Constants.input_manager.eventUpdateDown(i, event);
		return true;
	}
	
	@Override
	public boolean onKeyUp(int i, KeyEvent event)
	{
		// game.input_manager.eventUpdateUp(i, event);
		com.kobaj.math.Constants.input_manager.eventUpdateUp(i, event);
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		// game.input_manager.eventUpdate(e);
		com.kobaj.math.Constants.input_manager.eventUpdate(e);
		return true;
	}
}
