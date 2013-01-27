package com.kobaj.screen.screenaddons.floatingframe;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.TextButton;
import com.kobaj.screen.TitleScreen;

public class BasePlayType extends BaseFloatingFrame
{
	TextButton back_button;
	TextButton new_game_button;
	TextButton continue_button;
	TextButton download_maps_button;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		back_button = new TextButton(R.string.back);
		new_game_button = new TextButton(R.string.new_game);
		continue_button = new TextButton(R.string.continue_game);
		download_maps_button = new TextButton(R.string.download_maps);
		
		back_button.onInitialize();
		new_game_button.onInitialize();
		continue_button.onInitialize();
		download_maps_button.onInitialize();
		
		double shift_y = Functions.screenHeightToShaderHeight(32);
		double move_y = Functions.screenHeightToShaderHeight(5); // same value is in base audio settings
		
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y + shift_y + move_y, new_game_button, continue_button);
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y - 2.0 * shift_y + move_y, download_maps_button);
		BaseFloatingFrame.alignButtonsAlongXAxis(cancel_shift_y, back_button);
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		back_button.onUnInitialize();
		new_game_button.onUnInitialize();
		continue_button.onUnInitialize();
		download_maps_button.onUnInitialize();
	}
	
	@Override
	public boolean onUpdate(double delta)
	{
		if (new_game_button.isReleased())
		{
			// erase the last checkpoint since we are 'starting new';
			SinglePlayerSave.last_level = null;
			SinglePlayerSave.last_checkpoint = null;
			TitleScreen.fade_play = true;
		}
		else if (continue_button.isReleased())
		{
			TitleScreen.fade_play = true;
		}
		else if (download_maps_button.isReleased())
		{
			
		}
		else if (back_button.isReleased())
			return false;
		
		return super.onUpdate(delta);
	}
	
	public void reset()
	{
		
	}
	
	@Override
	public void onDraw()
	{
		main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
		Constants.text.drawText(R.string.play_header, label_x, label_y, EnumDrawFrom.center);
		
		new_game_button.onDrawConstant();
		continue_button.onDrawConstant();
		download_maps_button.onDrawConstant();
		back_button.onDrawConstant();
	}
	
}
