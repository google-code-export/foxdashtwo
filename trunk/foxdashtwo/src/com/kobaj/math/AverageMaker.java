package com.kobaj.math;

public class AverageMaker
{
	// averages
	// overall it provides smoother gameplay.
	private int max = 4;
	private double[] averageList;
	
	public AverageMaker(int max)
	{
		if(max > 0)
			this.max = max;
		averageList = new double[max];
	}
	
	// new method, fakeing our own queue soz we can save on memory
	public void offer(double value)
	{
		// not that slow considering it is 4 elemsent
		for (int i = max - 1; i >= 1; i--)
			averageList[i] = averageList[i - 1];
		
		averageList[0] = value;
	}
	
	public double calculateAverage(double my_offer)
	{
		offer(my_offer);
		
		return calculateAverage();
	}
	
	public double calculateAverage()
	{
		// who knew the whole time I was calculating averages wrong in the original fox dash...woops
		// I got it right this time, I swear!
		double average = 0;
		
		// this is ok because its a regular array;
		for (double avg : averageList)
			average += avg;
		
		average = average / max;
		
		return average;
	}
}
