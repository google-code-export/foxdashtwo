package com.kobaj.opengldrawable.Quad;

//thank you
//http://blog.shayanjaved.com/2011/05/13/android-opengl-es-2-0-render-to-texture/

import android.opengl.GLES20;

import com.kobaj.loader.GLBitmapReader;
import com.kobaj.math.Constants;

public class QuadRenderTo extends QuadGodRay
{
	// RENDER TO TEXTURE VARIABLES
	private int fb = -1;
	
	@Override
	public void onInitialize()
	{
		setupRenderToTexture();
		// now my_texture_data_handle is no longer -1
		
		super.onInitialize();
	}
	
	@Override
	public void setFBODivider(int fbo_divider)
	{
		super.setFBODivider(fbo_divider);
		pushBitmap();
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
	
	@Override
	public void onDrawAmbient(float[] my_vp_matrix, boolean skip_draw_check)
	{
		QuadRenderShell.onDrawQuad(my_vp_matrix, skip_draw_check, Constants.ambient_light, this);
	}
}
