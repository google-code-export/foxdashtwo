package com.kobaj.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;

import com.kobaj.foxdashtwo.R;
import com.kobaj.openglgraphics.Quad;

public class MyGame extends MyGLRender
{
	// test
	Quad quad;

	@Override
	void onInitialize(GL10 gl)
	{
		quad = new Quad(gl, R.drawable.titlescreen);
	}

	@Override
	public void onUpdate()
	{
		
	}
	
	@Override
	void onDraw()
	{
		// Add program to OpenGL environment
		GLES20.glUseProgram(point_light.my_shader);
			
		//draw stuffs.
		quad.onDrawPoint(my_view_matrix, my_proj_matrix, point_light);
	}	
}
