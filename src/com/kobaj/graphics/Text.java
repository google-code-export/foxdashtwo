package com.kobaj.graphics;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.kobaj.foxdashtwo.R;

public class Text{
	
	HashMap<Integer, Bitmap> bitmap_buffer;
	
	public Text()
	{
		//do nuffing!
	}
	
	public void onInitialize()
	{
		//set default size
		double size = 16.0 * com.kobaj.math.Constants.sd_scale;
		
		//new bitmap_buffer!
		bitmap_buffer = new HashMap<Integer, Bitmap>();
		
		//begin by getting all our strings
		String mTestArray[];
	    mTestArray = com.kobaj.math.Constants.context.getResources().getStringArray(R.array.my_sa);    
	    
	    //this is where it gets brilliant
	    for(String s: mTestArray)
	    {
	    	s = s.trim();
	    	
	    	//grab the id 
	    	int id_value = com.kobaj.math.Constants.context.getResources().getIdentifier(s, "string", "com.kobaj.foxdashtwo");
	    
	    	//generate an image
	    	Paint paint_temp = new Paint();
	    	paint_temp.setAntiAlias(true);
	    	paint_temp.setStyle(Style.FILL);
	    	paint_temp.setColor(Color.WHITE);
	    	paint_temp.setTextSize((float) size);
	    	
	    	
	    	Paint paint_stroke = new Paint();
	    	paint_stroke.setColor(Color.BLACK);
			paint_stroke.setTextAlign(Paint.Align.CENTER);
			paint_stroke.setTextSize((float) size);
			paint_stroke.setTypeface(Typeface.DEFAULT_BOLD);
			paint_stroke.setStyle(Paint.Style.STROKE);
			paint_stroke.setStrokeWidth(2);
			paint_stroke.setAntiAlias(true);
	    	
	    	//see how big it will be
	    	Rect rect_temp = new Rect();
	    	paint_stroke.getTextBounds(s, 0, s.length(), rect_temp);
	    	int height = rect_temp.top - rect_temp.bottom;
			
	    	Bitmap bitmap_temp = Bitmap.createBitmap(rect_temp.left + rect_temp.right, Math.abs(height), Bitmap.Config.ARGB_8888);
	    	Canvas canvas_temp = new Canvas(bitmap_temp);
	    	
	    	canvas_temp.drawColor(Color.WHITE); //erase this line
	    	
	    	//create some paths
	    	Path string_path = new Path();
	    	paint_temp.getTextPath(s, 0, s.length(), 0, Math.abs(rect_temp.top), string_path);
	    	
	    	//finaly after all that, draw it
	    	canvas_temp.drawPath(string_path, paint_stroke);
	    	canvas_temp.drawPath(string_path, paint_temp);
	    	
	    	//stuff it in the buffer
	    	bitmap_buffer.put(id_value, bitmap_temp);
	    }
	    
	    System.gc();
	}
	
	public void drawAll(Canvas canvas)
	{
		for(Bitmap b: bitmap_buffer.values())
		{
			canvas.drawBitmap(b, 50, 50, new Paint());
		}
	}
}