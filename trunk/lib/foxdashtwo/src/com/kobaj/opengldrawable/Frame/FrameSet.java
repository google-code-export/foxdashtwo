package com.kobaj.opengldrawable.Frame;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

public class FrameSet
{
	@ElementList
	public ArrayList<Frame> frame_list;
	
	//animation information
	//recomended fps to play the animation at
	@Element
	public double rec_fps; //allow .5fps
	private double frame_timer = 0.0;
	private boolean first_run = false;
	
	//current frame information
	private int current_frame;
	public Frame current_frame_reference;
	
	//assume loop animation forever
	public boolean repeat = true;
	
	//no constructor
	
	//just cause we are awesome, this will return true if 
	//the tex-coordinates need changing
	public boolean onUpdate(double delta)
	{
		if(!first_run && !repeat)
		{
			first_run = true;
			onAnimationStart(); //very first frame
		}
		
		//actual logic
		if (frame_timer < fpsToMs(rec_fps))
		{
			frame_timer += delta;
			return false;
		}
		else
		{
			//reset counter
			frame_timer = 0;
			
			//advanced the frame
			//do stuff depending on frame
			current_frame += 1;
			if(!repeat)
			{
				if(current_frame == frame_list.size()) //last frame
					onAnimationEnd();
			}
			else if(current_frame == frame_list.size())
				current_frame = 0;
			
			//set the reference.
			if(current_frame < frame_list.size())
				current_frame_reference = frame_list.get(current_frame);
			
			//things have changed
			return true;
		}
	}
	
	//check and set the current frame
	public boolean setCurrentFrame(int frame)
	{
		if(frame < frame_list.size())
		{
			current_frame = frame;
			current_frame_reference = frame_list.get(frame);
			return true;
		}
		
		return false;
	}
	
	//while yes this is math, it should not be used anywhere else besides animation
	//thus does not show up in the math class.
	private double fpsToMs(double fps)
	{
		if(fps != 0)
			return 1.0 / fps * 1000.0;
	
		return 0;
	}
	
	//the below should not be called if repeat(loop) is set to true
	
	//should be called when the animation JUST starts (before the frame executes)
	public void onAnimationStart(){}
	
	//should be called when the animation COMPLETELY finishes (after the last frame executes)
	public void onAnimationEnd(){}
}
