package com.kobaj.opengldrawable.Tween;

import java.util.ArrayList;

import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;

public class TweenManager
{
	private Quad quad_reference;
	private ArrayList<TweenEvent> tween_events;
	private ArrayList<Integer> tween_times;
	
	private int current_tween_index = 0;
	private double current_time = 0;
	
	/*
	 * intended use TweenHolder(myObject.quad, new TweenEvent(0, 0, Color.white), 1000 //one second new TweenEvent(10,10, Color.white), 1000 new TweenEvent(10,10, Color.blue));
	 */
	public TweenManager(Quad quad_reference, Object... tweenables)
	{
		this.quad_reference = quad_reference;
		
		tween_events = new ArrayList<TweenEvent>();
		tween_times = new ArrayList<Integer>();
		
		// its ok, its an array
		for (Object o : tweenables)
		{
			// sort between our tweens
			if (TweenEvent.class.isAssignableFrom(o.getClass()))
				tween_events.add(TweenEvent.class.cast(o));
			else if (Integer.class.isAssignableFrom(o.getClass()))
				tween_times.add(Integer.class.cast(o));
		}
		
		while (tween_times.size() < tween_events.size())
			tween_times.add(0);
	}
	
	public void reset()
	{
		current_tween_index = 0;
		current_time = 0;
	}
	
	public boolean onUpdate(double delta)
	{
		if (current_tween_index >= tween_events.size() - 1)
			return false;
		
		TweenEvent current_event = tween_events.get(current_tween_index);
		TweenEvent next_event = tween_events.get(current_tween_index + 1);
		int max_time = tween_times.get(current_tween_index);
		current_time += delta;
		
		// tween the position
		if (next_event.event == EnumTweenEvent.move || next_event.event == EnumTweenEvent.move_color || next_event.event == EnumTweenEvent.move_color_rotate
				|| next_event.event == EnumTweenEvent.move_rotate)
		{
			double x_pos = current_event.x_pos;
			if (current_event.x_pos != next_event.x_pos)
				x_pos = Functions.linearInterpolate(0, max_time, current_time, current_event.x_pos, next_event.x_pos);
			double y_pos = current_event.y_pos;
			if (current_event.y_pos != next_event.y_pos)
				y_pos = Functions.linearInterpolate(0, max_time, current_time, current_event.y_pos, next_event.y_pos);
			quad_reference.setXYPos(x_pos, y_pos, EnumDrawFrom.center);
		}
		
		// tween the rotation
		if (next_event.event == EnumTweenEvent.rotate || next_event.event == EnumTweenEvent.move_rotate || next_event.event == EnumTweenEvent.color_rotate
				|| next_event.event == EnumTweenEvent.move_color_rotate)
			if (current_event.degree != next_event.degree)
			{
				double degree = Functions.linearInterpolate(0, max_time, current_time, current_event.degree, next_event.degree);
				quad_reference.setRotationZ(degree);
			}
		
		// tween the color
		if (next_event.event == EnumTweenEvent.color || next_event.event == EnumTweenEvent.color_rotate || next_event.event == EnumTweenEvent.move_color
				|| next_event.event == EnumTweenEvent.move_color_rotate)
			if (current_event.color != next_event.color)
			{
				int color = Functions.linearInterpolateColor(0, max_time, current_time, current_event.color, next_event.color);
				quad_reference.color = color;
			}
		
		// at the very end
		if (current_time > max_time)
		{
			current_time = 0;
			current_tween_index += 1;
		}
		
		return true;
	}
}
