package com.kobaj.level;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.android.RectF;

public class CoordMap
{
	private ArrayList<LevelObject> objects_copy;
	
	// x, y, values are shader based indexes
	public SparseArray<SparseArray<ArrayList<LevelObject>>> calculated_objects;
	
	private ArrayList<LevelObject> ignored_objects;
	
	public List<LevelObject> visible_objects;
	public Boolean[] untrimmed_visible_objects;
	
	private double half_width;
	private double half_height;
	private double half_width_shader;
	private double half_height_shader;
	
	public CoordMap(int level_width, int level_height, ArrayList<LevelObject> level_objects)
	{
		// number of bins is x * y;
		calculate_x_y(level_width, level_height);
		
		calculated_objects = new SparseArray<SparseArray<ArrayList<LevelObject>>>();
		ignored_objects = new ArrayList<LevelObject>();
		visible_objects = new ArrayList<LevelObject>();
		
		insert_objects(level_objects);
		
		objects_copy = level_objects;
		
		untrimmed_visible_objects = new Boolean[level_objects.size()];
		for (int i = 0; i < untrimmed_visible_objects.length; i++)
			untrimmed_visible_objects[i] = false;
	}
	
	public void insert_objects(ArrayList<LevelObject> level_objects)
	{
		if (level_objects == null)
			return;
		
		int count = 0;
		for (LevelObject j : level_objects)
		{
			j.sort_index = count;
			count++;
			
			if (j.ignore_coord_map)
				ignored_objects.add(j);
			else
			{
				RectF bounds = j.quad_object.best_fit_aabb.main_rect;
				
				int left_most_x = (int) Math.ceil(bounds.left / this.half_width_shader);
				int right_most_x = (int) Math.ceil(bounds.right / this.half_width_shader);
				
				int top_most_y = (int) Math.ceil(bounds.top / this.half_height_shader);
				int bottom_most_y = (int) Math.ceil(bounds.bottom / this.half_height_shader);
				
				for (int x = left_most_x; x <= right_most_x; x++)
				{
					SparseArray<ArrayList<LevelObject>> y_objects = calculated_objects.get(x, new SparseArray<ArrayList<LevelObject>>());
					if (y_objects.size() == 0)
						calculated_objects.put(x, y_objects);
					
					for (int y = bottom_most_y; y <= top_most_y; y++)
					{
						ArrayList<LevelObject> objects = y_objects.get(y, new ArrayList<LevelObject>());
						if (objects.size() == 0)
							y_objects.put(y, objects);
						
						objects.add(j);
					}
				}
			}
		}
	}
	
	public void updated_visible_objects()
	{
		visible_objects.clear();
		
		// add the ignored objects
		for (int i = ignored_objects.size() - 1; i >= 0; i--)
			this.untrimmed_visible_objects[ignored_objects.get(i).sort_index] = true;
		
		// int ignore_size = ignored_objects.size();
		// for (int i = 0; i < ignore_size; i++)
		// sorted_insert(ignored_objects.get(i));
		
		// find all objects in view of the camera
		Functions.updateShaderRectFView();
		int left_most_x = (int) Math.ceil(Functions.shader_rectf_view.left / this.half_width_shader);
		int right_most_x = (int) Math.ceil(Functions.shader_rectf_view.right / this.half_width_shader);
		
		int top_most_y = (int) Math.ceil(Functions.shader_rectf_view.top / this.half_height_shader);
		int bottom_most_y = (int) Math.ceil(Functions.shader_rectf_view.bottom / this.half_height_shader);
		
		for (int x = left_most_x; x <= right_most_x; x++)
		{
			int x_index = calculated_objects.indexOfKey(x);
			if (x_index >= 0)
			{
				SparseArray<ArrayList<LevelObject>> y_objects = calculated_objects.valueAt(x_index);
				
				for (int y = bottom_most_y; y <= top_most_y; y++)
				{
					
					int y_index = y_objects.indexOfKey(y);
					if (y_index >= 0)
					{
						
						ArrayList<LevelObject> objects = y_objects.valueAt(y_index);
						
						for (int i = objects.size() - 1; i >= 0; i--)
						{
							LevelObject temp = objects.get(i);
							
							this.untrimmed_visible_objects[temp.sort_index] = true;
							
							// sorted_insert(temp);
							
							// if (!visible_objects.contains(temp))
							// visible_objects.add(temp);
						}
					}
				}
			}
		}
		
		for (int i = 0; i < untrimmed_visible_objects.length; i++)
		{
			if (untrimmed_visible_objects[i])
			{
				visible_objects.add(objects_copy.get(i));
				untrimmed_visible_objects[i] = false;
			}
		}
		
		// and then sort
		// Collections.sort(visible_objects, new ObjectDrawSort());
	}
	
	public int[] calculate_x_y(int level_width, int level_height)
	{
		half_width = (Constants.width / 2.0);
		half_height = (Constants.height / 2.0);
		
		half_width_shader = Functions.screenWidthToShaderWidth(half_width);
		half_height_shader = Functions.screenHeightToShaderHeight(half_height);
		
		double number_x = level_width / half_width;
		double number_y = level_height / half_height;
		
		int count_x = (int) Math.ceil(number_x);
		int count_y = (int) Math.ceil(number_y);
		
		int[] returnables = new int[2];
		returnables[0] = count_x;
		returnables[1] = count_y;
		
		return returnables;
	}
	
}
