package com.kobaj.foxdashtwo;

import com.kobaj.input.InputType.EnumInputType;

public class UserSettings
{
	public static EnumInputType active_input_type = EnumInputType.halfhalf;
	private final static String ait = "active_input_type";
	
	public static void loadUserSettings()
	{
		active_input_type = EnumInputType.values()[GameActivity.mPrefs.getInt(ait, EnumInputType.halfhalf.ordinal())];
	}
	
	public static void saveUserSettings()
	{
		GameActivity.ed.putInt(ait, active_input_type.ordinal());
	}
}
