package com.kobaj.opengldrawable.Tween;

import android.graphics.Color;

public class TweenEvent
{
	// class holds all the values that will be lerped.
	
	public double x_pos;
	public double y_pos;
	public double degree;
	public int color;
	
	public EnumTweenEvent event;
	
	public TweenEvent(EnumTweenEvent event, double x_pos, double y_pos)
	{
		this(event, x_pos, y_pos, Color.WHITE);
	}
	
	public TweenEvent(EnumTweenEvent event, double x_pos, double y_pos, int color)
	{
		this(event, x_pos, y_pos, 0, color);
	}
	
	// these are in screen coodinates
	public TweenEvent(EnumTweenEvent event, double x_pos, double y_pos, int color, double degree)
	{
		this.event = event;
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.degree = degree;
		this.color = color;
	}
}
