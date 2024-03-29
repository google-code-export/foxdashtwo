package com.kobaj.opengl;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.kobaj.account_settings.UserSettings;
import com.kobaj.foxdashtwo.R;
import com.kobaj.level.EnumLayerTypes;
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
	
	private BaseScreen pre_next_screen;
	
	private float[] my_local_projection = new float[16];
	private float[] my_local_ip_matrix = new float[16];
	
	private float[] used_matrix;
	
	// dont touch the variables below this line
	// final drawable.
	private QuadRenderTo scene;
	private QuadRenderTo lights;
	
	private final int detection_x_default = 75;
	private final int detection_y_default = 75;
	
	private final EnumLayerTypes[] backgroup_enums = { EnumLayerTypes.Background, EnumLayerTypes.Background_Aux, EnumLayerTypes.Post_interaction };
	private final EnumLayerTypes[] interaction_group_enums = { EnumLayerTypes.Interaction };
	private final EnumLayerTypes[] foregroup_enums = { EnumLayerTypes.Pre_interaction, EnumLayerTypes.Foreground_Aux, EnumLayerTypes.Foreground, EnumLayerTypes.Top };
	private final EnumLayerTypes[] shadow_enums = { EnumLayerTypes.Shadow };
	
	private int left_corner_count = 0;
	
	public MyGame()
	{
		currently_active_screen = new BlankScreen(); // single_player_screen;
	}
	
	// make the screen fade
	public void onPreChangeScreen(BaseScreen new_screen, boolean next_level)
	{
		pre_next_screen = new_screen;
		
		currently_active_screen.onScreenChange(next_level);
	}
	
	// commit a pre change screen
	public void onCommitChangeScreen()
	{
		if (pre_next_screen != null)
			this.next_active_screen = pre_next_screen;
	}
	
	// change screen without fade
	public void onChangeScreen(BaseScreen new_screen)
	{
		if (new_screen != null)
			this.next_active_screen = new_screen;
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
		
		// change camera
		Matrix.orthoM(my_local_projection, 0, (float) -Constants.device_ratio, (float) Constants.device_ratio, -1, 1, .9999999999f, 2);
		Matrix.multiplyMM(my_local_ip_matrix, 0, my_local_projection, 0, Constants.identity_matrix, 0);
		
		scene = QuadRendersHandler(scene);
		lights = QuadRendersHandler(lights);
		
		reInitializeQuadRenders();
		
		System.gc();
	}
	
	public void reInitializeQuadRenders()
	{
		scene.setFBODivider(UserSettings.fbo_divider);
		lights.setFBODivider(UserSettings.fbo_divider);
	}
	
	private QuadRenderTo QuadRendersHandler(QuadRenderTo scene)
	{
		if (scene != null)
		{
			scene.onUnInitialize();
			scene = null;
		}
		
		scene = new QuadRenderTo();
		scene.onInitialize();
		
		return scene;
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
		
		// super hacks to change settings
		if (Constants.input_manager.getPressed(0))
		{
			if (Constants.input_manager.getX(0) < detection_x_default && Constants.input_manager.getY(0) < detection_y_default)
			{
				left_corner_count++;
				
				if (left_corner_count > 4)
				{
					left_corner_count = 0;
					
					// and very simply get the next debug mode
					if (UserSettings.my_debug_mode == UserSettings.DebugMode.none)
						UserSettings.my_debug_mode = UserSettings.DebugMode.fps;
					else if (UserSettings.my_debug_mode == UserSettings.DebugMode.fps)
						UserSettings.my_debug_mode = UserSettings.DebugMode.detailed;
					else if (UserSettings.my_debug_mode == UserSettings.DebugMode.detailed)
						UserSettings.my_debug_mode = UserSettings.DebugMode.none;
				}
			}
		}
		
		// input manager loop
		Constants.input_manager.onUpdate(delta);
	}
	
	@Override
	protected void onDraw()
	{
		if (currently_active_screen.current_state == EnumScreenState.running || currently_active_screen.current_state == EnumScreenState.paused)
			onRunningDraw();
		else if (currently_active_screen.current_state == EnumScreenState.loading)
			onLoadingDraw();
		
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
		GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);	
		
		if (scene.beginRenderToTexture(true))
		{
			currently_active_screen.onDrawObject(interaction_group_enums);
			
			GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ZERO, GLES20.GL_ONE);
			currently_active_screen.onDrawObject(shadow_enums);
			
			GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);
			currently_active_screen.onDrawObject(foregroup_enums);
		}
		
		scene.endRenderToTexture(true);
		
		// draw everything 
		
		if (Constants.horizontal_ratio)
			used_matrix = my_local_ip_matrix;
		else
			used_matrix = Constants.my_ip_matrix;
		
		GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);
		currently_active_screen.onDrawObject(backgroup_enums);
		
		GLES20.glBlendFuncSeparate(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);
		scene.onDrawAmbient(used_matrix, true);
		
		GLES20.glBlendFuncSeparate(GLES20.GL_DST_COLOR, GLES20.GL_ZERO, GLES20.GL_DST_ALPHA, GLES20.GL_ZERO);
		lights.onDrawAmbient(used_matrix, true);
		
		// text below this line
		GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);
		currently_active_screen.onDrawConstant();
	}
	
	private void onDrawMetrics()
	{
		double x_pos = 0;
		double y_pos = 0;
		
		// fps
		if (UserSettings.my_debug_mode == UserSettings.DebugMode.fps //
				|| UserSettings.my_debug_mode == UserSettings.DebugMode.detailed)
		{
			int fps_color = Color.BLUE;
			if (fps.fps < 60)
				fps_color = Color.GREEN;
			if (fps.fps < 45)
				fps_color = Color.YELLOW;
			if (fps.fps < 30)
				fps_color = Color.RED;
			
			// fps metrics
			x_pos = Constants.x_100;
			y_pos = Constants.y_50;
			
			Constants.text.drawText(R.string.fps, x_pos, y_pos, EnumDrawFrom.bottom_right);
			Constants.text.drawIntNumber(fps.fps, x_pos, y_pos, EnumDrawFrom.bottom_left, fps_color);
		}
		
		if (UserSettings.my_debug_mode == UserSettings.DebugMode.detailed)
		{
			int oos_color = Color.RED;
			if (Constants.quads_drawn_screen < 50)
				oos_color = Color.YELLOW;
			if (Constants.quads_drawn_screen < 35)
				oos_color = Color.GREEN;
			if (Constants.quads_drawn_screen < 20)
				oos_color = Color.BLUE;
			
			Constants.text.drawText(R.string.qos, x_pos, y_pos, EnumDrawFrom.top_right);
			Constants.text.drawIntNumber(Constants.quads_drawn_screen, x_pos, y_pos, EnumDrawFrom.top_left, oos_color);
			
			// music metrics
			y_pos = Constants.y_125;
			
			Constants.text.drawText(R.string.volume, x_pos, y_pos, EnumDrawFrom.bottom_right);
			Constants.text.drawIntNumber((int) (Constants.music_player.actual_volume * 100.0), x_pos, y_pos, EnumDrawFrom.bottom_left);
			
			Constants.text.drawText(R.string.mpos, x_pos, y_pos, EnumDrawFrom.top_right);
			Constants.text.drawIntNumber((int) (Constants.music_player.getCurrentPosition() / 1000.0), x_pos, y_pos, EnumDrawFrom.top_left);
			
			y_pos = Constants.y_200;
			
			// more quad counting
			oos_color = Color.RED;
			if (Constants.quads_coord_map_check < 50)
				oos_color = Color.YELLOW;
			if (Constants.quads_coord_map_check < 35)
				oos_color = Color.GREEN;
			if (Constants.quads_coord_map_check < 20)
				oos_color = Color.BLUE;
			
			Constants.text.drawText(R.string.branch, x_pos, y_pos, EnumDrawFrom.bottom_right);
			Constants.text.drawIntNumber(Constants.quads_coord_map_check, x_pos, y_pos, EnumDrawFrom.bottom_left, oos_color);
			
			// position of player in the map
			y_pos = Constants.y_275;
			Constants.text.drawText(R.string.x, x_pos, y_pos, EnumDrawFrom.bottom_right);
			Constants.text.drawIntNumber((int) Functions.shaderXToScreenX(Constants.x_shader_translation), x_pos, y_pos, EnumDrawFrom.bottom_left);
			
			Constants.text.drawText(R.string.y, x_pos, y_pos, EnumDrawFrom.top_right);
			Constants.text.drawIntNumber((int) Functions.shaderXToScreenX(Constants.y_shader_translation), x_pos, y_pos, EnumDrawFrom.top_left);
			
			// particle info
			y_pos = Constants.y_350;
			Constants.text.drawText(R.string.p_total, x_pos, y_pos, EnumDrawFrom.bottom_right);
			Constants.text.drawIntNumber(Constants.particles_total, x_pos, y_pos, EnumDrawFrom.bottom_left);
			
			Constants.text.drawText(R.string.p_update, x_pos, y_pos, EnumDrawFrom.top_right);
			Constants.text.drawIntNumber(Constants.particles_updating, x_pos, y_pos, EnumDrawFrom.top_left);
		}
		
		// dont forget to clear our statistics
		Constants.quads_drawn_screen = 0;
		Constants.quads_coord_map_check = 0;
		Constants.particles_updating = 0;
		
	}
	
	@Override
	protected void onPause()
	{
		currently_active_screen.onPause();
	}

	@Override
	protected void on100msUpdate()
	{
		currently_active_screen.on100msUpdate();
	}
}
