package com.kobaj.opengldrawable.NewParticle;

import java.util.ArrayList;
import java.util.Stack;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.RectFExtended;
import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadColorShape;
import com.kobaj.opengldrawable.Quad.QuadCompressed;
import com.kobaj.opengldrawable.Quad.QuadRenderShell;

public class NParticleEmitter
{
	// couple of assumptions
	// no interaction with player
	// or environment
	// ...yet
	
	// if set to -1, the engine will calculate next_particle_spawn
	// otherwise next_particle_spawn is set to custom_spawn_time
	public int custom_spawn_time = -1;
	
	// this is in shader coordinates (0-1)
	public RectFExtended emit_location;
	public RectF visible_zone;
	
	// I have the best most readable variable names.
	public boolean is_affected_by_gravity = false;
	
	// from 0 to 360
	public int direction_start = 0;
	public int direction_end = 270;
	
	// -1, or a positive number
	public int number_of_particles = -1;
	
	// can only be a positive number
	public int number_of_particles_shown = 20;
	
	// the below only affects the 'start' of a particle
	// change size
	public boolean vary_scale = true;
	
	// change start speed
	public boolean vary_velocity = true;
	public double start_velocity = 0.055;
	
	// how it starts (And ends) drawn
	public int fade_in = 500;
	public int fade_out = 500;
	
	// and its life
	public int start_lifetime = 3000; // 3 seconds
	
	// the below are just containers
	private ArrayList<Quad> used_quads = new ArrayList<Quad>();
	private Stack<NParticle> unused_pool = new Stack<NParticle>();
	private ArrayList<NParticle> used_pool = new ArrayList<NParticle>();
	private double current_time;
	private double next_particle_spawn;
	private boolean update_quads = false;
	
	// and the unchangeable type
	private EnumParticleType particle_type;
	
	// association so we can move the particle with a quad
	// be sure to keep this as a reference.
	public Quad associated_quad;
	
	public boolean force_update = false;
	
	public NParticleEmitter(EnumParticleType type)
	{
		this.particle_type = type;
	}
	
	public void preUpdate()
	{
		force_update = true;
		
		visible_zone = new RectF(Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE);
		
		for (int i = 0; i < 200; i++)
			this.onUpdate(32);
		
		force_update = false;
	}
	
	public void onInitialize()
	{
		unused_pool.clear();
		used_pool.clear();
		
		// time till next particle = number_of_particles_shown / start_lifetime;
		if (custom_spawn_time == -1)
			next_particle_spawn = (double) start_lifetime / (double) number_of_particles_shown;
		else
			next_particle_spawn = custom_spawn_time;
		
		// make all our particles
		// put them all in the unused_pool
		for (int i = number_of_particles_shown + 1; i >= 0; i--)
		{
			NParticle temp = new NParticle(vary_scale, fade_in, fade_out, start_lifetime);
			
			// define the quad reference for each particle
			if (particle_type == EnumParticleType.floating_dust)
				temp.quad_reference = new QuadColorShape(28, 0xee000099, true, 0);
			else if (particle_type == EnumParticleType.floating_dust_2)
				temp.quad_reference = new QuadColorShape(28, 0xee000099, true, 0);
			else if (particle_type == EnumParticleType.snow)
				temp.quad_reference = new QuadCompressed(R.raw.white, R.raw.white, 8, 8);
			
			temp.onInitialize();
			unused_pool.push(temp);
		}
		
		preUpdate();
	}
	
	public void onUnInitialize()
	{
		for (int i = used_pool.size() - 1; i >= 0; i--)
			used_pool.get(i).quad_reference.onUnInitialize();
		
		for (int i = unused_pool.size() - 1; i >= 0; i--)
			unused_pool.get(i).quad_reference.onUnInitialize();
	}
	
