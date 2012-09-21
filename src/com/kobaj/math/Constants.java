package com.kobaj.math;

import android.content.Context;
import android.content.res.Resources;

import com.kobaj.audio.MusicPlayList;
import com.kobaj.audio.Sound;
import com.kobaj.input.InputManager;
import com.kobaj.opengldrawable.Text;
import com.kobaj.openglgraphics.AmbientLightShader;

public class Constants
{
	//you may be wondering why the heck I'm not using 
	//getters and setters to make this class
	//a bit more immutable.
	//answer 1: I'm crazy
	//answer 2: This is faster/easier to code (I'm on a tight schedule)
	//answer 3: I actually read somewhere (stackoverflow?) this is faster to execute. *shrugs*
	
	//density independent pixels
	public static double dip_scale;
	//if it happens to be unknown;
	public static boolean unknown_dip = false;
	
	//scaled text density
	public static double sd_scale;
	
	//width n height
	public static int width;
	public static final int static_width = 800;
	public static int height;
	public static final int static_height = 800;
	
	public static double shader_width;
	public static final double static_shader_width = 3.0 + 1.0 / 3.0;
	public static final double shader_height = 2.0; // always
	public static final double static_shader_height = 2.0; //always
	
	//this is width / height
	public static double ratio;
	public final static double static_ratio = 1.0 + (2.0 / 3.0);
	
	//for calculating bounds and positions
	public static double delta_width;
	public static double delta_height;
	public static double delta_shader_width;
	public static double delta_shader_height;
	
	//this is where the camera is translated to
	public static double x_shader_translation;
	public static double y_shader_translation;
	
	//mmmm context
	//you may be saying "BUT THATS A MEMORY LEAK"
	//nope, this is the application context...
	//So theoretically its all good. Theoretically. :)
	public static Context context;
	
	//the ever static reference to resources.
	public static Resources resources;
	
	//physics constants
	public static final double gravity 						= -.0000015; //well thats a random number.
	public static final double max_y_velocity 				= .01;
	public static final double max_x_velocity 				= .006;
	public static final double normal_acceleration 			= .000000075;
	public static final double normal_reverse_acceleration  = .000000100;
	public static final double normal_air_damping  			= .5;
	public static final double normal_friction 				= .000500000;
	
	//graphic constants
	public static final double max_brightness = 0.65;
	public static final double min_brightness = 0.45;
	
	//While the below are not really constant and don't belong here
	//this is a convenient way of being able to see all objects a game screen can see and use
	public static Physics physics;
	
	public static MusicPlayList music_play_list;
	public static Sound sound;
	
	public static InputManager input_manager;
	
	public static Text text;
	
	public static float[] my_view_matrix;
	public static float[] my_proj_matrix;
	public static final float[] identity_matrix = {1, 0, 0, 0,
												   0, 1, 0, 0,
												   0, 0, 1, 0,
												   0, 0, 0, 1};
	
	public static AmbientLightShader ambient_light;
}