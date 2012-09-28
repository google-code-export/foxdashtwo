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
import com.kobaj.openglgraphics.AmbientLight;

public class Text
{
	// has set of quads
	private SparseArray<Quad> bitmap_buffer;

    // has an ambient light
    private AmbientLight my_ambient_light;
	
	// nice constants
	private final int line_height = 4;
	private final int padding = 4;
	
	public Text()
	{
        // setup our ambient light
        my_ambient_light = new AmbientLight();
		
		// set default size
		double size = Constants.text_size * Constants.sd_scale;
		
		// new bitmap_buffer!
		bitmap_buffer = new SparseArray<Quad>();
		
		// begin by getting all our strings
		String m_test_array[];
		m_test_array = Constants.context.getResources().getStringArray(R.array.my_sa);
		
		// add additional 0-9
		ArrayList<String> my_string_array = new ArrayList<String>();
		
		// fill in our array list
		for (int i = 0; i < 10; i++)
			my_string_array.add(Integer.toString(i));
		//this is ok because it is an actual array
		for (String s : m_test_array)
			my_string_array.add(s);
		
		int count = 0;
		// where it starts to get brilliant.
		for (String string_name : my_string_array)
		{
			int key = count;
			String s = string_name;
			
			// grab the id
			if (count > 9)
			{
				key = Constants.context.getResources().getIdentifier(s, "string", "com.kobaj.foxdashtwo");
				s = Constants.context.getResources().getString(key);
			}
			
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
			Bitmap bitmap_temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas canvas_temp = new Canvas(bitmap_temp);
			
			// finaly after all that, draw it
			for(int i = path_splits.size() - 1; i >= 0; i--)
			{
				canvas_temp.drawPath(path_splits.get(i), paint_stroke);
				canvas_temp.drawPath(path_splits.get(i), paint_temp);
			}
			
			// stuff it in the buffer
			// note this automatically destroys the bitmap
			bitmap_buffer.put(key, new Quad(key, bitmap_temp));
			
			count++;
		}
		
		System.gc();
	}
	
	// x and y are in shader coordinates 0 to 1
	public void DrawNumber(int this_number, double x, double y, EnumDrawFrom where)
	{
		double total_width = 0;
		
        // set the light
        my_ambient_light.applyShaderProperties();
        
		// for now we only do positives (sorry).
		this_number = (int) Math.abs(this_number);
		
		//prepare to draw by seeing where we draw it.
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
		
		//begin drawing the number
		int number = this_number;
		while (number > 0)
		{
			//get the number
			int key = (number % 10);
			Quad temp = bitmap_buffer.get(key);
			
			// translate
			current_width += temp.shader_width;
			temp.setPos(x - current_width, y, where);
			
			// draw
			temp.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);
			
			//continue
			number = number / 10;
		}
	}
	
	// x and y are in shader coordinates 0 to 1
	public void DrawText(int resource_value, double x, double y, EnumDrawFrom where)
	{
		if (bitmap_buffer.indexOfKey(resource_value) >= 0)
		{
			// optimize the gets
			Quad temp = bitmap_buffer.get(resource_value);
			
			temp.setPos(x, y, where);
			
            // set the light
            my_ambient_light.applyShaderProperties();
			
			// draw pretty!
			temp.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);
		}
	}
}
