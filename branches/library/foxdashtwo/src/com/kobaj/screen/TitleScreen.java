package com.kobaj.screen;

import android.graphics.Color;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.Button;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Tween.TweenEvent;
import com.kobaj.opengldrawable.Tween.TweenManager;
import com.kobaj.screen.screenaddons.floatingframe.BaseQuit;
import com.kobaj.screen.screenaddons.settings.BaseSettingsScreen;

public class TitleScreen extends BaseScreen
{
	private BaseSettingsScreen base_settings;
	private BaseQuit base_quit;
	
	private Button play_button;
	private Button settings_button;
	private Button quit_button;
	private TweenManager play_tween;
	private TweenManager settings_tween;
	private TweenManager quit_tween;
	
	private boolean ready_to_quit = false;
	private boolean settings_visible = false;
	
	@Override
	public void onLoad()
	{
		double x_offset = -.3;
		
		play_button = new Button(R.string.play);
		play_button.onInitialize();
		play_tween = new TweenManager(play_button.invisible_outline,//
				new TweenEvent(0 + x_offset, -Constants.shader_height - play_button.invisible_outline.shader_height),//
				1000,//
				new TweenEvent(0 + x_offset, play_button.invisible_outline.shader_height),//
				400,//
				new TweenEvent(0 + x_offset, 0, Color.WHITE, 35));//
		
		settings_button = new Button(R.string.settings);
		settings_button.onInitialize();
		settings_tween = new TweenManager(settings_button.invisible_outline,//
				new TweenEvent(0 + x_offset, -Constants.shader_height - settings_button.invisible_outline.shader_height),//
				300,//
				new TweenEvent(.1 + x_offset, -Constants.shader_height - settings_button.invisible_outline.shader_height),//
				1000,//
				new TweenEvent(.1 + x_offset, 0),//
				400,//
				new TweenEvent(.1 + x_offset, -settings_button.invisible_outline.shader_height, Color.WHITE, 35));//
		
		quit_button = new Button(R.string.quit);
		quit_button.onInitialize();
		quit_tween = new TweenManager(quit_button.invisible_outline,//
				new TweenEvent(.2 + x_offset, -Constants.shader_height - quit_button.invisible_outline.shader_height),//
				600,//
				new TweenEvent(.2 + x_offset, -Constants.shader_height - quit_button.invisible_outline.shader_height),//
				1000,//
				new TweenEvent(.2 + x_offset, -quit_button.invisible_outline.shader_height),//
				400,//
				new TweenEvent(.2 + x_offset, 2.0 * -quit_button.invisible_outline.shader_height, Color.WHITE, 35));//
		
		base_settings = new BaseSettingsScreen();
		base_settings.onInitialize();
		
		base_quit = new BaseQuit();
		base_quit.onInitialize();
	}
	
	@Override
	public void onUpdate(double delta)
	{
		// base_settings.onUpdate(delta);
		if (settings_visible)
			settings_visible = base_settings.onUpdate(delta);
		else if (ready_to_quit)
			ready_to_quit = base_quit.onUpdate(delta);
		else
		{
			play_tween.onUpdate(delta);
			settings_tween.onUpdate(delta);
			quit_tween.onUpdate(delta);
			
			if(play_button.isReleased())
				GameActivity.mGLView.my_game.onChangeScreen(new SinglePlayerScreen());
			else if (quit_button.isReleased())
				ready_to_quit = true;
			else if (settings_button.isReleased())
			{
				base_settings.reset();
				settings_visible = true;
			}
		}
	}
	
	@Override
	public void onDrawObject()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDrawLight()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDrawConstant()
	{
		if (settings_visible)
			base_settings.onDraw();
		else if (ready_to_quit)
			base_quit.onDraw();
		else
		{
			double x_pos = Functions.screenXToShaderX(500);
			double y_pos = Functions.screenYToShaderY((int) Functions.fix_y(100));
			Constants.text.drawText(R.string.fdtdh, x_pos, y_pos, EnumDrawFrom.center);
			
			play_button.onDrawConstant();
			settings_button.onDrawConstant();
			quit_button.onDrawConstant();
		}
	}
	
	@Override
	public void onDrawLoading(double delta)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onPause()
	{
		// do nothing
	}
}
