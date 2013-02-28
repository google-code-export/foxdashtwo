package com.kobaj.level;

import java.util.ArrayList;
import java.util.Collections;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import android.graphics.Color;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.foxdashtwo.R;
import com.kobaj.level.LevelEventTypes.EnumLevelEvent;
import com.kobaj.level.LevelEventTypes.LevelEventTransportPlayer;
import com.kobaj.level.LevelTypeLight.LevelAmbientLight;
import com.kobaj.level.LevelTypeLight.LevelBloomLight;
import com.kobaj.level.LevelTypeLight.LevelCustomLight;
import com.kobaj.level.LevelTypeLight.LevelPointLight;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.EnumGlobalAnimationList;
import com.kobaj.opengldrawable.NewParticle.EnumParticleType;
import com.kobaj.opengldrawable.NewParticle.NParticleEmitter;
import com.kobaj.opengldrawable.NewParticle.NParticleManager;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadAnimated;
import com.kobaj.opengldrawable.Quad.QuadCompressed;

public class Level
{
	@Element
	public int backdrop_color;
	private Quad my_backdrop;
	
	@Element
	public String name;
	
	@Element
	public long changed;
	
	@Element
	public int lid;
	
	@Element
	public int left_limit;
	@Element
	public int top_limit;
	@Element
	public int right_limit;
	@Element
	public int bottom_limit;
	
	public double left_shader_limit;
	public double top_shader_limit;
	public double right_shader_limit;
	public double bottom_shader_limit;
	
	@ElementList
	public ArrayList<LevelObject> object_list;
	
	public ArrayList<LevelObject> physics_objects = new ArrayList<LevelObject>(); // references for only physics (objects that move).
	public ArrayList<LevelObject> interaction_objects = new ArrayList<LevelObject>(); // references for objects that are on the same z-plane as player.
	
	@ElementList
	public ArrayList<LevelAmbientLight> light_list; // all lights including blooms
	
	private ArrayList<LevelBloomLight> bloom_light_list = new ArrayList<LevelBloomLight>(); // references for only blooms
	
	@Element
	public LevelObject player;
	
	@ElementList
	public ArrayList<LevelEvent> event_list;
	
	// and our local particles
	private ArrayList<NParticleEmitter> local_np_emitter = new ArrayList<NParticleEmitter>();
	
	// no constructor
	
