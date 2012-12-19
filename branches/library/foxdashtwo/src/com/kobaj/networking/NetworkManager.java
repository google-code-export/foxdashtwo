package com.kobaj.networking;

import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.net.NetworkInfo;
import android.util.Log;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.loader.FileHandler;
import com.kobaj.math.Constants;

//thanks to http://www.vogella.com/articles/AndroidNetworking/article.html

public class NetworkManager implements Runnable
{
	private final String empty = "";
	private final String error_tag = "Network Error";
	private String the_url;
	private EnumNetworkAction action;
	
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
			throw new ClassCastException(activity.toString() + " must implement FinishedURLListener");
		}
	}
	
	public void accessNetwork(EnumNetworkAction action)
	{
		the_url = empty;
		
		if(action == EnumNetworkAction.get_level)
		{
			//do something to the url
			the_url = Constants.main_url;
		}
		
		new Thread(this).start();
	}
	
	public void run()
	{
		if(isNetworkAvailable() && !the_url.equals(empty))
		{
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
