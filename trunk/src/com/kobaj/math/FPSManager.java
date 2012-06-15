package com.kobaj.math;

import java.util.LinkedList;
import java.util.Queue;

public class FPSManager
{
	private int fps = 0;
	private long lastTime = 0;
	private long nowTime = 0;
	private long delta = 0;
	
	private long reset = 0;
	private final long wait = 1000;
	
	public FPSManager()
	{
		;// blank
	}
	
	//call this method at the top of surfacepanel onUpdate
	public void onUpdate(long gameTime)
	{
		nowTime = gameTime;
		
		delta = nowTime - lastTime;
		
		if (reset < wait)
			reset += delta;
		else
		{
			fps = (int) ((1.0 / (delta)) * 1000.0);
			reset = 0;
		}
		
		lastTime = nowTime;
	}
	
	//ever need to know fps
	public int getFPS()
	{
		return fps;
	}
	
	// averages
	//these help prevent irregularities, such as garbage collection.
	//overall it provides smoother gameplay.
	private int max = 4;
	private Queue<Double> averageList = new LinkedList<Double>();
	private double average = 0;
	
	private double calculateaverage(double newV)
	{
		averageList.offer(newV);
		
		if (averageList.size() > max)
			averageList.poll();
		
		for(double it: averageList)
			average += it;
		
		average = average / averageList.size();
		
		return average;
	}
	
	//pass this variable around to all the other onUpdates
	public double getDelta()
	{
		return Functions.clamp(100, calculateaverage(delta), 0);
	}
}
