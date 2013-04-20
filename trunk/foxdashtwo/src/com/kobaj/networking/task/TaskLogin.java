package com.kobaj.networking.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.message.ToastManager;
import com.kobaj.networking.NetworkManager;

public class TaskLogin extends MyTask
{
	protected String getUrl(String... attributes)
	{
		String the_url = Constants.empty;
		
		// modify the url
		if (attributes.length == 2)
		{
			Uri.Builder b = Uri.parse(NetworkManager.server).buildUpon();
			
			b.path(NetworkManager.php_extension + "/game.php");
			
			b.appendQueryParameter("action", "check_user");
			b.appendQueryParameter("username", attributes[0]);
			b.appendQueryParameter("token", attributes[1]);
			
			the_url = b.build().toString();
		}
		
		return the_url;
	}
	
	@Override
	protected void parseJSON(JSONObject json)
	{
		boolean success;
		try
		{
			success = json.getBoolean("success");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
