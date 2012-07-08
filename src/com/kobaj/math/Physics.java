package com.kobaj.math;

import com.kobaj.opengldrawable.Quad;

import android.graphics.Rect;

//broken down into three parts
//1. gravity/force application.
//2. Collision detection
//3. Collision handling
public class Physics
{	
	private double gravity = -.000098; //well thats a random number.
	
	public Physics()
	{
		gravity = gravity * Constants.dip_scale;
	}
	
	public void apply_force()
	{
		//if needed I'll make this.
	}
	
	//applies gravity
	public <T extends Quad> void apply_physics(double delta, T the_quad)
	{
		//set acceleration
		the_quad.y_acc = gravity;
		
		//add velocity
		the_quad.y_vel += the_quad.y_acc * delta;
		
		//add position
		the_quad.setPos(the_quad.get_x_pos(), the_quad.get_y_pos() + the_quad.y_vel * delta, com.kobaj.opengldrawable.EnumDrawFrom.center);
	}
	
	public Rect check_collision()
	{
		return null;
	}
	
	public void handle_collision()
	{
		
	}
}
