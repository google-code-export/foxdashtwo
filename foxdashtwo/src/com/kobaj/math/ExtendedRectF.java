package com.kobaj.math;

import com.kobaj.math.android.RectF;

//you would think this would extend rectF, but it is too different to use that as a base.
public class ExtendedRectF
{
	//truley this should not be public
	//and should only be accessed by accessors
	//but that would incur too much extra overhead
	public RectF main_rect;
	
	public final double x_offset;
	public final double y_offset;
	private final double half_width;
	private final double half_height;
	
	// these are in shader coordinates
	public ExtendedRectF(double left, double top, double right, double bottom)
	{
		x_offset = (left + right) / 2.0;
		y_offset = (top + bottom) / 2.0;
		
		half_width = (right - left) / 2.0;
		half_height = (top - bottom) / 2.0;
		
		main_rect = new RectF((float) (x_offset - half_width), (float) (y_offset + half_height),
				              (float) (x_offset + half_width), (float) (y_offset - half_height));
	}
	
	// shader coordinates
	// note, this is with offset
	public void setPositionWithOffset(double x, double y)
	{
		main_rect.left = (float) (x + x_offset - half_width);
		main_rect.top = (float) (y + y_offset + half_height);
		main_rect.right = (float) (x + x_offset + half_width);
		main_rect.bottom = (float) (y + y_offset - half_height);
	}
}
