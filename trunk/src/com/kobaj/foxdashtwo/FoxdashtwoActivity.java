package com.kobaj.foxdashtwo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class FoxdashtwoActivity extends Activity {
    /** Called when the activity is first created. */
	
	private PowerManager.WakeLock wl;
	
	private GLSurfaceView mGLView;
	
	//saving state
	public static SharedPreferences mPrefs;
	public static SharedPreferences.Editor ed;
	
	public static Handler itself;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//do context first, a lot of stuff relies on it.
		com.kobaj.math.Constants.context = getApplicationContext();
		
		//grabbing save states
		mPrefs = getSharedPreferences("com.kobaj.foxdashtwo_prefs", 0);
		ed = mPrefs.edit();
		
		//keeping the screen on
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
		
		//volume controls
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		//findout out the dpi
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = getWindowManager().getDefaultDisplay();
		display.getMetrics(metrics);

		switch(metrics.densityDpi)
		{
			default:
				com.kobaj.math.Constants.dip_scale = 1.0;
				com.kobaj.math.Constants.unknown_dip = true;
				com.kobaj.message.ToastManager.makeLongToast(getString(R.string.unknown_screen_size_message));
				break;
			case DisplayMetrics.DENSITY_XHIGH: //xhdpi 320
				com.kobaj.math.Constants.dip_scale = 1.0 + 1.0 / 3.0;
				break;
			case DisplayMetrics.DENSITY_HIGH: //HDPI 240
				com.kobaj.math.Constants.dip_scale = 1.0;
				break;
			case DisplayMetrics.DENSITY_MEDIUM: //MDPI 160
				com.kobaj.math.Constants.dip_scale = 2.0 / 3.0;
				break;
			case DisplayMetrics.DENSITY_LOW:  //LDPI 120
				com.kobaj.math.Constants.dip_scale = 0.5;
				break;
		}
		
		//fonts and text scale
		com.kobaj.math.Constants.sd_scale = metrics.scaledDensity;
		
		//put in the other stuffs.
		com.kobaj.math.Constants.width = display.getWidth();
		com.kobaj.math.Constants.height = display.getHeight();
		
		com.kobaj.math.Constants.ratio = (double) display.getWidth() / (double) display.getHeight();
		
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
		ed.commit();
		
		mGLView.onPause();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		wl.acquire();
		
		mGLView.onResume();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ed.commit();
	}
	
	//input
	
	@Override
	public boolean onKeyDown(int i, KeyEvent event)
	{
		if (i == KeyEvent.KEYCODE_VOLUME_DOWN || i == KeyEvent.KEYCODE_VOLUME_UP)
			return false;
		
		//game.input_manager.eventUpdateDown(i, event);
		return true;
	}
	
	@Override
	public boolean onKeyUp(int i, KeyEvent event)
	{
		//game.input_manager.eventUpdateUp(i, event);
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		//game.input_manager.eventUpdate(e);
		return true;
	}
}