	public void onUpdate(double delta)
	{
		// why update if not on screen?
		if (force_update || Functions.onShader(emit_location) || (visible_zone != null && Functions.onShader(visible_zone)))
		{
			if(!force_update)
			{
			if(visible_zone.left > emit_location.main_rect.left)
				visible_zone.left = emit_location.main_rect.left;
			if(visible_zone.top < emit_location.main_rect.top)
				visible_zone.top = emit_location.main_rect.top;
			if(visible_zone.right < emit_location.main_rect.right)
				visible_zone.right = emit_location.main_rect.right;
			if(visible_zone.bottom > emit_location.main_rect.bottom)
				visible_zone.bottom = emit_location.main_rect.bottom;
			}
			
			// first see if we need to spawn some particles
			if (number_of_particles != 0)
			{
				current_time += delta;
				if (current_time > next_particle_spawn)
				{
					emittParticle();
					current_time = 0;
				}
			}
			
			// and then update our currently visible particles
			for (int i = used_pool.size() - 1; i >= 0; i--)
			{
				NParticle reference = used_pool.get(i);
				
				// let the particle update its properties except for position
				reference.onUpdate(delta);
				
				// different affects on the particle
				if (is_affected_by_gravity)
					Constants.physics.addGravity(reference.quad_reference);
				
				// we update the particle's position
				Constants.physics.integratePhysics(delta, reference.quad_reference);
				
				// if its snow check and see if it has traveled outside of the spawn area
				if (particle_type == EnumParticleType.snow)
					if (!Functions.inRectF(emit_location.main_rect, reference.quad_reference.x_pos_shader, reference.quad_reference.y_pos_shader))
						reference.kill();
				
				if (reference.is_dead)
				{
					used_pool.remove(i);
					unused_pool.push(reference);
					update_quads = true;
				}
			}
		}
	}
	
	public void emittParticle()
	{
		// see if unused is empty,
		if (!unused_pool.isEmpty())
		{
			// if so, move a particle to the used pool
			NParticle reference = unused_pool.pop();
			reference.reset();
			
			// random position
			double x_pos = Functions.randomDouble(emit_location.main_rect.left, emit_location.main_rect.right);
			
			double y_pos = 0;
			if (particle_type == EnumParticleType.snow) // different start location because its snow
				y_pos = emit_location.main_rect.top - .00001;
			else
				y_pos = Functions.randomDouble(emit_location.main_rect.bottom, emit_location.main_rect.top);
			
			reference.quad_reference.setXYPos(x_pos, y_pos, EnumDrawFrom.center);
			
			// random velocity calculated based on direction.
			double degree = Functions.randomDouble(direction_start, direction_end);
			double velocity = start_velocity;
			if (vary_velocity)
				velocity = Functions.randomDouble(start_velocity / 2.0, start_velocity);
			
			double x_vel = velocity * Math.cos(Math.toRadians(degree));
			double y_vel = velocity * Math.sin(Math.toRadians(degree));
			
			reference.quad_reference.x_vel_shader = x_vel;
			reference.quad_reference.y_vel_shader = y_vel;
			
			used_pool.add(reference);
			update_quads = true;
		}
		
		// remaining calculations
		if (number_of_particles > 0)
			number_of_particles--;
	}
	
	public void onDraw()
	{
		if (update_quads)
		{
			used_quads.clear();
			for (int i = used_pool.size() - 1; i >= 0; i--)
				used_quads.add(used_pool.get(i).quad_reference);
			
			update_quads = false;
		}
		
		// INSTANCE THAT HECK YEAH
		if (used_quads.size() > 0)
		{
			if (Functions.onShader(emit_location) || (visible_zone != null && Functions.onShader(this.visible_zone)))
			{
				if (used_quads.get(0) instanceof QuadCompressed)
					QuadRenderShell.onDrawQuad(Constants.my_vp_matrix, true, Constants.compressed_light, used_quads);
				else
					QuadRenderShell.onDrawQuad(Constants.my_vp_matrix, true, Constants.ambient_light, used_quads);
			}
		}
	}
	
	// puts all particles in the unused pool,
	// and sets number_of_particles to 0 if its > 0
	public void killAllParticles()
	{
		if (number_of_particles > 0)
			number_of_particles = 0;
		
		for (int i = used_pool.size() - 1; i >= 0; i--)
		{
			NParticle reference = used_pool.get(i);
			
			used_pool.remove(i);
			unused_pool.push(reference);
			update_quads = true;
		}
	}
}
