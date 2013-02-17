package com.kobaj.loader;

//http://stackoverflow.com/questions/4087674/android-read-txt-raw-resource-file

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

public class RawTextReader
{
	public static String findValueInXML(String input, String tag)
	{
		final Pattern pattern = Pattern.compile("<" + tag + ">(.+?)</" + tag + ">");
		final Matcher matcher = pattern.matcher(input);
		matcher.find();
		String name = matcher.group(1);
		
		return name;
	}
	
	public static String readRawTextResource(Context ctx, int resId)
	{
		InputStream inputStream = ctx.getResources().openRawResource(resId);
		
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		StringBuilder text = new StringBuilder();
		
		try
		{
			while ((line = buffreader.readLine()) != null)
			{
				text.append(line);
				text.append('\n');
			}
		}
		catch (IOException e)
		{
			Log.e("Raw Text", "Unable to read raw text file: " + resId);
		}
		
		return text.toString();
	}
}
