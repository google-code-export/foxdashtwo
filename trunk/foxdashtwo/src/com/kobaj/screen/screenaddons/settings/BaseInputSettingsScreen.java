package com.kobaj.screen.screenaddons.settings;

import com.kobaj.foxdashtwo.R;
import com.kobaj.foxdashtwo.UserSettings;
import com.kobaj.input.InputType.EnumInputType;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.Button;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.screen.screenaddons.floatingframe.BasePopup;

public class BaseInputSettingsScreen extends BasePopup
{
	private Button cancel_button;
	private Button input_half_half_button;
	private Button input_nintendo_button; // 'controller'
	
	private double input_label_x;
	private double input_label_y;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		cancel_button = new Button(R.string.cancel);
		input_half_half_button = new Button(R.string.halfhalf);
		input_nintendo_button = new Button(R.string.controller);
		
		cancel_button.onInitialize();
		input_half_half_button.onInitialize();
		input_nintendo_button.onInitialize();
		
		input_label_x = -Functions.screenWidthToShaderWidth(default_label_left);
		input_label_y = shift_y;
		
		BasePopup.alignButtonsAlongXAxis(center_y, input_nintendo_button, input_half_half_button);
		BasePopup.alignButtonsAlongXAxis(cancel_shift_y, cancel_button);
	}
	
	@Override
	public boolean onUpdate(double delta)
	{
		if (input_half_half_button.isReleased())
			UserSettings.active_input_type = EnumInputType.halfhalf;
		else if (input_nintendo_button.isReleased())
			UserSettings.active_input_type = EnumInputType.nintendo;
		else if (cancel_button.isReleased())
			return false;
		
		return true;
	}
	
	@Override
	public void onDraw()
	{
		main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
		Constants.text.drawText(R.string.audio, label_x, label_y, EnumDrawFrom.center);
		
		Constants.text.drawText(R.string.current, input_label_x, input_label_y, EnumDrawFrom.bottom_right);
		if (UserSettings.active_input_type == EnumInputType.halfhalf)
			Constants.text.drawText(R.string.halfhalf, input_label_x, input_label_y, EnumDrawFrom.bottom_left);
		else if (UserSettings.active_input_type == EnumInputType.nintendo)
			Constants.text.drawText(R.string.controller, input_label_x, input_label_y, EnumDrawFrom.bottom_left);
		
		input_half_half_button.onDrawConstant();
		input_nintendo_button.onDrawConstant();
		cancel_button.onDrawConstant();
	}
	
}
