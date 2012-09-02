package com.kobaj.level;

import org.simpleframework.xml.Element;

import com.kobaj.opengldrawable.QuadColorShape;

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
			quad_light = new QuadColorShape(0, com.kobaj.math.Constants.height, com.kobaj.math.Constants.width, 0, color);
		else if(light == EnumLevelLight.point)
		{
			quad_light = new QuadColorShape(radius, color, false);
			if(is_bloom)
				quad_bloom = new QuadColorShape(radius, color, true);
		}
		else
		{
			quad_light = new QuadColorShape(radius, color, 10, 100, degree, false);
			if(is_bloom)
				quad_bloom = new QuadColorShape(radius, color, 10, 100, degree, true);	
		}
		
		quad_light.setPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), com.kobaj.opengldrawable.EnumDrawFrom.center);
		if(is_bloom)
			quad_bloom.setPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), com.kobaj.opengldrawable.EnumDrawFrom.center);
	}
}
