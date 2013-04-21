package com.kobaj.networking.task;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.SparseArray;

import com.kobaj.loader.FileHandler;
import com.kobaj.loader.RawTextReader;
import com.kobaj.math.Constants;
import com.kobaj.message.download.LevelItem;
import com.kobaj.networking.NetworkManager;

public class TaskUpdateMaps extends AsyncTask<Void, Void, ArrayList<LevelItem>>
{
	public static final String lid = "lid";
	public static final String name = "name";
	public static final String changed = "changed";
	public static final String download_time = "download_time";
	
	public interface finishedLoading
	{
		void onTaskComplete(ArrayList<LevelItem> levels);
	}
	
	private finishedLoading local_callback;
	
	public void setFinishedLoading(finishedLoading event)
	{
		this.local_callback = event;
	}
	
	@Override
	protected void onPostExecute(ArrayList<LevelItem> levels)
	{
		if (local_callback != null)
			local_callback.onTaskComplete(levels);
	}
	
	@Override
	protected ArrayList<LevelItem> doInBackground(Void... params)
	{
		String[] files = FileHandler.getFileList(FileHandler.download_dir);
		
		ArrayList<LevelItem> levels = new ArrayList<LevelItem>();
		for (int i = files.length - 1; i >= 0; i--)
		{
			String full_file = files[i];
			
			String the_level = FileHandler.readTextFile(FileHandler.download_dir, full_file);
			
			if (the_level == null || the_level.equals(Constants.empty))
				continue;
			
			LevelItem temp = new LevelItem();
			
			try
			{
				temp.lid = Integer.valueOf(RawTextReader.findValueInXML(the_level, lid));
				temp.name = RawTextReader.findValueInXML(the_level, name);
				temp.changed = Long.valueOf(RawTextReader.findValueInXML(the_level, changed));
				temp.download_time = Long.valueOf(RawTextReader.findValueInXML(the_level, download_time));
	
				temp.this_state = LevelItem.EnumButtonStates.play;
				
				// finally add and commit
				levels.add(temp);
				
				the_level = null;
			}
			catch (java.lang.NumberFormatException e)
			{
				// could not format, do nothing.
			}
			catch (java.lang.IllegalStateException e)
			{
				// bad download, do nothing.
			}
			
		}
		
		checkUpdates(levels);
		
		return levels;
	}
	
	private void checkUpdates(ArrayList<LevelItem> levels)
	{
		if (levels.size() == 0)
			return;
		
		SparseArray<Long> local_download_times = new SparseArray<Long>();
		
		// here we check if it needs to be updated.
		// because this is already in async mode, we dont need another task :D!
		
		NetworkManager my_manager = new NetworkManager();
		
		HashMap<String, String> url_helper = new HashMap<String, String>();
		url_helper.put(NetworkManager.url_file, NetworkManager.file_game);
		url_helper.put(NetworkManager.url_action, "check_xml_update");
		
		if(Constants.logged_in)
			url_helper.put("uid", String.valueOf(Constants.uid));

		// build a string of lids
		String lid_string = Constants.empty;
		String changed_string = Constants.empty;
		for (int i = levels.size() - 1; i >= 1; i--)
		{
			LevelItem reference = levels.get(i);
			
			lid_string += String.valueOf(reference.lid) + ",";
			changed_string += String.valueOf(reference.changed) + ",";
			
			local_download_times.put(reference.lid, reference.download_time);
		}
		lid_string += String.valueOf(levels.get(0).lid); // properly comma separated
		changed_string += String.valueOf(levels.get(0).changed);
		
		local_download_times.put(levels.get(0).lid, levels.get(0).download_time);
		
		url_helper.put(lid, String.valueOf(lid_string));
		url_helper.put(changed, String.valueOf(changed_string));
		
		String unparsed_return = my_manager.accessNetwork(NetworkManager.genericUrlBuilder(url_helper));
		
		// now lets see what we get back
		try
		{
			JSONObject json = new JSONObject(unparsed_return);
			if (json.getBoolean("success"))
			{
				for (int i = levels.size() - 1; i >= 0; i--)
				{
					LevelItem temp = levels.get(i);
					
					try
					{
						JSONObject lids_n_values = json.getJSONObject(String.valueOf(temp.lid));
						
						if (lids_n_values.getBoolean("needs_update") || local_download_times.get(temp.lid, Constants.force_update_epoch + 1) < Constants.force_update_epoch)
						{
							temp.this_state = LevelItem.EnumButtonStates.update;
						}
						
						temp.current_rateing = lids_n_values.getInt("rating");
						if(lids_n_values.getInt("reporting") != 0)
							temp.reported = true;
					}
					catch (JSONException e)
					{
						// do nothing
					}
				}
			}
		}
		catch (JSONException e)
		{
			// do nothing
		}
	}
}
