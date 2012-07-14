package com.kobaj.opengl;

import java.util.ArrayList;

import android.graphics.Color;
import android.opengl.GLES20;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Functions;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad;
import com.kobaj.opengldrawable.QuadAnimated;
import com.kobaj.opengldrawable.QuadColorShape;
import com.kobaj.opengldrawable.QuadRenderTo;
import com.kobaj.openglgraphics.AmbientLight;
import com.kobaj.openglgraphics.BaseLight;
import com.kobaj.openglgraphics.PointLight;
import com.kobaj.openglgraphics.SpotLight;

public class MyGame extends MyGLRender
{
	// test
	Quad ic;
	QuadAnimated quad;
	QuadColorShape floor;
	QuadColorShape ball;
	QuadColorShape ball2;
	QuadColorShape ball3;
	QuadColorShape black_drop;
	
	QuadRenderTo scene;
	
	ArrayList<Quad> my_quad_list = new ArrayList<Quad>();
	
	//lights
    PointLight pl_blue;
    PointLight pl_green;
    PointLight pl_red;
    
    AmbientLight al_test;
    
    SpotLight sl_test;

    ArrayList<BaseLight> my_light_list = new ArrayList<BaseLight>();

	@Override
	void onInitialize()
	{
		ic = new Quad(R.drawable.ic_launcher);
		
		quad = new QuadAnimated(R.drawable.titlescreen, R.raw.test_animation);
		quad.playing = true;
		
		floor = new QuadColorShape(0, 20, com.kobaj.math.Constants.width, 0, 0xFF0000FF);
		ball = new QuadColorShape(1000, Color.RED);
		ball2 = new QuadColorShape(1000, Color.GREEN);
		ball3 = new QuadColorShape(1000, Color.BLUE);
		
		floor.setPos(com.kobaj.math.Functions.screenXToShaderX(0), com.kobaj.math.Functions.screenYToShaderY(20), com.kobaj.opengldrawable.EnumDrawFrom.top_left);
		
		black_drop = new QuadColorShape(0, com.kobaj.math.Constants.height, com.kobaj.math.Constants.width, 0, 0x00000000);
		
		//take them away from the background a little
		quad.z_pos = -1.0000001;
		
		pl_blue = new PointLight(point_light, my_view_matrix);
        pl_blue.x_pos = 0.2;
        pl_blue.y_pos = 0;
        pl_blue.focus = .9;
        pl_blue.color = Color.BLUE;
        
        pl_green = new PointLight(point_light, my_view_matrix);
        pl_green.x_pos = -0.2;
        pl_green.y_pos = 0;
        pl_green.focus = .9;
        pl_green.color = Color.GREEN;
        
        pl_red = new PointLight(point_light, my_view_matrix);
        pl_red.x_pos = 0.0;
        pl_red.y_pos = 0.2;
        pl_red.focus = .9;
        pl_red.color = Color.RED;
        
        al_test = new AmbientLight(ambient_light, my_view_matrix);
        al_test.brightness = 1.0;
        
        sl_test = new SpotLight(spot_light, my_view_matrix);
        
        //add to our quad list
        my_quad_list.add(quad);
        my_quad_list.add(floor);
        my_quad_list.add(ball);
        my_quad_list.add(ic);
	
        //add to our light list
        my_light_list.add(al_test);
        //my_light_list.add(pl_green);
        //my_light_list.add(pl_red);
        //my_light_list.add(pl_blue);
        //my_light_list.add(sl_test);
        
        scene = new QuadRenderTo();
	}

	double add = 100000;
	double delta;
	
	@Override
	public void onUpdate(double delta)
	{
		this.delta = delta;
		
		add += .01f * delta;
		
        sl_test.lookAtAngle(add);

		
		//for animation
		quad.onUpdate(delta);
		
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
		al_test.applyShaderProperties();
		
		//regular objects
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		if(scene.beginRenderToTexture())
		{
			quad.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
			ic.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		}
		scene.endRenderToTexture();
		
		//lights
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_SRC_ALPHA); // cheap lights
		floor.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		ball.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		ball2.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		ball3.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		
		//final scene
		GLES20.glBlendFunc(GLES20.GL_DST_COLOR, GLES20.GL_ZERO); // masking
		scene.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
		
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // no see thru
		//see if text works.
		//text.DrawText(R.string.hello, 0, 0, DrawFrom.bottom_right);
		text.DrawNumber(fps.fps, Functions.screenXToShaderX(25), Functions.screenYToShaderY((int)Functions.fix_y(25)), EnumDrawFrom.top_left);
		text.DrawNumber((int)add, Functions.screenXToShaderX(25), Functions.screenYToShaderY((int)Functions.fix_y(75)), EnumDrawFrom.top_left);
		text.DrawNumber((int)delta, Functions.screenXToShaderX(25), Functions.screenYToShaderY((int)Functions.fix_y(125)), EnumDrawFrom.top_left);
	}

	@Override
	void onPause()
	{
		
	}
}