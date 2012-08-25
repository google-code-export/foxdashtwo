package com.kobaj.screen;

import android.graphics.Color;

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
	
	public SinglePlayerScreen()
	{
		
	}
	
	@Override
	public void onInitialize()
	{
		al_ambient_light = new AmbientLight(Constants.ambient_light, Constants.my_view_matrix);
		
		IC = new Quad(R.drawable.ic_launcher);
		real_ambient_light = new QuadColorShape(0, Constants.height, Constants.width, 0, Color.WHITE);
		
		Functions.setCamera(x_camera, y_camera);
	}

	@Override
	public void onUpdate(double delta)
	{
		if(Constants.input_manager.getTouched(0))
		{
			if(Constants.input_manager.getX(0) > Constants.width / 2.0)
				x_camera -= .005;
			else
				x_camera += .005;
			Functions.setCamera(x_camera, 0);
		}
	}

	@Override
	public void onDrawObject()
	{
		al_ambient_light.applyShaderProperties();
		
		real_ambient_light.onDrawAmbient();
		IC.onDrawAmbient();
	}

	@Override
	public void onDrawLight()
	{
		real_ambient_light.onDrawAmbient();
	}
	
}
