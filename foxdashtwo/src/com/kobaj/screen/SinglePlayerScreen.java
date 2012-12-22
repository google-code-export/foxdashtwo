package com.kobaj.screen;

import android.util.Log;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.input.EnumKeyCodes;
import com.kobaj.input.GameInputModifier;
import com.kobaj.loader.FileHandler;
import com.kobaj.loader.GLBitmapReader;
import com.kobaj.math.Constants;
import com.kobaj.screen.screenaddons.LevelDebugScreen;
import com.kobaj.screen.screenaddons.BaseInteractionPhysics;
import com.kobaj.screen.screenaddons.BaseLoadingScreen;
import com.kobaj.screen.screenaddons.EnumDebugType;
import com.kobaj.screen.screenaddons.floatingframe.BasePauseScreen;

public class SinglePlayerScreen extends BaseScreen
{
	// modification of input
	private GameInputModifier my_modifier;
	
	// test level
	public String level_string = null;
	public int level_R = R.raw.test_level;
	private com.kobaj.level.Level the_level;
	
	// addons
	LevelDebugScreen debug_addon;
	BaseLoadingScreen loading_addon;
	BaseInteractionPhysics interaction_addon;
	BasePauseScreen pause_addon;
	
	public SinglePlayerScreen()
	{
		// initialize everything
		my_modifier = new GameInputModifier();
		loading_addon = new BaseLoadingScreen();
		interaction_addon = new BaseInteractionPhysics(); //has no quads
	}
	
	@Override
	public void onLoad()
	{
		// load our addons. Do the loader first
		loading_addon.onInitialize();
		
		// level
		if(level_string != null)
			the_level = FileHandler.readSerialFile(level_string, com.kobaj.level.Level.class);
		else
			the_level = FileHandler.readSerialResource(Constants.resources, level_R, com.kobaj.level.Level.class);
		
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
		
		// debug_addon = new BaseDebugScreen(test_level, EnumDebugType.aabb);
		
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
		
		debug_addon = new LevelDebugScreen(the_level, EnumDebugType.original_aabb);
		
		System.gc();
	}

	@Override
	public void onUnload()
	{
		my_modifier.onUnInitialize();
		loading_addon.onUnInitialize();
		pause_addon.onUnInitialize();
		
		the_level.onUnInitialize();
	}
	
	@Override
	public void onUpdate(double delta)
	{
		// that music
		Constants.music_player.onUpdate();
		
		if (current_state != EnumScreenState.paused)
			onRunningUpdate(delta);
		else
			pause_addon.onUpdate(delta);
		
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
			else
				current_state = EnumScreenState.running;
		}
	}
	
	private void onRunningUpdate(double delta)
	{
		// update all our objects and lights and things
		the_level.onUpdate(delta);
		
		// interaction
		interaction_addon.onUpdate(delta, my_modifier, the_level);
		
		//debug_addon.onUpdate(delta, test_level);
	}
	
	@Override
	public void onDrawObject()
	{
		the_level.onDrawObject();
		
		//debug_addon.onDrawObject();
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
