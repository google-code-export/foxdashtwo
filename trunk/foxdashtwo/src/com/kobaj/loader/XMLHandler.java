package com.kobaj.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Environment;
import android.util.Log;

public class XMLHandler
{
	private final static String file_directory = "/foxdashtwo";
	private final static String error_tag = "XML Serial Error";
	private final static String save_format = ".xml";
	private final static String file_format = "UTF-8";
	
	//some of these create a little bit of string garbage, but its assumed this is only
	//called when loading, thus we will be using a system.gc after all is done loading.
	
	public static String[] getFileList()
	{
		//I'm not 100% sure if this implementation is truly recursive.
		if(hasStorage(true))
		{
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + file_directory);
			dir.mkdirs();
			
			File[] sdDirList = dir.listFiles();
			
			String[] temp = new String[sdDirList.length];
			
			for(int i = sdDirList.length - 1; i >=0; i--)
			{
				temp[i] = sdDirList[i].getName().toString();
			}
			
			return temp;
		}
		
		return null;
	}
	
	public static <T> boolean writeSerialFile(T writeable, String fileName)
	{
		if (hasStorage(true))
		{
			// write
			Serializer serial = new Persister();
			
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + file_directory);
			dir.mkdirs();
			
			File sdcardFile = new File(dir, fileName + save_format);
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
	
	private static boolean hasStorage(boolean requireWriteAccess)
	{
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(state))
			return true;
		else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
			return true;
		
		return false;
	}

	public static <T> T readSerialFile(String fileName, Class<? extends T> type)
	{
		T finalReturn = null;
		Serializer serial = new Persister();
		
		if (hasStorage(false))
		{
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + file_directory);
			
			File sdcardFile = new File(dir, fileName + save_format);
			try
			{
				if (sdcardFile.exists())
				{
					byte[] temp = ioStreamtoByteArray(new FileInputStream(sdcardFile));
					String temp2 = new String(temp, file_format);
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
	
	public static <T> T readSerialFile(Resources resources, int identity, Class<? extends T> type)
	{
		T final_return = null;
		Serializer serial = new Persister();
		
		try
		{
			byte[] byte_temp = ioStreamtoByteArray(resources.openRawResource(identity));
			String string_temp = new String(byte_temp, file_format);
			final_return = serial.read(type, string_temp);
		}
		catch (NotFoundException e)
		{
			Log.e(error_tag, e.toString());
		}
		catch (Exception e)
		{
			Log.e(error_tag, e.toString());
		}
		
		return final_return;
	}
	
	private static byte[] ioStreamtoByteArray(InputStream is)
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		int nRead;
		byte[] data = new byte[16384];
		
		try
		{
			while ((nRead = is.read(data, 0, data.length)) != -1)
			{
				buffer.write(data, 0, nRead);
			}
		}
		catch (IOException e)
		{
			Log.e(error_tag, e.toString());
		}
		
		try
		{
			buffer.flush();
		}
		catch (IOException e)
		{
			Log.e(error_tag, e.toString());
		}
		
		return buffer.toByteArray();
	}
}
