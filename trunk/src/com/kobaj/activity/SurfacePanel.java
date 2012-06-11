package com.kobaj.activity;

import com.kobaj.foxdashtwo.R;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Shader.TileMode;

//surface class that updates and draws everything.
public class SurfacePanel extends DrawablePanel
{
	//create
	public SurfacePanel(Context context)
	{
		super(context);	
	}
	
	Bitmap bitmap;
	Paint paint;
	Paint blur;
	Paint gradient;
	Paint red_light;
	Paint blue_light;
	Paint ambient;
	
	Paint redP, greenP, blueP;
	
	// load in our resources
	public void onInitialize()
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
	    opt.inPreferredConfig = Config.ARGB_8888;
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.titlescreen, opt);
		paint = new Paint();
		BitmapShader shady = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
		
		paint.setShader(shady);
		
		blur = new Paint();
		//blur.setAntiAlias(true);
		//blur.setMaskFilter(new BlurMaskFilter(25, Blur.NORMAL));
		LinearGradient shader = new LinearGradient(
	            0, 
	            0,
	            0,
	            200,
	            0xFF00FF00, 0x00000000, TileMode.CLAMP);
		
		blur.setShader(shader);
	    blur.setStyle(Paint.Style.FILL);
	    //lighten acts like alight
	    //multiply changes color
		blur.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.LIGHTEN));
	    
	    gradient = new Paint();
	    gradient.setShader(shader);
	    gradient.setStyle(Paint.Style.FILL);
	
	    red_light = new Paint();
	    red_light.setColor(Color.RED);
	    red_light.setStyle(Paint.Style.FILL);
	    red_light.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.LIGHTEN));
	    
	    blue_light = new Paint();
	    blue_light.setColor(Color.RED);
		LinearGradient shader2 = new LinearGradient(
	            0, 
	            0,
	            0,
	            200,
	            0xFF0000FF, 0x00000000, TileMode.CLAMP);
		blue_light.setShader(shader2);
	    blue_light.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.LIGHTEN));
	    
	    ambient = new Paint();
	    ambient.setColor(Color.BLACK);
	    ambient.setStyle(Paint.Style.FILL);
	    ambient.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.LIGHTEN));
	    
	    
	    //new try
	    LinearGradient shader3 = new LinearGradient(
	            0, 
	            0,
	            0,
	            200,
	            0xFFFFFFFF, 0xFFFFFFFF, TileMode.CLAMP);
	    
	    redP = new Paint();
	    redP.setShader(shader3);
	    redP.setColorFilter(new PorterDuffColorFilter(Color.RED, android.graphics.PorterDuff.Mode.MULTIPLY));
	    //use this for a bright intense light
	    //redP.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SCREEN));
	    //use this for a subtle color change
	    redP.setXfermode(new AvoidXfermode(Color.RED, 255, AvoidXfermode.Mode.TARGET));
	    
	    greenP = new Paint();
	    greenP.setShader(shader3);
	    greenP.setColorFilter(new PorterDuffColorFilter(Color.GREEN,android.graphics.PorterDuff.Mode.MULTIPLY));
	    //greenP.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SCREEN));
	    greenP.setXfermode(new AvoidXfermode(Color.GREEN, 255, AvoidXfermode.Mode.TARGET));
	 
	    blueP = new Paint();
	    blueP.setShader(shader3);
	    blueP.setColorFilter(new PorterDuffColorFilter(Color.BLUE,android.graphics.PorterDuff.Mode.MULTIPLY));
	    //blueP.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SCREEN));
	    blueP.setXfermode(new AvoidXfermode(Color.BLUE, 255, AvoidXfermode.Mode.TARGET));
	}
	
	//run logic
	public void onUpdate(long gameTime)
	{
		
	}
	
	//draw to screen
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		//canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.drawRect(0,0,bitmap.getWidth(), bitmap.getHeight(), paint);
		
		//ambient
		//canvas.drawRect(0,0,400,400, ambient);
		
		//light
		canvas.drawRect(0, 0, 200, 250, redP);
		canvas.drawRect(100,0, 300, 250, greenP);
		canvas.drawRect(175,0, 230, 250, blueP);
		
		//regular square
		canvas.drawRect(305, 0, 480, 200, gradient);
	}
}
