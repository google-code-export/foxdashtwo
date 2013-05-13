package com.kobaj.screen;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.input.EnumKeyCodes;
import com.kobaj.level.EnumLayerTypes;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.Button;
import com.kobaj.opengldrawable.Button.TextButton;
import com.kobaj.opengldrawable.Tween.EnumTweenEvent;
import com.kobaj.opengldrawable.Tween.TweenEvent;
import com.kobaj.opengldrawable.Tween.TweenManager;
import com.kobaj.screen.screenaddons.RotationLoadingJig;
import com.kobaj.screen.screenaddons.floatingframe.BaseError;
import com.kobaj.screen.screenaddons.floatingframe.BaseLoginInfo;
import com.kobaj.screen.screenaddons.floatingframe.BasePlayType;
import com.kobaj.screen.screenaddons.floatingframe.BaseQuit;
import com.kobaj.screen.screenaddons.settings.BaseSettingsScreen;

public class TitleScreen extends BaseScreen
{
	private BasePlayType base_play;
	private BaseSettingsScreen base_settings;
	private BaseQuit base_quit;
	private BaseError base_error;
	private BaseLoginInfo base_login;
	
	private Button play_button;
	private Button settings_button;
	private Button quit_button;
	private Button login_button;
	private TweenManager play_tween;
	private TweenManager settings_tween;
	private TweenManager quit_tween;
	private TweenManager login_tween;
	
	private RotationLoadingJig network_loader;
	
	// not the /best/ way of doing things, but it works and is efficient
	private boolean ready_to_quit = false;
	private boolean settings_visible = false;
	private boolean crash_visible = false;
	private boolean play_visible = false;
	private boolean login_visible = false;
	
	// display a popup only once when class is loaded
	public boolean crashed = false;
	
	// wheather to fade and what to fade to.
	public boolean fade_in = true;
	public static boolean fade_play = false;
	
	public TitleScreen()
	{
		fade_play = false;
	}
	
	@Override
	public void onLoad()
	{
		Constants.music_player.stop(Constants.music_fade_time);
		
		// Available buttons
		double x_offset = -.3;
		
		// TODO change all these to look the same regardless of screen size,
		play_button = new TextButton(R.string.play, false);
		play_button.onInitialize();
		play_button.setXYPos(x_offset, -Constants.shader_height - play_button.invisible_outline().shader_height, EnumDrawFrom.center);
		play_tween = new TweenManager(play_button.invisible_outline(),//
				new TweenEvent(EnumTweenEvent.delay, 0 + x_offset, -Constants.shader_height - play_button.invisible_outline().shader_height),//
				1000,//
				new TweenEvent(EnumTweenEvent.delay, 0 + x_offset, play_button.invisible_outline().shader_height),//
				400,//
				new TweenEvent(EnumTweenEvent.rotate, 0 + x_offset, 0, Color.WHITE, 35));//
		
		settings_button = new TextButton(R.string.settings_button, false);
		settings_button.onInitialize();
		settings_tween = new TweenManager(settings_button.invisible_outline(),//
				new TweenEvent(EnumTweenEvent.delay, .1 + x_offset, -Constants.shader_height - settings_button.invisible_outline().shader_height),//
				300,//
				new TweenEvent(EnumTweenEvent.move, .1 + x_offset, -Constants.shader_height - settings_button.invisible_outline().shader_height),//
				1000,//
				new TweenEvent(EnumTweenEvent.delay, .1 + x_offset, 0),//
				400,//
				new TweenEvent(EnumTweenEvent.rotate, .1 + x_offset, -settings_button.invisible_outline().shader_height, Color.WHITE, 35));//
		
		quit_button = new TextButton(R.string.quit, false);
		quit_button.onInitialize();
		quit_tween = new TweenManager(quit_button.invisible_outline(),//
				new TweenEvent(EnumTweenEvent.delay, .2 + x_offset, -Constants.shader_height - quit_button.invisible_outline().shader_height),//
				600,//
				new TweenEvent(EnumTweenEvent.move, .2 + x_offset, -Constants.shader_height - quit_button.invisible_outline().shader_height),//
				1000,//
				new TweenEvent(EnumTweenEvent.delay, .2 + x_offset, -quit_button.invisible_outline().shader_height),//
				400,//
				new TweenEvent(EnumTweenEvent.rotate, .2 + x_offset, 2.0 * -quit_button.invisible_outline().shader_height, Color.WHITE, 35));//
		
		login_button = new TextButton(R.string.login_button, false);
		login_button.onInitialize();
		login_tween = new TweenManager(login_button.invisible_outline(), //
				new TweenEvent(EnumTweenEvent.delay, Constants.shader_width, Constants.shader_height / 2.0 - login_button.invisible_outline().shader_height), //
				1000, //
				new TweenEvent(EnumTweenEvent.move, Constants.shader_width / 2.0 - login_button.invisible_outline().shader_width * 1.5, Constants.shader_height / 2.0
						- login_button.invisible_outline().shader_height)); //
		
		network_loader = new RotationLoadingJig();
		network_loader.onInitialize();
		network_loader.radius = Constants.spinning_jig_radius;
		
		// and allow the user to set some settings
		base_settings = new BaseSettingsScreen();
		base_settings.onInitialize();
		
		base_quit = new BaseQuit();
		base_quit.onInitialize();
		
		// level selection and such
		base_play = new BasePlayType();
		base_play.onInitialize();
		
		// login box
		base_login = new BaseLoginInfo();
		base_login.onInitialize();
		
		// if the last screen had a bug, alert the user on this screen
		if (crashed)
		{
			base_error = new BaseError();
			base_error.onInitialize();
			crash_visible = true;
		}
	}
	
