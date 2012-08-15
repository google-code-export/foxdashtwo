package com.kobaj.opengldrawable;

public class Particle
{
	public Quad actual_quad;
	public Quad bloom_quad;
	
	// if it is orbit, these help
	public double radius;
	public double degree; // yes, degrees
	public double speed;
	public double travel_time = 0;
	public double start_x;
	public double start_y;
	public double go_x;
	public double go_y;
	public double max_travel_time;
	
	// shader coordinates
	public void reset(double x, double y)
	{
		// reset everything.
		actual_quad.setPos(x, y, EnumDrawFrom.center);
		actual_quad.x_vel = 0;
		actual_quad.y_vel = 0;
		
		if (bloom_quad != null)
		{
			bloom_quad.setPos(x, y, EnumDrawFrom.center);
			bloom_quad.x_vel = 0;
			bloom_quad.y_vel = 0;
		}
		
		travel_time = 0;
	}
	
	// screen coordinates
	private double percent(double spread, double percent)
	{
		double ten_percent = percent * spread;
		double min = spread - ten_percent;
		double max = spread + ten_percent;
		
		double half_width = com.kobaj.math.Functions.randomDouble(min, max);
		if (com.kobaj.math.Functions.randomInt(0, 1) == 1)
			half_width = -half_width;
		
		return half_width / 1000.0;
	}
	
	private double getHalfWidth(double spread)
	{
		double half_width = com.kobaj.math.Functions.randomDouble(0, spread);
		if (com.kobaj.math.Functions.randomInt(0, 1) == 1)
			half_width = -half_width;
		
		return half_width / 1000.0;
	}
	
	// screen coordinates
	public void addLeftRight(double spread)
	{
		// scale things to shader coords
		double value = com.kobaj.math.Functions.screenWidthToShaderWidth(getHalfWidth(spread));
		actual_quad.x_vel += value;
		if (bloom_quad != null)
			bloom_quad.x_vel += value;
	}
	
	// screen coordinates
	public void addUpdown(double height)
	{
		double value = Math.abs(com.kobaj.math.Functions.screenHeightToShaderHeight(getHalfWidth(height))) * 4.5;
		actual_quad.y_vel += value;
		if (bloom_quad != null)
			bloom_quad.x_vel += value;
	}
	
	// screen coordinates
	public void setRadiusDegree(double radius, double speed, double shader_x, double shader_y, EnumParticleMovement movement)
	{
		travel_time = 0;
		
		actual_quad.y_vel = 0;
		actual_quad.x_vel = 0;
		
		this.radius = Math.abs(com.kobaj.math.Functions.screenWidthToShaderWidth(getHalfWidth(radius))) * 3000.0;
		this.speed = com.kobaj.math.Functions.screenWidthToShaderWidth(percent(speed, 0.35)); 
		
		if (movement == EnumParticleMovement.orbit || movement == EnumParticleMovement.frantic)
		{
			start_x = actual_quad.get_x_pos();
			start_y = actual_quad.get_y_pos();
		
			// calculate the tangent degree.
			double degree_to_target = com.kobaj.math.Functions.rectangularToDegree(start_x - shader_x, start_y - shader_y);
			if ((degree_to_target >= 90 && degree_to_target <= 180) || (degree_to_target <= 0 && degree_to_target >= -90))
				degree_to_target += 180;
			
			if (this.speed >= 0)
				degree_to_target += 90;
			else
				degree_to_target -= 90;
			this.degree = degree_to_target;
		}
		else
		{
			actual_quad.setPos(0 + shader_x, 0 + shader_y, EnumDrawFrom.center);
			
			start_x = 0 + shader_x;
			start_y = 0 + shader_y;
			
			this.degree = com.kobaj.math.Functions.randomDouble(0, 360);
		}
		
		// where we need to go
		go_x = com.kobaj.math.Functions.polarToX(this.degree, this.radius) + shader_x;
		go_y = com.kobaj.math.Functions.polarToY(this.degree, this.radius) + shader_y;
		
		// time it takes to get there
		if(movement == EnumParticleMovement.orbit)
			max_travel_time = 1000.0 * com.kobaj.math.Functions.rectangularToRadius(go_x - start_x, go_y - start_y);
		else
			max_travel_time = percent(speed, 0.25) * 1000.0 * 10.0 * com.kobaj.math.Functions.rectangularToRadius(go_x - start_x, go_y - start_y);
	}
}
