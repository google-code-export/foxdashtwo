package com.kobaj.networking.task;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.kobaj.math.Constants;
import com.kobaj.networking.NetworkManager;

public class TaskSendReport extends MyTask
{
	public interface FinishedReporting
	{
		void onReportCompleted(boolean reported);
	}
	
	private FinishedReporting local_callback;
	
	public void setFinishedReporting(FinishedReporting finishedReporting)
	{
		local_callback = finishedReporting;
	}
	
	@Override
	protected String getUrl(String... attributes)
	{
		String the_url = Constants.empty;
		
		// modify the url
		if (attributes.length == 3)
		{
			HashMap<String, String> url_helper = new HashMap<String, String>();
			url_helper.put(NetworkManager.url_file, NetworkManager.file_game);
			url_helper.put(NetworkManager.url_action, "report_map");
			
			url_helper.put("token", attributes[0]);
			url_helper.put("lid", attributes[1]);
			
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
				local_callback.onReportCompleted(true);
			}
			else
			{
				local_callback.onReportCompleted(false);
			}
		}
		catch (JSONException e)
		{
			// oh well!
		}
	}
	
}
