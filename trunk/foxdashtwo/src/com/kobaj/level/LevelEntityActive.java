package com.kobaj.level;

import org.simpleframework.xml.Element;

public abstract class LevelEntityActive
{
	@Element
	public boolean active; //if true, it can be seen and physics interacted with
}
