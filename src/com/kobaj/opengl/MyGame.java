package com.kobaj.opengl;

import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Particles;
import com.kobaj.opengldrawable.Quad;
import com.kobaj.opengldrawable.QuadAnimated;
import com.kobaj.opengldrawable.QuadColorShape;
import com.kobaj.opengldrawable.QuadRenderTo;
import com.kobaj.openglgraphics.AmbientLight;

public class MyGame extends MyGLRender
{
	//items
	Quad ic;
	QuadAnimated quad;
	QuadColorShape white;
	
	//lights
	QuadColorShape floor;
	QuadColorShape overlay;
	QuadColorShape ball;
	QuadColorShape ball2;
	QuadColorShape ball3;
	
	//particles
	Particles particles;
	
	//dont touch the stuff below this line
	//final drawable.
	QuadRenderTo scene;
	
	//lights
    AmbientLight al_ambient_light;
    //dont touch stuff above this line
    
	@Override
	void onInitialize()
	{
		ic = new Quad(R.drawable.ic_launcher);
		
		quad = new QuadAnimated(R.drawable.titlescreen, R.raw.test_animation);
		quad.playing = true;
		//take them away from the background a little
		quad.z_pos = -1.0000001;
		white = new QuadColorShape(0, com.kobaj.math.Constants.height, com.kobaj.math.Constants.width, 0, 0xFFFFFFFF);
		
		floor = new QuadColorShape(0, 20, com.kobaj.math.Constants.width, 0, 0xFF0000FF);
		overlay = new QuadColorShape(0, com.kobaj.math.Constants.height, com.kobaj.math.Constants.width, 0, 0xFF555555);
		ball = new QuadColorShape(25, Color.RED, 10, 30, false);
		ball2 = new QuadColorShape(25, Color.GREEN);
		ball3 = new QuadColorShape(25, Color.BLUE);		
		
		floor.setPos(com.kobaj.math.Functions.screenXToShaderX(0), com.kobaj.math.Functions.screenYToShaderY(20), com.kobaj.opengldrawable.EnumDrawFrom.top_left);
        
		particles = new Particles(16, 400, 240, 15, -1, false, -1);
		particles.setMovementToFountian(100, 100);
		//particles.setMovementToGravity(100);
		particles.advancePhysics();
		
		//dont touch below this line.
        al_ambient_light = new AmbientLight(ambient_light, my_view_matrix);
        scene = new QuadRenderTo();
        
        System.gc();
	}
	double delta;
	
	@Override
	public void onUpdate(double delta)
	{
		this.delta = delta;
		
		//for animation
		quad.onUpdate(delta);
		
		particles.onUpdate(delta);

		//for (int i = 0; i < com.kobaj.math.Constants.input_manager.fingerCount; i++)
			if (com.kobaj.math.Constants.input_manager.getReleased(0))
			{
				//change the particle effects.
				
				if(particles.movement == com.kobaj.opengldrawable.EnumParticleMovement.fountain)
					particles.setMovementToGravity(100);
				else if(particles.movement == com.kobaj.opengldrawable.EnumParticleMovement.gravity)
					particles.setMovementToOrbit(100, 50);
				else if(particles.movement == com.kobaj.opengldrawable.EnumParticleMovement.orbit)
					particles.setMovementToExplode(100, 100);
				else if(particles.movement == com.kobaj.opengldrawable.EnumParticleMovement.explode)
					particles.setMovementToFrantic(100, 100);
				else if(particles.movement == com.kobaj.opengldrawable.EnumParticleMovement.frantic)
					particles.setMovementToFountian(100, 100);
			}
		
		//quick test
		//TODO grab the initial translation matrix and store it
		//transforming it to the my_view_matrix to make the view change.
		//Matrix.translateM(my_view_matrix, 0, .0005f, .0005f, 0);
		
		//testing physics
		/*physics.apply_physics(delta, ball);
		physics.handle_collision(physics.check_collision(ball, floor), ball);
		
		physics.apply_physics(delta, ball2);
		physics.handle_collision(physics.check_collision(ball2, floor), ball2);
		
		physics.apply_physics(delta, ball3);
		physics.handle_collision(physics.check_collision(ball3, floor), ball3);*/
	}
	
	@Override
	void onDraw()
	{	
		GLES20.glUseProgram(ambient_light.my_shader);
		al_ambient_light.applyShaderProperties();
		
		//regular objects
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		if(scene.beginRenderToTexture())
		{
			quad.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
			white.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
			ic.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		}
		scene.endRenderToTexture();
	
		//lights
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_SRC_ALPHA); // cheap lights
		overlay.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		
		if(particles.have_lifes != 0)
		for(int i = particles.actual_particles.size() - 1; i >= 0; i--)
			particles.actual_particles.get(i).actual_quad.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);	
		//floor.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		//ball.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		//ball2.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		//ball3.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		
		//final scene
		GLES20.glBlendFunc(GLES20.GL_DST_COLOR, GLES20.GL_ZERO); // masking
		scene.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		
		//text below this line
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		text.DrawNumber(fps.fps, Functions.screenXToShaderX(25), Functions.screenYToShaderY((int)Functions.fix_y(25)), EnumDrawFrom.top_left);
		text.DrawNumber((int)delta, Functions.screenXToShaderX(100), Functions.screenYToShaderY((int)Functions.fix_y(25)), EnumDrawFrom.top_left);
			
		text.DrawNumber((int)particles.actual_particles.size(), Functions.screenXToShaderX(25), Functions.screenYToShaderY((int)Functions.fix_y(75)), EnumDrawFrom.top_left);
	}

	@Override
	void onPause()
	{
		
	}
}