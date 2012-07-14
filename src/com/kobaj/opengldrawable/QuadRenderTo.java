package com.kobaj.opengldrawable;

//thank you
//http://blog.shayanjaved.com/2011/05/13/android-opengl-es-2-0-render-to-texture/

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import android.opengl.GLES20;

public class QuadRenderTo extends Quad
{
	// RENDER TO TEXTURE VARIABLES
	int[] fb, depthRb, renderTex;
	int texW = 800;
	int texH = 480;
	IntBuffer texBuffer;

	public QuadRenderTo()
	{
		setupRenderToTexture();
		onCreate(renderTex[0], texW, texH);
	}
	
	private void setupRenderToTexture() {
		fb = new int[1];
		depthRb = new int[1];
		renderTex = new int[1];
		
		// generate
		GLES20.glGenFramebuffers(1, fb, 0);
		GLES20.glGenRenderbuffers(1, depthRb, 0);
		GLES20.glGenTextures(1, renderTex, 0);
		
		// generate color texture
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTex[0]);

		// Set all of our texture parameters:
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

		// create it 
		// create an empty intbuffer first?
		int[] buf = new int[texW * texH];
		texBuffer = ByteBuffer.allocateDirect(buf.length* 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, texW, texH, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, texBuffer);
		
		// create render buffer and bind 16-bit depth buffer
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRb[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, texW, texH);
		
		this.my_texture_data_handle = this.renderTex[0];
	}
	
	public boolean beginRenderToTexture()
	{
		//GLES20.glViewport(0, 0, this.texW, this.texH);
		
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);
		
		// specify texture as color attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, renderTex[0], 0);
		//GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, renderTex[0], 0);
		
		// attach render buffer as depth buffer
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRb[0]);
		//GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, depthRb[0]);
		
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
		
		GLES20.glClearColor(.0f, .0f, .0f, 1.0f);
		GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
	}
}
