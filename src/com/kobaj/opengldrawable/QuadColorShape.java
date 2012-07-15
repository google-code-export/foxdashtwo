package com.kobaj.opengldrawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
		super(findKey(), makeCircle(radius, color));
	}
	
	//gradient circle
	//this boolean might decide if its a bloom or not in the future
	//for now it just differentiats between the two types of circles to draw.
	public QuadColorShape(int radius, int color, boolean this_does_nothing)
	{
		super(findKey(), makeCircleGradient(radius, color));
	}
	
	public QuadColorShape(int radius, int color, int close_width, int far_width)
	{
		super(findKey(), makeCircleGradientWithMask(radius, color, close_width, far_width));
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
	
	private static Bitmap makeCircleGradient(double radius, int color)
	{
		int x = (int) radius;
		int y = (int) radius;
        
        RadialGradient shader_light = new RadialGradient(x, y, (float) radius, color, Color.BLACK, Shader.TileMode.CLAMP);
        Paint outline_paint = new Paint();
        outline_paint.setShader(shader_light);
        outline_paint.setAntiAlias(true);

        Rect screen = new Rect();
        screen.left = (int) (x - radius);
        screen.top = (int) (y - radius);
        screen.right = (int) (x + radius);
        screen.bottom = (int) (y + radius);
        
        Bitmap bitmap_temp = Bitmap.createBitmap((int)(radius * 2), (int)(radius * 2), Bitmap.Config.ARGB_8888);
		Canvas canvas_temp = new Canvas(bitmap_temp);
        
        // obscuring light
        outline_paint.setXfermode(null);
        outline_paint.setAlpha(255);
        canvas_temp.drawRect(screen, outline_paint);
        
        // bloom and after effect
        //outline_paint.setAlpha(50);
        //canvas_temp.drawRect(screen, outline_paint);

        return bitmap_temp;
	}
	
	private static Bitmap makeCircleGradientWithMask(double radius, int color, double close_width, double far_width)
	{
		Bitmap light = makeCircleGradient(radius, color);
		
		Paint mask_paint = new Paint();
		mask_paint.setAntiAlias(true);
		mask_paint.setStyle(Paint.Style.FILL);
		mask_paint.setColor(Color.BLACK);
		
		Path path = new Path();
		
		double half_close_width = close_width / 2.0;
		double half_far_width = far_width / 2.0;
		
		//check close boundaries
		if(half_close_width > radius)
		{
			half_close_width = radius;
			close_width = 2.0 * half_close_width;
		}
		
		double diameter = radius * 2;
		
		//draw mask
		if(half_far_width > radius)
		{
			double extra = half_far_width - radius;
			
			path.moveTo((float)-extra, (float)diameter);
			path.lineTo((float)-extra, 0);
			path.lineTo((float)(diameter + extra), 0);
			path.lineTo((float)(diameter + extra), (float)diameter);
			path.lineTo((float)(radius + half_close_width), (float)radius);
			path.lineTo((float)(radius - half_close_width), (float)radius);
			path.lineTo((float)-extra, (float)diameter); //end where we started
		}
		else	
		{
			double extra = radius - half_far_width;
			
			path.moveTo((float)extra, (float)diameter);
			path.lineTo(0, (float)diameter);
			path.lineTo(0, 0);
			path.lineTo((float)diameter, 0);
			path.lineTo((float)diameter, (float)diameter);
			path.lineTo((float)(diameter - extra), (float)diameter);
			path.lineTo((float)(radius + half_close_width), (float)radius);
			path.lineTo((float)(radius - half_close_width), (float)radius);
			path.lineTo((float)extra, (float)diameter); //end where we started
		}
		path.close(); 
		
		Bitmap bitmap_temp = Bitmap.createBitmap((int)(radius * 2), (int)(radius * 2), Bitmap.Config.ARGB_8888);
		Canvas canvas_temp = new Canvas(bitmap_temp);
		canvas_temp.drawBitmap(light, 0, 0, mask_paint); // may have to use a different paint here.
		canvas_temp.drawPath(path, mask_paint);
		
		return bitmap_temp;
	}
}
