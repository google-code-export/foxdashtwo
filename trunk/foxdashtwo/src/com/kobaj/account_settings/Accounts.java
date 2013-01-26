package com.kobaj.account_settings;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Bundle;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.math.Constants;
import com.kobaj.message.ListPopupManager;

public class Accounts implements Runnable
{
	public final String account_type = "com.google";
	public final String popup_tag = "Fox_Dash_Two_Accounts";
	
	private AccountManager am;
	
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
		// we dont want to needlsly allocate strings unless we absolutely have to.
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
	
	public void account_popup()
	{
		// first get accounts
		ListPopupManager popup = new ListPopupManager();
		popup.show(Constants.fragment_manager, popup_tag);
		
	}
	
	public void account_login()
	{
		new Thread(this).start();
	}
	
	private String get_token(boolean invalidateToken)
	{
		Account[] accounts = accounts_array();
		AccountManagerFuture<Bundle> accountManagerFuture;
		accountManagerFuture = am.getAuthToken(accounts[UserSettings.selected_account_login], "oauth2:https://www.googleapis.com/auth/userinfo.profile", null, GameActivity.activity, null, null);
		Bundle authTokenBundle;
		try
		{
			authTokenBundle = accountManagerFuture.getResult();
			String authToken = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN).toString();
			if (invalidateToken)
			{
				am.invalidateAuthToken("com.google", authToken);
				return authToken = get_token(false);
			}
			return authToken;
		}
		catch (OperationCanceledException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (AuthenticatorException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void run()
	{
		// networking
		String test = get_token(true);
		
		int x = 5;
		// this will simply get the gid to login with
	}
}
