package com.kobaj.opengldrawable;

import java.util.HashMap;

import org.simpleframework.xml.ElementMap;

public class FrameAnimation
{
	//look at the cute little class!
	
	@ElementMap
	public HashMap<EnumGlobalAnimationList, FrameSet> animation_set;
}
