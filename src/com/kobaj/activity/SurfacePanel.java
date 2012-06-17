package com.kobaj.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.kobaj.foxdashtwo.R;
import com.kobaj.graphics.BaseLight;
import com.kobaj.graphics.BaseSprite;
import com.kobaj.graphics.PointLight;
import com.kobaj.input.InputManager;
import com.kobaj.math.FPSManager;

//surface class that updates and draws everything.
public class SurfacePanel extends DrawablePanel
{
	//fps
	FPSManager fps;
	
	//input
	public InputManager input_manager;
	
	//text
	com.kobaj.graphics.Text text;
	
	//other variables
	BaseSprite bs;
	BaseLight bl;
	PointLight pl, plbloom, plthree;
	
	//create
	public SurfacePanel(Context context)
	{
		super(context);	
		
		//fps
		fps = new FPSManager();
		
		input_manager = new InputManager();
		
		//text
		text = new com.kobaj.graphics.Text();
		
		//everything else
		BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Config.ARGB_8888;
		Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.titlescreen, opt);
		bs = new BaseSprite(temp, new Rect(0,0,800,480));
		bl = new BaseLight();
		pl = new PointLight();
		plbloom = new PointLight();
		plthree = new PointLight();
	}
	
	// load in our resources
	public void onInitialize()
	{
		//text
		text.onInitialize();	
		
		//everything else
		bl.onInitialize(0x00111111);
		pl.onInitialize(0xFF0000FF, 600, 200, 200);
		plbloom.onInitialize(0xFF00FF00, 600, 600, 200);
		plthree.onInitialize(0xFFFF0000, 600, 400, 400);
			
	}
	
	//run logic
	public void onUpdate(long gameTime)
	{
		fps.onUpdate(gameTime);
	}
	
	//draw to screen
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	    //test
		Rect temp = new Rect(0,0, 800, 480);
	    bl.onDraw(canvas, bs.bitmap, temp, temp);
	    
			if (input_manager.getTouched(0))
			{
				/*pl.onDrawWithBloom(canvas,  bs.bitmap, temp, temp);
				plbloom.onDrawWithBloom(canvas, bs.bitmap, temp, temp);
				plthree.onDrawWithBloom(canvas, bs.bitmap, temp, temp);
				*/
			}
			else
			{
				pl.onDraw(canvas,  bs.bitmap, temp, temp);
				plbloom.onDraw(canvas, bs.bitmap, temp, temp);
				plthree.onDraw(canvas, bs.bitmap, temp, temp);
			}
	    
		//mmmmm fps
		text.drawNumber(canvas, fps.getFPS(), 50, 50);
	}
	
	public void onDestroy()
	{
		text.onDestroy();
	}
}
