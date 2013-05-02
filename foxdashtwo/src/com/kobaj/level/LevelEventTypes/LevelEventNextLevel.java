package com.kobaj.level.LevelEventTypes;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.screen.BaseScreen;
import com.kobaj.screen.SinglePlayerScreen;
import com.kobaj.screen.TitleScreen;

public class LevelEventNextLevel extends LevelEventBase
{
	public LevelEventNextLevel(EnumLevelEvent type)
	{
		super(type);
	}
	
	@Override
	public void onUpdate(double delta, boolean active)
	{
		
		if (active)
		{	
			SinglePlayerSave.last_level = null;
			SinglePlayerSave.last_checkpoint = null;
			
			BaseScreen next = null;
			
			// set the next level
			if (!this.id_cache.isEmpty())
			{
				SinglePlayerSave.last_level = id_cache.get(0);
				
				// load the next level
				next = (new SinglePlayerScreen());
			}
			else
				next = (new TitleScreen());
			
			GameActivity.mGLView.my_game.onPreChangeScreen(next);
		}
	}
	
}
