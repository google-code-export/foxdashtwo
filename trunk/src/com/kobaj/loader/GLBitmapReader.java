package com.kobaj.loader;

//http://blog.poweredbytoast.com/loading-opengl-textures-in-android

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import android.util.SparseArray;

import com.kobaj.foxdashtwo.FoxdashtwoActivity;

public class GLBitmapReader
{
	// key is the res id
	// just a nifty way of storing images we've already loaded
	// this might eventually be changed to a base class so that
	// it can contain sounds and other actually loaded resources.
	// but most likely not :/
	public static SparseArray<GLLoadedTexture> loaded_textures = new SparseArray<GLLoadedTexture>();
	
	// way of giving out resource id's to objects that dont have them natively
	private static int loaded_resource_id = 9;
	
	// in case context is lost
	public static void resetLoadedTextures()
	{
		loaded_textures = new SparseArray<GLLoadedTexture>();
	}
	
	// get a new unused resource id
	public static int newResourceID()
	{
		loaded_resource_id++;
		return loaded_resource_id;
	}
	
	// Get a new unused texture id:
	private static int newTextureID()
	{
		int[] temp = new int[1];
		GLES20.glGenTextures(1, temp, 0);
		return temp[0];
	}
	
	// Will load a texture out of a drawable resource file, and return an OpenGL
	// texture id
	public static void loadTextureFromResource(int resource)
	{
		// This will tell the BitmapFactory to not scale based on the device's
		// pixel density:
		// (Thanks to Matthew Marshall for this bit)
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inScaled = false;
		
		// Load up, and flip the texture:
		Bitmap temp = BitmapFactory.decodeResource(com.kobaj.math.Constants.resources, resource, opts);
		loadTextureFromBitmap(resource, temp);
	}
	
	public static void loadTextureFromBitmap(final int resource, final Bitmap temp)
	{
		FoxdashtwoActivity.mGLView.queueEvent(new Runnable() { public void run() {
			
			// get an item from our loaded resources (to see if this is a duplicate entry and thus can be skipped).
			GLLoadedTexture loaded_item = loaded_textures.get(resource);
			if (loaded_item != null)
			{
				// see if its a duplicate
				if(loaded_item.width != temp.getWidth() ||
						loaded_item.height != temp.getHeight() ||
						loaded_item.bitmap_hash != temp.hashCode())
					Log.e("loading_collision", ": loadTextureFromBitmap There was a collision with texture. Resource ID: " + resource + " ... " + loaded_item.bitmap_hash + ":" + temp.hashCode());
				
				// don't overwrite
				return;
			}
			
			// flip it the right way around.
			Matrix flip = new Matrix();
			flip.postScale(1f, -1f);
			
			int original_width = temp.getWidth();
			int original_height = temp.getHeight();
			int original_hash = temp.hashCode();
			
			Bitmap bmp1 = Bitmap.createBitmap(temp, 0, 0, original_width, original_height, flip, true);
			temp.recycle();
			
			int square_width = com.kobaj.math.Functions.nearestPowerOf2(bmp1.getWidth());
			int square_height = com.kobaj.math.Functions.nearestPowerOf2(bmp1.getHeight());
			
			int new_size = Math.max(square_width, square_height);
			
			Bitmap bmp = Bitmap.createBitmap(new_size, new_size, Bitmap.Config.ARGB_8888);
			Canvas temp_canvas = new Canvas(bmp);
			temp_canvas.drawBitmap(bmp1, 0, new_size - original_height, new Paint());
			
			bmp1.recycle();
			
			// In which ID will we be storing this texture?
			int id = newTextureID();
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
			
			// make a loader
			// put out info into someplace safe.
			GLLoadedTexture load = new GLLoadedTexture();
			load.resource_id = resource;
			load.height = original_height;
			load.width = original_width;
			load.texture_id = id;
			load.bitmap_hash = original_hash;
			
			// Set all of our texture parameters:
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			
			// Push the bitmap onto the GPU:
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
			
			loaded_textures.put(resource, load);
			
			// cleanup!
			bmp.recycle();
		}});
	}
}
