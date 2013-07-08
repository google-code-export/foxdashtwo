package com.kobaj.level.LevelEventTypes;

import java.util.ArrayList;

import android.graphics.Color;

import com.kobaj.foxdashtwo.R;
import com.kobaj.level.Level;
import com.kobaj.level.LevelTypeLight.LevelAmbientLight;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public class LevelEventThoughtBubble extends LevelEventBase
{
	private LevelAmbientLight bubble;
	private int text_id;
	private int padding = 20;
	
	private double pos_x = 0;
	private double pos_y_default = 50;
	private double pos_y;
	
	public LevelEventThoughtBubble(EnumLevelEvent type)
	{
		super(type);
		bubble = new LevelAmbientLight();
		bubble.active = false;
		bubble.secondary_color = Color.TRANSPARENT;
		bubble.color = Color.BLUE;
	}
	
	@Override
	public void onInitialize(final Level level, final ArrayList<String> affected_strings)
	{
		super.onInitialize(level, affected_strings);
		
		String value = Constants.empty;
		
		if(affected_strings.size() > 0)
		{
			value = affected_strings.get(0);
		}
		
		// and setup our string
		text_id = Constants.text.generateString(value);
		int width = Constants.text.measureTextWidth(text_id);
		int height = Constants.text.measureTextHeight(text_id);
		
		bubble.onInitialize();
		bubble.quad_light = new QuadCompressed(R.raw.white, R.raw.white, width + padding, height + padding);
		bubble.quad_light.color = Color.TRANSPARENT;
		bubble.quad_light.setXYPos(pos_x, Functions.screenYToShaderY(Functions.fix_y(pos_y_default)), EnumDrawFrom.center_top);
		
		pos_y = Functions.screenYToShaderY(Functions.fix_y(pos_y_default + padding / 2.0));
	}
	
	@Override
	public void onUpdate(double delta, boolean active)
	{
		bubble.onUpdate(delta);
		bubble.active = active;
	}
	
	@Override
	public void onDraw()
	{
		bubble.onDrawLight();
		Constants.text.drawText(text_id, pos_x, pos_y, EnumDrawFrom.center_top, bubble.quad_light.color);
	}
}
