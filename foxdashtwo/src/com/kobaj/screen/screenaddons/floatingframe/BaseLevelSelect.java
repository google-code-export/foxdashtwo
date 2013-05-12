package com.kobaj.screen.screenaddons.floatingframe;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.ImageButton;
import com.kobaj.opengldrawable.Button.TextButton;
import com.kobaj.screen.TitleScreen;

public class BaseLevelSelect extends BaseFloatingFrame
{
	
	private TextButton back_button;
	
	private ImageButton[] my_levels = new ImageButton[7];
	private boolean[] accessable_levels = new boolean[7]; 
	
	private final String level_prefix = "level_";
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		// see which levels can be clicked
		accessable_levels[0] = true; // level 1
		accessable_levels[6] = true; // credits
		
		for(int i = 0; i < 5; i++)
			if(SinglePlayerSave.getPrevBest(level_prefix + String.valueOf(i)) != Double.MAX_VALUE)
				accessable_levels[i + 1] = true;
			else
				accessable_levels[i + 1] = false;
		
		// load the button images
		back_button = new TextButton(R.string.back, true);
		
		my_levels[0] = new ImageButton(R.raw.level_button_1, R.raw.level_button_1_alpha, 64, 64);
		
		// sorry :(
		if(accessable_levels[1])
			my_levels[1] = new ImageButton(R.raw.level_button_2, R.raw.level_button_2_alpha, 64, 64);
		else
			my_levels[1] = new ImageButton(R.raw.level_button_2_bw, R.raw.level_button_2_bw_alpha, 64,64);
		
		if(accessable_levels[2])
			my_levels[2] = new ImageButton(R.raw.level_button_3, R.raw.level_button_3_alpha, 64, 64);
		else
			my_levels[2] = new ImageButton(R.raw.level_button_3_bw, R.raw.level_button_3_bw_alpha, 64,64);
		
		if(accessable_levels[3])
			my_levels[3] = new ImageButton(R.raw.level_button_4, R.raw.level_button_4_alpha, 64, 64);
		else
			my_levels[3] = new ImageButton(R.raw.level_button_4_bw, R.raw.level_button_4_bw_alpha, 64,64);
		
		if(accessable_levels[4])
			my_levels[4] = new ImageButton(R.raw.level_button_5, R.raw.level_button_5_alpha, 64, 64);
		else
			my_levels[4] = new ImageButton(R.raw.level_button_5_bw, R.raw.level_button_5_bw_alpha, 64,64);
		
		if(accessable_levels[5])
			my_levels[5] = new ImageButton(R.raw.level_button_6, R.raw.level_button_6_alpha, 64, 64);
		else
			my_levels[5] = new ImageButton(R.raw.level_button_6_bw, R.raw.level_button_6_bw_alpha, 64,64);
		
		my_levels[6] = new ImageButton(R.raw.level_button_c, R.raw.level_button_c_alpha, 64, 64);
		
		// initialize everything
		back_button.onInitialize();
		
		for(ImageButton b: my_levels)
			b.onInitialize();
		
		// set positions of buttons
		BaseFloatingFrame.alignButtonsAlongXAxis(cancel_shift_y, back_button);
	
		double shift_y = Functions.screenHeightToShaderHeight(32);
		double move_y = Functions.screenHeightToShaderHeight(5); // same value is in base audio settings
		
		//my_levels[0].setXYPos(0, 0, EnumDrawFrom.center);
		
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y + shift_y + move_y, my_levels[0], my_levels[2], my_levels[4], my_levels[6]);
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y - 1.9 * shift_y + move_y, my_levels[1], my_levels[3], my_levels[5]);
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		for(ImageButton b: my_levels)
			b.onUnInitialize();
		
		back_button.onUnInitialize();
	}
	
	@Override
	public boolean onUpdate(double delta)
	{
		if (back_button.isReleased())
			return false;
		
		for(int i = 0; i < 7; i++)
		{
			if(accessable_levels[i])
			{
				if(my_levels[i].isReleased())
				{
					SinglePlayerSave.last_level = level_prefix + String.valueOf(i);
					TitleScreen.fade_play = true;
				}
			}
		}
		
		return super.onUpdate(delta);
	}
	
	@Override
	public void onDraw()
	{
		main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
		Constants.text.drawText(R.string.level_select, label_x, label_y, EnumDrawFrom.center);
		
		for(ImageButton b: my_levels)
			b.onDrawConstant();
		
		back_button.onDrawConstant();
	}
	
}
