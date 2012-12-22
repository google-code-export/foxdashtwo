package com.kobaj.screen.screenaddons.settings;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.Button;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.screen.screenaddons.floatingframe.BaseFloatingFrame;

public class BaseAudioSettingsScreen extends BaseFloatingFrame
{
	private Button cancel_button;
	private Button volume_up_button;
	private Button volume_down_button;
	
	private Button sound_volume_up_button;
	private Button sound_volume_down_button;
	
	private double sound_label_x;
	private double sound_label_y;
	private double music_label_x;
	private double music_label_y;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		cancel_button = new Button(R.string.cancel);
		volume_up_button = new Button(R.string.volume_up);
		volume_down_button = new Button(R.string.volume_down);
		sound_volume_up_button = new Button(R.string.volume_up);
		sound_volume_down_button = new Button(R.string.volume_down);
		
		cancel_button.onInitialize();
		volume_up_button.onInitialize();
		volume_down_button.onInitialize();
		sound_volume_up_button.onInitialize();
		sound_volume_down_button.onInitialize();
		
		double shift_y = Functions.screenHeightToShaderHeight(32);
		double move_y = Functions.screenHeightToShaderHeight(5);

		music_label_y = 2 * shift_y + move_y;
		sound_label_y = -shift_y + move_y;
		
		sound_label_x = -Functions.screenWidthToShaderWidth(default_label_left);
		music_label_x = sound_label_x;
		
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y + shift_y + move_y, volume_down_button, volume_up_button);
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y - 2.0 * shift_y + move_y, sound_volume_down_button, sound_volume_up_button);
		BaseFloatingFrame.alignButtonsAlongXAxis(cancel_shift_y, cancel_button);
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		cancel_button.onUnInitialize();
		volume_up_button.onUnInitialize();
		volume_down_button.onUnInitialize();
		sound_volume_up_button.onUnInitialize();
		sound_volume_down_button.onUnInitialize();
	}
	
	@Override
	public boolean onUpdate(double delta)
	{
		if (volume_up_button.isReleased())
			Constants.music_player.setDesiredVolume(Constants.music_player.getDesiredVolume() + .1);
		else if (volume_down_button.isReleased())
			Constants.music_player.setDesiredVolume(Constants.music_player.getDesiredVolume() - .1);
		else if (cancel_button.isReleased())
			return false;
		
		return true;
	}
	
	@Override
	public void onDraw()
	{
		main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
		Constants.text.drawText(R.string.audio, label_x, label_y, EnumDrawFrom.center);
		
		Constants.text.drawText(R.string.music, music_label_x, music_label_y, EnumDrawFrom.bottom_right);
		Constants.text.drawNumber((int) (Constants.music_player.getDesiredVolume() * 100.0), music_label_x, music_label_y, EnumDrawFrom.bottom_left);
		Constants.text.drawText(R.string.sound, sound_label_x, sound_label_y, EnumDrawFrom.bottom_right);
		Constants.text.drawNumber((int) (0), sound_label_x, sound_label_y, EnumDrawFrom.bottom_left);
		
		sound_volume_down_button.onDrawConstant();
		sound_volume_up_button.onDrawConstant();
		volume_down_button.onDrawConstant();
		volume_up_button.onDrawConstant();
		cancel_button.onDrawConstant();
	}
	
}
