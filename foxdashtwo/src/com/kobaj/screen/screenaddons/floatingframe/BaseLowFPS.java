package com.kobaj.screen.screenaddons.floatingframe;

import com.kobaj.account_settings.UserSettings;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.TextButton;

public class BaseLowFPS extends BaseFloatingFrame
{
	TextButton ok_button;
	TextButton cancel_button;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		ok_button = new TextButton(R.string.ok, true);
		ok_button.onInitialize();
		
		cancel_button = new TextButton(R.string.cancel, true);
		cancel_button.onInitialize();
		
		BaseFloatingFrame.alignButtonsAlongXAxis(cancel_shift_y, cancel_button, ok_button);
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		ok_button.onUnInitialize();
		cancel_button.onUnInitialize();
	}
	
	@Override
	public boolean onUpdate(double delta)
	{	
		if(ok_button.isReleased())
		{
			UserSettings.fbo(1);
			return false;
		}
		else if(cancel_button.isReleased())
		{
			return false;
		}
		
		return super.onUpdate(delta);
	}
	
	@Override
	public void onDraw()
	{
		main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
		Constants.text.drawText(R.string.warning, label_x, label_y, EnumDrawFrom.center);
		
		Constants.text.drawText(R.string.low_fps_blurb, 0, 0, EnumDrawFrom.center);
		
		ok_button.onDrawConstant();
		cancel_button.onDrawConstant();
	}
}
