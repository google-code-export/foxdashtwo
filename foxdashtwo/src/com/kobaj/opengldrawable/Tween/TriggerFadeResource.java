package com.kobaj.opengldrawable.Tween;

import android.graphics.Color;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public class TriggerFadeResource
{
	public QuadCompressed resources;
	public int index;
	public double current_fade_times;
	public int color = Color.WHITE;
	
	public double max_fade = 1000;
	
	public TriggerFadeResource(int width, int height, int resource_id, int index)
	{
		this.index = index;
		resources = new QuadCompressed(resource_id, R.raw.white, width, height);
	}
	
	public void onUpdate(double delta, int global_index)
	{
		if(global_index <= index)
			return;
		
		if (current_fade_times < max_fade)
		{
			current_fade_times += delta;
			
			if (current_fade_times > max_fade)
				current_fade_times = max_fade;
		}
		
		setColorAndFade(max_fade);
	}
	
	private void setColorAndFade(double max_fade)
	{
		if (current_fade_times == max_fade)
		{
			color = Color.TRANSPARENT;
			return;
		}
		
		color = Functions.linearInterpolateColor(0, max_fade, current_fade_times, Color.WHITE, Color.TRANSPARENT);
	}
	
	public void onDraw()
	{
		if(color == Color.TRANSPARENT)
			return;
		
		resources.color = color;
		resources.onDrawAmbient(Constants.my_ip_matrix, true);
	}
}
