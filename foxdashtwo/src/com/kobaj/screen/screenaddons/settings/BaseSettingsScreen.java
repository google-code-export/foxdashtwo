package com.kobaj.screen.screenaddons.settings;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.Button;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.screen.screenaddons.BasePopup;

public class BaseSettingsScreen extends BasePopup
{
	private Button audio_button;
	private Button cancel_button;
	
	private EnumSettingsShowing current_settings = EnumSettingsShowing.none;
	
	private BaseAudioSettingsScreen base_audio = new BaseAudioSettingsScreen();
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		base_audio.onInitialize();
		
		cancel_button = new Button(R.string.cancel);
		audio_button = new Button(R.string.audio);
		
		Button[] buttons =  BasePopup.alignButtonsAlongXAxis(center_y - shift_y, cancel_button, audio_button);
		
		// this is ok because its an array
		for (Button button : buttons)
			button.onInitialize();
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
		else
		{
			if (audio_button.isReleased())
				current_settings = EnumSettingsShowing.audio;
			else if (cancel_button.isReleased())
				return false;
		}
		
		return true;
	}
	
	@Override
	public void onDraw()
	{
		if (current_settings == EnumSettingsShowing.audio)
		{
			base_audio.onDraw();
			return;
		}
		
		main_popup.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, main_color, true);
		Constants.text.drawText(R.string.settings, label_x, label_y, EnumDrawFrom.center);
		
		audio_button.onDrawConstant();
		cancel_button.onDrawConstant();
	}
}
