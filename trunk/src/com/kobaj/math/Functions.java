package com.kobaj.math;

public class Functions
{
	//input is related to x scale
	//output is related to y scale
	public static double linearInterpolate(double minX, double maxX, double value, double minY, double maxY)
	{
		if (minX == maxX)
			return minY;
		
		if(value < minX)
			return minY;
		
		if(value > maxX)
			return maxY;
		
		return minY * (value - maxX) / (minX - maxX) + maxY * (value - minX) / (maxX - minX);
	}
	
	public static double clamp(double max, double value, double min)
	{
		return Math.max(Math.min(value, max), min);
	}
	
	public static double fix_y(double input)
	{
		return Constants.height - input;
	}
}