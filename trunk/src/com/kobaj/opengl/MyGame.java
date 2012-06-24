package com.kobaj.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.opengl.GLES20;

import com.kobaj.foxdashtwo.R;
import com.kobaj.opengldrawable.DrawFrom;
import com.kobaj.opengldrawable.Quad;
import com.kobaj.openglgraphics.AmbientLight;
import com.kobaj.openglgraphics.PointLight;
import com.kobaj.math.*;

public class MyGame extends MyGLRender
{
	// test
	Quad quad;
	
	//lights
	PointLight pl_blue;
	PointLight pl_green;
	PointLight pl_red;
	AmbientLight al_test;

	@Override
	void onInitialize(GL10 gl)
	{
		quad = new Quad(gl, R.drawable.titlescreen);
		
		pl_blue = new PointLight(point_light, my_view_matrix);
		pl_blue.x_pos = 0.2;
		pl_blue.y_pos = 0;
		pl_blue.focus = .5;
		pl_blue.color = Color.BLUE;
		
		pl_green = new PointLight(point_light, my_view_matrix);
		pl_green.x_pos = -0.2;
		pl_green.y_pos = 0;
		pl_green.focus = .5;
		pl_green.color = Color.GREEN;
		
		pl_red = new PointLight(point_light, my_view_matrix);
		pl_red.x_pos = 0.0;
		pl_red.y_pos = 0.2;
		pl_red.focus = .5;
		pl_red.color = Color.RED;
		
		al_test = new AmbientLight(ambient_light, my_view_matrix);
	}

	@Override
	public void onUpdate(double delta)
	{

	}
	
	@Override
	void onDraw()
	{
		//ambient first
		GLES20.glUseProgram(ambient_light.my_shader);
		
		
		
		//set the light
		al_test.applyShaderProperties();
		
		//draw pretty!
		//quad.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		
		
		
		// Add program to OpenGL environment
		GLES20.glUseProgram(point_light.my_shader);
		
		//set the light
		pl_blue.applyShaderProperties();
		//draw stuffs.
		quad.onDrawPoint(my_view_matrix, my_proj_matrix, point_light);
		
		//set the light
		pl_green.applyShaderProperties();
		//draw stuffs.
		quad.onDrawPoint(my_view_matrix, my_proj_matrix, point_light);
		
		//set the light
		pl_red.applyShaderProperties();
		//draw stuffs.
		quad.onDrawPoint(my_view_matrix, my_proj_matrix, point_light);
		
	
		
		//see if text works.
		//text.DrawText(R.string.hello, 0, 0, DrawFrom.bottom_right);
		text.DrawNumber(fps.getFPS(), Functions.screenXToShaderX(25), Functions.screenYToShaderY((int)Functions.fix_y(25)), DrawFrom.top_left);
	}	
}
