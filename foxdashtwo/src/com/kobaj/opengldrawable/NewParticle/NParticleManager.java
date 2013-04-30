package com.kobaj.opengldrawable.NewParticle;

import com.kobaj.math.Functions;
import com.kobaj.math.RectFExtended;

public class NParticleManager
{
	// this is just a helpful tool for storing values and making different particle emitters
	// so we dont actually store the values in the emitters themselves, but reference them all here
	public static NParticleEmitter makeEmitter(EnumParticleType type, RectFExtended spawn)
	{
		NParticleEmitter emitter = new NParticleEmitter(type);
		emitter.emit_location = spawn;
		
		/*
		 * if the particle is floating dust
		 */
		if (type == EnumParticleType.floating_dust)
		{
			emitter.direction_start = 240;
			emitter.direction_end = 300;
			emitter.fade_in = 500;
			emitter.fade_out = 500;
			emitter.is_affected_by_gravity = false;
			emitter.number_of_particles = -1;
			emitter.number_of_particles_shown = 15;
			emitter.start_lifetime = 3000;
			emitter.start_velocity = Functions.screenHeightToShaderHeight(0.10);
			emitter.vary_scale = true;
			emitter.vary_velocity = true;
		}
		else if (type == EnumParticleType.floating_dust_2)
		{
			emitter.direction_start = 270;
			emitter.direction_end = 270;
			emitter.fade_in = 500;
			emitter.fade_out = 500;
			emitter.is_affected_by_gravity = false;
			emitter.number_of_particles = -1;
			emitter.number_of_particles_shown = 15;
			emitter.start_lifetime = 500;
			emitter.start_velocity = Functions.screenHeightToShaderHeight(0.25);
			emitter.vary_scale = true;
			emitter.vary_velocity = true;
		}
		else if (type == EnumParticleType.snow)
		{
			emitter.direction_start = 225;
			emitter.direction_end = 315;
			emitter.fade_in = emitter.fade_out = 250;
			emitter.is_affected_by_gravity = false;
			emitter.number_of_particles = -1;
			
			// this number gives a medium density of snow per screen. based on the snow spawn area
			double density = (spawn.main_rect.top - spawn.main_rect.bottom) * (spawn.main_rect.right - spawn.main_rect.left) * 5;
			emitter.number_of_particles_shown = (int) density;
			emitter.start_lifetime = 999999;
			emitter.start_velocity = Functions.screenHeightToShaderHeight(.08);
			emitter.vary_scale = true;
			emitter.vary_velocity = true;
			emitter.custom_spawn_time = 50;
		}
		
		// very last
		return emitter;
	}
}
