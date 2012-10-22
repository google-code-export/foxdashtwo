package com.kobaj.audio;

import android.media.MediaPlayer;

//this is just a very simple music player.
public class Music extends AudioBase
{
	protected MediaPlayer media_player;
	
	public int currently_playing = -1;
	
	public Music()
	{
		super();
		media_player = new MediaPlayer();
	}

	//return true if this is a new song and we can play it.
	@Override
	public boolean play(int sound_id, double volume)
	{
		boolean is_reset = false;
		
		if(sound_id != currently_playing)
		{
			currently_playing = sound_id;
			media_player.reset(); // make it fresh
			
			media_player = MediaPlayer.create(com.kobaj.math.Constants.context, sound_id); // set the music.
			
			//play
			media_player.start();
			
			is_reset = true;
		}
		
		//set volume
		float corrected_volume = (float) getCorrectedVolume(volume);
		media_player.setVolume(corrected_volume, corrected_volume);
		
		if(is_reset)
			return true;
		
		return false;
	}
	
	public boolean play(int sound_id)
	{
		return play(sound_id, 1.0);
	}
	
	public void stop()
	{
		media_player.stop();
		currently_playing = -1;
	}
}
