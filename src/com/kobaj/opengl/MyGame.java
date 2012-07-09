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
		
		//floor = new QuadColorShape(0, 20, com.kobaj.math.Constants.width, 0, 0xFF0000FF);
		//ball = new QuadColorShape(25, Color.RED);
		
		//floor.setPos(com.kobaj.math.Functions.screenXToShaderX(0), com.kobaj.math.Functions.screenYToShaderY(20), com.kobaj.opengldrawable.EnumDrawFrom.top_left);
		
		//take them away from the background a little
		quad.z_pos = -1.0000001;
		
		
		pl_blue = new PointLight(point_light, my_view_matrix);
        //pl_blue.x_pos = 0.2;
        //pl_blue.y_pos = 0;
        pl_blue.focus = .9;
        pl_blue.color = Color.BLUE;
        
        pl_green = new PointLight(point_light, my_view_matrix);
        //pl_green.x_pos = -0.2;
        //pl_green.y_pos = 0;
        pl_green.focus = .9;
        pl_green.color = Color.GREEN;
        
        pl_red = new PointLight(point_light, my_view_matrix);
        //pl_red.x_pos = 0.0;
        //pl_red.y_pos = 0.2;
        pl_red.focus = .9;
        pl_red.color = Color.RED;
        
        al_test = new AmbientLight(ambient_light, my_view_matrix);
        al_test.brightness = 0;
        
        sl_test = new SpotLight(spot_light, my_view_matrix);
        
        //add to our quad list
        my_quad_list.add(quad);
       // my_quad_list.add(floor);
       // my_quad_list.add(ball);
        my_quad_list.add(ic);
	
        //add to our light list
        my_light_list.add(al_test);
        my_light_list.add(pl_green);
        my_light_list.add(pl_red);
        my_light_list.add(pl_blue);
        my_light_list.add(sl_test);
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
		//physics.apply_physics(delta, ball);
		//physics.handle_collision(physics.check_collision(ball, floor), ball);
	}
	
	@Override
	void onDraw()
	{
		/*for(Quad q: my_quad_list) //43 fps
		{
			GLES20.glUseProgram(ambient_light.my_shader);
			al_test.applyShaderProperties();
			q.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
			
			GLES20.glUseProgram(point_light.my_shader);
			pl_blue.applyShaderProperties();
			q.onDrawPoint(my_view_matrix, my_proj_matrix, point_light);
			pl_red.applyShaderProperties();
			q.onDrawPoint(my_view_matrix, my_proj_matrix, point_light);
			pl_green.applyShaderProperties();
			q.onDrawPoint(my_view_matrix, my_proj_matrix, point_light);
		}*/
		
		/*for(BaseLight bl: my_light_list) //43 fps
		{
			if(bl instanceof AmbientLight)
			{
				GLES20.glUseProgram(ambient_light.my_shader);
				bl.applyShaderProperties();
				for(Quad q: my_quad_list)
					q.onDrawAmbient(my_view_matrix, my_proj_matrix, ambient_light);
			}
			else if(bl instanceof PointLight)
			{
		        GLES20.glUseProgram(point_light.my_shader);
		        bl.applyShaderProperties();
		        for(Quad q: my_quad_list)
					q.onDrawPoint(my_view_matrix, my_proj_matrix, point_light);
			}
		}*/
		
		
        
        
        
        //GLES20.glUseProgram(spot_light.my_shader);
        
        //sl_test.applyShaderProperties();
        
        //quad.onDrawSpot(my_view_matrix, my_proj_matrix, spot_light);
        //ic.onDrawSpot(my_view_matrix, my_proj_matrix, spot_light);
        //ball.onDrawSpot(my_view_matrix, my_proj_matrix, spot_light);
        //floor.onDrawSpot(my_view_matrix, my_proj_matrix, spot_light);
        
		
		
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