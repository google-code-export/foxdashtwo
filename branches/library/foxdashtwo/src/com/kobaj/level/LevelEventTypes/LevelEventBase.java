package com.kobaj.level.LevelEventTypes;

import com.kobaj.level.EnumLevelEvent;
import com.kobaj.opengldrawable.Quad.Quad;

public abstract class LevelEventBase
{
	public final EnumLevelEvent this_event;
	
	public LevelEventBase(EnumLevelEvent type)
	{
		this_event = type;
	}
	
	public abstract void onInitialize();
	public abstract void onUpdate(double delta, Quad player, boolean active);
	public abstract void onDraw();
}
