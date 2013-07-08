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
	
	private static UserSettings user_settings;
	private static SinglePlayerSave saved_game;
	
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
		boolean reset_user_settings = false;
		if(!FileHandler.fileExists(settings_name))
		{
			reset_user_settings = true;
		}
		
		// load user settings
		user_settings = FileHandler.readSerialFile(settings_name, UserSettings.class);
		
		if(user_settings == null)
		{
			user_settings = new UserSettings();
			reset_user_settings = true;
		}
		
		if(reset_user_settings)
			UserSettings.resetDefaults();
		
		////////////////////////////////////////////////
		
		boolean reset_saved_games = false;
		if(!FileHandler.fileExists(single_player_name))
		{
			reset_saved_games = true;
		}
		
		saved_game = FileHandler.readSerialFile(single_player_name, SinglePlayerSave.class);
		if(saved_game == null)
		{
			saved_game = new SinglePlayerSave();
			reset_saved_games = true;
		}
		
		if(reset_saved_games)
			SinglePlayerSave.resetDefaults();
		
		//lets try to login
		if(Constants.logged_in == false && UserSettings.auto_login == true)
			Constants.accounts.account_login();
		
		super.onResume();
	}
	
	public static void onSave()
	{
		// save user settings
		FileHandler.writeSerialFile(user_settings, settings_name);
		FileHandler.writeSerialFile(saved_game, single_player_name);
	}
}
