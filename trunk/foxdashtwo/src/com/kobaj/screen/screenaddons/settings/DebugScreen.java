package com.kobaj.screen.screenaddons.settings;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.kobaj.loader.GLBitmapReader;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.Quad.Quad;

public class DebugScreen
{
	public static Quad mouse(int color)
	{
		Bitmap temp = makeSquare(0, Constants.height, Constants.width, 0, 0x00000000);
		
		temp = drawCross(color, temp);
		
		Quad reference = new Quad(GLBitmapReader.newResourceID(), temp, Constants.width, Constants.height);
		
		return reference;
	}
	
	private static Bitmap drawCross(int color, Bitmap bitmap_temp)
	{
		Paint mask_paint = new Paint();
		mask_paint.setAntiAlias(true);
		mask_paint.setStyle(Paint.Style.STROKE);
		mask_paint.setColor(color);
		
		// draw out the path
		Path path = new Path();
		
		path.moveTo(Constants.width / 2, Constants.height);
		path.lineTo(Constants.width / 2, 0);
		path.lineTo(Constants.width, 0);
		path.lineTo(Constants.width, Constants.height  / 2);
		path.lineTo(0, Constants.height  / 2);
		
		// draw
		Canvas canvas_temp = new Canvas(bitmap_temp);
		canvas_temp.drawPath(path, mask_paint);
		
		return bitmap_temp;	
	}
	
	// this is in shader coordinates BUT NOTE this so far ONLY works in the original 0-800 and 0-480 texture coordinates
	public static Quad outline(RectF... rects)
	{
		Bitmap temp = makeSquare(0, Constants.height, Constants.width, 0, 0x66FF0000);
		
		int rect_count = 0;
		
		for (RectF rect : rects)
		{
			double left = Functions.shaderXToScreenX(rect.left);
			double top = Functions.shaderYToScreenY(rect.top);
			double right = Functions.shaderXToScreenX(rect.right);
			double bottom = Functions.shaderYToScreenY(rect.bottom);
			
			top = Functions.fix_y(top);
			bottom = Functions.fix_y(bottom);
			
			int color = Color.BLUE;
			if (rect_count == 1)
				color = Color.YELLOW;
			if (rect_count == 2)
				color = Color.GREEN;
			if (rect_count > 2)
				rect_count = 0;
			
			temp = drawRectOnBitmap((int) left, (int) top, (int) right, (int) bottom, color, temp);
			
			rect_count++;
		}
		
		Quad reference = new Quad(GLBitmapReader.newResourceID(), temp, Constants.width, Constants.height);
		
		return reference;
	}
	
	private static Bitmap drawRectOnBitmap(int left, int top, int right, int bottom, int color, Bitmap bitmap_temp)
	{
		Paint mask_paint = new Paint();
		mask_paint.setAntiAlias(true);
		mask_paint.setStyle(Paint.Style.STROKE);
		mask_paint.setColor(color);
		
		// draw out the path
		Path path = new Path();
		
		path.moveTo(left, top);
		path.lineTo(right, top);
		path.lineTo(right, bottom);
		path.lineTo(left, bottom);
		path.lineTo(left, top); // end where we started
		
		// draw
		Canvas canvas_temp = new Canvas(bitmap_temp);
		canvas_temp.drawPath(path, mask_paint);
		
		return bitmap_temp;
	}
	
	private static Bitmap makeSquare(int left, int top, int right, int bottom, int color)
	{
		// very simple, draw a square
		Bitmap bitmap_temp = Bitmap.createBitmap((int) Math.abs(right - left), (int) Math.abs(bottom - top), Bitmap.Config.ARGB_8888);
		Canvas canvas_temp = new Canvas(bitmap_temp);
		canvas_temp.drawColor(color);
		
		return bitmap_temp;
	}
}
