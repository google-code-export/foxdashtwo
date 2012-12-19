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
	
	// these are shader coordinates of the center of the button
	public Button(int resource_label)
	{
		this.label = resource_label;
		width = Constants.text.measureTextWidth(label) + padding;
		height = Constants.text.measureTextHeight(label) + padding;
	}
	
	public void onInitialize()
	{
		invisible_outline = new QuadCompressed(R.raw.black_alpha, R.raw.black_alpha, width, height);
	}
	
	private boolean current_touch;
	private boolean old_touch;
	
	public boolean isReleased()
	{
		boolean returned_touch = false;
		current_touch = isTouched();
		
		if(!current_touch && old_touch)
			returned_touch = true;
		
		old_touch = current_touch;
		return returned_touch;
	}
	
	private boolean isTouched()
	{
		for (int i = 0; i < Constants.input_manager.fingerCount; i++)
			if(Constants.input_manager.getTouched(i)) 
			if (Functions.inRectF(invisible_outline.best_fit_aabb.main_rect, // auto format
					Functions.screenXToShaderX(Constants.input_manager.getX(i)), // can some times be
					Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i))))) // really annoying
				return true;
		
		return false;
	}
	
	public void onDrawConstant()
	{
		int color = 0xEEBBBBCC;
		if(isTouched())
			color = 0xEE888899;
			
		invisible_outline.color = color;
		invisible_outline.onDrawAmbient(Constants.my_ip_matrix, true);
		Constants.text.drawText(label, invisible_outline.x_pos, invisible_outline.y_pos, EnumDrawFrom.center, invisible_outline.color, invisible_outline.degree);
	}
}
