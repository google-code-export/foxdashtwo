package com.kobaj.input.InputType;

import android.graphics.Color;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class InputTypeNintendo extends InputTypeBase
{
	private QuadColorShape my_quad_left;
	private QuadColorShape my_quad_right;
	private QuadColorShape my_quad_jump;
	
	@Override
	public void onInitialize()
	{
		// these will be replaced with images in the future.
		double shader_circle_pos_y = Functions.screenYToShaderY(Constants.input_circle_width);
		
		// left
		my_quad_left = new QuadColorShape(Constants.input_circle_width, Color.WHITE, 0);
		my_quad_left.setXYPos(Functions.screenXToShaderX(Constants.input_circle_width), shader_circle_pos_y, EnumDrawFrom.bottom_left);
		
		// right
		my_quad_right = new QuadColorShape(Constants.input_circle_width, Color.WHITE, 0);
		my_quad_right.setXYPos(Functions.screenXToShaderX(Constants.input_circle_width * 3), shader_circle_pos_y, EnumDrawFrom.bottom_left);
		
		// jump
		my_quad_jump = new QuadColorShape(Constants.input_circle_width, Color.WHITE, 0);
		my_quad_jump.setXYPos(Functions.screenXToShaderX(Constants.width - Constants.input_circle_width), shader_circle_pos_y, EnumDrawFrom.bottom_right);
	}
	
	@Override
	public void onUnInitialize()
	{
		my_quad_left.onUnInitialize();
		my_quad_right.onUnInitialize();
		my_quad_jump.onUnInitialize();
	}
	
	public boolean getTouchedJump()
	{
		for (int i = 0; i < Constants.input_manager.fingerCount; i++)
			if (Constants.input_manager.getTouched(i))
				if (Functions.inRectF(my_quad_jump.best_fit_aabb.main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)),
						Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
		
		return false;
	}
	
	@Override
	public boolean getPressedJump()
	{
		for (int i = 0; i < Constants.input_manager.fingerCount; i++)
			if (Constants.input_manager.getPressed(i))
				if (Functions.inRectF(my_quad_jump.best_fit_aabb.main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)),
						Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
		
		return false;
	}
	
	@Override
	public boolean getReleasedJump()
	{
		for (int i = 0; i < Constants.input_manager.fingerCount; i++)
			if (Constants.input_manager.getReleased(i))
				if (Functions.inRectF(my_quad_jump.best_fit_aabb.main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)),
						Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
		
		return false;
	}
	
	@Override
	public boolean getTouchedLeft()
	{
		for (int i = 0; i < Constants.input_manager.fingerCount; i++)
			if (Constants.input_manager.getTouched(i))
				if (Functions.inRectF(my_quad_left.best_fit_aabb.main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)),
						Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
		
		return false;
	}
	
	@Override
	public boolean getTouchedRight()
	{
		for (int i = 0; i < Constants.input_manager.fingerCount; i++)
			if (Constants.input_manager.getTouched(i))
				if (Functions.inRectF(my_quad_right.best_fit_aabb.main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)),
						Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
		
		return false;
	}
	
	// this is constant, so we can do text here too
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
		
		// jump
		if (getTouchedJump())
			my_quad_jump.color = Constants.input_press_color;
		else
			my_quad_jump.color = Constants.input_unpress_color;

		my_quad_jump.onDrawAmbient(Constants.my_ip_matrix, true);
		
		// text
		Constants.text.drawText(R.string.left, my_quad_left.x_pos, my_quad_left.y_pos, EnumDrawFrom.center);
		Constants.text.drawText(R.string.right, my_quad_right.x_pos, my_quad_right.y_pos, EnumDrawFrom.center);
		Constants.text.drawText(R.string.jump, my_quad_jump.x_pos, my_quad_jump.y_pos, EnumDrawFrom.center);
	}
}
