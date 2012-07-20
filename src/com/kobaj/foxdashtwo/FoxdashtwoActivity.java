package com.kobaj.foxdashtwo;

import com.kobaj.math.Constants;

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

public class FoxdashtwoActivity extends Activity {
    /** Called when the activity is first created. */
	
	private PowerManager.WakeLock wl;
	
	private com.kobaj.opengl.MyGLSurfaceView mGLView;
	
	//saving state
	public static SharedPreferences mPrefs;
	public static SharedPreferences.Editor ed;
	
	public static Handler itself;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//do context first, a lot of stuff relies on it.
		Constants.context = getApplicationContext();
		
		Constants.resources = this.getResources();
		
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
				Constants.dip_scale = 1.0;
				Constants.unknown_dip = true;
				com.kobaj.message.ToastManager.makeLongToast(getString(R.string.unknown_screen_size_message));
				break;
			case DisplayMetrics.DENSITY_XHIGH: //xhdpi 320
				Constants.dip_scale = 1.0 + 1.0 / 3.0;
				break;
			case DisplayMetrics.DENSITY_HIGH: //HDPI 240
				Constants.dip_scale = 1.0;
				break;
			case DisplayMetrics.DENSITY_MEDIUM: //MDPI 160
				Constants.dip_scale = 2.0 / 3.0;
				break;
			case DisplayMetrics.DENSITY_LOW:  //LDPI 120
				Constants.dip_scale = 0.5;
				break;
		}
		
		//fonts and text scale
		Constants.sd_scale = metrics.scaledDensity;
		
		//put in the other stuffs.
		Constants.width = display.getWidth();
		Constants.height = display.getHeight();
		
		Constants.ratio = (double) display.getWidth() / (double) display.getHeight();
		
		//touchy
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
		ed.commit();
		
		mGLView.onScreenPause();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		wl.acquire();
		
		mGLView.onResume();
	}
	
	//input
	
	@Override
	public boolean onKeyDown(int i, KeyEvent event)
	{
		if (i == KeyEvent.KEYCODE_VOLUME_DOWN || i == KeyEvent.KEYCODE_VOLUME_UP)
			return false;
		
		//game.input_manager.eventUpdateDown(i, event);
		com.kobaj.math.Constants.input_manager.eventUpdateDown(i, event);
		return true;
	}
	
	@Override
	public boolean onKeyUp(int i, KeyEvent event)
	{
		//game.input_manager.eventUpdateUp(i, event);
		com.kobaj.math.Constants.input_manager.eventUpdateUp(i, event);
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		//game.input_manager.eventUpdate(e);
		com.kobaj.math.Constants.input_manager.eventUpdate(e);
		return true;
	}
}