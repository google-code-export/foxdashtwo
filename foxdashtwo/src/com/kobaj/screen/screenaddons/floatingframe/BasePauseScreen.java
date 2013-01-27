package com.kobaj.screen.screenaddons.floatingframe;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.TextButton;
import com.kobaj.screen.EnumScreenState;
import com.kobaj.screen.TitleScreen;
import com.kobaj.screen.screenaddons.settings.BaseSettingsScreen;

public class BasePauseScreen extends BaseFloatingFrame
{
	private TextButton title_button;
	private TextButton quit_button;
	private TextButton cancel_button;
	private TextButton settings_button;
	
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
		
		cancel_button = new TextButton(R.string.back);
		quit_button = new TextButton(R.string.quit);
		settings_button = new TextButton(R.string.settings_button);
		title_button = new TextButton(R.string.title_screen);
		
		cancel_button.onInitialize();
		quit_button.onInitialize();
		settings_button.onInitialize();
		title_button.onInitialize();
		
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y, quit_button, title_button, settings_button);
		BaseFloatingFrame.alignButtonsAlongXAxis(cancel_shift_y, cancel_button);
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		base_settings.onUnInitialize();
		base_quit.onUnInitialize();
		
		cancel_button.onUnInitialize();
		quit_button.onUnInitialize();
		settings_button.onUnInitialize();
		title_button.onUnInitialize();
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
		return super.onUpdate(delta);
	}
	
	private void onUpdateNoBoolean(double delta)
	{
		// only update our children
		if (settings_visible)
			settings_visible = base_settings.onUpdate(delta);
		else if (ready_to_quit)
			ready_to_quit = base_quit.onUpdate(delta);
		else
			handleTextButtons();
		
	}
	
	private void handleTextButtons()
	{
		// turning on or off children
		if (settings_button.isReleased())
			settings_visible = true;
		else if (quit_button.isReleased())
			ready_to_quit = true;
		else if (title_button.isReleased())
		{
			GameActivity.mGLView.my_game.onChangeScreen(new TitleScreen());
			GameActivity.mGLView.my_game.onChangeScreenState(EnumScreenState.running);
		}
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
			base_settings.onDraw();
		else if (ready_to_quit)
			base_quit.onDraw();
		else
		{
			main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
			Constants.text.drawText(R.string.paused, label_x, label_y, EnumDrawFrom.center);
			
			title_button.onDrawConstant();
			settings_button.onDrawConstant();
			cancel_button.onDrawConstant();
			quit_button.onDrawConstant();
		}
	}
}
