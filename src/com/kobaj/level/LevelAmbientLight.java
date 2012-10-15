package com.kobaj.level;

import org.simpleframework.xml.Element;

import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class LevelAmbientLight
{
	@Element
	public boolean active;
	
	@Element
	private String id; //identifier. 
	public final String getID()
	{
		return id;
	}
	
	@Element
	public int color;
	
	public QuadColorShape quad_light;

	public void onInitialize()
	{
		quad_light = new QuadColorShape(0, com.kobaj.math.Constants.height, com.kobaj.math.Constants.width, 0, color, 0);
	}

	public void onUpdate(double delta)
	{
		// do nothing.	
	}
	
	public void onDrawLight()
	{
		if(active)
			quad_light.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);
	}
}
