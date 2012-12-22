package com.kobaj.opengldrawable;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public class Button
{
	private int label;
	private int padding = 35;
	public QuadCompressed invisible_outline;
	
	public int width;
	public int height;
	
	public boolean draw_background = true;
	
	// these are shader coordinates of the center of the button
	public Button(int resource_label)
	{
		this.label = resource_label;
		width = Constants.text.measureTextWidth(label) + padding;
		height = /* Constants.text.measureTextHeight(label) */23 + padding;
	}
	
	public void onInitialize()
	{
		// even if we dont draw this, we will need to instantiate it so we have something to check a bounding box with.
		invisible_outline = new QuadCompressed(R.raw.black_alpha, R.raw.black_alpha, width, height);
	}
	
	public void onUnInitialize()
	{
		invisible_outline.onUnInitialize();
	}
	
	private boolean current_touch;
	private boolean old_touch;
	
	public boolean isReleased()
	{
		boolean returned_touch = false;
		current_touch = isTouched();
		
		if (!current_touch && old_touch)
			returned_touch = true;
		
		old_touch = current_touch;
		return returned_touch;
	}
	
	private boolean isTouched()
	{
		// get
		double x = Functions.screenXToShaderX(Constants.input_manager.getX(0));
		double y = Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(0)));
		
		// shift
		x -= invisible_outline.x_pos;
		y -= invisible_outline.y_pos;
		
		// rotate
		final double rads = (float) Math.toRadians(-invisible_outline.degree);
		final double cos_rads = Math.cos(rads);
		final double sin_rads = Math.sin(rads);
		double nx = (x * cos_rads - y * sin_rads);
		double ny = (y * cos_rads + x * sin_rads);
		
		// shift back
		nx += invisible_outline.x_pos;
		ny += invisible_outline.y_pos;
		
		// check
		for (int i = 0; i < Constants.input_manager.fingerCount; i++)
			if (Constants.input_manager.getTouched(i))
				if (Functions.inRectF(invisible_outline.unrotated_aabb.main_rect, nx, ny))
					return true;
		
		return false;
	}
	
	public void onDrawConstant()
	{
		if (draw_background)
		{
			int color = Constants.unpressed_color;
			if (isTouched())
				color = Constants.pressed_color;
			
			invisible_outline.color = color;
			invisible_outline.onDrawAmbient(Constants.my_ip_matrix, true);
		}
		
		Constants.text.drawText(label, invisible_outline.x_pos, invisible_outline.y_pos, EnumDrawFrom.center, invisible_outline.color, invisible_outline.degree);
	}
}
