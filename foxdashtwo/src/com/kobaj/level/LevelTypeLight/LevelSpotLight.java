package com.kobaj.level.LevelTypeLight;

import org.simpleframework.xml.Element;

import com.kobaj.math.Functions;
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
		degree = ((degree % 360.0) + 360.0) % 360.0;
		
		quad_light = new QuadColorShape(radius, color, close_width, far_width, false, blur_amount);
		if (is_bloom)
			quad_bloom = new QuadColorShape(radius, color, close_width, far_width, true, blur_amount);
		
		setupPositions();
	}
	
	@Override
	protected void setupPositions()
	{
		quad_light.setXYPos(Functions.screenXToShaderX(x_pos), Functions.screenYToShaderY(y_pos), draw_from);
		if (is_bloom)
			quad_bloom.setXYPos(Functions.screenXToShaderX(x_pos), Functions.screenYToShaderY(y_pos), draw_from);
		
		quad_light.setRotationZ(degree);
		if (is_bloom)
			quad_bloom.setRotationZ(degree);
	}
}
