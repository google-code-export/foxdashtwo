package com.kobaj.level.LevelTypeLight;

import org.simpleframework.xml.Element;

import android.graphics.Color;

import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class LevelAmbientLight
{
	@Element
	public boolean active;
	
	@Element
	private String id = "unset"; //identifier. 
	public final String getID()
	{
		return id;
	}
	
	@Element
	public int color;
	
	public Quad quad_light;

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
			quad_light.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Color.WHITE, true);
	}
}
