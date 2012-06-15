package com.kobaj.math;

public class Functions
{
	public static <T> T linearInterpolate(T first)
	{
		return first;
	}
	
	public static double clamp(double max, double value, double min)
	{
		return Math.max(Math.min(value, max), min);
	}
}