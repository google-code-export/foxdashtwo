package com.kobaj.level;

import org.simpleframework.xml.Element;

import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class LevelSpotLight extends LevelPointLight
{
	@Element
	public double degree;
	@Element
	public int close_width;
	@Element
	public int far_width;
	
	public void onInitialize()
	{
		quad_light = new QuadColorShape(radius, color, close_width, far_width, degree, false, blur_amount);
		if(is_bloom)
			quad_bloom = new QuadColorShape(radius, color, close_width, far_width, degree, true, blur_amount);	
		
		setupPositions();
	}
}
