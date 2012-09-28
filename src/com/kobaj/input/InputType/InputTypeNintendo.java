package com.kobaj.input.InputType;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;
import com.kobaj.openglgraphics.AmbientLight;

public class InputTypeNintendo extends InputTypeBase
{
	private QuadColorShape my_quad_left;
	private QuadColorShape my_quad_right;
	private QuadColorShape my_quad_jump;
	
	private AmbientLight my_ambient_light;
	
	@Override
	public void onInitialize()
	{
		//these will be replaced with images in the future.
		double circle_size = Constants.input_circle_width;
		double shader_circle_size_x = Functions.screenWidthToShaderWidth(circle_size);
		double shader_circle_pos_y = Functions.screenYToShaderY(circle_size);
		
		//left
		my_quad_left = new QuadColorShape(circle_size, Constants.input_draw_color, 0);
		my_quad_left.setPos(Functions.screenXToShaderX(Constants.input_circle_width), shader_circle_pos_y, EnumDrawFrom.bottom_left);
		
		//right
		my_quad_right = new QuadColorShape(circle_size, Constants.input_draw_color, 0);
		my_quad_right.setPos(Functions.screenXToShaderX(Constants.input_circle_width * 3), shader_circle_pos_y, EnumDrawFrom.bottom_left);
		
		//jump
		my_quad_jump = new QuadColorShape(circle_size, Constants.input_draw_color, 0);
		my_quad_jump.setPos(Functions.screenXToShaderX(Constants.width - circle_size), shader_circle_pos_y, EnumDrawFrom.bottom_right);
		
		my_ambient_light = new AmbientLight();
	}
	
	public boolean getTouchedJump()
	{
		for(int i = 0; i < Constants.input_manager.fingerCount; i++)
			if(Constants.input_manager.getTouched(i))
				if(Functions.inRectF(my_quad_jump.phys_rect_list.get(0).main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)), Functions.screenYToShaderY(Constants.input_manager.getY(i))))
					return true;
				
		return false;
	}
	
	@Override
	public boolean getPressedJump()
	{
		for(int i = 0; i < Constants.input_manager.fingerCount; i++)
			if(Constants.input_manager.getPressed(i))
				if(Functions.inRectF(my_quad_jump.phys_rect_list.get(0).main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)), Functions.screenYToShaderY(Constants.input_manager.getY(i))))
					return true;
				
		return false;
	}

	@Override
	public boolean getReleasedJump()
	{
		for(int i = 0; i < Constants.input_manager.fingerCount; i++)
			if(Constants.input_manager.getReleased(i))
				if(Functions.inRectF(my_quad_jump.phys_rect_list.get(0).main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)), Functions.screenYToShaderY(Constants.input_manager.getY(i))))
					return true;
				
		return false;
	}

	@Override
	public boolean getTouchedLeft()
	{
		for(int i = 0; i < Constants.input_manager.fingerCount; i++)
			if(Constants.input_manager.getTouched(i))
				if(Functions.inRectF(my_quad_left.phys_rect_list.get(0).main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)), Functions.screenYToShaderY(Constants.input_manager.getY(i))))
					return true;
				
		return false;
	}

	@Override
	public boolean getTouchedRight()
	{
		for(int i = 0; i < Constants.input_manager.fingerCount; i++)
			if(Constants.input_manager.getTouched(i))
				if(Functions.inRectF(my_quad_right.phys_rect_list.get(0).main_rect, Functions.screenXToShaderX(Constants.input_manager.getX(i)), Functions.screenYToShaderY(Constants.input_manager.getY(i))))
					return true;
				
		return false;
	}

	@Override
	public boolean getLeftOrRight()
	{
		return (getTouchedLeft() || getTouchedRight());
	}

	@Override
	public void onDraw()
	{
		//left
		if(getTouchedLeft())
			my_ambient_light.brightness = Constants.min_brightness;
		else
			my_ambient_light.brightness = Constants.max_brightness;
		my_ambient_light.applyShaderProperties();
		my_quad_left.onDrawAmbient(Constants.my_view_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);
		
		//right
		if(getTouchedRight())
			my_ambient_light.brightness = Constants.min_brightness;
		else
			my_ambient_light.brightness = Constants.max_brightness;
		my_ambient_light.applyShaderProperties();
		my_quad_right.onDrawAmbient(Constants.my_view_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);
		
		//jump
		if(getTouchedJump())
			my_ambient_light.brightness = Constants.min_brightness;
		else
			my_ambient_light.brightness = Constants.max_brightness;
		my_ambient_light.applyShaderProperties();
		my_quad_jump.onDrawAmbient(Constants.my_view_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);
	}
}
