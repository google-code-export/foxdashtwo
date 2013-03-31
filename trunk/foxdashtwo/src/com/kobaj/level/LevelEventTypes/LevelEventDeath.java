package com.kobaj.level.LevelEventTypes;

import java.util.ArrayList;

import com.kobaj.level.Level;

public class LevelEventDeath extends LevelEventBase
{
	public LevelEventDeath(EnumLevelEvent type)
	{
		super(type);
	}
	
	private Level level_cache;
	
	public void onInitialize(final Level level, final ArrayList<String> id_strings)
	{
		level_cache = level;
	}
	
	@Override
	public void onUpdate(double delta, boolean active)
	{
		if (active)
			level_cache.kill = true;
	}
}
