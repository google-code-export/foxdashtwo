package com.kobaj.audio;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import com.kobaj.math.Constants;

//this is just a very simple music player.
public class Music extends AudioBase
{
	private final String error_tag = "Music Load Error";
	
	protected MediaPlayer media_player;
	
	public int currently_playing = -1;
	public boolean loaded = false;
	
	public Music()
	{
		super();
		media_player = new MediaPlayer();
		media_player.setOnPreparedListener(new OnPreparedListener()
		{
			public void onPrepared(MediaPlayer mp)
			{
				// play
				mp.start();
				loaded = true;
			}
		});
	}
	
	// return true if this is a new song and we can play it.
	public int play(int sound_id, double volume)
	{
		if(sound_id == -1)
			return 0;
		
		boolean is_reset = false;
		
		if (sound_id != currently_playing)
		{
			currently_playing = sound_id;
			media_player.reset(); // make it fresh
			media_player.start();
			
			loaded = false;
			// set the music.
			
			// thanks to http://stackoverflow.com/questions/2969242/problems-with-mediaplayer-raw-resources-stop-and-start
			AssetFileDescriptor afd = Constants.context.getResources().openRawResourceFd(sound_id);
			try
			{
				media_player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
				media_player.prepare();
				afd.close();
			}
			catch (IllegalArgumentException e)
			{
				Log.e(error_tag, "Unable to play audio queue do to exception: " + e.getMessage(), e);
			}
			catch (IllegalStateException e)
			{
				Log.e(error_tag, "Unable to play audio queue do to exception: " + e.getMessage(), e);
			}
			catch (IOException e)
			{
				Log.e(error_tag, "Unable to play audio queue do to exception: " + e.getMessage(), e);
			}
			
			is_reset = true;
		}
		
		// set volume
		float corrected_volume = (float) getCorrectedVolume(volume);
		media_player.setVolume(corrected_volume, corrected_volume);
		
		if(is_reset)
			return 1;
		return 0;
	}
	
	public int play(int sound_id)
	{
		return play(sound_id, 1.0);
	}
	
	public void stop()
	{
		media_player.stop();
		currently_playing = -1;
	}
}
