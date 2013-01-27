package com.kobaj.screen.screenaddons;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class RotationLoadingJig extends BaseLoadingScreen
{
	protected double loading_delta_shift = Math.PI / Constants.loading_max_shapes * 2.0;
	
	private double local_delta = 0;
	
	public double radius = 1.0;
	public double x_pos = 0.0;
	public double y_pos = 0.0;
	
	//higher is faster
	public double speed = 100.0;
	
	public void onUpdate(double delta)
	{
		local_delta = delta;
	}
	
	public void onDrawLoading()
	{
		onDrawLoading(local_delta);
	}
	
	@Override
	public void onDrawLoading(double delta)
	{
		total_delta += (Math.PI / Constants.loading_max_shapes) * delta * (speed / 10000.0);
		
		if (total_delta >= Math.PI * 4.0)
			total_delta = 0;
		
		for (int i = 0; i < Constants.loading_max_shapes; i++)
		{
			// set their position
			setPosition(my_shapes[i], i);
			
			// draw them all
			my_shapes[i].onDrawAmbient(Constants.my_ip_matrix, true);
		}
	}
	
	@Override
	protected void setPosition(QuadColorShape my_quad, double ith_pos)
	{
		double local_total_delta = total_delta - loading_delta_shift * ith_pos;
		
		// calculate r in a circle
		double r = radius;
		
		// calculate x
		double x = Functions.polarRadToX(local_total_delta, r) + x_pos;
		
		// calculate y;
		double y = Functions.polarRadToY(local_total_delta, r) + y_pos;
		
		// set it all
		my_quad.setXYPos(x, y, EnumDrawFrom.center);
	}
}
