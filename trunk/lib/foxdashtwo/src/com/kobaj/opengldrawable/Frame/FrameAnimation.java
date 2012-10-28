package com.kobaj.opengldrawable.Frame;

import java.util.HashMap;

import org.simpleframework.xml.ElementMap;

import com.kobaj.opengldrawable.EnumGlobalAnimationList;

public class FrameAnimation
{
	//look at the cute little class!
	
	@ElementMap
	public HashMap<EnumGlobalAnimationList, FrameSet> animation_set;
}
