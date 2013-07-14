package com.kobaj.level;

import java.util.ArrayList;

import com.kobaj.audio.Sound;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.RectFExtended;
import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.EnumGlobalAnimationList;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadAnimated;
import com.kobaj.opengldrawable.Quad.QuadCompressed;
import com.kobaj.opengldrawable.Quad.QuadSimplePhysics;

public class PlayerFox
{
	private ArrayList<QuadSimplePhysics> shadow_values;
	private ArrayList<QuadSimplePhysics> delayed_values;
	private int copy_count = 25;
	
	private boolean copy = false;
	private boolean credits = false;
	
	private double copy_shift_x_default = 100;
	private double copy_shift_x = 0;
	
	public LevelObject my_player;
	public QuadCompressed player_shadow;
	
	private final int[] current_playing_fox_paws = new int[Sound.sound_count];
	private int sound_placement = 0;
	
	// walking sounds
	private final double walking_max = 600;
	private double walking_timeout = 0;
	
	public PlayerFox(boolean copy, boolean credits)
	{
		this.copy = copy;
		this.credits = credits;
		
		if (copy)
		{
			shadow_values = new ArrayList<QuadSimplePhysics>();
			delayed_values = new ArrayList<QuadSimplePhysics>();
			for(int i = 0; i < copy_count; i++)
			{
				shadow_values.add(new QuadSimplePhysics());
				delayed_values.add(new QuadSimplePhysics());
			}
		}
		
		copy_shift_x = Functions.screenWidthToShaderWidth(copy_shift_x_default);
	}
	
	public void onUpdate(double delta, boolean play, boolean player_on_ground)
	{
		if (play && my_player.quad_object instanceof QuadAnimated)
		{
			QuadAnimated reference = QuadAnimated.class.cast(my_player.quad_object);
			
			double current_x_speed = Math.abs(reference.x_vel_shader);
			double current_y_speed = Math.abs(reference.y_vel_shader);
			
			// currently playing animation
			if (reference.y_vel_shader < 0 //
					&& current_y_speed > Constants.player_movement_threshold_vertical //
					&& !player_on_ground)
			{
				reference.setAnimation(EnumGlobalAnimationList.falling, 0, true);
			}
			else if (reference.y_vel_shader > 0 //
					&& current_y_speed > Constants.player_movement_threshold_vertical)
			{
				if (player_on_ground)
				{
					reference.setAnimation(EnumGlobalAnimationList.jump, 0, true, false);
				}
				else
				{
					if (reference.currently_playing == EnumGlobalAnimationList.jump)
					{
						if (reference.currently_playing_frameset_reference.animation_complete)
						{
							reference.setAnimation(EnumGlobalAnimationList.jumping, 0, true);
						}
					}
					else
					{
						reference.setAnimation(EnumGlobalAnimationList.jumping, 0, true);
					}
				}
			}
			else if (current_x_speed < Constants.player_movement_threshold_horizontal)
			{
				reference.setAnimation(EnumGlobalAnimationList.stop, 0, true);
			}
			else
			{
				if (reference.currently_playing == EnumGlobalAnimationList.falling)
				{
					reference.setAnimation(EnumGlobalAnimationList.landing, 0, true, false);
				}
				else if ((reference.currently_playing != EnumGlobalAnimationList.running && reference.currently_playing != EnumGlobalAnimationList.landing) || //
						(reference.currently_playing == EnumGlobalAnimationList.landing && reference.currently_playing_frameset_reference.animation_complete))
				{
					if(player_on_ground)
						reference.setAnimation(EnumGlobalAnimationList.running, 0, true);
				}
			}
			
			if (current_x_speed > Constants.player_movement_threshold_horizontal)
			{
				// current direction
				reference.reverseLeftRight((my_player.quad_object.x_vel_shader >= 0));
			}
			
			reference.onUpdate(delta);
		}
		
		// do sounds
		walking_timeout += delta;
		double velocity = Math.abs(my_player.quad_object.x_vel_shader) * 10000;
		if (player_on_ground && velocity > Constants.arbitrary_sound_velocity && walking_timeout > walking_max)
		{
			walking_timeout = 0;
			if (play)
			{
				sound_placement += 1;
				sound_placement = sound_placement % Sound.sound_count;
				
				int result = Constants.sound.play(R.raw.sound_fox_trot_2, 0);
				if (result != 0)
				{
					this.current_playing_fox_paws[sound_placement] = result;
				}
				
			}
		}
		else if (!player_on_ground)
		{
			for (int i = 0; i < current_playing_fox_paws.length; i++)
			{
				Constants.sound.stop(current_playing_fox_paws[i]);
				current_playing_fox_paws[i] = 0;
			}
		}
	}
	
