package com.kobaj.screen.screenaddons;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.RectF;

import com.kobaj.level.Level;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class BaseDebugScreen
{
	private ArrayList<QuadColorShape> outline_quads;
	private boolean only_outlines; //show object outline, or physics rectangles
	private boolean show_lights; //show lights or not
	
	public BaseDebugScreen(Level test_level, boolean only_outlines, boolean show_lights)
	{
		outline_quads = new ArrayList<QuadColorShape>();
		this.only_outlines = only_outlines;
		this.show_lights = show_lights;
		
		// lights
		if(show_lights)
		for (int i = test_level.light_list.size() - 1; i >= 0; i--)
			outline_quads.add(onMakeBoundingBox(test_level.light_list.get(i).quad_light));
		
		//events
		for (int i = test_level.event_list.size() - 1; i >= 0; i--)
			outline_quads.add(onMakeBoundingBox(test_level.event_list.get(i).my_collision_rect));
		
		// object
		if (only_outlines)
		{
			for (int e = test_level.object_list.size() - 1; e >= 0; e--)
				outline_quads.add(onMakeBoundingBox(test_level.object_list.get(e).quad_object));
			
			outline_quads.add(onMakeBoundingBox(test_level.player.quad_object));
		}
		else
		{
			// draw some helpful bounding boxes
			for (int e = test_level.object_list.size() - 1; e >= 0; e--)
				for (int i = test_level.object_list.get(e).quad_object.phys_rect_list.size() - 1; i >= 0; i--)
					outline_quads.add(onMakeBoundingBox(test_level.object_list.get(e).quad_object.phys_rect_list.get(i).main_rect));
			for (int i = test_level.player.quad_object.phys_rect_list.size() - 1; i >= 0; i--)
				outline_quads.add(onMakeBoundingBox(test_level.player.quad_object.phys_rect_list.get(i).main_rect));
		}
	}
	
	public void onDrawObject(Level test_level)
	{
		//update everything
		
		// lights
		int j = 0;
		if(show_lights)
		for (int i = test_level.light_list.size() - 1; i >= 0; i--, j++)
			onUpdateBoundingBox(test_level.light_list.get(i).quad_light, j);
		
		//events
		for(int i = test_level.event_list.size() - 1; i>=0; i--, j++)
			onUpdateBoundingBox(test_level.event_list.get(i).my_collision_rect, j);
		
		// object
		if (only_outlines)
		{
			for (int e = test_level.object_list.size() - 1; e >= 0; e--, j++)
				onUpdateBoundingBox(test_level.object_list.get(e).quad_object, j);
			
			onUpdateBoundingBox(test_level.player.quad_object, j);
		}
		else
		{
			// draw some helpful bounding boxes
			for (int e = test_level.object_list.size() - 1; e >= 0; e--, j++)
				for (int i = test_level.object_list.get(e).quad_object.phys_rect_list.size() - 1; i >= 0; i--, j++)
					onUpdateBoundingBox(test_level.object_list.get(e).quad_object.phys_rect_list.get(i).main_rect, j);
			for (int i = test_level.player.quad_object.phys_rect_list.size() - 1; i >= 0; i--, j++)
				onUpdateBoundingBox(test_level.player.quad_object.phys_rect_list.get(i).main_rect, j);
		}
		
		//draw everything
		for(int q = outline_quads.size() - 1; q >= 0; q--)
			outline_quads.get(q).onDrawAmbient(Constants.my_view_matrix, Constants.my_proj_matrix, Color.WHITE, true);
	}
	
	private void onUpdateBoundingBox(Quad my_quad, int j)
	{
		onUpdateBoundingBox(my_quad.getXPos(), my_quad.getYPos() + my_quad.shader_height,
				my_quad.getXPos() + my_quad.shader_width, my_quad.getYPos(), j);
	}
	
	private void onUpdateBoundingBox(RectF bounding_box, int j)
	{
		double left = Functions.shaderXToScreenX(bounding_box.left);
		double top = Functions.shaderYToScreenY(bounding_box.top);
		double right = Functions.shaderXToScreenX(bounding_box.right);
		double bottom = Functions.shaderYToScreenY(bounding_box.bottom);
		
		onUpdateBoundingBox(left, top, right,bottom, j);
	}
	
	private void onUpdateBoundingBox(double left, double top, double right, double bottom, int j)
	{	
		// holy garbage creation batman
		outline_quads.get(j).setPos(left, bottom, EnumDrawFrom.center);
	}
	
	
	
	
	private QuadColorShape onMakeBoundingBox(Quad my_quad)
	{
		return onMakeBoundingBox((int) Functions.shaderXToScreenX(my_quad.getXPos()), (int) Functions.shaderYToScreenY(my_quad.getYPos() + my_quad.shader_height),
				(int) Functions.shaderXToScreenX(my_quad.getXPos() + my_quad.shader_width), (int) Functions.shaderYToScreenY(my_quad.getYPos()));
	}
	
	private QuadColorShape onMakeBoundingBox(RectF bounding_box)
	{
		double left = Functions.shaderXToScreenX(bounding_box.left);
		double top = Functions.shaderYToScreenY(bounding_box.top);
		double right = Functions.shaderXToScreenX(bounding_box.right);
		double bottom = Functions.shaderYToScreenY(bounding_box.bottom);
		
		return onMakeBoundingBox((int) left, (int) top, (int) right, (int) bottom);
	}
	
	// this is in screen coords
	private QuadColorShape onMakeBoundingBox(int left, int top, int right, int bottom)
	{
		// holy garbage creation batman
		QuadColorShape outline = new QuadColorShape(left - 1, top + 1, right + 1, bottom - 1, 0x55FF00FF, 0);
		return outline;
	}
	
}
