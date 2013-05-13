package com.kobaj.opengldrawable.Button;

import android.graphics.Color;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadCompressed;
import com.kobaj.opengldrawable.Quad.QuadEmpty;

public class TextButton extends Button
{
	public int label;
	protected int padding = 35;
	protected int height = 64;// 23 + padding;
	
	private QuadCompressed left_outline;
	private QuadCompressed right_outline;
	private QuadCompressed middle_outline;
	
	private double shader_half_width;
	
	// these are shader coordinates of the center of the button
	public TextButton(int resource_label, boolean draw_background)
	{
		this.label = resource_label;
		width = Constants.text.measureTextWidth(label) + padding;
		
		this.draw_background = draw_background;
	}
	
	public void onInitialize()
	{
		// even if we dont draw this, we will need to instantiate it so we have something to check a bounding box with.
		invisible_outline = new QuadEmpty(width, height);
		
		if (draw_background)
		{
			int local_width = width;
			if (draw_background)
				local_width -= 64;
			
			shader_half_width = Functions.screenWidthToShaderWidth(local_width) / 2.0;
			
			middle_outline = new QuadCompressed(R.raw.ui_button_middle, R.raw.ui_button_middle_alpha, local_width, 64);
			
			left_outline = new QuadCompressed(R.raw.ui_button_left, R.raw.ui_button_left_alpha, 32, 64);
			
			right_outline = new QuadCompressed(R.raw.ui_button_right, R.raw.ui_button_right_alpha, 32, 64);
		}
	}
	
	@Override
	public void onUnInitialize()
	{
		invisible_outline.onUnInitialize();
		
		if (draw_background)
		{
			middle_outline.onUnInitialize();
			left_outline.onUnInitialize();
			right_outline.onUnInitialize();
		}
	}
	
	@Override
	public void setXYPos(double x, double y, EnumDrawFrom draw_from)
	{
		invisible_outline.setXYPos(x, y, draw_from);
		
		if (draw_background)
		{
			middle_outline.setXYPos(x, y, draw_from);
			left_outline.setXYPos(x - shader_half_width - Constants.sixteen, y, draw_from);
			right_outline.setXYPos(x + shader_half_width + Constants.sixteen, y, draw_from);
		}
	}
	
	public void onDrawConstant()
	{
		if (draw_background)
		{
			int color = Constants.ui_button_unpressed;
			if (isTouched())
				color = Constants.ui_button_pressed;
			
			middle_outline.color = color;
			middle_outline.onDrawAmbient(Constants.my_ip_matrix, true);
			
			left_outline.color = color;
			right_outline.color = color;
			
			left_outline.onDrawAmbient(Constants.my_ip_matrix, true);
			right_outline.onDrawAmbient(Constants.my_ip_matrix, true);
		}
		
		Constants.text.drawText(label, invisible_outline.x_pos_shader, invisible_outline.y_pos_shader + .01, EnumDrawFrom.center, Color.WHITE, invisible_outline.degree);
	}
}
