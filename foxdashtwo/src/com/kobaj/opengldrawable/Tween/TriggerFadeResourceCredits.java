package com.kobaj.opengldrawable.Tween;

import android.graphics.Color;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;

public class TriggerFadeResourceCredits extends TriggerFadeResource
{
	public TriggerFadeResourceCredits(int width, int height, int resource_id, int index)
	{
		super(width, height, resource_id, index);
		this.max_fade = 10000;
	}
	
	@Override
	public void onDraw()
	{
		if(color == Color.TRANSPARENT)
			return;
		
		resources.color = color;
		resources.onDrawAmbient(Constants.my_ip_matrix, true);
		
		// text
		if(index == 0)
		{
			Constants.text.drawText(R.string.credits_1, 0, Functions.screenYToShaderY(Functions.fix_y(100)), EnumDrawFrom.center_top, color);
			Constants.text.drawText(R.string.credits_1_1, 0, Functions.screenYToShaderY(Functions.fix_y(150)), EnumDrawFrom.center_top, color);	
		}
		else if (index == 1)
		{
			Constants.text.drawText(R.string.credits_2, -Constants.two_third_width_pos, Functions.screenYToShaderY(Functions.fix_y(100)), EnumDrawFrom.center_top, color);
			Constants.text.drawText(R.string.credits_2_1, Constants.two_third_width_pos, Functions.screenYToShaderY(Functions.fix_y(100)), EnumDrawFrom.center_top, color);	
		}
		else if (index == 2)
		{
			Constants.text.drawText(R.string.credits_3, -Constants.two_third_width_pos, Functions.screenYToShaderY(Functions.fix_y(25)), EnumDrawFrom.center_top, color);
			Constants.text.drawText(R.string.credits_3_1, Constants.two_third_width_pos, Functions.screenYToShaderY(Functions.fix_y(25)), EnumDrawFrom.center_top, color);	
			
			Constants.text.drawText(R.string.credits_3_2, -Constants.two_third_width_pos, Functions.screenYToShaderY(Functions.fix_y(100)), EnumDrawFrom.center_top, color);
			Constants.text.drawText(R.string.credits_3_3, Constants.two_third_width_pos, Functions.screenYToShaderY(Functions.fix_y(100)), EnumDrawFrom.center_top, color);	
		}
	}
}
