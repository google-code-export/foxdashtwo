package com.kobaj.opengldrawable.Quad;

//thank you
//http://blog.shayanjaved.com/2011/05/13/android-opengl-es-2-0-render-to-texture/

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.kobaj.foxdashtwo.R;
import com.kobaj.loader.GLBitmapReader;
import com.kobaj.math.Functions;
import com.kobaj.openglgraphics.ETC1Extended;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class QuadRenderTo extends Quad
{
	// RENDER TO TEXTURE VARIABLES
	private int fb = -1, depthRb = - 1;
	private int texW;
	private int texH;
	private IntBuffer texBuffer;

	public QuadRenderTo()
	{
		final int orig_tex_w = com.kobaj.math.Constants.width;
		final int orig_tex_h = com.kobaj.math.Constants.height;
		
		texW = com.kobaj.math.Functions.nearestPowerOf2(orig_tex_w);
		texH = com.kobaj.math.Functions.nearestPowerOf2(orig_tex_h);

		final int square = Math.max(texW, texH);
		
		texW = square;
		texH = square;
		
		setupRenderToTexture();
		onCreate(my_texture_data_handle, orig_tex_w, orig_tex_h);
		complexUpdateTexCoords(0, (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square, orig_tex_w, 0, 1), 
							   1.0f - (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square, orig_tex_h, 0, 1), 1);
	}
	
	private void setupRenderToTexture() {
		int[] fb = new int[1];
		int[] depthRb = new int[1];
		
		// generate
		if(this.fb != -1)
			GLES20.glDeleteFramebuffers(1, new int[] {this.fb}, 0);
		GLES20.glGenFramebuffers(1, fb, 0);
		
		if(this.depthRb != -1)
			GLES20.glDeleteRenderbuffers(1, new int[] {this.depthRb}, 0);
		GLES20.glGenRenderbuffers(1, depthRb, 0);
		
		if(this.my_texture_data_handle != -1)
			GLES20.glDeleteTextures(1, new int[] {this.my_texture_data_handle}, 0);
		
		
		//load a compressed texture
		//ETC1Extended my_etc1 = new ETC1Extended();
		//my_alpha_data_handle = my_texture_data_handle = my_etc1.loadETC1(R.raw.overlay);
		
		
		//load a regular texture
		this.my_texture_data_handle = GLBitmapReader.newTextureID();
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.my_texture_data_handle);
		
		// Set all of our texture parameters:
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		
		// Push the bitmap onto the GPU
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, Bitmap.createBitmap(texW, texH, Bitmap.Config.RGB_565), 0);

		
		//load a built compressed texture
		Bitmap temp = Bitmap.createBitmap(texW, texH, Bitmap.Config.RGB_565);
		ETC1Extended my_etc1 = new ETC1Extended();
		my_etc1.loadETC1(temp, texW, texH, false);
		
		
		// create render buffer and bind 16-bit depth buffer
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRb[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, texW, texH);
		
		this.depthRb = depthRb[0];
		this.fb = fb[0];
	}
	
	public boolean beginRenderToTexture()
	{	
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb);
		
		// specify texture as color attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, my_texture_data_handle, 0);		
		
		// attach render buffer as depth buffer
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRb);
		
		//clear
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		// check status
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
			return false;
		
		// draw all the objects
		return true;
	}
	
	public void endRenderToTexture()
	{
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		
		// Same thing, only different texture is bound now
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
	}
}
