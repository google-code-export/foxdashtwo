package com.kobaj.networking;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.kobaj.loader.FileHandler;
import com.kobaj.loader.RawTextReader;
import com.kobaj.math.Constants;
import com.kobaj.message.download.LevelItem;

public class TaskGetDownloadedMaps extends AsyncTask<Void, Void, ArrayList<LevelItem>>
{
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
			
			if(the_level == null || the_level.equals(Constants.empty))
				continue;
			
			if (the_level != null)
			{
				LevelItem temp = new LevelItem();
				
				try
				{
					temp.lid = Integer.valueOf(RawTextReader.findValueInXML(the_level, "lid"));
					temp.name = RawTextReader.findValueInXML(the_level, "name");
					temp.changed_time = Long.valueOf(RawTextReader.findValueInXML(the_level, "changed"));
				
					temp.this_state = LevelItem.EnumButtonStates.play;
				
					// finally add and commit
					levels.add(temp);
				
					the_level = null;
				}
				catch(java.lang.IllegalStateException e)
				{
					//bad download, do nothing.
				}
			}
		}
		
		checkUpdates(levels);
		
		return levels;
	}
	
	private void checkUpdates(ArrayList<LevelItem> levels)
	{
		if(levels.size() == 0)
			return;
		
		// here we check if it needs to be updated.
		// because this is already in async mode, we dont need another task :D!
		
		NetworkManager my_manager = new NetworkManager();
		
		HashMap<String, String> url_helper = new HashMap<String, String>();
		url_helper.put(NetworkManager.url_file, "shared.php");
		url_helper.put(NetworkManager.url_action, "check_xml_update");
		
		// build a string of lids
		String lid_string = "";
		for (int i = levels.size() - 1; i >= 1; i--)
			lid_string += String.valueOf(levels.get(i).lid) + ",";
		lid_string += String.valueOf(levels.get(0).lid);
		
		String changed_string = "";
		for (int i = levels.size() - 1; i >= 1; i--)
			changed_string += String.valueOf(levels.get(i).changed_time) + ",";
		changed_string += String.valueOf(levels.get(0).changed_time);
		
		url_helper.put("lid", String.valueOf(lid_string));
		url_helper.put("changed_time", String.valueOf(changed_string));
		
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
					
						if (lids_n_values.getBoolean("needs_update"))
						{
							temp.this_state = LevelItem.EnumButtonStates.update;
						}
					}
					catch(JSONException e)
					{
						//do nothing
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
