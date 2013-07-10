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
import com.kobaj.loader.GLBitmapReader;
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
	
	private int count = 0;
	
	// turn off to have numbers be dynamically shifted.
	// turn on to have all letters spaced evenly
	private boolean fixed_width = true;
	private double fixed_width_value = 0; // dont touch, auto calculated later
	
	public Text()
	{
		double size = Constants.text_size;
		
		// new bitmap_buffer!
		bitmap_buffer = new SparseArray<Quad>();
		string_container = new SparseArray<String>();
		
		// begin by getting all our strings
		String m_test_array[];
		m_test_array = Constants.resources.getStringArray(R.array.my_sa);
		
		ArrayList<String> my_string_array = new ArrayList<String>();
		
		// fill in our array list
		for (int i = 0; i < 10; i++)
			my_string_array.add(String.valueOf(i));
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
		
		// for fixed sizes calculate width
		if (fixed_width)
		{
			for (int i = 0; i < 10; i++)
			{
				fixed_width_value = Math.max(fixed_width_value, bitmap_buffer.get(i).shader_width);
			}
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
	
	public int generateString(String s)
	{
		return generateString(s, Constants.text_size);
	}
	
	// generate a string and return a key that will get that string.
	// note, you probably shouldn't call this except at loading time,
	// and never call this during game play
	public int generateString(String s, double size)
	{
		// see if already contained
		int checked_key = string_container.indexOfValue(s);
		if (checked_key > 0)
			return checked_key;
		
		// find a key
		// not the most efficient.
		// but 'shouldnt' have collision problems.
		int key = 0;
		boolean key_found = false;
		while (!key_found)
		{
			key = GLBitmapReader.newResourceID();
			
			if (bitmap_buffer.get(key) == null)
				key_found = true;
		}
		
		// generate
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
			paint_temp.setFilterBitmap(false);
			
			Paint paint_stroke = new Paint();
			paint_stroke.setColor(Color.BLACK);
			paint_stroke.setTextAlign(Paint.Align.CENTER);
			paint_stroke.setTextSize((float) size);
			paint_stroke.setTypeface(Typeface.DEFAULT_BOLD);
			paint_stroke.setStyle(Paint.Style.STROKE);
			paint_stroke.setStrokeWidth(6);
			paint_stroke.setAntiAlias(true);
			paint_stroke.setFilterBitmap(false);
			
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
	
	// x and y are shader coords. this is centered around the decimal
	public void drawDecimalNumber(double number, int number_of_predecimals, int number_of_decimals, double x, double y)
	{
		drawDecimalNumber(number, number_of_predecimals, number_of_decimals, x, y, Color.WHITE);
	}
	
	public void drawDecimalNumber(double number, int number_of_predecimals, int number_of_decimals, double x, double y, int color)
	{
		boolean negative = false;
		if (number < 0)
			negative = true;
		
		number = Math.abs(number);
		int value = (int) number;
		double decimal = number - value;
		
		int pre_decimal = value;
		if (negative)
			pre_decimal = -value;
		
		drawIntNumber(pre_decimal, x, y, EnumDrawFrom.bottom_right, color);
		if (value < Math.pow(10, number_of_predecimals))
		{
			Quad temp = bitmap_buffer.get(R.string.full_stop);
			drawText(R.string.full_stop, x - temp.shader_width, y, EnumDrawFrom.bottom_right, color);
			
			double moved_decimal = decimal;
			double total_width = 0;
			
			// draw zeros and then draw final number
			for (int i = 0; i < number_of_decimals; i++)
			{
				moved_decimal *= 10.0;
				int decimal_digit = (int) (moved_decimal % 10.0);
				drawIntNumber(decimal_digit, (x - temp.shader_width) + total_width, y, EnumDrawFrom.bottom_left, color);
				
				if (fixed_width)
					total_width += fixed_width_value;
				else
					total_width += bitmap_buffer.get(decimal_digit).shader_width;
			}
		}
	}
	
	// x and y are in shader coordinates 0 to 1
	public void drawIntNumber(int this_number, double x, double y, EnumDrawFrom where)
	{
		drawIntNumber(this_number, x, y, where, Color.WHITE);
	}
	
	// if you want to do the math to rotate an int, be my guest. I'll be over here...
	
	public void drawIntNumber(int this_number, double x, double y, EnumDrawFrom where, int color)
	{
		double total_width = 0;
		
		boolean negative = false;
		if (this_number < 0)
			negative = true;
		
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
		
		int number = this_number;
		while (number > 0)
		{
			int key = (number % 10);
			
			if (zero)
				key = 0;
			
			if (fixed_width)
				total_width += fixed_width_value;
			else
				total_width += bitmap_buffer.get(key).shader_width;
			
			number = number / 10;
		}
		
		if (negative)
		{
			if (fixed_width)
				total_width += fixed_width_value;
			else
				total_width += bitmap_buffer.get(R.string.negative).shader_width;
			
		}
		
		if (where == EnumDrawFrom.top_left || where == EnumDrawFrom.bottom_left)
			current_width = -total_width;
		else if (where == EnumDrawFrom.top_right || where == EnumDrawFrom.bottom_right)
		{
			// do nothing
		}
		else
			// center
			current_width = (-total_width / 2.0);
		
		// begin drawing the number
		number = this_number;
		while (number > 0)
		{
			// get the number
			int key = (number % 10);
			
			if (zero)
				key = 0;
			
			Quad temp = bitmap_buffer.get(key);
			
			// translate
			if (fixed_width)
				current_width += fixed_width_value;
			else
				current_width += temp.shader_width;
			
			temp.setXYPos(x - current_width, y, where);
			
			// draw
			temp.color = color;
			temp.onDrawAmbient(Constants.my_ip_matrix, true);
			
			// continue
			number = number / 10;
		}
		
		if (negative)
		{
			Quad temp = bitmap_buffer.get(R.string.negative);
			
			double y_pos = y;
			if (where == EnumDrawFrom.bottom_left || where == EnumDrawFrom.bottom_right)
				y_pos += (bitmap_buffer.get(0).shader_height / 2.0) - (temp.shader_height / 2.0);
			else if (where == EnumDrawFrom.top_left || where == EnumDrawFrom.top_right)
				y_pos -= (bitmap_buffer.get(0).shader_height / 2.0) - (temp.shader_height / 2.0);
			
			if (fixed_width)
				current_width += fixed_width_value;
			else
				current_width += temp.shader_width;
			
			temp.setXYPos(x - current_width, y_pos, where);
			temp.onDrawAmbient(Constants.my_ip_matrix, true);
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
		if(color == Color.TRANSPARENT)
			return;
		
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
