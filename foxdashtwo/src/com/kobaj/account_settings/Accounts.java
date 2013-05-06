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
import com.kobaj.networking.task.TaskSendRate;
import com.kobaj.networking.task.TaskSendReport;
import com.kobaj.networking.task.TaskSendScore;

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
	
	// login required for sendRate
	public void sendRate(int lid, int rateing, TaskSendRate sender)
	{
		TaskToken get_token = new TaskToken();
		get_token.execute(EnumNetworkAction.rate, lid, rateing, sender);
	}
	
	// login required for sendReport
	public void sendReport(int lid, TaskSendReport sender)
	{
		TaskToken get_token = new TaskToken();
		get_token.execute(EnumNetworkAction.report, lid, sender);
	}
	
	// login required for score
	public void sendScore(String lid, double score, TaskSendScore sender)
	{
		TaskToken get_token = new TaskToken();
		get_token.execute(EnumNetworkAction.score, lid, score, sender);
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
	public void tokenReceivedFromTask(String token, EnumNetworkAction action, Object[] passthru)
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
		else if (action == EnumNetworkAction.rate)
		{
			if (token.equals(Constants.empty))
			{
				return;
			}
			
			if (TaskSendRate.class.isAssignableFrom(passthru[3].getClass()))
			{
				TaskSendRate network_rate = TaskSendRate.class.cast(passthru[3]);
				network_rate.execute(token,
						String.valueOf((Integer) passthru[1]), // lid
						String.valueOf((Integer) passthru[2])); // rate
			}
		}
		else if (action == EnumNetworkAction.report)
		{
			if (token.equals(Constants.empty))
			{
				return;
			}
			
			if (TaskSendReport.class.isAssignableFrom(passthru[2].getClass()))
			{
				TaskSendReport network_report = TaskSendReport.class.cast((passthru[2]));
				network_report.execute(token, 
						String.valueOf((Integer) passthru[1])); // lid
			}
			
		}
		else if (action == EnumNetworkAction.score)
		{
			if(TaskSendScore.class.isAssignableFrom(passthru[3].getClass()))
			{
				TaskSendScore network_score = TaskSendScore.class.cast(passthru[3]);
				network_score.execute(token,
						String.valueOf(passthru[1]), // lid
						String.valueOf((Double) passthru[2])); // score
			}
		}
	}
}

class TaskToken extends AsyncTask<Object, Void, String>
{
	private EnumNetworkAction action;
	
	private Object[] passthru;
	
	@Override
	protected String doInBackground(Object... actions)
	{
		if (EnumNetworkAction.class.isAssignableFrom(actions[0].getClass()))
			this.action = (EnumNetworkAction) actions[0];
		
		passthru = actions;
		
		return Constants.accounts.get_token(true);
	}
	
	@Override
	protected void onPostExecute(String token)
	{
		Constants.accounts.tokenReceivedFromTask(token, action, passthru);
	}
}
