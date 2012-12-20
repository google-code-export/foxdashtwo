package com.kobaj.screen.screenaddons.settings;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.Button;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.screen.screenaddons.floatingframe.BasePopup;

public class BaseSettingsScreen extends BasePopup
{
	private Button audio_button;
	private Button cancel_button;
	private Button input_button;
	
	private EnumSettingsShowing current_settings = EnumSettingsShowing.none;
	
	private BaseAudioSettingsScreen base_audio = new BaseAudioSettingsScreen();
	private BaseInputSettingsScreen base_input = new BaseInputSettingsScreen();
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		//initialize screens
		base_audio.onInitialize();
		base_input.onInitialize();
		
		//initialize everything else
		input_button = new Button(R.string.input_button);
		cancel_button = new Button(R.string.cancel);
		audio_button = new Button(R.string.audio_button);
		
		input_button.onInitialize();
		cancel_button.onInitialize();
		audio_button.onInitialize();
		
		BasePopup.alignButtonsAlongXAxis(center_y, input_button, audio_button);
		BasePopup.alignButtonsAlongXAxis(cancel_shift_y, cancel_button);
	}
	
	public void reset()
	{
		current_settings = EnumSettingsShowing.none;
	}
	
	@Override
	public boolean onUpdate(double delta)
	{
		if (current_settings == EnumSettingsShowing.audio)
		{
			if (!base_audio.onUpdate(delta))
				current_settings = EnumSettingsShowing.none;
		}
		else if (current_settings == EnumSettingsShowing.input)
		{
			if (!base_input.onUpdate(delta))
				current_settings = EnumSettingsShowing.none;
		}
		else
		{
			if (audio_button.isReleased())
				current_settings = EnumSettingsShowing.audio;
			else if (input_button.isReleased())
				current_settings = EnumSettingsShowing.input;
			else if (cancel_button.isReleased())
				return false;
		}
		
		return true;
	}
	
	@Override
	public void onDraw()
	{
		if (current_settings == EnumSettingsShowing.audio)
			base_audio.onDraw();
		else if (current_settings == EnumSettingsShowing.input)
			base_input.onDraw();
		else
		{
			main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
			Constants.text.drawText(R.string.settings, label_x, label_y, EnumDrawFrom.center);
			
			input_button.onDrawConstant();
			audio_button.onDrawConstant();
			cancel_button.onDrawConstant();
		}
	}
}
