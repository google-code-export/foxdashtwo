package com.kobaj.screen;

import android.annotation.SuppressLint;
import android.graphics.Color;

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
import com.kobaj.networking.task.TaskSendScore;
import com.kobaj.networking.task.TaskSendScore.FinishedScoring;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Tween.EnumTweenEvent;
import com.kobaj.opengldrawable.Tween.TweenEvent;
import com.kobaj.opengldrawable.Tween.TweenManager;
import com.kobaj.screen.screenaddons.BaseInteractionPhysics;
import com.kobaj.screen.screenaddons.BaseLoadingScreen;
import com.kobaj.screen.screenaddons.LevelDebugScreen;
import com.kobaj.screen.screenaddons.RotationLoadingJig;
import com.kobaj.screen.screenaddons.floatingframe.BasePauseScreen;

public class SinglePlayerScreen extends BaseScreen implements FinishedScoring
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
	
	private boolean level_done = false;
	
	private int below_xfps_count = 300; // number of times to be triggered before throwing the popup
	private double fps_limit = 1.0 / 20.0 * 1000.0;
	
	// dealing with score presentation
	private double max_color_time = 2000;
	private double color_time = 0;
	private double game_time = 0;
	private double prev_best = 0;
	private double world_best = -1;
	
	private String level_name;
	
	private RotationLoadingJig network_loader;
	
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
			// change the first level
			the_level = FileHandler.readSerialResource(Constants.resources, R.raw.test_level, com.kobaj.level.Level.class);
			{
				level_name = "level_one";
				return true;
			}
		}
		
		// if not then try to load from R
		int level_R = Constants.resources.getIdentifier(level_name, "raw", "com.kobaj.foxdashtwo");
		if (level_R != 0)
		{
			the_level = FileHandler.readSerialResource(Constants.resources, level_R, com.kobaj.level.Level.class);
			if (the_level != null)
			{
				this.level_name = level_name;
				return true;
			}
		}
		
		// first see if it is a physical level on disk
		if (FileHandler.fileExists(level_name))
		{
			the_level = FileHandler.readSerialFile(level_name, com.kobaj.level.Level.class);
			if (the_level != null)
			{
				// split the path
				String[] paths = level_name.split("/");
				this.level_name = paths[paths.length - 1]; // last path is file name
				return true;
			}
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
		{
			the_level.onInitialize();
			
			// testing sounds
			the_level.startMusic();
		}
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
		
		// debug_addon = new LevelDebugScreen(the_level, EnumDebugType.events);
		
		network_loader = new RotationLoadingJig();
		network_loader.onInitialize();
		network_loader.radius = Constants.spinning_jig_radius;
		network_loader.x_pos = Constants.two_third_width;
		network_loader.y_pos = Constants.one_fourth_height;
		
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
		
		network_loader.onUnInitialize();
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
		
		// handle low fps situations
		if (delta > this.fps_limit && UserSettings.fbo_divider < 2 && !UserSettings.fbo_warned)
		{
			this.below_xfps_count--;
			if (below_xfps_count == 0)
			{
				UserSettings.fbo_warned = true;
				
				pause_addon.reset();
				pause_addon.low_fps_detected = true;
				current_state = EnumScreenState.paused;
			}
		}
		
		network_loader.onUpdate(delta);
	}
	
	private void onRunningUpdate(double delta)
	{
		// update all our objects and lights and things
		the_level.onUpdate(delta);
		
		if (!level_done)
			game_time += delta;
		
		// interaction and player
		if (!level_done)
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
			if (fade_out == false && !this.level_done)
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
		
		if (level_done && !fade_out)
		{
			color_time += delta;
			
			if (color_time > this.max_color_time)
				color_time = 0;
			
			// level is done and it finished fadeing
			if (Constants.input_manager.getPressed(0))
				GameActivity.mGLView.my_game.onCommitChangeScreen();
		}
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
		if (!level_done || fade_out)
		{
			the_level.onDrawObject(types);
		}
		
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
		if (current_state == EnumScreenState.paused)
			pause_addon.onDraw();
		else if (!level_done)
		{
			my_modifier.onDraw();
			
			Constants.text.drawDecimalNumber(this.game_time / 1000.0, 2, 1, Constants.mini_time_pos_x, Constants.mini_time_pos_y);
		}
		else if (level_done)
		{
			// draw the scores
			Constants.text.drawText(R.string.your_time, Constants.one_third_width, Constants.three_fourth_height, EnumDrawFrom.bottom_right);
			Constants.text.drawText(R.string.best_time, Constants.one_third_width, Constants.two_fourth_height, EnumDrawFrom.bottom_right);
			Constants.text.drawText(R.string.world_time, Constants.one_third_width, Constants.one_fourth_height, EnumDrawFrom.bottom_right);
			
			// make it look nice
			int game_color = Color.WHITE;
			int world_color = Color.WHITE;
			
			int change_color = 0;
			if (color_time > max_color_time / 2.0)
				change_color = Functions.linearInterpolateColor(max_color_time / 2.0, max_color_time, color_time, Color.GREEN, Color.WHITE);
			else
				change_color = Functions.linearInterpolateColor(0, max_color_time / 2.0, color_time, Color.WHITE, Color.GREEN);
			
			if (game_time / 1000.0 == prev_best)
			{
				game_color = change_color;
			}
			if (game_time / 1000.0 == world_best)
			{
				world_color = change_color;
			}
			
			// draw level score
			Constants.text.drawDecimalNumber(this.game_time / 1000.0, 4, 3, Constants.two_third_width, Constants.three_fourth_height);
			
			// draw our local score
			Constants.text.drawDecimalNumber(this.prev_best, 4, 3, Constants.two_third_width, Constants.two_fourth_height, game_color);
			
			// draw our world score
			if (Constants.network_activity > 0 || world_best < 0)
				network_loader.onDrawLoading();
			if (this.world_best > 0)
			{
				Constants.text.drawDecimalNumber(this.world_best, 4, 3, Constants.two_third_width, Constants.one_fourth_height, game_color);
			}
		}
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
	
	@Override
	public void onScreenChange()
	{
		if (level_done == false)
		{
			fade_out = true;
			level_done = true;
			
			// get the previous
			prev_best = SinglePlayerSave.getPrevBest(level_name);
			
			// get and set world bests here
			TaskSendScore score_sender = new TaskSendScore();
			score_sender.setFinishedScoring(this);
			Constants.accounts.sendScore(level_name, game_time / 1000.0, score_sender);
			
			// save this as the best if its better...
			if (game_time < prev_best && game_time > 0)
			{
				SinglePlayerSave.saveBest(level_name, game_time / 1000.0);
				prev_best = game_time / 1000.0;
			}
		}
	}
	
	public void onScoreCompleted(double score)
	{
		this.world_best = score;
	}
}
