package com.kobaj.opengldrawable.Quad;

import com.kobaj.math.Constants;

public class QuadShadow extends QuadGodRay
{	
	//pixel coordinates
	public float radius = 100;
	public float x_pos = 100;
	public float y_pos = 100;
	
	public int my_light_data_handle;
	
	@Override
	public void onDrawAmbient(float[] my_vp_matrix, boolean skip_draw_check)
	{
		QuadRenderShell.onDrawQuad(my_vp_matrix, skip_draw_check, Constants.shadow_light, this);
	}
}
