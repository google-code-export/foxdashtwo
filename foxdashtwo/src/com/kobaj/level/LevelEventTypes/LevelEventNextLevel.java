package com.kobaj.level.LevelEventTypes;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.SinglePlayerSave;
import com.kobaj.loader.FileHandler;
import com.kobaj.math.Constants;
import com.kobaj.screen.SinglePlayerScreen;

public class LevelEventNextLevel extends LevelEventBase
{
	SinglePlayerScreen next_screen;
	
	public LevelEventNextLevel(EnumLevelEvent type)
	{
		super(type);
		
		next_screen = new SinglePlayerScreen();
	}
	
	public void setNextLevel(String level_name)
	{
		// if not then try to load from R
		int level_R = Constants.resources.getIdentifier(level_name, "raw", "com.kobaj.foxdashtwo");
		if (level_R != 0)
		{
			next_screen.setLevel(level_R, level_name);
			return;
		}
		
		// first see if it is a physical level on disk
		if (FileHandler.fileExists(level_name))
		{
			next_screen.setLevel(level_name);
			return;
		}
	}
	
	@Override
	public void onUpdate(double delta, boolean active)
	{
		if (active)
		{
			// mark the old level as complete
			SinglePlayerSave.finished_levels.add(SinglePlayerSave.last_level);
			SinglePlayerSave.last_checkpoint = null;
			
			// load the next level
			GameActivity.mGLView.my_game.onChangeScreen(next_screen);
		}
	}
	
}
