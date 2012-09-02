package com.kobaj.screen;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.QuadColorShape;
import com.kobaj.openglgraphics.AmbientLight;

public class SinglePlayerScreen extends BaseScreen
{
	//keep track of our camera
	//shader coordinates
	private double x_camera = 0;
	private double y_camera = 0;
	private double x_player = 0;
	private double y_player = 0;
	
	//get to drawing stuff
	private QuadColorShape real_ambient_light;
	
	//basic light
	private AmbientLight al_ambient_light;
	
	private com.kobaj.level.Level test_level;
	
	public SinglePlayerScreen()
	{
		/* helpful while I build the level class
		 * 
		 * com.kobaj.level.Level test = new com.kobaj.level.Level();
		 * test.writeOut();
		 * com.kobaj.loader.XMLHandler.writeSerialFile(test, "test_level");
		*/
		
		test_level = com.kobaj.loader.XMLHandler.readSerialFile(com.kobaj.math.Constants.resources, R.raw.test_level, com.kobaj.level.Level.class);
	}
	
	@Override
	public void onInitialize()
	{
		al_ambient_light = new AmbientLight();
		
		real_ambient_light = new QuadColorShape(0, Constants.height, Constants.width, 0, 0xFF444444);
		
		if(test_level != null)
			test_level.onInitialize();
		
		Functions.setCamera(x_camera, y_camera);
	}

	@Override
	public void onUpdate(double delta)
	{		
		if(Constants.input_manager.getTouched(0))
		{
			if(Constants.input_manager.getX(0) > Constants.width / 2.0)
				x_player -= .0025 * delta;
			else
				x_player += .0025 * delta;
			
			test_level.player.quad_object.setPos(-x_player, test_level.player.quad_object.getYPos(), EnumDrawFrom.center);
		
			//prepare camera
			x_camera = x_player;
			y_camera = y_player;
			
			//restrict camera movement
			if(x_camera > test_level.x_limit)
				x_camera = test_level.x_limit;
			else if(x_camera < -test_level.x_limit)
				x_camera = - test_level.x_limit;
			
			if(y_camera > test_level.y_limit)
				x_camera = test_level.y_limit;
			else if(y_camera < -test_level.y_limit)
				y_camera = -test_level.y_limit;
			
			Functions.setCamera(x_camera, 0);
		}
		
		//physics
		Constants.physics.apply_physics(delta, test_level.player.quad_object);
		for(com.kobaj.level.LevelObject level_object: test_level.object_array)
			Constants.physics.handle_collision(Constants.physics.check_collision(test_level.player.quad_object, level_object.quad_object), test_level.player.quad_object);	
		
		if(test_level.player.quad_object.getYPos() < -1)
			test_level.player.quad_object.setPos(-x_player, 1, EnumDrawFrom.center);
	}

	@Override
	public void onDrawObject()
	{	
		al_ambient_light.applyShaderProperties();
		
		real_ambient_light.onDrawAmbient();
		
		//player
		test_level.player.quad_object.onDrawAmbient();
		
		for(com.kobaj.level.LevelObject level_object: test_level.object_array)
			level_object.quad_object.onDrawAmbient();
		
		
		for(com.kobaj.level.LevelLight level_light: test_level.light_array)
			if(level_light.is_bloom)
				level_light.quad_bloom.onDrawAmbient();
	}

	@Override
	public void onDrawLight()
	{
		real_ambient_light.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);
		
		for(com.kobaj.level.LevelLight level_light: test_level.light_array)
			level_light.quad_light.onDrawAmbient();
	}

	@Override
	public void onDrawText()
	{
		//should not be doing calculations here!!
		int drawn_count = 0;
		
		if(Functions.onShader(real_ambient_light.phys_rect_list))
			drawn_count++;
		
		for(com.kobaj.level.LevelObject level_object: test_level.object_array)
			if(Functions.onShader(level_object.quad_object.phys_rect_list))
				drawn_count++;
		
		for(com.kobaj.level.LevelLight level_light: test_level.light_array)
		{
			if(Functions.onShader(level_light.quad_light.phys_rect_list))
				drawn_count++;
			if(level_light.is_bloom)
				drawn_count++;
		}
		
		Constants.text.DrawNumber(drawn_count, Functions.screenXToShaderX(100), Functions.screenYToShaderY(100), com.kobaj.opengldrawable.EnumDrawFrom.top_left);
	}
}
