package com.kobaj.input.InputType;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;
import com.kobaj.openglgraphics.AmbientLight;

public class InputTypeHalfHalf extends InputTypeBase
{	
	private QuadColorShape my_quad;
	private AmbientLight my_ambient_light;
	
	@Override
	public void onInitialize()
	{
		my_quad = new QuadColorShape(0, Constants.height, 1, 0, Constants.input_draw_color, 0);
		my_ambient_light = new AmbientLight();
	}
	
	@Override
	public boolean getPressedJump()
	{
		if((Constants.input_manager.getTouched(1) && Constants.input_manager.getPressed(0)) ||
		(Constants.input_manager.getTouched(0) && Constants.input_manager.getPressed(1)) ||
		(Constants.input_manager.getPressed(1) && Constants.input_manager.getPressed(0)))
			return true;
		
		return false;
	}

	@Override
	public boolean getReleasedJump()
	{
		if((Constants.input_manager.getTouched(1) && Constants.input_manager.getReleased(0)) ||
		(Constants.input_manager.getTouched(0) && Constants.input_manager.getReleased(1)) ||
		(Constants.input_manager.getReleased(1) && Constants.input_manager.getReleased(0)))
			return true;
		
		return false;
	}

	@Override
	public boolean getTouchedLeft()
	{
		for(int i = 0; i < 2; i++)
			if(Constants.input_manager.getTouched(i) && Constants.input_manager.getX(i) < Constants.width / 2.0)
				return true;
		
		return false;
	}

	@Override
	public boolean getTouchedRight()
	{
		for(int i = 0; i < 2; i++)
			if(Constants.input_manager.getTouched(i) && Constants.input_manager.getX(i) > Constants.width / 2.0)
				return true;
		
		return false;
	}

	@Override
	public boolean getLeftOrRight()
	{
		if(Constants.input_manager.getTouched(0) || Constants.input_manager.getTouched(1))
			return true;
		
		return false;
	}

	@Override
	public void onDraw()
	{
		//draw left
		if(getTouchedLeft())
			my_ambient_light.brightness = Constants.min_brightness;
		else
			my_ambient_light.brightness = Constants.max_brightness;
		my_ambient_light.applyShaderProperties();
		my_quad.setPos(Functions.screenXToShaderX(1), Functions.screenYToShaderY(0), EnumDrawFrom.bottom_left);
		my_quad.onDrawAmbient(Constants.my_view_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);
		
		//draw right
		if(getTouchedRight())
			my_ambient_light.brightness = Constants.min_brightness;
		else
			my_ambient_light.brightness = Constants.max_brightness;
		my_ambient_light.applyShaderProperties();
		my_quad.setPos(Functions.screenXToShaderX(Constants.width), Functions.screenYToShaderY(0), EnumDrawFrom.bottom_right);
		my_quad.onDrawAmbient(Constants.my_view_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);	
	}
}
