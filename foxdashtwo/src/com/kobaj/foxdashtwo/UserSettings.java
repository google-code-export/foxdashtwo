package com.kobaj.foxdashtwo;

import org.simpleframework.xml.Element;

import com.kobaj.input.InputType.EnumInputType;

public class UserSettings
{
	@Element
	public static EnumInputType active_input_type = EnumInputType.halfhalf;
	
	@Element
	public static double desired_music_volume = 1.0;
	
	@Element
	public static double desired_sound_volume = 1.0;
}
