package com.kobaj.opengldrawable.Quad;

import com.kobaj.opengldrawable.Quad.Quad;

public class QuadEmpty extends Quad
{
	public QuadEmpty(int width, int height)
	{
		onCreate(Integer.MIN_VALUE, width, height);
	}
	
	@Override
	public void onDrawAmbient(float[] my_vp_matrix, boolean skip_draw_check)
	{
		return;
	}
}
