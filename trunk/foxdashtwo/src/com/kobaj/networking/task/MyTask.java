package com.kobaj.networking.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.message.ToastManager;
import com.kobaj.networking.NetworkManager;

public abstract class MyTask extends AsyncTask<String, Void, String>
{
	protected NetworkManager my_connection_manager;
	
	public MyTask()
	{
		my_connection_manager = new NetworkManager();
	}
	
	protected abstract String getUrl(String... attributes);
	
	protected abstract void parseJSON(JSONObject json);
	
	@Override
	protected void onPostExecute(String value)
	{
		// get that json
		try
		{
			JSONObject json = new JSONObject(value);
			parseJSON(json);
		}
		catch (JSONException e)
		{
			// do nothing
			ToastManager.makeShortToast(R.string.error_message);
		}
		
		Constants.logging_in = false;
	}
	
	@Override
	protected String doInBackground(String... attributes)
	{
		return my_connection_manager.accessNetwork(getUrl(attributes));
	}
}