	public void copyFox(Quad original_fox, Quad original_shadow)
	{
		QuadSimplePhysics tobemoved = delayed_values.get(0);
		QuadSimplePhysics shad_toberemoved = shadow_values.get(0);
		
		// shift the player
		my_player.quad_object.setXYPos(tobemoved.x_pos_shader - this.copy_shift_x, tobemoved.y_pos_shader, EnumDrawFrom.center);
		
		my_player.quad_object.x_acc_shader = tobemoved.x_acc_shader;
		my_player.quad_object.x_vel_shader = tobemoved.x_vel_shader;
		my_player.quad_object.y_acc_shader = tobemoved.y_acc_shader;
		my_player.quad_object.y_vel_shader = tobemoved.y_vel_shader;
		
		player_shadow.setXYPos(shad_toberemoved.x_pos_shader - this.copy_shift_x, shad_toberemoved.y_pos_shader, EnumDrawFrom.center);
		player_shadow.setScale(shad_toberemoved.x_acc_shader);
		
		shadow_values.remove(0);
		delayed_values.remove(0);
		
		// store old
		tobemoved.x_acc_shader = original_fox.x_acc_shader;
		tobemoved.x_pos_shader = original_fox.x_pos_shader;
		tobemoved.x_vel_shader = original_fox.x_vel_shader;
		
		tobemoved.y_acc_shader = original_fox.y_acc_shader;
		tobemoved.y_pos_shader = original_fox.y_pos_shader;
		tobemoved.y_vel_shader = original_fox.y_vel_shader;
		
		shad_toberemoved.x_pos_shader = original_shadow.x_pos_shader;
		shad_toberemoved.y_pos_shader = original_shadow.y_pos_shader;
		shad_toberemoved.x_acc_shader = original_shadow.scale_value;
		
		shadow_values.add(shad_toberemoved);
		delayed_values.add(tobemoved);
		
	}
	
	public LevelObject setupPlayer(LevelObject player)
	{	
		int fox = R.raw.fox;
		int fox_alpha = R.raw.fox_alpha;
		
		if (credits)
		{
			fox = R.raw.fox_smile;
			fox_alpha = R.raw.fox_smile_alpha;
		}
		
		if (copy)
		{
			fox = R.raw.fox_smile_red;
			fox_alpha = R.raw.fox_smile_red_alpha;
		}	
		
		QuadAnimated player_animation = new QuadAnimated(fox, fox_alpha, R.raw.animation_list_fox, 400, 215, 2048, 1024);
		player_animation.setAnimation(EnumGlobalAnimationList.stop, 0, true);
		
		RectF previous = player_animation.phys_rect_list.get(0).main_rect;
		player_animation.phys_rect_list.add(new RectFExtended(previous.left + Functions.screenWidthToShaderWidth(58), //
				previous.top - Functions.screenHeightToShaderHeight(55), //
				previous.right - Functions.screenWidthToShaderWidth(58), //
				previous.bottom + Functions.screenHeightToShaderHeight(15)));
		player_animation.phys_rect_list.remove(0);
		
		player.quad_object = player_animation;
		player.this_object = EnumLevelObject.fox2;
		player.eid = Integer.MIN_VALUE + 1;
		if(copy)
			player.eid = Integer.MIN_VALUE;
		player.layer = EnumLayerTypes.Pre_interaction;
		player.z_plane = Double.MIN_VALUE;
		player.quad_object.setScale(.75);
		player.ignore_coord_map = true;
		
		player_animation.reverseLeftRight(true);
		
		// player shadow
		player_shadow = new QuadCompressed(R.raw.shadow_square, R.raw.shadow_square_alpha, 179, 90);
		
		my_player = player;
		
		return player;
	}
}
