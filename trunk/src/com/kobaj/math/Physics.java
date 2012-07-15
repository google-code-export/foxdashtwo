package com.kobaj.math;

import android.graphics.RectF;

import com.kobaj.opengldrawable.Quad;

//broken down into three parts
//1. gravity/force application.
//2. Collision detection
//3. Collision handling
public class Physics
{	
	private double gravity = -.000000098; //well thats a random number.
	
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
	
	public <T extends Quad> RectF check_collision(T first_quad, T second_quad)
	{
		//quick check to even see if its possible for two quads to touch
		double first_x = first_quad.get_x_pos();
		double first_y = first_quad.get_y_pos();
		
		double second_x = second_quad.get_x_pos();
		double second_y = second_quad.get_y_pos();
		
		//reposition
		first_x = first_x - first_quad.shader_width / 2.0;
		first_y = first_y - first_quad.shader_height / 2.0;
		
		second_x = second_x - second_quad.shader_width / 2.0;
		second_y = second_y - second_quad.shader_height / 2.0;
		
		if(first_x + first_quad.shader_width < second_x || first_x > second_x + second_quad.shader_width)
			return null; //no possibility of collision
		
		if(first_y + first_quad.shader_height < second_y || first_y > second_y + second_quad.shader_height)
			return null; //no possibility of collision
		
		//Is this going to cause garbage?
		RectF collision = new RectF();
		
		//if its possible, get a detailed picture of the collision
		for(RectF first_rect: first_quad.phys_rect_list)
			for(RectF second_rect: second_quad.phys_rect_list)
				if(collision.setIntersect(first_rect, second_rect))
					return collision;
		
		return null;
	}
	
	public <T extends Quad> void handle_collision(RectF collision, T the_quad)
	{
		//assume a few things
		//only fix up and down
		//no bounce
		
		//early copouts
		if(collision == null)
			return;
		if(collision.height() == 0)
			return;
		
		//grab the direction of travel and normalize it
		double direction = the_quad.y_vel;
		if(direction > 0)
			direction = -1;
		else if(direction < 0 )
			direction = 1;
	
		double height = collision.height();
		height *= direction;
		
		//set the quad
		the_quad.setPos(the_quad.get_x_pos(), the_quad.get_y_pos() + height, com.kobaj.opengldrawable.EnumDrawFrom.center);
		the_quad.y_vel = 0;
	}
}