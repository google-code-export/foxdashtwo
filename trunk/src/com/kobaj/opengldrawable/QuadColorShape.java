package com.kobaj.opengldrawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class QuadColorShape extends Quad
{
	private QuadColorShape()
	{
		super(0);
	}
	
	//this is in screen coordinates 0-800
	//square/rectangle
	public QuadColorShape(int left, int top, int right, int bottom, int color)
	{	
		super(findKey(), makeSquare(left, top, right, bottom, color));
	}
	
	//this is in screen coordinates 0-800
	//circle
	public QuadColorShape(int radius, int color)
	{
		super(findKey(), makeCircle(radius, color));
	}
	
	private static int findKey()
	{
		int key = 20;
		//could potentially get slow
		//but I hope nobody is making thousands of quads and getting to that slow point.
		while(com.kobaj.loader.GLBitmapReader.loaded_textures.containsKey(key))
			key += 1;
		
		return key;	
	}
	
	private static Bitmap makeSquare(int left, int top, int right, int bottom, int color)
	{
		Bitmap bitmap_temp = Bitmap.createBitmap((int)Math.abs(right - left), (int)Math.abs(bottom - top), Bitmap.Config.ARGB_8888);
		Canvas canvas_temp = new Canvas(bitmap_temp);
		canvas_temp.drawColor(color);
	
		return bitmap_temp;
	}
	
	private static Bitmap makeCircle(int radius, int color)
	{
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		
		Bitmap bitmap_temp = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
		Canvas canvas_temp = new Canvas(bitmap_temp);	
		canvas_temp.drawCircle((float)radius, (float)radius, radius, paint);
		
		return bitmap_temp;
	}
}
