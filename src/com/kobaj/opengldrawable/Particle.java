package com.kobaj.opengldrawable;

public class Particle
{
	public Quad actual_quad;
	public Quad bloom_quad;
	
	//if it is orbit, these help
	public double radius;
	public double degree; //yes, degrees
	public double speed;
	
	public void reset(double x, double y)
	{
		//reset everything.
		actual_quad.setPos(x, y, EnumDrawFrom.center);
		actual_quad.x_vel = 0;
		actual_quad.y_vel = 0;
		if(bloom_quad != null)
		{
			bloom_quad.setPos(x, y, EnumDrawFrom.center);
			bloom_quad.x_vel = 0;
			bloom_quad.y_vel = 0;
		}
	}
	
	//screen coordinates
	private double getHalfWidth(double spread)
	{
		/*double ten_percent = .10 * spread;
		double min = spread - ten_percent;
		double max = spread + ten_percent;*/
		
		double half_width = com.kobaj.math.Functions.randomDouble(0, spread);
		if(com.kobaj.math.Functions.randomInt(0, 1) == 1)
			half_width = -half_width;
		
		return half_width / 1000.0;
	}
	
	//screen coordinates
	public void addLeftRight(double spread)
	{
		//scale things to shader coords 
		double value = com.kobaj.math.Functions.screenWidthToShaderWidth(getHalfWidth(spread));
		actual_quad.x_vel += value;
		if(bloom_quad != null)
			bloom_quad.x_vel += value;
	}
	
	//screen coordinates
	public void addUpdown(double height)
	{
		double value = Math.abs(com.kobaj.math.Functions.screenHeightToShaderHeight(getHalfWidth(height))) * 4.5;
		actual_quad.y_vel += value;
		if(bloom_quad != null)
			bloom_quad.x_vel += value;
	}
	
	//screen coordinates
	public void addRadiusDegree(double radius, double speed)
	{
		this.radius = Math.abs(com.kobaj.math.Functions.screenWidthToShaderWidth(getHalfWidth(radius)));
		this.speed = com.kobaj.math.Functions.screenWidthToShaderWidth(speed); //width is kinda bad, but it will do. everything is approx anyway.
		this.degree = com.kobaj.math.Functions.randomInt(0, 360);
	}
}
