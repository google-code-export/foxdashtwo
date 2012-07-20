package com.kobaj.math;

public class Functions
{
	//input is related to x scale
	//output is related to y scale
	public static final double linearInterpolate(double minX, double maxX, double value, double minY, double maxY)
	{
		if (minX == maxX)
			return minY;
		
		if(value < minX)
			return minY;
		
		if(value > maxX)
			return maxY;
	
		return linearInterpolateUnclamped(minX, maxX, value, minY, maxY);
	}
	
	//method that is unclamped.
	public static final double linearInterpolateUnclamped(double minX, double maxX, double value, double minY, double maxY)
	{
		return minY * (value - maxX) / (minX - maxX) + maxY * (value - minX) / (maxX - minX);
	}
	
	public static final double clamp(double max, double value, double min)
	{
		return Math.max(Math.min(value, max), min);
	}
	
	//makes up down and down up for screen coordinates.
	public static final double fix_y(double input)
	{
		return Constants.height - input;
	}
	
	//input 0 to 255, output 0 to 1
	//really helpful for color transformations
	public static final double byteToShader(int input)
	{
		return linearInterpolate(0, 255, input, 0, 1);
	}
	
	//used to translate screen widths to shader widths
	//for example, screen width is 0 to 800, shader is 0 to 1
	public static final double screenWidthToShaderWidth(double input_x)
	{
		return linearInterpolateUnclamped(0, Constants.width, input_x, 0, Constants.ratio);
	}
	
	public static final double screenHeightToShaderHeight(double input_y)
	{
		return linearInterpolateUnclamped(0, Constants.height, input_y, 0, 1);
	}
	
	//used to translate screen coordinates to shader coordinates
	//for example, screen width is 0 to 800px, shader is -1 to 1.
	public static final double screenXToShaderX(double input_x)
	{
		return linearInterpolateUnclamped(0, Constants.width, input_x, -Constants.ratio, Constants.ratio);
	}
	
	public static final double screenYToShaderY(double input_y)
	{
		return linearInterpolateUnclamped(0, Constants.height, input_y, -1, 1);
	}
	
	//random between two values
	public static final double randomDouble(double min, double max)
	{
		return min + (Math.random() * ((max - min) + 1));	
	}
	
	public static final int randomInt(int min, int max)
	{
		return (int)randomDouble(min, max);
	}
	
	//helpful to see whats on screen
	public static final boolean onScreen(int x, int y)
	{	
		if(x > 0 && x < Constants.width)
			if(y > 0 && y < Constants.height)
				return true;
		
		return false;
	}
	
	public static final boolean onShader(double x, double y)
	{
		if(x > -Constants.ratio && x < Constants.ratio)
			if(y > -1 && y < 1)
				return true;
		
		return false;
	}
	
	//radius stuff.
	public static final double rectangularToRadius(double x, double y)
	{
		return Math.sqrt(x * x + y * y);
	}
	
	public static final double polarToX(double degree, double radius)
	{
		return radius * Math.sin(Math.toRadians(degree));
	}
	
	public static final double polarToY(double degree, double radius)
	{
		return radius * Math.cos(Math.toRadians(degree));
	}
}