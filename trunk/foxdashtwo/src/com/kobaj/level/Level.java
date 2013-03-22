package com.kobaj.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import android.graphics.Color;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.foxdashtwo.R;
import com.kobaj.level.LevelEventTypes.EnumLevelEvent;
import com.kobaj.level.LevelTypeLight.LevelAmbientLight;
import com.kobaj.level.LevelTypeLight.LevelBloomLight;
import com.kobaj.level.LevelTypeLight.LevelCustomLight;
import com.kobaj.level.LevelTypeLight.LevelPointLight;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.Physics;
import com.kobaj.math.RectFExtended;
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
	// do not rely on this as an indication of death
	public boolean kill = false;
	
	@Element
	public int backdrop_color;
	private Quad my_backdrop = null;
	
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
	public HashMap<EnumLayerTypes, LevelObject[]> object_hash;
	
	@ElementList
	public ArrayList<LevelAmbientLight> light_list; // all lights including blooms
	
	private ArrayList<LevelBloomLight> bloom_light_list = new ArrayList<LevelBloomLight>(); // references for only blooms
	
	@Element
	public LevelObject player;
	
	private boolean player_on_ground = false;
	
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
		if (backdrop_color != Color.TRANSPARENT || backdrop_color != Color.BLACK)
		{
			my_backdrop = new QuadCompressed(R.raw.white, R.raw.white, Constants.width, Constants.height);
			my_backdrop.color = backdrop_color;
		}
		
		// setup general objects
		local_np_emitter.clear();
		physics_objects.clear();
		for (int i = object_list.size() - 1; i >= 0; i--)
		{
			LevelObject reference = object_list.get(i);
			reference.onInitialize();
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
			LevelEvent current_event = event_list.get(i);
			
			current_event.onInitialize(this);
			// if (event_list.get(i).this_event == EnumLevelEvent.send_to_start)
			// LevelEventTransportPlayer.class.cast(event_list.get(i).my_possible_event).setTransportTo(x_player, y_player);
			if (current_event.this_event == EnumLevelEvent.invisible_wall ||
					current_event.this_event == EnumLevelEvent.color)
			{
				LevelEvent original = event_list.get(i);
				
				LevelObject temp = new LevelObject();
				
				temp.active = true;
				temp.degree = 0;
				temp.scale = 1;
				temp.id = original.id;
				temp.x_pos = original.x_pos + original.width / 2.0;
				temp.y_pos = original.y_pos - original.height / 2.0;
				temp.my_width = original.width;
				temp.my_height = original.height;
				temp.z_plane = 5;
				temp.layer = EnumLayerTypes.Interaction;
				
				if(current_event.this_event == EnumLevelEvent.invisible_wall)
					temp.this_object = EnumLevelObject.transparent;
				else if(current_event.this_event == EnumLevelEvent.color)
				{
					temp.this_object = EnumLevelObject.color;
					temp.layer = EnumLayerTypes.Top;
				}
				
				temp.onInitialize();
				
				if(current_event.this_event == EnumLevelEvent.color)
				{
					if(!current_event.id_strings.isEmpty())
					{
						try
						{
							temp.quad_object.color = Integer.valueOf(current_event.id_strings.get(0));
						}
						catch(NumberFormatException e)
						{
							temp.quad_object.color = Color.BLACK;
						}
					}
				}
				
				object_list.add(temp);
				event_list.remove(i);
			}
		}
		
		// setup player
		player.quad_object = new QuadAnimated(R.raw.fox2, R.raw.fox2_alpha, R.raw.fox_animation_list, 350, 180, 1024, 1024);
		player.eid = Integer.MIN_VALUE;
		player.layer = EnumLayerTypes.Pre_interaction;
		player.z_plane = Double.MIN_VALUE;
		object_list.add(player);
		
		// sort the objects
		Collections.sort(object_list, new ObjectDrawSort());
		
		// player set position
		setPlayerPosition();
		
		// build our hashmap (garbage?)
		object_hash = new HashMap<EnumLayerTypes, LevelObject[]>();
		
		// optimize which objects to collide against
		for (EnumLayerTypes t : EnumLayerTypes.values())
		{
			ArrayList<LevelObject> layer_temp = new ArrayList<LevelObject>();
			int object_list_size = object_list.size();
			for (int i = 0; i < object_list_size; i++)
			{
				LevelObject reference = object_list.get(i);
				if (reference.layer == t)
					layer_temp.add(reference);
			}
			object_hash.put(t, (LevelObject[]) layer_temp.toArray(new LevelObject[layer_temp.size()]));
			
		}
		
		// then find our physics objects
		for (int i = object_list.size() - 1; i >= 0; i--)
		{
			LevelObject reference = object_list.get(i);
			
			// do some particles
			if (reference.this_object == EnumLevelObject.l2_ground_platform_floating_1)
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
			
			// and falling water drops
			else if (reference.this_object == EnumLevelObject.l1_decoration_water_1)
			{
				RectF water_drop = reference.quad_object.phys_rect_list.get(0).main_rect;
				// make a very tall collision box for the water drop
				reference.y_water_drop_path = new RectFExtended(water_drop.left, water_drop.top, water_drop.right, water_drop.bottom - 20);
				double collision_y = reference.y_water_drop_path.main_rect.bottom;
				
				LevelObject[] interactables = object_hash.get(EnumLayerTypes.Interaction);
				for (int e = interactables.length - 1; e >= 0; e--)
					if (interactables[e].this_object != EnumLevelObject.l1_decoration_water_1)
						collision_y = Physics.physicsCollisionUpDown(interactables[e].quad_object, reference.y_water_drop_path.main_rect, collision_y);
				
				if (collision_y < reference.y_water_drop_path.main_rect.top)
					reference.y_water_drop_path.setExtendedRectF(water_drop.left, water_drop.top, water_drop.right, collision_y);
				
				physics_objects.add(reference);
			}
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
		
		// sounds
		Constants.sound.addSound(R.raw.fox_trot_2);
	}
	
	public void setPlayerPosition()
	{
		// pre-player
		double x_player = Functions.screenXToShaderX(player.x_pos);
		double y_player = Functions.screenYToShaderY(player.y_pos);
		
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
			
			double current_x_speed = Math.abs(player.quad_object.x_vel_shader);
			
			// currently playing animation
			if (current_x_speed < Constants.player_movement_threshold)
				reference.setAnimation(EnumGlobalAnimationList.stop);
			else
			{
				reference.setAnimation(EnumGlobalAnimationList.running);
				
				// current direction
				reference.reverseLeftRight((player.quad_object.x_vel_shader > 0));
			}
			
			reference.onUpdate(delta);
		}
		
		// then do sounds
		walking_timeout += delta;
		double velocity = Math.abs(player.quad_object.x_vel_shader) * 10000;
		if (this.player_on_ground && velocity > 2 && walking_timeout - velocity > walking_max)
		{
			walking_timeout = 0;
			Constants.sound.play(R.raw.fox_trot_2, 0);
		}
	}
	
	private final double walking_max = 600;
	private double walking_timeout = 0;
	
	public void onDrawObject(EnumLayerTypes... types)
	{
		// is ok, is array
		for (EnumLayerTypes type : types)
		{
			// backdrop (looped in with background)
			if (type == EnumLayerTypes.Background)
				if (my_backdrop != null)
					my_backdrop.onDrawAmbient(Constants.my_ip_matrix, true);
			
			LevelObject[] objects = object_hash.get(type);
			
			// draw sorted objects
			for (int i = objects.length - 1; i >= 0; i--)
				objects[i].onDrawObject();
			
			// particles (looped in with top)
			if (type == EnumLayerTypes.Top)
				for (int i = local_np_emitter.size() - 1; i >= 0; i--)
					local_np_emitter.get(i).onDraw();
			
			// bloom lights
			if (type == EnumLayerTypes.Top)
				for (int i = bloom_light_list.size() - 1; i >= 0; i--)
					bloom_light_list.get(i).onDrawObject();
		}
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
		player_on_ground = false;
		
		// up down collision
		if (collision.width() == 0)
		{
			player_on_ground = true;
			
			// floating platforms
			if (reference.this_object == EnumLevelObject.l2_ground_platform_floating_1)
			{
				if (player.quad_object.y_pos_shader > reference.quad_object.y_pos_shader) // remember this is the center of the object
				{
					player.quad_object.setXYPos(player.quad_object.x_pos_shader, player.quad_object.y_pos_shader - Constants.collision_detection_height, EnumDrawFrom.center);
					// it would be neat to have the players velocity affect this downward push. but since we zero out the velocity upon collision, at this point, it would do nothing.
					reference.quad_object.y_acc_shader += Constants.player_downward_platform_acc;
				}
			}
		}
	}
	
	public void deadReset()
	{
		kill = false;
		this.setPlayerPosition();
	}
}
