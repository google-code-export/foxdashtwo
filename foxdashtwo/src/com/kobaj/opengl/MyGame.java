package com.kobaj.opengl;

import android.graphics.Color;
import android.opengl.GLES20;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadRenderTo;
import com.kobaj.screen.BaseScreen;
import com.kobaj.screen.BlankScreen;
import com.kobaj.screen.EnumScreenState;

public class MyGame extends MyGLRender
{
	// test screen
	// private SinglePlayerScreen single_player_screen;
	private BaseScreen currently_active_screen;
	private BaseScreen next_active_screen;
	
	// dont touch the variables below this line
	// final drawable.
	private QuadRenderTo scene;
	
	public MyGame()
	{
		// single_player_screen = new SinglePlayerScreen();
		currently_active_screen = new BlankScreen(); // single_player_screen;
	}
	
	public void onChangeScreen(BaseScreen next_active_screen)
	{
		this.next_active_screen = next_active_screen;
	}
	
	public void onChangeScreenState(EnumScreenState new_state)
	{
		this.currently_active_screen.current_state = new_state;
	}
	
	// for the record this is called everytime the screen is reset/paused/resumed
	// all graphics are destroyed (dunno about sounds >.>).
	@Override
	protected void onInitialize()
	{
		// begin by aligning our functions
		Functions.adjustConstantsToScreen();
		
		currently_active_screen.onInitialize();
		
		// dont touch below this line.
		if (scene == null)
			scene = new QuadRenderTo();
		scene.onInitialize();

		System.gc();
	}
	
	@Override
	protected void onUpdate(double delta)
	{
		// screen swap
		if (next_active_screen != null)
		{
			// unload
			currently_active_screen.onUnInitialize();
			
			// load next
			currently_active_screen = next_active_screen;
			next_active_screen = null;
			currently_active_screen.onInitialize();
		}
		
		// update as usual
		if (currently_active_screen.current_state == EnumScreenState.running || currently_active_screen.current_state == EnumScreenState.paused)
		{
			currently_active_screen.onUpdate(delta);
		}
	}
	
	@Override
	protected void onDraw()
	{
		if (currently_active_screen.current_state == EnumScreenState.running || currently_active_screen.current_state == EnumScreenState.paused)
			onRunningDraw();
		else if (currently_active_screen.current_state == EnumScreenState.loading)
			onLoadingDraw();
		
		if (Constants.draw_fps)
			onDrawMetrics();
	}
	
	private void onLoadingDraw()
	{
		currently_active_screen.onDrawLoading(fps.getDelta());
	}
	
	private void onRunningDraw()
	{
		// regular objects
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		if (scene.beginRenderToTexture())
		{
			// put opaque items here
			currently_active_screen.onDrawObject();
		}
		scene.endRenderToTexture();
		
		// lights
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_SRC_ALPHA); // cheap lights
		
		// put translucent (lights) here
		currently_active_screen.onDrawLight();
		
		// final scene
		GLES20.glBlendFunc(GLES20.GL_DST_COLOR, GLES20.GL_ZERO); // masking
		scene.onDrawAmbient(Constants.my_ip_matrix, true);
		
		// text below this line
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		currently_active_screen.onDrawConstant();
	}
	
	private void onDrawMetrics()
	{
		// fps
		int fps_color = Color.BLUE;
		if (fps.fps < 60)
			fps_color = Color.GREEN;
		if (fps.fps < 45)
			fps_color = Color.YELLOW;
		if (fps.fps < 30)
			fps_color = Color.RED;
		
		int oos_color = Color.RED;
		if (Constants.quads_drawn_screen < 50)
			oos_color = Color.YELLOW;
		if (Constants.quads_drawn_screen < 35)
			oos_color = Color.GREEN;
		if (Constants.quads_drawn_screen < 20)
			oos_color = Color.BLUE;
		
		// fps metrics
		double x_pos = Functions.screenXToShaderX(100);
		double y_pos = Functions.screenYToShaderY((int) Functions.fix_y(50));
		
		Constants.text.drawText(R.string.fps, x_pos, y_pos, EnumDrawFrom.bottom_right);
		Constants.text.drawNumber(fps.fps, x_pos, y_pos, EnumDrawFrom.bottom_left, fps_color);
		
		Constants.text.drawText(R.string.qos, x_pos, y_pos, EnumDrawFrom.top_right);
		Constants.text.drawNumber(Constants.quads_drawn_screen, x_pos, y_pos, EnumDrawFrom.top_left, oos_color);
		Constants.quads_drawn_screen = 0;
		
		// music metrics
		y_pos = Functions.screenYToShaderY((int) Functions.fix_y(100));
		
		Constants.text.drawText(R.string.volume, x_pos, y_pos, EnumDrawFrom.bottom_right);
		Constants.text.drawNumber((int) (Constants.music_player.actual_volume * 100.0), x_pos, y_pos, EnumDrawFrom.bottom_left);
		
		Constants.text.drawText(R.string.mpos, x_pos, y_pos, EnumDrawFrom.top_right);
		Constants.text.drawNumber((int) (Constants.music_player.getCurrentPosition() / 1000.0), x_pos, y_pos, EnumDrawFrom.top_left);
		
	}
	
	@Override
	protected void onPause()
	{
		currently_active_screen.onPause();
	}
}
