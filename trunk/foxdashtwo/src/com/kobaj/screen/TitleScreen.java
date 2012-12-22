package com.kobaj.screen;

import android.graphics.Color;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.Button;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Tween.EnumTweenEvent;
import com.kobaj.opengldrawable.Tween.TweenEvent;
import com.kobaj.opengldrawable.Tween.TweenManager;
import com.kobaj.screen.screenaddons.floatingframe.BaseError;
import com.kobaj.screen.screenaddons.floatingframe.BaseQuit;
import com.kobaj.screen.screenaddons.settings.BaseSettingsScreen;

public class TitleScreen extends BaseScreen
{
	private BaseSettingsScreen base_settings;
	private BaseQuit base_quit;
	private BaseError base_error;
	
	private Button play_button;
	private Button settings_button;
	private Button quit_button;
	private TweenManager play_tween;
	private TweenManager settings_tween;
	private TweenManager quit_tween;

	// not the /best/ way of doing things, but it works and is efficient
	private boolean ready_to_quit = false;
	private boolean settings_visible = false;
	private boolean crash_visible = false;
	
	public boolean crashed = false;
	
	@Override
	public void onLoad()
	{
		double x_offset = -.3;
		
		play_button = new Button(R.string.play);
		play_button.onInitialize();
		play_button.invisible_outline.setXYPos(x_offset, -Constants.shader_height - play_button.invisible_outline.shader_height, EnumDrawFrom.center);
		play_tween = new TweenManager(play_button.invisible_outline,//
				new TweenEvent(EnumTweenEvent.delay, 0 + x_offset, -Constants.shader_height - play_button.invisible_outline.shader_height),//
				1000,//
				new TweenEvent(EnumTweenEvent.delay, 0 + x_offset, play_button.invisible_outline.shader_height),//
				400,//
				new TweenEvent(EnumTweenEvent.rotate, 0 + x_offset, 0, Color.WHITE, 35));//
		
		settings_button = new Button(R.string.settings_button);
		settings_button.onInitialize();
		settings_tween = new TweenManager(settings_button.invisible_outline,//
				new TweenEvent(EnumTweenEvent.delay, .1 + x_offset, -Constants.shader_height - settings_button.invisible_outline.shader_height),//
				300,//
				new TweenEvent(EnumTweenEvent.move, .1 + x_offset, -Constants.shader_height - settings_button.invisible_outline.shader_height),//
				1000,//
				new TweenEvent(EnumTweenEvent.delay, .1 + x_offset, 0),//
				400,//
				new TweenEvent(EnumTweenEvent.rotate, .1 + x_offset, -settings_button.invisible_outline.shader_height, Color.WHITE, 35));//
		
		quit_button = new Button(R.string.quit);
		quit_button.onInitialize();
		quit_tween = new TweenManager(quit_button.invisible_outline,//
				new TweenEvent(EnumTweenEvent.delay, .2 + x_offset, -Constants.shader_height - quit_button.invisible_outline.shader_height),//
				600,//
				new TweenEvent(EnumTweenEvent.move, .2 + x_offset, -Constants.shader_height - quit_button.invisible_outline.shader_height),//
				1000,//
				new TweenEvent(EnumTweenEvent.delay, .2 + x_offset, -quit_button.invisible_outline.shader_height),//
				400,//
				new TweenEvent(EnumTweenEvent.rotate, .2 + x_offset, 2.0 * -quit_button.invisible_outline.shader_height, Color.WHITE, 35));//
		
		base_settings = new BaseSettingsScreen();
		base_settings.onInitialize();
		
		base_quit = new BaseQuit();
		base_quit.onInitialize();
		
		if(crashed)
		{
			base_error = new BaseError();
			base_error.onInitialize();
			crash_visible = true;
		}
	}
	
	@Override
	public void onUnload()
	{
		if(crashed)
			base_error.onUnInitialize();
		
		base_settings.onUnInitialize();
		base_quit.onUnInitialize();
		play_button.onUnInitialize();
		settings_button.onUnInitialize();
		quit_button.onUnInitialize();
	}
	
	@Override
	public void onUpdate(double delta)
	{
		// base_settings.onUpdate(delta);
		if (settings_visible)
			settings_visible = base_settings.onUpdate(delta);
		else if (ready_to_quit)
			ready_to_quit = base_quit.onUpdate(delta);
		else if(crash_visible)
			crash_visible = base_error.onUpdate(delta);
		else
		{
			// testing spring
			double y_pos_shader = 0;
			Constants.physics.addSpringY(.00003, .007, 0, play_button.invisible_outline.y_pos - y_pos_shader, play_button.invisible_outline);
			Constants.physics.integratePhysics(delta, play_button.invisible_outline);
			
			y_pos_shader = -settings_button.invisible_outline.shader_height;
			Constants.physics.addSpringY(.00003, .007, 0, settings_button.invisible_outline.y_pos - y_pos_shader, settings_button.invisible_outline);
			Constants.physics.integratePhysics(delta, settings_button.invisible_outline);
			
			y_pos_shader = 2.0 * -quit_button.invisible_outline.shader_height;
			Constants.physics.addSpringY(.00003, .007, 0, quit_button.invisible_outline.y_pos - y_pos_shader, quit_button.invisible_outline);
			Constants.physics.integratePhysics(delta, quit_button.invisible_outline);
			
			// tween
			play_tween.onUpdate(delta);
			settings_tween.onUpdate(delta);
			quit_tween.onUpdate(delta);
			
			// and buttons
			if (play_button.isReleased())
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
		else if(crash_visible)
			base_error.onDraw();
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
		//draw nothing
	}
	
	@Override
	public void onPause()
	{
		// do nothing
	}
}
