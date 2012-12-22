package com.kobaj.opengldrawable.NewParticle;

import java.util.ArrayList;
import java.util.Stack;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;
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
	public RectF emit_location;
	
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
	
	public void preUpdate()
	{
		for (int i = 0; i < 100; i++)
			this.onUpdate(16);
	}
	
	public void onInitialize()
	{
		unused_pool.clear();
		used_pool.clear();
		
		// time till next particle = number_of_particles_shown / start_lifetime;
		if(custom_spawn_time == -1)
			next_particle_spawn = (double) start_lifetime / (double) number_of_particles_shown;
		else
			next_particle_spawn = custom_spawn_time;
		
		// make all our particles
		// put them all in the unused_pool
		for (int i = number_of_particles_shown + 1; i >= 0; i--)
		{
			NParticle temp = new NParticle(vary_scale, fade_in, fade_out, start_lifetime);
			temp.onInitialize();
			unused_pool.push(temp);
		}
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
		// first see if we need to spawn some particles
		
		if (number_of_particles != 0)
		{
			current_time += delta;
			if (current_time > next_particle_spawn)
			{
				// see if unused is empty,
				if (!unused_pool.isEmpty())
				{
					// if so, move a particle to the used pool
					NParticle reference = unused_pool.pop();
					reference.reset();
					
					// random position
					double x_pos = Functions.randomDouble(emit_location.left, emit_location.right);
					double y_pos = Functions.randomDouble(emit_location.bottom, emit_location.top);
					reference.quad_reference.setXYPos(x_pos, y_pos, EnumDrawFrom.center);
					
					// random velocity calculated based on direction.
					double degree = Functions.randomDouble(direction_start, direction_end);
					double velocity = start_velocity;
					if (vary_velocity)
						velocity = Functions.randomDouble(start_velocity / 2.0, start_velocity);
					
					double x_vel = velocity * Math.cos(Math.toRadians(degree));
					double y_vel = velocity * Math.sin(Math.toRadians(degree));
					
					reference.quad_reference.x_vel = x_vel;
					reference.quad_reference.y_vel = y_vel;
					
					used_pool.add(reference);
					update_quads = true;
				}
				
				// remaining calculations
				if (number_of_particles > 0)
					number_of_particles--;
				
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
			
			if (reference.is_dead)
			{
				used_pool.remove(i);
				unused_pool.push(reference);
				update_quads = true;
			}
		}
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
		QuadRenderShell.onDrawQuad(Constants.my_vp_matrix, false, Constants.ambient_light, used_quads);
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
