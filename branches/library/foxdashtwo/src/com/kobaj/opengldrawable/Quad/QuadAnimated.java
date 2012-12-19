package com.kobaj.opengldrawable.Quad;

import com.kobaj.opengldrawable.EnumGlobalAnimationList;
import com.kobaj.opengldrawable.Frame.FrameAnimation;
import com.kobaj.opengldrawable.Frame.FrameSet;

public class QuadAnimated extends QuadCompressed
{
	// the all important animation
	private FrameAnimation frame_animation = new FrameAnimation();
	
	// whats currently playing
	private FrameSet currently_playing_frameset_reference; // long names are cool.
	
	// is playing?
	public boolean playing = false;
	
	// screen coordinates
	public QuadAnimated(int texture_resource, int alpha_resource, int animation_resource, int width, int height)
	{
		super(texture_resource, alpha_resource, width, height);
		
		// load in the animation
		frame_animation = com.kobaj.loader.FileHandler.readSerialResource(com.kobaj.math.Constants.resources, animation_resource, frame_animation.getClass());
		
		// every animation must have stop
		this.setAnimation(EnumGlobalAnimationList.stop, 0);
	}
	
	// called to find the text coords
	private void updateTexCoords()
	{
		// translate the frames coordinates
		final float tr_start_x = (float) com.kobaj.math.Functions.linearInterpolate(0.0, square, currently_playing_frameset_reference.current_frame_reference.x_start, 0.0, 1.0);
		final float tr_start_y = (float) com.kobaj.math.Functions.linearInterpolate(0.0, square, currently_playing_frameset_reference.current_frame_reference.y_start, 0.0, 1.0);
		
		final float tr_end_x = (float) com.kobaj.math.Functions.linearInterpolate(0.0, square, currently_playing_frameset_reference.current_frame_reference.x_end, 0.0, 1.0);
		final float tr_end_y = (float) com.kobaj.math.Functions.linearInterpolate(0.0, square, currently_playing_frameset_reference.current_frame_reference.y_end, 0.0, 1.0);
		
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
		if (frame_animation.animation_set.containsKey(id))
		{
			currently_playing_frameset_reference = frame_animation.animation_set.get(id);
			return true;
		}
		
		return false;
	}
	
	// see above, but this allows you to specify what frame to start from
	// other wise it will start from the last frame it was on
	public boolean setAnimation(EnumGlobalAnimationList id, int frame)
	{
		if (setAnimation(id))
		{
			if (currently_playing_frameset_reference.setCurrentFrame(frame))
				return true;
		}
		
		return false;
	}
}
