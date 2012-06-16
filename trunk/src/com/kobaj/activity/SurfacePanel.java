package com.kobaj.activity;

import com.kobaj.math.*;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.DisplayMetrics;

import com.kobaj.foxdashtwo.FoxdashtwoActivity;
import com.kobaj.foxdashtwo.R;

//surface class that updates and draws everything.
public class SurfacePanel extends DrawablePanel
{
	//fps
	FPSManager fps;
	
	//other variables
	Bitmap bitmap;
	Paint paint;
	Paint blur;
	Paint gradient;
	Paint red_light;
	Paint blue_light;
	Paint ambient;
	
	Paint redP, greenP, blueP;
	
	Paint outline;
	
	com.kobaj.graphics.Text text;
	
	//create
	public SurfacePanel(Context context)
	{
		super(context);	
	}
	
	// load in our resources
	public void onInitialize()
	{
		text = new com.kobaj.graphics.Text();
		text.onInitialize();
		
		//fps
		fps = new FPSManager();
		
		gradient = new Paint();
		gradient.setColor(Color.GREEN);
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
	    opt.inPreferredConfig = Config.ARGB_8888;
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.titlescreen, opt);
		paint = new Paint();
		ColorFilter filter = new LightingColorFilter(Color.argb(255, 100, 100, 100), 1);
		paint.setColorFilter(filter);
		
	    blueP = new Paint();
	    //blueP.setColorFilter(new PorterDuffColorFilter(Color.BLUE,android.graphics.PorterDuff.Mode.MULTIPLY));
	    blueP.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
	    
		RadialGradient shader4 = new RadialGradient(240, 400, 250, Color.WHITE, Color.TRANSPARENT, Shader.TileMode.CLAMP);
	    outline = new Paint();
	    outline.setShader(shader4);
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
		/*float w = 480;
		float h = 800;
		
		//ambient base layer
		canvas.drawBitmap(bitmap, 0, 0, paint);//canvas.drawRect(0,0,bitmap.getWidth(), bitmap.getHeight(), paint);
		
		//move the bitmap offscreen
		int sc = canvas.saveLayer(0, 0, w, h, null,
                Canvas.MATRIX_SAVE_FLAG |
                Canvas.CLIP_SAVE_FLAG |
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                Canvas.CLIP_TO_LAYER_SAVE_FLAG);
		
		//canvas.translate(w / 2.0f, 0);
		
		//obscuring light
		outline.setXfermode(null);
		outline.setAlpha(255);
		Path pth = new Path();
	    pth.moveTo(w*0.27f,0);
	    pth.lineTo(w*0.73f,0);
	    pth.lineTo(w*0.92f,h);
	    pth.lineTo(w*0.08f,h);
	    pth.lineTo(w*0.27f,0);
	    canvas.drawPath(pth,outline);
		
		//light
		canvas.drawBitmap(bitmap, 0 , 0, blueP);
		
		//bloom and after effect
		outline.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
		outline.setAlpha(90);
		canvas.drawPath(pth, outline);
		
	    //put the scene back
	    canvas.restoreToCount(sc);*/
	    
	    //test drawing some text
	    text.drawNumber(canvas, 121234, 50,50);
		
		//regular square
		canvas.drawRect(0,0,50,50, gradient);
	}
	
	public void onDestroy()
	{
		text.onDestroy();
	}
}
