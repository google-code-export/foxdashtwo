package com.kobaj.opengldrawable.Tween;

import android.annotation.SuppressLint;

public class TriggerFade
{
	private int current_index;
	
	private TriggerFadeResource[] resources;
	
	// all resources must have same width and height
	public void onInitialize(int width, int height, int... resources_to_fade)
	{
		onInitialize(width, height, false, resources_to_fade);
	}
	
	public void onInitialize(int width, int height, boolean credits, int... resources_to_fade)
	{
		resources = new TriggerFadeResource[resources_to_fade.length];
		for (int i = resources.length - 1; i >= 0; i--)
		{
			if (credits)
			{
				resources[i] = new TriggerFadeResourceCredits(width, height, resources_to_fade[i], i);
			}
			else
			{
				resources[i] = new TriggerFadeResource(width, height, resources_to_fade[i], i);
			}
		}
		
	}
	
	// triggers zero to turn off first
	public void trigger()
	{
		if (current_index < resources.length)
			current_index += 1;
	}
	
	public void onUpdate(double delta)
	{
		for (int i = resources.length - 1; i >= 0; i--)
		{
			resources[i].onUpdate(delta, current_index);
		}
	}
	
	@SuppressLint("WrongCall")
	public void onDraw()
	{
		// draw them in order
		for (int i = resources.length - 1; i >= 0; i--)
		{
			resources[i].onDraw();
		}
	}
}
