package com.kobaj.level;

import java.util.Comparator;

public class ObjectDrawSort implements Comparator<LevelObject>
{

	// sort it backwords because we iterate over the array backwords when we draw
	public int compare(LevelObject object1, LevelObject object2)
	{
		if(object1.z_plane < object2.z_plane)
			return -1;
		else if(object1.z_plane > object2.z_plane)
			return 1;
		else 
			return 0;
	}
}
