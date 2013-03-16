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
	// a note for future generations
	// this class could be made a bit faster
	// be storing an array of already made bitmaps
	// and seeing if we can just reuse said bitmap
	// instead of regenerating it
	// but this would require a rewrite of the quad system...
	
	// this is in screen coordinates 0-800
	// square/rectangle
	public QuadColorShape(int width, int height, int color, int blur_amount)
	{
		this(0, height, width, 0, color, blur_amount);
	}
	
	public QuadColorShape(int left, int top, int right, int bottom, int color, int blur_amount)
	{
		super(GLBitmapReader.newResourceID(), Functions.fastBlur(makeSquare(left, top, right, bottom, color), blur_amount), right - left, top - bottom);
	}
	
	// circle
	public QuadColorShape(double radius, int color, int blur_amount)
	{
		super(GLBitmapReader.newResourceID(), Functions.fastBlur(makeCircle(radius, color), blur_amount), (int) radius * 2, (int) radius * 2);
	}
	
	// gradient circle
	// this boolean might decide if its a bloom or not
	// the whole point of bloom is to not act as a light, but more of an overlay. kind of that
	// "ahh, the light is so bright its blinding me and I cant see behind it" effect...
	// this is in screen coordinates 0-800
	public QuadColorShape(double radius, int color, boolean is_bloom, int blur_amount)
	{
		super(GLBitmapReader.newResourceID(), Functions.fastBlur(makeCircleGradient(radius, color, is_bloom), blur_amount), (int) radius * 2, (int) radius * 2);
	}
	
	// gradient circle with mask (aka, think a flashlight).
	// this is in screen coordinates 0-800
	public QuadColorShape(double radius, int color, int close_width, int far_width, boolean is_bloom, int blur_amount)
	{
		super(GLBitmapReader.newResourceID(), Functions.fastBlur(makeCircleGradientWithMask(radius, color, close_width, far_width, is_bloom), blur_amount), (int) radius * 2, (int) radius * 2);
	}
	
	private static Bitmap makeSquare(int left, int top, int right, int bottom, int color)
	{
		// very simple, draw a square
		Bitmap bitmap_temp = Bitmap.createBitmap((int) Math.abs(right - left), (int) Math.abs(bottom - top), Bitmap.Config.ARGB_4444);
		Canvas canvas_temp = new Canvas(bitmap_temp);
		canvas_temp.drawColor(color);
		
		return bitmap_temp;
	}
	
	private static Bitmap makeCircle(double radius, int color)
	{
		// setup our paints
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		
		// make a bitmap that is the right size.
		Bitmap bitmap_temp = Bitmap.createBitmap((int) (radius * 2.0), (int) (radius * 2.0), Bitmap.Config.ARGB_4444);
		Canvas canvas_temp = new Canvas(bitmap_temp);
		
		// draw background then circle,
		canvas_temp.drawCircle((float) radius, (float) radius, (float) radius, paint);
		
		return bitmap_temp;
	}
	
	private static Bitmap makeCircleGradient(double radius, int color, boolean is_bloom)
	{
		// prepare the center of the screen
		int xy = (int) radius;
		
		// make our circular gradient
		RadialGradient shader_light;
		int secondary_color;
		if (!is_bloom)
			secondary_color = Color.BLACK;
		else
			secondary_color = Color.TRANSPARENT;
		shader_light = new RadialGradient(xy, xy, (float) radius, color, secondary_color, Shader.TileMode.CLAMP);
		
		// this paint holds the gradient
		Paint outline_paint = new Paint();
		outline_paint.setShader(shader_light);
		outline_paint.setAntiAlias(true);
		
		// put the gradient on a rectangle
		Rect screen = new Rect();
		screen.left = (int) (xy - radius);
		screen.top = (int) (xy - radius);
		screen.right = (int) (xy + radius);
		screen.bottom = (int) (xy + radius);
		
		// create the canvas
		Bitmap bitmap_temp = Bitmap.createBitmap((int) (radius * 2), (int) (radius * 2), Bitmap.Config.ARGB_4444);
		Canvas canvas_temp = new Canvas(bitmap_temp);
		
		// obscuring light
		outline_paint.setXfermode(null);
		
		// draw background, then draw square/circle gradient overtop
		canvas_temp.drawRect(screen, outline_paint);
		
		return bitmap_temp;
	}
	
	private static Bitmap makeCircleGradientWithMask(double radius, int color, double close_width, double far_width, boolean is_bloom)
	{
		// begin by grabbing a full circle gradient
		Bitmap light = makeCircleGradient(radius, color, is_bloom);
		
		// make a paint that will obscure parts of that full circle
		Paint mask_paint = new Paint();
		mask_paint.setAntiAlias(true);
		mask_paint.setStyle(Paint.Style.FILL);
		mask_paint.setColor(Color.BLACK);
		if (is_bloom)
			mask_paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_OUT));
		
		// draw out the path
		Path path = new Path();
		
		double half_close_width = close_width / 2.0;
		double half_far_width = far_width / 2.0;
		
		// check close boundaries
		if (half_close_width > radius)
		{
			half_close_width = radius;
			close_width = 2.0 * half_close_width;
		}
		
		if (half_far_width > radius)
		{
			half_far_width = radius;
			far_width = 2.0 * half_close_width;
		}
		
		// draw mask
		double diameter = radius * 2.0;
		double extra = radius - half_far_width;
		float buffer = 0;
		
		path.moveTo((float) extra - buffer, (float) diameter + buffer);
		path.lineTo(0 - buffer, (float) diameter + buffer);
		path.lineTo(0 - buffer, 0 - buffer);
		path.lineTo((float) diameter + buffer, 0 - buffer);
		path.lineTo((float) diameter + buffer, (float) diameter + buffer);
		path.lineTo((float) (diameter - extra + buffer), (float) diameter + buffer);
		path.lineTo((float) (radius + half_close_width), (float) radius);
		path.lineTo((float) (radius - half_close_width), (float) radius);
		path.lineTo((float) extra - buffer, (float) diameter + buffer); // end where we started
		
		path.close();
		
		// make a bitmap
		Bitmap bitmap_temp = Bitmap.createBitmap((int) (radius * 2), (int) (radius * 2), Bitmap.Config.ARGB_4444);
		Canvas canvas_temp = new Canvas(bitmap_temp);
		
		// rotate and draw
		canvas_temp.rotate(-90.0f, (float) radius, (float) radius);
		if (!is_bloom)
			canvas_temp.drawColor(Color.BLACK);
		canvas_temp.drawBitmap(light, 0, 0, new Paint()); // may have to use a different paint here.
		canvas_temp.drawPath(path, mask_paint);
		
		return bitmap_temp;
	}
}
