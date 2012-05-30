package com.kobaj.activity;

import android.content.Context;
import android.graphics.Canvas;

//surface class that updates and draws everything.
public class SurfacePanel extends DrawablePanel
{
	//create
	public SurfacePanel(Context context)
	{
		super(context);	
	}
	
	// load in our resources
	public void onInitialize()
	{

	}
	
	//run logic
	public void onUpdate(long gameTime)
	{
		
	}
	
	//draw to screen
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	}
}
