package com.kobaj.opengl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.QuadRenderTo;
import com.kobaj.openglgraphics.AmbientLight;

public class MyGame extends MyGLRender
{	
	//test screen
	
	
	//dont touch the stuff below this line
	//final drawable.
	private QuadRenderTo scene;
	
	//lights
    private AmbientLight al_ambient_light;
    //dont touch stuff above this line
    
    public MyGame()
    {
    	
    }
    
	@Override
	void onInitialize()
	{
		//for the record this is called everytime the screen is reset/paused/resumed
		
		//dont touch below this line.
        al_ambient_light = new AmbientLight(ambient_light, my_view_matrix);
        scene = new QuadRenderTo();
        
        System.gc();
	}
	
	@Override
	public void onUpdate(double delta)
	{
		//quick test
		//TODO grab the initial translation matrix and store it
		//transforming it to the my_view_matrix to make the view change.
		Matrix.translateM(my_view_matrix, 0, .0005f, .0005f, 0);
	}
	
	@Override
	void onDraw()
	{	
		GLES20.glUseProgram(ambient_light.my_shader);
		al_ambient_light.applyShaderProperties();
		
		//regular objects
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		if(scene.beginRenderToTexture())
		{
			//put opaque items here
		}
		scene.endRenderToTexture();
	
		//lights
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_SRC_ALPHA); // cheap lights

		//put transluscent (lights) here
		
		//final scene
		GLES20.glBlendFunc(GLES20.GL_DST_COLOR, GLES20.GL_ZERO); // masking
		scene.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		
		//text below this line
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		text.DrawNumber(fps.fps, Functions.screenXToShaderX(25), Functions.screenYToShaderY((int)Functions.fix_y(25)), EnumDrawFrom.top_left);
	}

	@Override
	void onPause()
	{
		
	}
}