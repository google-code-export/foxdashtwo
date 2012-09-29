package com.kobaj.screen;

import android.graphics.Color;
import android.graphics.RectF;

import com.kobaj.foxdashtwo.R;
import com.kobaj.input.GameInputModifier;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;
import com.kobaj.openglgraphics.AmbientLight;
import com.kobaj.screenaddons.BaseDebugScreen;
import com.kobaj.screenaddons.BaseLoadingScreen;

public class SinglePlayerScreen extends BaseScreen
{
	//modification of input
	private GameInputModifier my_modifier;
	
	// get to drawing stuff
	private QuadColorShape real_ambient_light;
	
	// test level
	private com.kobaj.level.Level test_level;
	
	//addons
	BaseDebugScreen debug_addon;
	BaseLoadingScreen loading_addon;
	
	public SinglePlayerScreen()
	{
		//initialize everything
		my_modifier = new GameInputModifier();
		
		/*
		 * helpful while I build the level class
		 * 
		 * com.kobaj.level.Level test = new com.kobaj.level.Level();
		 * test.writeOut(); com.kobaj.loader.XMLHandler.writeSerialFile(test,
		 * "test_level");
		 */
		
		test_level = com.kobaj.loader.XMLHandler.readSerialFile(com.kobaj.math.Constants.resources, R.raw.test_level, com.kobaj.level.Level.class);
	}
	
	@Override
	public void onLoad()
	{
		//load our addons. Do the loader first
		loading_addon = new BaseLoadingScreen();
		debug_addon = new BaseDebugScreen();
		
		//main light
		real_ambient_light = new QuadColorShape(0, Constants.height, Constants.width, 0, 0xFF444444, 0);
		
		//level
		if (test_level != null)
			test_level.onInitialize();
		
		//control input
		my_modifier.onInitialize();
		
		//put in some fake loading time.
		for(int i = 0; i < 6; i++)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onUpdate(double delta)
	{
		//just for now, this may be deleted later to replace a button
		my_modifier.onUpdate();
		
		boolean jump_time = false;
		boolean touched = false;
		
		// physics
		Constants.physics.integrate_physics(delta, test_level.player.quad_object);
		for (com.kobaj.level.LevelObject level_object : test_level.object_array)
		{
			RectF collision = Constants.physics.check_collision(test_level.player.quad_object, level_object.quad_object);
			if (collision != null && collision.height() != 0)
				jump_time = true;
			Constants.physics.handle_collision(collision, test_level.player.quad_object);
		}
		
		// initial touch
		if (my_modifier.getInputType().getLeftOrRight())
		{
			touched = true;
			double move_amount = 0;
			
			if (my_modifier.getInputType().getTouchedRight())
			{
				if(jump_time)
					move_amount += Constants.normal_reverse_acceleration;
				else
					move_amount += Constants.normal_acceleration;
			}
			
			if(my_modifier.getInputType().getTouchedLeft())
			{
				if(jump_time)
					move_amount += -Constants.normal_reverse_acceleration;
				else
					move_amount += -Constants.normal_acceleration;
			}
			
			// if in the air, apply a damping.
			if (!jump_time)
				move_amount *= Constants.normal_air_damping;
			
			// add to it
			test_level.player.quad_object.x_acc += move_amount;
		}
		
		// add forces
		// add gravity
		Constants.physics.add_gravity(test_level.player.quad_object);
		
		// add friction
		if (!touched && jump_time) 
		{
			double friction = -Constants.normal_friction * test_level.player.quad_object.x_vel;
			test_level.player.quad_object.x_acc += friction;
		}
		
		// prepare camera
		double x_camera = -test_level.player.quad_object.getXPos();
		double y_camera = -test_level.player.quad_object.getYPos();
		
		// restrict camera movement
		if (x_camera > test_level.left_shader_limit)
			x_camera = test_level.left_shader_limit;
		else if (x_camera < test_level.right_shader_limit)
			x_camera = test_level.right_shader_limit;
		
		if (y_camera < test_level.top_shader_limit)
			y_camera = test_level.top_shader_limit;
		else if (y_camera > test_level.bottom_shader_limit)
			y_camera = test_level.bottom_shader_limit;
		
		// set camera
		Functions.setCamera(x_camera, y_camera);
		
		// jump
		if (my_modifier.getInputType().getPressedJump() && jump_time)
		{
			test_level.player.quad_object.y_vel = Constants.jump_velocity;
			jump_time = false;
		}
		else if(my_modifier.getInputType().getReleasedJump())
			if(test_level.player.quad_object.y_vel > Constants.jump_limiter)
				test_level.player.quad_object.y_vel = Constants.jump_limiter;
		
		// make sure we dont go through the level (should be deleted later)
		if (test_level.player.quad_object.getYPos() < -1)
		{
			test_level.player.quad_object.y_acc = 0;
			test_level.player.quad_object.y_vel = 0;
			test_level.player.quad_object.setPos(test_level.player.quad_object.getXPos(), 1, EnumDrawFrom.center);
		}
	}
	
	@Override
	public void onDrawObject()
	{
		// player
		test_level.player.quad_object.onDrawAmbient();
		
		for (com.kobaj.level.LevelObject level_object : test_level.object_array)
			level_object.quad_object.onDrawAmbient();
		
		//draw some helpful bounding boxes
		//debug_addon.onDrawObject(test_level);
		
		for (com.kobaj.level.LevelLight level_light : test_level.light_array)
			if (level_light.is_bloom)
				level_light.quad_bloom.onDrawAmbient();
	}
	
	@Override
	public void onDrawLight()
	{
		real_ambient_light.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);
		
		for (com.kobaj.level.LevelLight level_light : test_level.light_array)
			level_light.quad_light.onDrawAmbient();
	}
	
	@Override
	public void onDrawConstant()
	{
		//draw the controls
		my_modifier.onDraw();
	}

	@Override
	public void onDrawLoading(double delta)
	{
		//we want all loading screens to look the same, so we use this helper loader thingy :)
		if(loading_addon != null)
			loading_addon.onDrawLoading(delta);
	}
}
