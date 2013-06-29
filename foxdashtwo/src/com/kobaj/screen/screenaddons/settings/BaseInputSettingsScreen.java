package com.kobaj.screen.screenaddons.settings;

import com.kobaj.account_settings.UserSettings;
import com.kobaj.foxdashtwo.R;
import com.kobaj.input.InputType.EnumInputType;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.TextButton;
import com.kobaj.opengldrawable.Button.ToggleTextButton;
import com.kobaj.screen.screenaddons.floatingframe.BaseFloatingFrame;

public class BaseInputSettingsScreen extends BaseFloatingFrame
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
		switch_button = new ToggleTextButton(R.string.halfhalf, R.string.controller);
		
		cancel_button.onInitialize();
		switch_button.onInitialize();
		
		if(UserSettings.active_input_type == EnumInputType.halfhalf)
			switch_button.setLabelPointer(0);
		else
			switch_button.setLabelPointer(1);
		
		double shift_y = Functions.screenHeightToShaderHeight(32);
		double move_y = Functions.screenHeightToShaderHeight(5); // same value is in baseplaytype
		
		input_label_y = 2 * shift_y + move_y;
       
        input_label_x = -Functions.screenWidthToShaderWidth(default_label_left);
		
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y + shift_y + move_y, switch_button);
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
		if(switch_button.isReleased())
		{
			if(switch_button.label_pointer == 0)
				UserSettings.active_input_type = EnumInputType.halfhalf;
			else
				UserSettings.active_input_type = EnumInputType.nintendo;
		}
		else if (cancel_button.isReleased())
			return false;
		
		return super.onUpdate(delta);
	}
	
	@Override
	public void onDraw()
	{
		main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
		Constants.text.drawText(R.string.input, label_x, label_y, EnumDrawFrom.center);
		
		Constants.text.drawText(R.string.current, input_label_x, input_label_y, EnumDrawFrom.bottom_right);

		switch_button.onDrawConstant();
		cancel_button.onDrawConstant();
	}
	
}
