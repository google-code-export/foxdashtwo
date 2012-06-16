package com.kobaj.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
	
	//holds all strings
	HashMap<Integer, BaseSprite> bitmap_buffer;
	
	//nice paint.
	Paint paint;
	
	//nice constants
	private final int line_height = 4;
	private final int padding = 4;
	private final double text_size = 16.0;
	
	public Text()
	{
		//do nuffing!
	}
	
	public void onInitialize()
	{
		//begin with a paint
		paint = new Paint();
		
		//set default size
		double size = text_size * com.kobaj.math.Constants.sd_scale;
		
		//new bitmap_buffer!
		bitmap_buffer = new HashMap<Integer, BaseSprite>();
		
		//begin by getting all our strings
		String m_test_array[];
		m_test_array = com.kobaj.math.Constants.context.getResources().getStringArray(R.array.my_sa);    
	    
	    //add additionaly 0-9
	    ArrayList<String> my_string_array = new ArrayList<String>();
	    
	    //fill in our array list
	    for(int i = 0; i < 10; i++)
	    	my_string_array.add(Integer.toString(i));
	    for(String s: m_test_array)
	    	my_string_array.add(s);
	    
	    int count = 0;
	    //where it starts to get brilliant.
	    for(String s: my_string_array)
	    {
	    	int key = count;
	    	
	    	//grab the id 
	    	if(count > 10)
	    		key = com.kobaj.math.Constants.context.getResources().getIdentifier(s, "string", "com.kobaj.foxdashtwo");
	    
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
	    		
			//prep
			ArrayList<Path> path_splits = new ArrayList<Path>();
			
			int height = padding;
			int width = 0;
			
			//split it apart
			for (String line : s.split("\n"))
			{
		    	line = line.trim();
		    	
		    	//mini measurements
		    	Rect rect_temp = new Rect();
		    	paint_stroke.getTextBounds(line, 0, line.length(), rect_temp);
		    	
		    	int this_line_height = Math.abs(rect_temp.top - rect_temp.bottom);
		    	int this_line_width = rect_temp.left + rect_temp.right;
		    	
		    	int old_height = height;
		    	
		    	height += this_line_height + line_height;
		    	if(this_line_width > width)
		    		width = this_line_width;

		    	//create some paths
		    	Path line_path = new Path();
		    	paint_temp.getTextPath(line, 0, line.length(), padding, Math.abs(rect_temp.top) + old_height, line_path);
		    	path_splits.add(line_path);
			}
			
			//proper padding
			width += padding * 2;
			height -= line_height;
			height += padding;
			
	    	//fullscale measurements
	    	Bitmap bitmap_temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    	Canvas canvas_temp = new Canvas(bitmap_temp);
	    	
	    	//finaly after all that, draw it
	    	for(Path path: path_splits)
	    	{
	    		canvas_temp.drawPath(path, paint_stroke);
	    		canvas_temp.drawPath(path, paint_temp);
	    	}
	    	
	    	//stuff it in the buffer
	    	bitmap_buffer.put(key, new BaseSprite(bitmap_temp, new Rect(0,0, width, height)));
	    	
	    	count++;
	    }
	    
	    System.gc();
	}
	
	public void drawNumber(Canvas canvas, int this_number, int x, int y)
	{	
		int width = 0;
		
		//a little bit inefficient
		int number = this_number;
		while (number > 0)
		{
			int key = (number % 10);
			
			width += bitmap_buffer.get(key).bounding_rectangle.right;
			
			number = number / 10;
		}
		
		canvas.translate(width, 0);
		
		number = this_number;
		while (number > 0) 
		{
			int key = (number % 10);
			
			//translate
			int my_x = bitmap_buffer.get(key).bounding_rectangle.right; //effectively the width	
			canvas.translate(-my_x, 0);
			
			//draw
			canvas.drawBitmap(bitmap_buffer.get(key).bitmap, x, y, paint);
			
		    number = number / 10;
		}
	}
	
	public void drawText(Canvas canvas, int r_value, int x, int y)
	{
		if(bitmap_buffer.containsKey(r_value))
			canvas.drawBitmap(bitmap_buffer.get(r_value).bitmap, x, y, paint);
	}
	
	public void onDestroy()
	{
		Iterator<Entry<Integer, BaseSprite>> it = bitmap_buffer.entrySet().iterator();
	    while (it.hasNext()) 
	    {
	        HashMap.Entry<Integer, BaseSprite> pairs = (HashMap.Entry<Integer, BaseSprite>)it.next();
	        pairs.getValue().onDestroy();
	        it.remove();
	    }
	    it = null;
	}
}