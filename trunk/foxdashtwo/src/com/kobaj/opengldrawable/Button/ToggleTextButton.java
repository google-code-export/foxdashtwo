package com.kobaj.opengldrawable.Button;

import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.EnumDrawFrom;

public class ToggleTextButton extends TextButton
{
	public int label_pointer = 0;
	private int[] labels;
	
	// these are shader coordinates of the center of the button
	public ToggleTextButton(int... resource_label)
	{
		super(resource_label[0]);
		
		this.labels = resource_label;
		
		int max_width = Integer.MIN_VALUE;
		for (int i = labels.length - 1; i >= 0; i--)
		{
			int measured_size = Constants.text.measureTextWidth(labels[i]);
			if (measured_size > max_width)
				max_width = measured_size;
		}
		
		width = max_width + padding;
	}
	
	@Override
	public boolean isReleased()
	{
		boolean pushed = super.isReleased();
		
		if(pushed)
			label_pointer = (label_pointer + 1) % labels.length;
		
		return pushed;
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
		
		Constants.text.drawText(labels[label_pointer], invisible_outline.x_pos_shader, invisible_outline.y_pos_shader, EnumDrawFrom.center, invisible_outline.color, invisible_outline.degree);
	}
	
}
