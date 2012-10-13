package com.kobaj.level;

import org.simpleframework.xml.Element;

import com.kobaj.math.Functions;
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
	@Element
	public EnumLightEffect light_effect;
	private double light_effect_timer = 0;
	
	public QuadColorShape quad_bloom;
	
	@Override
	public void onInitialize()
	{
		quad_light = new QuadColorShape(radius, color, false, blur_amount);
		if(is_bloom)
			quad_bloom = new QuadColorShape(radius, color, true, blur_amount);
		
		setupPositions();
		
		//offset so things dont blink/glow togeter
		light_effect_timer = Functions.randomDouble(0, 1000);
	}
	
	protected void setupPositions()
	{
		quad_light.setPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), com.kobaj.opengldrawable.EnumDrawFrom.bottom_left);
		if(is_bloom)
			quad_bloom.setPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), com.kobaj.opengldrawable.EnumDrawFrom.bottom_left);	
	}
	
	@Override
	public void onUpdate(double delta)
	{
		if(light_effect == EnumLightEffect.none)
			return;
		else if(light_effect == EnumLightEffect.pulse)
		{
			light_effect_timer += delta / 300.0;
			double radians = Math.toRadians(light_effect_timer);
			if(radians >= Math.PI * 2.0)
				light_effect_timer = 0;
			
			double scaler = (Math.cos(light_effect_timer) + 3.0) / 4.0;
			
			int widthheight = (int)(radius * 2 * scaler);
			quad_light.setWidthHeight(widthheight, widthheight);
			if(is_bloom)
				quad_bloom.setWidthHeight(widthheight, widthheight);
		}
		else if(light_effect == EnumLightEffect.flicker)
		{
			if(light_effect_timer <= 0)
			{
				//generate next random number
				light_effect_timer = Functions.randomInt(50, 900);
				
				//switch on or off
				active = !active;
			}
			else
				light_effect_timer -= delta;
				
		}
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
