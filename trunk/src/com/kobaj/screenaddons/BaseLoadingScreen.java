package com.kobaj.screenaddons;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class BaseLoadingScreen
{
	//eventually these will be moved to constants.
	private int radius = 5;
	private int max_shapes = 8;
	private int primary_color = 0xFFFFFFFF;
	private int secondary_color = 0x33FFFFFF;
	private double delta_shift = 30;
	
	//this will not be moved to constants (obviously XP)
	private QuadColorShape[] my_shapes = new QuadColorShape[max_shapes];
	private double total_delta = 0;
	
	public BaseLoadingScreen()
	{
		for(int i = 0; i < max_shapes; i++)
			my_shapes[i] = new QuadColorShape(radius, primary_color, 0);
	}
	
	public void onDrawLoading(double delta)
	{
		total_delta += delta;
	
		for(int i = 0; i < max_shapes; i++)
		{
			//set their position
			setPosition(my_shapes[i], i);
			
			//draw them all
			my_shapes[i].onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);	
		}
	}
	
	private void setPosition(QuadColorShape my_quad, int ith_pos)
	{
		double local_total_delta = total_delta - delta_shift * (double) ith_pos;
		
		//calculate r
		double r = Math.sin(Math.toRadians(local_total_delta));
		
		//calculate x;
		double x = Functions.polarToX(local_total_delta, r);
		
		//calculate y;
		double y = Functions.polarToY(local_total_delta, r);
		
		//set it all
		//my_quad.setPos(x, y, EnumDrawFrom.center);
	}
}
