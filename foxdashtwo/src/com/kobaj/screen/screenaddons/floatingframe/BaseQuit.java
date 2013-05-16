package com.kobaj.screen.screenaddons.floatingframe;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.TextButton;
import com.kobaj.screen.screenaddons.SimpleColorLoop;

public class BaseQuit extends BaseFloatingFrame
{
	private TextButton quit_button;
	private TextButton cancel_button;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		cancel_button = new TextButton(R.string.cancel, true);
		quit_button = new TextButton(R.string.quit, true);
		
		cancel_button.onInitialize();
		quit_button.onInitialize();
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y - shift_y, cancel_button, quit_button);
		
		main_popup.setScale(.5);
		my_color_set = new SimpleColorLoop(color_time, 0xCCBF8888, 0xCCDB6363);
		main_popup.color = 0xDDBF8888;
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		quit_button.onUnInitialize();
		cancel_button.onUnInitialize();
	}
	
	@Override
	public boolean onUpdate(double delta)
	{
		if (quit_button.isReleased())
		{
			// terrible
			GameActivity.activity.finish();
		}
		else if (cancel_button.isReleased())
			return false;
		
		return super.onUpdate(delta);
	}
	
	@Override
	public void onDraw()
	{
		main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
		Constants.text.drawText(R.string.are_you_sure, label_x, shift_y, EnumDrawFrom.center);
		
		cancel_button.onDrawConstant();
		quit_button.onDrawConstant();
	}
	
}
