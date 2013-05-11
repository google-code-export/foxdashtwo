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
	
	private TextButton in_button;
	private TextButton out_button;
	
	private double input_label_x;
	private double input_label_y;
	private double zoom_label_x;
	private double zoom_label_y;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		cancel_button = new TextButton(R.string.back, true);
		switch_button = new ToggleTextButton(R.string.halfhalf, R.string.controller);
		in_button = new TextButton(R.string.zoom_in, true);
		out_button = new TextButton(R.string.zoom_out, true);
		
		cancel_button.onInitialize();
		switch_button.onInitialize();
		in_button.onInitialize();
		out_button.onInitialize();
		
		double shift_y = Functions.screenHeightToShaderHeight(32);
		double move_y = Functions.screenHeightToShaderHeight(5); // same value is in baseplaytype
		
		zoom_label_y = 2 * shift_y + move_y;
		input_label_y = -shift_y + move_y;
		
		input_label_x = -Functions.screenWidthToShaderWidth(default_label_left);
		zoom_label_x = input_label_x;
		
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y + shift_y + move_y, in_button, out_button);
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y - 2.0 * shift_y + move_y, switch_button);
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
		else if(in_button.isReleased())
		{
			UserSettings.zoom(UserSettings.zoom_value - .1);
			Functions.setCamera(Constants.x_shader_translation, Constants.y_shader_translation, Constants.z_shader_translation);
		}
		else if(out_button.isReleased())
		{
			UserSettings.zoom(UserSettings.zoom_value += .1);
			Functions.setCamera(Constants.x_shader_translation, Constants.y_shader_translation, Constants.z_shader_translation);
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
		
		Constants.text.drawText(R.string.zoom, zoom_label_x, zoom_label_y, EnumDrawFrom.bottom_right);
		Constants.text.drawNumber((int) (UserSettings.zoom_value * 10.0), zoom_label_x, zoom_label_y, EnumDrawFrom.bottom_left);
		Constants.text.drawText(R.string.current, input_label_x, input_label_y, EnumDrawFrom.bottom_right);
		
		in_button.onDrawConstant();
		out_button.onDrawConstant();
		switch_button.onDrawConstant();
		cancel_button.onDrawConstant();
	}
	
}
