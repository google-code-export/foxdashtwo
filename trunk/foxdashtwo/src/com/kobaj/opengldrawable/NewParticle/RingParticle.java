package com.kobaj.opengldrawable.NewParticle;

import android.graphics.Color;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public class RingParticle
{
	private QuadCompressed[] rings;
	
	private int rings_per_press = 3;
	private int current_ring_index = 0;
	private int press_count = 6;
	
	private int total_rings = 0;
	
	private double time_multiplier = .001;
	
	public void onInitialize()
	{
		total_rings = press_count * rings_per_press;
		
		rings = new QuadCompressed[total_rings];
		
		for (int i = 0; i < total_rings; i++)
		{
			// this doesnt make duplicates in video memory, dont worry :)
			rings[i] = new QuadCompressed(R.raw.ring, R.raw.ring_alpha, 512, 512);
			rings[i].setScale(Double.MAX_VALUE);
		}
	}
	
	public void onUpdate(double delta)
	{
		for (int fing = 0; fing < Constants.input_manager.finger_count; fing++)
		{
			if (Constants.input_manager.getPressed(fing))
			{
				
				for (int i = current_ring_index; i < current_ring_index + rings_per_press; i++)
				{
					double finger_x = Constants.input_manager.getX(fing);
					double finger_y = Constants.input_manager.getY(fing);
					double finger_y_fixed = Functions.fix_y(finger_y);
					
					rings[i].setXYPos(Functions.screenXToShaderX(finger_x), Functions.screenYToShaderY(finger_y_fixed), EnumDrawFrom.center);
					rings[i].setScale(.02 * (double) i);
				}
				
				current_ring_index = (current_ring_index + rings_per_press) % total_rings;
			}
		}
		
		for(int i = 0; i < total_rings; i++)
		{
			if(rings[i].scale_value < .75)
			{
				rings[i].setScale(rings[i].scale_value + delta * time_multiplier);
				rings[i].color = Color.WHITE;
			}
			else if(rings[i].scale_value < 1.0)
			{
				rings[i].setScale(rings[i].scale_value + delta * time_multiplier);
				rings[i].color = Functions.linearInterpolateColor(.75, 1.0, rings[i].scale_value, Color.WHITE, Color.TRANSPARENT);
			}
			else
			{
				rings[i].color = Color.TRANSPARENT;
			}
			

		}
	}
	
	public void onDrawConstant()
	{
		for(int i = 0; i < total_rings; i++)
			rings[i].onDrawAmbient(Constants.my_ip_matrix, true);
	}
}
