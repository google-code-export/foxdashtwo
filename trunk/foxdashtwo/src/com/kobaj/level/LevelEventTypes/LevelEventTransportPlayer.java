package com.kobaj.level.LevelEventTypes;

import com.kobaj.level.EnumLevelEvent;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;

public class LevelEventTransportPlayer extends LevelEventBase
{
	private double x_destination;
	private double y_destination;
	
	private double x_start;
	private double y_start;
	
	private boolean teleporting = false;
	private double time_total = 4000; //4 seconds
	private double time_elapsed = 0;
	
	public LevelEventTransportPlayer(EnumLevelEvent type)
	{
		super(type);
	}
	
	// these are in screen coords
	public void setTransportTo(double x, double y)
	{
		x_destination = x;
		y_destination = y;
	}

	@Override
	public void onInitialize()
	{
		
	}

	@Override
	public void onUpdate(double delta, Quad player, boolean active)
	{
		if(active && !teleporting)
		{
			teleporting = true;
			x_start = player.getXPos();
			y_start = player.getYPos();
		}
		
		if(teleporting)
		{
			time_elapsed += delta;
			
			double x_current = Functions.linearInterpolate(0, time_total, time_elapsed, x_start, x_destination);
			double y_current = Functions.linearInterpolate(0, time_total, time_elapsed, y_start, y_destination);
			
			player.x_acc = 0;
			player.y_acc = 0;
			
			player.x_vel = 0;
			player.y_vel = 0;
			
			player.z_pos = -(1 + 0 * Constants.z_modifier);
			player.setPos(x_current, y_current, EnumDrawFrom.center);
			
			if(time_elapsed >= time_total)
			{
				player.z_pos = -(1 + 5 * Constants.z_modifier);
				time_elapsed = 0;
				teleporting = false;
			}
		}
	}

	@Override
	public void onDraw()
	{
		// TODO Auto-generated method stub
		
	}
	
}
