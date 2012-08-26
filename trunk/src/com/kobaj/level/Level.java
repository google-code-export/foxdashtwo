package com.kobaj.level;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

public class Level
{
	@Element
	public int total_width;
	@Element
	public int total_height;
	
	@ElementArray
	public LevelObject[] object_array;
	
	@ElementArray
	public LevelLight[] light_array;
	
	//no constructor
	
	public void onInitialize()
	{
		for(LevelObject object: object_array)
			object.onInitialize();
		for(LevelLight light: light_array)
			light.onInitialize();
	}
}
