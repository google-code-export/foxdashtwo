package com.kobaj.networking.task;

import java.util.HashMap;

import org.json.JSONObject;

import com.kobaj.account_settings.UserSettings;
import com.kobaj.loader.FileHandler;
import com.kobaj.loader.RawTextReader;
import com.kobaj.math.Constants;
import com.kobaj.networking.NetworkManager;

public class TaskDownloadMap extends MyTask
{
	public interface FinishedDownloading
	{
		void onDownloadCompleted(int lid);
	}
	
	private FinishedDownloading local_callback;
	
	public void setFinishedDownloading(FinishedDownloading finishedDownloading)
	{
		local_callback = finishedDownloading;
	}
	
	private int lid = 0;
	
	@Override
	protected String getUrl(String... attributes)
	{
		String the_url = Constants.empty;
		
		// modify the url
		if (attributes.length == 1)
		{
			HashMap<String, String> url_helper = new HashMap<String, String>();
			url_helper.put(NetworkManager.url_file, "shared.php");
			url_helper.put(NetworkManager.url_action, "download_xml");
			
			url_helper.put("lid", attributes[0]);
			
			if (UserSettings.send_statistics)
				url_helper.put("iso3", Constants.resources.getConfiguration().locale.getISO3Country());
			
			the_url = NetworkManager.genericUrlBuilder(url_helper);
			
			this.lid = Integer.valueOf(attributes[0]);
		}
		
		return the_url;
	}
	
	@Override
	protected void onPostExecute(String value)
	{
		if (value == null || value.equals(Constants.empty))
			return;
		
		try
		{
			String name = RawTextReader.findValueInXML(value, "lid");
			if (lid == Integer.valueOf(name))
			{
				FileHandler.writeTextFile(FileHandler.download_dir + name, value);
				
				if (local_callback != null)
					local_callback.onDownloadCompleted(lid);
			}
		}
		catch (IllegalStateException e)
		{
			// do nothing bad download
		}
	}
	
	@Override
	protected void parseJSON(JSONObject json)
	{
		// do nothing
	}
}
