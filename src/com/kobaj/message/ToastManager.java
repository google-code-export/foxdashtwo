package com.kobaj.message;

import android.widget.Toast;

public class ToastManager
{
	public static void makeShortToast(CharSequence text)
	{
		makeToast(text, Toast.LENGTH_SHORT);
	}
	
	public static void makeLongToast(CharSequence text)
	{
		makeToast(text, Toast.LENGTH_LONG);
	}
	
	private static void makeToast(CharSequence text, int duration)
	{
		Toast toast = Toast.makeText(com.kobaj.math.Constants.context, text, duration);
		toast.show();
		System.gc(); //gotta get rid of that string now.
	}
}
