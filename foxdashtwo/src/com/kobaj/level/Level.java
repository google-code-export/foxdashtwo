package com.kobaj.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.audio.Sound;
import com.kobaj.foxdashtwo.R;
import com.kobaj.level.LevelEventTypes.EnumLevelEvent;
import com.kobaj.level.LevelEventTypes.LevelEventBase;
import com.kobaj.level.LevelEventTypes.LevelEventThoughtBubble;
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
	public long download_time;
	
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
	
	@Element
	public double background_parallax_ratio;
	@Element
	public double foreground_parallax_ratio;
	
	public static enum EnumMusics
	{
		none, tunnel, field, swamp, mountain, canyon, river
	};
	
	@Element
	public EnumMusics music = EnumMusics.none;
	private final int[] current_playing_fox_paws = new int[Sound.sound_count];
	private int sound_placement = 0;
	
	public double left_shader_limit;
	public double top_shader_limit;
	public double right_shader_limit;
	public double bottom_shader_limit;
	
	private double x_start;
	private double y_start; // where the player starts in shader coordinates
	
	@ElementList
	public ArrayList<LevelObject> object_list;
	
	public ArrayList<LevelObject> physics_objects = new ArrayList<LevelObject>(); // references for only physics (objects that move).
	private ArrayList<LevelObject> background_objects = new ArrayList<LevelObject>(); // references to backgrounds
	private ArrayList<LevelObject> foreground_objects = new ArrayList<LevelObject>(); // references to foregrounds
	
	public HashMap<EnumLayerTypes, CoordMap> object_hash; // index way of referenceing all objects
	
	@ElementList
	public ArrayList<LevelAmbientLight> light_list; // all lights including blooms
	
	private ArrayList<LevelBloomLight> bloom_light_list = new ArrayList<LevelBloomLight>(); // references for only blooms
	
	@Element
	public LevelObject player;
	public QuadCompressed player_shadow;
	
	private boolean player_on_ground = false;
	
	@ElementList
	public ArrayList<LevelEvent> event_list;
	
	public ArrayList<LevelEventThoughtBubble> thought_bubble_cache;
	
	// and our local particles
	private ArrayList<NParticleEmitter> local_np_emitter = new ArrayList<NParticleEmitter>();
	
	// walking sounds
	private final double walking_max = 600;
	private double walking_timeout = 0;
	
	// other random sounds
	private int random_sound_time = 0;
	private int random_sound_key = 0;
	
	// forcing player movement
	public boolean force_left = false;
	public boolean force_jump_start = false;
	public boolean force_jump_end = false;
	public boolean force_right = false;
	
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
			my_backdrop.setXYPos(0, 0, EnumDrawFrom.center);
			my_backdrop.color = backdrop_color;
		}
		
		// setup general objects
		local_np_emitter.clear();
		physics_objects.clear();
		background_objects.clear();
		foreground_objects.clear();
		
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
			// TODO optimize this to only have one if
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
		thought_bubble_cache = new ArrayList<LevelEventThoughtBubble>();
		for (int i = event_list.size() - 1; i >= 0; i--)
		{
			LevelEvent current_event = event_list.get(i);
			
			current_event.onInitialize(this);
			if (current_event.this_event == EnumLevelEvent.invisible_wall //
					|| current_event.this_event == EnumLevelEvent.color) //
			{
				LevelEvent original = event_list.get(i);
				
				LevelObject temp = new LevelObject();
				
				temp.active = true;
				temp.degree = 0;
				temp.scale = 1;
				temp.id = original.id;
				temp.x_pos = original.x_pos + original.width / 2.0;
				temp.y_pos = original.y_pos - original.height / 2.0;
				temp.quad_width = original.width;
				temp.quad_height = original.height;
				temp.z_plane = 5;
				temp.layer = EnumLayerTypes.Interaction;
				
				if (current_event.this_event == EnumLevelEvent.invisible_wall)
				{
					temp.this_object = EnumLevelObject.invisible_wall;
					temp.collide_with_player = true;
				}
				else if (current_event.this_event == EnumLevelEvent.color)
				{
					temp.this_object = EnumLevelObject.color;
					temp.layer = EnumLayerTypes.Top;
				}
				
				temp.onInitialize();
				
				if (current_event.this_event == EnumLevelEvent.color)
				{
					if (!current_event.id_strings.isEmpty())
					{
						try
						{
							temp.quad_object.color = Integer.valueOf(current_event.id_strings.get(0));
						}
						catch (NumberFormatException e)
						{
							temp.quad_object.color = Color.BLACK;
						}
					}
				}
				
				object_list.add(temp);
				event_list.remove(i);
			}
			else if (current_event.this_event == EnumLevelEvent.snow)
			{
				// snow
				LevelEvent original = event_list.get(i);
				
				double x_pos = original.x_pos;
				double y_pos = original.y_pos;
				
				RectFExtended shader_limits_for_snow_test = new RectFExtended((float) Functions.screenXToShaderX(x_pos),//
						(float) Functions.screenYToShaderY(y_pos),//
						(float) Functions.screenXToShaderX(x_pos + original.width),//
						(float) Functions.screenYToShaderY(y_pos - original.height));//
				NParticleEmitter test = NParticleManager.makeEmitter(EnumParticleType.snow, shader_limits_for_snow_test);
				test.onInitialize();
				
				local_np_emitter.add(test);
				
				event_list.remove(i);
			}
			else if (current_event.this_event == EnumLevelEvent.thought_bubble)
			{
				LevelEventBase base = current_event.my_possible_event;
				if (base instanceof LevelEventThoughtBubble)
					thought_bubble_cache.add((LevelEventThoughtBubble) base);
			}
		}
		
		// setup player
		setupPlayer();
		
		// setup our two bounding fade edges
		QuadCompressed left_edge = new QuadCompressed(R.raw.black, R.raw.lx_background_fade_1_alpha, this.right_limit - this.left_limit, 512);
		left_edge.setRotationZ(-90);
		left_edge.setXYPos(Functions.screenXToShaderX(left_limit + 255), Functions.screenYToShaderY(((double) top_limit + (double) bottom_limit) / 2.0), EnumDrawFrom.center);
		LevelObject left_object = new LevelObject();
		left_object.this_object = EnumLevelObject.lx_background_fade_1;
		left_object.quad_object = left_edge;
		left_object.eid = Integer.MAX_VALUE;
		left_object.layer = EnumLayerTypes.Top;
		left_object.z_plane = Double.MAX_VALUE;
		left_object.active = true;
		// note, you dont have to set x_pos or y_pos of the levelobject,
		// because this object isnt one a moving layer, nor does it move itself.
		object_list.add(left_object);
		
		QuadCompressed right_edge = new QuadCompressed(R.raw.black, R.raw.lx_background_fade_1_alpha, this.right_limit - this.left_limit, 512);
		right_edge.setRotationZ(90);
		right_edge.setXYPos(Functions.screenXToShaderX(right_limit - 255), Functions.screenYToShaderY(((double) top_limit + (double) bottom_limit) / 2.0), EnumDrawFrom.center);
		LevelObject right_object = new LevelObject();
		right_object.this_object = EnumLevelObject.lx_background_fade_1;
		right_object.quad_object = right_edge;
		right_object.eid = Integer.MAX_VALUE;
		right_object.layer = EnumLayerTypes.Top;
		right_object.z_plane = Double.MAX_VALUE;
		right_object.active = true;
		
		object_list.add(right_object);
		
		// player shadow
		player_shadow = new QuadCompressed(R.raw.shadow_square, R.raw.shadow_square_alpha, 179, 90);
		
		// sort the objects
		Collections.sort(object_list, new ObjectDrawSort());
		
		// player set position
		setPlayerPosition();
		
		// build our hashmap (garbage?)
		object_hash = new HashMap<EnumLayerTypes, CoordMap>();
		
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
			object_hash.put(t, new CoordMap(this.right_limit - this.left_limit, this.top_limit - this.bottom_limit, layer_temp));
		}
		
		// then find our objects
		for (int i = object_list.size() - 1; i >= 0; i--)
		{
			LevelObject reference = object_list.get(i);
			
			// do some particles
			if (reference.this_object == EnumLevelObject.l2_ground_platform_floating_1 //
					|| reference.this_object == EnumLevelObject.l2_ground_platform_floating_2 //
					|| reference.this_object == EnumLevelObject.l4_ground_platform_floating)
			{
				double scale_reference = reference.quad_object.scale_value;
				
				float half_width = (float) (reference.quad_object.best_fit_aabb.main_rect.width() - Functions.screenWidthToShaderWidth(25.0 * scale_reference)) / 2.0f;
				float half_height_top = (float) Functions.screenHeightToShaderHeight(0.0 * scale_reference);
				float half_height_bottom = (float) Functions.screenHeightToShaderHeight(-50.0 * scale_reference);
				
				// additional changes
				if (reference.this_object == EnumLevelObject.l4_ground_platform_floating)
				{
					half_height_top = (float) Functions.screenHeightToShaderHeight(-60.0 * scale_reference);
					half_height_bottom = (float) Functions.screenHeightToShaderHeight(-110.0 * scale_reference);
				}
				
				RectFExtended emitt_from = new RectFExtended(-half_width, half_height_top, half_width, half_height_bottom);
				
				NParticleEmitter my_particle_emitter = null;
				
				if (reference.this_object == EnumLevelObject.l2_ground_platform_floating_1)
				{
					my_particle_emitter = NParticleManager.makeEmitter(EnumParticleType.floating_dust, emitt_from);
				}
				else if (reference.this_object == EnumLevelObject.l2_ground_platform_floating_2 //
						|| reference.this_object == EnumLevelObject.l4_ground_platform_floating)
					my_particle_emitter = NParticleManager.makeEmitter(EnumParticleType.floating_dust_2, emitt_from);
				
				my_particle_emitter.onInitialize();
				
				my_particle_emitter.associated_quad = reference.quad_object;
				local_np_emitter.add(my_particle_emitter);
				
				physics_objects.add(reference);
			}
			
			// and falling water drops
			else if (reference.this_object == EnumLevelObject.l1_decoration_water_1)
			{
				RectF water_drop = reference.quad_object.phys_rect_list.get(0).main_rect;
				// make a very tall collision box for the water drop
				reference.y_water_drop_path = new RectFExtended(water_drop.left, water_drop.top, water_drop.right, water_drop.bottom - 20);
				double collision_y = reference.y_water_drop_path.main_rect.bottom;
				
				for (int e = object_list.size() - 1; e >= 0; e--)
				{
					LevelObject local_reference = object_list.get(e);
					if (local_reference.layer == EnumLayerTypes.Interaction)
						if (local_reference.this_object == EnumLevelObject.invisible_wall)
							collision_y = Physics.physicsCollisionUpDown(local_reference.quad_object, reference.y_water_drop_path.main_rect, collision_y);
				}
				
				if (collision_y < reference.y_water_drop_path.main_rect.top)
					reference.y_water_drop_path.setExtendedRectF(water_drop.left, water_drop.top, water_drop.right, collision_y);
				
				physics_objects.add(reference);
			}
			
			// checkpoint
			else if (reference.this_object == EnumLevelObject.lx_pickup_checkpoint)
				physics_objects.add(reference);
			
			// then find our background objects
			if (reference.layer == EnumLayerTypes.Background || reference.layer == EnumLayerTypes.Background_Aux)
			{
				background_objects.add(reference);
			}
			else if (reference.layer == EnumLayerTypes.Foreground || reference.layer == EnumLayerTypes.Foreground_Aux)
			{
				foreground_objects.add(reference);
			}
		}
		
		// set widths and heights for the camera
		left_shader_limit = (Functions.screenXToShaderX(left_limit) + Constants.ratio);
		right_shader_limit = (Functions.screenXToShaderX(right_limit) - Constants.ratio);
		
		top_shader_limit = Functions.screenYToShaderY(top_limit) - Constants.shader_height / 2.0;
		bottom_shader_limit = Functions.screenYToShaderY(bottom_limit) + Constants.shader_height / 2.0;
		
		// sounds
		Constants.sound.addSound(R.raw.sound_fox_trot_2);
		Constants.sound.addSound(R.raw.sound_checkpoint);
		Constants.sound.addSound(R.raw.sound_death);
		
		// last
		this.resetLevel();
	}
	
	public void setupPlayer()
	{
		QuadAnimated player_animation = new QuadAnimated(R.raw.fox, R.raw.fox_alpha, R.raw.animation_list_fox, 400, 215, 2048, 1024);
		player_animation.setAnimation(EnumGlobalAnimationList.stop, 0, true);
		
		RectF previous = player_animation.phys_rect_list.get(0).main_rect;
		player_animation.phys_rect_list.add(new RectFExtended(previous.left + Functions.screenWidthToShaderWidth(58), //
				previous.top - Functions.screenHeightToShaderHeight(55), //
				previous.right - Functions.screenWidthToShaderWidth(58), //
				previous.bottom + Functions.screenHeightToShaderHeight(15)));
		player_animation.phys_rect_list.remove(0);
		
		player.quad_object = player_animation;
		player.this_object = EnumLevelObject.fox2;
		player.eid = Integer.MIN_VALUE;
		player.layer = EnumLayerTypes.Pre_interaction;
		player.z_plane = Double.MIN_VALUE;
		player.quad_object.setScale(.75);
		player.ignore_coord_map = true;
		
		player_animation.reverseLeftRight(true);
		
		object_list.add(player);
	}
	
	public void setPlayerPosition()
	{
		// pre-player
		x_start = Functions.screenXToShaderX(player.x_pos);
		y_start = Functions.screenYToShaderY(player.y_pos);
		
		boolean player_set = false;
		if (SinglePlayerSave.last_checkpoint != null)
		{
			for (int i = this.physics_objects.size() - 1; i >= 0; i--)
			{
				LevelObject checkpoint = physics_objects.get(i);
				if (checkpoint.this_object == EnumLevelObject.lx_pickup_checkpoint)
					if (checkpoint.id.equals(SinglePlayerSave.last_checkpoint))
					{
						player_set = true;
						player.quad_object.setXYPos(checkpoint.x_pos_shader, checkpoint.y_pos_shader, EnumDrawFrom.center);
					}
			}
		}
		
		if (!player_set)
			player.quad_object.setXYPos(x_start, y_start, player.draw_from);
		
		player.quad_object.x_acc_shader = 0;
		player.quad_object.y_acc_shader = 0;
		player.quad_object.x_vel_shader = 0;
		player.quad_object.y_vel_shader = 0;
	}
	
	public void onUnInitialize()
	{
		if (my_backdrop != null)
			my_backdrop.onUnInitialize();
		
		// objects
		for (int i = object_list.size() - 1; i >= 0; i--)
			object_list.get(i).onUnInitialize();
		
		// particles
		for (int i = local_np_emitter.size() - 1; i >= 0; i--)
			local_np_emitter.get(i).onUnInitialize();
		
		// bloom lights
		for (int i = bloom_light_list.size() - 1; i >= 0; i--)
			bloom_light_list.get(i).onUnInitialize();
		
		// events
		for (int i = this.event_list.size() - 1; i >= 0; i--)
			event_list.get(i).onUnInitialize();
	}
	
	public void onUpdate(double delta, boolean play)
	{
		// reset our forces
		force_left = false;
		force_jump_start = false;
		force_jump_end = false;
		force_right = false;
		
		// compute the players position relative on screen
		// this is in shader coordinates
		// first do x
		double player_x = player.quad_object.x_pos_shader;
		double screen_x = Constants.x_shader_translation;
		double shift_x = (player_x - screen_x); // will be zero if in the middle of the screen.
		double player_shader_shifted_x = shift_x;
		
		// then y
		double player_y = player.quad_object.y_pos_shader;
		double screen_y = Constants.y_shader_translation;
		double shift_y = (player_y - screen_y);
		double player_shader_shifted_y = shift_y;
		
		for (int i = light_list.size() - 1; i >= 0; i--)
			light_list.get(i).onUpdate(delta);
		
		// do our thought bubble changes
		for (int i = thought_bubble_cache.size() - 1; i >= 0; i--)
		{
			double bubble_shift_x = player.quad_object.shader_width / 2.0;
			
			if(!player.quad_object.reverse_left_right)
				bubble_shift_x = -bubble_shift_x;
			
			thought_bubble_cache.get(i).setPlayerPosRelative(player_shader_shifted_x + bubble_shift_x, player_shader_shifted_y);
		}
		
		for (int i = event_list.size() - 1; i >= 0; i--)
			event_list.get(i).onUpdate(delta);
		
		// whaaaaat
		if (play)
			for (int i = physics_objects.size() - 1; i >= 0; i--)
				physics_objects.get(i).onUpdate(delta);
		
		for (int i = local_np_emitter.size() - 1; i >= 0; i--)
		{
			NParticleEmitter reference = local_np_emitter.get(i);
			if (reference.associated_quad != null)
			{
				reference.emit_location.setPositionWithOffset(reference.associated_quad.x_pos_shader, reference.associated_quad.y_pos_shader);
			}
			reference.onUpdate(delta);
		}
		
		if (play && player.quad_object instanceof QuadAnimated)
		{
			QuadAnimated reference = QuadAnimated.class.cast(player.quad_object);
			
			double current_x_speed = Math.abs(reference.x_vel_shader);
			double current_y_speed = Math.abs(reference.y_vel_shader);
			
			// currently playing animation
			if (reference.y_vel_shader < 0 //
					&& current_y_speed > Constants.player_movement_threshold_vertical //
					&& !player_on_ground)
			{
					reference.setAnimation(EnumGlobalAnimationList.falling, 0, true);
			}
			else if (reference.y_vel_shader > 0 //
					&& current_y_speed > Constants.player_movement_threshold_vertical)
			{
				if(player_on_ground)
				{
					reference.setAnimation(EnumGlobalAnimationList.jump, 0, true, false);
				}
				else
				{
					if(reference.currently_playing == EnumGlobalAnimationList.jump)
					{
						if(reference.currently_playing_frameset_reference.animation_complete)
						{
							reference.setAnimation(EnumGlobalAnimationList.jumping, 0, true);
						}
					}
					else
					{
						reference.setAnimation(EnumGlobalAnimationList.jumping, 0, true);
					}
				}
			}
			else if (current_x_speed < Constants.player_movement_threshold_horizontal)
			{
				reference.setAnimation(EnumGlobalAnimationList.stop, 0, true);
			}
			else
			{	
				if(reference.currently_playing == EnumGlobalAnimationList.falling)
				{
					reference.setAnimation(EnumGlobalAnimationList.landing, 0, true, false);
				}
				else if(reference.currently_playing == EnumGlobalAnimationList.stop || //
						(reference.currently_playing == EnumGlobalAnimationList.landing && reference.currently_playing_frameset_reference.animation_complete))
				{
					reference.setAnimation(EnumGlobalAnimationList.running, 0, true);
				}
			}
			
			if(current_x_speed > Constants.player_movement_threshold_horizontal)
			{
				// current direction
				reference.reverseLeftRight((player.quad_object.x_vel_shader >= 0));	
			}
			
			if (reference.best_fit_aabb.main_rect.top + Constants.shader_height / 2.0 < this.bottom_shader_limit //
					|| reference.best_fit_aabb.main_rect.bottom - Constants.shader_height / 2.0 > this.top_shader_limit //
					|| reference.best_fit_aabb.main_rect.left - Constants.ratio > this.right_shader_limit //
					|| reference.best_fit_aabb.main_rect.right + Constants.ratio < this.left_shader_limit) //
				kill();
			
			reference.onUpdate(delta);
		}
		
		// move the paralax backgrounds
		double x_distance = -Constants.x_shader_translation;
		
		if (this.background_parallax_ratio != 0)
			for (int i = background_objects.size() - 1; i >= 0; i--)
			{
				LevelObject reference = background_objects.get(i);
				double multiplier = this.background_parallax_ratio / Constants.enum_layer_background_para;
				if (reference.layer == EnumLayerTypes.Background_Aux)
					multiplier = this.background_parallax_ratio / Constants.enum_layer_background_aux_para;
				
				reference.quad_object.setXYPos(reference.x_pos_shader - x_distance * multiplier, reference.y_pos_shader, EnumDrawFrom.center);
			}
		
		if (this.foreground_parallax_ratio != 0)
			for (int i = foreground_objects.size() - 1; i >= 0; i--)
			{
				LevelObject reference = foreground_objects.get(i);
				double multiplier = this.foreground_parallax_ratio / Constants.enum_layer_foreground_para;
				if (reference.layer == EnumLayerTypes.Foreground_Aux)
					multiplier = this.foreground_parallax_ratio / Constants.enum_layer_foreground_aux_para;
				
				reference.quad_object.setXYPos(reference.x_pos_shader + x_distance * multiplier, reference.y_pos_shader, EnumDrawFrom.center);
			}
		
		// then do sounds
		walking_timeout += delta;
		double velocity = Math.abs(player.quad_object.x_vel_shader) * 10000;
		if (this.player_on_ground && velocity > Constants.arbitrary_sound_velocity && walking_timeout > walking_max)
		{
			walking_timeout = 0;
			if (play)
			{
				sound_placement += 1;
				sound_placement = sound_placement % Sound.sound_count;
				
				int result = Constants.sound.play(R.raw.sound_fox_trot_2, 0);
				if (result != 0)
				{
					this.current_playing_fox_paws[sound_placement] = result;
				}
				
			}
		}
		else if (!this.player_on_ground)
		{
			for (int i = 0; i < current_playing_fox_paws.length; i++)
			{
				Constants.sound.stop(current_playing_fox_paws[i]);
				current_playing_fox_paws[i] = 0;
			}
		}
		
		// random sounds
		if (this.random_sound_time > 0)
		{
			this.random_sound_time -= (int) delta;
			if (this.random_sound_time < 0)
				Constants.sound.play(random_sound_key);
		}
		
		// prepare for next loop
		player_on_ground = false;
	}
	
	public double compute_parallax_shift(EnumLayerTypes type)
	{
		// this is the exact same as above
		
		double x_distance = -Constants.x_shader_translation;
		double multiplier = 0;
		if (type == EnumLayerTypes.Background)
			multiplier = this.background_parallax_ratio / Constants.enum_layer_background_para;
		else if (type == EnumLayerTypes.Background_Aux)
			multiplier = this.background_parallax_ratio / Constants.enum_layer_background_aux_para;
		else if (type == EnumLayerTypes.Foreground)
			multiplier = -this.foreground_parallax_ratio / Constants.enum_layer_foreground_para;
		else if (type == EnumLayerTypes.Foreground_Aux)
			multiplier = -this.foreground_parallax_ratio / Constants.enum_layer_foreground_aux_para;
		
		return x_distance * multiplier;
	}
	
	@SuppressLint("WrongCall")
	public void onDrawObject(EnumLayerTypes... types)
	{
		// is ok, is array
		for (EnumLayerTypes type : types)
		{
			if (type == EnumLayerTypes.Shadow)
			{
				player_shadow.onDrawAmbient();
				continue;
			}
			
			// backdrop (looped in with background)
			if (type == EnumLayerTypes.Background)
				if (my_backdrop != null)
					my_backdrop.onDrawAmbient(Constants.my_ip_matrix, true);
			
			CoordMap temp_coord = object_hash.get(type);
			temp_coord.updated_visible_objects(compute_parallax_shift(type));
			LevelObject[] objects = object_hash.get(type).visible_objects;
			
			// draw sorted objects
			for (int i = object_hash.get(type).visible_object_count - 1; i >= 0; i--)
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
	
	@SuppressLint("WrongCall")
	public void onDrawConstant()
	{
		// events
		for (int i = event_list.size() - 1; i >= 0; i--)
			event_list.get(i).onDraw();
	}
	
	public void objectInteraction(final RectF collision, final LevelObject player, final LevelObject reference, double delta)
	{
		// up down collision
		if (collision.width() == 0)
		{
			player_on_ground = true;
			
			// floating platforms
			if (reference.this_object == EnumLevelObject.l2_ground_platform_floating_1 //
					|| reference.this_object == EnumLevelObject.l2_ground_platform_floating_2 //
					|| reference.this_object == EnumLevelObject.l4_ground_platform_floating)
			{
				if (player.quad_object.y_pos_shader > reference.quad_object.y_pos_shader) // remember this is the center of the object
				{
					player.quad_object.setXYPos(player.quad_object.x_pos_shader + reference.quad_object.x_vel_shader * delta, player.quad_object.y_pos_shader - 2.0
							* Constants.collision_detection_height, EnumDrawFrom.center);
					// it would be neat to have the players velocity affect this downward push. but since we zero out the velocity upon collision, at this point, it would do nothing.
					reference.quad_object.y_acc_shader += Constants.player_downward_platform_acc;
				}
			}
		}
		
		if (reference.this_object == EnumLevelObject.lx_pickup_checkpoint)
		{
			Constants.sound.play(R.raw.sound_checkpoint);
			reference.collide_with_player = false;
			if (reference.my_checkpoint != null)
				reference.my_checkpoint.explode();
			
			SinglePlayerSave.last_checkpoint = reference.id;
			this.resetCheckpoints(reference.id);
		}
	}
	
	public void deadReset()
	{
		kill = false;
		this.setPlayerPosition();
		
		for (int i = event_list.size() - 1; i >= 0; i--)
			event_list.get(i).onKillReset();
	}
	
	public void kill()
	{
		if (kill != true)
		{
			kill = true;
			Constants.sound.play(R.raw.sound_death);
		}
	}
	
	public void startMusic()
	{
		if (music == EnumMusics.none)
		{
			Constants.music_player.stop(Constants.music_fade_time);
			return;
		}
		
		if (music == EnumMusics.tunnel)
		{
			Constants.music_player.start(R.raw.music_tunnel, Constants.music_fade_time, true);
			random_sound_key = R.raw.sound_hum;
		}
		else if (music == EnumMusics.field)
		{
			Constants.music_player.start(R.raw.music_field, Constants.music_fade_time, true);
			random_sound_key = R.raw.sound_hum;
		}
		else if (music == EnumMusics.swamp)
		{
			Constants.music_player.start(R.raw.music_swamp, Constants.music_fade_time, true);
			random_sound_key = R.raw.sound_swamp_bugs;
		}
		else if (music == EnumMusics.mountain)
		{
			Constants.music_player.start(R.raw.music_mountain, Constants.music_fade_time, true);
			random_sound_key = R.raw.sound_mountain_wind;
		}
		else if (music == EnumMusics.river)
		{
			Constants.music_player.start(R.raw.music_river, Constants.music_fade_time, true);
			random_sound_key = R.raw.sound_hum;
		}
		else if (music == EnumMusics.canyon)
		{
			Constants.music_player.start(R.raw.music_canyon, Constants.music_fade_time, true);
			random_sound_key = R.raw.sound_rock_rumbles;
		}
		
		Constants.sound.addSound(random_sound_key);
	}
	
	public void resetLevel()
	{
		this.random_sound_time = Functions.randomInt(Constants.min_random_wait_time, Constants.max_random_wait_time);
		
		resetCheckpoints();
	}
	
	// this is called when the player resets the level
	public void resetCheckpoints()
	{
		resetCheckpoints(Constants.empty);
	}
	
	public void resetCheckpoints(String skip_id)
	{
		for (int i = this.physics_objects.size() - 1; i >= 0; i--)
		{
			LevelObject checkpoint = physics_objects.get(i);
			if (checkpoint.this_object == EnumLevelObject.lx_pickup_checkpoint && !checkpoint.id.equals(skip_id))
			{
				if (checkpoint.my_checkpoint != null)
					checkpoint.my_checkpoint.reset();
				checkpoint.collide_with_player = true;
			}
		}
	}
}
