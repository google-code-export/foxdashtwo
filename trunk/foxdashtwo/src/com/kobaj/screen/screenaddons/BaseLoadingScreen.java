package com.kobaj.screen.screenaddons;

import android.graphics.Color;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class BaseLoadingScreen
{	
	//this will not be moved to constants (obviously XP)
	private QuadColorShape[] my_shapes = new QuadColorShape[Constants.loading_max_shapes];
	private double total_delta = 0;
	
	public BaseLoadingScreen()
	{
		for(int i = 0; i < Constants.loading_max_shapes; i++)
			my_shapes[i] = new QuadColorShape(Constants.loading_radius, getColor(i), 0);
	}
	
	public int getColor(int i)
	{
		//pull apart
		int pc = Constants.loading_primary_color;
		int pr = (pc >> 16) & 0xFF;
		int pg = (pc >> 8) & 0xFF;
		int pb = (pc & 0xFF);
		int pa = pc >>> 24;
		
		int sc = Constants.loading_secondary_color;
		int sr = (sc >> 16) & 0xFF;
		int sg = (sc >> 8) & 0xFF;
		int sb = (sc & 0xFF);
		int sa = sc >>> 24;
		
		//lerp
		int lr = (int)Functions.linearInterpolate(0, Constants.loading_max_shapes - 1, i, pr, sr);
		int lg = (int)Functions.linearInterpolate(0, Constants.loading_max_shapes - 1, i, pg, sg);
		int lb = (int)Functions.linearInterpolate(0, Constants.loading_max_shapes - 1, i, pb, sb);
		int la = (int)Functions.linearInterpolate(0, Constants.loading_max_shapes - 1, i, pa, sa);
		
		//stick back together;
		return (la << 24) | (lr << 16) | (lg << 8) | lb;
	}
	
	public void onDrawLoading(double delta)
	{
		total_delta += (Math.PI / 8) * delta / 250;
	
		if(total_delta >= Math.PI * 4)
			total_delta = 0;
		
		for(int i = 0; i < Constants.loading_max_shapes; i++)
		{
			//set their position
			setPosition(my_shapes[i], i);
			
			//draw them all
			my_shapes[i].onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Color.WHITE, true);	
		}
		
		//draw some text
		Constants.text.drawText(R.string.loading, 0, 0, EnumDrawFrom.center);
	}
	
	private void setPosition(QuadColorShape my_quad, int ith_pos)
	{
		double local_total_delta = total_delta - Constants.loading_delta_shift * (double) ith_pos;
		
		//calculate r
		//double r = Math.cos(local_total_delta) * Math.sin(local_total_delta); //4 leaf clover
		//double r = Math.cos(.5 * local_total_delta); //2 smaller circles in 2 bigger circles
		double r = Math.cos(.5 * local_total_delta) * 2.0 * Math.sin(local_total_delta); //jumpy one.
		
		//calculate x;
		double x = Functions.polarRadToX(local_total_delta, r);
		
		//calculate y;
		double y = Functions.polarRadToY(local_total_delta, r);
		
		//set it all
		my_quad.setPos(x, y, EnumDrawFrom.center);
	}
}
