package com.kobaj.math;


public class FPSManager
{
	public int fps = 0;
	private long last_time = 0;
	private long now_time = 0;
	private long delta = 0;
	
	private long reset = 0;
	private final long wait = 1000; //always 1 second
	
	private AverageMaker my_fps_average = new AverageMaker(4);
	
	//call this method at the top of surfacepanel onUpdate
	public void onUpdate(long gameTime)
	{
		now_time = gameTime;
		
		delta = now_time - last_time;
		
		if (reset < wait)
			reset += delta;
		else
		{
			fps = (int) ((1.0 / (delta)) * 1000.0);
			reset = 0;
		}
		
		last_time = now_time;
	}
	
	//pass this variable around to all the other onUpdates
	public double getDelta()
	{
		//return delta;
		return Functions.clamp(100, my_fps_average.calculateAverage(delta), 0);
	}
}
