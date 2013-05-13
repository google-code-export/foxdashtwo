package com.kobaj.screen.screenaddons;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class RotationLoadingJig extends BaseLoadingScreen
{
	protected double local_delta = 0;
	
	// shader coords
	public double radius = 1.0;
	
	// shader coords
	public double x_pos = 0.0;
	public double y_pos = 0.0;
	
	//higher is faster
	public double speed = 100.0;
	
	public RotationLoadingJig()
	{
		loading_delta_shift = Math.PI / shape_count * 2.0;
	}
	
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
		total_delta += (Math.PI / shape_count) * delta * (speed / 10000.0);
		
		if (total_delta >= Math.PI * 4.0)
			total_delta = 0;
		
		for (int i = 0; i < shape_count; i++)
		{
			// set their position
			setPosition(my_shapes[i], i);
			
			// draw them all
			if(vp_matrix)
				my_shapes[i].onDrawAmbient(Constants.my_vp_matrix, false);
			else
				my_shapes[i].onDrawAmbient(Constants.my_ip_matrix, false);
		}
	}
	
	@Override
	protected void setPosition(QuadColorShape my_quad, double ith_pos)
	{
		double local_total_delta = total_delta - loading_delta_shift * ith_pos;
		
		// calculate r in a circle
		double r = radius;
		
		// calculate x
		double x = (Constants.ratio / Constants.my_ratio) * Functions.polarRadToX(local_total_delta, r) + x_pos;
		
		// calculate y;
		double y = (Constants.ratio / Constants.my_ratio) * Functions.polarRadToY(local_total_delta, r) + y_pos;
		
		// set it all
		my_quad.setXYPos(x, y, EnumDrawFrom.center);
	}
}
