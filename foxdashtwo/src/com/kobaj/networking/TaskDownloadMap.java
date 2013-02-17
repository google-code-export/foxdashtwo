package com.kobaj.networking;

import org.json.JSONObject;

import android.net.Uri;

import com.kobaj.loader.FileHandler;
import com.kobaj.loader.RawTextReader;
import com.kobaj.math.Constants;

public class TaskDownloadMap extends MyTask
{
	public interface FinishedDownloading
	{
		void onTaskCompleted(int lid);
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
			Uri.Builder b = Uri.parse(NetworkManager.server).buildUpon();
			
			b.path(NetworkManager.php_extension + "/shared.php");
			
			b.appendQueryParameter("action", "download_xml");
			b.appendQueryParameter("lid", attributes[0]);
			
			this.lid = Integer.valueOf(attributes[0]);
			
			the_url = b.build().toString();
		}
		
		return the_url;
	}
	
	@Override
	protected void onPostExecute(String value)
	{
		String name = RawTextReader.findValueInXML(value, "lid");
		if (lid == Integer.valueOf(name))
		{
			FileHandler.writeTextFile(FileHandler.download_dir + name, value);
			
			if (local_callback != null)
				local_callback.onTaskCompleted(lid);
		}
	}
	
	@Override
	protected void parseJSON(JSONObject json)
	{
		// do nothing
	}
}
