package com.kobaj.graphics;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class BaseSprite
{
	public Rect bounding_rectangle;
	public Bitmap bitmap;
	
	//this might need to be changed to onInitialize to match?
	public BaseSprite(Bitmap bitmap, Rect rect)
	{
		this.bounding_rectangle = rect;
		this.bitmap = bitmap;	
	}
	
	public boolean get_touch(int x, int y)
	{
		if(bounding_rectangle.contains(x,y))
			return true;
		
		return false;
	}
	
	public void onDestroy()
	{
		bounding_rectangle = null;
		bitmap.recycle();
		bitmap = null;
	}
}
