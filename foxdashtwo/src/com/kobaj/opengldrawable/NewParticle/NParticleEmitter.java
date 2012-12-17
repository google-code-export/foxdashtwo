package com.kobaj.opengldrawable.NewParticle;

import java.util.ArrayList;
import java.util.Stack;

import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.EnumDrawFrom;

public class NParticleEmitter
{
	// couple of assumptions
	// no interaction with player
	// or environment
	// or gravity
	// ...yet
	
	// the below relates to the emitter
	
	// this is in shader coordinates (0-1)
	private RectF emit_location;
	
	// from 0 to 360
	public int direction_start = 270;
	public int direction_end = 270;
	
	// -1, or a positive number
	public int number_of_particles = -1;
	
	// can only be a positive number
	public int number_of_particles_shown = 20;
	
	// the below relate to the particles
	
	public boolean vary_scale = true;
	public boolean vary_velocity = true;
	public double start_velocity = 0.055;
	public int fade_in = 500;
	public int fade_out = 500;
	public int start_lifetime = 3000; // 3 seconds
	
	// the below are just containers
	private ArrayList<NParticle> all_particles = new ArrayList<NParticle>();
	private Stack<NParticle> unused_pool = new Stack<NParticle>();
	private ArrayList<NParticle> used_pool = new ArrayList<NParticle>();
	private double current_time;
	private double next_particle_spawn;
	
	public NParticleEmitter(RectF emit_location)
	{
		this.emit_location = emit_location;
	}
	
	public void preUpdate()
	{
		for (int i = 0; i < 100; i++)
			this.onUpdate(16);
	}
	
	public void onInitialize()
	{
		// normalize our velocity
		start_velocity = Functions.screenHeightToShaderHeight(start_velocity);
		
		all_particles.clear();
		unused_pool.clear();
		used_pool.clear();
		
		// time till next particle = number_of_particles_shown / start_lifetime;
		next_particle_spawn = (double) start_lifetime / (double) number_of_particles_shown;
		
		// make all our particles
		// put them all in the unused_pool
		for (int i = number_of_particles_shown + 1; i >= 0; i--)
		{
			NParticle temp = new NParticle(vary_scale, fade_in, fade_out, start_lifetime);
			temp.onInitialize();
			all_particles.add(temp);
			unused_pool.push(temp);
		}
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
					reference.quad_reference.setPos(x_pos, y_pos, EnumDrawFrom.center);
					
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
				}
				
				//remaining calculations
				if(number_of_particles > 0)
					number_of_particles--;
				
				current_time = 0;
			}
		}
		
		// and then update our currently visible particles
		for (int i = used_pool.size() - 1; i >= 0; i--)
		{
			NParticle reference = used_pool.get(i);
			reference.onUpdate(delta);
			Constants.physics.integratePhysics(delta, reference.quad_reference);
			
			if (reference.is_dead)
			{
				used_pool.remove(i);
				unused_pool.push(reference);
			}
		}
	}
	
	public void onDraw()
	{
		for (int i = used_pool.size() - 1; i >= 0; i--)
			used_pool.get(i).quad_reference.onDrawAmbient();
	}
}
