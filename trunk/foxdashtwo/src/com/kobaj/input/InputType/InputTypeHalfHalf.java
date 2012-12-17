package com.kobaj.input.InputType;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class InputTypeHalfHalf extends InputTypeBase
{	
	private QuadColorShape my_quad;
	
	@Override
	public void onInitialize()
	{
		my_quad = new QuadColorShape(0, Constants.height, 2, 0, Constants.input_draw_color, 0);
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
	public void onDraw()
	{
		//draw left
		int alpha = 0;
		
		if(getTouchedLeft())
			alpha = (int)(255 * Constants.min_brightness);
		else
			alpha = (int)(255 * Constants.max_brightness);
		my_quad.setPos(Functions.screenXToShaderX(1), Functions.screenYToShaderY(0), EnumDrawFrom.bottom_left);
		my_quad.color = Functions.makeColor(255, 255, 255, alpha);
		my_quad.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, true);
		
		//draw right
		if(getTouchedRight())
			alpha = (int)(255 * Constants.min_brightness);
		else
			alpha = (int)(255 * Constants.max_brightness);
		my_quad.setPos(Functions.screenXToShaderX(Constants.width), Functions.screenYToShaderY(0), EnumDrawFrom.bottom_right);
		my_quad.color = Functions.makeColor(255, 255, 255, alpha);
		my_quad.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, true);	
	}
}
