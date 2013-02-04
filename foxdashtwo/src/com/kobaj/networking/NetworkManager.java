package com.kobaj.networking;

import java.net.HttpURLConnection;
import java.net.URL;

import android.net.NetworkInfo;
import android.util.Log;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.loader.FileHandler;
import com.kobaj.math.Constants;

//thanks to http://www.vogella.com/articles/AndroidNetworking/article.html

public class NetworkManager
{
	private final String error_tag = "Network Error";
	
	public static final String server = "http://normannexus.com:8080/";
	
	public String accessNetwork(EnumNetworkAction action, String the_url)
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
