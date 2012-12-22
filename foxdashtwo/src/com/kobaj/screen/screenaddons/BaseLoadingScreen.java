package com.kobaj.screen.screenaddons;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class BaseLoadingScreen
{
	// this will not be moved to constants (obviously XP)
	private QuadColorShape[] my_shapes = new QuadColorShape[Constants.loading_max_shapes];
	private double total_delta = 0;
	private double loading_delta_shift = Math.PI / Constants.loading_max_shapes;
	
	public void onInitialize()
	{
		for (int i = 0; i < Constants.loading_max_shapes; i++)
			my_shapes[i] = new QuadColorShape(Constants.loading_radius, getColor(i), 0);
	}
	
	public void onUnInitialize()
	{
		for (int i = 0; i < Constants.loading_max_shapes; i++)
			my_shapes[i].onUnInitialize();
	}
	
	// this function will linear interpolate between the two colors
	public int getColor(int i)
	{
		return Functions.linearInterpolateColor(0, Constants.loading_max_shapes - 1, i, Constants.loading_primary_color, Constants.loading_secondary_color);
	}
	
	public void onDrawLoading(double delta)
	{
		total_delta += (Math.PI / 8) * delta / 250;
		
		if (total_delta >= Math.PI * 4)
			total_delta = 0;
		
		for (int i = 0; i < Constants.loading_max_shapes; i++)
		{
			// set their position
			setPosition(my_shapes[i], i);
			
			// draw them all
			my_shapes[i].onDrawAmbient();
		}
		
		// draw some text
		Constants.text.drawText(R.string.loading, 0, 0, EnumDrawFrom.center);
	}
	
	private void setPosition(QuadColorShape my_quad, int ith_pos)
	{
		double local_total_delta = total_delta - loading_delta_shift * (double) ith_pos;
		
		// calculate r
		// double r = Math.cos(local_total_delta) * Math.sin(local_total_delta); //4 leaf clover
		// double r = Math.cos(.5 * local_total_delta); //2 smaller circles in 2 bigger circles
		double r = Math.cos(.5 * local_total_delta) * 2.0 * Math.sin(local_total_delta); // jumpy one.
		
		// these are purposly flipped to make it 'look cooler'
		// calculate x;
		double y = Functions.polarRadToX(local_total_delta, r);
		
		// calculate y;
		double x = Constants.ratio * Functions.polarRadToY(local_total_delta, r);
		
		// set it all
		my_quad.setXYPos(x, y, EnumDrawFrom.center);
	}
}
