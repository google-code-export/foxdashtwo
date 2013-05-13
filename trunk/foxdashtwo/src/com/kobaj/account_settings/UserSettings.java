package com.kobaj.account_settings;

import org.simpleframework.xml.Element;

import com.kobaj.foxdashtwo.GameActivity;
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
	
	public static double zoom_value = 0.5;
	
	@Element
	public static boolean send_statistics = true;
	
	@Element
	public static int max_fps = 60;
	
	public static enum DebugMode
	{
		none, fps, detailed
	};
	
	@Element
	public static DebugMode my_debug_mode = DebugMode.none;
	
	@Element
	public static int fbo_divider = 1;
	
	@Element
	public static boolean fbo_warned = false;
	
	public static void zoom(double value)
	{
		if (value > Constants.user_zoom_max)
			value = Constants.user_zoom_max;
		else if (value < Constants.user_zoom_min)
			value = Constants.user_zoom_min;
		
		zoom_value = value;
	}
	
	public static void fbo(int value)
	{
		// best way to do this EVER
		if (value > 0)
		{
			if (fbo_divider == 1)
				fbo_divider = 2;
			else if (fbo_divider == 2)
				fbo_divider = 4;
			else
				fbo_divider = 8;
		}
		
		if (value < 0)
		{
			if (fbo_divider == 8)
				fbo_divider = 4;
			else if (fbo_divider == 4)
				fbo_divider = 2;
			else
				fbo_divider = 1;
		}
		
		GameActivity.mGLView.my_game.reInitializeQuadRenders();
	}
}