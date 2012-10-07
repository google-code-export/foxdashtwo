package com.kobaj.level;

import org.simpleframework.xml.Element;

import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class LevelPointLight extends LevelAmbientLight
{
	@Element
	public double radius;
	@Element
	public boolean is_bloom;
	@Element
	public double x_pos;
	@Element
	public double y_pos;
	@Element
	public int blur_amount;
	
	public QuadColorShape quad_bloom;
	
	@Override
	public void onInitialize()
	{
		quad_light = new QuadColorShape(radius, color, false, blur_amount);
		if(is_bloom)
			quad_bloom = new QuadColorShape(radius, color, true, blur_amount);
	}
	
	protected void setupPositions()
	{
		quad_light.setPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), com.kobaj.opengldrawable.EnumDrawFrom.bottom_left);
		if(is_bloom)
			quad_bloom.setPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), com.kobaj.opengldrawable.EnumDrawFrom.bottom_left);	
	}
	
	@Override
	public void onDrawLight()
	{
		if(active)
			quad_light.onDrawAmbient();
	}
	
	public void onDrawObject()
	{
		if(active && is_bloom)
			quad_bloom.onDrawAmbient();
	}
}
