package com.kobaj.level.LevelEventTypes;

import java.util.ArrayList;

import com.kobaj.foxdashtwo.R;
import com.kobaj.foxdashtwo.UserSettings;
import com.kobaj.input.InputType.EnumInputType;
import com.kobaj.level.LevelObject;
import com.kobaj.level.LevelTypeLight.LevelAmbientLight;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public class LevelEventArrows extends LevelEventBase
{
	// my state
	private Quad my_draw;
	private Quad secondary_draw;
	private double brightness;
	private double keep;
	
	private ArrayList<Double> x_poss = new ArrayList<Double>();
	private ArrayList<Double> y_poss = new ArrayList<Double>();
	
	private final double speed_divisor = 7.0;
	
	public LevelEventArrows(EnumLevelEvent type)
	{
		super(type);
	}
	
	@Override
	public void onInitialize(LevelObject player, ArrayList<LevelObject> objects, ArrayList<LevelAmbientLight> lights, ArrayList<String> id_strings)
	{
		super.onInitialize(player, objects, lights, id_strings);
		
		// drawable
		// all the different types of level events
		if (this_event == EnumLevelEvent.left_arrow || this_event == EnumLevelEvent.right_arrow)
		{
			double x_pos = 0;
			double y_pos = 0;
			
			if (this_event == EnumLevelEvent.left_arrow)
			{
				x_pos = Functions.screenXToShaderX(100);
				my_draw = new Quad(R.drawable.left_arrow, 112, 200);
				
			}
			else if (this_event == EnumLevelEvent.right_arrow)
			{
				x_pos = Functions.screenXToShaderX(Constants.width - 100);
				my_draw = new QuadCompressed(R.raw.right_arrow, R.raw.right_arrow_alpha, 112, 200);
			}
			
			my_draw.setXYPos(x_pos, y_pos, EnumDrawFrom.center);
			
			x_poss.add(x_pos);
			y_poss.add(y_pos);
		}
		else if (this_event == EnumLevelEvent.up_arrow)
		{
			double y_pos = 0;
			
			double x_pos_1 = Functions.screenXToShaderX(100);
			my_draw = new Quad(R.drawable.up_arrow, 200, 112);
			my_draw.setXYPos(x_pos_1, y_pos, EnumDrawFrom.center);
			
			double x_pos_2 = Functions.screenXToShaderX(Constants.width - 100);
			secondary_draw = new Quad(R.drawable.up_arrow, 200, 112);
			secondary_draw.setXYPos(x_pos_2, y_pos, EnumDrawFrom.center);
			
			x_poss.add(x_pos_1);
			x_poss.add(x_pos_2);
			
			y_poss.add(y_pos);
			y_poss.add(y_pos);
		}
	}
	
	@Override
	public void onUpdate(double delta, boolean active)
	{
		if (active || brightness != 0)
		{
			keep += Math.toRadians(delta) / speed_divisor;
			if (keep > Math.PI * 2)
				keep = 0;
			brightness = Math.sin(keep);
		}
		else
		{
			brightness = 0;
			keep = 0;
		}
	}
	
	@Override
	public void onDraw()
	{
		if (brightness == 0)
			return;
		
		int first_color = Functions.makeColor(255, 255, 255, (int) (255 * brightness));
		int secon_color = Functions.makeColor(255, 255, 255, (int) (255 * -brightness));
		
		if (brightness > 0)
		{
			if (my_draw != null)
			{
				my_draw.color = first_color;
				my_draw.onDrawAmbient(Constants.my_ip_matrix, true);
			}
			
			if (secondary_draw != null)
			{
				secondary_draw.color =  first_color;
				secondary_draw.onDrawAmbient(Constants.my_ip_matrix, true);
			}
		}
		else if (brightness < 0 && UserSettings.active_input_type == EnumInputType.halfhalf)
			for (int i = x_poss.size() - 1; i >= 0; i--)
			{
				double x_pos = x_poss.get(i);
				double y_pos = y_poss.get(i);
				
				Constants.text.drawText(R.string.touch_here, x_pos, y_pos, EnumDrawFrom.center, secon_color);
			}
	}
}
