package com.kobaj.math;

import com.kobaj.math.android.RectF;

//you would think this would extend rectF, but it is too different to use that as a base.
public class RectFExtended
{
	// truley this should not be public
	// and should only be accessed by accessors
	// but that would incur too much extra overhead
	public final RectF main_rect;
	
	private double scale_value = 1.0;
	private double x_pos;
	private double y_pos;
	private double x_offset;
	private double y_offset;
	private double half_width;
	private double half_height;
	
	// these are in shader coordinates
	public RectFExtended(double left, double top, double right, double bottom)
	{
		x_offset = (left + right) / 2.0;
		y_offset = (top + bottom) / 2.0;
		
		half_width = (right - left) / 2.0;
		half_height = (top - bottom) / 2.0;
		
		main_rect = new RectF((float) (x_offset - half_width), (float) (y_offset + half_height), (float) (x_offset + half_width), (float) (y_offset - half_height));
	}
	
	// shader coordinates
	// note, this is with offset
	public void setPositionWithOffset(double x, double y)
	{
		x_pos = x;
		y_pos = y;
		
		main_rect.left = (float) (x + x_offset - half_width);
		main_rect.top = (float) (y + y_offset + half_height);
		main_rect.right = (float) (x + x_offset + half_width);
		main_rect.bottom = (float) (y + y_offset - half_height);
	}
	
	// does change the offset
	public void setScale(double scale_value)
	{
		if (scale_value < 0 || scale_value > 1)
			return;
		
		// calculate the new offset
		final double old_scale_value = this.scale_value;
		final double scale_factor = (scale_value / old_scale_value);
		x_offset = x_offset * scale_factor;
		y_offset = y_offset * scale_factor;
		
		//set the width n such
		setHalfWidthHalfHeight(half_width * scale_factor, half_height * scale_factor);
		
		// finally set the global scale value
		this.scale_value = scale_value;
	}
	
	// doesn't change the offset
	public void setHalfWidthHalfHeight(double new_half_width, double new_half_height)
	{
		half_width = new_half_width;
		half_height = new_half_height;
		setPositionWithOffset(x_pos, y_pos);
	}
}
