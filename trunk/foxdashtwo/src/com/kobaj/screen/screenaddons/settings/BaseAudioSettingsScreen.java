package com.kobaj.screen.screenaddons.settings;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.Button;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.screen.screenaddons.floatingframe.BasePopup;

public class BaseAudioSettingsScreen extends BasePopup
{
	private Button cancel_button;
	private Button volume_up_button;
	private Button volume_down_button;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		cancel_button = new Button(R.string.cancel);
		volume_up_button = new Button(R.string.volume_up);
		volume_down_button = new Button(R.string.volume_down);
		
		cancel_button.onInitialize();
		volume_up_button.onInitialize();
		volume_down_button.onInitialize();
		BasePopup.alignButtonsAlongXAxis(center_y - shift_y, cancel_button, volume_up_button, volume_down_button);
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
		
		volume_down_button.onDrawConstant();
		volume_up_button.onDrawConstant();
		cancel_button.onDrawConstant();
	}
	
}
