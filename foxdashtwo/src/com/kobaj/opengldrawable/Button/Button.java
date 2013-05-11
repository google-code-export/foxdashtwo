package com.kobaj.opengldrawable.Button;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public abstract class Button
{
	private boolean current_touch;
	private boolean old_touch;
	
	protected QuadCompressed invisible_outline;
	
	public int width;
	public int height;
	
	protected boolean draw_background = false;
	
	public abstract void onInitialize();
	
	public void setXYPos(double x, double y, EnumDrawFrom draw_from)
	{
		invisible_outline.setXYPos(x, y, draw_from);
	}
	
	public QuadCompressed invisible_outline()
	{
		return invisible_outline;
	}
	
	public void onUnInitialize()
	{
		invisible_outline.onUnInitialize();
	}
	
	public boolean isReleased()
	{
		boolean returned_touch = false;
		current_touch = isTouched();
		
		if (!current_touch && old_touch)
			returned_touch = true;
		
		old_touch = current_touch;
		return returned_touch;
	}
	
	protected boolean isTouched()
	{
		// get
		double x = Functions.screenXToShaderX(Constants.input_manager.getX(0));
		double y = Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(0)));
		
		// shift
		x -= invisible_outline.x_pos_shader;
		y -= invisible_outline.y_pos_shader;
		
		// rotate
		final double rads = (float) Math.toRadians(-invisible_outline.degree);
		final double cos_rads = Math.cos(rads);
		final double sin_rads = Math.sin(rads);
		double nx = (x * cos_rads - y * sin_rads);
		double ny = (y * cos_rads + x * sin_rads);
		
		// shift back
		nx += invisible_outline.x_pos_shader;
		ny += invisible_outline.y_pos_shader;
		
		// check
		for (int i = 0; i < Constants.input_manager.fingerCount; i++)
			if (Constants.input_manager.getTouched(i))
				if (Functions.inRectF(invisible_outline.unrotated_aabb.main_rect, nx, ny))
					return true;
		
		return false;
	}
	
	public abstract void onDrawConstant();
}
