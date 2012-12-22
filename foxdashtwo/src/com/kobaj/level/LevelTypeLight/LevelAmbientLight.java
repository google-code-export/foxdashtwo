package com.kobaj.level.LevelTypeLight;

import org.simpleframework.xml.Element;

import com.kobaj.level.LevelEntityActive;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class LevelAmbientLight extends LevelEntityActive
{
	@Element
	public String id = "unset"; // identifier.
	
	@Element
	public int color;
	
	public Quad quad_light;
	
	public void onInitialize()
	{
		quad_light = new QuadColorShape(0, com.kobaj.math.Constants.height, com.kobaj.math.Constants.width, 0, color, 0);
	}
	
	public void onUnInitialize()
	{
		quad_light.onUnInitialize();
	}
	
	public void onUpdate(double delta)
	{
		// do nothing.
	}
	
	public void onDrawLight()
	{
		if (active)
			quad_light.onDrawAmbient(Constants.my_ip_matrix, true);
	}
}
