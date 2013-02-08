package com.kobaj.opengldrawable.Button;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public class TextButton extends Button
{
	private int label;
	protected int padding = 35;
	protected int height = 23 + padding;
	
	// these are shader coordinates of the center of the button
	public TextButton(int resource_label)
	{
		this.label = resource_label;
		width = Constants.text.measureTextWidth(label) + padding;
	}
	
	public void onInitialize()
	{
		// even if we dont draw this, we will need to instantiate it so we have something to check a bounding box with.
		invisible_outline = new QuadCompressed(R.raw.white, R.raw.white, width, height);
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
