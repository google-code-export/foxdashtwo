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
	
		return unclampedLinearInterpolate(minX, maxX, value, minY, maxY);
	}
	
	//method that is unclamped.
	public static double unclampedLinearInterpolate(double minX, double maxX, double value, double minY, double maxY)
	{
		return minY * (value - maxX) / (minX - maxX) + maxY * (value - minX) / (maxX - minX);
	}
	
	public static double clamp(double max, double value, double min)
	{
		return Math.max(Math.min(value, max), min);
	}
	
	//makes up down and down up for screen coordinates.
	public static double fix_y(double input)
	{
		return Constants.height - input;
	}
	
	//input 0 to 255, output 0 to 1
	//really helpful for color transformations
	public static double byteToShader(int input)
	{
		return linearInterpolate(0, 255, input, 0, 1);
	}
	
	//used to translate screen widths to shader widths
	//for example, screen width is 0 to 800, shader is 0 to 1
	public static double screenWidthToShaderWidth(int input_x)
	{
		return unclampedLinearInterpolate(0, Constants.width, input_x, 0, Constants.ratio);
	}
	
	public static double screenHeightToShaderHeight(int input_y)
	{
		return unclampedLinearInterpolate(0, Constants.height, input_y, 0, 1);
	}
	
	//used to translate screen coordinates to shader coordinates
	//for example, screen width is 0 to 800px, shader is -1 to 1.
	public static double screenXToShaderX(int input_x)
	{
		return unclampedLinearInterpolate(0, Constants.width, input_x, -Constants.ratio, Constants.ratio);
	}
	
	public static double screenYToShaderY(int input_y)
	{
		return unclampedLinearInterpolate(0, Constants.height, input_y, -1, 1);
	}
}