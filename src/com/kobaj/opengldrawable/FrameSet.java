package com.kobaj.opengldrawable;

import java.util.ArrayList;

public class FrameSet
{
	public ArrayList<Frame> frame_list;
	
	//recomended fps to play the animation at
	public int rec_fps;
	
	private int current_frame;

	//how to reference.
	public int id;
	
	//assume loop animation forever
	public boolean repeat = true;
	
	public FrameSet()
	{
		if(frame_list == null)
			frame_list = new ArrayList<Frame>();
	}
	
	public void onUpdate(double delta)
	{
		//animation code here.
	}
	
	//check and set the current frame
	public boolean setCurrentFrame(int frame)
	{
		if(frame < frame_list.size())
		{
			current_frame = frame;
			return true;
		}
		
		return false;
	}
	
	//the below should not be called if repeat(loop) is set to true
	
	//should be called when the animation JUST starts (before the frame executes)
	public void onAnimationStart(){}
	
	//should be called when the animation COMPLETELY finishes (after the last frame executes)
	public void onAnimationEnd(){}
}
