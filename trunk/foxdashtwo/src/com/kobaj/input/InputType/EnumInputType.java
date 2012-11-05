package com.kobaj.input.InputType;

public enum EnumInputType
{
	halfhalf, nintendo;
	
	//just an experiment
	/*private EnumInputType[] values;
	private int ordinal = -1;
	
	
	public EnumInputType[] efficient_values()
	{
		if(values == null)
			values = values();
		return values;
	}
	
	public int efficient_ordinal()
	{
		if(ordinal == -1)
			ordinal = ordinal();
		return ordinal;
	}
	
	public EnumInputType getNext()
	{
		efficient_values();
		efficient_ordinal();
		
		return values[(ordinal + 1) % values.length];
	}*/
}
