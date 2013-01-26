package com.kobaj.foxdashtwo;

import android.os.Bundle;

import com.kobaj.account_settings.Accounts;
import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.account_settings.UserSettings;
import com.kobaj.loader.FileHandler;
import com.kobaj.math.Constants;
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
		
		// log the user in
		if (UserSettings.selected_account_login == -1)
			if (Constants.accounts.count_accounts() > 1)
				Constants.accounts.account_popup();
			else
			{
				UserSettings.selected_account_login = 0;
				Constants.accounts.account_login();
			}
		else
			Constants.accounts.account_login();
		
		super.onResume();
	}
	
	public void onFinishedURL(String value, EnumNetworkAction action)
	{
		// TODO Auto-generated method stub
		
	}
	
	public void onDialogListSelect(int id)
	{
		UserSettings.selected_account_login = id;
		Constants.accounts.account_login();
	}
}
