package com.kobaj.opengldrawable.Quad;

import com.kobaj.opengldrawable.EnumGlobalAnimationList;
import com.kobaj.opengldrawable.Frame.FrameAnimation;
import com.kobaj.opengldrawable.Frame.FrameSet;

public class QuadAnimated extends QuadCompressed
{
	// the all important animation
	private FrameAnimation frame_animation = new FrameAnimation();
	
	// whats currently playing
	public FrameSet currently_playing_frameset_reference; // long names are cool.
	public EnumGlobalAnimationList currently_playing;
	
	// is playing?
	public boolean playing = true;
	
	// screen coordinates
	public QuadAnimated(int texture_resource, int alpha_resource, int animation_resource, int width, int height)
	{
		this(texture_resource, alpha_resource, animation_resource, width, height, width, height);
	}
	
	public QuadAnimated(int texture_resource, int alpha_resource, int animation_resource, int quad_width, int quad_height, int texture_width, int texture_height)
	{
		super(texture_resource, alpha_resource, quad_width, quad_height);
		
		// load in the animation
		frame_animation = com.kobaj.loader.FileHandler.readSerialResource(com.kobaj.math.Constants.resources, animation_resource, frame_animation.getClass());
		
		// every animation must have stop
		this.setAnimation(EnumGlobalAnimationList.stop, 0, true);
		
		// texture data
		square_width = com.kobaj.math.Functions.nearestPowerOf2(texture_width);
		square_height = com.kobaj.math.Functions.nearestPowerOf2(texture_height);
	}
	
	// called to find the text coords
	private void updateTexCoords()
	{
		// translate the frames coordinates
		float tr_start_x = (float) com.kobaj.math.Functions.linearInterpolate(0.0, square_width, currently_playing_frameset_reference.current_frame_reference.x_start, 0.0, 1.0);
		float tr_start_y = (float) com.kobaj.math.Functions.linearInterpolate(0.0, square_height, currently_playing_frameset_reference.current_frame_reference.y_start, 0.0, 1.0);
		
		float tr_end_x = (float) com.kobaj.math.Functions.linearInterpolate(0.0, square_width, currently_playing_frameset_reference.current_frame_reference.x_end, 0.0, 1.0);
		float tr_end_y = (float) com.kobaj.math.Functions.linearInterpolate(0.0, square_height, currently_playing_frameset_reference.current_frame_reference.y_end, 0.0, 1.0);
		
		complexUpdateTexCoords(tr_start_x, tr_end_x, tr_start_y, tr_end_y);
	}
	
	// should be called on every update
	public void onUpdate(double delta)
	{
		if (playing && currently_playing_frameset_reference != null)
		{
			// animate
			if (currently_playing_frameset_reference.onUpdate(delta))
				updateTexCoords();
		}
	}
	
	// returns true if animation can be played
	public boolean setAnimation(EnumGlobalAnimationList id)
	{
		return setAnimation(id, -1, false, false);
	}
	
	public boolean setAnimation(EnumGlobalAnimationList id, int frame, boolean instant_update)
	{
		return setAnimation(id, frame, instant_update, true);
	}
	
	// if animation is already set, then continue
	// otherwise force an instant update to the frame number
	public boolean setAnimation(EnumGlobalAnimationList id, int frame, boolean instant_update, boolean repeat)
	{
		if(frame_animation.animation_set.containsKey(id))
		{
			if(currently_playing != id)
			{
				currently_playing = id;
				currently_playing_frameset_reference = frame_animation.animation_set.get(id);
					
				if (frame >= 0)
					currently_playing_frameset_reference.setCurrentFrame(frame);
				
				if(instant_update)
					updateTexCoords();
				
				if(!repeat)
				{
					// reset the frameset
					currently_playing_frameset_reference.animation_complete = false;
					currently_playing_frameset_reference.animation_started = false;
				}
				
				currently_playing_frameset_reference.repeat = repeat;
				
				return true;
			}
		}
		
		return false;
	}
}
