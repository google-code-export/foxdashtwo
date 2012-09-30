package com.kobaj.level;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import android.graphics.Color;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;

public class Level
{
	@Element
	public int left_limit;
	@Element
	public int top_limit;
	@Element
	public int right_limit;
	@Element
	public int bottom_limit;
	
	public double left_shader_limit;
	public double top_shader_limit;
	public double right_shader_limit;
	public double bottom_shader_limit;
	
	@ElementList
	public ArrayList<LevelObject> object_list;
	
	@ElementList
	public ArrayList<LevelLight> light_list;
	
	//make things a little faster by storing only those that draw
	ArrayList<LevelPointLight> bloom_light_list = new ArrayList<LevelPointLight>();
	
	@Element
	public LevelObject player;
	
	//no constructor
	
	public void onInitialize()
	{
		//setup general objects
		for(int i = object_list.size() - 1; i >= 0; i--)
			object_list.get(i).onInitialize();
		for(int i = light_list.size() - 1; i >= 0; i--)
		{
			light_list.get(i).onInitialize();
			if(LevelPointLight.class.isAssignableFrom(light_list.get(i).getClass()))
			{
				LevelPointLight temp = LevelPointLight.class.cast(light_list.get(i));
				if(temp.is_bloom)
					bloom_light_list.add(temp);
			}
		}
		
		//setup player
		player.quad_object = new com.kobaj.opengldrawable.Quad.QuadColorShape(0, 64, 64, 0, Color.GRAY, 0);//new com.kobaj.opengldrawable.Quad(R.drawable.ic_launcher);
		player.quad_object.z_pos -= (player.z_plane * Constants.z_modifier);
		player.quad_object.setPos(Functions.screenXToShaderX(player.x_pos), Functions.screenYToShaderY(player.y_pos), player.draw_from);
	
		//set widths and heights for the camera
		left_shader_limit = -(Functions.screenXToShaderX(left_limit) + Constants.ratio);
		right_shader_limit = -(Functions.screenXToShaderX(right_limit) - Constants.ratio);
		
		top_shader_limit = -Functions.screenYToShaderY(top_limit) + Constants.shader_height / 2.0;
		bottom_shader_limit = -Functions.screenYToShaderY(bottom_limit) - Constants.shader_height / 2.0;
	}
	
	public void onDrawObject()
	{
		// player
		player.quad_object.onDrawAmbient();
		
		// objects
		for(LevelObject object: object_list)
			object.onDrawObject();

		// bloom lights
		for(int i = bloom_light_list.size() - 1; i >= 0; i--)
			bloom_light_list.get(i).onDrawObject();
	}
	
	public void onDrawLight()
	{
		for(LevelLight light: light_list)
			light.onDrawLight();
	}
	
	//this method will be deleted.
	public void writeOut()
	{
		player = new LevelObject();
		player.draw_from = com.kobaj.opengldrawable.EnumDrawFrom.top_left;
		player.this_object = EnumLevelObject.test;
		player.x_pos = 0;
		player.y_pos = 100;
		player.z_plane = 5;
		
		object_list = new ArrayList<LevelObject>();
		light_list = new ArrayList<LevelLight>();
		
		LevelObject temp = new LevelObject();
		temp.draw_from = com.kobaj.opengldrawable.EnumDrawFrom.top_left;
		temp.this_object = EnumLevelObject.test;
		temp.x_pos = 200;
		temp.y_pos = 200;
		temp.z_plane = 5;
		temp.active = true;
		
		LevelSpotLight templ = new LevelSpotLight();
		templ.is_bloom = false;
		templ.radius = 100;
		templ.color = Color.WHITE;
		templ.degree = 0;
		templ.x_pos = 0;
		templ.y_pos = 0;
		templ.blur_amount = 0;
		templ.close_width = 10;
		templ.far_width = 100;
		
		LevelPointLight templ2 = new LevelPointLight();
		templ2.is_bloom = false;
		templ2.radius = 100;
		templ2.color = Color.WHITE;
		templ2.x_pos = 0;
		templ2.y_pos = 0;
		templ2.blur_amount = 0;

		LevelAmbientLight templ3 = new LevelAmbientLight();
		templ3.color = Color.WHITE;
		
		object_list.add(temp);
		light_list.add(templ);
		light_list.add(templ2);
		light_list.add(templ3);
	}
}
