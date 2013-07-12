package com.kobaj.screen.screenaddons;

import com.kobaj.input.GameInputModifier;
import com.kobaj.level.EnumLayerTypes;
import com.kobaj.level.EnumLevelObject;
import com.kobaj.level.Level;
import com.kobaj.level.LevelObject;
import com.kobaj.math.AverageMaker;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.android.RectF;

public class BaseInteractionPhysics
{
	// camera zoom
	private final int average_count = 20;
	private final AverageMaker my_camera_average = new AverageMaker(average_count);
	private final AverageMaker my_camera_shift = new AverageMaker(average_count);
	
	private final RectF collision = new RectF();
	
	public double player_shadow_y;
	public final RectF player_extended = new RectF();
	
	private final boolean integratePhysics(final double delta, final Level the_level)
	{
		// something to collide with the shadow
		player_extended.left = (float) (the_level.player.quad_object.x_pos_shader - the_level.player.quad_object.shader_width / 2.0);
		player_extended.right = (float) (the_level.player.quad_object.x_pos_shader + the_level.player.quad_object.shader_width / 2.0);
		
		double bottom_of_player = the_level.player.quad_object.y_pos_shader - the_level.player.quad_object.shader_height / 5.0;
		player_extended.top = (float) (the_level.player.quad_object.y_pos_shader + the_level.player.quad_object.shader_height / 2.0);
		player_extended.bottom = (float) (bottom_of_player - Constants.shadow_height_shader);
		
		double shadow_collision_y = player_extended.bottom;
		player_shadow_y = -200;
		
		boolean can_jump = false;
		
		Constants.physics.integratePhysics(delta, the_level.player.quad_object);
		
		// integrate all other objects
		LevelObject[] temp = the_level.object_hash.get(EnumLayerTypes.Interaction).visible_objects;
		for (int i = the_level.object_hash.get(EnumLayerTypes.Interaction).visible_object_count - 1; i >= 0; i--)
		{
			LevelObject reference = temp[i];
			
			if (!reference.collide_with_player)
				continue;
			
			if (reference.active)
			{
				// do it again for the shadow
				collision.left = 0;
				collision.top = 0;
				collision.right = 0;
				collision.bottom = 0;
				
				int initial_shadow_collision = Constants.physics.checkCollision(collision, player_extended, reference.quad_object, false);
				
				if (initial_shadow_collision == 1)
				{
					// if its horizontally in the correct spot
					if (collision.width() > Constants.collision_detection_width)
					{
						// if its vertically in the correct spot
						if (collision.bottom > shadow_collision_y && collision.bottom < bottom_of_player)
						{
							// new calculation
							this.player_shadow_y = shadow_collision_y = collision.bottom;
						}
					}
				}
				
				// if its -1 there is no way the fox will collide with anything anyway.
				if (initial_shadow_collision == -1)
					continue;
				
				// do the regular fox's collision
				collision.left = 0;
				collision.top = 0;
				collision.right = 0;
				collision.bottom = 0;
				
				int collision_agent = 0;
				if (reference.this_object == EnumLevelObject.lx_pickup_checkpoint)
					collision_agent = 3;
				
				if (Constants.physics.checkCollision(collision, the_level.player.quad_object, reference.quad_object, collision_agent) == 1)
					can_jump = true;
				
				if (collision.width() != 0 || collision.height() != 0)
					the_level.objectInteraction(collision, the_level.player, reference, delta);
				
				// do the fox's shadow
				// shadow_collision_y = calc_foxes_shadow(reference, shadow_collision_y, the_level.player.quad_object.y_pos_shader);
			}
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
				if (can_jump && the_level.player.quad_object.x_vel_shader < 0)
					move_amount += Constants.normal_reverse_acceleration;
				else
					move_amount += Constants.normal_acceleration;
			}
			
			// if touch left
			else if (my_modifier.getInputType().getTouchedLeft())
			{
				if (can_jump && the_level.player.quad_object.x_vel_shader > 0)
					move_amount += -Constants.normal_reverse_acceleration;
				else
					move_amount += -Constants.normal_acceleration;
			}
			
			// if in the air, apply a damping.
			if (!can_jump)
				move_amount *= Constants.normal_air_damping;
			
			// add the key press (force) to the player acceleration
			the_level.player.quad_object.x_acc_shader += move_amount;
			
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
			the_level.player.quad_object.x_acc_shader -= Constants.normal_friction * the_level.player.quad_object.x_vel_shader;
		
		// add jump
		if (my_modifier.getInputType().getPressedJump() && can_jump)
			the_level.player.quad_object.y_vel_shader = Constants.jump_velocity;
		else if (my_modifier.getInputType().getReleasedJump())
			if (the_level.player.quad_object.y_vel_shader > Constants.jump_limiter)
				the_level.player.quad_object.y_vel_shader = Constants.jump_limiter;
	}
	
	private void setCameraXYZ(final Level test_level)
	{
		// prepare camera
		double x_camera = test_level.player.quad_object.x_pos_shader;
		double y_camera = test_level.player.quad_object.y_pos_shader;
		
		// restrict camera movement
		double x_buffer = Constants.ratio * Constants.z_shader_translation;
		
		double left_level_limit = test_level.left_shader_limit + x_buffer;
		double right_level_limit = test_level.right_shader_limit - x_buffer;
		double top_level_limit = test_level.top_shader_limit - Constants.z_shader_translation;
		double bottom_level_limit = test_level.bottom_shader_limit + Constants.z_shader_translation;
		
		// shifts so the user can see more
		double x_camera_shift = Functions.linearInterpolate(0, Constants.max_x_velocity, Math.abs(test_level.player.quad_object.x_vel_shader), -Constants.backward_camera_shift_width,
				Constants.forward_camera_shift_width);
		x_camera_shift = Math.max(x_camera_shift, 0);
		if (test_level.player.quad_object.x_vel_shader < 0)
			x_camera_shift = -x_camera_shift;
		
		x_camera += my_camera_shift.calculateAverage(x_camera_shift);
		
		// DO NOT ALTER
		if (x_camera < left_level_limit)
			x_camera = left_level_limit;
		else if (x_camera > right_level_limit)
			x_camera = right_level_limit;
		
		if (y_camera > top_level_limit)
			y_camera = top_level_limit;
		else if (y_camera < bottom_level_limit)
			y_camera = bottom_level_limit;
		
		// update the camera zoom effect
		double buffer = (float) Functions.linearInterpolate(//
				0, //
				Constants.max_speed, //
				Functions.speed(test_level.player.quad_object.x_vel_shader, test_level.player.quad_object.y_vel_shader), //
				Constants.min_zoom, //
				Constants.max_zoom); //
		
		// set camera
		Functions.setCamera(x_camera, y_camera, my_camera_average.calculateAverage(buffer));
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
		setCameraXYZ(the_level);
	}
}
