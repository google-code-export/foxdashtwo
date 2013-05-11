package com.kobaj.screen.screenaddons.settings;

import com.kobaj.account_settings.UserSettings;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.TextButton;
import com.kobaj.opengldrawable.Button.ToggleTextButton;
import com.kobaj.screen.screenaddons.floatingframe.BaseFloatingFrame;

public class BaseAccountSettingsScreen extends BaseFloatingFrame
{
	private TextButton cancel_button;
	private ToggleTextButton switch_button;
	
	private double input_label_x;
	private double input_label_y;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		cancel_button = new TextButton(R.string.back, true);
		switch_button = new ToggleTextButton(R.string.yes_button, R.string.no_button);
		
		cancel_button.onInitialize();
		switch_button.onInitialize();
		
		input_label_x = -Functions.screenWidthToShaderWidth(default_label_left_shift);
		input_label_y = shift_y;
		
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y, switch_button);
		BaseFloatingFrame.alignButtonsAlongXAxis(cancel_shift_y, cancel_button);
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		cancel_button.onUnInitialize();
		switch_button.onUnInitialize();
	}
	
	@Override
	public boolean onUpdate(double delta)
	{
		if (UserSettings.auto_login)
			switch_button.label_pointer = 0;
		else
			switch_button.label_pointer = 1;
		
		if (switch_button.isReleased())
		{
			if (switch_button.label_pointer == 0)
				UserSettings.auto_login = true;
			else
				UserSettings.auto_login = false;
		}
		else if (cancel_button.isReleased())
			return false;
		
		return super.onUpdate(delta);
	}
	
	@Override
	public void onDraw()
	{
		main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
		Constants.text.drawText(R.string.account, label_x, label_y, EnumDrawFrom.center);
		
		Constants.text.drawText(R.string.account_auto_login, input_label_x, input_label_y, EnumDrawFrom.bottom_left);
		
		switch_button.onDrawConstant();
		cancel_button.onDrawConstant();
	}
}
