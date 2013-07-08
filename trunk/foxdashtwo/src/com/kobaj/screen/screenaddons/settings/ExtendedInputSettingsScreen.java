package com.kobaj.screen.screenaddons.settings;

import android.annotation.SuppressLint;

import com.kobaj.account_settings.UserSettings;
import com.kobaj.foxdashtwo.R;
import com.kobaj.input.GameInputModifier;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.TextButton;
import com.kobaj.screen.screenaddons.floatingframe.BaseFloatingFrame;

public class ExtendedInputSettingsScreen extends BaseFloatingFrame
{
	private TextButton cancel_button;
	private TextButton reset_button;
	
	public GameInputModifier my_modifier;
	
	private double input_label_y;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		my_modifier = new GameInputModifier();
		my_modifier.onInitialize();
		
		cancel_button = new TextButton(R.string.ok, true);
		cancel_button.onInitialize();
		
		reset_button = new TextButton(R.string.reset, true);
		reset_button.onInitialize();
		
		double shift_y = Functions.screenHeightToShaderHeight(32);
		double move_y = Functions.screenHeightToShaderHeight(5); // same value is in baseplaytype
		
		input_label_y = 2 * shift_y + move_y;
		
		BaseFloatingFrame.alignButtonsAlongXAxis(cancel_shift_y, reset_button, cancel_button);
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		cancel_button.onUnInitialize();
		reset_button.onUnInitialize();
		
		my_modifier.onUnInitialize();
	}
	
	@Override
	public boolean onUpdate(double delta)
	{
		// move things around
		if (my_modifier.getInputType().getTouchedLeft())
		{
			UserSettings.left_button_position.x = Constants.input_manager.getX(0);
			UserSettings.left_button_position.y = Constants.input_manager.getY(0);
			my_modifier.getInputType().updateUserSetPositions();
		}
		else if (my_modifier.getInputType().getTouchedRight())
		{
			UserSettings.right_button_position.x = Constants.input_manager.getX(0);
			UserSettings.right_button_position.y = Constants.input_manager.getY(0);
			my_modifier.getInputType().updateUserSetPositions();
		}
		else if (my_modifier.getInputType().getTouchedJump())
		{
			UserSettings.jump_button_position.x = Constants.input_manager.getX(0);
			UserSettings.jump_button_position.y = Constants.input_manager.getY(0);
			my_modifier.getInputType().updateUserSetPositions();
		}
		
		// regular popup stuff
		if (reset_button.isReleased())
		{
			UserSettings.resetButtonPoints();
			my_modifier.getInputType().updateUserSetPositions();
		}
		else if (cancel_button.isReleased())
			return false;
		
		return super.onUpdate(delta);
	}
	
	@SuppressLint("WrongCall")
	@Override
	public void onDraw()
	{
		main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
		Constants.text.drawText(R.string.input_adjustment, label_x, label_y, EnumDrawFrom.center);
		
		Constants.text.drawText(R.string.adjustment_blurb, 0, input_label_y, EnumDrawFrom.center_top);
		
		reset_button.onDrawConstant();
		cancel_button.onDrawConstant();
		
		my_modifier.onDraw();
	}
}
