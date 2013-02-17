package com.kobaj.screen;

import android.util.Log;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.input.EnumKeyCodes;
import com.kobaj.input.GameInputModifier;
import com.kobaj.loader.FileHandler;
import com.kobaj.loader.GLBitmapReader;
import com.kobaj.math.Constants;
import com.kobaj.screen.screenaddons.BaseInteractionPhysics;
import com.kobaj.screen.screenaddons.BaseLoadingScreen;
import com.kobaj.screen.screenaddons.LevelDebugScreen;
import com.kobaj.screen.screenaddons.floatingframe.BasePauseScreen;

public class SinglePlayerScreen extends BaseScreen
{
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
			the_level = FileHandler.readSerialResource(Constants.resources, R.raw.test_level, com.kobaj.level.Level.class);
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
		
		GLBitmapReader.isLoaded();
		
		// debug_addon = new LevelDebugScreen(the_level, EnumDebugType.original_aabb);
		
		System.gc();
	}
	
	@Override
	public void onUnload()
	{
		my_modifier.onUnInitialize();
		loading_addon.onUnInitialize();
		pause_addon.onUnInitialize();
		
		if(the_level != null)
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
		}
	}
	
	private void onRunningUpdate(double delta)
	{
		// update all our objects and lights and things
		the_level.onUpdate(delta);
		
		// interaction
		interaction_addon.onUpdate(delta, my_modifier, the_level);
		
		// debug_addon.onUpdate(delta, test_level);
		
		// regardless we fade in
		if (fade_in)
			fade_in = tween_fade_in.onUpdate(delta);
	}
	
	@Override
	public void onDrawObject()
	{
		the_level.onDrawObject();
		
		// debug_addon.onDrawObject();
	}
	
	@Override
	public void onDrawLight()
	{
		the_level.onDrawLight();
	}
	
	@Override
	public void onDrawConstant()
	{
		the_level.onDrawConstant();
		
		// draw the controls
		if (current_state != EnumScreenState.paused)
			my_modifier.onDraw();
		else
			pause_addon.onDraw();
		
		// finally
		if (fade_in)
			black_overlay.onDrawAmbient(Constants.my_ip_matrix, true);
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
