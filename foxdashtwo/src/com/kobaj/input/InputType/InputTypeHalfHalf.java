package com.kobaj.input.InputType;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public class InputTypeHalfHalf extends InputTypeBase
{
	private Quad my_quad_left;
	private Quad my_quad_right;
	
	@Override
	public void onInitialize()
	{
		// make
		my_quad_left = new QuadCompressed(R.raw.white, R.raw.white, 2, Constants.height);
		my_quad_right = new QuadCompressed(R.raw.white, R.raw.white, 2, Constants.height);
		
		// move
		my_quad_left.setXYPos(Functions.screenXToShaderX(2), Functions.screenYToShaderY(0), EnumDrawFrom.bottom_left);
		my_quad_right.setXYPos(Functions.screenXToShaderX(Constants.width - 2), Functions.screenYToShaderY(0), EnumDrawFrom.bottom_right);
	}
	
	@Override
	public void onUnInitialize()
	{
		my_quad_left.onUnInitialize();
		my_quad_right.onUnInitialize();
	}
	
	@Override
	public boolean getPressedJump()
	{
		if ((Constants.input_manager.getTouched(1) && Constants.input_manager.getPressed(0)) || //
				(Constants.input_manager.getTouched(0) && Constants.input_manager.getPressed(1)) || //
				(Constants.input_manager.getPressed(1) && Constants.input_manager.getPressed(0))) //
			return true;
		
		return false;
	}
	
	@Override
	public boolean getReleasedJump()
	{
		if ((Constants.input_manager.getTouched(1) && Constants.input_manager.getReleased(0)) || //
				(Constants.input_manager.getTouched(0) && Constants.input_manager.getReleased(1)) || //
				(Constants.input_manager.getReleased(1) && Constants.input_manager.getReleased(0))) //
			return true;
		
		return false;
	}
	
	@Override
	public boolean getTouchedLeft()
	{
		if (Constants.input_manager.getTouched(0) && Constants.input_manager.getX(0) < Constants.width / 2.0)
			return true;
		
		if (Constants.input_manager.getTouched(1) && Constants.input_manager.getX(1) < Constants.width / 2.0)
			if(!getTouchedRight())
				return true;
		
		return false;
	}
	
	@Override
	public boolean getTouchedRight()
	{
		if (Constants.input_manager.getTouched(0) && Constants.input_manager.getX(0) > Constants.width / 2.0)
			return true;
	
		if (Constants.input_manager.getTouched(1) && Constants.input_manager.getX(1) > Constants.width / 2.0)
			if(!getTouchedLeft())
				return true;
		
		return false;
	}
	
	@Override
	public void onDraw()
	{
		// draw left
		if (getTouchedLeft())
			my_quad_left.color = Constants.input_press_color;
		else
			my_quad_left.color = Constants.input_unpress_color;
		
		my_quad_left.onDrawAmbient(Constants.my_ip_matrix, true);
		
		// draw right
		if (getTouchedRight())
			my_quad_right.color = Constants.input_press_color;
		else
			my_quad_right.color = Constants.input_unpress_color;
		
		my_quad_right.onDrawAmbient(Constants.my_ip_matrix, true);
	}
}
