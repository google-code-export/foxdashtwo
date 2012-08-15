package com.kobaj.opengldrawable;

public class QuadAnimated extends Quad
{
	//the all important animation
	private FrameAnimation frame_animation = new FrameAnimation();
	
	//whats currently playing
	private FrameSet currently_playing_frameset_reference; //long names are cool.
	
	//is playing?
	public boolean playing = false;
	
	public QuadAnimated(int texture_resource, int animation_resource)
	{
		super(texture_resource);
			
		//load in the animation
		frame_animation = com.kobaj.loader.XMLHandler.readSerialFile(com.kobaj.math.Constants.resources, animation_resource, frame_animation.getClass());
	
		//every animation must have stop
		this.setAnimation(EnumGlobalAnimationList.stop, 0);
	}
	
	//called to find the text coords
	private void updateTexCoords()
	{
		//translate the frames coordinates
		final float tr_start_x = (float) com.kobaj.math.Functions.linearInterpolate(0.0, square, currently_playing_frameset_reference.current_frame_reference.start_x, 0.0, 1.0);
		final float tr_start_y = (float) com.kobaj.math.Functions.linearInterpolate(0.0, square, currently_playing_frameset_reference.current_frame_reference.start_y, 0.0, 1.0);
		
		final float tr_end_x = (float) com.kobaj.math.Functions.linearInterpolate(0.0, square, currently_playing_frameset_reference.current_frame_reference.end_x, 0.0, 1.0);
		final float tr_end_y = (float) com.kobaj.math.Functions.linearInterpolate(0.0, square, currently_playing_frameset_reference.current_frame_reference.end_y, 0.0, 1.0);
		
		complexUpdateTexCoords(tr_start_x, tr_end_x, tr_start_y, tr_end_y);
	}
	
	//should be called on every update
	public void onUpdate(double delta)
	{
		if(playing && currently_playing_frameset_reference != null)
		{
			//animate
			if(currently_playing_frameset_reference.onUpdate(delta))
				updateTexCoords();
		}
	}
	
	//returns true if animation can be played
	public boolean setAnimation(EnumGlobalAnimationList id)
	{
		if(frame_animation.animation_set.containsKey(id))
		{
			currently_playing_frameset_reference = frame_animation.animation_set.get(id);
			return true;
		}
			
		return false;
	}
	
	//see above, but this allows you to specify what frame to start from
	//other wise it will start from the last frame it was on
	public boolean setAnimation(EnumGlobalAnimationList id, int frame)
	{
		if(setAnimation(id))
		{
			if(currently_playing_frameset_reference.setCurrentFrame(frame))
				return true;
		}
		
		return false;
	}
}
