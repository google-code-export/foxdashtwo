package com.kobaj.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;

import com.kobaj.foxdashtwo.R;
import com.kobaj.openglgraphics.PointLight;
import com.kobaj.openglgraphics.Quad;

public class MyGame extends MyGLRender
{
	// test
	Quad quad;
	PointLight pl_test;

	@Override
	void onInitialize(GL10 gl)
	{
		quad = new Quad(gl, R.drawable.titlescreen);
		pl_test = new PointLight(point_light, my_view_matrix);
		pl_test.x_pos = -1;
		pl_test.y_pos = -1;
		
		//quad.x_pos = com.kobaj.math.Functions.screenToShaderX(0);
		//quad.y_pos = com.kobaj.math.Functions.screenToShaderY(0);
	}

	@Override
	public void onUpdate(double delta)
	{
		pl_test.x_pos += .0001 * delta;
		pl_test.y_pos += .0001 * delta;
	}
	
	@Override
	void onDraw()
	{
		// Add program to OpenGL environment
		GLES20.glUseProgram(point_light.my_shader);
		
		//set the light
		pl_test.applyShaderProperties();
			
		//draw stuffs.
		quad.onDrawPoint(my_view_matrix, my_proj_matrix, point_light);
	}	
}
