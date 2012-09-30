package com.kobaj.screen;

import com.kobaj.foxdashtwo.R;
import com.kobaj.input.GameInputModifier;
import com.kobaj.opengldrawable.EnumDrawFrom;
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
	
	public SinglePlayerScreen()
	{
		//initialize everything
		my_modifier = new GameInputModifier();
		
		/*
		  helpful while I build the level class
		 */ 
		com.kobaj.level.Level test = new com.kobaj.level.Level();
		test.writeOut(); com.kobaj.loader.XMLHandler.writeSerialFile(test,
		"test_level");
		 
		test_level = com.kobaj.loader.XMLHandler.readSerialFile(com.kobaj.math.Constants.resources, R.raw.test_level, com.kobaj.level.Level.class);
	}
	
	@Override
	public void onLoad()
	{
		//load our addons. Do the loader first
		loading_addon = new BaseLoadingScreen();
		debug_addon = new BaseDebugScreen();
		interaction_addon = new BaseInteractionScreen();
		
		//level
		if (test_level != null)
			test_level.onInitialize();
		
		//control input
		my_modifier.onInitialize();
		
		//put in some fake loading time.
		for(int i = 0; i < 60000; i++)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onUpdate(double delta)
	{
		//just for now, this may be deleted later to replace a button
		my_modifier.onUpdate();
		
		//interaction
		interaction_addon.onUpdate(delta, my_modifier, test_level);
		
		// make sure we dont go through the level (should be deleted later)
		if (test_level.player.quad_object.getYPos() < -1)
		{
			test_level.player.quad_object.y_acc = 0;
			test_level.player.quad_object.y_vel = 0;
			test_level.player.quad_object.setPos(test_level.player.quad_object.getXPos(), 1, EnumDrawFrom.center);
		}
	}
	
	@Override
	public void onDrawObject()
	{	
		test_level.onDrawObject();
		
		//draw some helpful bounding boxes
		//debug_addon.onDrawObject(test_level);
	}
	
	@Override
	public void onDrawLight()
	{
		test_level.onDrawLight();
	}
	
	@Override
	public void onDrawConstant()
	{
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