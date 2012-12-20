package com.kobaj.screen;

import android.graphics.Color;

import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.Button;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Tween.EnumTweenEvent;
import com.kobaj.opengldrawable.Tween.TweenEvent;
import com.kobaj.opengldrawable.Tween.TweenManager;
import com.kobaj.screen.screenaddons.floatingframe.BaseQuit;
import com.kobaj.screen.screenaddons.settings.BaseSettingsScreen;
import com.kobaj.screen.screenaddons.settings.DebugScreen;

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
		
		mouse_cross = DebugScreen.mouse(Color.BLUE);
		mouse_rot = DebugScreen.mouse(Color.GREEN);
	}
	
	private double delete_me = 0;
	private Quad delete_me_too;
	private Quad mouse_cross;
	private Quad mouse_rot;
	
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
			double y_pos_shader = 0;
			Constants.physics.addSpringY(.00003, .007, 0, play_button.invisible_outline.y_pos - y_pos_shader, play_button.invisible_outline);
			Constants.physics.integratePhysics(delta, play_button.invisible_outline);
			
			y_pos_shader = -settings_button.invisible_outline.shader_height;
			Constants.physics.addSpringY(.00003, .007, 0, settings_button.invisible_outline.y_pos - y_pos_shader, settings_button.invisible_outline);
			Constants.physics.integratePhysics(delta, settings_button.invisible_outline);
			
			y_pos_shader = 2.0 * -quit_button.invisible_outline.shader_height;
			Constants.physics.addSpringY(.00003, .007, 0, quit_button.invisible_outline.y_pos - y_pos_shader, quit_button.invisible_outline);
			Constants.physics.integratePhysics(delta, quit_button.invisible_outline);
			
			double left = quit_button.invisible_outline.best_fit_aabb.main_rect.left;
			double top = quit_button.invisible_outline.best_fit_aabb.main_rect.top;
			double right = quit_button.invisible_outline.best_fit_aabb.main_rect.right;
			double bottom = quit_button.invisible_outline.best_fit_aabb.main_rect.bottom;
			
			left = Functions.shaderXToScreenX(left);
			top = Functions.shaderYToScreenY(top);
			right = Functions.shaderXToScreenX(right);
			bottom = Functions.shaderXToScreenX(bottom);
			
			settings_button.invisible_outline.setScale(4);

			delete_me += delta;
			if (delete_me > 4000)
			{
				delete_me = 0;
				
				if (delete_me_too == null)
				{
					delete_me_too = DebugScreen.outline(settings_button.invisible_outline.unrotated_aabb.main_rect,
							settings_button.invisible_outline.best_fit_aabb.main_rect);
					
					//delete_me_too = DebugScreen.outline(play_button.invisible_outline.best_fit_aabb.main_rect, settings_button.invisible_outline.best_fit_aabb.main_rect,
					//		quit_button.invisible_outline.best_fit_aabb.main_rect);
					delete_me_too.setXYPos(0, 0, EnumDrawFrom.center);
				}
			}
			
			double x = Functions.screenXToShaderX(Constants.input_manager.getX(0));
			double y = Functions.screenYToShaderY(Functions.fix_y(Constants.input_manager.getY(0)));
			mouse_cross.setXYPos(x, y, EnumDrawFrom.center);
			
			mouse_rot.setRotationZ(settings_button.invisible_outline.degree);
			
			// shift
			x -= settings_button.invisible_outline.x_pos;
			y -= settings_button.invisible_outline.y_pos;
			
			// rotate
			final double rads = (float) Math.toRadians(-settings_button.invisible_outline.degree);
			final double cos_rads = Math.cos(rads);
			final double sin_rads = Math.sin(rads);
			x = (x * cos_rads - y * sin_rads);
			y = (y * cos_rads + x * sin_rads);
			
			// shift back
			x += settings_button.invisible_outline.x_pos;
			y += settings_button.invisible_outline.y_pos;
			
			mouse_rot.setXYPos(x, y, EnumDrawFrom.center);
			
			play_tween.onUpdate(delta);
			settings_tween.onUpdate(delta);
			quit_tween.onUpdate(delta);
			
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
		else
		{
			double x_pos = Functions.screenXToShaderX(500);
			double y_pos = Functions.screenYToShaderY((int) Functions.fix_y(100));
			Constants.text.drawText(R.string.fdtdh, x_pos, y_pos, EnumDrawFrom.center);
			
			play_button.onDrawConstant();
			settings_button.onDrawConstant();
			quit_button.onDrawConstant();
		}
		
		if(delete_me_too != null)
			delete_me_too.onDrawAmbient(Constants.my_ip_matrix, true);
		
		mouse_cross.onDrawAmbient(Constants.my_ip_matrix, true);
		mouse_rot.onDrawAmbient(Constants.my_ip_matrix, true);
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