	public void onInitialize()
	{
		// backdrop
		// hey, you, the one about to optimize this out
		// the backdrop is never not transparent (at least it shouldnt be).
		// dont worry about it.
		if (backdrop_color != Color.TRANSPARENT)
		{
			my_backdrop = new QuadCompressed(R.raw.white, R.raw.white, Constants.width, Constants.height);
			my_backdrop.color = backdrop_color;
		}
		
		// pre-player
		double x_player = Functions.screenXToShaderX(player.x_pos);
		double y_player = Functions.screenYToShaderY(player.y_pos);
		
		// setup general objects
		local_np_emitter.clear();
		physics_objects.clear();
		for (int i = object_list.size() - 1; i >= 0; i--)
		{
			LevelObject reference = object_list.get(i);
			reference.onInitialize();
			
			// do some particles
			if (reference.this_object == EnumLevelObject.l2_ground_platform_floating)
			{
				RectF emitt_from = new RectF((float) (reference.quad_object.best_fit_aabb.main_rect.left + Functions.screenWidthToShaderWidth(45)),
						(float) (reference.quad_object.best_fit_aabb.main_rect.top - Functions.screenHeightToShaderHeight(85)),
						(float) (reference.quad_object.best_fit_aabb.main_rect.right - Functions.screenWidthToShaderWidth(45)),
						(float) (reference.quad_object.best_fit_aabb.main_rect.bottom + Functions.screenHeightToShaderHeight(85)));
				
				NParticleEmitter test = NParticleManager.makeEmitter(EnumParticleType.floating_dust, emitt_from);
				test.onInitialize();
				test.preUpdate();
				local_np_emitter.add(test);
				
				physics_objects.add(reference);
			}
		}
		
		// setup lights
		bloom_light_list.clear();
		for (int i = light_list.size() - 1; i >= 0; i--)
		{
			// store bloom lights in another array for easy use later
			light_list.get(i).onInitialize();
			if (LevelPointLight.class.isAssignableFrom(light_list.get(i).getClass()))
			{
				LevelPointLight temp = LevelPointLight.class.cast(light_list.get(i));
				if (temp.is_bloom)
					bloom_light_list.add(temp);
			}
			else if (LevelCustomLight.class.isAssignableFrom(light_list.get(i).getClass()))
			{
				LevelCustomLight temp = LevelCustomLight.class.cast(light_list.get(i));
				if (temp.is_bloom)
					bloom_light_list.add(temp);
			}
		}
		
		// setup events
		for (int i = event_list.size() - 1; i >= 0; i--)
		{
			event_list.get(i).onInitialize(player, object_list, light_list);
			if (event_list.get(i).this_event == EnumLevelEvent.send_to_start)
				LevelEventTransportPlayer.class.cast(event_list.get(i).my_possible_event).setTransportTo(x_player, y_player);
			else if (event_list.get(i).this_event == EnumLevelEvent.invisible_wall)
			{
				LevelEvent original = event_list.get(i);
				
				LevelObject temp = new LevelObject();
				temp.active = true;
				temp.degree = 0;
				temp.scale = 1;
				temp.this_object = EnumLevelObject.transparent;
				temp.id = original.id;
				temp.x_pos = original.x_pos + original.width / 2.0;
				temp.y_pos = original.y_pos - original.height / 2.0;
				temp.my_width = original.width;
				temp.my_height = original.height;
				temp.z_plane = 5;
				temp.layer = EnumLayerTypes.Interaction;
				
				temp.onInitialize();
				
				object_list.add(temp);
				event_list.remove(i);
			}
		}
		
		// setup player
		player.quad_object = new QuadAnimated(R.raw.fox2, R.raw.fox2_alpha, R.raw.fox_animation_list, 350, 180, 1024, 1024);
		player.eid = -1;
		player.layer = EnumLayerTypes.Pre_interaction;
		player.z_plane = -1;
		object_list.add(player);
		
		// sort the objects
		Collections.sort(object_list, new ObjectDrawSort());
		
		boolean player_set = false;
		if (SinglePlayerSave.last_checkpoint != null)
		{
			for (int i = event_list.size() - 1; i >= 0; i--)
			{
				LevelEvent event_reference = event_list.get(i);
				for (int e = event_reference.id_strings.size() - 1; e >= 0; e--)
					if (event_reference.id_strings.get(e).equals(SinglePlayerSave.last_checkpoint))
					{
						player_set = true;
						player.quad_object.setXYPos(Functions.screenXToShaderX(event_reference.x_pos + event_reference.width / 2),
								Functions.screenYToShaderY(event_reference.y_pos + event_reference.height / 2), EnumDrawFrom.center);
					}
			}
		}
		
		if (!player_set)
			player.quad_object.setXYPos(x_player, y_player, player.draw_from);
		
		// optimize which objects to collide against
		for (int i = object_list.size() - 1; i >= 0; i--)
		{
			LevelObject reference = object_list.get(i);
			if (reference.layer == EnumLayerTypes.Interaction)
				this.interaction_objects.add(reference);
		}
		
		// set widths and heights for the camera
		left_shader_limit = (Functions.screenXToShaderX(left_limit) + Constants.ratio);
		right_shader_limit = (Functions.screenXToShaderX(right_limit) - Constants.ratio);
		
		top_shader_limit = Functions.screenYToShaderY(top_limit) - Constants.shader_height / 2.0;
		bottom_shader_limit = Functions.screenYToShaderY(bottom_limit) + Constants.shader_height / 2.0;
		
		// snow
		/*
		 * RectF shader_limits_for_snow_test = new RectF((float) Functions.screenXToShaderX(left_limit),// (float) Functions.screenYToShaderY(top_limit),// (float)
		 * Functions.screenXToShaderX(right_limit),// (float) Functions.screenYToShaderY(bottom_limit));// NParticleEmitter test = NParticleManager.makeEmitter(EnumParticleType.snow,
		 * shader_limits_for_snow_test); test.onInitialize(); test.preUpdate(); local_np_emitter.add(test);
		 */
	}
	
