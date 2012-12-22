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

public class GameInputModifier
{
	private HashMap<EnumInputType, InputTypeBase> input_types = new HashMap<EnumInputType, InputTypeBase>();
	
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
	
	public void onUnInitialize()
	{
		Iterator<Entry<EnumInputType, InputTypeBase>> it = input_types.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<EnumInputType, InputTypeBase> pairs = (Map.Entry<EnumInputType, InputTypeBase>) it.next();
			pairs.getValue().onUnInitialize();
		}
	}
	
	public InputTypeBase getInputType()
	{
		return input_types.get(UserSettings.active_input_type);
	}
	
	public void onDraw()
	{
		input_types.get(UserSettings.active_input_type).onDraw();
	}
}
