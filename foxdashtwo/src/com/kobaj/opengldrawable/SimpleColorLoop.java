package com.kobaj.opengldrawable;

import com.kobaj.math.Functions;

public class SimpleColorLoop
{
	private final int[] colors;
	private final double delay_between_colors;
	
	private double current_delta;
	private int current_color_index;
	private int current_color;
	
	// this will cycle through a set of colors over the period of time specified
	// this could be accomplished with a long set of animations/tweens
	// but I wanted a simpler implementation
	// time is in ms
	public SimpleColorLoop(double time, int...colors)
	{
		if(time <= 0)
			time = 1000;
		
		delay_between_colors = time;
		
		this.colors = colors;
	}
	
	public void onUpdate(double delta)
	{
		current_delta += delta;
		
		if(current_delta > delay_between_colors)
		{
			// update everything
			current_delta = 0;
			current_color_index = (current_color_index + 1) % colors.length;
		}
		
		int next_index = (current_color_index + 1) % colors.length;
		
		current_color = Functions.linearInterpolateColor(0, delay_between_colors, current_delta, colors[current_color_index], colors[next_index]);
	}
	
	public int getCurrentColor()
	{
		return current_color;
	}
	
	public int pickRandomStart()
	{
		current_color_index = Functions.randomInt(0, colors.length - 1);
		current_color = colors[current_color_index];
		return current_color;
	}
	
}
