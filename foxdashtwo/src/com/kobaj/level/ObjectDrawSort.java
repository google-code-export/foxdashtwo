package com.kobaj.level;

import java.util.Comparator;


public class ObjectDrawSort implements Comparator<LevelObject>
{

	// sort it backwords because we iterate over the array backwords when we draw
	public int compare(LevelObject object1, LevelObject object2)
	{
		//first sort by layer
		int result = object1.layer.compareTo(object2.layer);
		if(result != 0)
			return result;
		
		//then sort by z
		result = object1.z_plane < object2.z_plane ? +1 : object1.z_plane > object2.z_plane ? -1 : 0;
		if(result != 0)
			return result;
		
		//then sort by eid
		result = object1.eid < object2.eid ? +1 : object1.eid > object2.eid ? -1 : 0;
		return result;
	}
}
