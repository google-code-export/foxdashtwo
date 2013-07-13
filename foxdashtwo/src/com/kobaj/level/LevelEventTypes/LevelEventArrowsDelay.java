package com.kobaj.level.LevelEventTypes;

public class LevelEventArrowsDelay extends LevelEventArrows
{
	public double max_delay = 5000; // 5 seconds

	public LevelEventArrowsDelay(EnumLevelEvent type)
	{
		super(type);
	}
	
	@Override
	public void onUpdate(double delta, boolean active)
	{
		if(max_delay > 0)
		{
			max_delay += delta;
			return;
		}
		
		super.onUpdate(delta, active);
	}
}
