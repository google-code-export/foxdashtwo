package com.kobaj.level.LevelEventTypes;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.SinglePlayerSave;
import com.kobaj.screen.SinglePlayerScreen;

public class LevelEventNextLevel extends LevelEventBase
{
	SinglePlayerScreen next_screen;
	
	public LevelEventNextLevel(EnumLevelEvent type)
	{
		super(type);
		
		next_screen = new SinglePlayerScreen();
	}
	
	@Override
	public void onUpdate(double delta, boolean active)
	{
		if (active)
		{
			// mark the old level as complete
			if (SinglePlayerSave.last_level != null)
				SinglePlayerSave.finished_levels.add(SinglePlayerSave.last_level);
			
			// set the next level
			if (!this.id_cache.isEmpty())
				SinglePlayerSave.last_level = id_cache.get(0);
			SinglePlayerSave.last_checkpoint = null;
			
			// load the next level
			GameActivity.mGLView.my_game.onChangeScreen(next_screen);
		}
	}
	
}
