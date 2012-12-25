package com.kobaj.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

public class FileHandler
{
	private final static String file_directory = "/foxdashtwo";
	private final static String error_tag = "XML Serial Error";
	private final static String save_format = ".xml";
	private final static String fullstop = ".";
	
	// some of these create a little bit of string garbage, but its assumed this is only
	// called when loading, thus we will be using a system.gc after all is done loading.
	
	// thanks to http://blog.mynotiz.de/programmieren/java-text-in-eine-datei-schreiben-450/
	public static void writeTextFile(String file_name, String file_contents)
	{
		if (hasStorage(false))
		{
			try
			{
				if(!file_name.contains(fullstop))
					file_name += save_format;
					
				// create file and directory
				File dir = prepareDirectory();
				File sdcardFile = new File(dir, file_name);
				
				if (sdcardFile.exists())
					sdcardFile.delete();
				
				sdcardFile.createNewFile();
				
				// make a writer
				FileWriter my_writer = new FileWriter(sdcardFile, true);
				
				// write out and close
				my_writer.write(file_contents);
				
				my_writer.flush();
				my_writer.close();
			}
			catch (IOException e)
			{
				Log.e(error_tag, e.toString());
			}
		}
	}
	
	public static boolean fileExists(String file_name)
	{
		if(hasStorage(false))
		{
			if(!file_name.contains(fullstop))
				file_name += save_format;
			
			File dir = prepareDirectory();
			File sdcardFile = new File(dir, file_name);
			return sdcardFile.exists();
		}
		
		return false;
	}	
	
	public static String[] getFileList()
	{
		// I'm not 100% sure if this implementation is truly recursive.
		if (hasStorage(false))
		{
			File dir = prepareDirectory();
			
			File[] sdDirList = dir.listFiles();
			
			String[] temp = new String[sdDirList.length];
			
			for (int i = sdDirList.length - 1; i >= 0; i--)
				temp[i] = sdDirList[i].getName().toString();
			
			return temp;
		}
		
		return null;
	}
	
	public static <T> boolean writeSerialFile(T writeable, String file_name)
	{
		if (hasStorage(true))
		{
			// write
			Serializer serial = new Persister();
			File dir = prepareDirectory();
			
			if(!file_name.contains(fullstop))
				file_name += save_format;
			
			File sdcardFile = new File(dir, file_name);
			try
			{
				if (sdcardFile.exists())
					sdcardFile.delete();
				
				sdcardFile.createNewFile();
			}
			catch (IOException e)
			{
				Log.e(error_tag, e.toString());
			}
			
			try
			{
				serial.write(writeable, sdcardFile);
				return true;
			}
			catch (Exception e)
			{
				Log.e(error_tag, e.toString());
			}
		}
		
		return false;
	}
	
	// read in a file to a class
	public static <T> T readSerialFile(String file_name, Class<? extends T> type)
	{
		T finalReturn = null;
		Serializer serial = new Persister();
		
		if (hasStorage(false))
		{
			File dir = prepareDirectory();
			
			if(!file_name.contains(fullstop))
				file_name += save_format;
			
			File sdcardFile = new File(dir, file_name);
			try
			{
				if (sdcardFile.exists())
				{
					String temp2 = ioStreamToString(new FileInputStream(sdcardFile));
					try
					{
						finalReturn = serial.read(type, temp2);
					}
					catch (Exception e)
					{
						Log.e(error_tag, e.toString());
					}
				}
			}
			catch (IOException e)
			{
				Log.e(error_tag, e.toString());
			}
		}
		
		return finalReturn;
	}
	
	// used to read in a resource to class
	public static <T> T readSerialResource(Resources resources, int identity, Class<? extends T> type)
	{
		T final_return = null;
		
		// dont need to check for directory or read access, as resources are in memory.
		final_return = readString(ioStreamToString(resources.openRawResource(identity)), type);

		
		return final_return;
	}
	
	// takes a string and converts it to the type via serializeation
	public static <T> T readString(String input, Class<? extends T> type)
	{
		T final_return = null;
		
		// read it in
		Serializer serial = new Persister();
		
		try
		{
			final_return = serial.read(type, input);
		}
		catch (Exception e)
		{
			Log.e(error_tag, e.toString());
		}
		
		return final_return;
	}
	
	//build our folder structure
	private static File prepareDirectory()
	{
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + file_directory);
		dir.mkdirs();
		
		return dir;
	}
	
	// check to see if we can read and or write to sd card
	private static boolean hasStorage(boolean requireWriteAccess)
	{
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(state))
			return true;
		else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
			return true;
		
		return false;
	}
	
	//convert iostream to string (io can be from the internet or disk)
	public static String ioStreamToString(InputStream is)
	{
		BufferedReader reader = null;
		StringBuilder b = new StringBuilder();
		try
		{
			reader = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = reader.readLine()) != null)
				b.append(line);
		}
		catch (IOException e)
		{
			Log.e(error_tag, e.toString());
		}
		finally
		{
			if (reader != null)
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					Log.e(error_tag, e.toString());
				}
		}
		
		return b.toString();
	}
}
