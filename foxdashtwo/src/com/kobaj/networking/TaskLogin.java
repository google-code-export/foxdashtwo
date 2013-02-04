package com.kobaj.networking;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.message.ToastManager;

public class TaskLogin extends AsyncTask<String, Void, String>
{
	public String get_url(String... attributes)
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
				ToastManager.makeShortToast(R.string.logged_in);
			}
			else
				ToastManager.makeShortToast(R.string.login_fail);
			
		}
		catch (JSONException e)
		{
			// do nothing
		}
	}
	
	@Override
	protected String doInBackground(String... attributes)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
