package com.kobaj.audio;

import java.util.ArrayList;

//this is an extension of the music player
//it lets you pass in a list of songs
//and how to transition between them
//then it plays them.
public class MusicPlayList
{
	private ArrayList<Integer> play_list;
	private int currently_playing_key = 0;
	
	//store a reference to the player
	//I know, I love statics, but this just doesnt go there.
	private Music music_player;
	
	//how to play the list.
	public double volume = 1.0;
	public boolean should_loop = true;
	
	// the below is in milliseconds
	public int transition_fade_time = 10000;
	//set to how long you want to play (say, 30,000 milliseconds (30 seconds) of each song in playlist)
	//or -1 for the whole song.
	public int play_time = 30000;
	
	private EnumMusicStates current_state = EnumMusicStates.playing;
	
	public MusicPlayList(Music music_player)
	{
		this.music_player = music_player;
	}
	
	public void setPlayList(int... sound_ids)
	{
		if(play_list == null)
			play_list = new ArrayList<Integer>();
		
		play_list.clear();
		
		for(int sound_id: sound_ids)
			play_list.add(sound_id);
	}
	
	public void onUpdate()
	{
		if(music_player.currently_playing != -1)
		{
			if(current_state == EnumMusicStates.fade_out)
			{
				//lower the volume
				final double end_duration = get_real_duration();
				final double start_duration = end_duration - transition_fade_time;
				
				final double calculated_volume =  com.kobaj.math.Functions.linearInterpolate(start_duration, end_duration, music_player.media_player.getCurrentPosition(), volume, 0.0);
				music_player.play(music_player.currently_playing, calculated_volume);
				
				//if volume is 0, we are done fading out.
				if(calculated_volume <= 0.0)
				{
					currently_playing_key += 1;
					
					//see if we are at the end of the list
					if(currently_playing_key == play_list.size())
					{
						if(!should_loop)
						{
							//stop the music
							music_player.stop();
							return;
						}
						else
							currently_playing_key = 0;
					}
					
					//keep going
					current_state = EnumMusicStates.fade_in;
					music_player.play(play_list.get(currently_playing_key), 0.0);
				}
			}
			
			else if(current_state == EnumMusicStates.playing)
			{
				//see what current time is in relation to duration and play_time
				final int duration = get_real_duration() - transition_fade_time;
				
				final int current_time = music_player.media_player.getCurrentPosition();
				
				//if its time to fade out, make it so!
				if(current_time >= duration)
					current_state = EnumMusicStates.fade_out;
			}
			
			else if(current_state == EnumMusicStates.fade_in)
			{	
				//simply fade in. real easy. :)
				double calculated_volume = com.kobaj.math.Functions.linearInterpolate(0, transition_fade_time, music_player.media_player.getCurrentPosition(), 0.0, volume);
				
				if(calculated_volume == volume)
				{
					current_state = EnumMusicStates.playing;
					return;
				}
					
				music_player.play(music_player.currently_playing, calculated_volume);
			}
		}
	}
	
	private int get_real_duration()
	{
		int duration = music_player.media_player.getDuration();
		
		if(play_time != -1 && play_time < duration)
			duration = play_time;
				
		return duration;
	}
	
	//returns true on success
	public boolean start()
	{
		if(play_list.size() == 0)
			return false;
		
		if(music_player.media_player.isPlaying())
		{
			//smoothly transition out of what we are currently playing
			current_state = EnumMusicStates.fade_out;
		}
		else
		{
			//start playing the new playlist.	
			current_state = EnumMusicStates.fade_in;
			music_player.play(play_list.get(0), 0.0);
		}
		
		return true;
	}
	
	public void fadeStop()
	{
		current_state = EnumMusicStates.fade_out;
		play_list.clear();
	}
	
	public void stop()
	{
		play_list.clear();
		music_player.stop();
	}
}
