package com.kobaj.foxdashtwo;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.kobaj.account_settings.Accounts;
import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.account_settings.UserSettings;
import com.kobaj.loader.FileHandler;
import com.kobaj.math.Constants;
import com.kobaj.message.ToastManager;
import com.kobaj.networking.EnumNetworkAction;
import com.kobaj.screen.TitleScreen;

public final class FoxdashtwoActivity extends GameActivity implements com.kobaj.networking.NetworkManager.FinishedURLListener, com.kobaj.message.ListPopupManager.NoticeDialogListener
{
	private static final String settings_name = "user_settings";
	private static final String single_player_name = "single_player";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Constants.accounts = new Accounts();
		
		mGLView.my_game.onChangeScreen(new TitleScreen());
	}
	
	@Override
	public void onPause()
	{
		// save user settings
		FileHandler.writeSerialFile(new UserSettings(), settings_name);
		FileHandler.writeSerialFile(new SinglePlayerSave(), single_player_name);
		
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		// load user settings
		@SuppressWarnings("unused")
		UserSettings temp = FileHandler.readSerialFile(settings_name, UserSettings.class);
		
		@SuppressWarnings("unused")
		SinglePlayerSave saved_game = FileHandler.readSerialFile(single_player_name, SinglePlayerSave.class);
		
		super.onResume();
	}
	
	public void onFinishedURL(String value, EnumNetworkAction action)
	{	
		// get that json
		try
		{
			JSONObject json = new JSONObject(value);
			boolean success = json.getBoolean("success");
	
			Constants.network_activity--;
			
			// parse it
			if (action == EnumNetworkAction.login)
			{
				if(success)
					ToastManager.makeShortToast(R.string.logged_in);
				else
				{
					ToastManager.makeShortToast(R.string.login_fail);
				}
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onDialogListSelect(int id)
	{
		UserSettings.selected_account_login = id;
		Constants.accounts.account_login();
	}
}