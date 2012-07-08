package com.kobaj.loader;

//http://blog.poweredbytoast.com/loading-opengl-textures-in-android

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class GLBitmapReader
{
	// key is the res id
	// just a nifty way of storing images we've already loaded
	// this might eventually be changed to a base class so that
	// it can contain sounds and other actually loaded resources.
	// but most likely not :/
	public static HashMap<Integer, GLLoadedTexture> loaded_textures = new HashMap<Integer, GLLoadedTexture>();
	
	//in case context is lost
	public static void resetLoadedTextures()
	{
		loaded_textures = new HashMap<Integer, GLLoadedTexture>(); 
	}
	
	// Get a new texture id:
	private static int newTextureID()
	{
		int[] temp = new int[1];
		GLES20.glGenTextures(1, temp, 0);
		return temp[0];
	}
	
	// Will load a texture out of a drawable resource file, and return an OpenGL texture id
	public static int loadTextureFromResource(int resource)
	{		
		// This will tell the BitmapFactory to not scale based on the device's
		// pixel density:
		// (Thanks to Matthew Marshall for this bit)
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inScaled = false;
		
		// Load up, and flip the texture:
		Bitmap temp = BitmapFactory.decodeResource(com.kobaj.math.Constants.resources, resource, opts);
		return loadTextureFromBitmap(resource, temp);
	}
	
	public static int loadTextureFromBitmap(int resource, Bitmap temp)
	{
		if (loaded_textures.containsKey(resource))
			return loaded_textures.get(resource).texture_id;
		
		//flip it the right way around.
		Matrix flip = new Matrix();
		flip.postScale(1f, -1f);
		
		Bitmap bmp = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), flip, true);
		temp.recycle();
		
		// In which ID will we be storing this texture?
		int id = newTextureID();
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
		
		//make a loader
		// put out info into someplace safe.
		GLLoadedTexture load = new GLLoadedTexture();
		load.resource_id = resource;
		load.height = bmp.getHeight();
		load.width = bmp.getWidth();
		load.texture_id = id;
		
		// Set all of our texture parameters:
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		
		// Generate, and load up all of the mipmaps:
		for (int level = 0, height = bmp.getHeight(), width = bmp.getWidth(); true; level++)
		{
			// Push the bitmap onto the GPU:
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, level, bmp, 0);
			
			// We need to stop when the texture is 1x1:
			if (height == 1 && width == 1)
				break;
			
			// Resize, and let's go again:
			width >>= 1;
			height >>= 1;
			if (width < 1)
				width = 1;
			if (height < 1)
				height = 1;
			
			Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, width, height, true);
			bmp.recycle();
			bmp = bmp2;
		}
		
		loaded_textures.put(resource, load);
		
		// cleanup!
		bmp.recycle();
		return id;
	}
}
