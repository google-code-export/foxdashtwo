package com.kobaj.account_settings;

import java.util.HashSet;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

public class SinglePlayerSave
{
	@Element
	public static String last_level = "";
	
	@Element
	public static String last_checkpoint = "";
	
	@ElementList
	public static HashSet<String> finished_levels = new HashSet<String>();
}
