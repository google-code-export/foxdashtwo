package com.kobaj.foxdashtwo;

import android.os.Bundle;

import com.kobaj.account_settings.Accounts;
import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.account_settings.UserSettings;
import com.kobaj.loader.FileHandler;
import com.kobaj.math.Constants;
import com.kobaj.screen.TitleScreen;

public final class FoxdashtwoActivity extends GameActivity
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
		onSave();
		
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
		
		//lets try to login
		if(Constants.logged_in == false && UserSettings.auto_login == true)
			Constants.accounts.account_login();
		
		super.onResume();
	}
	
	public static void onSave()
	{
		// save user settings
		FileHandler.writeSerialFile(new UserSettings(), settings_name);
		FileHandler.writeSerialFile(new SinglePlayerSave(), single_player_name);
	}
}
