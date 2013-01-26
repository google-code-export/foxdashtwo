package com.kobaj.account_settings;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.SharedPreferencesBackupHelper;

import com.kobaj.math.Constants;

public class AccountBackup extends BackupAgentHelper
{
	// The name of the SharedPreferences file
	public static final String MY_PREFS_KEY = "prefs_foxdashtwo";

	// key used by the cloud to get data
	public static final String MY_PREFS_BACKUP_KEY = "cloud_foxdashtwo";
	
	private static BackupManager backup_manager;
	
	// Allocate a helper and add it to the backup agent
	@Override
	public void onCreate()
	{
		SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(Constants.context, MY_PREFS_KEY);
		addHelper(MY_PREFS_BACKUP_KEY, helper);
	}
	
	public static void requestBackup()
	{
		if(backup_manager == null)
			backup_manager = new BackupManager(Constants.context);
		backup_manager.dataChanged();
	}
	
	public static void requestRestore()
	{
		if(backup_manager == null)
			backup_manager = new BackupManager(Constants.context);
		backup_manager.requestRestore(null);
	}
}
