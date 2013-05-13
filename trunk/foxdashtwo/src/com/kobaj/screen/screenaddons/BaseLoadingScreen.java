package com.kobaj.screen.screenaddons;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class BaseLoadingScreen
{
	// this will not be moved to constants (obviously XP)
	protected QuadColorShape[] my_shapes;
	protected double total_delta = 0;
	
	protected int shape_count = Constants.loading_max_shapes;
	
	protected double loading_delta_shift = Math.PI / shape_count;
	
	protected int primary_color = Constants.loading_primary_color;
	protected int secondary_color = Constants.loading_secondary_color;
	
	public boolean vp_matrix = false;
	
	public void onInitialize()
	{	
		my_shapes = new QuadColorShape[shape_count];
		
		for (int i = 0; i < shape_count; i++)
			my_shapes[i] = new QuadColorShape(Constants.loading_radius, getColor(i), 0);
	}
	
	public void onUnInitialize()
	{
		for (int i = 0; i < shape_count; i++)
			my_shapes[i].onUnInitialize();
	}
	
	// this function will linear interpolate between the two colors
	public int getColor(int i)
	{
		return Functions.linearInterpolateColor(0, shape_count - 1, i, primary_color, secondary_color);
	}
	
	public void onDrawLoading(double delta)
	{
		total_delta += (Math.PI / shape_count) * delta / 250.0;
		
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
		
		// draw some text
		Constants.text.drawText(R.string.loading, 0, 0, EnumDrawFrom.center);
	}
	
	protected void setPosition(QuadColorShape my_quad, double ith_pos)
	{
		double local_total_delta = total_delta - loading_delta_shift * ith_pos;
		
		// calculate r
		// double r = Math.cos(local_total_delta) * Math.sin(local_total_delta); //4 leaf clover
		// double r = Math.cos(.5 * local_total_delta); //2 smaller circles in 2 bigger circles
		double r = Math.cos(.5 * local_total_delta) * 2.0 * Math.sin(local_total_delta); // jumpy one.
		
		// these are purposely flipped to make it 'look cooler'
		// calculate x;
		double y = Functions.polarRadToX(local_total_delta, r);
		
		// calculate y;
		double x = Constants.ratio * Functions.polarRadToY(local_total_delta, r);
		
		// set it all
		my_quad.setXYPos(x, y, EnumDrawFrom.center);
	}
}
