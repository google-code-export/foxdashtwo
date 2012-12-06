package com.kobaj.screen;

import com.kobaj.foxdashtwo.R;
import com.kobaj.input.EnumKeyCodes;
import com.kobaj.input.GameInputModifier;
import com.kobaj.loader.FileHandler;
import com.kobaj.math.Constants;
import com.kobaj.screen.screenaddons.BaseDebugScreen;
import com.kobaj.screen.screenaddons.BaseInteractionPhysics;
import com.kobaj.screen.screenaddons.BaseLoadingScreen;
import com.kobaj.screen.screenaddons.BasePauseScreen;

public class SinglePlayerScreen extends BaseScreen
{
	// modification of input
	private GameInputModifier my_modifier;
	
	// test level
	public int test_level_R = R.raw.test_level;
	private com.kobaj.level.Level test_level;
	
	// addons
	BaseDebugScreen debug_addon;
	BaseLoadingScreen loading_addon;
	BaseInteractionPhysics interaction_addon;
	BasePauseScreen pause_addon;
	
	public SinglePlayerScreen()
	{
		// initialize everything
		my_modifier = new GameInputModifier();
		
		/*
		 * helpful while I build the level class com.kobaj.level.Level test = new com.kobaj.level.Level(); test.writeOut(); com.kobaj.loader.XMLHandler.writeSerialFile(test, "test_level");
		 */
	}
	
	@Override
	public void onLoad()
	{
		// load our addons. Do the loader first
		loading_addon = new BaseLoadingScreen();
		interaction_addon = new BaseInteractionPhysics();
		
		// level
		test_level = FileHandler.readSerialResource(Constants.resources, test_level_R, com.kobaj.level.Level.class);
		if (test_level != null)
			test_level.onInitialize();
		
		// control input and other addons
		my_modifier.onInitialize();
		
		// debug_addon = new BaseDebugScreen(test_level, EnumDebugType.aabb);
		
		pause_addon = new BasePauseScreen();
		pause_addon.onInitialize();
		
		System.gc();
	}
	
	@Override
	public void onUpdate(double delta)
	{
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
		// just for now, this may be deleted later to replace a button
		my_modifier.onUpdate();
		
		// update all our objects and lights and things
		test_level.onUpdate(delta);
		
		// interaction
		interaction_addon.onUpdate(delta, my_modifier, test_level);
		
		// debug_addon.onUpdate(delta, test_level);
	}
	
	@Override
	public void onDrawObject()
	{
		test_level.onDrawObject();
	}
	
	@Override
	public void onDrawLight()
	{
		test_level.onDrawLight();
	}
	
	@Override
	public void onDrawConstant()
	{
		test_level.onDrawConstant();
		
		// debug_addon.onDrawObject();
		
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
