package com.kobaj.audio;

public abstract class AudioBase
{
	// use this to ensure volumes are what the user specified
	protected double getCorrectedVolume(double stream_volume)
	{
		// same for every device
		stream_volume = com.kobaj.math.Functions.clamp(.99999, stream_volume, .00001);
		
		// multiply by 100 and turn it into an int
		// this will make our volume scale into 100 discrete units
		final int max_volume = 100;
		final int current_volume = (int) (100.0 * stream_volume);
		
		// thanks to http://stackoverflow.com/questions/5215459/android-mediaplayer-setvolume-function
		float log1 = (float) (Math.log(max_volume - current_volume) / Math.log(max_volume));
		
		return 1.0 - log1;
	}
	
	abstract int play(int sound_id);
}
