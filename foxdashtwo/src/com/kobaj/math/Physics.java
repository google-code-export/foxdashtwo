package com.kobaj.math;

import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.Quad.Quad;

//broken down into three parts
//1. gravity/force application.
//2. Collision detection
//3. Collision handling
public class Physics
{
	//applies gravity
	public <T extends Quad> void addGravity(T the_quad)
	{
		the_quad.y_acc += Constants.gravity;
	}
	
	// screen coordinates 0-1
	public <T extends Quad> void addSpringX(double k, double b, double desired_distance, double distance, T the_quad)
	{
		// thanks to http://gafferongames.com/game-physics/spring-physics/
		// |x| is desired distance
		// d is current distance
		// k is spring factor
		// b is damping factor
		// v is velocity
		// F = -k(|x|-d)(x/|x|) - bv
		
		double abs_distance = Math.abs(distance);
		double force = (-k * (abs_distance - desired_distance) * (distance / abs_distance)) - (b * the_quad.x_vel);
		the_quad.x_acc += force;
	}
	
	public <T extends Quad> void addSpringY(double k, double b, double desired_distance, double distance, T the_quad)
	{	
		double abs_distance = Math.abs(distance);
		double force = (-k * (abs_distance - desired_distance) * (distance / abs_distance)) - (b * the_quad.y_vel);
		if(Double.isNaN(force))
			force = 0;
		
		the_quad.y_acc += force;
	}	
	
	public <T extends Quad> void integratePhysics(double delta, T the_quad)
	{
		// the below is in shader coordinates
		
		// add velocity
		the_quad.y_vel += the_quad.y_acc * delta;
		the_quad.x_vel += the_quad.x_acc * delta;
		
		// set acceleration
		the_quad.y_acc = 0;
		the_quad.x_acc = 0;
		
		// clamp velocities
		if (the_quad.y_vel > Constants.max_y_velocity)
			the_quad.y_vel = Constants.max_y_velocity;
		else if (the_quad.y_vel < -Constants.max_y_velocity)
			the_quad.y_vel = -Constants.max_y_velocity;
		
		if (the_quad.x_vel > Constants.max_x_velocity)
			the_quad.x_vel = Constants.max_x_velocity;
		else if (the_quad.x_vel < -Constants.max_x_velocity)
			the_quad.x_vel = -Constants.max_x_velocity;
		
		// add position
		double x_pos = the_quad.x_pos + the_quad.x_vel * delta;
		double y_pos = the_quad.y_pos + the_quad.y_vel * delta;
		the_quad.setXYPos(x_pos, y_pos, com.kobaj.opengldrawable.EnumDrawFrom.center);
	}
	
	// check for a collision and return true if the collision is up and down (the player can jump)
	// otherwise return false
	// zero based index. 0 = first quad, 1 = second_quad
	public <T extends Quad> boolean checkCollision(RectF collision, T first_quad, T second_quad, int quad_to_move)
	{	
		// quick check to even see if its possible for two quads to touch
		double first_x = first_quad.x_pos;
		double first_y = first_quad.y_pos;
		
		double second_x = second_quad.x_pos;
		double second_y = second_quad.y_pos;
		
		// reposition
		first_x = first_x - first_quad.shader_width / 2.0;
		first_y = first_y - first_quad.shader_height / 2.0;
		
		second_x = second_x - second_quad.shader_width / 2.0;
		second_y = second_y - second_quad.shader_height / 2.0;
		
		if (first_x + first_quad.shader_width < second_x || first_x > second_x + second_quad.shader_width || first_y + first_quad.shader_height < second_y
				|| first_y > second_y + second_quad.shader_height)
			return false; // no possibility of collision
			
		// if its possible, get a detailed picture of the collision
		for (int i = first_quad.phys_rect_list.size() - 1; i >= 0; i--)
			for (int e = second_quad.phys_rect_list.size() - 1; i >= 0; i--)
			{
				Functions.setEqualIntersects(collision, first_quad.phys_rect_list.get(i).main_rect, second_quad.phys_rect_list.get(e).main_rect);
				if (collision.left != collision.right || collision.top != collision.bottom)
				{
					// we will generate normals
					// and in addition modify the collision rectangle
					// based on how the object should react (bounce, fly through, stick, etc
					
					// then decide normal
					if (collision.width() < 0)
					{
						float temp = collision.left;
						collision.left = collision.right;
						collision.right = temp;
					}
					
					if (collision.height() < 0)
					{
						float temp = collision.bottom;
						collision.bottom = collision.top;
						collision.top = temp;
					}
					double width = collision.width();
					double height = collision.height();
					
					if (width >= height)
					{
						collision.left = collision.right;
						width = 0;
					}
					else if (height > Constants.collision_detection_height)
					{
						collision.top = collision.bottom;
						height = 0;
					}
					else
					{
						collision.left = collision.right;
						collision.top = collision.bottom;
						return false;
					}
					
					// and move the user specified quad
					if (quad_to_move == 0)
						handleCollision(collision, first_quad);
					else if (quad_to_move == 1)
						handleCollision(collision, second_quad);
					
					// we can still return false
					if (height != 0)
						return true;
					else
						return false;
				}
			}
		
		// if there is a collision, return a rectF, if no collision then null.
		return false;
	}
	
	public <T extends Quad> void handleCollision(RectF my_collision, T the_quad)
	{
		// assume a few things
		// no bounce
		
		double width = my_collision.width();
		double height = my_collision.height();
		
		if (width == height)
			return;
		
		if (width != 0)
		{
			if (my_collision.centerX() > the_quad.x_pos) // push object to the right
				width = -width;
			the_quad.setXYPos(the_quad.x_pos + width, the_quad.y_pos, com.kobaj.opengldrawable.EnumDrawFrom.center);
			the_quad.x_vel = 0;
		}
		else
		{
			if (my_collision.centerY() > the_quad.y_pos)
				height = -height;
			the_quad.setXYPos(the_quad.x_pos, the_quad.y_pos + height, com.kobaj.opengldrawable.EnumDrawFrom.center);
			the_quad.y_vel = 0;
		}
	}
}
