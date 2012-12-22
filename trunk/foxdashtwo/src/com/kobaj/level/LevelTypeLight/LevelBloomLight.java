package com.kobaj.level.LevelTypeLight;

import org.simpleframework.xml.Element;

import com.kobaj.opengldrawable.Quad.Quad;

public abstract class LevelBloomLight extends LevelAmbientLight
{
	@Element
	public double x_pos;
	@Element
	public double y_pos;
	@Element
	public boolean is_bloom;
	
	public Quad quad_bloom;
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		if(is_bloom)
			quad_bloom.onUnInitialize();
	}
	
	protected void setupPositions()
	{
		quad_light.setXYPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), com.kobaj.opengldrawable.EnumDrawFrom.bottom_left);
		if(is_bloom)
			quad_bloom.setXYPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), com.kobaj.opengldrawable.EnumDrawFrom.bottom_left);	
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
