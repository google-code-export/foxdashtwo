package com.kobaj.level.LevelTypeLight;

import org.simpleframework.xml.Element;

import android.graphics.Color;

import com.kobaj.foxdashtwo.R;
import com.kobaj.level.LevelEntityActive;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadColorShape;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public class LevelAmbientLight extends LevelEntityActive
{
	@Element
	public String id = "unset"; // identifier.
	
	@Element
	public int color;
	
	public Quad quad_light;
	
	private double fade_delta = 0;
	private boolean old_active;
	
	public void onInitialize()
	{
		quad_light = new QuadCompressed(R.raw.white, R.raw.white, Constants.width, Constants.height);
		quad_light.setXYPos(0, 0, EnumDrawFrom.center);
		quad_light.color = color;
		
		if (!active)
			quad_light.color = Color.BLACK;
		
		old_active = active;
	}
	
	public void onUnInitialize()
	{
		quad_light.onUnInitialize();
	}
	
	public void onUpdate(double delta)
	{
		if (fade_delta > 0)
			fade_delta -= delta;
		else if (fade_delta <= 0)
			fade_delta = 0;
		
		if (old_active != active)
		{
			fade_delta = Constants.light_active_fade - fade_delta;
			old_active = active;
		}
	
		if (active) // turn on
		{
			if (quad_light.color != color)
			{
				quad_light.color = Functions.linearInterpolateColor(0, Constants.light_active_fade, fade_delta, color, Color.BLACK);
			}
		}
		else
		// turn off
		{
			if (quad_light.color != Color.BLACK)
			{
				quad_light.color = Functions.linearInterpolateColor(0, Constants.light_active_fade, fade_delta, Color.BLACK, color);
			}
			
		}
	}
	
	public void onDrawLight()
	{
		if (active || quad_light.color != Color.BLACK)
			quad_light.onDrawAmbient(Constants.my_ip_matrix, true);
	}
}
