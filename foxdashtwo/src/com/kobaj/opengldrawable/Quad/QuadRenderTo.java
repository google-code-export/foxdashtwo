package com.kobaj.opengldrawable.Quad;

//thank you
//http://blog.shayanjaved.com/2011/05/13/android-opengl-es-2-0-render-to-texture/

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.opengl.GLES20;

import com.kobaj.loader.GLBitmapReader;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;

public class QuadRenderTo extends Quad
{
	// RENDER TO TEXTURE VARIABLES
	private int fb = -1;
	
	private int local_fbo_divider = 1;
	
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
	
	public void setFBODivider(int fbo_divider)
	{
		if (fbo_divider < 0)
			fbo_divider = 1;
		local_fbo_divider = Functions.nearestPowerOf2(fbo_divider);
		
		computeSquares();
		pushBitmap();
	}
	
	public QuadRenderTo()
	{
		width = com.kobaj.math.Constants.device_width;
		height = com.kobaj.math.Constants.device_height;
		
		computeSquares();
	}
	
	private void computeSquares()
	{
		// must precompute the square (Even though its computed in the oncreate).
		square_width = Functions.nearestPowerOf2(this.width) / local_fbo_divider;
		square_height = Functions.nearestPowerOf2(this.height) / local_fbo_divider;
	}
	
	public void onInitialize()
	{
		setupRenderToTexture();
		// now my_texture_data_handle is no longer -1
		
		onCreate(my_texture_data_handle, this.width, this.height);
		
		// dividing this by scale_factor gets the same result...
		complexUpdateTexCoords(0, (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square_width, this.width, 0, 1),
				1.0f - (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square_height, this.height, 0, 1), 1);
	}
	
	private void setupRenderToTexture()
	{
		int[] fb = new int[1];
		
		// generate
		if (this.fb != -1)
			GLES20.glDeleteFramebuffers(1, new int[] { this.fb }, 0);
		GLES20.glGenFramebuffers(1, fb, 0);
		
		if (this.my_texture_data_handle != -1)
			GLES20.glDeleteTextures(1, new int[] { this.my_texture_data_handle }, 0);
		
		// load a regular texture
		this.my_texture_data_handle = GLBitmapReader.newTextureID();
		
		pushBitmap();
		
		this.fb = fb[0];
		
		// bind our attachments really quick
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);
		
		// specify texture as color attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, my_texture_data_handle, 0);
		
		// reset attachment
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}
	
	private void pushBitmap()
	{
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.my_texture_data_handle);
		
		// Set all of our texture parameters:
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		
		int bitmap_size_x = square_width;
		int bitmap_size_y = square_height;
		
		// Push the bitmap onto the GPU
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap_size_x, bitmap_size_y, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
	}
	
	public boolean beginRenderToTexture(boolean clear)
	{
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb);
		
		if (local_fbo_divider > 1)
			GLES20.glViewport(0, 0, Constants.device_width / local_fbo_divider, Constants.device_height / local_fbo_divider);
		
		// clear
		if (clear)
		{
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		}
		
		// check status
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
			return false;
		
		// draw all the objects
		return true;
	}
	
	public void endRenderToTexture(boolean clear)
	{
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		
		if (local_fbo_divider > 1)
			GLES20.glViewport(0, 0, Constants.device_width, Constants.device_height);
		
		// Same thing, only different texture is bound now
		if (clear)
		{
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		}
	}
}
