package com.kobaj.screen;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.account_settings.UserSettings;
import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.input.EnumKeyCodes;
import com.kobaj.input.GameInputModifier;
import com.kobaj.level.EnumLayerTypes;
import com.kobaj.loader.FileHandler;
import com.kobaj.loader.GLBitmapReader;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.screen.screenaddons.BaseInteractionPhysics;
import com.kobaj.screen.screenaddons.BaseLoadingScreen;
import com.kobaj.screen.screenaddons.LevelDebugScreen;
import com.kobaj.screen.screenaddons.floatingframe.BasePauseScreen;

public class SinglePlayerScreen extends BaseScreen
{
	// handling death
	public enum EnumDeathStages
	{
		alive, kill, fade_to_black, dead, fade_to_color
	};
	
	public EnumDeathStages current_death_stage = EnumDeathStages.alive;
	
	// modification of input
	private GameInputModifier my_modifier;
	
	// test level
	private com.kobaj.level.Level the_level;
	
	// addons
	private LevelDebugScreen debug_addon;
	private BaseLoadingScreen loading_addon;
	private BaseInteractionPhysics interaction_addon;
	private BasePauseScreen pause_addon;
	
	// wheather to fade and what to fade to.
	public boolean fade_in = true;
	public boolean fade_out = false;
	
	private int below_xfps_count = 300;
	private double fps_limit = 1.0 / 20.0 * 1000.0;
	
	public SinglePlayerScreen()
	{
		// initialize everything
		my_modifier = new GameInputModifier();
		loading_addon = new BaseLoadingScreen();
		interaction_addon = new BaseInteractionPhysics(); // has no quads
	}
	
	// loads the next level and returns true if successful.
	private boolean setNextLevel(String level_name)
	{
		if (level_name != null)
			level_name = level_name.trim();
		
		if (level_name == null || level_name.equals(Constants.empty))
		{
			//change the first level
			the_level = FileHandler.readSerialResource(Constants.resources, R.raw.snow_test, com.kobaj.level.Level.class);
			return true;
		}
		
		// if not then try to load from R
		int level_R = Constants.resources.getIdentifier(level_name, "raw", "com.kobaj.foxdashtwo");
		if (level_R != 0)
		{
			the_level = FileHandler.readSerialResource(Constants.resources, level_R, com.kobaj.level.Level.class);
			if (the_level != null)
				return true;
		}
		
		// first see if it is a physical level on disk
		if (FileHandler.fileExists(level_name))
		{
			the_level = FileHandler.readSerialFile(level_name, com.kobaj.level.Level.class);
			if (the_level != null)
				return true;
		}
		
		return false;
	}
	
	@Override
	public void onLoad()
	{
		// load our addons. Do the loader first
		loading_addon.onInitialize();
		
		this.setNextLevel(SinglePlayerSave.last_level);
		
		if (the_level != null)
			the_level.onInitialize();
		else
		{
			TitleScreen crash = new TitleScreen();
			crash.crashed = true;
			GameActivity.mGLView.my_game.onChangeScreen(crash);
		}
		
		// control input and other addons
		my_modifier.onInitialize();
		
		pause_addon = new BasePauseScreen();
		pause_addon.onInitialize();
		
		// testing sounds
		Constants.music_player.start(R.raw.tunnel, 5000, true);
		while (!Constants.music_player.isLoaded())
		{
			try
			{
				Thread.sleep(Constants.exception_timeout);
			}
			catch (InterruptedException e)
			{
				Log.e("Single Player Exception", e.toString());
			}
		}
		
		// debug_addon = new LevelDebugScreen(the_level, EnumDebugType.events);
		
		GLBitmapReader.isLoaded();
		
		System.gc();
	}
	
	@Override
	public void onUnload()
	{
		my_modifier.onUnInitialize();
		loading_addon.onUnInitialize();
		pause_addon.onUnInitialize();
		
		if (the_level != null)
			the_level.onUnInitialize();
	}
	
