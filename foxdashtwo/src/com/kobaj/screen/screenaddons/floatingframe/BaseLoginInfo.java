package com.kobaj.screen.screenaddons.floatingframe;

import com.kobaj.account_settings.UserSettings;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.Button;
import com.kobaj.opengldrawable.Button.TextButton;

public class BaseLoginInfo extends BaseFloatingFrame
{
	private Button cancel_button;
	private Button ok_button;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		cancel_button = new TextButton(R.string.cancel);
		ok_button = new TextButton(R.string.ok);
		
		cancel_button.onInitialize();
		ok_button.onInitialize();
		BaseFloatingFrame.alignButtonsAlongXAxis(center_y - shift_y * 3.0, cancel_button, ok_button);
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		ok_button.onUnInitialize();
		cancel_button.onUnInitialize();
	}
	
	@Override
	public boolean onUpdate(double delta)
	{
		if(cancel_button.isReleased())
			return false;
		else if(ok_button.isReleased())
		{
			UserSettings.auto_login = true;
			
			// should alwayse be true
			if (UserSettings.selected_account_login == -1)
			{
				if (Constants.accounts.count_accounts() > 1)
					Constants.accounts.account_popup();
				else if(Constants.accounts.count_accounts() == 1)
				{
					UserSettings.selected_account_login = 0;
					Constants.accounts.account_login();
				}
			}
			
			return false;
		}
				
		return super.onUpdate(delta);
	}
	
	@Override
	public void onDraw()
	{
		main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
		Constants.text.drawText(R.string.login, label_x, label_y, EnumDrawFrom.center);
		
		Constants.text.drawText(R.string.login_blurb, 0,0, EnumDrawFrom.center);
		
		cancel_button.onDrawConstant();
		ok_button.onDrawConstant();
	}
}
