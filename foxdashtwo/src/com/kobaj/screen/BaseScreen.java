package com.kobaj.screen;

import android.graphics.Color;

import com.kobaj.foxdashtwo.R;
import com.kobaj.level.EnumLayerTypes;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadCompressed;
import com.kobaj.opengldrawable.Tween.EnumTweenEvent;
import com.kobaj.opengldrawable.Tween.TweenEvent;
import com.kobaj.opengldrawable.Tween.TweenManager;

public abstract class BaseScreen implements Runnable
{
	public EnumScreenState current_state = EnumScreenState.not_started;
	
	protected QuadCompressed black_overlay_fade;
	protected QuadCompressed color_overlay;
	protected TweenManager tween_fade_in;
	protected TweenManager tween_fade_out;
	
	private EnumScreenState previous_state = EnumScreenState.not_started;
	
	public final void onInitialize()
	{
		previous_state = current_state;
		current_state = EnumScreenState.loading;
		new Thread(this).start();
	}
	
	public final void onUnInitialize()
	{
		try
		{
			current_state = EnumScreenState.unload;
			
			// unload everything
			black_overlay_fade.onUnInitialize();
			color_overlay.onUnInitialize();
			onUnload();
			
			current_state = EnumScreenState.stopped;
		}
		catch (NullPointerException e)
		{
			// do nothing
		}
	}
	
	public void run()
	{
		// nice overlay for fade in and out
		black_overlay_fade = new QuadCompressed(R.raw.white, R.raw.white, Constants.width, Constants.height);
		black_overlay_fade.setXYPos(0, 0, EnumDrawFrom.center);
		black_overlay_fade.color = Color.BLACK;
		
		recomputeDefaultTweens();
		
		color_overlay = new QuadCompressed(R.raw.white, R.raw.white, Constants.width, Constants.height);
		color_overlay.setXYPos(0, 0, EnumDrawFrom.center);
		color_overlay.color = Color.BLACK;
		
		// load everything else.
		onLoad();
		
		if (previous_state == EnumScreenState.paused)
			current_state = EnumScreenState.paused;
		else
			current_state = EnumScreenState.running;
	}
	
	protected void recomputeDefaultTweens()
	{
		tween_fade_in = new TweenManager(black_overlay_fade, new TweenEvent(EnumTweenEvent.color, 0, 0, Color.BLACK), //
				Constants.fade_delay, // ms fade in.
				new TweenEvent(EnumTweenEvent.color, 0, 0, Color.TRANSPARENT));
		tween_fade_out = new TweenManager(black_overlay_fade, new TweenEvent(EnumTweenEvent.color, 0, 0, Color.TRANSPARENT), //
				Constants.fade_delay, //
				new TweenEvent(EnumTweenEvent.color, 0, 0, Color.BLACK));
	}
	
	// loading things on a seperate thread
	public abstract void onLoad();
	
	public abstract void onUnload();
	
	// update the main thread login
	public abstract void onUpdate(double delta);
	
	// draw most everything
	public abstract void onDrawObject(EnumLayerTypes... types);
	
	public abstract void onDrawLight();
	
	public abstract void onDrawConstant(); // put drawtext here
	
	// draw a loading screen. only time we do math in draw
	public abstract void onDrawLoading(double delta);
	
	// do something when the system pauses
	public abstract void onPause();
	
	// prepare for shutdown
	public abstract void onScreenChange(boolean next_level);
}
