package com.kobaj.account_settings;

import java.util.HashMap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import com.kobaj.math.Constants;

public class SinglePlayerSave
{
	public static String last_level = "";
	
	public static String last_checkpoint = "";
	
	@Element
	public static boolean are_you_still_there = true;
	
	@ElementMap
	public static HashMap<String, Double> finished_levels_and_times = new HashMap<String, Double>();
	
	public static double getPrevBest(String level_name)
	{
		if (!level_name.equals(Constants.empty))
		{
			if (finished_levels_and_times.containsKey(level_name))
			{
				return finished_levels_and_times.get(level_name);
			}
		}
		
		return Double.MAX_VALUE;
	}
	
	public static void saveBest(String level_name, Double new_best)
	{
		if (!level_name.equals(Constants.empty))
			finished_levels_and_times.put(level_name, new_best);
	}
}
