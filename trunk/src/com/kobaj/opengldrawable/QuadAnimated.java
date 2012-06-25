package com.kobaj.opengldrawable;

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

public class QuadAnimated extends Quad
{
	private HashMap<GlobalAnimationList, FrameSet> animation_set;
	
	//whats currently playing
	private GlobalAnimationList currently_playing;
	
	//is playing?
	public boolean playing = false;
	
	public QuadAnimated(GL10 gl, int texture_resource, int width, int height, int animation_resource)
	{
		super(gl, texture_resource, width, height);
		
		//this is for when loading occures, still not sure how I'm going to handle that.
		//XML handling is weird and slow, but very easy to use and I may use again
		//(I used it in fox dash).
		if(animation_set == null)
			animation_set = new HashMap<GlobalAnimationList, FrameSet>();
		
		//none the less load in animations here
		//for example
		animation_set.put(GlobalAnimationList.stop, new FrameSet());
	}
	
	//called to find the text coords
	public void getTexCoords(/*references here?*/)
	{
		//grab text coords here somehow
	}
	
	//should be called on every update
	public void onUpdate(double delta)
	{
		if(playing)
		{
			//animate
			FrameSet temp = animation_set.get(currently_playing);
			temp.onUpdate(delta);
		}
	}
	
	//returns true if animation can be played
	public boolean setAnimation(GlobalAnimationList id)
	{
		if(animation_set.containsKey(id))
		{
			currently_playing = id;
			return true;
		}
			
		return false;
	}
	
	//see above, but this allows you to specify what frame to start from
	//other wise it will start from the last frame it was on
	public boolean setAnimation(GlobalAnimationList id, int frame)
	{
		if(setAnimation(id))
		{
			if(animation_set.get(id).setCurrentFrame(frame))
				return true;
		}
		
		return false;
	}
}
