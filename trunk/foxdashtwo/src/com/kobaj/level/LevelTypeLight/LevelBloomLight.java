package com.kobaj.level.LevelTypeLight;

import org.simpleframework.xml.Element;

import android.graphics.Color;

import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
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
	
	protected EnumDrawFrom draw_from = com.kobaj.opengldrawable.EnumDrawFrom.top_left;
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		if(is_bloom)
			quad_bloom.onUnInitialize();
	}
	
	protected void setupPositions()
	{
		quad_light.setXYPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), draw_from);
		if(is_bloom)
			quad_bloom.setXYPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), draw_from);	
	}
	
	@Override
	public void onDrawLight()
	{
		if(active || quad_light.color != Color.BLACK)
			quad_light.onDrawAmbient();
	}
	
	public void onDrawObject()
	{
		if(is_bloom)
		if(active || quad_light.color != Color.TRANSPARENT)
		{
			// blue, or green would also work
			int red = Functions.red(quad_light.color);
			
			quad_bloom.color = Functions.linearInterpolateColor(0, 255, red, Color.TRANSPARENT, Color.WHITE);
			
			quad_bloom.onDrawAmbient();
		}
	}
}
