package com.kobaj.screen.screenaddons.floatingframe;

import android.annotation.SuppressLint;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.message.download.DownloadMapsManager;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.TextButton;
import com.kobaj.screen.TitleScreen;

public class BasePlayType extends BaseFloatingFrame
{
	private TextButton back_button;
	private TextButton new_game_button;
	
	private TextButton level_select_button;
	private TextButton download_maps_button;
	
	public static final String popup_tag = "FoxDashTwoDownload";
	
	private BaseLevelSelect level_selector;
	private boolean level_select_open = false;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		back_button = new TextButton(R.string.back, true);
		new_game_button = new TextButton(R.string.new_game, true);
		level_select_button = new TextButton(R.string.level_select_game, true);
		download_maps_button = new TextButton(R.string.download_maps, true);
		
		back_button.onInitialize();
		new_game_button.onInitialize();
		level_select_button.onInitialize();
		download_maps_button.onInitialize();
		
		double shift_y = Functions.screenHeightToShaderHeight(32);
		double move_y = Functions.screenHeightToShaderHeight(5); // same value is in base audio settings
		
		if (Constants.demo_mode)
		{
			BaseFloatingFrame.alignButtonsAlongXAxis(center_y + shift_y + move_y, new_game_button/* , level_select_button */);
			// BaseFloatingFrame.alignButtonsAlongXAxis(center_y - 2.0 * shift_y + move_y, download_maps_button);
		}
		else
		{
			BaseFloatingFrame.alignButtonsAlongXAxis(center_y + shift_y + move_y, new_game_button, level_select_button);
			BaseFloatingFrame.alignButtonsAlongXAxis(center_y - 1.9 * shift_y + move_y, download_maps_button);
		}
		
		BaseFloatingFrame.alignButtonsAlongXAxis(cancel_shift_y, back_button);
		
		level_selector = new BaseLevelSelect();
		level_selector.onInitialize();
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		back_button.onUnInitialize();
		new_game_button.onUnInitialize();
		level_select_button.onUnInitialize();
		download_maps_button.onUnInitialize();
		
		level_selector.onUnInitialize();
	}
	
	@Override
	public boolean onUpdate(double delta)
	{
		if (this.level_select_open)
			level_select_open = this.level_selector.onUpdate(delta);
		else if (new_game_button.isReleased())
		{
			TitleScreen.fade_play = true;
			SinglePlayerSave.last_level = Constants.empty;
		}
		else if (level_select_button.isReleased() && !Constants.demo_mode)
		{
			level_select_open = true;
		}
		else if (download_maps_button.isReleased() && !Constants.demo_mode)
		{
			DownloadMapsManager popup = new DownloadMapsManager();
			popup.show(Constants.fragment_manager, popup_tag);
		}
		else if (back_button.isReleased())
			return false;
		
		return super.onUpdate(delta);
	}
	
	public void reset()
	{
		
	}
	
	@SuppressLint("WrongCall")
	@Override
	public void onDraw()
	{
		if (this.level_select_open)
			this.level_selector.onDraw();
		else
		{
			
			main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
			Constants.text.drawText(R.string.play_header, label_x, label_y, EnumDrawFrom.center);
			
			new_game_button.onDrawConstant();
			
			if (!Constants.demo_mode)
			{
				level_select_button.onDrawConstant();
				download_maps_button.onDrawConstant();
			}
			
			back_button.onDrawConstant();
		}
	}
}
