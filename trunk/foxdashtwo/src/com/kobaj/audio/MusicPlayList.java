package com.kobaj.audio;

import java.util.ArrayList;

import com.kobaj.math.Constants;

//this is an extension of the music player
//it lets you pass in a list of songs
//and how to transition between them
//then it plays them.
public class MusicPlayList
{
	private ArrayList<Integer> play_list;
	private int currently_playing_key = 0;
	
	private int fade_length = 0;
	private boolean loop_list = true;
	
	// set to how long you want to play (say, 30,000 milliseconds (30 seconds) of each song in playlist)
	// or -1 for the whole song.
	private int play_time = -1;
	
	public void onUpdate()
	{
		if(Constants.music_player.getMusicState() != EnumMusicStates.playing)
			return;
		
		int local_play_length = play_time;
		if (play_time == -1)
			local_play_length = Constants.music_player.getDuration();
		
		local_play_length -= fade_length;
		
		final int current_position = Constants.music_player.getCurrentPosition();
		
		//ready to swap to the next song
		if (current_position > local_play_length)
		{
			currently_playing_key += 1;
			
			if (currently_playing_key >= play_list.size())
			{
				if (loop_list)
					currently_playing_key = 0;
				else
				{
					Constants.music_player.stop(fade_length);
					return;
				}
			}
			
			Constants.music_player.changeSong(play_list.get(currently_playing_key), fade_length);
				
		}
	}
	
	//assume we want to play the whole song
	public void setPlayList(int... sound_ids)
	{
		setPlayList(-1, sound_ids);
	}
	
	public void setPlayList(int play_time, int... sound_ids)
	{
		this.play_time = play_time;
		
		if (play_list == null)
			play_list = new ArrayList<Integer>();
		
		play_list.clear();
		
		// this is ok because its just a regular array
		for (int sound_id : sound_ids)
			play_list.add(sound_id);
	}
	
	public void clearPlayList()
	{
		if (play_list == null)
			play_list = new ArrayList<Integer>();
		
		play_list.clear();
		currently_playing_key = 0;
	}
	
	// assume zero fade length
	public void start()
	{
		start(0);
	}
	
	// assume the user wants to play first song
	// and that user does want to loop
	public void start(int fade_length)
	{
		start(0, true);
	}
	
	public void start(int fade_length, boolean loop_list)
	{
		if (fade_length < 0)
			fade_length = 0;
		
		this.fade_length = fade_length;
		this.loop_list = loop_list;
		Constants.music_player.start(play_list.get(currently_playing_key), fade_length, false);
	}
	
	public void stop()
	{
		stop(0);
	}
	
	public void stop(int fade_length)
	{
		this.fade_length = fade_length;
		Constants.music_player.stop(fade_length);
	}
}
