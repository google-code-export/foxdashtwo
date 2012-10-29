package com.kobaj.screen;

import com.kobaj.foxdashtwo.R;
import com.kobaj.input.GameInputModifier;
import com.kobaj.loader.XMLHandler;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;
import com.kobaj.screen.screenaddons.BaseDebugScreen;
import com.kobaj.screen.screenaddons.BaseInteractionScreen;
import com.kobaj.screen.screenaddons.BaseLoadingScreen;

public class SinglePlayerScreen extends BaseScreen
{
	//modification of input
	private GameInputModifier my_modifier;

	// test level
	private com.kobaj.level.Level test_level;

	//addons
	BaseDebugScreen debug_addon;
	BaseLoadingScreen loading_addon;
	BaseInteractionScreen interaction_addon;
	
	QuadColorShape my_backdrop;
	
	public SinglePlayerScreen()
	{
		//initialize everything
		my_modifier = new GameInputModifier();
		
		/*
		 * helpful while I build the level class
		 */
		/*
		com.kobaj.level.Level test = new com.kobaj.level.Level();
		test.writeOut(); com.kobaj.loader.XMLHandler.writeSerialFile(test,
		"test_level");*/
	}
	
	@Override
	public void onLoad()
	{
		//load our addons. Do the loader first
		loading_addon = new BaseLoadingScreen();
		interaction_addon = new BaseInteractionScreen();
	
		//grab from disk
		boolean loaded = false;
		String[] levels = XMLHandler.getFileList();
		for(String p: levels)
			if (p.equalsIgnoreCase("external_level"))
			{
				loaded = true;
				test_level = XMLHandler.readSerialFile("external_level", com.kobaj.level.Level.class);
				break;
			}
		
		if(!loaded)
			test_level = XMLHandler.readSerialFile(Constants.resources, R.raw.test_level, com.kobaj.level.Level.class);
		
		//level
		if (test_level != null)
			test_level.onInitialize();
		
		//control input
		my_modifier.onInitialize();
		
		debug_addon = new BaseDebugScreen(test_level, true, false);
		
		my_backdrop = new QuadColorShape(Constants.width, Constants.height, 0xFF333333, 0);
	}
	
	@Override
	public void onUpdate(double delta)
	{
		Functions.checkGlError();
		
		//just for now, this may be deleted later to replace a button
		my_modifier.onUpdate();
		
		//interaction
		interaction_addon.onUpdate(delta, my_modifier, test_level);
		
		//update all our objects and lights and things
		test_level.onUpdate(delta);
		
		// make sure we dont go through the level (should be deleted later)
		/*if (test_level.player.quad_object.getYPos() < -1)
		{
			test_level.player.quad_object.y_acc = 0;
			test_level.player.quad_object.y_vel = 0;
			test_level.player.quad_object.setPos(test_level.player.quad_object.getXPos(), 1, EnumDrawFrom.center);
		}*/
		
		Functions.checkGlError();
	}
	
	@Override
	public void onDrawObject()
	{	
		//my_backdrop.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Color.WHITE, true);
		
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
		
		//draw some helpful bounding boxes
		//debug_addon.onDrawObject(test_level);
		
		//draw the controls
		my_modifier.onDraw();
	}

	@Override
	public void onDrawLoading(double delta)
	{
		//we want all loading screens to look the same, so we use this helper loader thingy :)
		if(loading_addon != null)
			loading_addon.onDrawLoading(delta);
	}
}
