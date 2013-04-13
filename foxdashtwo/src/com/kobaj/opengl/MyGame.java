package com.kobaj.opengl;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.kobaj.foxdashtwo.R;
import com.kobaj.level.EnumLayerTypes;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadRenderTo;
import com.kobaj.opengldrawable.Quad.QuadShadow;
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
	private QuadRenderTo lights;
	private QuadRenderTo backgroup;
	private QuadRenderTo foregroup;
	
	private float[] my_local_projection = new float[16];
	private float[] my_local_ip_matrix = new float[16];
	
	private QuadShadow shadow_generator;
	
	private final EnumLayerTypes[] backgroup_enums = { EnumLayerTypes.Background, EnumLayerTypes.Background_Aux };
	private final EnumLayerTypes[] interaction_group_enums = { EnumLayerTypes.Post_interaction, EnumLayerTypes.Interaction, EnumLayerTypes.Pre_interaction };
	private final EnumLayerTypes[] foregroup_enums = { EnumLayerTypes.Foreground_Aux, EnumLayerTypes.Foreground, EnumLayerTypes.Top };
	
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
		
		if (lights == null)
			lights = new QuadRenderTo();
		lights.onInitialize();
		
		if (backgroup == null)
			backgroup = new QuadRenderTo();
		backgroup.onInitialize();
		
		if (foregroup == null)
			foregroup = new QuadRenderTo();
		foregroup.onInitialize();
		
		// change the camera
		Matrix.orthoM(my_local_projection, 0, (float) -Constants.ratio, (float) Constants.ratio, -1, 1, .9999999999f, 2);
		Matrix.multiplyMM(my_local_ip_matrix, 0, my_local_projection, 0, Constants.identity_matrix, 0);
		
		if (shadow_generator == null)
			shadow_generator = new QuadShadow();
		shadow_generator.my_texture_data_handle = scene.my_texture_data_handle;
		shadow_generator.my_light_data_handle = lights.my_texture_data_handle;
		shadow_generator.my_backgroup_data_handle = backgroup.my_texture_data_handle;
		shadow_generator.my_foregroup_data_handle = foregroup.my_texture_data_handle;
		
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
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_SRC_ALPHA); // put translucent and cheap (lights) here
		if (lights.beginRenderToTexture(true))
			currently_active_screen.onDrawLight();
		
		// regular objects
		//GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		
		if (scene.beginRenderToTexture(true))
			currently_active_screen.onDrawObject(interaction_group_enums);
		
		if (backgroup.beginRenderToTexture(true))
			currently_active_screen.onDrawObject(backgroup_enums);
		
		if (foregroup.beginRenderToTexture(true))
			currently_active_screen.onDrawObject(foregroup_enums);
		foregroup.endRenderToTexture(true);
		
		// draw everything
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ZERO);
		shadow_generator.shadow_radius = (float) currently_active_screen.player_stats[2];
		shadow_generator.shadow_x_pos = (float) currently_active_screen.player_stats[0];
		shadow_generator.shadow_y_pos = (float) currently_active_screen.player_stats[1];
		shadow_generator.onDrawAmbient(my_local_ip_matrix, true);
		
		//debugging
		//scene.onDrawAmbient(my_local_ip_matrix, true);
		
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
		y_pos = Functions.screenYToShaderY((int) Functions.fix_y(125));
		
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
