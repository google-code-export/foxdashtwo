package com.kobaj.networking.task;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.kobaj.math.Constants;
import com.kobaj.networking.NetworkManager;

public class TaskSendScore extends MyTask
{
	
	public interface FinishedScoring
	{
		void onScoreCompleted(double rate);
	}
	
	private FinishedScoring local_callback;
	
	public void setFinishedScoring(FinishedScoring finishedScoring)
	{
		local_callback = finishedScoring;
	}
	
	@Override
	protected String getUrl(String... attributes)
	{
		String the_url = Constants.empty;
		
		// modify the url
		if (attributes.length == 3 || attributes.length == 2)
		{
			HashMap<String, String> url_helper = new HashMap<String, String>();
			url_helper.put(NetworkManager.url_file, NetworkManager.file_game);
			url_helper.put(NetworkManager.url_action, "score_map");
			
			// note token can be empty if the user is not signed in.
			// this is normal.
			url_helper.put("token", attributes[0]);
			url_helper.put("lid", attributes[1]);
			url_helper.put("score", attributes[2]);
			
			the_url = NetworkManager.genericUrlBuilder(url_helper);
		}
		
		return the_url;
	}
	
	@Override
	protected void parseJSON(JSONObject json)
	{
		boolean success;
		try
		{
			success = json.getBoolean("success");
			if (success)
			{
				local_callback.onScoreCompleted(json.getDouble("score"));
			}
			else
			{
				local_callback.onScoreCompleted(0);
			}
		}
		catch (JSONException e)
		{
			// oh well!
		}
	}
	
}
