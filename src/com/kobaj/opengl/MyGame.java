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
		// test quad
		quad = new Quad(gl, R.drawable.titlescreen);
	}

	@Override
	void onDraw()
	{
		// Add program to OpenGL environment
				GLES20.glUseProgram(point_light.my_shader);
			
				//draw stuffs.
				quad.onDrawPoint(my_view_matrix, my_proj_matrix, point_light);
	}

	@Override
	public void onUpdate()
	{
		point_light.onUpdateFrame(0, my_view_matrix);
		
	}
	
}
