package com.kobaj.screen;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.Quad;
import com.kobaj.opengldrawable.QuadColorShape;
import com.kobaj.openglgraphics.AmbientLight;

public class SinglePlayerScreen extends BaseScreen
{
	//keep track of our camera
	private double x_camera = 0;
	private double y_camera = 0;
	
	//get to drawing stuff
	Quad IC;
	QuadColorShape real_ambient_light;
	
	AmbientLight al_ambient_light;
	
	com.kobaj.level.Level test_level;
	
	public SinglePlayerScreen()
	{
		test_level = com.kobaj.loader.XMLHandler.readSerialFile(com.kobaj.math.Constants.resources, R.raw.test_level, com.kobaj.level.Level.class);
	}
	
	@Override
	public void onInitialize()
	{
		al_ambient_light = new AmbientLight(Constants.ambient_light, Constants.my_view_matrix);
		
		IC = new Quad(R.drawable.ic_launcher);
		real_ambient_light = new QuadColorShape(0, Constants.height, Constants.width, 0, 0xFF444444);
		
		test_level.onInitialize();
		
		Functions.setCamera(x_camera, y_camera);
	}

	@Override
	public void onUpdate(double delta)
	{
		if(Constants.input_manager.getTouched(0))
		{
			if(Constants.input_manager.getX(0) > Constants.width / 2.0)
				x_camera -= .01;
			else
				x_camera += .01;
			Functions.setCamera(x_camera, 0);
		}
	}

	@Override
	public void onDrawObject()
	{
		al_ambient_light.applyShaderProperties();
		
		real_ambient_light.onDrawAmbient();
		IC.onDrawAmbient();
		
		for(com.kobaj.level.LevelObject level_object: test_level.object_array)
			level_object.quad_object.onDrawAmbient();
		
		/*for(com.kobaj.level.LevelLight level_light: test_level.light_array)
			if(level_light.is_bloom)
				level_light.quad_bloom.onDrawAmbient();
		*/
	}

	@Override
	public void onDrawLight()
	{
		real_ambient_light.onDrawAmbient();
		
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
		if(Functions.onShader(IC.phys_rect_list))
			drawn_count++;
		
		for(com.kobaj.level.LevelObject level_object: test_level.object_array)
			if(Functions.onShader(level_object.quad_object.phys_rect_list))
				drawn_count++;
		
		for(com.kobaj.level.LevelLight level_light: test_level.light_array)
			if(Functions.onShader(level_light.quad_light.phys_rect_list))
				drawn_count++;
		
		Constants.text.DrawNumber(drawn_count, Functions.screenXToShaderX(100), Functions.screenYToShaderY(100), com.kobaj.opengldrawable.EnumDrawFrom.top_left);
	}
}
