package com.kobaj.level;

import org.simpleframework.xml.Element;

import com.kobaj.opengldrawable.QuadColorShape;

import android.graphics.Color;

public class LevelObject
{
	@Element
	public EnumLevelObject this_object;
	@Element
	public com.kobaj.opengldrawable.EnumDrawFrom draw_from;
	@Element
	public double x_pos; //screen coordinates
	@Element
	public double y_pos; //screen coordinates
	
	public QuadColorShape quad_object;
	
	public void onInitialize()
	{
		quad_object = new QuadColorShape(0, 200, 200, 0, Color.RED);
		quad_object.setPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), draw_from);
	}
}
