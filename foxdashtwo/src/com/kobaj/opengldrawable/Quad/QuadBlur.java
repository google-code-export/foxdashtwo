package com.kobaj.opengldrawable.Quad;

import com.kobaj.math.Constants;

public class QuadBlur extends QuadGodRay
{
	private final double blur_offset = 1.0;
	public double x_blur_offset = 0.0;
	public double y_blur_offset = 0.0;
	
	@Override
	public void onDrawAmbient(float[] my_vp_matrix, boolean skip_draw_check)
	{
		QuadRenderShell.onDrawQuad(my_vp_matrix, skip_draw_check, Constants.blur_light, this);
	}
	
	public void setHorizontalBlur()
	{
		y_blur_offset = 0;
		x_blur_offset = blur_offset / (double) square_width;
	}
	
	public void setVerticalBlur()
	{
		y_blur_offset = blur_offset / (double) square_height;
		x_blur_offset = 0;
	}
	
	public void setFullBlur()
	{
		x_blur_offset = blur_offset / (double) square_width;
		y_blur_offset = blur_offset / (double) square_height;
	}
}
