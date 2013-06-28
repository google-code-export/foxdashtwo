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
import com.kobaj.openglgraphics.ShadowLightShader;

public class Constants
{
	// you may be wondering why the heck I'm not using
	// getters and setters to make this class a bit more immutable
	// answer 1: I'm crazy
	// answer 2: This is faster/easier to code (I'm on a tight schedule)
	// answer 3: I actually read somewhere (stackoverflow?) this is faster to execute. *shrugs*
	
	public static final long force_update_epoch = 1367777076;
	
	public static final boolean demo_mode = false;
	
	// density independent pixels
	public static double dip_scale;
	
	// width n height
	public static int width; // relative to the game
	public static int height;
	
	public static int device_width; // relative to the device
	public static int device_height;
	public static double device_ratio;
	public static double device_vratio;
	public static boolean horizontal_ratio = false;
	
	public static double shader_width; // technically double the ratio
	public static final double shader_height = 2.0; // 
	
	// this is (width / height)
	public static double ratio;
	public static final double my_ratio = 1.777777777; // some things (infinite jig) require a set ratio
	
	public static double local_ratio;
	public static double local_vratio;
	
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

	// other pretty constants for the shadow under the fox
	public static final double shadow_radius = 50.0;
	public static final double shadow_height = 450;
	public static double shadow_height_shader;
	
	// score position constants
	public static final double mini_time_pos_x_default = 100;
	public static final double mini_time_pos_y_default = 100;
	public static final double width_padding_default = 75;
	public static final double height_padding_default = 75;
	
	// shader coordinates
	public static double mini_time_pos_x;
	public static double mini_time_pos_y;
	public static double width_padding;
	public static double height_padding;
	
	// additional shader coordinates
	public static double one_fourth_height;
	public static double two_fourth_height;
	public static double three_fourth_height;
	public static double one_third_width;
	public static double two_third_width;
	public static double three_fourth_width;
	
	// physics constants
	public static final double gravity_default = 0.00100;
	public static final double max_y_velocity_default = .9;
	public static final double max_x_velocity_default = .9;
	public static final double normal_acceleration_default = 0.00100;
	public static final double normal_reverse_acceleration_default = 0.00750;
	public static final double collision_detection_height_default = 1.0;
	public static final double jump_velocity_default = 0.800;
	public static final double jump_limiter_default = 0.185;
	public static final double player_downward_platform_acc_default = -.001;
	public static final double player_movement_threshold_horizontal_default = .01;
	public static final double player_movement_threshold_vertical_default = .01;
	
	// floating platform help
	public static final double floating_move_lr_speed_default = .02;
	public static final double floating_lr_distance_default = 2.0;
	
	public static final double arbitrary_sound_velocity = 2.0;
	
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
	public static double player_movement_threshold_horizintal;
	public static double player_movement_threshold_vertical;
	public static double floating_move_lr_acc;
	public static double floating_lr_distance;
	
	// same regardless of screen.
	public static final double normal_air_damping = .75;
	public static final double normal_friction = .011;
	
	// floating platforms
	public static final double floating_spring = .00005;
	public static final double floating_damper = .0035;
	
	// loading screen pretties
	public static final int loading_radius = 4;
	public static final int loading_max_shapes = 8;
	public static final int loading_primary_color = 0xFFDD550C;
	public static final int loading_secondary_color = 0xFF03244D;
	
	public static final double default_spinning_jig_radius = 25;
	public static double spinning_jig_radius;
	
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
	
	// music
	public static MusicPlayer music_player;
	public static Sound sound;
	public static final int music_fade_time = 3000;
	
	public static InputManager input_manager;
	// input constants
	public static final double input_circle_width = 50;
	public static final int input_unpress_color = 0xBBBBBBCC;
	public static final int input_press_color = 0xBB888899;
	
	// button constants
	public static final int ui_button_unpressed = 0xFFFFFFFF;
	public static final int ui_button_pressed = 0xFFBBBBBB;
	public static final int unpressed_color = 0xBBBBBBCC;
	public static final int pressed_color = 0xBB888899;
	
	// text (obviously?)
	public static Text text;
	public static double text_size = 28.0; // all text is the same size (how lame!)
	
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
	public static ShadowLightShader shadow_light;
	
	// metrics
	public static int quads_drawn_screen = 0;
	public static int quads_coord_map_check = 0;
	public static double x_100;
	public static double y_50;
	public static double y_125;
	public static double y_200;
	public static double sixteen;
	
	// errors
	public static int exception_timeout = 300; // ms
	
	// accounts
	public static Accounts accounts;
	public static boolean logged_in = false;
	public static boolean logging_in = false;
	public static int uid = -1;
	
	// network +1 when activity,
	// -1 when activity ends
	// if 0, then no activity
	public static int network_activity = 0;
	
	// strings
	public static final String empty = "";
	
	// true = draw, false = draw absolutely nothing.
	// this is used in combination with the map download tool
	public static boolean global_draw = true; 
	
	// screen changes
	public static final int fade_delay = 250;
	public static final int light_active_fade = 1000;
	public static final int object_active_fade = 1000;
	
	// background parallax
	public static final double enum_layer_background_para = 30.0;
	public static final double enum_layer_background_aux_para = 20.0;
	public static final double enum_layer_foreground_para = 30.0;
	public static final double enum_layer_foreground_aux_para = 20.0;
}
