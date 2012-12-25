package com.kobaj.screen;

import android.graphics.Color;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadCompressed;
import com.kobaj.opengldrawable.Tween.EnumTweenEvent;
import com.kobaj.opengldrawable.Tween.TweenEvent;
import com.kobaj.opengldrawable.Tween.TweenManager;

public abstract class BaseScreen implements Runnable
{
	public EnumScreenState current_state = EnumScreenState.not_started;
	
	// constructor should be used to initialize things, but not load them.
	
	protected Quad black_overlay;
	protected TweenManager tween_fade_in;
	protected TweenManager tween_fade_out;
	
	public final void onInitialize()
	{
		current_state = EnumScreenState.loading;
		new Thread(this).start();
	}
	
	public final void onUnInitialize()
	{
		current_state = EnumScreenState.unload;
		
		black_overlay.onUnInitialize();
		onUnload();
		
		current_state = EnumScreenState.stopped;
	}
	
	public void run()
	{
		// black_overlay = new QuadColorShape(1,1,Color.BLACK, 0); //if you want to use quadcolorshapes instead of compressed
		// black_overlay.setWidthHeight(Constants.width, Constants.height);
		
		black_overlay = new QuadCompressed(R.raw.black_alpha, R.raw.black_alpha, Constants.width, Constants.height);
		black_overlay.setXYPos(0, 0, EnumDrawFrom.center);
		black_overlay.color = Color.BLACK;
		
		int fade_delay = 200;
		tween_fade_in = new TweenManager(black_overlay, new TweenEvent(EnumTweenEvent.color, 0, 0, Color.BLACK), //
				fade_delay, // ms fade in.
				new TweenEvent(EnumTweenEvent.color, 0, 0, Color.TRANSPARENT));
		tween_fade_out = new TweenManager(black_overlay, new TweenEvent(EnumTweenEvent.color, 0, 0, Color.TRANSPARENT), //
				fade_delay, //
				new TweenEvent(EnumTweenEvent.color, 0, 0, Color.BLACK));
		
		onLoad();
		current_state = EnumScreenState.running;
	}
	
	// loading things on a seperate thread
	public abstract void onLoad();
	
	public abstract void onUnload();
	
	// update the main thread login
	public abstract void onUpdate(double delta);
	
	// draw most everything
	public abstract void onDrawObject();
	
	public abstract void onDrawLight();
	
	public abstract void onDrawConstant(); // put drawtext here
	
	// draw a loading screen. only time we do math in draw
	public abstract void onDrawLoading(double delta);
	
	// do something when the system pauses
	public abstract void onPause();
}
