package com.kobaj.openglgraphics;

//thanks to http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/4.1.1_r1/com/example/android/apis/graphics/CompressedTextureActivity.java#CompressedTextureActivity.SyntheticCompressedTextureLoader
//and http://stackoverflow.com/questions/7208794/android-opengl-es-1-1-texture-compression-on-the-fly

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.graphics.Bitmap;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.util.Log;

import com.kobaj.loader.GLBitmapReader;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;

public class ETC1Extended
{
	private final boolean use_stream_io = false;
	private int hash = -1;
	private InputStream input;
	
	// returns the handle to the RGB in int[0] and the handle to A in int[1] if alpha is set to true
	public int[] loadETC1(Bitmap image, int square_width, int square_height, boolean has_alpha)
	{
		if (square_width != square_height && square_width != Functions.nearestPowerOf2(square_width))
		{
			Log.e("Incorrect Image Size", "Width and Height are not square");
			return new int[0];
		}
		
		// what to return
		int[] my_array = new int[2];
		
		// prepare buffers
		int stride = 3 * square_width;
		ByteBuffer out_rgb = ByteBuffer.allocateDirect(square_height * stride).order(ByteOrder.nativeOrder());
		ByteBuffer out_a = ByteBuffer.allocate(0);
		if (has_alpha)
			out_a = ByteBuffer.allocateDirect(square_height * stride).order(ByteOrder.nativeOrder());
		
		// split image
		splitRGBA(image, square_width, square_height, stride, out_rgb, out_a);
		
		// rgb compression and loading
		ETC1Util.ETC1Texture rgb_compressed = compress(out_rgb, square_width, square_height);
		my_array[0] = loadETC1(rgb_compressed);
		
		// alpha compression and loading
		if (has_alpha)
		{
			ETC1Util.ETC1Texture a_compressed = compress(out_a, square_width, square_height);
			my_array[1] = loadETC1(a_compressed);
		}
		
		return my_array;
	}
	
	// returns the handle to this texture
	public int loadETC1(ETC1Util.ETC1Texture etc1Texture)
	{
		int id = GLBitmapReader.newTextureID();
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
		
		// Set all of our texture parameters:
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		
		// push etc1 to gpu
		if (use_stream_io) // Test the ETC1Util APIs for reading and writing compressed textures to I/O streams.
			try
			{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ETC1Util.writeTexture(etc1Texture, bos);
				ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
				ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, bis);
			}
			catch (IOException e)
			{
				Log.e("ETC1 Conversion Error", "Could not load texture: trace: ", e);
			}
		else
			ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, etc1Texture);
		
		return id;
	}
	
	public int getETC1Hash(int resource_id)
	{
		if (hash == -1 || input == null)
		{
			input = Constants.resources.openRawResource(resource_id);
			hash = input.hashCode();
		}
		
		return hash;
	}
	
	public int loadETC1(int resource_id)
	{
		int id = GLBitmapReader.newTextureID();
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
		
		// Set all of our texture parameters:
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		
		getETC1Hash(resource_id);
		
		try
		{
			ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, input);
		}
		catch (IOException e)
		{
			Log.e("ETC1 Conversion Error", "Could not load texture: id=" + resource_id, e);
		}
		finally
		{
			try
			{
				input.close();
			}
			catch (IOException e)
			{
				// ignore exception thrown from close.
			}
		}
		
		input = null;
		
		return id;
	}
	
	public ETC1Util.ETC1Texture compress(ByteBuffer image, int width, int height)
	{
		return ETC1Util.compressTexture(image, width, height, 3, 3 * width);
	}
	
	/* WARNING */
	// the below method sometimes will fail
	// Use at your own risk.
	
	// split an image into two channels
	// so we can load both into our shader
	// and still have both channels despite ETC1 not supporting both
	public void splitRGBA(Bitmap image, int width, int height, int stride, ByteBuffer out_rgb, ByteBuffer out_a)
	{
		byte[] rgb_array = new byte[height * stride];
		byte[] a_array = new byte[height * stride];
		
		int max_all_array = width * height;
		int[] all_array = new int[max_all_array];
		// decompose the image into a byte array
		image.getPixels(all_array, 0, width, 0, 0, width, height);
		
		// scan through all pixels and allocate to new byte array
		int z = 0;
		for (int x = 0; x < max_all_array; x++)
		{
			int pixel = all_array[x];
			byte alpha = (byte) (pixel >>> 24);
			byte red = (byte) ((pixel >> 16) & 0xFF);
			byte green = (byte) ((pixel >> 8) & 0xFF);
			byte blue = (byte) (pixel & 0xFF);
			
			// set new pixel color to output image
			rgb_array[z] = red;
			rgb_array[z + 1] = green;
			rgb_array[z + 2] = blue;
			
			a_array[z] = alpha;
			a_array[z + 1] = 0;
			a_array[z + 2] = 0;
			
			z += 3;
		}
		
		out_rgb = out_rgb.put(rgb_array);
		
		if (out_a.capacity() > 0)
			out_a = out_a.put(a_array);
		
		out_rgb.position(0);
		out_a.position(0);
	}
}
