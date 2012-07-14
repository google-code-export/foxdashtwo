package com.kobaj.opengldrawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;

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
		super(findKey(), makeCircleGradient(radius,color));
		//super(findKey(), makeCircle(radius, color));
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
	
	//intensity is actually radius
	//TODO go through and clean this up proper
	private static Bitmap makeCircleGradient(double intensity, int color)
	{
		int x = (int) intensity;
		int y = (int) intensity;
		
		Paint base_paint = new Paint();
        base_paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        ColorFilter filter = new LightingColorFilter(color, 1);
        base_paint.setColorFilter(filter);
        base_paint.setAntiAlias(true);
        
        RadialGradient shader_light = new RadialGradient(x, y, (float) intensity, color, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        Paint outline_paint = new Paint();
        outline_paint.setShader(shader_light);
        outline_paint.setAntiAlias(true);

        Paint save_paint = new Paint();
        save_paint.setAntiAlias(true);
        save_paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SCREEN));
        
        Rect screen = new Rect();
        screen.left = (int) (x - intensity);
        screen.top = (int) (y - intensity);
        screen.right = (int) (x + intensity);
        screen.bottom = (int) (y + intensity);
        
        Bitmap bitmap_temp = Bitmap.createBitmap((int)(intensity * 2), (int)(intensity * 2), Bitmap.Config.ARGB_8888);
		Canvas canvas_temp = new Canvas(bitmap_temp);
        
        // obscuring light
        outline_paint.setXfermode(null);
        outline_paint.setAlpha(255);
        canvas_temp.drawRect(screen, outline_paint);
        
        // bloom and after effect
        outline_paint.setAlpha(50);
        //canvas_temp.drawRect(screen, outline_paint);

        return bitmap_temp;
	}
}
