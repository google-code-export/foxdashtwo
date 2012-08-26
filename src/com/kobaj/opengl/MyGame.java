package com.kobaj.opengl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.QuadRenderTo;
import com.kobaj.screen.BaseScreen;
import com.kobaj.screen.SinglePlayerScreen;

public class MyGame extends MyGLRender
{	
	//test screen
	SinglePlayerScreen single_player_screen;
	BaseScreen currently_active_screen;
	
	//dont touch the stuff below this line
	//final drawable.
	private QuadRenderTo scene;

    public MyGame()
    {
    	single_player_screen = new SinglePlayerScreen();
    	currently_active_screen = single_player_screen;
    }
    
	@Override
	void onInitialize()
	{
		//for the record this is called everytime the screen is reset/paused/resumed
		//all graphics are destroyed (dunno about sounds >.>).
		
		currently_active_screen.onInitialize();
		
		//dont touch below this line.
        scene = new QuadRenderTo();
        
        System.gc();
	}
	
	@Override
	public void onUpdate(double delta)
	{
		currently_active_screen.onUpdate(delta);
	}
	
	@Override
	void onDraw()
	{	
		GLES20.glUseProgram(ambient_light.my_shader);
		
		//regular objects
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		if(scene.beginRenderToTexture())
		{
			/* It is recommended that the screen calls something similar to the following to functions
			 * al_ambient_light.applyShaderProperties();
			 */
			
			//put opaque items here
			currently_active_screen.onDrawObject();
		}
		scene.endRenderToTexture();
	
		//lights
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_SRC_ALPHA); // cheap lights

		//put translucent (lights) here
		currently_active_screen.onDrawLight();
		
		//reset the camera so the following is drawn correctly
		Matrix.setIdentityM(my_view_matrix, 0);
		
		//final scene
		GLES20.glBlendFunc(GLES20.GL_DST_COLOR, GLES20.GL_ZERO); // masking
		scene.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light, true);
		
		//text below this line
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		text.DrawNumber(fps.fps, Functions.screenXToShaderX(25), Functions.screenYToShaderY((int)Functions.fix_y(25)), EnumDrawFrom.top_left);
		currently_active_screen.onDrawText();
	
		//move the camera back
		Matrix.translateM(Constants.my_view_matrix, 0, (float) Constants.x_shader_translation, (float) Constants.y_shader_translation, 0);
	}

	@Override
	void onPause()
	{
		
	}
}