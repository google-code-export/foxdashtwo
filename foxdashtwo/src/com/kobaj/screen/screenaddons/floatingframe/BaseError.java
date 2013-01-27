package com.kobaj.screen.screenaddons.floatingframe;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.TextButton;

public class BaseError extends BaseFloatingFrame
{
	TextButton ok_button;
	
	@Override
	public void onInitialize()
	{
		super.onInitialize();
		
		ok_button = new TextButton(R.string.ok);
		ok_button.onInitialize();
		
		BaseFloatingFrame.alignButtonsAlongXAxis(cancel_shift_y, ok_button);
	}
	
	@Override
	public void onUnInitialize()
	{
		super.onUnInitialize();
		
		ok_button.onUnInitialize();
	}
	
	@Override
	public boolean onUpdate(double delta)
	{	
		if(ok_button.isReleased())
			return false;
		
		return super.onUpdate(delta);
	}
	
	@Override
	public void onDraw()
	{
		main_popup.onDrawAmbient(Constants.my_ip_matrix, true);
		Constants.text.drawText(R.string.error, label_x, label_y, EnumDrawFrom.center);
		
		Constants.text.drawText(R.string.error_message, 0, 0, EnumDrawFrom.center);
		
		ok_button.onDrawConstant();
	}
	
}
