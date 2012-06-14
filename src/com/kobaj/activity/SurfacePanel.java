package com.kobaj.activity;

import com.kobaj.foxdashtwo.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Paint.Style;
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
	
	Paint outline;
	
	// load in our resources
	public void onInitialize()
	{
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
	
	long change;
	long last;
	
	//run logic
	public void onUpdate(long gameTime)
	{
		change = gameTime -last;
		last = gameTime;
		
		
	}
	
	//draw to screen
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		float w = 480;
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
		
		//bloom
		outline.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
		outline.setAlpha(90);
		canvas.drawPath(pth, outline);
		
	    
	    //put the scene back
	    canvas.restoreToCount(sc);

	    //little bit of after effect
	    
		
		//light
		//canvas.drawRect(0, 0, 200, 250,	redP);
		//canvas.drawRect(100,0, 300, 250, greenP);
		//canvas.drawRect(175,0, 230, 250, blueP);
		
		//regular square
		canvas.drawRect(305, 0, 480, 200, gradient);
	}
}
