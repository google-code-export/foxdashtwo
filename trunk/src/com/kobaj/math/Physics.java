package com.kobaj.math;

import android.graphics.RectF;

import com.kobaj.opengldrawable.Quad.Quad;

//broken down into three parts
//1. gravity/force application.
//2. Collision detection
//3. Collision handling
public class Physics
{	
	public <T extends Quad> void add_gravity(T the_quad)
	{
		the_quad.y_acc += Constants.gravity;
	}
	
	//applies gravity
	public <T extends Quad> void integrate_physics(double delta, T the_quad)
	{	
		//the below is in shader coordinates
		
		//add velocity
		the_quad.y_vel += the_quad.y_acc * delta;
		the_quad.x_vel += the_quad.x_acc * delta;
		
		//set acceleration
		the_quad.y_acc = 0;
		the_quad.x_acc = 0;
		
		//clamp velocities
		if(the_quad.y_vel > Constants.max_y_velocity)
			the_quad.y_vel = Constants.max_y_velocity;
		else if(the_quad.y_vel < -Constants.max_y_velocity)
			the_quad.y_vel = -Constants.max_y_velocity;
		
		if(the_quad.x_vel > Constants.max_x_velocity)
			the_quad.x_vel = Constants.max_x_velocity;
		else if(the_quad.x_vel < -Constants.max_x_velocity)
			the_quad.x_vel = -Constants.max_x_velocity;
		
		//add position
		the_quad.setPos(the_quad.getXPos() + the_quad.x_vel * delta, the_quad.getYPos() + the_quad.y_vel * delta, com.kobaj.opengldrawable.EnumDrawFrom.center);
	}
	
	//check for a collision and return a collission rectf
	public <T extends Quad> RectF check_collision(T first_quad, T second_quad)
	{
		//see if in the same z_plane
		if(first_quad.z_pos != second_quad.z_pos)
			return null;
		
		//quick check to even see if its possible for two quads to touch
		double first_x = first_quad.getXPos();
		double first_y = first_quad.getYPos();
		
		double second_x = second_quad.getXPos();
		double second_y = second_quad.getYPos();
		
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
		RectF collision;
		
		//if its possible, get a detailed picture of the collision
		for(int i = first_quad.phys_rect_list.size() - 1; i >= 0; i--)
			for(int e = second_quad.phys_rect_list.size() - 1; i >= 0; i--)
			{
				collision = Functions.setEqualIntersects(first_quad.phys_rect_list.get(i).main_rect, second_quad.phys_rect_list.get(e).main_rect);
				if(collision != null)
				{
					//we will generate normals
					//and in addition modify the collision rectangle
					//based on how the object should react (bounce, fly through, stick, etc
					
					//then decide normal
					final double width = Math.abs(collision.width());
					final double height = Math.abs(collision.height());
				
					if(width >= height)
						collision.left = collision.right;
					else if(height > Constants.collision_detection_height)
						collision.top = collision.bottom;
					else
						collision = null;
							
					//and here we would decide reaction (if it were programmed)
					
					if(collision != null)
						return collision;
				}
			}
		
		//if there is a collision, return a rectF, if no collision then null.
		return null;
	}
	
	public <T extends Quad> void handle_collision(RectF collision, T the_quad)
	{
		//assume a few things
		//no bounce
	
		//early copouts
		if(collision == null)
			return;
		
		double width = Math.abs(collision.width());
		double height = Math.abs(collision.height());
			
		if(width == height)
			return;
		
		if(collision.width() != 0)
		{
			if(collision.centerX() > the_quad.getXPos()) // push object to the right
				width = -width;
			the_quad.setPos(the_quad.getXPos() + width, the_quad.getYPos(), com.kobaj.opengldrawable.EnumDrawFrom.center);
			the_quad.x_vel = 0;
		}
		else
		{
			if(collision.centerY() > the_quad.getYPos())
				height = -height;
			the_quad.setPos(the_quad.getXPos(), the_quad.getYPos() + height, com.kobaj.opengldrawable.EnumDrawFrom.center);
			
			if(the_quad.y_vel != 0)
				the_quad.y_vel = 0;
		}
	}
}
