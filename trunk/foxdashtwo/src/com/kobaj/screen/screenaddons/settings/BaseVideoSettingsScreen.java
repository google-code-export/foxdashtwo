package com.kobaj.screen.screenaddons.settings;

import com.kobaj.account_settings.UserSettings;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.TextButton;
import com.kobaj.screen.screenaddons.floatingframe.BaseFloatingFrame;

public class BaseVideoSettingsScreen extends BaseFloatingFrame
{
	private TextButton cancel_button;
	private TextButton fbo_up_button;
	private TextButton fbo_down_button;
	
	private double music_label_x;
	private double music_label_y;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		cancel_button = new TextButton(R.string.back);
		fbo_up_button = new TextButton(R.string.fbo_up);
		fbo_down_button = new TextButton(R.string.fbo_down);
		
		cancel_button.onInitialize();
		fbo_up_button.onInitialize();
		fbo_down_button.onInitialize();
		
		double shift_y = Functions.screenHeightToShaderHeight(32);
		double move_y = Functions.screenHeightToShaderHeight(5); // same value is in baseplaytype
		
		music_label_y = 2 * shift_y + move_y;
		music_label_x = -Functions.screenWidthToShaderWidth(default_label_left);
		
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y + shift_y + move_y, fbo_down_button, fbo_up_button);
		BaseFloatingFrame.alignButtonsAlongXAxis(cancel_shift_y, cancel_button);
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		cancel_button.onUnInitialize();
		fbo_up_button.onUnInitialize();
		fbo_down_button.onUnInitialize();
	}
	
	@Override
	public boolean onUpdate(double delta)
	{
		if (fbo_up_button.isReleased())
			UserSettings.fbo(1);
		else if (fbo_down_button.isReleased())
			UserSettings.fbo(-1);
		else if (cancel_button.isReleased())
			return false;
		
		return super.onUpdate(delta);
	}
	
	@Override
	public void onDraw()
	{
		main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
		Constants.text.drawText(R.string.video, label_x, label_y, EnumDrawFrom.center);
		
		Constants.text.drawText(R.string.fbo_divider, music_label_x, music_label_y, EnumDrawFrom.bottom_right);
		Constants.text.drawNumber(UserSettings.fbo_divider, music_label_x, music_label_y, EnumDrawFrom.bottom_left);
		
		fbo_down_button.onDrawConstant();
		fbo_up_button.onDrawConstant();
		cancel_button.onDrawConstant();
	}
	
}
