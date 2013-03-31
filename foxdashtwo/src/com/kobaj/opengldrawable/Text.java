package com.kobaj.opengldrawable;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.SparseArray;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.Quad.Quad;

// funfact, HashMap.get is O(1) when there is no collision, and SparseArray.get is O(log n).
// BUT HashMap makes a but load of garbage >.<
public class Text
{
	// has set of quads
	private SparseArray<Quad> bitmap_buffer;
	private SparseArray<String> string_container;
	
	// nice constants
	private final int line_height = 4;
	private final int padding = 4;
	
	public double size = 0;
	private int count = 0;
	
	public Text()
	{	
		size = Constants.text_size * Constants.sd_scale;
		
		// new bitmap_buffer!
		bitmap_buffer = new SparseArray<Quad>();
		string_container = new SparseArray<String>();
		
		// begin by getting all our strings
		String m_test_array[];
		m_test_array = Constants.resources.getStringArray(R.array.my_sa);
		
		ArrayList<String> my_string_array = new ArrayList<String>();
		
		// fill in our array list
		for (int i = 0; i < 10; i++)
			my_string_array.add(Integer.toString(i));
		// this is ok because it is an actual array
		for (String s : m_test_array)
			my_string_array.add(s);
		
		// where it starts to get brilliant.
		int buffer_size = my_string_array.size();
		for (int i = 0; i < buffer_size; i++)
		{
			String s = my_string_array.get(i);
		
			int key = count;
			
			// grab the id
			if (count > 9)
			{
				key = Constants.resources.getIdentifier(s, "string", "com.kobaj.foxdashtwo");
				if (key != 0)
					s = Constants.resources.getString(key);
				else
					s = null;
			}
			
			// actual creation
			generateString(s, size, key);
			
			count++;
		}
		
		// bump up the size
		m_test_array = Constants.resources.getStringArray(R.array.my_sa2);
		my_string_array.clear();
		// this is ok because it is an actual array
		for (String s : m_test_array)
			my_string_array.add(s);
		
		buffer_size = my_string_array.size();
		for (int i = 0; i < buffer_size; i++)
		{
			String s = my_string_array.get(i);
			
			// grab the id
			int key = Constants.resources.getIdentifier(s, "string", "com.kobaj.foxdashtwo");
			if (key != 0)
				s = Constants.resources.getString(key);
			else
				s = null;
			
			// actual creation
			generateString(s, size * 2.0, key);
			
			count++;
		}
		
		System.gc();
	}
	
	// generate a string and return a key that will get that string.
	// note, you probably shouldn't call this except at loading time, 
	// and never call this during game play
	public int generateString(String s, double size)
	{
		//see if already contained
		int checked_key = string_container.indexOfValue(s);
		if(checked_key > 0)
			return checked_key;
		
		// find a key
		// not the most efficient.
		// but 'shouldnt' have collision problems.
		int key = 0;
		boolean key_found = false;
		while(!key_found)
		{
			key = count;
			count++;
			
			if(bitmap_buffer.get(key) == null)
				key_found = true;		
		}
		
		//generate
		generateString(s, size, key);
		
		return key;
	}
	
	private void generateString(String s, double size, int key)
	{
		if (s != null)
		{
			// generate an image
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
			
			// prep
			ArrayList<Path> path_splits = new ArrayList<Path>();
			
			int height = padding;
			int width = 0;
			
			// split it apart
			for (String line : s.split("\n"))
			{
				line = line.trim();
				
				// mini measurements
				Rect rect_temp = new Rect();
				paint_stroke.getTextBounds(line, 0, line.length(), rect_temp);
				
				int this_line_height = Math.abs(rect_temp.top - rect_temp.bottom);
				int this_line_width = rect_temp.left + rect_temp.right;
				
				int old_height = height;
				
				height += this_line_height + line_height;
				if (this_line_width > width)
					width = this_line_width;
				
				// create some paths
				Path line_path = new Path();
				paint_temp.getTextPath(line, 0, line.length(), padding, Math.abs(rect_temp.top) + old_height, line_path);
				path_splits.add(line_path);
			}
			
			// proper padding
			width += padding * 2;
			height -= line_height;
			height += padding;
			
			// fullscale measurements
			Bitmap bitmap_temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
			Canvas canvas_temp = new Canvas(bitmap_temp);
			
			// finaly after all that, draw it
			for (int i = path_splits.size() - 1; i >= 0; i--)
			{
				canvas_temp.drawPath(path_splits.get(i), paint_stroke);
				canvas_temp.drawPath(path_splits.get(i), paint_temp);
			}
			
			// stuff it in the buffer
			// note this automatically destroys the bitmap
			bitmap_buffer.put(key, new Quad(key, bitmap_temp, bitmap_temp.getWidth(), bitmap_temp.getHeight()));
		}
	}
	
