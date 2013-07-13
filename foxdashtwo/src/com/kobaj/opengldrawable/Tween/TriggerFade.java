package com.kobaj.opengldrawable.Tween;

import android.annotation.SuppressLint;

public class TriggerFade
{
	public double fade_time = 1000; // ms
	private int current_index;
	
	private TriggerFadeResource[] resources;
	
	// all resources must have same width and height
	public void onInitialize(int width, int height, int... resources_to_fade)
	{
		resources = new TriggerFadeResource[resources_to_fade.length];
		for (int i = resources.length - 1; i >= 0; i--)
			resources[i] = new TriggerFadeResource(width, height, resources_to_fade[i], i);
	}
	
	// triggers zero to turn off first
	public void trigger()
	{
		if(current_index < resources.length)
		current_index += 1;
	}
	
	public void onUpdate(double delta)
	{
		for (int i = resources.length - 1; i >= 0; i--)
		{
			resources[i].onUpdate(delta, fade_time, current_index);
		}
	}
	
	@SuppressLint("WrongCall")
	public void onDraw()
	{
		// draw them in order
		for (int i = 0; i < resources.length; i++)
		{
			resources[i].onDraw();
			
			if (i == current_index)
				break;
		}
	}
}
