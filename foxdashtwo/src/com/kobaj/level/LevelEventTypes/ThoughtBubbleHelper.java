package com.kobaj.level.LevelEventTypes;

import android.graphics.Color;

import com.kobaj.foxdashtwo.R;
import com.kobaj.level.LevelTypeLight.LevelAmbientLight;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadAnimated;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public class ThoughtBubbleHelper
{
	private LevelAmbientLight bubble;
	
	private int number_of_mini_bubbles = 3;
	private QuadCompressed[] mini_bubbles = new QuadCompressed[number_of_mini_bubbles];
	
	private int text_id;
	private String message;
	
	private double x_pos; // default shader coords
	private double y_pos; // default shader coords
	
	public static final int padding = 20;
	
	private boolean fully_lit = false;
	
	// position in screen coordinates
	public ThoughtBubbleHelper(int x, int y, String message)
	{
		bubble = new LevelAmbientLight();
		bubble.active = false;
		bubble.secondary_color = Color.TRANSPARENT;
		bubble.color = Color.WHITE;
		
		this.message = message;
		
		x_pos = Functions.screenXToShaderX(x);
		y_pos = Functions.screenYToShaderY(Functions.fix_y(y));
	}
	
	public void onInitialize()
	{
		// and setup our string
		text_id = Constants.text.generateString(message);
		int width = Constants.text.measureTextWidth(text_id);
		int height = Constants.text.measureTextHeight(text_id);
		
		bubble.onInitialize();
		
		// find out the best size for our situation
		int raw_bubble = 0;
		int raw_bubble_alpha = 0;
		int bubble_width = 0;
		int bubble_height = 0;
		
		// yeah best way of doing this!
		if (width < 375)
		{
			bubble_width = 250;
			if (height < 200)
			{
				raw_bubble = R.raw.bubble_250_100;
				raw_bubble_alpha = R.raw.bubble_250_100_alpha;
				bubble_height = 100;
			}
			else
			{
				raw_bubble = R.raw.bubble_250_300;
				raw_bubble_alpha = R.raw.bubble_250_300_alpha;
				bubble_height = 300;
			}
		}
		else if (width < 625)
		{
			bubble_width = 500;
			if (height < 200)
			{
				raw_bubble = R.raw.bubble_500_100;
				raw_bubble_alpha = R.raw.bubble_500_100_alpha;
				bubble_height = 100;
			}
			else
			{
				raw_bubble = R.raw.bubble_500_300;
				raw_bubble_alpha = R.raw.bubble_500_300_alpha;
				bubble_height = 300;
			}
		}
		else
		{
			bubble_width = 750;
			if (height < 200)
			{
				raw_bubble = R.raw.bubble_750_100;
				raw_bubble_alpha = R.raw.bubble_750_100_alpha;
				bubble_height = 100;
			}
			else
			{
				raw_bubble = R.raw.bubble_750_300;
				raw_bubble_alpha = R.raw.bubble_750_300_alpha;
				bubble_height = 300;
			}
		}
		
		QuadCompressed full_cloud = new QuadCompressed(raw_bubble, raw_bubble_alpha, bubble_width, bubble_height);
		full_cloud.setWidthHeight(width + padding, height + padding);
		full_cloud.color = Color.TRANSPARENT;
		full_cloud.setXYPos(x_pos, y_pos, EnumDrawFrom.center);
		bubble.quad_light = full_cloud;
		
		for (int i = 0; i < number_of_mini_bubbles; i++)
		{
			QuadCompressed reference = new QuadCompressed(R.raw.mini_bubble, R.raw.mini_bubble_alpha, 32, 32);
			mini_bubbles[i] = reference;
			reference.setScale((double) (i + 1) / (double) number_of_mini_bubbles);
		}
	}
	
	public void onUnInitalize()
	{
		bubble.onUnInitialize();
	}
	
	public void onUpdate(double delta, boolean active, double player_x, double player_y)
	{
		bubble.onUpdate(delta);
		bubble.active = active;
		
		if (bubble.quad_light.color == bubble.color)
			fully_lit = true;
		
		if (bubble.quad_light.color != bubble.secondary_color)
		{
			if (bubble.quad_light instanceof QuadAnimated)
			{
				QuadAnimated cloud = (QuadAnimated) bubble.quad_light;
				cloud.onUpdate(delta);
			}
			
			for (int i = 0; i < number_of_mini_bubbles; i++)
			{
				Quad mini_bubble = mini_bubbles[i];
				mini_bubble.color = bubble.quad_light.color;
				mini_bubble.setXYPos(Functions.linearInterpolate(0, number_of_mini_bubbles + 1, i + 1, player_x, x_pos),
						Functions.linearInterpolate(0, number_of_mini_bubbles + 1, i + 1, player_y, y_pos), EnumDrawFrom.center);
			}
		}
	}
	
	public boolean finished()
	{
		if (fully_lit && bubble.quad_light.color == bubble.secondary_color)
			return true;
		
		return false;
	}
	
	public void onDraw()
	{
		bubble.onDrawLight();
		Constants.text.drawText(text_id, x_pos, y_pos, EnumDrawFrom.center, bubble.quad_light.color);
		
		if (bubble.quad_light.color != bubble.secondary_color)
		{
			for (int i = 0; i < number_of_mini_bubbles; i++)
				mini_bubbles[i].onDrawAmbient(Constants.my_ip_matrix, true);
		}
	}
}