	// x and y are in shader coordinates 0 to 1
	public void drawNumber(int this_number, double x, double y, EnumDrawFrom where)
	{
		drawNumber(this_number, x, y, where, Color.WHITE);
	}
	
	// if you want to do the math to rotate an int, be my guest. I'll be over here...
	
	public void drawNumber(int this_number, double x, double y, EnumDrawFrom where, int color)
	{
		double total_width = 0;
		
		// for now we only do positives (sorry).
		this_number = (int) Math.abs(this_number);
		
		boolean zero = false;
		if (this_number == 0)
		{
			// just so our calculations below are correct.
			// dont worry, we'll still draw a zero.
			this_number = 2;
			zero = true;
		}
		
		// prepare to draw by seeing where we draw it.
		double current_width = 0;
		if (where == EnumDrawFrom.top_left || where == EnumDrawFrom.bottom_left)
		{
			int number = this_number;
			while (number > 0)
			{
				int key = (number % 10);
				
				total_width += bitmap_buffer.get(key).shader_width;
				
				number = number / 10;
			}
			
			current_width = -total_width;
		}
		else if (where == EnumDrawFrom.center)
			current_width = (-total_width / 2.0);
		
		// begin drawing the number
		int number = this_number;
		while (number > 0)
		{
			// get the number
			int key = (number % 10);
			
			if (zero)
				key = 0;
			
			Quad temp = bitmap_buffer.get(key);
			
			// translate
			current_width += temp.shader_width;
			temp.setXYPos(x - current_width, y, where);
			
			// draw
			temp.color = color;
			temp.onDrawAmbient(Constants.my_ip_matrix, true);
			
			// continue
			number = number / 10;
		}
	}
	
	// x and y are in shader coordinates 0 to 1
	public void drawText(int resource_value, double x, double y, EnumDrawFrom where)
	{
		drawText(resource_value, x, y, where, Color.WHITE);
	}
	
	public void drawText(int resource_value, double x, double y, EnumDrawFrom where, int color)
	{
		drawText(resource_value, x, y, where, color, 0);
	}
	
	public void drawText(int resource_value, double x, double y, EnumDrawFrom where, int color, double degree)
	{
		if (bitmap_buffer.indexOfKey(resource_value) >= 0)
		{
			// optimize the gets
			Quad temp = bitmap_buffer.get(resource_value);
			
			if (temp.degree != degree)
				temp.setRotationZ(degree);
			temp.setXYPos(x, y, where);
			
			// draw pretty!
			temp.color = color;
			temp.onDrawAmbient(Constants.my_ip_matrix, true);
		}
	}
	
	// return the size of a section of text in screen coords
	public int measureTextWidth(int resource_id)
	{
		if (bitmap_buffer.indexOfKey(resource_id) >= 0)
		{
			Quad temp = bitmap_buffer.get(resource_id);
			return temp.width;
		}
		
		return 0;
	}
	
	public int measureTextHeight(int resource_id)
	{
		if (bitmap_buffer.indexOfKey(resource_id) >= 0)
		{
			Quad temp = bitmap_buffer.get(resource_id);
			return temp.height;
		}
		
		return 0;
	}
	
	public void onUnInitialize()
	{
		for (int i = bitmap_buffer.size() - 1; i >= 0; i--)
			bitmap_buffer.get(bitmap_buffer.keyAt(i)).onUnInitialize();
	}
}
