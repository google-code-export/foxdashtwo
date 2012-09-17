package com.kobaj.level;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

import android.graphics.Color;

public class Level
{
	@Element
	public int total_width;
	public double x_limit;
	@Element
	public int total_height;
	public double y_limit;
	
	@ElementArray
	public LevelObject[] object_array;
	
	@ElementArray
	public LevelLight[] light_array;
	
	@Element
	public LevelObject player;
	
	//no constructor
	
	public void onInitialize()
	{
		//setup general objects
		for(LevelObject object: object_array)
			object.onInitialize();
		for(LevelLight light: light_array)
			light.onInitialize();
		
		//setup player
		//player.onInitialize();
		player.quad_object = new com.kobaj.opengldrawable.Quad.QuadColorShape(0, 64, 64, 0, Color.GRAY, 0);//new com.kobaj.opengldrawable.Quad(R.drawable.ic_launcher);
		player.quad_object.z_pos -= (player.z_plane * .00001);
	
		//set widths and heights for the camera
		x_limit = com.kobaj.math.Functions.screenXToShaderX(total_width) / 2.0;
		y_limit = com.kobaj.math.Functions.screenYToShaderY(total_height) / 2.0;
	}
	
	//this method will be deleted.
	public void writeOut()
	{
		player = new LevelObject();
		player.draw_from = com.kobaj.opengldrawable.EnumDrawFrom.top_left;
		player.this_object = EnumLevelObject.test;
		player.x_pos = 0;
		player.y_pos = 0;
		player.z_plane = 5;
		
		object_array = new LevelObject[1];
		light_array = new LevelLight[1];
		
		LevelObject temp = new LevelObject();
		temp.draw_from = com.kobaj.opengldrawable.EnumDrawFrom.top_left;
		temp.this_object = EnumLevelObject.test;
		temp.x_pos = 200;
		temp.y_pos = 200;
		temp.z_plane = 5;
		
		LevelLight templ = new LevelLight();
		templ.is_bloom = false;
		templ.radius = 100;
		templ.color = Color.WHITE;
		templ.degree = 0;
		templ.x_pos = 0;
		templ.y_pos = 0;
		templ.light = EnumLevelLight.ambient;
		
		object_array[0] = temp;
		light_array[0] = templ;
	}
}
