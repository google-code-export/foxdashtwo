package com.kobaj.level.LevelEventTypes;

import java.util.ArrayList;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.level.Level;
import com.kobaj.math.Constants;

public class LevelEventEraseCheckpoints extends LevelEventBase
{
	
	private Level level_cache;
	
	public LevelEventEraseCheckpoints(EnumLevelEvent type)
	{
		super(type);
	}
	
	@Override
	public void onInitialize(final Level level, final ArrayList<String> affected_strings)
	{
		super.onInitialize(level, affected_strings);
		
		level_cache = level;
	}
	
	@Override
	public void onUpdate(double delta, boolean active)
	{
		if(active)
		{
			SinglePlayerSave.last_checkpoint = Constants.empty;
			level_cache.resetCheckpoints();
		}
	}
}
