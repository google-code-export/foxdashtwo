package com.kobaj.graphics;

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

//you may be saying, brilliant, he extended BaseLight!
//well no...
public class PointLight
{
	Paint base_paint;
	Paint outline_paint;
	Paint save_paint;
	
	Rect screen;
	
	public PointLight()
	{
		
	}
	
	public void onInitialize(int color, int intensity, int x, int y)
	{
		base_paint = new Paint();
		base_paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
		ColorFilter filter = new LightingColorFilter(color, 1);
		base_paint.setColorFilter(filter);
		//base_paint.setAntiAlias(true);
		
		RadialGradient shader_light = new RadialGradient(x, y, intensity, color, Color.TRANSPARENT, Shader.TileMode.CLAMP);
		outline_paint = new Paint();
		outline_paint.setShader(shader_light);
		//outline_paint.setAntiAlias(true);

		save_paint = new Paint();
		//save_paint.setAntiAlias(true);
		save_paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SCREEN));
		
		screen = new Rect(x - intensity, y - intensity, x + intensity, y + intensity);
	}
	
	public void onDraw(Canvas canvas, Bitmap bitmap, Rect src, Rect dst)
	{
		int sc = startDraw(canvas);
		drawNoBloom(canvas, bitmap, src, dst);
		endDraw(canvas, sc);
	}
	
	public void onDrawWithBloom(Canvas canvas, Bitmap bitmap, Rect src, Rect dst)
	{
		int sc = startDraw(canvas);
		drawNoBloom(canvas, bitmap, src, dst);
		drawBloom(canvas);
		endDraw(canvas, sc);
	}
	
	private int startDraw(Canvas canvas)
	{
		return  canvas.saveLayer(screen.left, screen.bottom, screen.right, screen.top, save_paint, 
				Canvas.MATRIX_SAVE_FLAG | 
				Canvas.CLIP_SAVE_FLAG | 
				Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | 
				Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
				Canvas.CLIP_TO_LAYER_SAVE_FLAG); 
	}
	
	private void drawNoBloom(Canvas canvas, Bitmap bitmap, Rect src, Rect dst)
	{
		// obscuring light
		outline_paint.setXfermode(null);
		outline_paint.setAlpha(255);
		canvas.drawRect(screen, outline_paint);
		
		// light
		canvas.drawBitmap(bitmap, src, dst, base_paint);
	}
	
	private void drawBloom(Canvas canvas)
	{
		// bloom and after effect
		outline_paint.setAlpha(50);
		canvas.drawRect(screen, outline_paint);
	}
	
	private void endDraw(Canvas canvas, int sc)
	{
		// put the scene back
		canvas.restoreToCount(sc);
	}
}
