package com.kobaj.opengldrawable.Quad;

import com.kobaj.loader.GLBitmapReader;
import com.kobaj.math.Functions;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;

public class QuadColorShape extends Quad
{	
	//this is in screen coordinates 0-800
	//square/rectangle
	public QuadColorShape(int left, int top, int right, int bottom, int color, int blur_amount)
	{	
		super(GLBitmapReader.newResourceID(), Functions.fastBlur(makeSquare(left, top, right, bottom, color), blur_amount), right-left, top-bottom);
	}
	
	//this is in screen coordinates 0-800
	//circle
	public QuadColorShape(double radius, int color, int blur_amount)
	{
		super(GLBitmapReader.newResourceID(), Functions.fastBlur(makeCircle(radius, color), blur_amount), (int)radius * 2, (int)radius * 2);
	}
	
	//gradient circle
	//this boolean might decide if its a bloom or not in the future
	//the whole point of bloom is to not act as a light, but more of an overlay. kind of that
	//"ahh, the light is so bright its blinding me and I cant see behind it" effect...
	//this is in screen coordinates 0-800
	public QuadColorShape(double radius, int color, boolean is_bloom, int blur_amount)
	{
		super(GLBitmapReader.newResourceID(), Functions.fastBlur(makeCircleGradient(radius, color, is_bloom), blur_amount), (int)radius * 2, (int)radius * 2);
	}
	
	//gradient circle with mask
	//this is in screen coordinates 0-800
	public QuadColorShape(double radius, int color, int close_width, int far_width, double degree, boolean is_bloom, int blur_amount)
	{
		super(GLBitmapReader.newResourceID(), Functions.fastBlur(makeCircleGradientWithMask(radius, color, close_width, far_width, degree, is_bloom), blur_amount), (int)radius * 2, (int)radius * 2);
	}
	
	private static Bitmap makeSquare(int left, int top, int right, int bottom, int color)
	{
		Bitmap bitmap_temp = Bitmap.createBitmap((int)Math.abs(right - left), (int)Math.abs(bottom - top), Bitmap.Config.ARGB_8888);
		Canvas canvas_temp = new Canvas(bitmap_temp);
		canvas_temp.drawColor(color);
	
		return bitmap_temp;
	}
	
	private static Bitmap makeCircle(double radius, int color)
	{
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		
		Bitmap bitmap_temp = Bitmap.createBitmap((int)(radius * 2.0), (int)(radius * 2.0), Bitmap.Config.ARGB_8888);
		Canvas canvas_temp = new Canvas(bitmap_temp);	
		canvas_temp.drawCircle((float)radius, (float)radius, (float)radius, paint);
		
		return bitmap_temp;
	}
	
	private static Bitmap makeCircleGradient(double radius, int color, boolean is_bloom)
	{
		int x = (int) radius;
		int y = (int) radius;
        
        RadialGradient shader_light;
        if(!is_bloom)
        	shader_light = new RadialGradient(x, y, (float) radius, color, Color.BLACK, Shader.TileMode.CLAMP);
        else
        	shader_light = new RadialGradient(x, y, (float) radius, color, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        
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
        if(!is_bloom)
        	outline_paint.setAlpha(255);
        else
        	outline_paint.setAlpha(100);
        
        canvas_temp.drawRect(screen, outline_paint);
        
        return bitmap_temp;
	}
	
	private static Bitmap makeCircleGradientWithMask(double radius, int color, double close_width, double far_width, double degree, boolean is_bloom)
	{
		Bitmap light = makeCircleGradient(radius, color, is_bloom);
		
		Paint mask_paint = new Paint();
		mask_paint.setAntiAlias(true);
		mask_paint.setStyle(Paint.Style.FILL);
		mask_paint.setColor(Color.BLACK);
		if(is_bloom)
			mask_paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_OUT));
		
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
		
		Bitmap bitmap_temp = Bitmap.createBitmap((int)(radius * 2) - 2, (int)(radius * 2) - 2, Bitmap.Config.ARGB_8888);
		Canvas canvas_temp = new Canvas(bitmap_temp);
		canvas_temp.rotate((float) degree - 90.0f, (float)radius, (float)radius);
		canvas_temp.drawBitmap(light, -1, -1, new Paint()); // may have to use a different paint here.
		canvas_temp.drawPath(path, mask_paint);
		
		return bitmap_temp;
	}
}
