package com.kobaj.math;


public class FPSManager
{
	public int fps = 0;
	private long last_time = 0;
	private long now_time = 0;
	private long delta = 0;
	
	private long reset = 0;
	private final long wait = 1000; //always 1 second
	
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
	
	// averages
	//overall it provides smoother gameplay.
	private final int max = 4;
	private double[] averageList = new double[max];
	//private Queue<Double> averageList = new LinkedList<Double>();
	
	//new method, fakeing our own queue soz we can save on memory
	private void offer(double value)
	{
		//not that slow considering it is 4 elemsent
		for(int i = max - 1; i >= 1; i--)
			averageList[i] = averageList[i-1];
		
		averageList[0] = value;
	}
	
	private double calculateAverage(double newV)
	{	
		offer(newV);
	
		// who knew the whole time I was calculating averages wrong in the original fox dash...woops
		// I got it right this time, I swear!
		double average = 0; 
		
		//this is ok because its a regular array;
		for(double avg: averageList)
			average += avg;
		
		average = average / max;
		
		return average;
	}
	
	//pass this variable around to all the other onUpdates
	public double getDelta()
	{
		//return delta;
		return Functions.clamp(100, calculateAverage(delta), 0);
	}
}