	public void onUnInitialize()
	{
		if (my_backdrop != null)
			my_backdrop.onUnInitialize();
		
		// draw sorted objects
		for (int i = object_list.size() - 1; i >= 0; i--)
			object_list.get(i).quad_object.onUnInitialize();
		
		// particles
		for (int i = local_np_emitter.size() - 1; i >= 0; i--)
			local_np_emitter.get(i).onUnInitialize();
		
		// bloom lights
		for (int i = bloom_light_list.size() - 1; i >= 0; i--)
			bloom_light_list.get(i).onUnInitialize();
	}
	
	public void onUpdate(double delta)
	{
		for (int i = light_list.size() - 1; i >= 0; i--)
			light_list.get(i).onUpdate(delta);
		
		for (int i = event_list.size() - 1; i >= 0; i--)
			event_list.get(i).onUpdate(delta);
		
		// whaaaaat
		for (int i = physics_objects.size() - 1; i >= 0; i--)
			physics_objects.get(i).onUpdate(delta);
		
		for (int i = local_np_emitter.size() - 1; i >= 0; i--)
			local_np_emitter.get(i).onUpdate(delta);
		
		if (QuadAnimated.class.isAssignableFrom(player.quad_object.getClass()))
		{
			QuadAnimated reference = QuadAnimated.class.cast(player.quad_object);
			
			double current_x_speed = Math.abs(player.quad_object.x_vel);
			
			// currently playing animation
			if (current_x_speed < Constants.player_movement_threshold)
				reference.setAnimation(EnumGlobalAnimationList.stop);
			else
			{
				reference.setAnimation(EnumGlobalAnimationList.running);
				
				// current direction
				reference.reverseLeftRight((player.quad_object.x_vel > 0));
			}
			
			reference.onUpdate(delta);
		}
	}
	
	public void onDrawObject()
	{
		// backdrop
		if (backdrop_color != Color.TRANSPARENT)
			my_backdrop.onDrawAmbient(Constants.my_ip_matrix, true);
		
		// draw sorted objects
		for (int i = object_list.size() - 1; i >= 0; i--)
			object_list.get(i).onDrawObject();
		
		// particles
		for (int i = local_np_emitter.size() - 1; i >= 0; i--)
			local_np_emitter.get(i).onDraw();
		
		// bloom lights
		for (int i = bloom_light_list.size() - 1; i >= 0; i--)
			bloom_light_list.get(i).onDrawObject();
	}
	
	public void onDrawLight()
	{
		// lights
		for (int i = light_list.size() - 1; i >= 0; i--)
			light_list.get(i).onDrawLight();
	}
	
	public void onDrawConstant()
	{
		// events
		for (int i = event_list.size() - 1; i >= 0; i--)
			event_list.get(i).onDraw();
	}
	
	public void objectInteraction(final RectF collision, final LevelObject player, final LevelObject reference)
	{
		// up down collision
		if (collision.width() == 0)
		{
			// floating platforms
			if (reference.this_object == EnumLevelObject.l2_ground_platform_floating)
				if (player.quad_object.y_pos > reference.quad_object.y_pos) // remember this is the center of the object
				{
					player.quad_object.setXYPos(player.quad_object.x_pos, player.quad_object.y_pos - Constants.collision_detection_height, EnumDrawFrom.center);
					// it would be neat to have the players velocity affect this downward push. but since we zero out the velocity upon collision, at this point, it would do nothing.
					reference.quad_object.y_acc += Constants.player_downward_platform_acc;
				}
		}
	}
}
