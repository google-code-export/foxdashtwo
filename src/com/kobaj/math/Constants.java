package com.kobaj.math;

import android.content.Context;
import android.content.res.Resources;

import com.kobaj.audio.MusicPlayList;
import com.kobaj.audio.Sound;
import com.kobaj.input.InputManager;

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
	public static int height;
	
	public static double shader_width;
	public final static double shader_height = 2.0; // always
	
	// this is width / height
	public static double ratio;
	
	//mmmm context
	//you may be saying "BUT THATS A MEMORY LEAK"
	//nope, this is the application context...
	//So theoretically its all good. Theoretically. :)
	public static Context context;
	
	//the ever static reference to resources.
	public static Resources resources;
	
	public static Physics physics;
	
	public static MusicPlayList music_play_list;
	public static Sound sound;
	
	public static InputManager input_manager;
}