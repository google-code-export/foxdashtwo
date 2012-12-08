package com.kobaj.screen.screenaddons;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.Button;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.screen.EnumScreenState;
import com.kobaj.screen.screenaddons.settings.BaseSettingsScreen;

public class BasePauseScreen extends BasePopup
{
	private Button quit_button;
	private Button cancel_button;
	private Button settings_button;
	
	// let users edit settings right in game!
	private BaseSettingsScreen base_settings = new BaseSettingsScreen();
	
	private boolean ready_to_quit = false;
	private boolean settings_visible = false;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		base_settings.onInitialize();
		
		cancel_button = new Button(R.string.cancel);
		quit_button = new Button(R.string.quit);
		settings_button = new Button(R.string.settings);
		
		Button[] buttons = BasePopup.alignButtonsAlongXAxis(center_y - shift_y, cancel_button, quit_button);
		
		// this is ok because its an array
		for (Button button : buttons)
			button.onInitialize();
		
		// couple extra buttons
		buttons = BasePopup.alignButtonsAlongXAxis(center_y - 3.0 * shift_y, settings_button);
		
		for (Button button : buttons)
			button.onInitialize();
	}
	
	public void reset()
	{
		ready_to_quit = false;
		settings_visible = false;
		
		// all our childrens
		base_settings.reset();
	}
	
	@Override
	public boolean onUpdate(double delta)
	{
		onUpdateNoBoolean(delta);
		return true;
	}
	
	private void onUpdateNoBoolean(double delta)
	{
		// only update our children
		if (settings_visible)
		{
			if (!base_settings.onUpdate(delta))
				settings_visible = false;
		}
		else
			handleButtons();
		
	}
	
	private void handleButtons()
	{
		// turning on or off children
		if (settings_button.isReleased())
			settings_visible = true;
		
		// do our screen
		if (ready_to_quit)
		{
			if (quit_button.isReleased())
			{
				// terrible
				GameActivity.activity.finish();
			}
			else if (cancel_button.isReleased())
				ready_to_quit = false;
		}
		else
		{
			if (quit_button.isReleased())
				ready_to_quit = true;
			else if (cancel_button.isReleased())
			{
				// also terrible
				GameActivity.mGLView.my_game.onChangeScreenState(EnumScreenState.running);
			}
		}
	}
	
	@Override
	public void onDraw()
	{
		if (settings_visible)
		{
			base_settings.onDraw();
			return;
		}
		
		if (ready_to_quit)
		{
			secondary_popup.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, 0xCCFFDDDD, true);
			Constants.text.drawText(R.string.are_you_sure, label_x, label_y, EnumDrawFrom.center);
		}
		else
		{
			main_popup.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, main_color, true);
			Constants.text.drawText(R.string.paused, label_x, label_y, EnumDrawFrom.center);
			settings_button.onDrawConstant();
		}
		
		cancel_button.onDrawConstant();
		quit_button.onDrawConstant();
	}
}
