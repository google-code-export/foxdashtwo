package com.kobaj.screen.screenaddons;

import com.kobaj.input.GameInputModifier;
import com.kobaj.level.Level;
import com.kobaj.level.LevelObject;
import com.kobaj.math.AverageMaker;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.android.RectF;

public class BaseInteractionPhysics
{
	// camera zoom
	private AverageMaker my_camera_average = new AverageMaker(20);
	public RectF collision = new RectF();
	
	private final boolean integratePhysics(final double delta, final Level the_level)
	{
		boolean can_jump = false;
		
		Constants.physics.integratePhysics(delta, the_level.player.quad_object);
		
		for (int i = the_level.object_list.size() - 1; i >= 0; i--)
		{
			LevelObject reference = the_level.object_list.get(i);
			
			collision.left = 0;
			collision.top = 0;
			collision.right = 0;
			collision.bottom = 0;
			
			if(Constants.physics.checkCollision(collision, the_level.player.quad_object, reference.quad_object, 0))
				can_jump = true;
			
			if(collision.width() != 0 || collision.height() != 0)
				the_level.objectInteraction(collision, the_level.player, reference);
		}
		
		return can_jump;
	}
	
	private final boolean handleTouchInput(final boolean can_jump, final GameInputModifier my_modifier, final Level the_level)
	{
		// initial touch
		if (my_modifier.getInputType().getLeftXorRight())
		{
			double move_amount = 0;
			
			// if touch right
			if (my_modifier.getInputType().getTouchedRight())
			{
				// if we are on the ground
				if (can_jump && the_level.player.quad_object.x_vel < 0)
					move_amount += Constants.normal_reverse_acceleration;
				else
					move_amount += Constants.normal_acceleration;
			}
			
			// if touch left
			else if (my_modifier.getInputType().getTouchedLeft())
			{
				if (can_jump && the_level.player.quad_object.x_vel > 0)
					move_amount += -Constants.normal_reverse_acceleration;
				else
					move_amount += -Constants.normal_acceleration;
			}
			
			// if in the air, apply a damping.
			if (!can_jump)
				move_amount *= Constants.normal_air_damping;
			
			// add the key press (force) to the player acceleration
			the_level.player.quad_object.x_acc += move_amount;
			
			return true;
		}
		
		return false;
	}
	
	private void addForce(final boolean is_touched, final boolean can_jump, final GameInputModifier my_modifier, final Level the_level)
	{
		// add gravity
		Constants.physics.addGravity(the_level.player.quad_object);
		
		// add friction
		if (!is_touched && can_jump)
			the_level.player.quad_object.x_acc -= Constants.normal_friction * the_level.player.quad_object.x_vel;
		
		// add jump
		if (my_modifier.getInputType().getPressedJump() && can_jump)
			the_level.player.quad_object.y_vel = Constants.jump_velocity;
		else if (my_modifier.getInputType().getReleasedJump())
			if (the_level.player.quad_object.y_vel > Constants.jump_limiter)
				the_level.player.quad_object.y_vel = Constants.jump_limiter;
	}
	
	private void setCameraXY(final Level test_level)
	{
		// prepare camera
		double x_camera = test_level.player.quad_object.x_pos;
		double y_camera = test_level.player.quad_object.y_pos;
		
		// restrict camera movement
		double x_buffer = Constants.ratio * Constants.z_shader_translation;
		
		// DO NOT ALTER
		if (x_camera < test_level.left_shader_limit + x_buffer)
			x_camera = test_level.left_shader_limit + x_buffer;
		else if (x_camera > test_level.right_shader_limit - x_buffer)
			x_camera = test_level.right_shader_limit - x_buffer;
		
		if (y_camera > test_level.top_shader_limit - Constants.z_shader_translation)
			y_camera = test_level.top_shader_limit - Constants.z_shader_translation;
		else if (y_camera < test_level.bottom_shader_limit + Constants.z_shader_translation)
			y_camera = test_level.bottom_shader_limit + Constants.z_shader_translation;
		
		// set camera
		Functions.setCamera(x_camera, y_camera);
	}
	
	private void setCameraZ(final Level test_level)
	{
		// update the camera zoom effect
		double buffer = (float) Functions.linearInterpolate(
				0, 
				Constants.max_speed, 
				Functions.speed(test_level.player.quad_object.x_vel, test_level.player.quad_object.y_vel),
				Constants.min_zoom,
				Constants.max_zoom);
		Functions.setCameraZ(my_camera_average.calculateAverage(buffer));
	}
	
	public void onUpdate(final double delta, final GameInputModifier my_modifier, final Level the_level)
	{
		// first physics integration (make the objects follow gravity/forces and collide)
		boolean can_jump = integratePhysics(delta, the_level);
		
		// next handle touch input (see what our fingers are doing)
		boolean is_touched = handleTouchInput(can_jump, my_modifier, the_level);
		
		// add forces
		addForce(is_touched, can_jump, my_modifier, the_level);
		
		// set camera positions
		setCameraXY(the_level);
		setCameraZ(the_level);
	}
}
