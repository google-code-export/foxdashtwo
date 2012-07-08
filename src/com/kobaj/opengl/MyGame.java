package com.kobaj.opengl;

import android.opengl.GLES20;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.QuadAnimated;
import com.kobaj.openglgraphics.AmbientLight;

public class MyGame extends MyGLRender
{
	// test
	QuadAnimated quad;
	
	AmbientLight al_test;

	@Override
	void onInitialize()
	{
		quad = new QuadAnimated(R.drawable.titlescreen, R.raw.test_animation);
		quad.playing = true;
		
		al_test = new AmbientLight(ambient_light, my_view_matrix);
	}

	double add = 100000;
	double delta;
	
	@Override
	public void onUpdate(double delta)
	{
		this.delta = delta;
		
		add += .01f * delta;
		
		quad.onUpdate(delta); // for animation
		
		//quick test
		//TODO grab the initial translation matrix and store it
		//transforming it to the my_view_matrix to make the view change.
		//Matrix.translateM(my_view_matrix, 0, .0005f, .0005f, 0);
		
		
		//testing physics
		physics.apply_physics(delta, quad);
	}
	
	@Override
	void onDraw()
	{
		//ambient first
		GLES20.glUseProgram(ambient_light.my_shader);
		
		
		
		//set the light
		al_test.applyShaderProperties();
		
		//draw pretty!
		quad.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		
		
		
		
		//see if text works.
		//text.DrawText(R.string.hello, 0, 0, DrawFrom.bottom_right);
		text.DrawNumber(fps.fps, Functions.screenXToShaderX(25), Functions.screenYToShaderY((int)Functions.fix_y(25)), EnumDrawFrom.top_left);
		text.DrawNumber((int)add, Functions.screenXToShaderX(25), Functions.screenYToShaderY((int)Functions.fix_y(50)), EnumDrawFrom.top_left);
		text.DrawNumber((int)delta, Functions.screenXToShaderX(25), Functions.screenYToShaderY((int)Functions.fix_y(75)), EnumDrawFrom.top_left);
	}

	@Override
	void onPause()
	{
		
	}
}