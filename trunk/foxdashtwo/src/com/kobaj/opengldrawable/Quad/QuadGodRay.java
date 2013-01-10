package com.kobaj.opengldrawable.Quad;

import com.kobaj.math.Constants;

public class QuadGodRay extends Quad
{	
	// screen coordinates
	public QuadGodRay()
	{
		this.width = Constants.width;
		this.height = Constants.height;
		
		square_width = com.kobaj.math.Functions.nearestPowerOf2(this.width);
		square_height = com.kobaj.math.Functions.nearestPowerOf2(this.height);
		
		onCreate(1, this.width, this.height);
		complexUpdateTexCoords(0, (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square_width, this.width, 0, 1),
				1.0f - (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square_height, this.height, 0, 1), 1);
	}
	
	@Override
	public void onDrawAmbient(float[] my_vp_matrix, boolean skip_draw_check)
	{
		QuadRenderShell.onDrawQuad(my_vp_matrix, skip_draw_check, Constants.god_ray_light, this);
	}
}
