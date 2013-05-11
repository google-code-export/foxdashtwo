package com.kobaj.loader;

import com.kobaj.foxdashtwo.FoxdashtwoActivity;

import android.os.AsyncTask;

public class AsyncSave extends AsyncTask<Void, Void, Void>
{

	@Override
	protected Void doInBackground(Void... arg0)
	{
		FoxdashtwoActivity.onSave();
		return null;
	}
	
}
