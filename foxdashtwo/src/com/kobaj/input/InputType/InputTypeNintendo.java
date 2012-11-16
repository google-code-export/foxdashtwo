package com.kobaj.input.InputType;

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
		//these will be replaced with images in the future.
		double shader_circle_pos_y = Functions.screenYToShaderY(Constants.input_circle_width);
		
		//left
		my_quad_left = new QuadColorShape(Constants.input_circle_width, Constants.input_draw_color, 0);
		my_quad_left.setPos(Functions.screenXToShaderX(Constants.input_circle_width), shader_circle_pos_y, EnumDrawFrom.bottom_left);
		
		//right
		my_quad_right = new QuadColorShape(Constants.input_circle_width, Constants.input_draw_color, 0);
		my_quad_right.setPos(Functions.screenXToShaderX(Constants.input_circle_width * 3), shader_circle_pos_y, EnumDrawFrom.bottom_left);
		
		//jump
		my_quad_jump = new QuadColorShape(Constants.input_circle_width, Constants.input_draw_color, 0);
		my_quad_jump.setPos(Functions.screenXToShaderX(Constants.width - Constants.input_circle_width), shader_circle_pos_y, EnumDrawFrom.bottom_right);
	}
	
	public boolean getTouchedJump()
	{
		for(int i = 0; i < Constants.input_manager.fingerCount; i++)
			if(Constants.input_manager.getTouched(i))
				if(Functions.inRectF(my_quad_jump.phys_rect_list.get(0).main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)), Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
				
		return false;
	}
	
	@Override
	public boolean getPressedJump()
	{
		for(int i = 0; i < Constants.input_manager.fingerCount; i++)
			if(Constants.input_manager.getPressed(i))
				if(Functions.inRectF(my_quad_jump.phys_rect_list.get(0).main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)), Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
				
		return false;
	}

	@Override
	public boolean getReleasedJump()
	{
		for(int i = 0; i < Constants.input_manager.fingerCount; i++)
			if(Constants.input_manager.getReleased(i))
				if(Functions.inRectF(my_quad_jump.phys_rect_list.get(0).main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)), Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
				
		return false;
	}

	@Override
	public boolean getTouchedLeft()
	{
		for(int i = 0; i < Constants.input_manager.fingerCount; i++)
			if(Constants.input_manager.getTouched(i))
				if(Functions.inRectF(my_quad_left.phys_rect_list.get(0).main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)), Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
				
		return false;
	}

	@Override
	public boolean getTouchedRight()
	{
		for(int i = 0; i < Constants.input_manager.fingerCount; i++)
			if(Constants.input_manager.getTouched(i))
				if(Functions.inRectF(my_quad_right.phys_rect_list.get(0).main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)), Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i)))))
					return true;
				
		return false;
	}

	//this is constant, so we can do text here too
	@Override
	public void onDraw()
	{
		//left
		int alpha = 0;
		
		if(getTouchedLeft())
			alpha = (int)(255 * Constants.min_brightness);
		else
			alpha = (int)(255 * Constants.max_brightness);
		my_quad_left.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Functions.makeColor(255, 255, 255, alpha), true);
		
		//right
		if(getTouchedRight())
			alpha = (int)(255 * Constants.min_brightness);
		else
			alpha = (int)(255 * Constants.max_brightness);
		my_quad_right.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Functions.makeColor(255, 255, 255, alpha), true);
		
		//jump
		if(getTouchedJump())
			alpha = (int)(255 * Constants.min_brightness);
		else
			alpha = (int)(255 * Constants.max_brightness);
		my_quad_jump.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Functions.makeColor(255, 255, 255, alpha), true);
	
		//text
		Constants.text.drawText(R.string.left, my_quad_left.x_pos, my_quad_left.y_pos, EnumDrawFrom.center);
		Constants.text.drawText(R.string.right, my_quad_right.x_pos, my_quad_right.y_pos, EnumDrawFrom.center);
		Constants.text.drawText(R.string.jump, my_quad_jump.x_pos, my_quad_jump.y_pos, EnumDrawFrom.center);
	}
}
