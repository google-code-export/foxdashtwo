package com.kobaj.screen.screenaddons.settings;

import android.annotation.SuppressLint;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.TextButton;
import com.kobaj.screen.screenaddons.floatingframe.BaseFloatingFrame;

public class BaseSettingsScreen extends BaseFloatingFrame
{
	private TextButton account_button;
	private TextButton audio_button;
	private TextButton cancel_button;
	private TextButton input_button;
	private TextButton video_button;
	
	private EnumSettingsShowing current_settings = EnumSettingsShowing.none;
	
	private BaseVideoSettingsScreen base_video = new BaseVideoSettingsScreen();
	private BaseAudioSettingsScreen base_audio = new BaseAudioSettingsScreen();
	private BaseInputSettingsScreen base_input = new BaseInputSettingsScreen();
	private BaseAccountSettingsScreen base_account = new BaseAccountSettingsScreen();
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		// initialize screens
		base_video.onInitialize();
		base_audio.onInitialize();
		base_input.onInitialize();
		base_account.onInitialize();
		
		// initialize everything else
		account_button = new TextButton(R.string.account_button);
		input_button = new TextButton(R.string.input_button);
		cancel_button = new TextButton(R.string.back);
		audio_button = new TextButton(R.string.audio_button);
		video_button = new TextButton(R.string.video_button);
		
		account_button.onInitialize();
		input_button.onInitialize();
		cancel_button.onInitialize();
		audio_button.onInitialize();
		video_button.onInitialize();
		
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y, account_button, input_button, audio_button, video_button);
		BaseFloatingFrame.alignButtonsAlongXAxis(cancel_shift_y, cancel_button);
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		account_button.onUnInitialize();
		audio_button.onUnInitialize();
		cancel_button.onUnInitialize();
		input_button.onUnInitialize();
		video_button.onUnInitialize();
		
		base_account.onUnInitialize();
		base_audio.onUnInitialize();
		base_input.onUnInitialize();
		base_video.onUnInitialize();
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
		else if (current_settings == EnumSettingsShowing.account)
		{
			if (!base_account.onUpdate(delta))
				current_settings = EnumSettingsShowing.none;
		}
		else if (current_settings == EnumSettingsShowing.video)
		{
			if(!base_video.onUpdate(delta))
				current_settings = EnumSettingsShowing.none;
		}
		else
		{
			if (audio_button.isReleased())
				current_settings = EnumSettingsShowing.audio;
			else if (input_button.isReleased())
				current_settings = EnumSettingsShowing.input;
			else if (account_button.isReleased())
				current_settings = EnumSettingsShowing.account;
			else if(video_button.isReleased())
				current_settings = EnumSettingsShowing.video;
			else if (cancel_button.isReleased())
				return false;
		}
		
		return super.onUpdate(delta);
	}
	
	@SuppressLint("WrongCall")
	@Override
	public void onDraw()
	{
		if (current_settings == EnumSettingsShowing.audio)
			base_audio.onDraw();
		else if (current_settings == EnumSettingsShowing.input)
			base_input.onDraw();
		else if (current_settings == EnumSettingsShowing.account)
			base_account.onDraw();
		else if (current_settings == EnumSettingsShowing.video)
			base_video.onDraw();
		else
		{
			main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
			Constants.text.drawText(R.string.settings, label_x, label_y, EnumDrawFrom.center);
			
			input_button.onDrawConstant();
			audio_button.onDrawConstant();
			cancel_button.onDrawConstant();
			account_button.onDrawConstant();
			video_button.onDrawConstant();
		}
	}
}
