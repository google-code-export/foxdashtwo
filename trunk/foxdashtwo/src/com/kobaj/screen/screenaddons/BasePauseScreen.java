package com.kobaj.screen.screenaddons;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.Button;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.QuadCompressed;
import com.kobaj.screen.EnumScreenState;

public class BasePauseScreen
{
	private QuadCompressed main_popup;
	private QuadCompressed secondary_popup;
	
	private Button quit_button;
	private Button cancel_button;
	
	public boolean ready_to_quit = false;
	
	private double label_x;
	private double label_y;
	
	public void onInitialize()
	{
		double center_x = 0; //heh
		double center_y = 0;
		
		main_popup = new QuadCompressed(R.raw.big_popup, R.raw.big_popup_alpha, 626, 386);
		secondary_popup = new QuadCompressed(R.raw.little_popup, R.raw.little_popup, 326, 206);
		
		double shift_y = Functions.screenHeightToShaderHeight(45);
		double shift_x = Functions.screenWidthToShaderWidth(90);
		
		label_x = center_x;
		label_y = center_y + shift_y;
		
		quit_button = new Button(R.string.quit, center_x + shift_x, center_y - shift_y);
		cancel_button = new Button(R.string.cancel, center_x - shift_x, center_y - shift_y);
	}
	
	public void reset()
	{
		ready_to_quit = false;
	}
	
	public void onUpdate(double delta)
	{
		if(ready_to_quit)
		{
			if(quit_button.isReleased())
			{
				//terrible
				GameActivity.activity.finish();
			}
			else if(cancel_button.isReleased())
					ready_to_quit = false;
		}
		else
		{
			if(quit_button.isReleased())
				ready_to_quit = true;
			else if(cancel_button.isReleased())
			{
				//also terrible
				GameActivity.mGLView.my_game.onChangeScreenState(EnumScreenState.running);
			}
		}
	}
	
	public void onDraw()
	{
		if(ready_to_quit)
		{
			secondary_popup.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, 0xCCFFDDDD, true);
			Constants.text.drawText(R.string.are_you_sure, label_x, label_y, EnumDrawFrom.center);
		}
		else
		{
			main_popup.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, 0xCC999999, true);
			Constants.text.drawText(R.string.paused, label_x, label_y, EnumDrawFrom.center);
		}
		
		cancel_button.onDrawConstant();
		quit_button.onDrawConstant();
	}
}
