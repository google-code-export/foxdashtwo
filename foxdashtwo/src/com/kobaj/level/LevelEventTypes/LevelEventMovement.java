package com.kobaj.level.LevelEventTypes;

import java.util.ArrayList;

import android.util.Log;

import com.kobaj.level.Level;

public class LevelEventMovement extends LevelEventBase
{
	// how long to press each button in ms
	private double left;
	private double jump;
	private double right;
	
	private double original_left;
	private double original_jump;
	private double original_right;
	
	private boolean activated = false;
	
	private Level level_cache;
	
	public LevelEventMovement(EnumLevelEvent type)
	{
		super(type);
	}
	
	@Override
	public void onInitialize(final Level level, final ArrayList<String> affected_strings)
	{
		super.onInitialize(level, affected_strings);
		
		this.level_cache = level;
		
		if (affected_strings.size() == 3)
		{
			try
			{
				original_left = left = Double.parseDouble(affected_strings.get(0));
				original_jump = jump = Double.parseDouble(affected_strings.get(1));
				original_right = right = Double.parseDouble(affected_strings.get(2));
			}
			catch (NumberFormatException e)
			{
				Log.e("Parse Double Error", "Incorrectly formatted double for level movement. Left: " //
						+ affected_strings.get(0) + " Jump: " + affected_strings.get(1) + " Right: " + affected_strings.get(2));
			}
		}
	}
	
	@Override
	public void onUpdate(double delta, boolean active)
	{
		if (active)
		{
			activated = true;
			if (jump > 0)
				level_cache.force_jump_start = true;
		}
		
		if (activated)
		{
			if (left > 0)
			{
				left -= delta;
				level_cache.force_left = true;
			}
			
			if (jump > 0)
			{
				jump -= delta;
				
				if (jump <= 0)
				{
					level_cache.force_jump_end = true;
				}
			}
			
			if (right > 0)
			{
				right -= delta;
				level_cache.force_right = true;
			}
		}
	}
	
	@Override
	public void onKillReset()
	{
		activated = false;
		left = original_left;
		jump = original_jump;
		right = original_right;
	}
}
