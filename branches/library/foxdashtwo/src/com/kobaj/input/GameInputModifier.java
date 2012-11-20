package com.kobaj.input;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.kobaj.foxdashtwo.UserSettings;
import com.kobaj.input.InputType.EnumInputType;
import com.kobaj.input.InputType.InputTypeBase;
import com.kobaj.input.InputType.InputTypeHalfHalf;
import com.kobaj.input.InputType.InputTypeNintendo;
import com.kobaj.math.Constants;

public class GameInputModifier
{
	private HashMap<EnumInputType, InputTypeBase> input_types = new HashMap<EnumInputType, InputTypeBase>();
	
	private int swipes = 0;
	
	public GameInputModifier()
	{
		input_types.put(EnumInputType.halfhalf, new InputTypeHalfHalf());
		input_types.put(EnumInputType.nintendo, new InputTypeNintendo());
	}
	
	public void onInitialize()
	{
		// loading
		// http://stackoverflow.com/questions/1066589/java-iterate-through-hashmap
		Iterator<Entry<EnumInputType, InputTypeBase>> it = input_types.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<EnumInputType, InputTypeBase> pairs = (Map.Entry<EnumInputType, InputTypeBase>) it.next();
			pairs.getValue().onInitialize();
		}
	}
	
	public InputTypeBase getInputType()
	{
		return input_types.get(UserSettings.active_input_type);
	}
	
	// technically this method should be replaced with a less intrusive button
	// but for now this will work.
	public void onUpdate()
	{
		// if we detect a three finger swipe, right to left, we switch input types
		
		if (Constants.input_manager.getTouched(0) && Constants.input_manager.getTouched(1) && Constants.input_manager.getTouched(2))
			swipes += Constants.input_manager.getDeltax(1);
		else
			swipes = 0;
		
		if (swipes < -Constants.input_swipe_sensitivity * Constants.dip_scale || swipes > Constants.input_swipe_sensitivity * Constants.dip_scale)
		{
			if(UserSettings.active_input_type == EnumInputType.halfhalf)
				UserSettings.active_input_type = EnumInputType.nintendo;
			else
				UserSettings.active_input_type = EnumInputType.halfhalf;

			swipes = 0;
		}
	}
	
	public void onDraw()
	{
		input_types.get(UserSettings.active_input_type).onDraw();
	}
}
