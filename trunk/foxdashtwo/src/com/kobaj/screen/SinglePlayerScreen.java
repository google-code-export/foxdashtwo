package com.kobaj.screen;

import android.support.v4.app.DialogFragment;

import com.kobaj.foxdashtwo.FoxdashtwoActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.input.EnumKeyCodes;
import com.kobaj.input.GameInputModifier;
import com.kobaj.loader.FileHandler;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.message.PopupManager;
import com.kobaj.screen.screenaddons.BaseDebugScreen;
import com.kobaj.screen.screenaddons.BaseInteractionScreen;
import com.kobaj.screen.screenaddons.BaseLoadingScreen;

public class SinglePlayerScreen extends BaseScreen
{
	// modification of input
	private GameInputModifier my_modifier;
	
	// test level
	private com.kobaj.level.Level test_level;
	
	// addons
	BaseDebugScreen debug_addon;
	BaseLoadingScreen loading_addon;
	BaseInteractionScreen interaction_addon;
	
	//may be deleted later
	public static final String save_file_name = "external_level";
	private static final String format = ".xml";
	
	public SinglePlayerScreen()
	{
		// initialize everything
		my_modifier = new GameInputModifier();
		
		/*
		 * helpful while I build the level class
		 * com.kobaj.level.Level test = new com.kobaj.level.Level(); test.writeOut(); com.kobaj.loader.XMLHandler.writeSerialFile(test, "test_level");
		 */
	}
	
	@Override
	public void onLoad()
	{
		// load our addons. Do the loader first
		loading_addon = new BaseLoadingScreen();
		interaction_addon = new BaseInteractionScreen();
		
		// grab from disk
		boolean loaded = false;
		String[] levels = FileHandler.getFileList();
		for (String p : levels)
			if (p.equalsIgnoreCase(save_file_name + format))
			{
				loaded = true;
				try
				{
					test_level = FileHandler.readSerialFile("external_level", com.kobaj.level.Level.class);
				}
				catch(Exception e)
				{
					loaded = false;
					// do nothing
				}
				catch(Error e)
				{
					loaded = false;
					// do nothing
				}
					
				break;
			}
		
		if (!loaded)
			test_level = FileHandler.readSerialResource(Constants.resources, R.raw.test_level, com.kobaj.level.Level.class);
		
		// level
		if (test_level != null)
			test_level.onInitialize();
		
		// control input
		my_modifier.onInitialize();
		
		//debug_addon = new BaseDebugScreen(test_level, true, false);
	}
	
	@Override
	public void onUpdate(double delta)
	{
		Functions.checkGlError();
		
		// just for now, this may be deleted later to replace a button
		my_modifier.onUpdate();
		
		// update all our objects and lights and things
		test_level.onUpdate(delta);
		
		// interaction
		interaction_addon.onUpdate(delta, my_modifier, test_level);
		
		// let the user load a map
		if(Constants.input_manager.getKeyPressed(EnumKeyCodes.back))
		{
			DialogFragment newFragment = new PopupManager();
		    newFragment.show(Constants.fragment_manager, "missiles");
			
			//Constants.context.start(
			//PopupManager.showSimplePopUp(EnumPopupType.save_map);
		}
		
		Functions.checkGlError();
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
		
		// draw some helpful bounding boxes
		// debug_addon.onDrawObject(test_level);
		
		// draw the controls
		my_modifier.onDraw();
	}
	
	@Override
	public void onDrawLoading(double delta)
	{
		// we want all loading screens to look the same, so we use this helper loader thingy :)
		if (loading_addon != null)
			loading_addon.onDrawLoading(delta);
	}
}
