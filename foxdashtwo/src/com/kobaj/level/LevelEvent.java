package com.kobaj.level;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import com.kobaj.level.LevelEventTypes.LevelEventArrows;
import com.kobaj.level.LevelEventTypes.LevelEventBase;
import com.kobaj.level.LevelEventTypes.LevelEventTransportPlayer;
import com.kobaj.math.Functions;
import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.Quad.Quad;

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
	public ArrayList<String> affected_object_strings;
	
	// my elements
	public RectF my_collision_rect;
	
	public LevelEventBase my_possible_event;
	
	public void onInitialize()
	{
		// bottom left
		my_collision_rect = new RectF((float) Functions.screenXToShaderX(x_pos), (float) (Functions.screenYToShaderY(y_pos + height)), (float) (Functions.screenXToShaderX(x_pos + width)),
				(float) Functions.screenYToShaderY(y_pos));
		
		if(this_event == EnumLevelEvent.left_arrow ||
				this_event == EnumLevelEvent.right_arrow ||
				this_event == EnumLevelEvent.up_arrow)
			my_possible_event = new LevelEventArrows(this_event);
		else if(this_event == EnumLevelEvent.send_to_start)
		{
			my_possible_event = new LevelEventTransportPlayer(this_event);
		}
		
		if(my_possible_event != null)
			my_possible_event.onInitialize();
	}
	
	public void onUpdate(double delta, Quad player)
	{
		boolean active = false;
		for (int i = player.phys_rect_list.size() - 1; i >= 0; i--)
			if (Functions.equalIntersects(player.phys_rect_list.get(i).main_rect, my_collision_rect))
			{
				active = true;
				break;
			}
		
		if(my_possible_event != null)
			my_possible_event.onUpdate(delta, player, active);
	}
	
	public void onDraw()
	{
		if(my_possible_event != null)
			my_possible_event.onDraw();
	}
}
