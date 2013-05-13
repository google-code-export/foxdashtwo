package com.kobaj.screen.screenaddons.floatingframe;

import com.kobaj.foxdashtwo.R;
import com.kobaj.input.EnumKeyCodes;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.Button;
import com.kobaj.opengldrawable.Quad.QuadCompressed;
import com.kobaj.screen.screenaddons.SimpleColorLoop;

public abstract class BaseFloatingFrame
{
	protected QuadCompressed main_popup;
	
	// these need to be refactored to x_label, etc.
	protected double label_x;
	protected double label_y;
	protected double shift_y;
	protected double cancel_shift_y;
	protected int default_label_left = 175;
	protected int default_label_left_shift = 275;
	protected final double center_x = 0; // heh
	protected final double center_y = 0;
	
	protected final double color_time = 5000;
	protected SimpleColorLoop my_color_set;
	
	public void onInitialize()
	{
		main_popup = new QuadCompressed(R.raw.ui_big_popup, R.raw.ui_big_popup_alpha, 626, 386);
		
		shift_y = Functions.screenHeightToShaderHeight(45);
		
		cancel_shift_y = center_y - 3.0 * shift_y;
		
		label_x = center_x;
		label_y = center_y + 3.0 * shift_y; // Functions.screenHeightToShaderHeight(95);
		
		// set colors
		my_color_set = new SimpleColorLoop(color_time, 0xDDBBBBBB, 0xDD95BF96, 0xDD95BFBF, 0xDD9597BF, 0xDDB895BF, 0xDDBEBF95);
		main_popup.color = my_color_set.pickRandomStart();
		
		// floating frame colors
		// public static final int frame_main_color = 0xDDBBBBBB;
		// public static final int frame_sec_color = 0xDDBF8888;
	}
	
	public void onUnInitialize()
	{
		main_popup.onUnInitialize();
	}
	
	public boolean onUpdate(double delta)
	{
		my_color_set.onUpdate(delta);
		main_popup.color = my_color_set.getCurrentColor();
		
		if (Constants.input_manager.getKeyPressed(EnumKeyCodes.back))
			return false;
		
		return true;
	}// true it shows, false it doesn't show.
	
	public abstract void onDraw();
	
	// y pos is in shader coords
	public static Button[] alignButtonsAlongXAxis(double y_pos, Button... buttons)
	{
		return alignButtonsAlongXAxis(y_pos, 0, buttons);
	}
	
	public static <T extends Button> Button[] alignButtonsAlongXAxis(double y_pos, int shift_x, T... buttons)
	{
		final int padding = 10;
		
		int total_width = 0;
		// get total width
		// this is ok because its an array;
		for (Button button : buttons)
		{
			total_width += button.width;
			total_width += padding;
		}
		total_width -= padding;
		
		// this becomes our starting point
		int half_width = (-total_width / 2) + shift_x;
		
		for (Button button : buttons)
		{
			// calculate current button position
			int half_button_width = (button.width / 2);
			int x_pos = half_width + half_button_width;
			double bx_pos = Functions.screenWidthToShaderWidth(x_pos);
			double by_pos = y_pos;
			button.setXYPos(bx_pos, by_pos, EnumDrawFrom.center);
			
			// calculate next buttons starting point
			half_width += button.width + padding;
		}
		
		// now they're all sorted, send it back
		return buttons;
	}
}
