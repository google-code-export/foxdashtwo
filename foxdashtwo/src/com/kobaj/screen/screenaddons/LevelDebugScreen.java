package com.kobaj.screen.screenaddons;

import java.util.ArrayList;

import com.kobaj.level.Level;
import com.kobaj.math.Functions;
import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class LevelDebugScreen
{
	private ArrayList<QuadColorShape> outline_quads;
	private EnumDebugType type;
	
	public LevelDebugScreen(Level test_level, EnumDebugType type)
	{
		this.type = type;
		outline_quads = new ArrayList<QuadColorShape>();
		
		if (type == EnumDebugType.original_aabb)
		{
			for (int i = test_level.object_list.size() - 1; i >= 0; i--)
			{
				Quad temp = test_level.object_list.get(i).quad_object;
				outline_quads.add(new QuadColorShape( // slightly inaccurate
						(int) Functions.shaderXToScreenX(temp.best_fit_aabb.main_rect.left), // left
						(int) Functions.shaderYToScreenY(temp.best_fit_aabb.main_rect.top), // top
						(int) Functions.shaderXToScreenX(temp.best_fit_aabb.main_rect.right), // right
						(int) Functions.shaderYToScreenY(temp.best_fit_aabb.main_rect.bottom), // bottom
						0x44FF00AA, // color
						0)); // blur
			}
		}
		else if (type == EnumDebugType.physics)
		{
			for (int i = test_level.object_list.size() - 1; i >= 0; i--)
			{
				Quad outer_temp = test_level.object_list.get(i).quad_object;
				for (int e = outer_temp.phys_rect_list.size() - 1; e >= 0; e--)
				{
					
					RectF temp = outer_temp.phys_rect_list.get(e).main_rect;
					outline_quads.add(new QuadColorShape( // slightly inaccurate
							(int) Functions.shaderXToScreenX(temp.left), // left
							(int) Functions.shaderYToScreenY(temp.top), // top
							(int) Functions.shaderXToScreenX(temp.right), // right
							(int) Functions.shaderYToScreenY(temp.bottom), // bottom
							0x4400FFAA, // color
							0)); // blur
				}
			}
		}
		else if (type == EnumDebugType.player_physics)
		{
			for (int i = test_level.player.quad_object.phys_rect_list.size() - 1; i >= 0; i--)
			{
				RectF temp = test_level.player.quad_object.phys_rect_list.get(i).main_rect;
				
				outline_quads.add(new QuadColorShape( // slightly inaccurate
						(int) Functions.shaderXToScreenX(temp.left), // left
						(int) Functions.shaderYToScreenY(temp.top), // top
						(int) Functions.shaderXToScreenX(temp.right), // right
						(int) Functions.shaderYToScreenY(temp.bottom), // bottom
						0x4400FFAA, // color
						0)); // blur
			}
		}
		else if (type == EnumDebugType.camera)
		{
			outline_quads.add(new QuadColorShape( //
					(int) Functions.shaderXToScreenX(Functions.shader_rectf_view.left),//
					(int) Functions.shaderYToScreenY(Functions.shader_rectf_view.top),//
					(int) Functions.shaderXToScreenX(Functions.shader_rectf_view.right),//
					(int) Functions.shaderYToScreenY(Functions.shader_rectf_view.bottom),//
					0x440000FF,//
					0));//
		}
	}
	
	private double timeout = 0;
	private int on_draw = 0;
	
	public void onUpdate(double delta, Level test_level)
	{
		// look at our ondraw
		timeout += delta;
		if (timeout > 2000)
		{
			timeout = 0;
			on_draw += 1;
			
			if (on_draw > outline_quads.size() - 1)
				on_draw = 0;
		}
		
		// then update positions and things
		if (type == EnumDebugType.original_aabb)
			for (int i = test_level.object_list.size() - 1; i >= 0; i--)
			{
				Quad temp = test_level.object_list.get(i).quad_object;
				Quad relative = outline_quads.get(i);

				relative.setXYPos(temp.x_pos_shader, temp.y_pos_shader, EnumDrawFrom.center);
			}
		else if (type == EnumDebugType.physics)
			for (int i = test_level.object_list.size() - 1; i >= 0; i--)
			{
				Quad temp = test_level.object_list.get(i).quad_object;
				for (int e = temp.phys_rect_list.size() - 1; e >= 0; e--)
					outline_quads.get(e).setXYPos(temp.x_pos_shader, temp.y_pos_shader, EnumDrawFrom.center);
			}
		else if (type == EnumDebugType.player_physics)
			for (int i = test_level.player.quad_object.phys_rect_list.size() - 1; i >= 0; i--)
				outline_quads.get(i).setXYPos(test_level.player.x_pos, test_level.player.y_pos, EnumDrawFrom.center);
		else if (type == EnumDebugType.camera)
			outline_quads.get(0).setXYPos(Functions.shader_rectf_view.left, Functions.shader_rectf_view.top, EnumDrawFrom.top_left);
	}
	
	public void onDrawObject()
	{
		// make this outline_quads.get(on_draw).onDrawAmbient() to walk through drawable quads.
		// kinda useful...I guess.
		
		for (int i = outline_quads.size() - 1; i >= 0; i--)
			outline_quads.get(i).onDrawAmbient();
		
		// outline_quads.get(on_draw).onDrawAmbient(Constants.my_vp_matrix, true);
	}
}