	@Override
	public void onUpdate(double delta)
	{
		// that music
		Constants.music_player.onUpdate();
		
		if (current_state != EnumScreenState.paused)
			onRunningUpdate(delta);
		else if (!pause_addon.onUpdate(delta))
			current_state = EnumScreenState.running;
		
		// go into pause mode by hitting menu, search, back...
		if (Constants.input_manager.getKeyPressed(EnumKeyCodes.back) || //
				Constants.input_manager.getKeyPressed(EnumKeyCodes.menu) || //
				Constants.input_manager.getKeyPressed(EnumKeyCodes.search)) //
		{
			// this is possible because onUpdate is only called when in two states, running or paused
			if (current_state != EnumScreenState.paused)
			{
				pause_addon.reset();
				current_state = EnumScreenState.paused;
			}
			
			// MyGLRender.slowmo = !MyGLRender.slowmo;
		}
		
		//handle low fps situations
		if(delta > this.fps_limit && UserSettings.fbo_divider < 2 && !UserSettings.fbo_warned)
		{
			this.below_xfps_count--;
			if(below_xfps_count == 0)
			{
				UserSettings.fbo_warned = true;
				
				pause_addon.reset();
				pause_addon.low_fps_detected = true;
				current_state = EnumScreenState.paused;
			}
		}
	}
	
	private void onRunningUpdate(double delta)
	{
		// update all our objects and lights and things
		the_level.onUpdate(delta);
		
		// interaction and player
		interaction_addon.onUpdate(delta, my_modifier, the_level);
		
		getPlayerPosition();
		
		// regardless we fade in
		if (fade_in)
		{
			fade_in = tween_fade_in.onUpdate(delta);
			if (fade_in == false)
				tween_fade_in.reset();
		}
		
		if (fade_out)
		{
			fade_out = tween_fade_out.onUpdate(delta);
			if (fade_out == false)
				tween_fade_out.reset();
		}
		
		// handle death events
		if (this.current_death_stage == EnumDeathStages.alive)
		{
			if (the_level.kill)
				this.current_death_stage = EnumDeathStages.kill;
		}
		else if (this.current_death_stage == EnumDeathStages.kill)
		{
			this.current_death_stage = EnumDeathStages.fade_to_black;
			fade_out = true;
		}
		else if (this.current_death_stage == EnumDeathStages.fade_to_black && fade_out == false)
			this.current_death_stage = EnumDeathStages.dead;
		else if (this.current_death_stage == EnumDeathStages.dead)
		{
			the_level.deadReset();
			this.current_death_stage = EnumDeathStages.fade_to_color;
			fade_in = true;
		}
		else if (this.current_death_stage == EnumDeathStages.fade_to_color && fade_in == false)
			this.current_death_stage = EnumDeathStages.alive;
		
		// debug_addon.onUpdate(delta, the_level);
	}
	
	private void getPlayerPosition()
	{
		// radius
		double radius = Constants.ratio * Constants.shadow_radius;
		radius = radius - Constants.z_shader_translation * 100.0;
		this.player_stats[2] = radius;
		
		// first do x
		double player_x = the_level.player.quad_object.x_pos_shader;
		double screen_x = Constants.x_shader_translation;
		double shift_x = Functions.shaderXToScreenX((player_x - screen_x)); // will be zero if in the middle of the screen.
		
		this.player_stats[0] = shift_x;
		this.player_stats[1] = interaction_addon.player_shadow_y;// +
		
		// finally recalculate radius to account for distance between fox and ground
		double distance = Functions.distanceSquared(0, interaction_addon.player_extended.top, 0, interaction_addon.player_shadow_scale);
		double multiplier = Functions.linearInterpolate(0, Constants.shadow_height_shader, distance, 1, 0) + .01;
		this.player_stats[2] *= multiplier;
	}
	
	@Override
	public void onDrawObject(EnumLayerTypes... types)
	{
		the_level.onDrawObject(types);
		
		// debug_addon.onDrawObject();
	}
	
	@Override
	public void onDrawLight()
	{
		the_level.onDrawLight();
	}
	
	@SuppressLint("WrongCall")
	@Override
	public void onDrawConstant()
	{
		the_level.onDrawConstant();
		
		// cover everything up
		if (fade_in || fade_out) // yes this is correct
			black_overlay_fade.onDrawAmbient(Constants.my_ip_matrix, true);
		
		if (this.current_death_stage == EnumDeathStages.dead)
		{
			color_overlay.color = Color.BLACK;
			color_overlay.onDrawAmbient(Constants.my_ip_matrix, true);
		}
		
		// draw the controls
		if (current_state != EnumScreenState.paused)
			my_modifier.onDraw();
		else
			pause_addon.onDraw();
	}
	
	@Override
	public void onDrawLoading(double delta)
	{
		// we want all loading screens to look the same, so we use this helper loader thingy :)
		if (loading_addon != null)
			loading_addon.onDrawLoading(delta);
	}
	
	@Override
	public void onPause()
	{
		// only on game screens to we send the system into paused state.
		current_state = EnumScreenState.paused;
	}
}
