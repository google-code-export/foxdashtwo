package com.kobaj.level.LevelTypeLight;

import org.simpleframework.xml.Element;

import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class LevelPointLight extends LevelBloomLight
{
	@Element
	public double radius;
	
	protected int blur_amount = 0;
	
	@Element
	public EnumLightEffect light_effect;
	private double light_effect_timer = 0;
	
	@Override
	public void onInitialize()
	{
		quad_light = new QuadColorShape(radius, color, false, blur_amount);
		if (is_bloom)
			quad_bloom = new QuadColorShape(radius, color, true, blur_amount);
		
		setupPositions();
		
		// offset so things dont blink/glow togeter
		light_effect_timer = Functions.randomDouble(0, 1000);
	}
	
	@Override
	protected void setupPositions()
	{
		double buffer = radius / 2.0;
		
		quad_light.setXYPos(Functions.screenXToShaderX(x_pos - buffer), Functions.screenYToShaderY(y_pos + buffer), draw_from);
		if(is_bloom)
			quad_bloom.setXYPos(Functions.screenXToShaderX(x_pos - buffer), Functions.screenYToShaderY(y_pos + buffer), draw_from);
	}
	
	@Override
	public void onUpdate(double delta)
	{
		super.onUpdate(delta);
		
		if (light_effect == EnumLightEffect.none)
			return;
		else if (!com.kobaj.math.Functions.onShader(quad_light.best_fit_aabb))
			return;
		else if (light_effect == EnumLightEffect.pulse)
		{
			light_effect_timer += delta / 300.0;
			double radians = Math.toRadians(light_effect_timer);
			if (radians >= Math.PI * 2.0)
				light_effect_timer = 0;
			
			double scaler = (Math.cos(light_effect_timer) + 3.0) / 4.0;
			
			int widthheight = (int) (radius * 2 * scaler);
			quad_light.setWidthHeight(widthheight, widthheight);
			if (is_bloom)
				quad_bloom.setWidthHeight(widthheight, widthheight);
		}
		else if (light_effect == EnumLightEffect.flicker)
		{
			if (light_effect_timer <= 0)
			{
				// generate next random number
				light_effect_timer = Functions.randomInt(40, 800);
				
				// switch on or off
				active = !active;
			}
			else
				light_effect_timer -= delta;
		}
		else if (light_effect == EnumLightEffect.rotate)
		{
			float update_amount = (float) (delta / 8.0);
			light_effect_timer += update_amount;
			
			if (light_effect_timer >= 360)
				light_effect_timer = 0;
			
			quad_light.setRotationZ(light_effect_timer);
			
			if (is_bloom)
				quad_bloom.setRotationZ(light_effect_timer);
		}
	}
}
