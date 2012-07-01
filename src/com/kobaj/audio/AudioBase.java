package com.kobaj.audio;

import android.content.Context;
import android.media.AudioManager;

public abstract class AudioBase
{
	protected AudioManager audio_manager;
	
	public AudioBase()
	{
	    audio_manager = (AudioManager)com.kobaj.math.Constants.context.getSystemService(Context.AUDIO_SERVICE);	
	}
	
	//use this to ensure volumes are what the user specified
	protected double getCorrectedVolume(double volume)
	{
		volume = com.kobaj.math.Functions.clamp(.99999, volume, .00001);
		
		double streamVolume = audio_manager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume / audio_manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * volume;
		
		return streamVolume;
	}
	
	abstract boolean play(int sound_id, double volume);
}
