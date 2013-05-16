package com.kobaj.screen.screenaddons;

import android.graphics.Color;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class InfiniteJig extends RotationLoadingJig
{
	private boolean explode = false;
	private double saved_delta = 0;
	
	// getting crazy up in here
	private RotationLoadingJig[] outer_rings = new RotationLoadingJig[1];
	
	public InfiniteJig()
	{
		primary_color = Color.WHITE;
		secondary_color = 0xFF000000;
		
		speed = 75;
		
		loading_delta_shift = Math.PI / shape_count;
		
		for (int i = 0; i < outer_rings.length; i++)
		{
			int i_2 = i + 2;
			
			outer_rings[i] = new RotationLoadingJig();
			outer_rings[i].shape_count = i_2;
			outer_rings[i].radius = .1 * i_2;
			outer_rings[i].speed = 10;
			outer_rings[i].loading_delta_shift = Math.PI / ((double) i_2) * 2.0;
		}
	}
	
	public void set_vp(boolean new_vp)
	{
		this.vp_matrix = new_vp;
		for (RotationLoadingJig j : outer_rings)
			j.vp_matrix = new_vp;
	}
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		for (RotationLoadingJig j : outer_rings)
			j.onInitialize();
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		for (RotationLoadingJig j : outer_rings)
			j.onUnInitialize();
	}
	
	@Override
	public void onUpdate(double delta)
	{
		if (saved_delta > 10000)
		{
			return;
		}
		
		super.onUpdate(delta);
		
		for (int i = 0; i < outer_rings.length; i++)
		{
			outer_rings[i].x_pos = this.x_pos;
			outer_rings[i].y_pos = this.y_pos;
			outer_rings[i].onUpdate(delta);
		}
	}
	
	@Override
	public void onDrawLoading()
	{
		if (saved_delta > 10000)
		{
			return;
		}
		
		super.onDrawLoading();
		
		for (int i = 0; i < outer_rings.length; i++)
		{
			outer_rings[i].onDrawLoading();
		}
		
	}
	
	@Override
	protected void setPosition(QuadColorShape my_quad, double ith_pos)
	{
		double local_total_delta = total_delta - loading_delta_shift * ith_pos;
		
		// calculate r in a infinite shape
		double r = .08 + .08 * Math.cos(2 * local_total_delta); // jumpy one.
		
		if (explode)
		{
			saved_delta += local_delta;
			double add_amount = .00035 * saved_delta;
			
			r += add_amount;
			for (int i = 0; i < outer_rings.length; i++)
			{
				outer_rings[i].radius += add_amount / 10.0;
			}
			
		}
		
		// calculate x
		double pre_x = Functions.polarRadToX(local_total_delta, r);
		double x = (Constants.ratio / Constants.my_ratio) * pre_x + x_pos;
		
		// calculate y;
		double pre_y = Functions.polarRadToY(local_total_delta, r);
		
		// flip the y upside down
		if (pre_x < 0)
		{
			pre_y = -pre_y;
		}
		
		double y = (Constants.ratio / Constants.my_ratio) * pre_y + y_pos;
		
		// set it all
		my_quad.setXYPos(x, y, EnumDrawFrom.center);
	}
	
	public void explode()
	{
		explode = true;
	}
	
	public void reset()
	{
		explode = false;
		saved_delta = 0;
	}
}
