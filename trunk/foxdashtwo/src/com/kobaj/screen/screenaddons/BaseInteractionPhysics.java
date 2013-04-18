package com.kobaj.screen.screenaddons;

import com.kobaj.input.GameInputModifier;
import com.kobaj.level.EnumLayerTypes;
import com.kobaj.level.Level;
import com.kobaj.level.LevelObject;
import com.kobaj.math.AverageMaker;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.Physics;
import com.kobaj.math.RectFExtended;
import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.Quad.Quad;

public class BaseInteractionPhysics
{
	// camera zoom
	private final AverageMaker my_camera_average = new AverageMaker(20);
	private final RectF collision = new RectF();
	
	public double player_shadow_scale;
	public double player_shadow_y;
	public final RectF player_extended = new RectF();
	
	private final boolean integratePhysics(final double delta, final Level the_level)
	{
		// something to collide with the shadow
		player_extended.left = (float) (the_level.player.quad_object.x_pos_shader - the_level.player.quad_object.shader_width / 2.0);
		player_extended.right = (float) (player_extended.left + the_level.player.quad_object.shader_width);
		player_extended.top = (float) (the_level.player.quad_object.y_pos_shader - the_level.player.quad_object.shader_height / 2.0);
		player_extended.bottom = (float) (player_extended.top - Functions.screenHeightToShaderHeight(Constants.shadow_height));
		
		double shadow_collision_y = player_extended.bottom;
		player_shadow_y = -200;
		
		boolean can_jump = false;
		
		Constants.physics.integratePhysics(delta, the_level.player.quad_object);
		
		// integrate all other objects
		LevelObject[] temp = the_level.object_hash.get(EnumLayerTypes.Interaction);
		for (int i = temp.length - 1; i >= 0; i--)
		{			
			LevelObject reference = temp[i];
			
			if(!reference.collide_with_player)
				continue;
				
			if (reference.active)
			{
				// do the regular fox's collision
				collision.left = 0;
				collision.top = 0;
				collision.right = 0;
				collision.bottom = 0;
				
				if (Constants.physics.checkCollision(collision, the_level.player.quad_object, reference.quad_object, 0))
					can_jump = true;
				
				if (collision.width() != 0 || collision.height() != 0)
					the_level.objectInteraction(collision, the_level.player, reference, delta);
				
				// do the fox's shadow
				shadow_collision_y = calc_foxes_shadow(reference, shadow_collision_y);
			}
		}
		
		player_shadow_scale = shadow_collision_y;
		
		return can_jump;
	}
	
	private final double calc_foxes_shadow(LevelObject reference, double collision_y)
	{
		collision.left = 0;
		collision.top = 0;
		collision.right = 0;
		collision.bottom = 0;
		
		Quad second_quad = reference.quad_object;
		RectFExtended best_fit_aabb = second_quad.best_fit_aabb;
		
		// short circuit
		if (player_extended.right < best_fit_aabb.main_rect.left || player_extended.left > best_fit_aabb.main_rect.right || player_extended.top < best_fit_aabb.main_rect.bottom
				|| player_extended.bottom > best_fit_aabb.main_rect.top)
			return collision_y;
		
		for (int e = reference.quad_object.phys_rect_list.size() - 1; e >= 0; e--)
		{
			RectF second = reference.quad_object.phys_rect_list.get(e).main_rect;
			
			if (player_extended.left > second.right || player_extended.right < second.left || player_extended.top < second.bottom || player_extended.bottom > second.top)
			{
				// no possible collision
			}
			else
			{
				Functions.setEqualIntersects(collision, player_extended, second);
				
				// force this to be an up-down collision
				if (collision.height() != 0)
				{
					collision.left = (float) -Constants.shadow_height;
					collision.right = (float) Constants.shadow_height;
				}
				
				if (Physics.cleanCollision(collision))
					if (collision.height() != 0)
						if (collision.bottom > collision_y)
						{
							// collision, find the shadow
							double player_y = collision_y = collision.bottom;
							double screen_y = Constants.y_shader_translation;
							
							double shift_y = Functions.shaderYToScreenY(player_y - screen_y);
							this.player_shadow_y = shift_y;
						}
			}
		}
		
		return collision_y;
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
