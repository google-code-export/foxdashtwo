package com.kobaj.input;

import com.kobaj.input.InputType.InputTypeBase;
import com.kobaj.input.InputType.InputTypeHalfHalf;
import com.kobaj.input.InputType.InputTypeNintendo;
import com.kobaj.math.Constants;

public class GameInputModifier
{
	private final int total = 2;
	private int current_selection = 0;
	private InputTypeBase[] input_types = new InputTypeBase[total];
	
	private int swipes = 0;
	
	public GameInputModifier()
	{
		input_types[0] = new InputTypeHalfHalf();
		input_types[1] = new InputTypeNintendo();
	}
	
	public void onInitialize()
	{
		//loading
		for(int i = 0; i < total; i++)
			input_types[i].onInitialize();
	}
	
	public InputTypeBase getInputType()
	{
		return input_types[current_selection];
	}
	
	// why am I not using enums like usual?
	// I wanted to try out a more modular approach with this.
	public void setInput(int input)
	{
		if(input < total)
			current_selection = input;
	}
	
	// technically this method should be replaced with a less intrusive button
	// but for now this will work.
	public void onUpdate()
	{
		//if we detect a three finger swipe, right to left, we switch input types
		
		if(Constants.input_manager.getTouched(0) && Constants.input_manager.getTouched(1) && Constants.input_manager.getTouched(2))
			swipes += Constants.input_manager.getDeltax(1);
		else
			swipes = 0;
			
		if(swipes < -Constants.input_swipe_sensitivity * Constants.dip_scale || swipes > Constants.input_swipe_sensitivity * Constants.dip_scale)
		{
			current_selection = (current_selection + 1) % total;
			swipes = 0;
		}
	}
	
	public void onDraw()
	{
		input_types[current_selection].onDraw();
	}
}
