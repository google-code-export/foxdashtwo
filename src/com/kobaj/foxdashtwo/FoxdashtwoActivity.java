package com.kobaj.foxdashtwo;

import com.kobaj.activity.SurfacePanel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class FoxdashtwoActivity extends Activity {
    /** Called when the activity is first created. */
	
	private PowerManager.WakeLock wl;
	private SurfacePanel game;
	
	//saving state
	public static SharedPreferences mPrefs;
	public static SharedPreferences.Editor ed;
	
	public static Handler itself;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		mPrefs = getSharedPreferences("com.kobaj.foxdashtwo_prefs", 0);
		ed = mPrefs.edit();
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		game = new SurfacePanel(this);
		game.onInitialize();
		
		// last
		setContentView(game);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		wl.release();
		
		ed.commit();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		wl.acquire();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ed.commit();
	}
	
	@Override
	public boolean onKeyDown(int i, KeyEvent event)
	{
		if (i == KeyEvent.KEYCODE_VOLUME_DOWN || i == KeyEvent.KEYCODE_VOLUME_UP)
			return false;
		
		//game.im.eventUpdateDown(i, event);
		return true;
	}
	
	@Override
	public boolean onKeyUp(int i, KeyEvent event)
	{
		//game.im.eventUpdateUp(i, event);
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		//game.im.eventUpdate(e);
		return true;
	}
}