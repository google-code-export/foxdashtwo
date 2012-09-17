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
	
	private final double circle_width = 50;
	
	@Override
	public void onInitialize()
	{
		//left
		my_quad_left = new QuadColorShape(circle_width * Constants.dip_scale, 0x88FFFFFF, 0);
		my_quad_left.setPos(Functions.screenXToShaderX(circle_width * Constants.dip_scale), Functions.screenYToShaderY(circle_width * Constants.dip_scale), EnumDrawFrom.bottom_left);
		
		//right
		my_quad_right = new QuadColorShape(circle_width * Constants.dip_scale, 0x88FFFFFF, 0);
		my_quad_right.setPos(Functions.screenXToShaderX(circle_width * Constants.dip_scale * 3), Functions.screenYToShaderY(circle_width* Constants.dip_scale), EnumDrawFrom.bottom_left);
		
		//jump
		my_quad_jump = new QuadColorShape(circle_width * Constants.dip_scale, 0x88FFFFFF, 0);
		my_quad_jump.setPos(Functions.screenXToShaderX(Constants.width - (circle_width * Constants.dip_scale)), Functions.screenYToShaderY(circle_width * Constants.dip_scale), EnumDrawFrom.bottom_right);
		
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
		//return getTouchedJump();
		
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
		
		if(getTouchedJump())
			my_ambient_light.brightness = Constants.min_brightness;
		else
			my_ambient_light.brightness = Constants.max_brightness;
		my_ambient_light.applyShaderProperties();
		my_quad_jump.onDrawAmbient(Constants.my_view_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);
	}
}
