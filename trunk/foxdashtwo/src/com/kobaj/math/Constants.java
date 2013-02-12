package com.kobaj.math;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentManager;

import com.kobaj.account_settings.Accounts;
import com.kobaj.audio.MusicPlayer;
import com.kobaj.audio.Sound;
import com.kobaj.input.InputManager;
import com.kobaj.opengldrawable.Text;
import com.kobaj.openglgraphics.AmbientLightShader;
import com.kobaj.openglgraphics.BlurLightShader;
import com.kobaj.openglgraphics.CompressedLightShader;
import com.kobaj.openglgraphics.GodRayLightShader;

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
	public static final double user_zoom_min = 0;
	public static final double user_zoom_max = .5;
	public static final double min_zoom = -.35;
	public static final double max_zoom = .35;
	public static final double arbitrary_z = .180;
	
	// and for when a level loads
	public static final double z_modifier = 0.00001;
	
	// physics constants
	public static final double gravity_default = 0.000800;
	public static final double max_y_velocity_default = 1.0;
	public static final double max_x_velocity_default = 1.0;
	public static final double normal_acceleration_default = 0.00050;
	public static final double normal_reverse_acceleration_default = 0.00600;
	public static final double collision_detection_height_default = 1.0;
	public static final double jump_velocity_default = 0.800;
	public static final double jump_limiter_default = 0.185;
	public static final double player_downward_platform_acc_default = -.001;
	public static final double player_movement_threshold_default = .001;
	
	// physics variables
	public static double gravity;
	public static double max_y_velocity; // of all objects
	public static double max_x_velocity;
	public static double normal_acceleration;
	public static double normal_reverse_acceleration;
	public static double collision_detection_height;
	public static double jump_velocity;
	public static double jump_limiter;
	public static double max_speed; // this is of all objects
	public static double player_downward_platform_acc; // player specific
	public static double player_movement_threshold;
	
	// same regardless of screen.
	public static final double normal_air_damping = .90;
	public static final double normal_friction = .011;
	public static final double floating_spring = .00005;
	public static final double floating_damper = .0035;
	
	// loading screen pretties
	public static final int loading_radius = 4;
	public static final int loading_max_shapes = 8;
	public static final int loading_primary_color = 0xFFDD550C;
	public static final int loading_secondary_color = 0xFF03244D;
	
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
	public static final double input_circle_width = 50;
	public static final int input_swipe_sensitivity = 140;
	public static final int input_unpress_color = 0xBBBBBBCC;
	public static final int input_press_color = 0xBB888899;
	
	// button constants
	public static final int unpressed_color = 0xBBBBBBCC;
	public static final int pressed_color = 0xBB888899;
	
	// floating frame colors
	public static final int frame_main_color = 0xCC999999;
	public static final int frame_sec_color = 0xCCFFAAAA;
	
	// text (obviously?)
	public static Text text;
	public static double text_size = 16.0; // all text is the same size (how lame!)
	
	// camera
	public static float[] my_view_matrix = new float[16];
	public static float[] my_proj_matrix = new float[16];
	public static float[] my_vp_matrix = new float[16];
	public static float[] my_ip_matrix = new float[16];
	public static final float[] identity_matrix = { //
	1, 0, 0, 0, //
			0, 1, 0, 0, //
			0, 0, 1, 0, //
			0, 0, 0, 1 }; //
	
	// lighting
	public static AmbientLightShader ambient_light;
	public static CompressedLightShader compressed_light;
	public static GodRayLightShader god_ray_light;
	public static BlurLightShader blur_light;
	
	// metrics
	public static boolean draw_fps = true;
	public static int quads_drawn_screen = 0;
	
	// errors
	public static int exception_timeout = 300; // ms
	
	// accounts
	public static Accounts accounts;
	public static boolean logged_in = false;
	public static boolean logging_in = false;
	
	// network +1 when activity,
	// -1 when activity ends
	// if 0, then no activity
	public static int network_activity = 0;
	
	// strings
	public static final String empty = "";
}
