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
	public ArrayList<LevelAmbientLight> light_list; //all lights including blooms
	
	ArrayList<LevelPointLight> bloom_light_list = new ArrayList<LevelPointLight>(); //only blooms
	
	@Element
	public LevelObject player;
	
	@ElementList
	public ArrayList<LevelEvent> event_list;
	
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
		for(int i = event_list.size() - 1; i >= 0; i--)
			event_list.get(i).onInitialize();
		
		//setup player
		player.quad_object = new com.kobaj.opengldrawable.Quad.QuadColorShape(0, 64, 64, 0, Color.GRAY, 0);
		player.quad_object.z_pos -= (player.z_plane * Constants.z_modifier);
		player.quad_object.setPos(Functions.screenXToShaderX(player.x_pos), Functions.screenYToShaderY(player.y_pos), player.draw_from);
	
		//set widths and heights for the camera
		left_shader_limit = (Functions.screenXToShaderX(left_limit) + Constants.ratio);
		right_shader_limit = (Functions.screenXToShaderX(right_limit) - Constants.ratio);
		
		top_shader_limit = Functions.screenYToShaderY(top_limit) - Constants.shader_height / 2.0;
		bottom_shader_limit = Functions.screenYToShaderY(bottom_limit) + Constants.shader_height / 2.0;
	}
	
	public void onUpdate(double delta)
	{
		for(int i = light_list.size() - 1; i >= 0; i--)
			light_list.get(i).onUpdate(delta);
		
		for(int i = event_list.size() - 1; i >= 0; i--)
			event_list.get(i).checkActivity(delta, player.quad_object);
	}
	
	public void onDrawObject()
	{
		// player
		player.quad_object.onDrawAmbient();
		
		// objects
		for(int i = object_list.size() - 1; i >= 0; i--)
			object_list.get(i).onDrawObject();

		// bloom lights
		for(int i = bloom_light_list.size() - 1; i >= 0; i--)
			bloom_light_list.get(i).onDrawObject();
	}
	
	public void onDrawLight()
	{
		for(int i = light_list.size() - 1; i >= 0; i--)
			light_list.get(i).onDrawLight();
	}
	
	public void onDrawConstant()
	{
		for(int i = event_list.size() - 1; i >= 0; i--)
			event_list.get(i).onDraw();
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
		player.active = true;
		
		//initialize everything
		object_list = new ArrayList<LevelObject>();
		light_list = new ArrayList<LevelAmbientLight>();
		event_list = new ArrayList<LevelEvent>();
		
		//make everything
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
		templ.active = true;
		
		LevelPointLight templ2 = new LevelPointLight();
		templ2.is_bloom = false;
		templ2.radius = 100;
		templ2.color = Color.WHITE;
		templ2.x_pos = 0;
		templ2.y_pos = 0;
		templ2.blur_amount = 0;
		templ2.active = true;

		LevelAmbientLight templ3 = new LevelAmbientLight();
		templ3.color = Color.WHITE;
		templ3.active = true;
		
		LevelEvent tempe = new LevelEvent();
		tempe.height = 200;
		tempe.width = 600;
		tempe.affected_object_ids = new ArrayList<String>();
		tempe.affected_object_ids.add("empty");
		tempe.x_pos = 0;
		tempe.y_pos = 0;
		
		//fill everything in
		object_list.add(temp);
		light_list.add(templ);
		light_list.add(templ2);
		light_list.add(templ3);
		event_list.add(tempe);
	}
}
