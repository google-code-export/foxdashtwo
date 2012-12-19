package com.kobaj.opengldrawable.Tween;

import android.graphics.Color;

public class TweenEvent
{
	// class holds all the values that will be lerped.
	
	public double x_pos;
	public double y_pos;
	public double degree;
	public int color;
	
	public TweenEvent(double x_pos, double y_pos)
	{
		this(x_pos, y_pos, Color.WHITE);
	}
	
	public TweenEvent(double x_pos, double y_pos, int color)
	{
		this(x_pos, y_pos, 0, color);
	}
	
	// these are in screen coodinates
	public TweenEvent(double x_pos, double y_pos, int color, double degree)
	{
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.degree = degree;
		this.color = color;
	}
}
