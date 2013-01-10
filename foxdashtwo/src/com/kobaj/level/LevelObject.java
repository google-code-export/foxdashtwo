package com.kobaj.level;

import org.simpleframework.xml.Element;

import android.graphics.Color;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.AverageMaker;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.RectFExtended;
import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadColorShape;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public class LevelObject extends LevelEntityActive
{
	@Element
	public EnumLevelObject this_object;
	
	public final EnumDrawFrom draw_from = EnumDrawFrom.bottom_left;
	@Element
	public double x_pos; // screen coordinates
	@Element
	public double y_pos; // screen coordinates
	@Element
	public double z_plane; // generally 1-9, with 1 being closest to the 'front' or 'camera'
	@Element
	public double scale;
	@Element
	public double degree;
	@Element
	public String id = "unset"; // identifier.
	
	public Quad quad_object;
	
	// usually read only
	// screen coords
	public double width;
	public double height;
	
	// these dont change,
	public double x_pos_shader;
	public double y_pos_shader;
	
	public void onInitialize()
	{
		/* Level 1 (l1) objects */
		if (this_object == EnumLevelObject.l1_bg1)
			quad_object = new QuadCompressed(R.raw.l1_bg1, R.raw.l1_bg1_alpha, 1659, 1064);
		else if (this_object == EnumLevelObject.l1_bg2)
			quad_object = new QuadCompressed(R.raw.l1_bg2, R.raw.l1_bg2_alpha, 1551, 1080);
		else if (this_object == EnumLevelObject.l1_fungus1)
			quad_object = new QuadCompressed(R.raw.l1_fungus1, R.raw.l1_fungus1_alpha, 73, 354);
		else if (this_object == EnumLevelObject.l1_fungus2)
			quad_object = new QuadCompressed(R.raw.l1_fungus2, R.raw.l1_fungus2_alpha, 299, 278);
		else if (this_object == EnumLevelObject.l1_fungus3)
			quad_object = new QuadCompressed(R.raw.l1_fungus3, R.raw.l1_fungus3_alpha, 57, 50);
		else if (this_object == EnumLevelObject.l1_fungus4)
			quad_object = new QuadCompressed(R.raw.l1_fungus4, R.raw.l1_fungus4_alpha, 82, 78);
		else if (this_object == EnumLevelObject.l1_layer14)
			quad_object = new QuadCompressed(R.raw.l1_layer14, R.raw.l1_layer14_alpha, 247, 226);
		else if (this_object == EnumLevelObject.l1_layer17)
			quad_object = new QuadCompressed(R.raw.l1_layer17, R.raw.l1_layer17_alpha, 696, 347);
		else if (this_object == EnumLevelObject.l1_layer18)
			quad_object = new QuadCompressed(R.raw.l1_layer18, R.raw.l1_layer18_alpha, 676, 280);
		else if (this_object == EnumLevelObject.l1_new_platform)
			quad_object = new QuadCompressed(R.raw.l1_new_platform, R.raw.l1_new_platform_alpha, 749, 301);
		else if (this_object == EnumLevelObject.l1_new_platform_2)
			quad_object = new QuadCompressed(R.raw.l1_new_platform_2, R.raw.l1_new_platform_2_alpha, 679, 366);
		else if (this_object == EnumLevelObject.l1_new_platform_3)
			quad_object = new QuadCompressed(R.raw.l1_new_platform_3, R.raw.l1_new_platform_3_alpha, 600, 359);
		else if (this_object == EnumLevelObject.l1_new_platform_4)
			quad_object = new QuadCompressed(R.raw.l1_new_platform_4, R.raw.l1_new_platform_4_alpha, 385, 220);
		else if (this_object == EnumLevelObject.l1_new_platform_5)
			quad_object = new QuadCompressed(R.raw.l1_new_platform_5, R.raw.l1_new_platform_5_alpha, 254, 223);
		else if (this_object == EnumLevelObject.l1_new_platform_6)
			quad_object = new QuadCompressed(R.raw.l1_new_platform_6, R.raw.l1_new_platform_6_alpha, 408, 265);
		else if (this_object == EnumLevelObject.l1_prot_rock)
			quad_object = new QuadCompressed(R.raw.l1_prot_rock, R.raw.l1_prot_rock_alpha, 469, 181);
		else if (this_object == EnumLevelObject.l1_prot_rock_2)
			quad_object = new QuadCompressed(R.raw.l1_prot_rock_2, R.raw.l1_prot_rock_2_alpha, 530, 173);
		else if (this_object == EnumLevelObject.l1_prot_rock_3)
			quad_object = new QuadCompressed(R.raw.l1_prot_rock_3, R.raw.l1_prot_rock_3_alpha, 326, 195);
		else if (this_object == EnumLevelObject.l1_shroom1)
			quad_object = new QuadCompressed(R.raw.l1_shroom1, R.raw.l1_shroom1_alpha, 256, 367);
		else if (this_object == EnumLevelObject.l1_shroom2)
			quad_object = new QuadCompressed(R.raw.l1_shroom2, R.raw.l1_shroom2_alpha, 107, 111);
		else if (this_object == EnumLevelObject.l1_shroom3)
			quad_object = new QuadCompressed(R.raw.l1_shroom3, R.raw.l1_shroom3_alpha, 111, 113);
		else if (this_object == EnumLevelObject.l1_shroom4)
			quad_object = new QuadCompressed(R.raw.l1_shroom4, R.raw.l1_shroom4_alpha, 183, 229);
		
		/* Level 2 (l2) objects */
		else if (this_object == EnumLevelObject.l2_accent_grass)
			quad_object = new QuadCompressed(R.raw.l2_accent_grass, R.raw.l2_accent_grass_alpha, 130, 84);
		else if (this_object == EnumLevelObject.l2_background_gradient)
			quad_object = new QuadCompressed(R.raw.l2_background_gradient, R.raw.l2_background_gradient_alpha, 2, 1080);
		else if (this_object == EnumLevelObject.l2_background_rocks)
			quad_object = new QuadCompressed(R.raw.l2_background_rocks, R.raw.l2_background_rocks_alpha, 592, 264);
		else if (this_object == EnumLevelObject.l2_background_rocks_big)
			quad_object = new QuadCompressed(R.raw.l2_background_rocks_big, R.raw.l2_background_rocks_big_alpha, 652, 226);
		else if (this_object == EnumLevelObject.l2_big_platform)
			quad_object = new QuadCompressed(R.raw.l2_big_platform, R.raw.l2_big_platform_alpha, 411, 216);
		else if (this_object == EnumLevelObject.l2_big_tall_platform)
			quad_object = new QuadCompressed(R.raw.l2_big_tall_platform, R.raw.l2_big_tall_platform_alpha, 281, 218);
		else if (this_object == EnumLevelObject.l2_big_wide_platform)
			quad_object = new QuadCompressed(R.raw.l2_big_wide_platform, R.raw.l2_big_wide_platform_alpha, 437, 231);
		else if (this_object == EnumLevelObject.l2_dead_grass)
			quad_object = new QuadCompressed(R.raw.l2_dead_grass, R.raw.l2_dead_grass_alpha, 438, 198);
		else if (this_object == EnumLevelObject.l2_end_platform)
			quad_object = new QuadCompressed(R.raw.l2_end_platform, R.raw.l2_end_platform_alpha, 66, 256);
		else if (this_object == EnumLevelObject.l2_medium_platform)
			quad_object = new QuadCompressed(R.raw.l2_medium_platform, R.raw.l2_medium_platform_alpha, 350, 255);
		else if (this_object == EnumLevelObject.l2_more_dead_grass)
			quad_object = new QuadCompressed(R.raw.l2_more_dead_grass, R.raw.l2_more_dead_grass_alpha, 314, 334);
		else if (this_object == EnumLevelObject.l2_small_platform)
			quad_object = new QuadCompressed(R.raw.l2_small_platform, R.raw.l2_small_platform_alpha, 193, 178);
		else if (this_object == EnumLevelObject.l2_small_right)
			quad_object = new QuadCompressed(R.raw.l2_small_right, R.raw.l2_small_right_alpha, 168, 179);
		else if (this_object == EnumLevelObject.l2_floating_platform)
		{
			quad_object = new QuadCompressed(R.raw.l2_floating_platform, R.raw.l2_floating_platform_alpha, 391, 198);
			RectF previous = quad_object.phys_rect_list.get(0).main_rect;//
			quad_object.phys_rect_list.add(new RectFExtended(previous.left + Functions.screenWidthToShaderWidth(45), //
					previous.top - Functions.screenHeightToShaderHeight(30),//
					previous.right - Functions.screenWidthToShaderWidth(45),//
					previous.bottom + Functions.screenHeightToShaderHeight(50)));//
			quad_object.phys_rect_list.remove(0);
		}
		
		/* everything else */
		else if (this_object == EnumLevelObject.transparent)
			quad_object = new QuadCompressed(R.raw.transparent, R.raw.transparent, (int) width, (int) height);
		else if (this_object == EnumLevelObject.test)
			quad_object = new QuadColorShape(0, 200, 200, 0, Color.WHITE, 0);
		else
			quad_object = new QuadColorShape(0, 200, 200, 0, Color.RED, 0);
		
		quad_object.setZPos(quad_object.z_pos - (z_plane * Constants.z_modifier));
		quad_object.setXYPos(Functions.screenXToShaderX(x_pos), Functions.screenYToShaderY(y_pos), draw_from);
		
		// note how these are set AFTER
		this.x_pos_shader = quad_object.x_pos;
		this.y_pos_shader = quad_object.y_pos;
		
		width = quad_object.width;
		height = quad_object.height;
		
		if (degree != 0)
			quad_object.setRotationZ(degree);
		if (scale != 1)
			quad_object.setScale(scale);
		
	}
	
	AverageMaker y_average = new AverageMaker(10);
	
	public void onUpdate(double delta)
	{
		if (this_object == EnumLevelObject.l2_floating_platform)
		{
			Constants.physics.addSpringY(Constants.floating_spring, Constants.floating_damper, 0, quad_object.y_pos - y_pos_shader, quad_object);
			Constants.physics.integratePhysics(delta, quad_object);
		}
	}
	
	public void onDrawObject()
	{
		if (active)
			quad_object.onDrawAmbient();
	}
}
