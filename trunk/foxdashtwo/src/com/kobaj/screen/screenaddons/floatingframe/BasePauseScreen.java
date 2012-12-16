package com.kobaj.screen.screenaddons.floatingframe;

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
	private BaseQuit base_quit = new BaseQuit();
	
	private boolean ready_to_quit = false;
	private boolean settings_visible = false;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		base_settings.onInitialize();
		base_quit.onInitialize();
		
		cancel_button = new Button(R.string.cancel);
		quit_button = new Button(R.string.quit);
		settings_button = new Button(R.string.settings);
		
		cancel_button.onInitialize();
		quit_button.onInitialize();
		BasePopup.alignButtonsAlongXAxis(center_y - shift_y, cancel_button, quit_button);
		
		// couple extra buttons
		settings_button.onInitialize();
		BasePopup.alignButtonsAlongXAxis(center_y - 3.0 * shift_y, settings_button);
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
			settings_visible = base_settings.onUpdate(delta);
		else if (ready_to_quit)
			ready_to_quit = base_quit.onUpdate(delta);
		else
			handleButtons();
		
	}
	
	private void handleButtons()
	{
		// turning on or off children
		if (settings_button.isReleased())
			settings_visible = true;
		else if (quit_button.isReleased())
			ready_to_quit = true;
		else if (cancel_button.isReleased())
		{
			// also terrible
			GameActivity.mGLView.my_game.onChangeScreenState(EnumScreenState.running);
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
			base_quit.onDraw();
			return;
		}
		else
		{
			main_popup.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, true);
			Constants.text.drawText(R.string.paused, label_x, label_y, EnumDrawFrom.center);
			
			settings_button.onDrawConstant();
			cancel_button.onDrawConstant();
			quit_button.onDrawConstant();
		}
	}
}
