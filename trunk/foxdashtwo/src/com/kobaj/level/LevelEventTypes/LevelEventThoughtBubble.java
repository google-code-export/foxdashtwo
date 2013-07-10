package com.kobaj.level.LevelEventTypes;

import java.util.ArrayList;

import android.annotation.SuppressLint;

import com.kobaj.level.Level;
import com.kobaj.math.Constants;

public class LevelEventThoughtBubble extends LevelEventBase
{
	private int x_pos = 0; // screen coordinates
	private int y_pos = 0;
	
	private double player_x; // shader coordinates
	private double player_y;
	
	private boolean activated = false;
	private boolean finished = false;
	
	private int string_count;
	
	private int timer = 0;
	private int timer_limit = 5000; // 5 seconds
	private int current_index = 0;
	
	private ThoughtBubbleHelper[] thought_bubbles;
	
	public LevelEventThoughtBubble(EnumLevelEvent type)
	{
		super(type);
		
		x_pos = (int) (Constants.width / 2.0);
		y_pos = 75;
	}
	
	@Override
	public void onInitialize(final Level level, final ArrayList<String> affected_strings)
	{
		super.onInitialize(level, affected_strings);
		
		string_count = affected_strings.size();
		if (string_count > 0)
		{
			thought_bubbles = new ThoughtBubbleHelper[string_count];
			
			for (int i = 0; i < string_count; i++)
			{
				thought_bubbles[i] = new ThoughtBubbleHelper(x_pos, y_pos, affected_strings.get(i));
				thought_bubbles[i].onInitialize();
			}
		}
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		for (int i = 0; i < string_count; i++)
			thought_bubbles[i].onUnInitalize();
	}
	
	// shader coords
	public void setPlayerPosRelative(double player_screen_x, double player_screen_y)
	{
		player_x = player_screen_x;
		player_y = player_screen_y;
	}
	
	@Override
	public void onUpdate(double delta, boolean active)
	{
		if (string_count <= 0 || finished)
			return;
		
		if (!activated)
		{
			if (active)
				activated = true;
		}
		
		if (activated && current_index < string_count)
		{
			timer += delta;
			if (timer > timer_limit)
			{
				current_index++;
				timer = 0;
			}
		}
		
		if (activated)
		{
			for (int i = 0; i < string_count; i++)
			{
				boolean child_active = false;
				if (i == current_index)
					child_active = true;
				
				thought_bubbles[i].onUpdate(delta, child_active, player_x, player_y);
			}
		
		if (current_index == string_count)
			if (thought_bubbles[string_count - 1].finished())
				finished = true;
		}
	}
	
	@SuppressLint("WrongCall")
	@Override
	public void onDraw()
	{
		if (string_count <= 0 || finished)
			return;
		
		if (activated)
			for (int i = 0; i < string_count; i++)
				thought_bubbles[i].onDraw();
	}
}
