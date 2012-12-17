package com.kobaj.math;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentManager;

import com.kobaj.audio.MusicPlayer;
import com.kobaj.audio.Sound;
import com.kobaj.input.InputManager;
import com.kobaj.opengldrawable.Text;
import com.kobaj.openglgraphics.AmbientLightShader;
import com.kobaj.openglgraphics.CompressedLightShader;

public class Constants
{
	// you may be wondering why the heck I'm not using
	// getters and setters to make this class a bit more immutable
	// answer 1: I'm crazy
	// answer 2: This is faster/easier to code (I'm on a tight schedule)
	// answer 3: I actually read somewhere (stackoverflow?) this is faster to execute. *shrugs*
	
	// density independent pixels
	public static double dip_scale;
	
	// scaled text density
	public static double sd_scale;
	
	// width n height
	public static int width;
	public static int height;
	
	public static double shader_width; // technically double the ratio
	public static final double shader_height = 2.0; // always regardless of phone
	
	// this is (width / height)
	public static double ratio;
	
	// this is where the camera is translated to
	public static double x_shader_translation;
	public static double y_shader_translation;
	public static double z_shader_translation;
	
	// camera zoom settings
	public static double min_zoom = -.10;
	public static double max_zoom = .35;
	
	// and for when a level loads
	public static final double z_modifier = 0.00001;
	
	// physics constants
	public static final double gravity_default = 0.000750;
	public static final double max_y_velocity_default = 2.5;
	public static final double max_x_velocity_default = 2.5;
	public static final double normal_acceleration_default = 0.00050;
	public static final double normal_reverse_acceleration_default = 0.00600;
	public static final double collision_detection_height_default = 1.0;
	public static final double jump_velocity_default = 0.750;
	public static final double jump_limiter_default = 0.185;
	
	// physics variables
	public static double gravity;
	public static double max_y_velocity;
	public static double max_x_velocity;
	public static double normal_acceleration;
	public static double normal_reverse_acceleration;
	public static double collision_detection_height;
	public static double jump_velocity;
	public static double jump_limiter;
	public static double max_speed;
	
	// same regardless of screen.
	public static final double normal_air_damping = .90;
	public static final double normal_friction = .011;
	
	// loading screen pretties
	public static final int loading_radius = 4;
	public static final int loading_max_shapes = 8;
	public static final int loading_primary_color = 0xFF0000FF;
	public static final int loading_secondary_color = 0xFF00FF00;
	
	// While the below are not really constant and don't belong here
	// this is a convenient way of being able to see all objects a game screen can see and use
	
	// mmmm context
	// you may be saying "BUT THATS A MEMORY LEAK"
	// nope, this is the application context...
	// So theoretically its all good. Theoretically. :)
	public static Context context;
	public static FragmentManager fragment_manager;
	
	// the ever static reference to resources.
	public static Resources resources;
	
	public static Physics physics;
	
	public static MusicPlayer music_player;
	public static Sound sound;
	
	public static InputManager input_manager;
	// input constants
	public static final int input_draw_color = 0xAAFFFFFF;
	public static final double input_circle_width = 50;
	public static final int input_swipe_sensitivity = 140;
	public static final double max_brightness = 0.75;
	public static final double min_brightness = 0.55;
	
	//text (obviously?)
	public static Text text;
	public static double text_size = 16.0; // all text is the same size (how lame!)
	
	//camera
	public static float[] my_view_matrix = new float[16];
	public static float[] my_proj_matrix = new float[16];
	public static final float[] identity_matrix = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
	
	//lighting
	public static AmbientLightShader ambient_light;
	public static CompressedLightShader compressed_light;
	
	//networking 
	public static final String main_url = "http://something.com";
	
	//metrics
	public static boolean draw_fps= true;
	public static int quads_drawn_screen = 0;
	
	//errors
	public static int exception_timeout = 300; //ms
}
