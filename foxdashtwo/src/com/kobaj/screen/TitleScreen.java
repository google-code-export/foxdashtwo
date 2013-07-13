package com.kobaj.screen;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.audio.MusicPlayList;
import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.input.EnumKeyCodes;
import com.kobaj.level.EnumLayerTypes;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.Button;
import com.kobaj.opengldrawable.Button.TextButton;
import com.kobaj.opengldrawable.NewParticle.RingParticle;
import com.kobaj.opengldrawable.Quad.QuadCompressed;
import com.kobaj.opengldrawable.Tween.EnumTweenEvent;
import com.kobaj.opengldrawable.Tween.TriggerFade;
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
	
	private boolean touch_debugging = false;
	private QuadCompressed beta_lines_v;
	private QuadCompressed beta_lines_h;
	private double y_limit = 720;
	private double x_limit = 1280;
	private QuadCompressed x_line;
	private QuadCompressed y_line;
	
	private MusicPlayList play_list;
	
	private RingParticle rings;
	
	// thats right, I'm crazy!
	private SinglePlayerScreen backdrop_screen;
	
	private boolean title_screen_ready = false;
	
	private TriggerFade my_trigger_fader;
	private int last_loading = 0;
	
	public TitleScreen()
	{
		fade_play = false;
	}
	
	@Override
	public void onLoad()
	{
		my_trigger_fader = new TriggerFade();
		my_trigger_fader.onInitialize(1000, 563, R.raw.blur_title_50, R.raw.blur_title_25, R.raw.blur_title_15, R.raw.blur_title_10);
		
		play_list = new MusicPlayList();
		this.startMusic();
		
		Functions.setCamera(0, 0, Constants.arbitrary_z);
		
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
		
		if (touch_debugging)
		{
			this.y_limit = Constants.height;
			this.x_limit = Constants.width;
			this.beta_lines_h = new QuadCompressed(R.raw.white, R.raw.white, 2, (int) y_limit);
			this.beta_lines_v = new QuadCompressed(R.raw.white, R.raw.white, (int) x_limit, 2);
			
			this.x_line = new QuadCompressed(R.raw.white, R.raw.white, 2, (int) y_limit * 2);
			this.x_line.color = Color.GREEN;
			this.y_line = new QuadCompressed(R.raw.white, R.raw.white, (int) x_limit * 2, 2);
			this.y_line.color = Color.RED;
		}
		
		// silly rings
		rings = new RingParticle();
		rings.onInitialize();
		
		// if the last screen had a bug, alert the user on this screen
		if (crashed)
		{
			base_error = new BaseError();
			base_error.onInitialize();
			crash_visible = true;
		}
		
		title_screen_ready = true;
		
		SinglePlayerSave.last_level = "level_title";
		backdrop_screen = new SinglePlayerScreen();
		backdrop_screen.fade_in = false;
		backdrop_screen.run();
	}
	
	@Override
	public void onUnload()
	{
		backdrop_screen.onUnInitialize();
		
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
	public void on100msUpdate()
	{
		play_list.onUpdate();
	}
	
	@Override
	public void onUpdate(double delta)
	{
		my_trigger_fader.onUpdate(delta);
		
		if (last_loading != this.backdrop_screen.loading_amount)
		{
			int current_load = backdrop_screen.loading_amount;
			
			if (current_load == 60 || //
					current_load == 80)
				my_trigger_fader.trigger();
			
			last_loading = backdrop_screen.loading_amount;
		}
		
		// that music
		play_list.onUpdate();
		
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
			// silly rings
			rings.onUpdate(delta);
			
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
		
		if (this.backdrop_screen.current_state == EnumScreenState.running)
			this.backdrop_screen.onUpdate(delta / 1.5);
	}
	
	@Override
	public void onDrawObject(EnumLayerTypes... types)
	{
		my_trigger_fader.trigger();
		this.backdrop_screen.onDrawObject(types);
	}
	
	@Override
	public void onDrawLight()
	{
		this.backdrop_screen.onDrawLight();
	}
	
	@SuppressLint("WrongCall")
	@Override
	public void onDrawConstant()
	{
		my_trigger_fader.onDraw();
		
		if (this.backdrop_screen.current_state != EnumScreenState.running)
		{
			Constants.text.drawText(R.string.loading_level_data, Functions.screenXToShaderX(150), Functions.screenYToShaderY(25), EnumDrawFrom.center);
		}
		
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
			rings.onDrawConstant();
			
			// TODO add the actual logo here and delete this.
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
		
		if (touch_debugging)
		{
			// beta lines
			this.beta_lines_h.setXYPos(Functions.screenXToShaderX(x_limit - 2.0), Functions.screenYToShaderY(y_limit), EnumDrawFrom.top_left);
			this.beta_lines_h.onDrawAmbient(Constants.my_ip_matrix, true);
			this.beta_lines_h.setXYPos(Functions.screenXToShaderX(0), Functions.screenYToShaderY(y_limit), EnumDrawFrom.top_left);
			this.beta_lines_h.onDrawAmbient(Constants.my_ip_matrix, true);
			
			this.beta_lines_v.setXYPos(Functions.screenXToShaderX(0), Functions.screenYToShaderY(2), EnumDrawFrom.top_left);
			this.beta_lines_v.onDrawAmbient(Constants.my_ip_matrix, true);
			this.beta_lines_v.setXYPos(Functions.screenXToShaderX(0), Functions.screenYToShaderY(y_limit), EnumDrawFrom.top_left);
			this.beta_lines_v.onDrawAmbient(Constants.my_ip_matrix, true);
			
			for (int i = 0; i < Constants.input_manager.finger_count; i++)
				if (Constants.input_manager.getTouched(i))
				{
					this.y_line
							.setXYPos(Functions.screenXToShaderX(Constants.input_manager.getX(i)), Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i))), EnumDrawFrom.center);
					this.y_line.onDrawAmbient(Constants.my_ip_matrix, true);
					this.x_line
							.setXYPos(Functions.screenXToShaderX(Constants.input_manager.getX(i)), Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(i))), EnumDrawFrom.center);
					this.x_line.onDrawAmbient(Constants.my_ip_matrix, true);
				}
		}
	}
	
	@SuppressLint("WrongCall")
	@Override
	public void onDrawLoading(double delta)
	{
		if (!this.title_screen_ready)
			Constants.text.drawText(R.string.loading, 0, 0, EnumDrawFrom.center);
		else
		{
			onUpdate(delta);
			onDrawConstant();
		}
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
	
	private void startMusic()
	{
		play_list.setPlayList(-1, R.raw.music_title_screen, R.raw.music_title_screen_loop_body);
		play_list.startLoopLast(0, true);
	}
}
