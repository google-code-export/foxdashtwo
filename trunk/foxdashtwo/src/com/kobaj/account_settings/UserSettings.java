package com.kobaj.account_settings;

import org.simpleframework.xml.Element;

import com.kobaj.input.InputType.EnumInputType;
import com.kobaj.math.Constants;

public class UserSettings
{
	@Element
	public static EnumInputType active_input_type = EnumInputType.halfhalf;
	
	@Element
	public static double desired_music_volume = 1.0;
	
	@Element
	public static double desired_sound_volume = 1.0;
	
	@Element
	public static int selected_account_login = -1;
	
	@Element
	public static boolean auto_login = false;
	
	@Element
	public static double zoom_value = 0;
	
	@Element
	public static boolean send_statistics = true;
	
	@Element
	public static int max_fps = 45;
	
	public static void zoom(double value)
	{
		if(value > Constants.user_zoom_max)
			value = Constants.user_zoom_max;
		else if(value < Constants.user_zoom_min)
			value = Constants.user_zoom_min;
		
		zoom_value = value;
	}
}
