package com.kobaj.account_settings;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.AsyncTask;
import android.os.Bundle;

import com.kobaj.foxdashtwo.FoxdashtwoActivity;
import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.math.Constants;
import com.kobaj.message.ListPopupManager;
import com.kobaj.networking.EnumNetworkAction;
import com.kobaj.networking.task.TaskLogin;

public class Accounts
{
	public final String account_type = "com.google";
	public static final String popup_tag = "FoxDashTwoAccounts";
	
	private AccountManager am;
	
	// helpful methods
	public Accounts()
	{
		am = AccountManager.get(Constants.context);
	}
	
	private Account[] accounts_array()
	{
		return am.getAccountsByType(account_type);
	}
	
	public int count_accounts()
	{
		// we dont want to needlessly allocate strings unless we absolutely have to.
		return accounts_array().length;
	}
	
	public String[] get_accounts()
	{
		Account[] accounts = accounts_array();
		
		String[] string_names = new String[accounts.length];
		
		for (int i = accounts.length - 1; i >= 0; i--)
			string_names[i] = accounts[i].name;
		
		return string_names;
	}
	
	// outside world calls this
	public void account_login()
	{
		Constants.logging_in = true;
		account_popup();
	}
	
	// real action starts here
	public void account_popup()
	{
		// first get accounts
		if (accounts_array().length == 1)
			UserSettings.selected_account_login = 0;
		
		if (UserSettings.selected_account_login == -1)
		{
			ListPopupManager popup = new ListPopupManager();
			popup.show(Constants.fragment_manager, popup_tag);
		}
		else
			login_step_two();
	}
	
	// call after account popup
	public void login_step_two()
	{
		// rest of stuff
		TaskToken get_token = new TaskToken();
		get_token.execute(EnumNetworkAction.login);
		
	}
	
	// async task calls this
	public String get_token(boolean invalidateToken)
	{
		if (UserSettings.selected_account_login == -1)
			return Constants.empty;
		
		Account[] accounts = Constants.accounts.accounts_array();
		AccountManagerFuture<Bundle> accountManagerFuture;
		accountManagerFuture = am.getAuthToken(accounts[UserSettings.selected_account_login], // account
				"oauth2:https://www.googleapis.com/auth/userinfo.email", // scope
				null, // options
				GameActivity.activity, // activity
				null, // call back
				null); // handler
		Bundle authTokenBundle;
		try
		{
			authTokenBundle = accountManagerFuture.getResult();
			String authToken = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN).toString();
			if (invalidateToken && authToken != null)
			{
				am.invalidateAuthToken("com.google", authToken);
				return authToken = get_token(false);
			}
			return authToken;
		}
		catch (OperationCanceledException e)
		{
			// do nothing
		}
		catch (AuthenticatorException e)
		{
			// do nothing
		}
		catch (IOException e)
		{
			// do nothing
		}
		
		return Constants.empty;
	}
	
	// async task calls this too
	public void tokenReceivedFromTask(String token, EnumNetworkAction action)
	{
		if (action == EnumNetworkAction.login)
		{
			if (token.equals(Constants.empty))
			{
				UserSettings.auto_login = false;
				Constants.logging_in = false;
				return;
			}
			else
			{
				UserSettings.auto_login = true;
			}
			
			FoxdashtwoActivity.onSave();
			
			// start a task to talk to our server
			TaskLogin network_login = new TaskLogin();
			network_login.execute(get_accounts()[UserSettings.selected_account_login], token);
		}
	}
}

class TaskToken extends AsyncTask<EnumNetworkAction, Void, String>
{
	private EnumNetworkAction action;
	
	@Override
	protected String doInBackground(EnumNetworkAction... action)
	{
		this.action = action[0];
		return Constants.accounts.get_token(true);
	}
	
	@Override
	protected void onPostExecute(String token)
	{
		Constants.accounts.tokenReceivedFromTask(token, action);
	}
}
