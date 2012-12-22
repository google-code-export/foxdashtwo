package com.kobaj.opengldrawable.NewParticle;

import com.kobaj.math.Functions;
import com.kobaj.math.android.RectF;

public class NParticleManager
{
	// this is just a helpful tool for storing values and making different particle emitters
	// so we dont actually store the values in the emitters themselves, but reference them all here
	public static NParticleEmitter makeEmitter(EnumParticleType type, RectF spawn)
	{
		NParticleEmitter emitter = new NParticleEmitter();
		emitter.emit_location = spawn;
		
		/*
		 * if the particle is floating dust
		 */
		if(type == EnumParticleType.floating_dust)
		{
			emitter.direction_end = 270;
			emitter.direction_start = 270;
			emitter.fade_in = 500;
			emitter.fade_out = 500;
			emitter.is_affected_by_gravity = false;
			emitter.number_of_particles = -1;
			emitter.number_of_particles_shown = 15;
			emitter.start_lifetime = 3000;
			emitter.start_velocity = Functions.screenHeightToShaderHeight(0.05); 
			emitter.vary_scale = true;
			emitter.vary_velocity = true;
		}

		//very last
		return emitter;
	}
}
