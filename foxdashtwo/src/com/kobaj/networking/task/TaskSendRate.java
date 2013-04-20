package com.kobaj.networking.task;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.kobaj.math.Constants;
import com.kobaj.networking.NetworkManager;

public class TaskSendRate extends MyTask
{
	public interface FinishedRateing
	{
		void onRateCompleted(int rate);
	}
	
	private FinishedRateing local_callback;
	
	public void setFinishedRateing(FinishedRateing finishedRateing)
	{
		local_callback = finishedRateing;
	}
	
	@Override
	protected String getUrl(String... attributes)
	{
		String the_url = Constants.empty;
		
		// modify the url
		if (attributes.length == 3)
		{
			HashMap<String, String> url_helper = new HashMap<String, String>();
			url_helper.put(NetworkManager.url_file, "shared.php");
			url_helper.put(NetworkManager.url_action, "rate_map");
			
			url_helper.put("token", attributes[0]);
			url_helper.put("lid", attributes[1]);
			url_helper.put("rate", attributes[2]);
			
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
				local_callback.onRateCompleted(json.getInt("rate"));
			}
			else
			{
				local_callback.onRateCompleted(0);
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
