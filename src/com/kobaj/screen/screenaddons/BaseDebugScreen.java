package com.kobaj.screen.screenaddons;

import android.graphics.RectF;

import com.kobaj.level.Level;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class BaseDebugScreen
{
	public void onDrawObject(Level test_level)
	{
		// draw some helpful bounding boxes
		for (com.kobaj.level.LevelObject level_object : test_level.object_list)
			for (int i = level_object.quad_object.phys_rect_list.size() - 1; i >= 0; i--)
				onDrawBoundingBox(level_object.quad_object.phys_rect_list.get(i).main_rect);
		for (int i = test_level.player.quad_object.phys_rect_list.size() - 1; i >= 0; i--)
			onDrawBoundingBox(test_level.player.quad_object.phys_rect_list.get(i).main_rect);
		
	}
	
	private void onDrawBoundingBox(RectF bounding_box)
	{
		double left = Functions.shaderXToScreenX(bounding_box.left);
		double top = Functions.shaderYToScreenY(bounding_box.top);
		double right = Functions.shaderXToScreenX(bounding_box.right);
		double bottom = Functions.shaderYToScreenY(bounding_box.bottom);
		
		double x_center = bounding_box.centerX();
		double y_center = bounding_box.centerY();
		
		// holy garbage creation batman
		QuadColorShape outline = new QuadColorShape((int) left, (int) top, (int) right, (int) bottom, 0x99FF00FF, 0);
		outline.setPos(x_center, y_center, EnumDrawFrom.center);
		outline.onDrawAmbient(Constants.my_view_matrix, Constants.my_proj_matrix, Constants.ambient_light, true);
	}
}
