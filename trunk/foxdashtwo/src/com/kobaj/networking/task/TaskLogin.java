package com.kobaj.networking.task;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

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
			HashMap<String, String> url_helper = new HashMap<String, String>();
			url_helper.put(NetworkManager.url_file, NetworkManager.file_game);
			url_helper.put(NetworkManager.url_action, "check_user");
			
			url_helper.put("token", attributes[1]);
			url_helper.put("username", attributes[0]);
			
			the_url = NetworkManager.genericUrlBuilder(url_helper);
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
		
				Constants.uid = json.getInt("uid");
			}
			else
			{
				Constants.logged_in = false;
				ToastManager.makeShortToast(R.string.login_fail);
			}
		}
		catch (JSONException e)
		{
			// oh well!
		}
	}
	
}
