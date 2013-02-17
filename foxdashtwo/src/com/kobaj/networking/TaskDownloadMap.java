package com.kobaj.networking;

import org.json.JSONObject;

import android.net.Uri;

import com.kobaj.loader.FileHandler;
import com.kobaj.loader.RawTextReader;
import com.kobaj.math.Constants;

public class TaskDownloadMap extends MyTask
{
	
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
			
			the_url = b.build().toString();
		}
		
		return the_url;
	}
	
	@Override
	protected void onPostExecute(String value)
	{
		String name = RawTextReader.findValueInXML(value, "lid");
		
		FileHandler.writeTextFile(FileHandler.download_dir + name, value);
	}
	
	@Override
	protected void parseJSON(JSONObject json)
	{
		// do nothing
	}
}
