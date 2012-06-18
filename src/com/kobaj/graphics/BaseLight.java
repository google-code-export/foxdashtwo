package com.kobaj.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;

//basically an ambient light
public class BaseLight
{
	Paint base_paint;
	
	public BaseLight()
	{
		//do nothing
	}
	
	public void onInitialize(int color)
	{
		base_paint = new Paint();
		
		//let them set color
		ColorFilter filter = new LightingColorFilter(color, 1);
		base_paint.setColorFilter(filter);
		//base_paint.setAntiAlias(true);
	}
	
	public void onDraw(Canvas canvas, Bitmap bitmap, Rect src, Rect dst)
	{
		canvas.drawBitmap(bitmap, src, dst, base_paint);
	}
}
