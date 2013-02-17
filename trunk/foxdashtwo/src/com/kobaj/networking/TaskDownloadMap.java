package com.kobaj.networking;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.net.Uri;

import com.kobaj.loader.FileHandler;
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
		final Pattern pattern = Pattern.compile("<lid>(.+?)</lid>");
		final Matcher matcher = pattern.matcher(value);
		matcher.find();
		String name = matcher.group(1);
		
		FileHandler.writeTextFile(FileHandler.download_dir + name, value);
	}
	
	@Override
	protected void parseJSON(JSONObject json)
	{
		//do nothing
	}
}
