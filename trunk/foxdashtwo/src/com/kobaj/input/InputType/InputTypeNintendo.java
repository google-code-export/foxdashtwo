package com.kobaj.input.InputType;

import android.graphics.Color;

import com.kobaj.account_settings.UserSettings;
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
		// left
		my_quad_left = new QuadColorShape(Constants.input_circle_width, Color.WHITE, 0);

		// right
		my_quad_right = new QuadColorShape(Constants.input_circle_width, Color.WHITE, 0);

		// jump
		my_quad_jump = new QuadColorShape(Constants.input_circle_width, Color.WHITE, 0);

		updateUserSetPositions();
	}
	
	@Override
	public void updateUserSetPositions()
	{
		my_quad_left.setXYPos(Functions.screenXToShaderX(UserSettings.left_button_position.x), 
				Functions.screenYToShaderY(Functions.fix_y(UserSettings.left_button_position.y)), EnumDrawFrom.center);
		my_quad_right.setXYPos(Functions.screenXToShaderX(UserSettings.right_button_position.x), 
				Functions.screenYToShaderY(Functions.fix_y(UserSettings.right_button_position.y)), EnumDrawFrom.center);
		my_quad_jump.setXYPos(Functions.screenXToShaderX(UserSettings.jump_button_position.x), 
				Functions.screenYToShaderY(Functions.fix_y(UserSettings.jump_button_position.y)), EnumDrawFrom.center);
	}
	
	@Override
	public void onUnInitialize()
	{
		my_quad_left.onUnInitialize();
		my_quad_right.onUnInitialize();
		my_quad_jump.onUnInitialize();
	}
	
	@Override
	public boolean getTouchedJump()
	{
		for (int i = 0; i < Constants.input_manager.finger_count; i++)
			if (Constants.input_manager.getTouched(i))
				if (Functions.inRectF(my_quad_jump.best_fit_aabb.main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)),
						Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
		
		return false;
	}
	
	@Override
	public boolean getPressedJump()
	{
		for (int i = 0; i < Constants.input_manager.finger_count; i++)
			if (Constants.input_manager.getPressed(i))
				if (Functions.inRectF(my_quad_jump.best_fit_aabb.main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)),
						Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
		
		return false;
	}
	
	@Override
	public boolean getReleasedJump()
	{
		for (int i = 0; i < Constants.input_manager.finger_count; i++)
			if (Constants.input_manager.getReleased(i))
				if (Functions.inRectF(my_quad_jump.best_fit_aabb.main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)),
						Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
		
		return false;
	}
	
	@Override
	public boolean getTouchedLeft()
	{
		for (int i = 0; i < Constants.input_manager.finger_count; i++)
			if (Constants.input_manager.getTouched(i))
				if (Functions.inRectF(my_quad_left.best_fit_aabb.main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)),
						Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
		
		return false;
	}
	
	@Override
	public boolean getTouchedRight()
	{
		for (int i = 0; i < Constants.input_manager.finger_count; i++)
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
		Constants.text.drawText(R.string.left, my_quad_left.x_pos_shader, my_quad_left.y_pos_shader, EnumDrawFrom.center, my_quad_left.color);
		Constants.text.drawText(R.string.right, my_quad_right.x_pos_shader, my_quad_right.y_pos_shader, EnumDrawFrom.center, my_quad_right.color);
		Constants.text.drawText(R.string.jump, my_quad_jump.x_pos_shader, my_quad_jump.y_pos_shader, EnumDrawFrom.center, my_quad_jump.color);
	}
}
