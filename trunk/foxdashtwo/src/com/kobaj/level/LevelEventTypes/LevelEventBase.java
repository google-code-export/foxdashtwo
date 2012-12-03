package com.kobaj.level.LevelEventTypes;

import java.util.ArrayList;

import com.kobaj.level.EnumLevelEvent;
import com.kobaj.level.LevelObject;
import com.kobaj.level.LevelTypeLight.LevelAmbientLight;

public abstract class LevelEventBase
{
	public final EnumLevelEvent this_event;
	
	protected LevelObject player_cache;
	protected ArrayList<LevelObject> object_cache;
	protected ArrayList<LevelAmbientLight> light_cache;
	protected ArrayList<String> id_cache;
	
	public LevelEventBase(EnumLevelEvent type)
	{
		this_event = type;
	}
	
	public void onInitialize(final LevelObject player, final ArrayList<LevelObject> objects, final ArrayList<LevelAmbientLight> lights, final ArrayList<String> affected_strings)
	{
		player_cache = player;
		object_cache = objects;
		light_cache = lights;
		id_cache = affected_strings;
	}
	
	public abstract void onUpdate(double delta, boolean active);
	
	public void onDraw()
	{
		// do nothing
	}
}
