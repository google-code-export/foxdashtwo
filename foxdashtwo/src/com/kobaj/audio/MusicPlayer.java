package com.kobaj.audio;

import com.kobaj.account_settings.UserSettings;

public class MusicPlayer
{
	private Music music_player;
	private EnumMusicStates current_state = EnumMusicStates.playing;
	
	private int fade_start = 0;
	private int fade_end = 0;
	
	private int next_song = -1;
	
	public double actual_volume = 0.0;
	
	private boolean isLooping = false;
	
	public MusicPlayer(Music music_player)
	{
		this.music_player = music_player;
	}
	
	public boolean isLoaded()
	{
		return music_player.loaded;
	}
	
	public void onUpdate()
	{
		if (!music_player.loaded)
			return;
		
		if (current_state == EnumMusicStates.fade_in)
		{
			final int current_position = music_player.media_player.getCurrentPosition();
			final double calculated_volume = com.kobaj.math.Functions.linearInterpolate(fade_start, fade_end, current_position, 0.0, UserSettings.desired_music_volume);
			actual_volume = calculated_volume;
			music_player.play(music_player.currently_playing, calculated_volume);
			
			if (current_position >= fade_end)
				current_state = EnumMusicStates.playing;
		}
		
		if (current_state == EnumMusicStates.playing)
		{
			if (!isLooping)
			{
				final int fade_length = fade_end - fade_start;
				final int current_position = music_player.media_player.getCurrentPosition();
				final int song_duration = music_player.media_player.getDuration();
				
				if (current_position >= (song_duration - fade_length))
					current_state = EnumMusicStates.fade_out;
			}
		}
		
		if (current_state == EnumMusicStates.fade_out)
		{
			final int current_position = music_player.media_player.getCurrentPosition();
			final double calculated_volume = com.kobaj.math.Functions.linearInterpolate(fade_start, fade_end, current_position, UserSettings.desired_music_volume, 0.0);
			actual_volume = calculated_volume;
			music_player.play(music_player.currently_playing, calculated_volume);
			
			// should we fade in next song?
			if (current_position >= fade_end && next_song != -1)
				start(next_song, fade_end - fade_start, music_player.media_player.isLooping());
			else if (current_position >= fade_end && next_song == -1)
			{
				music_player.stop();
				current_state = EnumMusicStates.stopped;
			}
		}
	}
	
	public void setDesiredVolume(double input)
	{
		if (input < 0)
			input = 0;
		else if (input > 1)
			input = 1;
		
		UserSettings.desired_music_volume = input;
		
		if (current_state == EnumMusicStates.playing)
		{
			music_player.play(music_player.currently_playing, UserSettings.desired_music_volume);
			actual_volume = UserSettings.desired_music_volume;
		}
	}
	
	public int getCurrentPosition()
	{
		if (!music_player.loaded)
			return 0;
		
		return music_player.media_player.getCurrentPosition();
	}
	
	public int getDuration()
	{
		if (!music_player.loaded)
			return 0;
		
		return music_player.media_player.getDuration();
	}
	
	public EnumMusicStates getMusicState()
	{
		return current_state;
	}
	
	// assume want loop with zero fade in
	public void start(int song)
	{
		start(song, 0, true);
	}
	
	// assume we want loop
	public void start(int song, int fade_length)
	{
		start(song, fade_length, true);
	}
	
	// fade_length is in milliseconds, 30000ms is 30 seconds
	public void start(int song, int fade_length, boolean loop)
	{
		if (fade_length < 0)
			fade_length = 0;
		
		fade_start = 0;
		fade_end = fade_start + fade_length;
		checkFadeEnd();
		current_state = EnumMusicStates.fade_in;
		
		music_player.play(song, 0.0);
		music_player.media_player.setLooping(loop);
		isLooping = loop;
		
		next_song = -1;
	}
	
	// assume zero fade
	public void changeSong(int song)
	{
		changeSong(song, 0);
	}
	
	// assume we want previous loop strategy
	public void changeSong(int song, int fade_length)
	{
		changeSong(song, fade_length, music_player.media_player.isLooping());
	}
	
	public void changeSong(int song, int fade_length, boolean loop)
	{
		if (fade_length < 0)
			fade_length = 0;
		
		fade_start = music_player.media_player.getCurrentPosition();
		fade_end = fade_start + fade_length;
		checkFadeEnd();
		current_state = EnumMusicStates.fade_out;
		
		music_player.media_player.setLooping(loop);
		isLooping = loop;
		
		next_song = song;
	}
	
	public void stop()
	{
		stop(0);
	}
	
	public void stop(int fade_length)
	{
		if(fade_length < 0)
			fade_length = 0;
		
		fade_start = music_player.media_player.getCurrentPosition();
		fade_end = fade_start + fade_length;
		checkFadeEnd();
		current_state = EnumMusicStates.fade_out;
		
		next_song = -1;
		
		// something to note, if fade_length is zero, we must shut it down NOW
		// cant wait for update thread, as it may never come.
		if (fade_length == 0)
		{
			music_player.stop();
			current_state = EnumMusicStates.stopped;
		}
	}
	
	private void checkFadeEnd()
	{
		if (!music_player.loaded)
			return;
		
		if (fade_end < 0)
			fade_end = 0;
		
		final int song_duration = music_player.media_player.getDuration();
		if (fade_end > song_duration)
			fade_end = song_duration;
	}
}
