package com.kobaj.account_settings;

import org.simpleframework.xml.Element;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.input.InputType.EnumInputType;
import com.kobaj.math.Constants;
import com.kobaj.math.Point2D;

public class UserSettings
{
	@Element
	public static EnumInputType active_input_type;
	
	@Element
	public static double desired_music_volume;
	
	@Element
	public static double desired_sound_volume;
	
	@Element
	public static int selected_account_login;
	
	@Element
	public static boolean auto_login;
	
	@Element
	public static boolean send_statistics;
	
	@Element
	public static int max_fps;
	
	public static enum DebugMode
	{
		none, fps, detailed
	};
	
	@Element
	public static DebugMode my_debug_mode;
	
	@Element
	public static int fbo_divider;
	
	@Element
	public static boolean fbo_warned;
	
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
	
	// buttons for nintendo in screen coords
	@Element
	public static Point2D left_button_position;
	
	@Element
	public static Point2D right_button_position;
	
	@Element
	public static Point2D jump_button_position;
	
	public static void resetDefaults()
	{
		UserSettings.active_input_type = EnumInputType.halfhalf;
		UserSettings.desired_music_volume = 0.9;
		UserSettings.desired_sound_volume = 0.8;
		UserSettings.selected_account_login = -1;
		UserSettings.auto_login = false;
		UserSettings.send_statistics = true;
		UserSettings.max_fps = 60;
		UserSettings.my_debug_mode = DebugMode.none;
		UserSettings.fbo_divider = 1;
		UserSettings.fbo_warned = false;
		
		resetButtonPoints();
	}
	
	public static void resetButtonPoints()
	{
		left_button_position = new Point2D();
		left_button_position.setPoint2D(Constants.input_circle_width, 500);
		right_button_position = new Point2D();
		right_button_position.setPoint2D(Constants.input_circle_width * 3.0, 500);
		jump_button_position = new Point2D();
		jump_button_position.setPoint2D(1000.0 - Constants.input_circle_width, 500);
	}
}
