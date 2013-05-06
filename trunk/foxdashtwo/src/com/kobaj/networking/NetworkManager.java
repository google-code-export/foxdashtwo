package com.kobaj.networking;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.loader.FileHandler;
import com.kobaj.math.Constants;

//thanks to http://www.vogella.com/articles/AndroidNetworking/article.html

public class NetworkManager
{
	private final String error_tag = "Network Error";
	
	// public static final String server = "http://foxdashgame.com/";
	// public static final String php_extension = "php";
	
	// test server
	 public static final String server = "http://normannexus.com:8080/";
	 public static final String php_extension = "new_level_editor/php";
	
	public static final String file_game = "game.php";
	public static final String file_shared = "shared.php";
	
	public static final String url_file = "url_file";
	public static final String url_action = "action";
	public static final String slash = "/";
	
	public static String genericUrlBuilder(HashMap<String, String> gets)
	{
		String the_url = Constants.empty;
		Uri.Builder b = Uri.parse(NetworkManager.server).buildUpon();
		
		Iterator<Entry<String, String>> it = gets.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
			
			if (pairs.getKey().equals(url_file))
				b.path(NetworkManager.php_extension + slash + pairs.getValue());
			else
				b.appendQueryParameter(pairs.getKey(), pairs.getValue());
		}
		
		the_url = b.build().toString();
		
		return the_url;
	}
	
	public String accessNetwork(String the_url)
	{
		if (isNetworkAvailable() && !the_url.equals(Constants.empty))
		{
			Constants.network_activity++;
			return executeURL(the_url);
		}
		
		return Constants.empty;
	}
	
	private String executeURL(String the_url)
	{
		try
		{
			URL url = new URL(the_url);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			Constants.network_activity--;
			return FileHandler.ioStreamToString(con.getInputStream());
		}
		catch (Exception e)
		{
			Log.e(error_tag, e.toString());
		}
		
		return Constants.empty;
	}
	
	private boolean isNetworkAvailable()
	{
		NetworkInfo networkInfo = GameActivity.cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		
		return false;
	}
}
