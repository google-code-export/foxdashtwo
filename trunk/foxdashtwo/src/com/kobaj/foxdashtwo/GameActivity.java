package com.kobaj.foxdashtwo;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.kobaj.loader.FileHandler;
import com.kobaj.math.Constants;

public class GameActivity extends FragmentActivity
{
	/** Called when the activity is first created. */
	
	// the fuck are you doing?!
	public static Activity activity;
	
	// related to permissions mostly
	public static ConnectivityManager cm;
	
	// drawing
	public static com.kobaj.opengl.MyGLSurfaceView mGLView;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// do context first, a lot of stuff relies on it.
		Constants.context = getApplicationContext();
		Constants.fragment_manager = getSupportFragmentManager();
		
		Constants.resources = this.getResources();
		
		// keeping the screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// networking state
		cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
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
		Constants.input_manager = new com.kobaj.input.InputManager();
		
		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity
		mGLView = new com.kobaj.opengl.MyGLSurfaceView(this);
		setContentView(mGLView);
		
		// make a nomedia file for other applications
		if (!FileHandler.fileExists(".nomedia"))
			FileHandler.writeTextFile(".nomedia", "");
		
		GameActivity.activity = this;
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		// shut down the music
		Constants.music_player.stop();
		
		mGLView.onScreenPause();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		mGLView.onResume();
	}
	
	// input
	
	@Override
	public boolean onKeyDown(int i, KeyEvent event)
	{
		if (i == KeyEvent.KEYCODE_VOLUME_DOWN || i == KeyEvent.KEYCODE_VOLUME_UP)
			return false;
		
		com.kobaj.math.Constants.input_manager.eventUpdateDown(i, event);
		return true;
	}
	
	@Override
	public boolean onKeyUp(int i, KeyEvent event)
	{
		com.kobaj.math.Constants.input_manager.eventUpdateUp(i, event);
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		com.kobaj.math.Constants.input_manager.eventUpdate(e);
		return true;
	}
}
