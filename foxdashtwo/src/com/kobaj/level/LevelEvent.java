package com.kobaj.level;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import com.kobaj.level.LevelEventTypes.EnumLevelEvent;
import com.kobaj.level.LevelEventTypes.LevelEventActive;
import com.kobaj.level.LevelEventTypes.LevelEventArrows;
import com.kobaj.level.LevelEventTypes.LevelEventBase;
import com.kobaj.level.LevelEventTypes.LevelEventNextLevel;
import com.kobaj.level.LevelEventTypes.LevelEventTransportPlayer;
import com.kobaj.level.LevelTypeLight.LevelAmbientLight;
import com.kobaj.math.Functions;
import com.kobaj.math.android.RectF;

public class LevelEvent
{
	@Element
	public EnumLevelEvent this_event;
	// @Element
	// public com.kobaj.opengldrawable.EnumDrawFrom draw_from;
	@Element
	public double x_pos; // screen coordinates
	@Element
	public double y_pos; // screen coordinates
	@Element
	public double width; // screen
	@Element
	public double height; // screen
	@Element
	public String id = "unset"; // identifier.
	
	@ElementList
	public ArrayList<String> id_strings;
	
	// my elements
	public RectF my_collision_rect;
	public LevelEventBase my_possible_event;
	
	protected LevelObject player_cache;
	
	public void onInitialize(LevelObject player, ArrayList<LevelObject> objects, ArrayList<LevelAmbientLight> lights)
	{		
		//nice reference to our player
		player_cache = player;
		
		// bottom left
		my_collision_rect = new RectF((float) Functions.screenXToShaderX(x_pos), (float) (Functions.screenYToShaderY(y_pos + height)), (float) (Functions.screenXToShaderX(x_pos + width)),
				(float) Functions.screenYToShaderY(y_pos));
		
		if (this_event == EnumLevelEvent.left_arrow || this_event == EnumLevelEvent.right_arrow || this_event == EnumLevelEvent.up_arrow)
			my_possible_event = new LevelEventArrows(this_event);
		else if (this_event == EnumLevelEvent.send_to_start)
			my_possible_event = new LevelEventTransportPlayer(this_event);
		else if (this_event == EnumLevelEvent.active_off ||
				 this_event == EnumLevelEvent.active_on ||
				 this_event == EnumLevelEvent.active_anti_touch ||
				 this_event == EnumLevelEvent.active_touch ||
				 this_event == EnumLevelEvent.active_toggle)
			my_possible_event = new LevelEventActive(this_event);
		else if(this_event == EnumLevelEvent.next_level)
		{
			LevelEventNextLevel temp = new LevelEventNextLevel(this_event);
			temp.setNextLevel(id_strings.get(0));
			my_possible_event = temp;
		}
		
		if (my_possible_event != null)
			my_possible_event.onInitialize(player, objects, lights, id_strings);
	}
	
	public void onUpdate(double delta)
	{
		if (my_possible_event != null)
		{
			boolean active = false;
			for (int i = player_cache.quad_object.phys_rect_list.size() - 1; i >= 0; i--)
				if (Functions.equalIntersects(player_cache.quad_object.phys_rect_list.get(i).main_rect, my_collision_rect))
				{
					active = true;
					break;
				}
		
			my_possible_event.onUpdate(delta, active);
		}
	}
	
	public void onDraw()
	{
		if (my_possible_event != null)
			my_possible_event.onDraw();
	}
}
