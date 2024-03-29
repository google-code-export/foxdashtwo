package com.kobaj.opengldrawable.NewParticle;

import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.Quad.Quad;

public class NParticle
{
	private int life_time;
	private int original_life_time;
	private final int fade_out_time;
	private final int fade_in_time;
	private final boolean vary_scale;
	
	private int current_time;
	
	public Quad quad_reference;
	public boolean is_dead = false;
	private boolean killed = false;
	
	public int red_reference = 255;
	public int green_reference = 255;
	public int blue_reference = 255;
	
	public boolean preUpdate = true;
	
	public int count_off = 0;
	
	public NParticle(boolean vary_scale, int fade_in_time, int fade_out_time, int life_time)
	{
		this.fade_in_time = fade_in_time;
		this.fade_out_time = fade_out_time;
		this.life_time = life_time;
		this.original_life_time = life_time;
		this.vary_scale = vary_scale;
	}
	
	public void onInitialize()
	{
		if (vary_scale)
			quad_reference.setScale(Functions.randomDouble(0.3, 1.0));
	}
	
	public void reset()
	{
		current_time = 0;
		is_dead = false;
		killed = false;
		this.life_time = original_life_time;
		preUpdate = false;
	}
	
	public void onUpdate(double delta)
	{
		if (!is_dead)
		{
			current_time += delta;
			
			// white
			// quad_reference.color = Functions.makeColor(red_reference, green_reference, blue_reference, 255);
			
			// fade in
			if (current_time <= fade_in_time)
				quad_reference.color = Functions.makeColor(red_reference, green_reference, blue_reference, (int) Functions.linearInterpolate(0, fade_in_time, current_time, 0, 255));
			
			// fade out
			int fade_time = life_time - fade_out_time;
			if (current_time > fade_time)
			{
				int amount_fade = current_time - fade_time;
				quad_reference.color = Functions.makeColor(red_reference, green_reference, blue_reference, (int) Functions.linearInterpolate(0, fade_out_time, amount_fade, 255, 0));
			}
		}
		
		if (current_time > life_time)
			is_dead = true;
	}
	
	// this will make the particle fade out to its death
	public void kill()
	{
		if(!killed)
		{
			life_time = current_time + fade_out_time;
			killed = true;
		}
	}
}
