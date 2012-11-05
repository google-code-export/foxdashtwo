package com.kobaj.opengl;

import android.graphics.Color;
import android.opengl.GLES20;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadRenderTo;
import com.kobaj.screen.BaseScreen;
import com.kobaj.screen.BlankScreen;
import com.kobaj.screen.EnumScreenState;

public class MyGame extends MyGLRender
{	
	//test screen
	//private SinglePlayerScreen single_player_screen;
	private BaseScreen currently_active_screen;
	private BaseScreen next_active_screen;
	
	public boolean draw_fps= true;
	
	//dont touch the variables below this line
	//final drawable.
	private QuadRenderTo scene;

    public MyGame()
    {
    	//single_player_screen = new SinglePlayerScreen();
    	currently_active_screen = new BlankScreen(); //single_player_screen;
    }
    
    
    public void onChangeScreen(BaseScreen next_active_screen)
    {
		if(next_active_screen != null)
			this.next_active_screen = next_active_screen;
    }
	
	//for the record this is called everytime the screen is reset/paused/resumed
	//all graphics are destroyed (dunno about sounds >.>).
	@Override
	protected void onInitialize()
	{
		//begin by aligning our functions
		Functions.adjustConstantsToScreen();
		
		currently_active_screen.onInitialize();
		
		//dont touch below this line.
		if(scene == null)
			scene = new QuadRenderTo();
		scene.onInitialize();
        
        System.gc();
	}
	
	@Override
	protected void onUpdate(double delta)
	{		
		//screen swap
		if(next_active_screen != null)
		{
			currently_active_screen = next_active_screen;
			next_active_screen = null;
			currently_active_screen.onInitialize();
		}
		
		//update as usual
		if(currently_active_screen.current_state == EnumScreenState.running)
			currently_active_screen.onUpdate(delta);
	}
	
	@Override
	protected void onDraw()
	{	
		if(currently_active_screen.current_state == EnumScreenState.running)
			onRunningDraw();
		else if(currently_active_screen.current_state == EnumScreenState.loading)
			onLoadingDraw();
		
		if(draw_fps)
		{
		//fps
			int color = Color.BLUE;
			if(fps.fps < 60)
				color = Color.GREEN;
			if(fps.fps < 45)
				color = Color.YELLOW;
			if(fps.fps < 30)
				color = Color.RED;
		
			Constants.text.drawNumber(fps.fps, Functions.screenXToShaderX(25), Functions.screenYToShaderY((int)Functions.fix_y(25)), EnumDrawFrom.top_left, color);
		}
	}
	
	private void onLoadingDraw()
	{
		currently_active_screen.onDrawLoading(fps.getDelta());
	}
	
	private void onRunningDraw()
	{	
		//regular objects
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		if(scene.beginRenderToTexture())
		{	
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			
			//put opaque items here
			currently_active_screen.onDrawObject();
			
			GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		}
		scene.endRenderToTexture();
	
		//lights
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_SRC_ALPHA); // cheap lights

		//put translucent (lights) here
		currently_active_screen.onDrawLight();
		
		//final scene
		GLES20.glBlendFunc(GLES20.GL_DST_COLOR, GLES20.GL_ZERO); // masking
		scene.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Color.WHITE, true);
		
		//text below this line
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		currently_active_screen.onDrawConstant();	
	}

	@Override
	protected void onPause()
	{
		
	}
}