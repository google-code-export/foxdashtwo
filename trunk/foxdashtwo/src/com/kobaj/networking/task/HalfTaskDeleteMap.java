package com.kobaj.networking.task;

public class HalfTaskDeleteMap
{
	public interface FinishedDeleteing
	{
		void onDeleteCompleted();
	}
	
	private FinishedDeleteing local_callback;
	
	public void setFinishedDeleteing(FinishedDeleteing finishedDeleteing)
	{
		local_callback = finishedDeleteing;
	}
	
	public void onFinishedDeleteing()
	{
		local_callback.onDeleteCompleted();
	}
}
