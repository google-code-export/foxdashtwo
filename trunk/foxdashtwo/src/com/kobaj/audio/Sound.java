package com.kobaj.audio;

//help from
//http://www.droidnova.com/creating-sound-effects-in-android-part-1,570.html

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

//this class is for playing sounds overtop of music
//like gunshots, or foot steps or something.
public class Sound extends AudioBase
{	
	private SoundPool sound_pool;
	private SparseIntArray sound_pool_map;
	
	public Sound()
	{
		super();
		
		sound_pool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
	    sound_pool_map = new SparseIntArray();
	}
	
	//pass in the R value
	public void addSound(int sound_id)
	{
	    sound_pool_map.put(sound_id, sound_pool.load(com.kobaj.math.Constants.context, sound_id, 1));
	}
	
	//for volume: 1 is loud, 0 is quiet
	//for loop_count: it is zero index based ;). -1 for infinite loops
	public boolean play(int sound_id, double volume, int loop_count)
	{
		int sound_to_be_played = sound_pool_map.get(sound_id);
		
		//see if it is contained in map
		if(sound_to_be_played != 0)
		{
			float correct_volume = (float) getCorrectedVolume(volume);
			
			sound_pool.play(sound_pool_map.get(sound_id), correct_volume, correct_volume, 1, loop_count, 1f);
			return true;
		}
		
		return false;
	}
	
	public boolean play(int sound_id, double volume)
	{
		return play(sound_id, volume, 0);
	}
	
	public boolean play(int sound_id)
	{
		return play(sound_id, 1.0, 0);
	}
}
