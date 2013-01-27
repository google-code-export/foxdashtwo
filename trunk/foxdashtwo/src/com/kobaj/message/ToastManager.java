package com.kobaj.message;

import android.widget.Toast;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.math.Constants;

public class ToastManager
{
	public static void makeShortToast(int resource)
	{
		makeShortToast(Constants.resources.getString(resource));
	}
	
	public static void makeLongToast(int resource)
	{
		makeLongToast(Constants.resources.getString(resource));
	}
	
	public static void makeShortToast(CharSequence text)
	{
		makeToast(text, Toast.LENGTH_SHORT);
	}
	
	public static void makeLongToast(CharSequence text)
	{
		makeToast(text, Toast.LENGTH_LONG);
	}
	
	private static void makeToast(final CharSequence text, final int duration)
	{
		// have to load on UI thread
		GameActivity.activity.runOnUiThread(new Runnable()
		{
			public void run()
			{
				Toast toast = Toast.makeText(com.kobaj.math.Constants.context, text, duration);
				toast.show();
				System.gc(); // gotta get rid of that string now.
			}
		});
	}
}