	@Override
	public void onUnload()
	{
		if (crashed)
			base_error.onUnInitialize();
		
		base_settings.onUnInitialize();
		base_quit.onUnInitialize();
		play_button.onUnInitialize();
		settings_button.onUnInitialize();
		quit_button.onUnInitialize();
		base_play.onUnInitialize();
		login_button.onUnInitialize();
		base_login.onUnInitialize();
		
		network_loader.onUnInitialize();
	}
	
	@Override
	public void onUpdate(double delta)
	{
		// that music
		Constants.music_player.onUpdate();
		
		// these are all the different possible poups that can be visible
		if (settings_visible)
			settings_visible = base_settings.onUpdate(delta);
		else if (ready_to_quit)
			ready_to_quit = base_quit.onUpdate(delta);
		else if (crash_visible)
			crash_visible = base_error.onUpdate(delta);
		else if (play_visible)
			play_visible = base_play.onUpdate(delta);
		else if (login_visible)
			login_visible = base_login.onUpdate(delta);
		else
		{
			// and if nothing is visible, then just update like normal
			if (Constants.input_manager.getKeyPressed(EnumKeyCodes.back))
				ready_to_quit = true;
			
			// testing spring
			double y_pos_shader = 0;
			Constants.physics.addSpringY(.00003, .007, 0, play_button.invisible_outline().y_pos_shader - y_pos_shader, play_button.invisible_outline());
			Constants.physics.integratePhysics(delta, play_button.invisible_outline());
			
			y_pos_shader = -settings_button.invisible_outline().shader_height;
			Constants.physics.addSpringY(.00003, .007, 0, settings_button.invisible_outline().y_pos_shader - y_pos_shader, settings_button.invisible_outline());
			Constants.physics.integratePhysics(delta, settings_button.invisible_outline());
			
			y_pos_shader = 2.0 * -quit_button.invisible_outline().shader_height;
			Constants.physics.addSpringY(.00003, .007, 0, quit_button.invisible_outline().y_pos_shader - y_pos_shader, quit_button.invisible_outline());
			Constants.physics.integratePhysics(delta, quit_button.invisible_outline());
			
			// tween
			play_tween.onUpdate(delta);
			settings_tween.onUpdate(delta);
			quit_tween.onUpdate(delta);
			login_tween.onUpdate(delta);
			
			network_loader.onUpdate(delta);
			
			// and buttons
			if (play_button.isReleased())
			{
				base_play.reset();
				play_visible = true;
			}
			else if (quit_button.isReleased())
				ready_to_quit = true;
			else if (settings_button.isReleased())
			{
				base_settings.reset();
				settings_visible = true;
			}
			else if (login_button.isReleased() && !Constants.logged_in && !Constants.logging_in)
				login_visible = true;
			
			if (Constants.logging_in)
			{
				login_tween.finish();
				
				// set the rotator
				network_loader.x_pos = login_button.invisible_outline().x_pos_shader;
				network_loader.y_pos = login_button.invisible_outline().y_pos_shader;
			}
		}
		
		// regardless we fade in
		if (fade_in)
			fade_in = tween_fade_in.onUpdate(delta);
		if (fade_play)
			if (!tween_fade_out.onUpdate(delta))
				GameActivity.mGLView.my_game.onChangeScreen(new SinglePlayerScreen());
	}
	
	@Override
	public void onDrawObject(EnumLayerTypes... types)
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onDrawLight()
	{
		// TODO Auto-generated method stub
		
	}
	
	@SuppressLint("WrongCall")
	@Override
	public void onDrawConstant()
	{
		if (settings_visible)
			base_settings.onDraw();
		else if (ready_to_quit)
			base_quit.onDraw();
		else if (crash_visible)
			base_error.onDraw();
		else if (play_visible)
			base_play.onDraw();
		else if (login_visible)
			base_login.onDraw();
		else
		{
			double x_pos = Functions.screenXToShaderX(500);
			double y_pos = Functions.screenYToShaderY((int) Functions.fix_y(100));
			Constants.text.drawText(R.string.fdtdh, x_pos, y_pos, EnumDrawFrom.center);
			
			play_button.onDrawConstant();
			settings_button.onDrawConstant();
			quit_button.onDrawConstant();
			
			if (!Constants.logged_in && !Constants.logging_in)
				login_button.onDrawConstant();
			
			if (Constants.network_activity > 0 || Constants.logging_in)
				network_loader.onDrawLoading();
		}
		
		if (fade_in || fade_play)
			black_overlay_fade.onDrawAmbient(Constants.my_ip_matrix, true);
	}
	
	@Override
	public void onDrawLoading(double delta)
	{
		// draw nothing
	}
	
	@Override
	public void onPause()
	{
		// do nothing
	}
	
	@Override
	public void onScreenChange(boolean next_level)
	{
		// TODO Auto-generated method stub
		
	}
}
