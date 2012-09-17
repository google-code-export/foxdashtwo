package com.kobaj.level;

import org.simpleframework.xml.Element;

import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class LevelLight
{
	@Element
	public EnumLevelLight light;
	@Element
	public int color;
	@Element
	public double degree;
	@Element
	public double radius;
	@Element
	public boolean is_bloom;
	@Element
	public double x_pos;
	@Element
	public double y_pos;
	
	public QuadColorShape quad_light;
	public QuadColorShape quad_bloom;
	
	public void onInitialize()
	{
		if(light == EnumLevelLight.ambient)
			quad_light = new QuadColorShape(0, com.kobaj.math.Constants.height, com.kobaj.math.Constants.width, 0, color, 0);
		else if(light == EnumLevelLight.point)
		{
			quad_light = new QuadColorShape(radius, color, false, 0);
			if(is_bloom)
				quad_bloom = new QuadColorShape(radius, color, true, 0);
		}
		else
		{
			quad_light = new QuadColorShape(radius, color, 10, 100, degree, false, 0);
			if(is_bloom)
				quad_bloom = new QuadColorShape(radius, color, 10, 100, degree, true, 0);	
		}
		
		quad_light.setPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), com.kobaj.opengldrawable.EnumDrawFrom.center);
		if(is_bloom)
			quad_bloom.setPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), com.kobaj.opengldrawable.EnumDrawFrom.center);
	}
}
