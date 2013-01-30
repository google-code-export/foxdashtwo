package com.kobaj.networking;

import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.loader.FileHandler;
import com.kobaj.math.Constants;

//thanks to http://www.vogella.com/articles/AndroidNetworking/article.html

public class NetworkManager implements Runnable
{
	private final String error_tag = "Network Error";
	private String the_url;
	private EnumNetworkAction action;
	private final static String implement_error = " must implement FinishedURLListener";
	
	private static final String server = "http://normannexus.com:8080/";
	
	public interface FinishedURLListener
	{
		public void onFinishedURL(String value, EnumNetworkAction action);
	}
	
	private FinishedURLListener mListener;
	
	public NetworkManager(Activity activity)
	{
		try
		{
			mListener = (FinishedURLListener) activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + implement_error);
		}
	}
	
	public void accessNetwork(EnumNetworkAction action, String... attributes)
	{
		the_url = Constants.empty;
		this.action = action;
		
		// modify the url
		if (action == EnumNetworkAction.login)
			if (attributes.length == 2)
			{
				Uri.Builder b = Uri.parse(server).buildUpon();
				
				b.path("new_level_editor/php/game.php");
				
				b.appendQueryParameter("action", "check_user");
				b.appendQueryParameter("username", attributes[0]);
				b.appendQueryParameter("token", attributes[1]);
				
				the_url = b.build().toString();
			}
		
		new Thread(this).start();
	}
	
	public void run()
	{
		if (isNetworkAvailable() && !the_url.equals(Constants.empty))
		{
			Constants.network_activity++;
			executeURL();
		}
	}
	
	private void executeURL()
	{
		try
		{
			URL url = new URL(the_url);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			mListener.onFinishedURL(FileHandler.ioStreamToString(con.getInputStream()), action);
		}
		catch (Exception e)
		{
			Log.e(error_tag, e.toString());
		}
		
	}
	
	private boolean isNetworkAvailable()
	{
		NetworkInfo networkInfo = GameActivity.cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		
		return false;
	}
}
