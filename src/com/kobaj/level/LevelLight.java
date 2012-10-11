package com.kobaj.level;

import org.simpleframework.xml.Element;

//interfaces are slow.
public abstract class LevelLight
{	
	@Element
	public boolean active;
	
	@Element
	public int id;
	
	public abstract void onInitialize();
	public void onUpdate(double delta)
	{
		// do nothing.	
	}
	public abstract void onDrawLight();
}
