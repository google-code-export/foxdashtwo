package com.kobaj.math;

import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.Quad.Quad;

//broken down into three parts
//1. gravity/force application.
//2. Collision detection
//3. Collision handling
public class Physics
{
	// applies gravity
	public <T extends Quad> void addGravity(T the_quad)
	{
		the_quad.y_acc_shader += Constants.gravity;
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
		
		if (distance == 0)
			return;
		
		double abs_distance = Math.abs(distance);
		double force = (-k * (abs_distance - desired_distance) * (distance / abs_distance)) - (b * the_quad.x_vel_shader);
		the_quad.x_acc_shader += force;
	}
	
	public <T extends Quad> void addSpringY(double k, double b, double desired_distance, double distance, T the_quad)
	{
		if (distance == 0)
			return;
		
		double abs_distance = Math.abs(distance);
		double force = (-k * (abs_distance - desired_distance) * (distance / abs_distance)) - (b * the_quad.y_vel_shader);
		if (Double.isNaN(force))
			force = 0;
		
		the_quad.y_acc_shader += force;
	}
	
	public <T extends Quad> void integratePhysics(double delta, T the_quad)
	{
		// the below is in shader coordinates
		
		// add velocity
		the_quad.y_vel_shader += the_quad.y_acc_shader * delta;
		the_quad.x_vel_shader += the_quad.x_acc_shader * delta;
		
		// set acceleration
		the_quad.y_acc_shader = 0;
		the_quad.x_acc_shader = 0;
		
		// clamp velocities
		if (the_quad.y_vel_shader > Constants.max_y_velocity)
			the_quad.y_vel_shader = Constants.max_y_velocity;
		else if (the_quad.y_vel_shader < -Constants.max_y_velocity)
			the_quad.y_vel_shader = -Constants.max_y_velocity;
		
		if (the_quad.x_vel_shader > Constants.max_x_velocity)
			the_quad.x_vel_shader = Constants.max_x_velocity;
		else if (the_quad.x_vel_shader < -Constants.max_x_velocity)
			the_quad.x_vel_shader = -Constants.max_x_velocity;
		
		// add position
		double x_pos = the_quad.x_pos_shader + the_quad.x_vel_shader * delta;
		double y_pos = the_quad.y_pos_shader + the_quad.y_vel_shader * delta;
		the_quad.setXYPos(x_pos, y_pos, com.kobaj.opengldrawable.EnumDrawFrom.center);
	}
	
	// check for a collision and return true if the collision is up and down (the player can jump)
	// otherwise return false
	// zero based index. 0 = first quad, 1 = second_quad
	public <T extends Quad> boolean checkCollision(RectF collision, T first_quad, T second_quad, int quad_to_move)
	{
		// quick check to even see if its possible for two quads to touch
		RectF player_extended = first_quad.best_fit_aabb.main_rect;
		RectF main_rect = second_quad.best_fit_aabb.main_rect;
		
		// short circuit
		if (player_extended.right < main_rect.left || //
				player_extended.left > main_rect.right || //
				player_extended.top < main_rect.bottom || //
				player_extended.bottom > main_rect.top) //
			return false;
		
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
					if (!cleanCollision(collision))
						return false;
					
					// double width = collision.width();
					double height = collision.height();
					
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
	
	// this is a garbage filled terribly implemented static method that should only be used when you know what you're doing
	// so like...durring loading scenes and stuff.
	public static <T extends Quad> double physicsCollisionUpDown(T second_quad, RectF player_extended, double collision_y)
	{
		RectF collision = new RectF();
		collision.left = collision.right = collision.top = collision.bottom = 0;
		
		// quick check to even see if its possible for two quads to touch
		RectF main_rect = second_quad.best_fit_aabb.main_rect;
		
		// if its possible, get a detailed picture of the collision
		for (int i = second_quad.phys_rect_list.size() - 1; i >= 0; i--)
		{
			if (player_extended.right < main_rect.left || player_extended.left > main_rect.right || player_extended.top < main_rect.bottom || player_extended.bottom > main_rect.top)
			{
				// do nothing
			}
			else
			{
				Functions.setEqualIntersects(collision, second_quad.phys_rect_list.get(i).main_rect, player_extended);
				if (collision.left != collision.right || collision.top != collision.bottom)
				{
					// force up down
					if (collision.height() != 0)
					{
						collision.left = (float) -Constants.shadow_height;
						collision.right = (float) Constants.shadow_height;
					}
					
					if (Physics.cleanCollision(collision))
						if (collision.height() != 0)
							if (collision.bottom > collision_y)
								collision_y = collision.bottom;
				}
			}
		}
		
		return collision_y;
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
			if (my_collision.centerX() > the_quad.x_pos_shader) // push object to the right
				width = -width;
			the_quad.setXYPos(the_quad.x_pos_shader + width, the_quad.y_pos_shader, com.kobaj.opengldrawable.EnumDrawFrom.center);
			the_quad.x_vel_shader = 0;
		}
		else
		{
			if (my_collision.centerY() > the_quad.y_pos_shader)
				height = -height;
			the_quad.setXYPos(the_quad.x_pos_shader, the_quad.y_pos_shader + height, com.kobaj.opengldrawable.EnumDrawFrom.center);
			the_quad.y_vel_shader = 0;
		}
	}
	
	public static boolean cleanCollision(RectF collision)
	{
		if (collision.left > collision.right)
		{
			float temp = collision.left;
			collision.left = collision.right;
			collision.right = temp;
		}
		
		if (collision.bottom < collision.top)
		{
			float temp = collision.bottom;
			collision.bottom = collision.top;
			collision.top = temp;
		}
		
		double width = collision.width();
		double height = collision.height();
		
		if (width > height)
			collision.left = collision.right = 0;
		else if (height > Constants.collision_detection_height)
			collision.top = collision.bottom = 0;
		else
		{
			collision.left = collision.right = collision.top = collision.bottom = 0;
			return false;
		}
		
		return true;
	}
}
