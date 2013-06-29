package com.kobaj.opengldrawable.Quad;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;

public class QuadGodRay extends Quad
{
	protected int local_fbo_divider = 1;
	
	// screen coordinates
	public QuadGodRay()
	{
		this.width = Constants.device_width;
		this.height = Constants.device_height;

		computeSquares();
	}
	
	public void setFBODivider(int fbo_divider)
	{
		if (fbo_divider < 0)
			fbo_divider = 1;
		local_fbo_divider = Functions.nearestPowerOf2(fbo_divider);
		
		computeSquares();
	}
	
	protected void calcCorrectedTexCoords()
	{
		// dividing this by scale_factor gets the same result...
		complexUpdateTexCoords(0, (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square_width, this.width, 0, 1),
				1.0f - (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square_height, this.height, 0, 1), 1);
	}
	
	protected void computeSquares()
	{
		// must precompute the square (Even though its computed in the oncreate).
		square_width = Functions.nearestPowerOf2(this.width) / local_fbo_divider;
		square_height = Functions.nearestPowerOf2(this.height) / local_fbo_divider;
	}
	
	public void onInitialize()
	{
		onCreate(1, this.width, this.height);
		
		calcCorrectedTexCoords();
	}
	
	@Override
	public void onDrawAmbient(float[] my_vp_matrix, boolean skip_draw_check)
	{
		QuadRenderShell.onDrawQuad(my_vp_matrix, skip_draw_check, Constants.god_ray_light, this);
	}
	
	@Override
	public void setWidthHeight(int width, int height)
	{
		// store these for our bounding rectangle
		this.width = width;
		this.height = height;
		
		// Define points for a cube.
		this.shader_width = Functions.linearInterpolateUnclamped(0, Constants.device_width, width, 0, Constants.device_ratio * 2.0);
		this.shader_height = Functions.linearInterpolateUnclamped(0, Constants.device_height, height, 0, Constants.shader_height);
		
		float pos_tr_x = (float) (this.shader_width / 2.0);
		float pos_tr_y = (float) (this.shader_height / 2.0f);
		
		float neg_tr_x = -pos_tr_x;
		float neg_tr_y = -pos_tr_y;
		
		final float z_buffer = 0.0f;
		
		// X, Y, Z
		my_position_matrix[0] = neg_tr_x;
		my_position_matrix[1] = pos_tr_y;
		my_position_matrix[2] = z_buffer;
		
		my_position_matrix[3] = neg_tr_x;
		my_position_matrix[4] = neg_tr_y;
		my_position_matrix[5] = z_buffer;
		
		my_position_matrix[6] = pos_tr_x;
		my_position_matrix[7] = pos_tr_y;
		my_position_matrix[8] = z_buffer;
		
		my_position_matrix[9] = neg_tr_x;
		my_position_matrix[10] = neg_tr_y;
		my_position_matrix[11] = z_buffer;
		
		my_position_matrix[12] = pos_tr_x;
		my_position_matrix[13] = neg_tr_y;
		my_position_matrix[14] = z_buffer;
		
		my_position_matrix[15] = pos_tr_x;
		my_position_matrix[16] = pos_tr_y;
		my_position_matrix[17] = z_buffer;
		
		// Initialize the buffers. and store the new coords
		if (my_position == null)
			my_position = ByteBuffer.allocateDirect(my_position_matrix.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		else
			my_position.clear();
		
		my_position.put(my_position_matrix).position(0);
		
		update_position_matrix(true);
	}
}
