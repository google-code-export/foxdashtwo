package com.kobaj.screen;

import android.graphics.RectF;

import com.kobaj.foxdashtwo.R;
import com.kobaj.input.GameInputModifier;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;
import com.kobaj.openglgraphics.AmbientLight;

public class SinglePlayerScreen extends BaseScreen
{
	//modification of input
	private GameInputModifier my_modifier;
	
	// get to drawing stuff
	private QuadColorShape real_ambient_light;
	
	// basic light
	private AmbientLight al_ambient_light;
	
	// test level
	private com.kobaj.level.Level test_level;
	
	public SinglePlayerScreen()
	{
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
	public void onInitialize()
	{
		al_ambient_light = new AmbientLight();
		
		real_ambient_light = new QuadColorShape(0, Constants.height, Constants.width, 0, 0xFF444444, 0);
		
		if (test_level != null)
			test_level.onInitialize();
		
		my_modifier.onInitialize();
	}
	
	boolean jump_time = false;
	
	@Override
	public void onUpdate(double delta)
	{
		//just for now, this may be deleted later to replace a button
		my_modifier.onUpdate();
		
		jump_time = false;
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
				if (test_level.player.quad_object.x_vel > 0)
					move_amount = Constants.normal_acceleration;
				else
					move_amount = Constants.normal_reverse_acceleration;
			}
			else
			{
				if (my_modifier.getInputType().getTouchedLeft())
					move_amount = -Constants.normal_acceleration;
				else
					move_amount = -Constants.normal_reverse_acceleration;
			}
			
			// if in the air, apply a dampint.
			if (!jump_time)
				move_amount *= Constants.normal_air_damping;
			
			// add to it
			test_level.player.quad_object.x_acc += move_amount * delta;
		}
		
		// add forces
		// add gravity
		Constants.physics.add_gravity(test_level.player.quad_object);
		
		// add friction
		if (!touched && jump_time) 
		{
			double friction = -Constants.normal_friction * delta * test_level.player.quad_object.x_vel;
			test_level.player.quad_object.x_acc += friction;
		}
		
		// prepare camera
		double x_camera = -test_level.player.quad_object.getXPos();
		double y_camera = -test_level.player.quad_object.getYPos();
		
		// restrict camera movement
		if (x_camera > test_level.x_limit)
			x_camera = test_level.x_limit;
		else if (x_camera < -test_level.x_limit)
			x_camera = -test_level.x_limit;
		
		if (y_camera > test_level.y_limit)
			y_camera = test_level.y_limit;
		else if (y_camera < -test_level.y_limit)
			y_camera = -test_level.y_limit;
		
		// set camera
		Functions.setCamera(x_camera, y_camera);
		
		// jump
		if (my_modifier.getInputType().getPressedJump() && jump_time)
		{
			test_level.player.quad_object.y_vel = .000025 * delta;
			jump_time = false;
		}
		else if(my_modifier.getInputType().getReleasedJump())
			if(test_level.player.quad_object.y_vel > .0005)
				test_level.player.quad_object.y_vel = .0005;
		
		// make sure we dont go through the level
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
		al_ambient_light.applyShaderProperties();
		
		real_ambient_light.onDrawAmbient();
		
		// player
		test_level.player.quad_object.onDrawAmbient();
		
		for (com.kobaj.level.LevelObject level_object : test_level.object_array)
			level_object.quad_object.onDrawAmbient();
		
		// draw some helpful bounding boxes
		/*
		 * for(com.kobaj.level.LevelObject level_object:
		 * test_level.object_array) for(int i =
		 * level_object.quad_object.phys_rect_list.size() - 1; i>= 0; i--)
		 * onDrawBoundingBox
		 * (level_object.quad_object.phys_rect_list.get(i).main_rect); for(int i
		 * = test_level.player.quad_object.phys_rect_list.size() - 1; i>= 0;
		 * i--)
		 * onDrawBoundingBox(test_level.player.quad_object.phys_rect_list.get
		 * (i).main_rect);
		 */
		
		for (com.kobaj.level.LevelLight level_light : test_level.light_array)
			if (level_light.is_bloom)
				level_light.quad_bloom.onDrawAmbient();
	}
	
	private void onDrawBoundingBox(RectF bounding_box)
	{
		double left = Functions.shaderXToScreenX(bounding_box.left);
		double top = Functions.shaderYToScreenY(bounding_box.top);
		double right = Functions.shaderXToScreenX(bounding_box.right);
		double bottom = Functions.shaderYToScreenY(bounding_box.bottom);
		
		double x_center = bounding_box.centerX();
		double y_center = bounding_box.centerY();
		
		// holy garbage creation batman
		QuadColorShape outline = new QuadColorShape((int) left, (int) top, (int) right, (int) bottom, 0x99FF00FF, 0);
		outline.setPos(x_center, y_center, EnumDrawFrom.center);
		outline.onDrawAmbient(Constants.my_view_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);
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
		if(jump_time)
			Constants.text.DrawNumber(1, Functions.screenXToShaderX(100), Functions.screenYToShaderY(100), EnumDrawFrom.top_left);
		
		my_modifier.onDraw();
	}
}
