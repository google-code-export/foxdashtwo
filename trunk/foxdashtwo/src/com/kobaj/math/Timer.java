package com.kobaj.math;

public class Timer
{
	// will trigger every x miliseconds
	
	private double total_time = 0;
	private double current_time = 0;
	
	public int trigger_count = 0;
	
	public Timer(double ms)
	{
		total_time = ms;
	}
	
	public boolean onUpdate(double delta)
	{
		current_time += delta;
		
		if(current_time > total_time)
		{
			trigger_count++;
			
			current_time = 0;
			return true;
		}
		
		return false;
	}
}
