package com.kobaj.level;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import android.graphics.RectF;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;

public class LevelEvent
{
	@Element
	public EnumLevelEvent this_event;
	//@Element
	//public com.kobaj.opengldrawable.EnumDrawFrom draw_from;
	@Element
	public int x_pos; //screen coordinates
	@Element
	public int y_pos; //screen coordinates
	@Element
	public int width; //screen
	@Element
	public int height; //screen
	@Element
	private String id; //identifier. 
	public final String getID()
	{
		return id;
	}
	@ElementList
	public ArrayList<String> affected_object_ids;
	
	//my elements
	public RectF my_collision_rect;
	
	//my state
	private boolean active = false;
	private Quad my_draw;
	private double brightness;
	private double keep;
	
	public void onInitialize()
	{
		//bottom left
		my_collision_rect = new RectF(
				(float) Functions.screenXToShaderX(x_pos),
				(float) (Functions.screenYToShaderY(y_pos + height)),
				(float) (Functions.screenXToShaderX(x_pos + width)),
				(float) Functions.screenYToShaderY(y_pos));
		
		//drawable
		//all the different types of level events
		if(this_event == EnumLevelEvent.left_arrow)
		{
			my_draw = new Quad(R.drawable.left_arrow, 112, 200);
			my_draw.setPos(Functions.screenXToShaderX(100), 0, EnumDrawFrom.center);
		}
		else if(this_event == EnumLevelEvent.right_arrow)
		{
			my_draw = new Quad(R.drawable.right_arrow, 112, 200);
			my_draw.setPos(Functions.screenXToShaderX(Constants.width - 100), 0, EnumDrawFrom.center);
		}
		else if(this_event == EnumLevelEvent.up_arrow)
		{
			my_draw = new Quad(R.drawable.up_arrow, 200, 112);
			my_draw.setPos(0, Functions.screenYToShaderY(100), EnumDrawFrom.center);
		}
	}
	
	public EnumLevelEvent checkActivity(double delta, Quad player)
	{	
		active = false;
		for(int i = player.phys_rect_list.size() - 1; i >= 0; i--) 
			if(Functions.equalIntersects(player.phys_rect_list.get(i).main_rect, my_collision_rect))
			{
				active = true;
				//return this_event;
			}
		
		if(active || brightness >= .1)
		{
		keep += Math.toRadians(delta) / 15.0;
		if(keep > Math.PI)
			keep = 0;
		brightness = Math.sin(keep);
		}
		else
		{
			brightness = 0;
			keep = 0;
		}
		
		return EnumLevelEvent.none;
	}
	
	public void onDraw()
	{
		if(my_draw != null)
		{
			//TODO make this an ambient light and not access this directly
			final double old_brightness = Constants.ambient_light.my_brightness;		
			
			if(active || brightness > 0)
			{
				//draw any active quad
				Constants.ambient_light.my_brightness = brightness;
				my_draw.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);
			}
		
		Constants.ambient_light.my_brightness = old_brightness;
		
		}
	}
}
