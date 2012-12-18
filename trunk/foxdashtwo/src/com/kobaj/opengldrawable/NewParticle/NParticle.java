package com.kobaj.opengldrawable.NewParticle;

import android.graphics.Color;

import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class NParticle
{
	private final int life_time;
	private final int fade_out_time;
	private final int fade_in_time;
	private final boolean vary_scale;
	
	private int current_time;
	
	public Quad quad_reference;
	public boolean is_dead = false;
	
	public NParticle(boolean vary_scale, int fade_in_time, int fade_out_time, int life_time)
	{
		this.fade_in_time = fade_in_time;
		this.fade_out_time = fade_out_time;
		this.life_time = life_time;
		this.vary_scale = vary_scale;
	}
	
	public void onInitialize()
	{
		//this will be changed in the future to compressed user chosen quads
		this.quad_reference = new QuadColorShape(8, 0x99000099, true, 0);
		
		if(vary_scale)
			quad_reference.setScale(Functions.randomDouble(0.3, 1.0));
	}
	
	public void reset()
	{
		current_time = 0;
		is_dead = false;
	}
	
	public void onUpdate(double delta)
	{	
		if(!is_dead)
		{
			current_time += delta;
			
			//fade in
			if(current_time <= fade_in_time)
			{
				quad_reference.color = Functions.makeColor(255,255,255,(int) Functions.linearInterpolate(0, fade_out_time, current_time, 0, 255));
			}
			
			//white
			quad_reference.color = Color.WHITE;
			
			//fade out
			int fade_time = life_time - fade_out_time;
			if(current_time > fade_time)
			{
				int amount_fade = current_time - fade_time;
				quad_reference.color = Functions.makeColor(255,255,255,(int) Functions.linearInterpolate(0, fade_out_time, amount_fade, 255, 0));
			}
		}
		
		if(current_time > life_time)
			is_dead = true;
	}
}
