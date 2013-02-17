package com.kobaj.networking;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.message.ToastManager;

public class TaskLogin extends AsyncTask<String, Void, String>
{
	private NetworkManager my_connection_manager;
	
	public TaskLogin()
	{
		my_connection_manager = new NetworkManager();
	}
	
	protected String get_url(String... attributes)
	{
		String the_url = Constants.empty;
		
		// modify the url
		if (attributes.length == 2)
		{
			Uri.Builder b = Uri.parse(NetworkManager.server).buildUpon();
			
			b.path("new_level_editor/php/game.php");
			
			b.appendQueryParameter("action", "check_user");
			b.appendQueryParameter("username", attributes[0]);
			b.appendQueryParameter("token", attributes[1]);
			
			the_url = b.build().toString();
		}
		
		return the_url;
	}
	
	@Override
	protected void onPostExecute(String value)
	{
		// get that json
		try
		{
			JSONObject json = new JSONObject(value);
			boolean success = json.getBoolean("success");
			
			if (success)
			{
				Constants.logged_in = true;
				ToastManager.makeShortToast(R.string.logged_in);
			}
			else
			{
				Constants.logged_in = false;
				ToastManager.makeShortToast(R.string.login_fail);
			}
			
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
		return my_connection_manager.accessNetwork(get_url(attributes));
	}
}
